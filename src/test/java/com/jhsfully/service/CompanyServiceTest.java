package com.jhsfully.service;

import com.jhsfully.exception.impl.NoCompanyException;
import com.jhsfully.model.Company;
import com.jhsfully.model.Dividend;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.persist.CompanyRepository;
import com.jhsfully.persist.DividendRepository;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.persist.entity.DividendEntity;
import com.jhsfully.scraper.Scraper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private Scraper yahooFinanceScrapper;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("[Service] 별명으로 회사 저장 - Success")
    void saveSuccess(){
        //given
        given(companyRepository.existsByTicker(anyString()))
                .willReturn(false);

        Company company = new Company("MMM", "MMM");
        ScrapedResult result = new ScrapedResult(company, Arrays.asList(
                new Dividend(LocalDateTime.of(2023,1,1,1,1,1), "123")
        ));

        given(yahooFinanceScrapper.scrapCompanyByTicker(anyString()))
                .willReturn(company);

        given(yahooFinanceScrapper.scrap(any()))
                .willReturn(result);

        given(companyRepository.save(any())).willReturn(
                CompanyEntity.builder().id(1L).build()
        );

        //when
        companyService.save("MMM");

        ArgumentCaptor<CompanyEntity> companyCaptor = ArgumentCaptor.forClass(CompanyEntity.class);
        ArgumentCaptor<List> dividendsCaptor = ArgumentCaptor.forClass(List.class);


        //then
        verify(companyRepository, times(1)).save(companyCaptor.capture());
        verify(dividendRepository, times(1)).saveAll(dividendsCaptor.capture());

        assertAll(
                () -> assertEquals("MMM", companyCaptor.getValue().getName()),
                () -> assertEquals("MMM", companyCaptor.getValue().getTicker()),
                () -> assertEquals(LocalDateTime.of(2023,1,1,1,1,1),
                        ((DividendEntity)dividendsCaptor.getValue().get(0)).getDate()),
                () -> assertEquals("123",
                        ((DividendEntity)dividendsCaptor.getValue().get(0)).getDividend()),
                () -> assertEquals(1L,
                        ((DividendEntity)dividendsCaptor.getValue().get(0)).getCompanyId())
        );
    }

    @Test
    @DisplayName("[Service] 별명으로 회사 저장 - Fail(Already exist ticker)")
    void saveFail(){
        //given
        given(companyRepository.existsByTicker(anyString()))
                .willReturn(true);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> companyService.save("MMM"));

        //then
        assertTrue(exception.getMessage().contains("Already exist ticker"));

    }

    @Test
    @DisplayName("[Service] 회사 삭제 - Success")
    void deleteCompanySuccess(){
        //given
        given(companyRepository.findByTicker(anyString()))
                .willReturn(Optional.of(new CompanyEntity(1L, "MMM", "MMM")));
        //when
        String result = companyService.deleteCompany("MMM");

        //then
        verify(dividendRepository, times(1)).deleteAllByCompanyId(anyLong());
        verify(companyRepository, times(1)).delete(any());

        assertEquals("MMM", result);
    }

    @Test
    @DisplayName("[Service] 회사 삭제 - Fail(NoCompany)")
    void deleteCompanyFail(){
        //given
        given(companyRepository.findByTicker(anyString()))
                .willReturn(Optional.empty());
        //when
        assertThrows(NoCompanyException.class,
                () -> companyService.deleteCompany("AAA"));

    }

}