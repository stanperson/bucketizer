package com.stan.person.model;

class PlanItem {

    protected String ticker;
    protected String type;
    protected String description;
    protected Double bucket1Pct;
    protected Double bucket2Pct;
    protected Double bucket3Pct;


    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBucket1Pct() {
        return bucket1Pct;
    }

    public void setBucket1Pct(Double bucket1Pct) {
        this.bucket1Pct = bucket1Pct;
    }

    public Double getBucket2Pct() {
        return bucket2Pct;
    }

    public void setBucket2Pct(Double bucket2Pct) {
        this.bucket2Pct = bucket2Pct;
    }

    public Double getBucket3Pct() {
        return bucket3Pct;
    }

    public void setBucket3Pct(Double bucket3Pct) {
        this.bucket3Pct = bucket3Pct;
    }


}
