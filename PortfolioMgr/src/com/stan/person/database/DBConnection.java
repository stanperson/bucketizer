package com.stan.person.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DBConnection {
	public enum QueryStatus {OK, FAILED, DUPLICATE, EMPTY};
	public enum DBCommand {NEWEST, NEXT, PREV, OLDEST}
	// TODO: externalize in a property file
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/PortfolioMgr";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "carefx211";
	static  Connection conn = null;
	static PreparedStatement ps = null;
	static Statement s = null;

	public static void connection() {
		try{

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		//end try
	}

	@SuppressWarnings("unused")
	private static Connection getConn() {
		if (conn == null)
			connection();
		return conn;
	}

	public static void closeAll() {
		try {
			if (ps != null)
				ps.close();
			if (s != null) {
				s.close();
			}
			if (conn != null)
				conn.close();

		} catch (SQLException sqle){
			sqle.printStackTrace();
		} finally{
			//finally block used to close resources
			try{
				if(s!=null)
					s.close();
				if (ps!=null)
					ps.close();
			}catch(SQLException se2){
				se2.printStackTrace();
				System.exit(-2);
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
				System.exit(-3);
			}
		}//end finally try
	}
	private static SQLException lastSQLE= new SQLException(); // create new empty one so I don't return a null.

	public static SQLException getLastException(){


			return lastSQLE;
	}
	private static ResultSet rs;
	public static QueryStatus executeQuery() {
		QueryStatus qs = QueryStatus.OK;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			System.out.println("Query error: " + e.getMessage());
			qs = QueryStatus.FAILED;
		}
		
		return qs;
	}
	public static ResultSet getRS() {
		return rs;
	}
	
	public static QueryStatus executeUpdate() {

		QueryStatus qs = QueryStatus.OK;
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			//e.printStackTrace();
			closeAll();

			lastSQLE = e;
			if (e.getSQLState().startsWith("23")) {
				qs = QueryStatus.DUPLICATE;
			} else {
				qs = QueryStatus.FAILED;
			}
		}
		return qs;
	}
	public static void setDate(int parameterIndex, Date value) {
		try {
			// convert from java.util.Date value to java.sql.Date dateValue
			java.sql.Date dateValue = new java.sql.Date(value.getTime());
			ps.setDate(parameterIndex, dateValue);
		} catch (SQLException e) {
			e.printStackTrace();
			closeAll();
			System.exit(-1);
			}

	}
	public static void setDouble(int parameterIndex, Double value ){
		try {
			ps.setDouble(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
			closeAll();
			System.exit(-1);
			}
	}
	public static void setString(int parameterIndex, String value ){
		try {
			ps.setString(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
			closeAll();
			System.exit(-1);
			}
	}

	public static void preparedStatement(String sql) {

		try {
			if (ps != null)
				ps.close();
			connection();
			ps = conn.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
			closeAll();
			System.exit(-1);
		}

	}
}


