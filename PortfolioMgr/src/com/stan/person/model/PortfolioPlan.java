package com.stan.person.model;

import java.util.ArrayList;

public class PortfolioPlan {


    private Double bucket1Allocation;
    private Double bucket2Allocation;
    private Double bucket3Allocation;

    ArrayList<PlanItem> planItems = new ArrayList<>();

    public void add(String csvString) {
        String[]values = csvString.split(",");
        switch (values[0]) {
            case "Buckets":
                bucket1Allocation = Double.parseDouble(values[3]);
                bucket2Allocation = Double.parseDouble(values[4]);
                bucket3Allocation = Double.parseDouble(values[5]);
                break;
            case  "End":
                return;
            default:
                PlanItem planItem = new PlanItem();
                planItem.ticker = values[0];
                planItem.type = values[1];
                planItem.description = values[2];
                planItem.bucket1Pct = Double.parseDouble(values[3]);
                planItem.bucket2Pct = Double.parseDouble(values[4]);
                planItem.bucket3Pct = Double.parseDouble(values[5]);
                planItems.add(planItem);
                break;
        }

    }

    public PlanItem getPlanItem( String ticker) {

        for(PlanItem item: planItems ){
            if (item.ticker.equalsIgnoreCase(ticker)) {
                return item;
            }

        }
        return null;
    }

    public Double getBucketAllocation(int bucketId) {
        switch (bucketId) {
            case 1:
                return bucket1Allocation;

            case 2:
                return bucket2Allocation;

            case 3:
                return bucket3Allocation;

            default:
                return 0.0;

        }
    }

    public Double getBucket1Allocation() {
        return bucket1Allocation;
    }

    public Double getBucket2Allocation() {
        return bucket2Allocation;
    }

    public Double getBucket3Allocation() {
        return bucket3Allocation;
    }

    public ArrayList<PlanItem> getPlanItems() {
        return planItems;
    }
}

