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
    void createsTournamentAndMatches() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Morgan")
                        .header("X-Auth-User-Email", "morgan@example.com")
                        .content("""
                                {
                                  "name":"Friday Draft",
                                  "players":["Alice","Bob","Chandra"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.players.length()").value(3))
                .andExpect(jsonPath("$.matches.length()").value(3))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.joinCode").isString())
                .andExpect(jsonPath("$.participants.length()").value(1))
                .andExpect(jsonPath("$.currentUserJoined").value(true));
    }

    @Test
    void listsTournamentOverview() throws Exception {
        mockMvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk());
    }

    @Test
    void joinsTournamentByCodeWithoutDuplicatingParticipant() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Commander Night",
                                  "players":["Aria","Bex"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String joinCode = created.get("joinCode").asText();

        mockMvc.perform(post("/api/tournaments/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Taylor")
                        .header("X-Auth-User-Email", "taylor@example.com")
                        .content("""
                                {
                                  "code":"%s"
                                }
                                """.formatted(joinCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants.length()").value(1))
                .andExpect(jsonPath("$.participants[0].email").value("taylor@example.com"))
                .andExpect(jsonPath("$.currentUserJoined").value(true));

        mockMvc.perform(post("/api/tournaments/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-User-Name", "Taylor")
                        .header("X-Auth-User-Email", "taylor@example.com")
                        .content("""
                                {
                                  "code":"%s"
                                }
                                """.formatted(joinCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants.length()").value(1));
    }

    @Test
    void rejectsJoinWhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/tournaments/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code":"INVALID12"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
