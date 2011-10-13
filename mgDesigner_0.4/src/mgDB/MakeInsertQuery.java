package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;

public class MakeInsertQuery {
	
	public Vector<Integer> bgSPVec = new Vector<Integer>();
	public Vector<Integer> bgEPVec = new Vector<Integer>();
	public Vector<Integer> bgStrandVec = new Vector<Integer>();
	public Vector<Integer> egIndexVec = new Vector<Integer>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<String> orgVec = new Vector<String>();
	public Vector<Integer> mgSPVec = new Vector<Integer>();
	public Vector<Integer> mgEPVec = new Vector<Integer>();
	public Vector<Integer> mcSPVec = new Vector<Integer>();
	public Vector<Integer> mcEPVec = new Vector<Integer>();
	public Vector<Integer> egSPVec = new Vector<Integer>();
	public Vector<Integer> mcegSPVec = new Vector<Integer>();

	private int bgIDVal;
	private int bgSPVal;
	private int bgEPVal;
	private int bgStrandVal;
	private String funCatVal;
	private String organismVal;
	private int mgSPVal;
	private int mgEPVal;
	private int mcSPVal;
	private int mcEPVal;
	private int loopCount;
	private String orgName;

	public void makeInsertQuery (int loopCount, Vector<Integer> bgSPVec, Vector<Integer> bgEPVec,
			Vector<Integer> bgStrandVec, Vector<String> funCatVec, String orgName, Vector<Integer> mgSPVec,
			Vector<Integer> mgEPVec, Vector<Integer> egSPVec, Vector<Integer> mcSPVec,
			Vector<Integer> mcEPVec, Vector<Integer> mcegSPVec) {
	
		this.loopCount = loopCount;
		this.bgSPVec = bgSPVec;
		this.bgEPVec = bgEPVec;
		this.bgStrandVec = bgStrandVec;
		this.funCatVec = funCatVec;
//		orgVec = orgVec;
		this.orgName = orgName;
		this.mgSPVec = mgSPVec;
		this.mgEPVec = mgEPVec;
		this.egSPVec = egSPVec;
		
		this.mcSPVec = mcSPVec;
		this.mcEPVec = mcEPVec;
		this.mcegSPVec = mcegSPVec;
		
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
			
			int STMT_MAX = 80;  //한번에 수행할 수 있는  stmt 최대 회수가 80임
		    int stmtSP = STMT_MAX * loopCount;
		    
			for(int i = stmtSP ; i < stmtSP + STMT_MAX ; i++){
				if (i < bgSPVec.size()) {
					bgIDVal = i+1;
					bgSPVal = bgSPVec.elementAt(i).intValue();
					bgEPVal = bgEPVec.elementAt(i).intValue();
					bgStrandVal = bgStrandVec.elementAt(i).intValue();
					organismVal = orgName;
 
					System.out.print(bgIDVal + ", ");       //반복 현항을 파악하기 위헤!!!!!!!!
					
					boolean found = false;
					for (int j = 0; j < egSPVec.size(); j++){
						if (bgSPVec.elementAt(i).intValue() == egSPVec.elementAt(j).intValue()) {
							funCatVal = funCatVec.elementAt(j).toString();
							mgSPVal = mgSPVec.elementAt(j).intValue();
							mgEPVal = mgEPVec.elementAt(j).intValue();
							found = true;
							break;
						}
					}
					if(!found){
						funCatVal = "X";
						mgSPVal = -1;
						mgEPVal = -1;
					}
					
					boolean mcfound = false;
					for (int j = 0; j < mcSPVec.size(); j++){
						if (bgSPVec.elementAt(i).intValue() == mcegSPVec.elementAt(j).intValue()) {
							mcSPVal = mcSPVec.elementAt(j).intValue();
							mcEPVal = mcEPVec.elementAt(j).intValue();
							mcfound = true;
							break;
						}
					}
					if(!mcfound){
						mcSPVal = -1;
						mcEPVal = -1;
					}
					
					String insertQuery = "insert into " + tableName + "(" + bgIDField + ", " + bgSPField + ", " + bgEPField +
					", " + bgStrandField + ", " + funCatField + ", " + organismField +
					", " + mgSPField + ", " + mgEPField + ", " + mcSPField + ", " + mcEPField + 
					" ) values(" + bgIDVal + ", " + bgSPVal + ", " + bgEPVal + ", " + bgStrandVal +
					", '" + funCatVal + "', '" + organismVal + "', " + mgSPVal + ", " + mgEPVal +
					", " + mcSPVal + ", " + mcEPVal + ")";	
								
					stmt.executeUpdate(insertQuery);
				}
			}
			
			stmt.close();
			conn.close();
			System.out.println(); 
			System.out.println("Insert 실행 완료.");
			
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
