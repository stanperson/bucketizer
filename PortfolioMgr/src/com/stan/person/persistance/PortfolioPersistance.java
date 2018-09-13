package com.stan.person.persistance;

import java.util.List;

import com.stan.person.database.DBConnection;
import com.stan.person.model.Investment;
import com.stan.person.model.Portfolio;
import com.stan.person.model.PortfolioPlan;


public class PortfolioPersistance {


	@SuppressWarnings("unused")
	public static String PortfolioWriter( Portfolio portfolio) {


		Double pendingCash = portfolio.getPendingCash();
		PortfolioPlan plan = portfolio.getPortfolioPlan();
		DBConnection.preparedStatement(PersistenceSQL.parInsert);
		DBConnection.setDate(1, portfolio.getDateDownloaded());
		DBConnection.setDouble(2,pendingCash);
		if (DBConnection.executeUpdate() ){
			DBConnection.preparedStatement(PersistenceSQL.invInsert);
			List<Investment>investments = portfolio.getInvestments();
			for (Investment inv: investments) {
				System.out.println("Processing: " + inv.getTicker());
				DBConnection.setString(1,inv.getTicker());
				DBConnection.setString(2, inv.getDescription());
				DBConnection.setDouble(3, inv.getCurrentPrice());
				DBConnection.setDate(4,  portfolio.getDateDownloaded() );
				DBConnection.setDouble(5, inv.getNumberOfShares());
				DBConnection.setDouble(6, inv.getCostBasis());
				DBConnection.setString(7, inv.getType());
				DBConnection.setDouble(8, inv.getBucket1Pct());
				DBConnection.setDouble(9, inv.getBucket2Pct());
				DBConnection.setDouble(10, inv.getBucket3Pct());
				DBConnection.setDouble(11, inv.getActualPct());
				DBConnection.setDouble(12, inv.getTargetPct());
				if (! DBConnection.executeUpdate()){
					System.out.println("Update Failed:" + DBConnection.getLastException().getMessage());
					return DBConnection.getLastException().getMessage();
				} else {
					System.out.println("success");
				}
			} 
		} else {
			System.out.println("Update Failed:" + DBConnection.getLastException().getMessage());
			return DBConnection.getLastException().getMessage();
		}
		DBConnection.closeAll();
		return "OK";
	}

}
