package sqlTools;

import hauptFenster.Reha;

import java.awt.Cursor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;



import systemEinstellungen.SystemConfig;

public class SqlInfo {
	
/***********************************/	
	public static boolean gibtsSchon(String sstmt){
		boolean gibtsschon = false;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			rs = stmt.executeQuery(sstmt);
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if(rs.next()){
				gibtsschon = true;
			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return gibtsschon;
	}
/*******************************************/
	public static int holeId(String tabelle, String feld){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt1 = "insert into "+tabelle+" set "+feld+" = '"+SystemConfig.dieseMaschine+"'";
			stmt.execute(sstmt1);			
			String sstmt2 = "select id from "+tabelle+" where "+feld+" = '"+SystemConfig.dieseMaschine+"'";
			rs = stmt.executeQuery(sstmt2);
			if(rs.next()){
				retid = rs.getInt("id");
			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return retid;
	}
/*******************************/
	public static Vector holeSatz(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			int nichtlesen = ausschliessen.size();
			if(rs.next()){
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(nichtlesen > 0){
						 if(!ausschliessen.contains( rsMetaData.getColumnName(i)) ){
							 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );						 
						 }
					 }else{
						 retvec.add(rs.getString(i));
					 }
				 }
			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return (Vector)retvec.clone();
	}
/*****************************************/
	/*******************************/
	public static Vector holeSaetze(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		Vector<Vector> retkomplett = new Vector<Vector>();	
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			int nichtlesen = ausschliessen.size();
			while(rs.next()){
				retvec.clear();
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(nichtlesen > 0){
						 if(!ausschliessen.contains( rsMetaData.getColumnName(i)) ){
							 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );						 
						 }
					 }else{
						 retvec.add(rs.getString(i));
					 }
				 }
				 retkomplett.add((Vector)retvec.clone());
			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return (Vector)retkomplett.clone();
	}
/*****************************************/

}
