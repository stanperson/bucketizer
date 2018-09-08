package com.stan.person.model;

import java.util.List;

public interface InvestmentReader {
    List<Investment> getInvestments();
    List<Investment> readInvestments(String filePath);
    String getDateDownloaded();
    Double getPendingActivity();

}
