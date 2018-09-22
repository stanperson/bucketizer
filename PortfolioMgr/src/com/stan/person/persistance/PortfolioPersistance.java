package com.stan.person.persistance;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stan.person.database.DBConnection;
import com.stan.person.database.DBConnection.DBCommand;
import com.stan.person.database.DBConnection.QueryStatus;
import com.stan.person.model.Investment;
import com.stan.person.model.Portfolio;
import com.stan.person.model.PortfolioPlan;


public class PortfolioPersistance {


	@SuppressWarnings("unused")
	public static QueryStatus portfolioWriter( Portfolio portfolio) {

		Double pendingCash = portfolio.getPendingCash();
		PortfolioPlan plan = portfolio.getPortfolioPlan();
		DBConnection.preparedStatement(PersistenceSQL.parInsert);
		DBConnection.setDate(1, portfolio.getDateDownloaded());
		DBConnection.setDouble(2,pendingCash);
		QueryStatus qs;
		if ((qs =DBConnection.executeUpdate()) == QueryStatus.OK ){
			DBConnection.preparedStatement(PersistenceSQL.invInsert);
			List<Investment>investments = portfolio.getInvestments();
			for (Investment inv: investments) {
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
				qs = DBConnection.executeUpdate();
			}
		}
		switch (qs)  {
		case OK:
			break;
		case DUPLICATE:
			break;
		case FAILED:
			break;

		}
		DBConnection.closeAll();
		return qs;
	}

	public static QueryStatus portfolioReader( DBCommand dc,Portfolio portfolio ) {
		QueryStatus qs = QueryStatus.OK;
		switch ( dc  ){
		case NEWEST:
			DBConnection.preparedStatement(PersistenceSQL.parFindNewest);
			qs = DBConnection.executeQuery();
			switch (qs) {
			case OK:
				hydratePortfolio(DBConnection.getRS(), portfolio);
				break;
			default:
				qs = QueryStatus.FAILED;
				break;
			}
			break;
		default:
			System.out.println("PortfolioPersistance has not implemented: " + dc);
			qs = QueryStatus.FAILED;
			break;

		}
		return qs;
	}

	private static void hydratePortfolio(ResultSet rs, Portfolio portfolio){
		List<Investment> investments= new ArrayList<>();
		try {
			Double pc = 0.0;
			Date dd= null;
			int rsCntr = 0;
			while (rs.next()) {
				if (rsCntr++ == 0) {
					dd = (Date)rs.getDate("activityDate");
					pc = rs.getDouble("pendingCash");
				}
				Investment inv = new Investment(rs.getString("ticker"),
												rs.getString("type"),
												rs.getString("inv.description"),
												rs.getDouble("numberShares"),
												rs.getDouble("currentPrice"),
												rs.getDouble("costBasis"),
												rs.getDouble("targetPct"));

				investments.add(inv);
			}
			portfolio.setInvestmentActivity(investments, dd, pc);
			

		}
		catch (SQLException e) {
			e.printStackTrace();

		}
	}

}
