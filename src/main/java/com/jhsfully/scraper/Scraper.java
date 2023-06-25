package com.jhsfully.scraper;

import com.jhsfully.model.Company;
import com.jhsfully.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);

}
