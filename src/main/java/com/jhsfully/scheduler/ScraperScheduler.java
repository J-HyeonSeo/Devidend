package com.jhsfully.scheduler;

import com.jhsfully.model.Company;
import com.jhsfully.model.Dividend;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.model.constants.CacheKey;
import com.jhsfully.persist.CompanyRepository;
import com.jhsfully.persist.DividendRepository;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.persist.entity.DividendEntity;
import com.jhsfully.scraper.Scraper;
import com.jhsfully.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    //일정 주기마다 실행하게 함.
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사를 순회하면서 배당금 정보를 새로 스크래핑함.
        for(CompanyEntity company : companies){
            log.info("scraping scheduler -> company : " + company.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));
            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 것은 추가로 저장함.
            int cnt = 0;
            for(Dividend dividend : scrapedResult.getDividends()){
                if(!dividendRepository.existsByCompanyIdAndDate(company.getId(),
                        dividend.getDate())){
                    dividendRepository.save(DividendEntity.from(dividend, company.getId()));
                    cnt++;
                }
            }
            log.info("scraping scheduler -> update " + cnt + " rows. company : " + company.getName());

            //요청 주기를 제한해야함.(순간적인 대량 요청은 접근 제한이 걸릴 수 있음)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("Thread.sleep has Exception from scraping scheduler");
                Thread.currentThread().interrupt();
            }
        }
        log.info("scraping scheduler is ended");
    }
}
