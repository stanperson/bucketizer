package com.stan.person;

import com.stan.person.configuration.ConfigProperties;
import com.stan.person.model.*;
import com.stan.person.view.ColorCodedTableCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.stan.person.utility.Math.setPrecision;


public class Controller implements Initializable {
    @FXML
    private TextField activityFilePath;
    @FXML
    private TextField pendingCash;
    @FXML
    private TableView<Investment> portfolioTableView;
    @FXML
    private TableColumn<Investment, String> ticker ;
    @FXML
    private TableColumn<Investment, String> description;
    @FXML
    private TableColumn<Investment, String> type;
    @FXML
    private TableColumn<Investment, Double> numberOfShares;
    @FXML
    private TableColumn<Investment, Double> currentPrice;
    @FXML
    private TableColumn<Investment, Double> currentValue;
    @FXML
    private TableColumn<Investment, Double> costBasis;
    @FXML
    private TableColumn<Investment, Double> gain;
    @FXML
    private TableColumn<Investment, String> gainWithPct; // String value that shows gain as NNN(Pct)
    @FXML
    private TableColumn<Investment, Double> actualPct;
    @FXML
    private TableColumn<Investment, Double> targetPct;
    @FXML
    private TableColumn<Investment, Double> todayChange;
    @FXML
    private TableColumn<Investment, Double> above50Day;
    @FXML
    private TableColumn<Investment, Double> above200Day;
    @FXML
    private TextField portfolioValue;
    @FXML
    private TextField totalCostBasis;
    @FXML
    private TextField totalGain;
    @FXML
    private TextField totalCashValue;
    @FXML
    private TextField totalFixedValue;
    @FXML
    private TextField totalEquityValue;
    @FXML
    private TextField bucket1;
    @FXML
    private TextField bucket2;
    @FXML
    private TextField bucket3;
    @FXML
    private TextArea quoteArea;

    /*
    @FXML
    private TableColumn<Investment, Double> targetPct;
    */

    //private final static Properties properties = new Properties();

    private InvestmentReaderFactory investmentReaderFactory = new InvestmentReaderFactory();
    private InvestmentReader reader;
    private Portfolio portfolio;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        File planFilePath = new File(System.getProperty("user.dir") + ConfigProperties.getProperty("portfolioPlanPath","/resources/PortfolioPlan.csv"));
        if (!planFilePath.exists()) {
            // for now, bark and quit
            Properties systemProp = System.getProperties();

            System.out.println("user.dir: " + systemProp.getProperty("user.dir"));
            System.out.println("user.home: " + systemProp.getProperty("user.home"));
            System.out.println("Plan file not at: " + planFilePath.getPath() +"...bailing out.");
            System.exit(-1);
        }

        PortfolioPlan plan = PortfolioPlanReader.readPortfolioPlan(planFilePath);
        portfolio = new Portfolio(plan);


        // get the path to a Portfolio_Activity-MMM-DD-YYYY.csv file
        File activityDirectory = new File(ConfigProperties.getProperty("portfolioActivityPath", "/Users/stanperson/Downloads/"));
        ArrayList<File> fileInFolder = listFilesForFolder(activityDirectory);
        for (File f : fileInFolder) {
            if (f.getName().startsWith(ConfigProperties.getProperty("activityFilePrefix", "Portfolio_Position"))) {
                activityFilePath.setText(f.getPath());
                break;
            }
        }

        // hook up the table columns with the object (Investment) values.
        // set up special formatting.
        ticker.setCellValueFactory(new PropertyValueFactory<>("ticker"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        numberOfShares.setCellValueFactory(new PropertyValueFactory<>("numberOfShares"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        //currentPrice.setCellFactory(new ColorCodedTableCellFactory<>());
        above50Day.setCellValueFactory(new PropertyValueFactory<>("above50Day"));
        above50Day.setCellFactory(new ColorCodedTableCellFactory<>());
        above200Day.setCellValueFactory(new PropertyValueFactory<>("above200Day"));
        above200Day. setCellFactory(new ColorCodedTableCellFactory<>());
        currentValue.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
        costBasis.setCellValueFactory(new PropertyValueFactory<>("costBasis"));
        gainWithPct.setCellValueFactory(new PropertyValueFactory<>("gainPct"));
        gainWithPct.setCellFactory(new ColorCodedTableCellFactory<>());
        targetPct.setCellValueFactory(new PropertyValueFactory<>("targetPct"));
        actualPct.setCellValueFactory(new PropertyValueFactory<>("actualPct"));
        todayChange.setCellValueFactory(new PropertyValueFactory<>("todayChange"));
        todayChange.setCellFactory(new ColorCodedTableCellFactory<>());

        // Read the input file (this one is from Fidelity).
        reader = investmentReaderFactory.getInvestmentReader(ConfigProperties.getProperty("investmentCompany", "Fidelity"));
        reader.readInvestments(activityFilePath.getText());
        portfolio.setPendingCash(reader.getPendingActivity());
        portfolio.setInvestments(reader.readInvestments(activityFilePath.getText()));
        pendingCash.setText(reader.getPendingActivity().toString());

        portfolioTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                tableRowSelected();
            }
        });
        refresh();
    }

