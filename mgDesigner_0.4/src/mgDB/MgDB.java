package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class MgDB {
	String query;
	String data;
	String orgName;
	//int genomeOption;


//	public Vector<Integer> bgIDVec = new Vector<Integer>();
	public Vector<Integer> mgSPVec = new Vector<Integer>();
	public Vector<Integer> mgEPVec = new Vector<Integer>();
	public Vector<Integer> strandVec = new Vector<Integer>();
	public Vector<String> geneNameVec = new Vector<String>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<String> orgVec = new Vector<String>();	
	
//	public Vector<Integer> geneidVec = new Vector<Integer>();
//	public Vector<Integer> giVec = new Vector<Integer>();
	public Vector<Integer> bgSPVec = new Vector<Integer>();
	public Vector<Integer> bgEPVec = new Vector<Integer>();
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
			new MgDB();	
		}
			
		public MgDB() {
			
			Connection con = null;
	    	Statement stmt = null;
	    	ResultSet rs = null;
	
	    
			try {
				CallDb();

				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
				stmt = con.createStatement(); 
			
				String tableName ="mGenome";
				
				String mgIDField = "mgID";
//				String bgIDField = "bgID";
				String bgStrandField = "bgStrand"; // StrandField로 통일하는게 좋을듯.
				String mgSPField = "mgSP";
				String mgEPField = "mgEP";
				String geneNameField = "geneName";
				String funCatField = "funCat";
				String organismField = "organism";
				String bgSPField = "bgSP";
				String bgEPField = "bgEP";
//				String egIndexField = "egIndex";
//				String mcSPField = "mcSP";
//				String mcEPField = "mcEP";
				
/* 같이돌려도가능할까?
  				String selectquery ="select bgID, mgSP, mgEP, bgStrand, " +
									"geneName, funCat, organism " +
									"from bGenome where egIndex = -1 ";
					rs = stmt.executeQuery(selectquery);
					System.out.println("stmt= " + stmt);
					
					while(rs.next())
					{
						bgIDVec.add(Integer.parseInt(rs.getString("bgid")));
						mgSPVec.add(Integer.parseInt(rs.getString("mgsp")));     
						mgEPVec.add(Integer.parseInt(rs.getString("mgep")));
						strandVec.add(Integer.parseInt(rs.getString("bgstrand")));
						geneNameVec.add(rs.getString("geneName"));
						funCatVec.add(rs.getString("funcat"));                 
						orgVec.add(rs.getString("organism")); 
					}
*/
				
				String dropTableQuery = "drop table if exists " + tableName;
					stmt.executeUpdate(dropTableQuery);
				
				
				String createTableQuery = "create table " + tableName + " (" + 
						mgIDField + " int, " + bgSPField + " int, " + bgEPField + " int, " +
						mgSPField + " int, " + mgEPField + " int, " + bgStrandField + " int, " +
						geneNameField + " varchar(30), " +funCatField + " varchar(3), " + 
						organismField + " text" + ")";
					stmt.executeUpdate(createTableQuery);

				
				String insertQuery = "insert into mGenome (mgID, bgSP, bgEP, " +
							"mgSP, mgEP, bgStrand, geneName, funCat, organism )" +
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement pstmt = con.prepareStatement(insertQuery);
					System.out.println("PreparedStatement(" + insertQuery + ")로 생성됨.");
					
					for(int i = 0 ; i < mgSPVec.size() ; i++){
						pstmt.setInt(1, i + 1);
						System.out.print(i + 1 + ", ");       //반복 현항을 파악하기 위헤!!!!!!!!
						pstmt.setInt(2, bgSPVec.elementAt(i).intValue());
						pstmt.setInt(3, bgEPVec.elementAt(i).intValue());
						pstmt.setInt(4, mgSPVec.elementAt(i).intValue());
						pstmt.setInt(5, mgEPVec.elementAt(i).intValue());
						pstmt.setInt(6, strandVec.elementAt(i).intValue());
						pstmt.setString(7, geneNameVec.elementAt(i).toString());
						pstmt.setString(8, funCatVec.elementAt(i).toString());
						pstmt.setString(9, orgVec.elementAt(i).toString());
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
			System.out.println();	
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
			query = "select bgSP, bgEP, mgSP, mgEP, bgStrand, geneName, funCat, organism" +
					" from bGenome where egIndex = 1 ";
			rs = stmt.executeQuery(query);
			System.out.println("stmt= " + stmt);
			
			while(rs.next())
			{
				bgSPVec.add(Integer.parseInt(rs.getString("bgsp")));
				bgEPVec.add(Integer.parseInt(rs.getString("bgep")));
				mgSPVec.add(Integer.parseInt(rs.getString("mgsp")));     
				mgEPVec.add(Integer.parseInt(rs.getString("mgep")));
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
			System.out.println("mgSPVec.size() = " + mgSPVec.size());
			System.out.println("bgSPVec.size() = " + bgSPVec.size());
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}		
		
}
