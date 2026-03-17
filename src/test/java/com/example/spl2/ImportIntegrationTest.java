package com.example.spl2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class ImportIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @LocalServerPort
    private int port;

    @Test
    void testCsvImportCreatesPlayers() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String csv = "Name,Date Of Birth,Age,Role,Batting Type,Bowling Type,Image,Category,Base Price\n" +
                "Virat Kohli,1988-11-05,33,Batter,Right-hand,None,https://example.com/virat.png,International,1000000\n" +
                "Jasprit Bumrah,1993-12-06,27,Only Bowler,Right-hand,Right-arm fast,https://example.com/bumrah.png,International,800000\n" +
                "Rohit Sharma,1987-04-30,34,Only Batsman,Right-hand,None,https://example.com/rohit.png,International,900000\n";

        MockMultipartFile file = new MockMultipartFile("file", "players.csv", "text/csv", csv.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/import/upload-csv").file(file))
                .andExpect(status().isOk());

        // Now fetch players
        String playersJson = mockMvc.perform(MockMvcRequestBuilders.get("/players?status=REGISTERED").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(playersJson).contains("Virat Kohli");
        assertThat(playersJson).contains("Jasprit Bumrah");
        assertThat(playersJson).contains("Rohit Sharma");
        assertThat(playersJson).contains("https://example.com/virat.png");
    }
}

