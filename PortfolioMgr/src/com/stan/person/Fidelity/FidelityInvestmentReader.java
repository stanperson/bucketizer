package com.stan.person.Fidelity;
import com.stan.person.model.Investment;
import com.stan.person.model.InvestmentReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FidelityInvestmentReader implements InvestmentReader {

    private List<Investment> investments;
    private String dateDownloaded;
    private Double pendingActivity=0.0;

    @Override
    public List<Investment> getInvestments() {
        return investments;
    }

    @Override
    public String getDateDownloaded() {
        return dateDownloaded;
    }

    @Override
    public List<Investment> readInvestments(String filePath) {


        /*
        csv from Fidelity has the first line set to:
           1: "Account Name/Number",
           2: "Symbol","Description",  <-------
           3: "Quantity","Last Price", <-------
           4: "Last Price Change",
           5: "Current Value",         <-------
           6: "Today's Gain/Loss Dollar",
           7: "Today's Gain/Loss Percent",
           8: "Total Gain/Loss Dollar",
           9: "Total Gain/Loss Percent",
           10: Cost Basis Per Share",
           11: "Cost Basis Total",      <--------
           12: "Type"

           There are several blank/text lines and then a Date downloaded field.


         */



        // this gives you a 2-dimensional array of strings
        List<String> lines = new ArrayList<>();
        Scanner inputStream;
        if (filePath.isEmpty()){
            System.out.println( "FidelityInvestmentReader received empty path to Portfolio Activity File");
            System.exit(-1);
        }
        try{
            File file= new File(filePath);
            inputStream = new Scanner(file);

            while(inputStream.hasNextLine()){
                String line= inputStream.nextLine();
                if (line.isEmpty()) break;

                line = line.replaceAll(Pattern.quote("$"), "");
                line = line.replaceAll(Pattern.quote("n/a"), "0");
                line = line.replaceAll("--", "0");
                line = line.replaceAll("\"", "");
                lines.add(line);
            }

            // now look for the "date downloaded" and "Pending Activity field
            while ( inputStream.hasNextLine()){
                String line = inputStream.nextLine();
                if (line.isEmpty()) continue;
                if (line.startsWith(",,,")) continue; // numbers sets empty lines to have all commas...why?
                line=line.replaceAll("\"", "");  // get rid of quotes
                if (line.startsWith("Date")) {
                    lines.add(0, line);
                    break;
                }else if (line.startsWith("Pending")) {
                    String[] values = line.split(",");
                    try {
                        if (values[1] != null)
                            pendingActivity = Double.parseDouble(values[1]);
                    } catch (NumberFormatException e) {
                        pendingActivity = 0.0;
                        System.out.println("Pending activity number format exception:" + values[1] + " at " + filePath);
                    }
                }
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        int lineNo = 0;

        // transform from list of String (comma-delimited investments) to List of Investment objects

        dateDownloaded="none found";
        investments = new ArrayList<>();

        for (String line: lines) {
            // first line has a date downloaded in it
            if (lineNo == 0) {
                dateDownloaded = line;
            }
            // second line has the column headers in it...skip it
            if (lineNo > 1) {
                String[] values = line.split(",") ;
                String ticker = values[1];
                String description = values[2];
                Double numberOfShares =  Double.parseDouble(values[3]);

                Double currentPrice = Double.parseDouble(values[4]);
                Double currentValue =  Double.parseDouble(values[6]);
                Double costBasis = Double.parseDouble(values[12]);
                if (costBasis == 0) // fudge...n/a was replaced by 0 in input file.
                    costBasis = currentValue;
                Double targetPct = 0.0;

                Investment inv = new Investment(ticker, "defaultType", description, numberOfShares, currentPrice,  costBasis, targetPct); // creates an investment object from CSV text.Investment inv = new Investment();
                investments.add(inv);
            }
            lineNo++;
        }
        return investments;
    }

    public Double getPendingActivity() {
        return pendingActivity;
    }
}
