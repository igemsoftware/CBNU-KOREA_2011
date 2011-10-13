package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DbmanagerV0 {

	public static ResultSet rs;
	String query;
	String data;
	
	Vector<Integer> geneidVec = new Vector<Integer>();
	Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> spVec = new Vector<Integer>();
	Vector<Integer> epVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	Vector<String> funcDefVec = new Vector<String>();
	
	int exe =0;
	
	public DbmanagerV0(String orgName) {
		
		Connection con;
	    Statement stmt = null;
	    
//	    GetOrgNameV0 getOrgName = new GetOrgNameV0();
//	    String orgName = getOrgName.getorgName();
//		String orgName = "Escherichia coli str. K-12 substr. MG1655";
	    
	    
		if(exe==0){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select geneid, gi, functionclass, sposition, eposition, Description from genedata where organism = '"+orgName+"';";
			rs = stmt.executeQuery(query);
			//int num = 0;
			while(rs.next())
			{
				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
				giVec.add(Integer.parseInt(rs.getString("gi")));
				//spVec.add((int)(Integer.parseInt(rs.getString("sposition")))); //직접 int 형으로 저장하는 방법은? FuncCat에 넘겨줌
				spVec.add(Integer.parseInt(rs.getString("sposition")));           //모든 vector의 원소수는 689개
				epVec.add(Integer.parseInt(rs.getString("eposition")));
				funcClassVec.add(rs.getString("functionclass"));
				//if (rs.getString("functionclass")!= "") num++;                   // 값이 있는 원소수는 630개
				funcDefVec.add(rs.getString("Description"));
			}
			rs.close();
			stmt.close();
			con.close();
			//System.out.println(funcClassVec.elementAt(0) + "; " + funcClassVec.size() + "; " + num);			
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		
		}
		exe=1;
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
/*	
	public static void main(String args[]){
		String fileName = args[0];
		new DbmanagerV0();	
	}
	*/
}

