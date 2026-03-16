package com.tournamagic;

import com.tournamagic.controller.TournamentController;
import com.tournamagic.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TournamentController.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @Test
    void createTournamentRequiresAuthHeader() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"name\":\"Friday Draft\",
                                  \"players\":[\"Alice\",\"Bob\"]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsTournamentOverviewWithAuthHeader() throws Exception {
        Mockito.when(tournamentService.listTournaments("token")).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/tournaments")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
