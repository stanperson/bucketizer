package com.stan.person.model;
import java.math.BigDecimal;

public class Quote {

    private String symbol;
    private BigDecimal price;
    private BigDecimal open;
    private BigDecimal change;
    private BigDecimal change50;
    private BigDecimal change200;

    Quote(String symbol, BigDecimal price, BigDecimal open, BigDecimal change, BigDecimal change50, BigDecimal change200) {
        this.symbol = symbol;
        this. price = price;
        this. open = open;
        this.change = change;
        this.change50 = change50;
        this.change200 = change200;
    }

    public BigDecimal getChange200() {
        return change200;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getChange() {
        return change;
    }

    public BigDecimal getChange50() {
        return change50;
    }
}
