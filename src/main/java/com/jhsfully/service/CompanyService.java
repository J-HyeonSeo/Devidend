package com.jhsfully.service;

import com.jhsfully.exception.impl.NoCompanyException;
import com.jhsfully.model.Company;
import com.jhsfully.model.ScrapedResult;
import com.jhsfully.persist.CompanyRepository;
import com.jhsfully.persist.DividendRepository;
import com.jhsfully.persist.entity.CompanyEntity;
import com.jhsfully.persist.entity.DividendEntity;
import com.jhsfully.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie;
    private final Scraper yahooFinanceScrapper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker){
        boolean exist = companyRepository.existsByTicker(ticker);
        if(exist){
            throw new RuntimeException("Already exist ticker -> " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker){
        //ticker를 기준으로 회사를 스크래핑함.
        Company company = yahooFinanceScrapper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScrapper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.from(company));
        Long companyId = companyEntity.getId();
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(x -> DividendEntity.from(x, companyId))
                .collect(Collectors.toList());
        dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());
    }

    public void addAutoCompleteKeyword(String keyword){
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword){
        return (List<String>) trie.prefixMap(keyword).keySet().stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword){
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker){
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);
        deleteAutoCompleteKeyword(company.getName());

        return company.getName();
    }
}
