package com.stan.person.model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuoteReader {

    private List<Quote> quotes;
    private String[] symbols;
    private Portfolio portfolio;

    public QuoteReader(Portfolio portfolio) {
        this.portfolio = portfolio;

        // extract the fixed income and equity type ticker symbols from the portfolio
        ArrayList<String> mySymbols = new ArrayList<>();
        for (Investment inv: portfolio.getInvestments()) {
            if (!inv.getType().equalsIgnoreCase("cash")) {
                // not cash, must be one of fixed or equity
                String ticker = inv.getTicker();
                mySymbols.add(ticker);
            }
        }
        symbols = new String[mySymbols.size()];
        mySymbols.toArray(symbols);

    }

    public String getQuotesAsText() {
        Date now = new Date();

        String quoteText = "Major Indices at: " + now +  "\n";
        ObservableList<Quote> observableList = getQuotesAsObservableList();
        for (int i = 0; i < observableList.size(); i++) {
            Quote quote = observableList.get(i);
            Double gainLoss = 0.0;
            if (quote.getOpen() == null) {
                gainLoss = 0.0;
            } else {
                gainLoss = quote.getChange().setScale(2,RoundingMode.HALF_UP).doubleValue();
                //gainLoss = (quote.getPrice().subtract(quote.getOpen())).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            //update portfolio with new price
            Investment inv = portfolio.get(quote.getSymbol());

            if (inv != null) {
            	if (quote.getPrice().doubleValue() > 0.0) {
            		inv.setCurrentPrice(quote.getPrice().setScale(2,RoundingMode.HALF_UP).doubleValue());
            		inv.setTodayChange( gainLoss);
            		inv.setAbove50Day( quote.getChange50().setScale(2,RoundingMode.HALF_UP).doubleValue());
            		inv.setAbove200Day(quote.getChange200().setScale(2,RoundingMode.HALF_UP).doubleValue());
            	} else {
            		String description = inv.getDescription();
            		if (!description.endsWith("*"))
            			inv.setDescription(inv.getDescription() + "*");
            	}

            }

            if (quote.getSymbol().startsWith("^")) {
                String currentPrice = String.format("%1$9s", quote.getPrice().setScale(2,RoundingMode.HALF_UP).toString());
                quoteText += quote.getSymbol() + "\t" + currentPrice + "\t" + String.format("%1$7s", gainLoss.toString()) + "\n";
            }
        }
        return quoteText;
    }

    public ObservableList<Quote> getQuotesAsObservableList() {
        // new Portfolio(symbol, type, description,currentPrice, currentValue, costBasis)
        //YahooFinance yf = new YahooFinance();
        Map<String, Stock> stocks;
        quotes = new ArrayList<>();
        ObservableList<Quote> quotesAsList = FXCollections.observableArrayList();
        if (symbols != null) {
            String[] augmentedSymbols = {"^DJI", "^GSPC", "^IXIC", "^RUT"};
            String[] allSymbols = new String[augmentedSymbols.length + symbols.length];

            System.arraycopy(augmentedSymbols, 0, allSymbols, 0, augmentedSymbols.length);
            System.arraycopy(this.symbols, 0, allSymbols, augmentedSymbols.length, symbols.length);

            try {

                stocks = YahooFinance.get(allSymbols);
                int size = stocks.size();
                for (int i = 0; i < size; i++) {
                    Stock stock = stocks.get(allSymbols[i]);
                    if (stock != null) {
                        StockQuote sq = stock.getQuote();
                        String symbol = sq.getSymbol();
                        BigDecimal price = sq.getPrice();
                        if (price.doubleValue() == 0.0)
                        	System.out.println("Caution...quoted price is zero for ticker: " + symbol);
                    
                        BigDecimal open = sq.getOpen();
                        BigDecimal change = sq.getChange();
                        BigDecimal change50 = sq.getChangeFromAvg50();
                        BigDecimal change200 = sq.getChangeFromAvg200();
                    quotes.add(new Quote(symbol, price, open, change, change50, change200));
                       
                    } else {
                        System.out.println("Nothing available for " + symbols[i]);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            quotesAsList.addAll(quotes);
        }
        return quotesAsList;
    }
}
