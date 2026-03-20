package com.tournamagic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsTournamentAndClaimsCreatorPlayer() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Morgan")
                        .header("X-Auth-User-Email", "morgan@example.com")
                        .content("""
                                {
                                  "name":"Friday Draft",
                                  "players":["Morgan","Taylor"],
                                  "creatorPlayerName":"Morgan"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.players[0].claimedByEmail").value("morgan@example.com"))
                .andExpect(jsonPath("$.participants.length()").value(1))
                .andExpect(jsonPath("$.participants[0].playerName").value("Morgan"));
    }

    @Test
    void userMustClaimPlayerToJoinAndOnlyJoinedUsersCanViewTournament() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Host")
                        .header("X-Auth-User-Email", "host@example.com")
                        .content("""
                                {
                                  "name":"Commander Night",
                                  "players":["Host","Leo","Nick"],
                                  "creatorPlayerName":"Host"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String joinCode = created.get("joinCode").asText();
        String tournamentId = created.get("id").asText();
        String leoPlayerId = created.get("players").get(1).get("id").asText();

        mockMvc.perform(get("/api/tournaments/{id}", tournamentId)
                        .header("X-Auth-User-Name", "Viewer")
                        .header("X-Auth-User-Email", "viewer@example.com"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/tournaments/join/{code}", joinCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availablePlayers.length()").value(2));

        mockMvc.perform(post("/api/tournaments/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Leo")
                        .header("X-Auth-User-Email", "leo@example.com")
                        .content("""
                                {
                                  "code":"%s",
                                  "playerId":"%s"
                                }
                                """.formatted(joinCode, leoPlayerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants.length()").value(2))
                .andExpect(jsonPath("$.participants[1].playerName").value("Leo"));

        mockMvc.perform(get("/api/tournaments" )
                        .header("X-Auth-User-Name", "Leo")
                        .header("X-Auth-User-Email", "leo@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tournamentId));
    }

    @Test
    void cannotStartUntilEveryPlayerIsClaimed() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Host")
                        .header("X-Auth-User-Email", "host@example.com")
                        .content("""
                                {
                                  "name":"Two-Headed Giant",
                                  "players":["Host","Leo"],
                                  "creatorPlayerName":"Host"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String tournamentId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/tournaments/{id}/start", tournamentId)
                        .header("X-Auth-User-Name", "Host")
                        .header("X-Auth-User-Email", "host@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void leaveUnclaimsPlayer() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Host")
                        .header("X-Auth-User-Email", "host@example.com")
                        .content("""
                                {
                                  "name":"Draft",
                                  "players":["Host","Taylor"],
                                  "creatorPlayerName":"Host"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String joinCode = created.get("joinCode").asText();
        String tournamentId = created.get("id").asText();
        String taylorPlayerId = created.get("players").get(1).get("id").asText();

        mockMvc.perform(post("/api/tournaments/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Taylor")
                        .header("X-Auth-User-Email", "taylor@example.com")
                        .content("""
                                {
                                  "code":"%s",
                                  "playerId":"%s"
                                }
                                """.formatted(joinCode, taylorPlayerId)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/tournaments/{id}/participants/me", tournamentId)
                        .header("X-Auth-User-Name", "Taylor")
                        .header("X-Auth-User-Email", "taylor@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentUserJoined").value(false));

        mockMvc.perform(get("/api/tournaments" )
                        .header("X-Auth-User-Name", "Taylor")
                        .header("X-Auth-User-Email", "taylor@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
