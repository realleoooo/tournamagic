package com.tournamagic;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsTournamentAndMatches() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
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
                .andExpect(jsonPath("$.matches[0].timerDirection").value("up"))
                .andExpect(jsonPath("$.matches[0].timerNotifyIntervalSeconds").value(600));
    }

    @Test
    void updatesMatchTimerSettings() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Timer Cup",
                                  "players":["Alice","Bob"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String payload = createResult.getResponse().getContentAsString();
        String tournamentId = JsonPath.read(payload, "$.id");
        String matchId = JsonPath.read(payload, "$.matches[0].id");

        mockMvc.perform(patch("/api/tournaments/{id}/matches/{matchId}/timer", tournamentId, matchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "running": true,
                                  "direction": "down",
                                  "durationSeconds": 900,
                                  "notifyIntervalSeconds": 120
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches[0].timerRunning").value(true))
                .andExpect(jsonPath("$.matches[0].timerDirection").value("down"))
                .andExpect(jsonPath("$.matches[0].timerDurationSeconds").value(900))
                .andExpect(jsonPath("$.matches[0].timerNotifyIntervalSeconds").value(120));
    }

    @Test
    void listsTournamentOverview() throws Exception {
        mockMvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk());
    }
}
