package com.jhsfully.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.model.Company;
import com.jhsfully.model.Dividend;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.security.JwtAuthenticationFilter;
import com.jhsfully.security.SecurityConfiguration;
import com.jhsfully.service.FinanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FinanceController.class, excludeFilters =
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            JwtAuthenticationFilter.class, SecurityConfiguration.class
    }))
class FinanceControllerTest {

    @MockBean
    private FinanceService financeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("[Controller]회사명으로 배당금 조회")
    void searchFinance() throws Exception {
        //given
        given(financeService.getDividendByCompanyName(anyString()))
                .willReturn(new ScrapedResult(new Company("MMM", "MMM"),
                        Arrays.asList(
                                new Dividend(LocalDateTime.of(2023,1,1,1,1,1), "123")
                        )));
        //when & then
        mockMvc.perform(get("/finance/dividend/MMM"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company.name").value("MMM"))
                .andExpect(jsonPath("$.company.ticker").value("MMM"))
                .andExpect(jsonPath("$.dividends[0].date").value(
                        LocalDateTime.of(2023,1,1,1,1,1).toString()
                ))
                .andExpect(jsonPath("$.dividends[0].dividend").value("123"));
    }

}