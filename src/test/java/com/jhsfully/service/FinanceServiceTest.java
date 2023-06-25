package com.jhsfully.service;

import com.jhsfully.exception.impl.NoCompanyException;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.persist.CompanyRepository;
import com.jhsfully.persist.DividendRepository;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.persist.entity.DividendEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private FinanceService financeService;

    @Test
    @DisplayName("[Service] 회사명으로 배당금정보 조회 - Success")
    void getDividendByCompanyNameSuccess(){
        CompanyEntity company = CompanyEntity.builder()
                .id(1L)
                .name("MMM")
                .ticker("MMM").build();

        //given
        given(companyRepository.findByName(anyString()))
                .willReturn(Optional.of(company));
        given(dividendRepository.findAllByCompanyId(anyLong()))
                .willReturn(Arrays.asList(
                        DividendEntity.builder()
                                .id(1L)
                                .companyId(1L)
                                .date(LocalDateTime.of(2023,1,1,1,1,1))
                                .dividend("111")
                                .build()
                ));
        //when
        ScrapedResult result = financeService.getDividendByCompanyName("MMM");

        //then
        assertAll(
                () -> assertEquals("MMM", result.getCompany().getName()),
                () -> assertEquals("MMM", result.getCompany().getTicker()),
                () -> assertEquals(LocalDateTime.of(2023,1,1,1,1,1),
                        result.getDividends().get(0).getDate()),
                () -> assertEquals("111",
                        result.getDividends().get(0).getDividend())
        );

    }

    @Test
    @DisplayName("[Service] 회사명으로 배당금정보 조회 - Fail")
    void getDividendByCompanyNameFail(){
        //given
        given(companyRepository.findByName(anyString()))
                .willReturn(Optional.empty());

        //when & then
        assertThrows(NoCompanyException.class,
                () -> financeService.getDividendByCompanyName("ABC"));
    }

}