package com.stan.person.model;

import java.util.Date;
import java.util.List;

public interface InvestmentReader {
    List<Investment> getInvestments();
    List<Investment> readInvestments(String filePath);
    Date getDateDownloaded();
    Double getPendingActivity();

}
