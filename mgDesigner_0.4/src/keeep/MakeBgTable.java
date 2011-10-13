package keeep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class MakeBgTable {
	
	public void makeBgTable() {
		
		String tableName ="bGenome";
		String bgIDField = "bgID";
		String bgSPField = "bgSP";
		String bgEPField = "bgEP";
		String bgStrandField = "bgStrand";
//		String egIndexField = "egIndex";
		String funCatField = "funCat";
		String organismField = "organism";
		String mgSPField = "mgSP";
		String mgEPField = "mgEP";
		String mcSPField = "mcSP";
		String mcEPField = "mcEP";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
//		set max_prepared_stmt_count = 5000;
		
		try {
	        
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eg", "root", "1234");
			stmt = conn.createStatement(); 

			String dropTableQuery = "drop table if exists " + tableName;
			stmt.executeUpdate(dropTableQuery);
			
			String createTableQuery = "create table " + tableName + " (" + 
				bgIDField + " int, " + bgSPField + " int, " + bgEPField + " int, " + bgStrandField + " int, " +
				funCatField + " varchar(3), " + organismField + " text, " +
				mgSPField + " int, " + mgEPField + " int, " + mcSPField + " int, " +
				mcEPField + " int)";

			stmt.executeUpdate(createTableQuery);
			stmt.close();
			conn.close();
			
		} catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException 발생"); 
		} catch(SQLException e){
			System.out.println("SQLException 발생"); 
			e.printStackTrace();
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){}
			}
			if(stmt != null){
				try{
					stmt.close();
				}catch(SQLException e){}
			}
			if(conn != null){
				try{
					conn.close();
				}catch(SQLException e){}
			}		
		}	
	}
}
