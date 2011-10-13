package mgDB;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Vector;


import mgFile.MgFileCreator;

public class OrgDB {
	
	public static void main(String[] args) {
		
		Vector<Integer> bgIDVec = new Vector<Integer>();
		Vector<String> orgVec = new Vector<String>();
		Vector<String> locusVec = new Vector<String>();
		Vector<Integer> seqlenVec = new Vector<Integer>();
		Vector<Integer> circularVec = new Vector<Integer>();
//		Vector<Integer> egIndexVec = new Vector<Integer>();
//		Vector<String> funCatVec = new Vector<String>();
//		Vector<Integer> mgSPVec = new Vector<Integer>();
//		Vector<Integer> mgEPVec = new Vector<Integer>();
//		Vector<Integer> mcSPVec = new Vector<Integer>();
//		Vector<Integer> mcEPVec = new Vector<Integer>();
//		Vector<Integer> egSPVec = new Vector<Integer>();
//		Vector<Integer> mcegSPVec = new Vector<Integer>();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
//		set max_prepared_stmt_count = 5000;
		

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = conn.createStatement(); 
		
			String tableName ="orgData";
			
			String orgIDField = "orgID";
//			String bgIDField = "bgID";  orgName으로 연결해도 되지않을까.. 보류보류
			String orgNameField = "orgName";
			String locusField = "locus";
			String seqlenField = "seqlen";
			String circularField = "circular";

//			String egIndexField = "egIndex";
//			String funCatField = "funCat";
//			String mgSPField = "mgSP";
//			String mgEPField = "mgEP";
//			String mcSPField = "mcSP";
//			String mcEPField = "mcEP";

			String dropTableQuery = "drop table if exists " + tableName;
			stmt.executeUpdate(dropTableQuery);
			
			String createTableQuery = "create table " + tableName + " (" + 
				orgIDField + " int, " + orgNameField + " text, " + 
				locusField + " varchar(15), " + seqlenField + " int, " + circularField + " int"+ ")";
			
		
			stmt.executeUpdate(createTableQuery);
			
			File f = new File(".\\data\\egGenome\\");
			File[] files = f.listFiles();
			for (int k = 0; k < files.length; k++){
				System.out.println(k + 1 + ", ");       //반복 현황을 파악하기 위헤!!!!!!!!
//				if (files[k].getName().startsWith("NC_")) {                  //NC_.......gbk !!!!
					String sFile = files[k].getPath();
					//String sFile = ".\\data\\egGenome\\NC_000913.gbk";
					MgFileCreator mgFileCreator = new MgFileCreator(sFile);
									
					orgVec.add(mgFileCreator.getOrgName()); 
					locusVec.add(mgFileCreator.getLocusName());
					seqlenVec.add(mgFileCreator.getSeqLen());
					if (mgFileCreator.getCircular()!="")
						circularVec.add(1);					
						else
							circularVec.add(-1);
			
			}
			System.out.println("orgVec.size() = " + orgVec.size());
			System.out.println("circular.size()= " + circularVec.size());		
			System.out.println("locusVec.size() = " + locusVec.size());		
			
			String insertQuery = "insert into orgData " +
					"(orgID, orgName, locus, seqlen, circular)" +
					" values(?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			System.out.println("PreparedStatement(" + insertQuery + ")로 생성됨.");
				
			for(int i = 0 ; i < orgVec.size() ; i++){
				pstmt.setInt(1, i + 1);
				System.out.print(i + 1 + ", ");       //반복 현항을 파악하기 위헤!!!!!!!!

				pstmt.setString(2, orgVec.elementAt(i).toString());
				pstmt.setString(3, locusVec.elementAt(i).toString());
				pstmt.setInt(4, seqlenVec.elementAt(i).intValue());
				pstmt.setInt(5, circularVec.elementAt(i).intValue());
		
				pstmt.executeUpdate(); //매개인자가 없다는 것에 주의하라.
			}	
		
			System.out.println("PreparedStatement를 통한 가변 필드값 Insert 실행 완료.");		
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