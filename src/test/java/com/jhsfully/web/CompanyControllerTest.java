package com.jhsfully.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.model.Company;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.security.JwtAuthenticationFilter;
import com.jhsfully.security.SecurityConfiguration;
import com.jhsfully.service.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CompanyController.class, excludeFilters =
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            JwtAuthenticationFilter.class, SecurityConfiguration.class
    }))
class CompanyControllerTest {

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("[Controller]회사명 자동완성 목록 조회")
    void autoCompleteTest() throws Exception {
        //given
        given(companyService.getCompanyNamesByKeyword(anyString()))
                .willReturn(Arrays.asList(
                        "AAA", "BBB"
                ));
        //when & then
        mockMvc.perform(get("/company/autocomplete?keyword=A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("AAA"))
                .andExpect(jsonPath("$[1]").value("BBB"));

    }

    @Test
    @WithMockUser
    @DisplayName("[Controller] 회사명 페이징 조회")
    void getCompaniesTest() throws Exception {
        //given
        given(companyService.getAllCompany(any()))
                .willReturn(new PageImpl<>(
                        Arrays.asList(
                                CompanyEntity.builder().id(1L).name("MMM").ticker("MMM").build()
                        ),
                        PageRequest.of(0, 10),
                        1
                ));
        //when & then
        mockMvc.perform(get("/company"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ticker").value("MMM"))
                .andExpect(jsonPath("$.content[0].name").value("MMM"));
    }

    @Test
    @WithMockUser
    @DisplayName("[Controller]회사 추가 테스트")
    void addCompanyTest() throws Exception {
        //given

        Company company = new Company("MMM", "MMM");

        given(companyService.save(anyString()))
                .willReturn(company);

        //when & then
        mockMvc.perform(post("/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(company))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("MMM"))
                .andExpect(jsonPath("$.name").value("MMM"));

        verify(companyService, times(1)).save(anyString());
        verify(companyService, times(1)).addAutoCompleteKeyword(anyString());
    }

    @Test
    @WithMockUser
    @DisplayName("[Controller]회사 삭제 테스트")
    void deleteCompanyTest() throws Exception {
        //given
        given(companyService.deleteCompany(anyString()))
                .willReturn("MMM");

        //when & then
        mockMvc.perform(delete("/company/MMM").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("MMM"));

        verify(companyService, times(1)).deleteCompany(anyString());
    }

}