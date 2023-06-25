package com.jhsfully.service;

import com.jhsfully.exception.impl.NoCompanyException;
import com.jhsfully.model.Company;
import com.jhsfully.model.Dividend;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.model.constants.CacheKey;
import com.jhsfully.persist.CompanyRepository;
import com.jhsfully.persist.DividendRepository;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company -> " + companyName);
        //1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        //3. 결과 조합 후 반환
        return new ScrapedResult(
                new Company(company.getTicker(), company.getName())
                , dividendEntities.stream().map(x -> new Dividend(x.getDate(), x.getDividend()))
                        .collect(Collectors.toList())
        );
    }

}
