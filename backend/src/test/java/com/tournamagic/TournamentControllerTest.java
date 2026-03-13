package com.tournamagic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void listsTournamentOverview() throws Exception {
        mockMvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk());
    }
}
