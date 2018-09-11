package com.stan.person.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.stan.person.utility.Math.setPrecision;

public class Portfolio {
    private List<Investment> investments= new ArrayList<>();
    private PortfolioPlan portfolioPlan;
    private Date dateDownloaded;
    private Double pendingCash= 0.0;


    // can't create a new portfolio without having a portfolio plan. Need a portfolio plan to classify and set up bucket
    // parameters.
    public Portfolio(PortfolioPlan portfolioPlan) {
        this.portfolioPlan = portfolioPlan;
    }

    private void addInvestment(Investment investment) {
        String ticker = investment.getTicker();
        Investment investment1 = get(ticker);
        if (investment1 == null) {
            // it's not there yet...get type from plan (default to cash)
            PlanItem item = portfolioPlan.getPlanItem(ticker);
            if (item != null) {
                investment.setType(item.getType());
                investment.setBucket2Pct(item.getBucket2Pct());
                investment.setBucket3Pct(item.getBucket3Pct());
            }
            investments.add(investment);
        } else {
            // already one there, merge them
            investment1.merge(investment);
        }
    }
    public void setInvestments(List<Investment> investments) {

        // rather than just copy the investments parm, add one at a time
        // so can apply plan and merge if necessary.
        this.investments = new ArrayList<>();
        for (Investment investment: investments) {
            addInvestment(investment);
        }
        /* 
         * add any investments from the portfolio plan that are NOT in  List investments
         */
        for (PlanItem item: portfolioPlan.getPlanItems()) {
        	if (this.get(item.getTicker()) == null) {
        		// this plan item ticker is not in List investments...add it
        		Investment investment = new Investment(item.getTicker(), item.getType(),item.getDescription(), 0.0, -1.0, 0.0, item.getBucket2Pct() + item.getBucket3Pct() );
        		addInvestment(investment);
        		System.out.println("adding investment " + item.getTicker());
        	}
        	
        }
        // update actualPct in each investment, now that we have the whole set in place.
        Double percentConvert = 100.0/this.getTotalValue();
        for (Investment investment: investments) {
            investment.setActualPct(Double.valueOf(String.format("%.1f", investment.getCurrentValue()* percentConvert)));
        }
    }


    public Investment get(String ticker) {
        for (Investment inv: investments )
            if (inv.getTicker().equalsIgnoreCase(ticker)) {

                return inv;
            }

        return null;
    }
    public List<Investment> getInvestments() {
        return this.investments;
    }
    public PortfolioPlan getPortfolioPlan() {
    	return portfolioPlan;
    }
    
    public void setPlanItems(PortfolioPlan portfolioPlan) {
        this.portfolioPlan = portfolioPlan;
    }
    
    public void setDateDownloaded(Date downloadDate) {
    	this.dateDownloaded = downloadDate;   	
    }
    /*
    public void setDateDownloaded(String dateDownloaded) {
    	try {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
    		sdf.parse(dateDownloaded);
    		this.dateDownloaded = dateDownloaded;
    	} catch (ParseException pe) {
    		System.out.println("dateDownloaded incorrectly formatted, should be yyyy-MM-dd HH:mm:00, found" + dateDownloaded);
    		pe.printStackTrace();
    		System.exit(-1);
    	}
    	
    }
*/
    public Date getDateDownloaded() {
        return dateDownloaded;
    }

    public Double getPendingCash() {
        return pendingCash;
    }

    public void setPendingCash(Double pendingCash) {
        this.pendingCash = pendingCash;
    }

    public Double getTotalValue() {

        Double total = 0.0;
        for (Investment investment: investments) {
            total += investment.getCurrentValue();
        }
        return total += pendingCash;
    }

    public Double getTotalCost() {

        Double total = 0.0;
        for (Investment investment: investments) {
            total += investment.getCostBasis();
        }

        return total += pendingCash;
    }

    public Double getTotalGain() {
        return getTotalValue()-getTotalCost();
    }

    public Double getTotalByType(String type) {
        Double total = 0.0;
        for (Investment investment: investments) {
            if (investment.getType().equalsIgnoreCase(type)) {
                total += investment.getCurrentValue();
            }
        }
        if (type.equalsIgnoreCase("cash")) {
            total+= pendingCash;
        }
        return total;

    }

    public String getTotalByBucket(int bucketNumber) {
        Double bucketSize = 0.0;

        if (bucketNumber == 1) {
            // sum up the cash positions in the collection of investments
            for (Investment investment: investments) {
                if (investment.getType().equalsIgnoreCase("cash")) {
                    bucketSize += investment.getCurrentValue();
                }
            }
            bucketSize += pendingCash;
        } else if (bucketNumber > 1) {
            for (Investment investment : investments) {
                if (investment.getBucketPct(bucketNumber) > 0.0) {
                    bucketSize += investment.getCurrentValue();
                }
            }
        }
        return setPrecision(bucketSize, 2) + "(" + setPrecision((100. * bucketSize/getTotalValue()),1) + "%:" + setPrecision(portfolioPlan.getBucketAllocation(bucketNumber),1)+ "%)";
    }

}
