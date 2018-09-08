package com.stan.person.model;
import com.stan.person.Fidelity.FidelityInvestmentReader;

public class InvestmentReaderFactory {
    public InvestmentReader getInvestmentReader(String type) {
        if (type.equalsIgnoreCase("Fidelity"))
            return new FidelityInvestmentReader();
        else {
            System.out.println("No InvestmentReader of type : " + type);
            System.exit(-1);
        }
        return null;

    }
}

