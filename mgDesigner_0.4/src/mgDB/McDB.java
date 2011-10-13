package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class McDB {
	String query;
	String data;
	String orgName;
	//int genomeOption;


	public Vector<Integer> bgIDVec = new Vector<Integer>();
	public Vector<Integer> mcSPVec = new Vector<Integer>();
	public Vector<Integer> mcEPVec = new Vector<Integer>();
	public Vector<Integer> strandVec = new Vector<Integer>();
	public Vector<String> geneNameVec = new Vector<String>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<String> orgVec = new Vector<String>();	
	
//	public Vector<Integer> geneidVec = new Vector<Integer>();
//	public Vector<Integer> giVec = new Vector<Integer>();
//	public Vector<Integer> bgSPVec = new Vector<Integer>();
//	public Vector<Integer> bgEPVec = new Vector<Integer>();
//	public Vector<Integer> mcSPVec = new Vector<Integer>();
//	public Vector<Integer> mcEPVec = new Vector<Integer>();
//	public Vector<Integer> bgStrandVec = new Vector<Integer>();
//	public Vector<Integer> bgspVec = new Vector<Integer>();
//	public Vector<Integer> bgepVec = new Vector<Integer>();
//	public Vector<Integer> mgspVec = new Vector<Integer>();
//	public Vector<Integer> mgepVec = new Vector<Integer>();
//	public Vector<Integer> mcspVec = new Vector<Integer>();
//	public Vector<Integer> mcepVec = new Vector<Integer>();
//	public Vector<Integer> egIndexVec = new Vector<Integer>();
	
	
		public static void main(String args[]){
			new McDB();	
		}
			
		public McDB() {
			
			Connection con = null;
	    	Statement stmt = null;
	    	ResultSet rs = null;
	
	    
			try {
				CallDb();

				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
				stmt = con.createStatement(); 
			
				String tableName ="mChromosome";
				
				String mcIDField = "mcID";
				String bgIDField = "bgID";
				String bgStrandField = "bgStrand"; // StrandField로 통일하는게 좋을듯.
				String mcSPField = "mcSP";
				String mcEPField = "mcEP";
				String geneNameField = "geneName";
				String funCatField = "funCat";
				String organismField = "organism";
				
				String dropTableQuery = "drop table if exists " + tableName;
					stmt.executeUpdate(dropTableQuery);
				
				
				String createTableQuery = "create table " + tableName + " (" + 
						mcIDField + " int, " +bgIDField + " int, " +
						mcSPField + " int, " + mcEPField + " int, " + bgStrandField + " int, " +
						geneNameField + " varchar(30), " +funCatField + " varchar(3), " + 
						organismField + " text" + ")";
					stmt.executeUpdate(createTableQuery);

				
				String insertQuery = "insert into " + tableName + " (mcID, bgID," +
							"mcSP, mcEP, bgStrand, geneName, funCat, organism )" +
							"values(?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement pstmt = con.prepareStatement(insertQuery);
					System.out.println("PreparedStatement(" + insertQuery + ")로 생성됨.");
					
					for(int i = 0 ; i < bgIDVec.size() ; i++){
						pstmt.setInt(1, i + 1);
						System.out.print(i + 1 + ", ");       //반복 현항을 파악하기 위헤!!!!!!!!
						pstmt.setInt(2, bgIDVec.elementAt(i).intValue());
						pstmt.setInt(3, mcSPVec.elementAt(i).intValue());
						pstmt.setInt(4, mcEPVec.elementAt(i).intValue());
						pstmt.setInt(5, strandVec.elementAt(i).intValue());
						pstmt.setString(6, geneNameVec.elementAt(i).toString());
						pstmt.setString(7, funCatVec.elementAt(i).toString());
						pstmt.setString(8, orgVec.elementAt(i).toString());
						pstmt.executeUpdate(); //매개인자가 없다는 것에 주의하라.		

					}
					
/*					boolean found = false;
					for (int j = 0; j < egSPVec.size(); j++){
						if (bgSPVec.elementAt(i).intValue() == egSPVec.elementAt(j).intValue()) {
							pstmt.setString(5, funCatVec.elementAt(j).toString());
							pstmt.setInt(7, +1);
							pstmt.setInt(8, mgSPVec.elementAt(j).intValue());
							pstmt.setInt(9, mgEPVec.elementAt(j).intValue());
							found = true;
							break;
		
						}
					}
					if(!found){
						pstmt.setString(5, "X");
						pstmt.setInt(7, -1);
						pstmt.setInt(8, -1);
						pstmt.setInt(9, -1);
					}
					
					boolean mcfound = false;
					for (int j = 0; j < mcSPVec.size(); j++){
						if (bgSPVec.elementAt(i).intValue() == mcegSPVec.elementAt(j).intValue()) {
							pstmt.setInt(10, mcSPVec.elementAt(j).intValue());
							pstmt.setInt(11, mcEPVec.elementAt(j).intValue());
							mcfound = true;
							break;
		
						}
					}
					if(!mcfound){
						pstmt.setInt(10, -1);
						pstmt.setInt(11, -1);
					}
*/											
				
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
			if(con != null){
				try{
					con.close();
				}catch(SQLException e){}
			}
		}
	}	
	
	
		void CallDb() {
			
			Connection con = null;
	    	Statement stmt = null;
	    	ResultSet rs = null;
	    
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select bgID, mcSP, mcEP, bgStrand, geneName, funCat, organism" +
					" from bGenome where mcEP != -1 ";
			rs = stmt.executeQuery(query);
			System.out.println("stmt= " + stmt);
			
			while(rs.next())
			{
				bgIDVec.add(Integer.parseInt(rs.getString("bgid")));
				mcSPVec.add(Integer.parseInt(rs.getString("mcsp")));     
				mcEPVec.add(Integer.parseInt(rs.getString("mcep")));
				strandVec.add(Integer.parseInt(rs.getString("bgstrand")));
				geneNameVec.add(rs.getString("geneName"));
				funCatVec.add(rs.getString("funcat"));                 
				orgVec.add(rs.getString("organism")); 
			
//				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
//				giVec.add(Integer.parseInt(rs.getString("gi")));
//				egIndexVec.add(Integer.parseInt(rs.getString("egIndex")));

			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println("geneNameVec.size() = " + geneNameVec.size());
			System.out.println("mcSPVec.size() = " + mcSPVec.size());
			System.out.println("bgIDVec.size() = " + bgIDVec.size());
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}		
		
}