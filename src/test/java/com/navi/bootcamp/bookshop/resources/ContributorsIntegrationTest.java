package com.navi.bootcamp.bookshop.resources;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ContributorsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnAllContributors() throws Exception {
        mockMvc.perform( MockMvcRequestBuilders.get("/contributors.txt").accept( MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("Abhi, Jothi Kapur, Xin"));
    }

    @Test
    void shouldReturnContributorsInAscendingOrder() throws Exception {
        MvcResult mvcResult = mockMvc.perform( MockMvcRequestBuilders.get("/contributors.txt").accept( MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk()).andReturn();

        String contributors = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(isSorted(contributors), "Contributors list is not sorted");
    }

    private boolean isSorted( String contributorsResponse) {
        final String[] contributors = contributorsResponse.split(", ");

        for (int contributorIndex = 0; contributorIndex < contributors.length - 1; contributorIndex++) {
            if (contributors[contributorIndex].compareTo(contributors[contributorIndex + 1]) > 0) {
                return false;
            }
        }

        return true;
    }
}
