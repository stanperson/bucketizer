package com.stan.person.Fidelity;
import com.stan.person.model.Investment;
import com.stan.person.model.InvestmentReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FidelityInvestmentReader implements InvestmentReader {
	
    /*
    CSV from Fidelity has the first line set to:
       1: "Account Name/Number",
       2: "Symbol", <--------
       3:"Description",  <-------
       4: "Quantity", <---------
       5: "Last Price", <-------
       7: "Last Price Change",
       8: "Current Value",         <-------
       9: "Today's Gain/Loss Dollar",
       10: "Today's Gain/Loss Percent",
       11: "Total Gain/Loss Dollar",
       12: "Total Gain/Loss Percent",
       13: Cost Basis Per Share",
       14: "Cost Basis Total",      <--------
       15: "Type"

       There are several blank/text lines and then a Date down loaded field.
		
		The new CSV format has different order to the columns:
		1: "Account",
		2: "Symbol", <-----------
		3: "Description", <----------
		4: "Last Price", <--------
		5: "Last Price Change",
		6: "$ Today's Gain/Loss",
		7: "% Today's Gain/Loss",
		8: "$ Total Gain/Loss",
		9: "% Total Gain/Loss",
		10:"Current Value", <---------
		11:"Quantity", <.-----------
		12:"Cost Basis Per Share",
		13:"Cost Basis Total"   <----------

     */


    private List<Investment> investments;
    private Date dateDownloaded= null;
    private Double pendingActivity=0.0;
	final String SYMBOL = "Symbol";
	final String DESCRIPTION = "Description"; 
	final String LASTPRICE = "Last Price";
	final String CURRENTVALUE = "Current Value";
	final String QUANTITY = "Quantity";
	final String COSTBASIS = "Cost Basis Total";
	
	String[] columnIds = {SYMBOL, DESCRIPTION, LASTPRICE, CURRENTVALUE, QUANTITY, COSTBASIS};
	HashMap <String, Integer>columnMap= null;
	List<String> lines = new ArrayList<>();

    @Override
    public List<Investment> getInvestments() {
        return investments;
    }

    @Override
    public Date getDateDownloaded() {
        return dateDownloaded;
    }

    @Override
    public List<Investment> readInvestments(String filePath) {
    	Scanner inputStream;
    	if (filePath.isEmpty()){
    		System.out.println( "FidelityInvestmentReader received empty path to Portfolio Activity File");
    		System.exit(-1);
    	}
    	try{
    		File file= new File(filePath);
    		inputStream = new Scanner(file);
    		pendingActivity = 0.0;  // reset in case it isn't found below   		
    		String columnLabels = inputStream.nextLine();    		
        	columnMap = fidelityParseColumns(columnIds, columnLabels);
        	int versionNumber = fidelityVersion(columnMap); // try and figure out the version number from the labels.
    		if (versionNumber == 1) {
    			lines = readInvestmentsV1(inputStream);
    		} else {
    			lines = readInvestmentsV2(inputStream);
    		}
    	} catch(FileNotFoundException e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
        

        // transform from list of String (comma-delimited investments) to List of Investment objects
        investments = new ArrayList<>();

        for (String line: lines) {         
                String[] values = line.split(",") ;
 
                String ticker = values[columnMap.get(SYMBOL).intValue()];
                String description = values[columnMap.get(DESCRIPTION).intValue()];
                Double numberOfShares =  Double.parseDouble(values[columnMap.get(QUANTITY).intValue()]);

                Double currentPrice = Double.parseDouble(values[columnMap.get(LASTPRICE).intValue()]);
                Double currentValue =  Double.parseDouble(values[columnMap.get(CURRENTVALUE).intValue()]);
                Double costBasis = Double.parseDouble(values[columnMap.get(COSTBASIS).intValue()]);
                if (costBasis == 0) // fudge...n/a was replaced by 0 in input file.
                    costBasis = currentValue;
                Double targetPct = 0.0;
                Investment inv = new Investment(ticker, "defaultType", description, numberOfShares, currentPrice,  costBasis, targetPct); // creates an investment object from CSV text.Investment inv = new Investment();
                investments.add(inv);           
        }
        return investments;
    }
    
    private int fidelityVersion(HashMap<String,Integer> colMap) {    	
    	int version = 1;
    	if (colMap.get(QUANTITY).intValue() == 10) {
    		version = 2;
    	}
    	return version;
    }
    
    /*
     * input is an array of strings that has the text to look for in the line. The returned hash map has the id and the column 
     * number it was found in. Line is the first line of a CSV file that contains column labels.
     */
    private HashMap<String, Integer> fidelityParseColumns(String[] ids, String line){
    	String[] values = line.split(",");
/*    	for (int i = 0; i < values.length; i++) {
    		System.out.println(i + " " + values[i]);
    	}
*/    	HashMap<String, Integer> cols = new HashMap<>();
    	for (String id: ids) {
    		boolean found = false;
    		for (int i = 0; i < values.length; i++){
    			if (values[i].contains(id)){
    				cols.put(id, new Integer(i));
    				found = true;
    				//System.out.println("Found: " + id + "at " + i );
    				break;
    			}
     		}
   			if (! found) {
				System.out.println("Can't find " + id + " in " + line);
				System.exit(-1);
			}

    	}
    	return cols;
    }

    public Double getPendingActivity() {
        return pendingActivity;
    }
    
    List<String>  readInvestmentsV1( Scanner inputStream) {  
    	
    while(inputStream.hasNextLine()){
        String line = inputStream.nextLine();   
        if (line.isEmpty()) continue;
        if (line.startsWith(",,,,")) continue;  // filter out the lines with just commas
        if (line.startsWith("\"\"")) continue;  // filter out lines that look like "","","","","
        if (line.contains("Fidelity.com")) continue;
        if (line.contains("SIPC, NYSE")) continue;
        line = line.replaceAll(Pattern.quote("$"), "");
        line = line.replaceAll(Pattern.quote("%"), "");
        line = line.replaceAll(Pattern.quote("n/a"), "0.0");
        line = line.replaceAll("--", "0");
        line = line.replaceAll("\"", "");
        if (line.startsWith("Date")){
        	String origDate = line.substring(16, line.length() -1);
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        	try {
        		
        		this.dateDownloaded = sdf.parse(origDate);; //sdf2.format(finalDate);
        		System.out.println("readInvestmentsV1: " + dateDownloaded);
        		
        	} catch (ParseException dfe)
        	{
        		System.out.println("Fidelity Investment Reader Error formatting date: " + origDate + "using MM/dd/yyyy hh:mm a");
        		System.exit(-1);       		
        	}
        	
        } else if (line.startsWith("Pending")){
        	String[] values = line.split(",");
            try {
                if (values[1] != null)
                    pendingActivity = Double.parseDouble(values[1]);
            } catch (NumberFormatException e) {
                pendingActivity = 0.0;
                System.out.println("Pending activity number format exception:" + values[1] + "in Portfolio_Activity_File");
            }
        	
        } else {
        	lines.add(line);
        }
        
    }
    inputStream.close();
    return lines;
    }
    
    List<String>  readInvestmentsV2( Scanner inputStream) {   
    while(inputStream.hasNextLine()){
        String line= inputStream.nextLine();        
        if (line.isEmpty()) continue;
        if (line.startsWith(",,,,")) continue;  // filter out the lines with just commas
        if (line.startsWith("\"\"")) continue;  // filter out lines that look like "","","","","
        if (line.contains("Fidelity.com")) continue;
        if (line.contains("SIPC, NYSE")) continue;

        line = line.replaceAll(Pattern.quote("$"), "");
        line = line.replaceAll(Pattern.quote("%"), "");
        line = line.replaceAll(Pattern.quote("n/a"), "0.0");
        line = line.replaceAll(Pattern.quote("+"), "");
        line = line.replaceAll("--", "0");        //line = line.replaceAll("\"", "");
        if (line.startsWith("\"Date")){
        	String origDate = line.substring(17, 36);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd yy:mm:ss");
        	try {
         		dateDownloaded = sdf.parse(origDate);; //sdf2.format(finalDate);
        		
        	} catch (ParseException dfe)
        	{
        		System.out.println("Fidelity Investment Reader Error formatting date: " + origDate + " using format yyyy/MM/dd yy:mm:ss");        		
        	}
        	
        } else if (line.startsWith("\"Pending")){
        	String[] values = line.split(",");
            try {
                if (values[1] != null)
                    pendingActivity = Double.parseDouble(values[1]);
            } catch (NumberFormatException e) {
                pendingActivity = 0.0;
                System.out.println("Pending activity number format exception:" + values[1] + "in Portfolio_Activity_File");
            }
        	
        } else {
        	
        	// we have to get rid of all the quotes and ,'s in doubles. First split the line on "," and then get rid of embedded comma
        	String [] values = line.split("\",\"");
        	for (int i = 0; i < values.length; i++){
        		values[i] = values[i].replaceAll(",", "");
        	}
        	// now put the line back together as a CSV line
        	line = "";
        	for (int i = 0; i < values.length-1; i++){
        		line += values[i] + ",";
        	}
        	line += values[values.length-1]; // last one doesn't have a comma at the end
        	line = line.substring(0,line.length()-1); // split above left quotes at beginning and end.
        	lines.add(line);
        }      
    }
    inputStream.close();
    return lines;
    } 
}

