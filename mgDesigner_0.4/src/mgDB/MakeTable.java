package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class MakeTable {

	public static ResultSet rs;
	String query;
	String data;
	String orgName;
	
	Vector<Integer> geneidVec = new Vector<Integer>();
	Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> spVec = new Vector<Integer>();
	Vector<Integer> epVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	Vector<String> funcDefVec = new Vector<String>();
	Vector<Integer> mGSPVec = new Vector<Integer>();
	Vector<Integer> mGEPVec = new Vector<Integer>();
	int exe =0;

	
	public static void main(String args[]){
		
		Connection con;
	    Statement stmt = null;
/*	    
		GetOrgName getOrgName = new GetOrgName();
		this.orgName = getOrgName.getorgName();
*/
		String orgName = "Escherichia coli str. K-12 substr. MG1655";
		Vector<String> queryVec = new Vector<String>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eg", "root", "1234");
			stmt = con.createStatement();

// table 생성			

			
			
			

/* table 합치기			
						
			String query = "INSERT Ignore INTO bGenome SELECT * FROM bGenome1";
			stmt.executeUpdate(query);
									
*/			
//			show table status from eg like 'bgenome';
//			alter table bGenome max_rows = 1000
			
			
/*	컬럼 생성		
			queryVec.add("alter table genedata add mgsp int");
			queryVec.add("alter table genedata add mgep int");
			queryVec.add("alter table genedata add mcsp int");
			queryVec.add("alter table genedata add mcep int");
			
			for (int i=0; i < queryVec.size(); i++) {
				stmt.executeUpdate(queryVec.elementAt(i).toString());
			}
*/
			
/*	검색		
			query = "select geneid, gi, functionclass, sposition, eposition, Description from genedata where organism = '"+orgName+"';";
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
				giVec.add(Integer.parseInt(rs.getString("gi")));
				spVec.add(Integer.parseInt(rs.getString("sposition")));           //모든 vector의 원소수는 689개
				epVec.add(Integer.parseInt(rs.getString("eposition")));
				funcClassVec.add(rs.getString("functionclass"));                  // 값이 있는 원소수는 630개
				funcDefVec.add(rs.getString("Description"));
			}
			rs.close();
*/			

/*	검색	& 정렬	
			query = "select geneid, gi, functionclass, sposition, eposition, Description from genedata where organism = '"+orgName+"';";
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
				giVec.add(Integer.parseInt(rs.getString("gi")));
				spVec.add(Integer.parseInt(rs.getString("sposition")));           //모든 vector의 원소수는 689개
				epVec.add(Integer.parseInt(rs.getString("eposition")));
				funcClassVec.add(rs.getString("functionclass"));                  // 값이 있는 원소수는 630개
				funcDefVec.add(rs.getString("Description"));
			}
			rs.close();
*/			

			stmt.close();
			con.close();
//			System.out.println(spVec.size() + ", " + funcClassVec.size());			
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}
	
	public Vector<Integer> getgeneidVec(){
		return geneidVec;
	}
	
	public Vector<Integer> getgiVec(){
		return giVec;
	}
	
	public Vector<Integer> getspVec(){
		return spVec;
	}
	
	public Vector<Integer> getepVec(){
		return epVec;
	}
	
	public Vector<String> getfuncClassVec(){
		return funcClassVec;
	}
	
	public Vector<String> getfuncDefVec(){
		return funcDefVec;
	}

}

