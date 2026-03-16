package com.tournamagic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsTournamentAndMatches() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(post("/api/tournaments")
                        .header("Authorization", "Bearer " + token)
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
        String token = registerAndGetToken();
        mockMvc.perform(get("/api/tournaments").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void blocksTournamentAccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tournaments"))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndGetToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Tester",
                                  "email":"tester-%s@example.com",
                                  "password":"strongpass123"
                                }
                                """.formatted(System.nanoTime())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode body = objectMapper.readTree(response);
        return body.get("token").asText();
    }
}