    @FXML
    private void refresh() {
        // update the quotes from Yahoo
    	/*
        QuoteReader quoteReader = new QuoteReader(portfolio);
        quoteArea.clear();
        quoteArea.setText(quoteReader.getQuotesAsText());
		*/
        portfolio.setDateDownloaded(reader.getDateDownloaded());
        portfolio.setPendingCash(Double.valueOf(pendingCash.getText()));

        ObservableList<Investment> invAsList = FXCollections.observableArrayList();
        invAsList.addAll(portfolio.getInvestments());
        portfolioTableView.getItems().clear();
        portfolioTableView.setItems(invAsList);
        portfolioValue.setText( setPrecision( portfolio.getTotalValue(),2).toString());
        totalCostBasis.setText( setPrecision( portfolio.getTotalCost(),2).toString());
        totalGain.setText( setPrecision(portfolio.getTotalGain(), 2) + "(" + setPrecision(100.* portfolio.getTotalGain()/portfolio.getTotalCost(),2) + "%)" );
        totalCashValue.setText( getTextByType("cash"));
        totalFixedValue.setText( getTextByType("fixed"));
        totalEquityValue.setText( getTextByType("equity"));
        bucket1.setText(portfolio.getTotalByBucket(1));
        bucket2.setText(portfolio.getTotalByBucket(2));
        bucket3.setText(portfolio.getTotalByBucket(3));
    }

    private String getTextByType(String type) {
        return setPrecision( portfolio.getTotalByType(type),2) + "(" + setPrecision(100.* portfolio.getTotalByType(type)/portfolio.getTotalValue(),2) + "%)";

    }
    
    @FXML
    public void refreshClicked() {
        QuoteReader quoteReader = new QuoteReader(portfolio);
        quoteArea.clear();
        quoteArea.setText(quoteReader.getQuotesAsText());
        refresh();
    }

    @FXML
    public void quitProgram() {
        Main.getWindow().close();
        System.exit(0);
    }

    @FXML
    public void openActivityFileClicked() {
        Stage window = Main.getWindow();
        File openName = (new FileChooser()).showOpenDialog(window);
        if (openName != null) {
            activityFilePath.setText(openName.getPath());
            portfolio.setInvestments(reader.readInvestments(activityFilePath.getText()));
            pendingCash.setText(reader.getPendingActivity().toString());
            refresh();
        }
    }
    @FXML
    public void openPortfolioPlan() {
        Stage window = Main.getWindow();
        File openName = new FileChooser().showOpenDialog(window);
        if (openName != null) {
            PortfolioPlan plan = PortfolioPlanReader.readPortfolioPlan(openName);
            portfolio = new Portfolio(plan);
            reader = investmentReaderFactory.getInvestmentReader(ConfigProperties.getProperty("investmentCompany", "Fidelity"));
            reader.readInvestments(activityFilePath.getText());
            portfolio.setInvestments(reader.getInvestments());
            refresh();
        }

    }

    @FXML
    public void tableRowSelected() {
        Investment selectedInvestment = portfolioTableView.getSelectionModel().getSelectedItem();
        if (selectedInvestment != null) {
            //System.out.println("Requesting: " + "https://finance.yahoo.com/quote/" + selectedInvestment.getTicker());
            System.out.println("requesting: " + "https://screener.fidelity.com/ftgw/etf/goto/snapshot/snapshot.jhtml?symbols=" + selectedInvestment.getTicker());
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://screener.fidelity.com/ftgw/etf/goto/snapshot/snapshot.jhtml?symbols=" + selectedInvestment.getTicker()));
                }catch (IOException|URISyntaxException e){
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("selectedInvestment is null");
        }
    }


    public static ArrayList<File> listFilesForFolder(final File folder) {

        ArrayList<File> filesInFolder = new ArrayList<File>();
        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            File fileEntry = files[i];
            if (fileEntry.isDirectory()) {
                // ignore directories for this simple case
            } else {
                filesInFolder.add(fileEntry);
            }
        }
        return filesInFolder;
    }
/*
    private class FormatTableCell extends TableCell<Investment,Double> {
        @Override
        protected void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            }
            if (item != null) {
                if (item.compareTo(0.0) == 0) {
                    setText(null);
                } else if (item.compareTo(1.0) == 0) {
                    setText(null);
                } else {
                    if (item.compareTo(0.0) > 0)
                        setTextFill(Color.GREEN);
                    else
                        setTextFill(Color.RED);
                    setText(setPrecision(item, 2).toString());
                }
            }

        }
    }
*/
}
