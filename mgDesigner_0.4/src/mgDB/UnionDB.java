package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class UnionDB {

	String query;
	public Vector<Integer> bgSPVec = new Vector<Integer>();
	public Vector<Integer> bgEPVec = new Vector<Integer>();
	public Vector<Integer> operonIDVec = new Vector<Integer>();
	public Vector<Integer> tuIDVec = new Vector<Integer>();
	public Vector<Integer> opSPVec = new Vector<Integer>();
	public Vector<Integer> opEPVec = new Vector<Integer>();
	public Vector<String> isOperonVec = new Vector<String>();
	public Vector<Integer> tuSPVec = new Vector<Integer>();
	public Vector<Integer> tuEPVec = new Vector<Integer>();
	public Vector<String> egIndexVec = new Vector<String>();
	public Vector<Integer> opIndexVec = new Vector<Integer>();
	public Vector<Integer> tuIndexVec = new Vector<Integer>();
	public Vector<Integer> opbgIndexVec = new Vector<Integer>();
	public Vector<Integer> bgopIndexVec = new Vector<Integer>();
	public Vector<Integer> opegIndexVec = new Vector<Integer>();
	public Vector<Integer> egopIndexVec = new Vector<Integer>();

	
	void unionDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
	    
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			
			query = "(select bgSP, bgEP from bGenome where egIndex = 1) union" +
					" (select bgSP as bgSP, bgEP as bgEP " +
					"from Operon) order by bgSP";
				
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
//				operonIDVec.add(Integer.parseInt(rs.getString("operonid")));
				bgSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				bgEPVec.add(Integer.parseInt(rs.getString("bgep")));
//				strandVec.add(rs.getString("strand"));
//				geneNameVec.add(rs.getString("geneName"));
//				funCatVec.add(rs.getString("funcat"));                 
//				orgVec.add(rs.getString("organism")); 
			
	//				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
	//				giVec.add(Integer.parseInt(rs.getString("gi")));
	//				egIndexVec.add(Integer.parseInt(rs.getString("egIndex")));
	
			}
			rs.close();
			stmt.close();
			con.close();
//			System.out.println("geneNameVec.size() = " + geneNameVec.size());
			System.out.println("bgSPVec.size() = " + bgSPVec.size());
			System.out.println("operonIDVec.size() = " + operonIDVec.size());
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}	
}
