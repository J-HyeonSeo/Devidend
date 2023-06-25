package com.jhsfully.scraper;

import com.jhsfully.model.Company;
import com.jhsfully.model.ScrapedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Mocking을 수행할 대상이, 변동될 가능성이 있으므로, 통합테스트로 진행함.
class ScraperTest {

    private Scraper scraper;

    @BeforeEach
    void setup() throws InterruptedException {
        Thread.sleep(200); //prevent IP ban.
        scraper = new YahooFinanceScraper();
    }

    @Test
    @DisplayName("[Scraper]Ticker를 통해 회사명 스크랩하기")
    void scrapCompanyByTickerTest(){
        //when
        Company company = scraper.scrapCompanyByTicker("MMM");

        //then
        assertAll(
            () -> assertEquals("3M Company", company.getName()),
            () -> assertEquals("MMM", company.getTicker())
        );
    }

    @Test
    @DisplayName("[Scraper]Company객체를 통한 스크랩 테스트")
    void scrapTest(){
        //when
        Company company = new Company("MMM", "3M Company");

        ScrapedResult result = scraper.scrap(company);

        //then
        assertAll(
                () -> assertEquals(company, result.getCompany()),
                () -> assertTrue(result.getDividends().size() > 0)
        );
    }

}