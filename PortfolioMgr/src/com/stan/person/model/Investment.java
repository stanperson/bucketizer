package com.stan.person.model;



import static com.stan.person.utility.Math.setPrecision;

public class Investment {

     private String ticker ="default";
     private String description="default";
     private Double numberOfShares = 0.;
     private Double currentPrice = 0.;
     private Double currentValue = 0.;
     private Double costBasis = 0.;
     private Double gain;
     private Double targetPct = 0.0;
     private Double actualPct = 0.0;
     private String type = "Cash";
     private Double bucket1Pct = 0.;
     private Double bucket2Pct = 0.;
     private Double bucket3Pct = 0.;
     private Double todayChange = 0.0;
     private Double above50Day = 0.0;
     private Double above200Day = 0.0;



     public Investment( String ticker, String type, String description, Double numberOfShares, Double currentPrice, Double costBasis, Double targetPct ) {
         this.ticker = ticker;
         this.description = description;
         this.numberOfShares = numberOfShares;
         this.currentPrice = currentPrice;
         this.currentValue = this.numberOfShares * this.currentPrice;
         this.costBasis =  costBasis;
         this.gain = this.currentValue - this.costBasis;
         this.targetPct =  targetPct;

     }

     public Investment merge( Investment inv) {
         // add the quantities, value, cost and target pct...that's a merge.

         this.numberOfShares += inv.numberOfShares;
         this.currentValue +=  inv.getCurrentValue();
         this.costBasis += inv.getCostBasis();
         this.gain = this.gain + inv.getGain();
         this.targetPct += inv.getTargetPct();
         return this;
     }



     public Double getGain() {
         return setPrecision(gain,3);
     }
     public String getType() {
         return type;
     }

     public void setType(String type) {

         this.type = type;
         if (type.equalsIgnoreCase("cash"))
             this.gain= null;
     }

     public Double  getTargetPct() {

         return  bucket2Pct + bucket3Pct;
     }


     public String getTicker() {
         return ticker;
     }


     public Double getNumberOfShares() {
         return setPrecision(numberOfShares,2);
     }

     public void setNumberOfShares(Double numberOfShares) {
         this.numberOfShares =  numberOfShares;
         this.currentValue = this.numberOfShares * currentPrice;
         this.gain = currentValue - costBasis;
     }

     public Double getCurrentValue () {
         return setPrecision(currentValue,2);
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description ) {
         this.description = description;
     }

     public Double getCurrentPrice() {
         return setPrecision(currentPrice,2);
     }

    public void setCurrentPrice (Double currentPrice){

        this.currentPrice = currentPrice;
        this.currentValue = this.numberOfShares * currentPrice;
        this.gain = currentValue - costBasis;
    }

    public Double getCostBasis() {
        return setPrecision(costBasis,2);
    }

    public String getGainPct() {
         Double gainPct= 0.0;
         if (costBasis != 0.0)
        	 gainPct = setPrecision(100.* (currentValue - costBasis)/costBasis, 2);
        if (currentValue-costBasis == 0.0) {
            return "";
        }
         else {
             return setPrecision(currentValue - costBasis, 2).toString() +"(" + gainPct.toString()+ ")" ;
        }

    }
    public Double getBucket1Pct() {
         return bucket1Pct;
     }

     public void setBucket1Pct(Double bucket1Pct) {
         this.bucket1Pct = bucket1Pct;
     }

     public Double getBucketPct(int bucketId) {
         switch (bucketId) {
             case 1:
                 return bucket1Pct;

             case 2:
                 return bucket2Pct;

             case 3:
                 return bucket3Pct;

             default:
                 return 0.0;

         }
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

     public Double getActualPct() {
         return actualPct;
     }

     public void setActualPct(Double actualPct) {
         this.actualPct = actualPct;
     }

    public Double getTodayChange() {
        return todayChange;
    }

    public void setTodayChange(Double todayChange) {
        this.todayChange = todayChange;
    }

    public Double getAbove50Day() {
        return above50Day;
    }

    public void setAbove50Day(Double above50Day) {
        this.above50Day = above50Day;
    }

    public Double getAbove200Day() {
        return above200Day;
    }

    public void setAbove200Day(Double above200Day) {
        this.above200Day = above200Day;
    }
}


