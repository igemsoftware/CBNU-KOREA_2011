package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class OperonDB {
	
	public static void main(String args[]) {
		
		String idField = "ID";
		String tuIDField = "TuID";
		String isOperonField = "isOperon";
		String operonIDField = "OperonID";
		String geneCountField = "GeneCount";
		String geneTypeField = "GeneType";
		String conservedField = "Conserved";
		String giField = "GI";
		String locusTagField = "locus_tag";
		String startField = "bgSP";
		String endField = "bgEP";
		String strandField = "Strand";
		String lengthField = "Length";
		String cogNumField = "COG_number";
		String productField = "Product";
		String fromtoField = "FromTo";
		String scoreField = "Score";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String fileName = "D://workspace//mgDesigner_0.4//data/operon//operonsoft.txt";
		
		try {
	        
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = conn.createStatement(); 
// for OPR  String tableName = "Operon";
//for Soft	
			String tableName = "OperonSoft";
			
			String dropTableQuery = "drop table if exists " + tableName;
			stmt.executeUpdate(dropTableQuery);
/*	for OPR		
			String createTableQuery = "create table " + tableName + " (" + 
				operonIDField + " int, " + giField + " int, " + locusTagField + " char(5), "  + 
				startField + " int, " + endField + " int, " + strandField + " char(1), " +
				lengthField + " int, " + cogNumField + " varchar(30), " + productField + " text)";
*/
//  for Soft
			String createTableQuery = "create table " + tableName + " (" + 
				idField + " int, " + tuIDField + " int, " + isOperonField + " char(2), "  + 
				geneCountField + " int, " + conservedField + " char(1), "  + strandField + " char(1), " + 
				geneTypeField + " char(7), " + startField + " int, " + fromtoField + " char(1), " + endField + " int, " + 
				scoreField + " int)";

			stmt.executeUpdate(createTableQuery);
			
			String insertDataQuery = "load data infile '" + fileName + "' " +
					"into table " + tableName + " fields terminated by '\t' lines terminated by '\n'";
			stmt.executeUpdate(insertDataQuery);
			
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
