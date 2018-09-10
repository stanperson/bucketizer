package com.stan.person.persistance;

public class PersistenceSQL {
	public static final String parInsert =
			"INSERT INTO par"
					+ "(ACTIVITYDATE,PENDINGCASH)"
					+ "VALUES"
					+ "( ?,?)";

	public static final String invInsert =
			"INSERT INTO INV"
					+ "(ticker,description,currentPrice,priceDateTime,numberShares,costBasis,type,bucket1Pct,bucket2Pct,bucket3Pct,actualPct,targetPct)"
					+ "VALUES"
					+ "(?,?,?,?,?,?,?,?,?,?,?,?)";

}
