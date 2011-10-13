package mgDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class Dbmanager07 {

	public static ResultSet rs;
	String query;
	String data;
	String orgName;
	int genomeOption;
	Vector<Integer> geneidVec = new Vector<Integer>();
	Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> bgSPVec = new Vector<Integer>();
	Vector<Integer> bgEPVec = new Vector<Integer>();
	Vector<Integer> mgSPVec = new Vector<Integer>();
	Vector<Integer> mgEPVec = new Vector<Integer>();
	Vector<Integer> mcSPVec = new Vector<Integer>();
	Vector<Integer> mcEPVec = new Vector<Integer>();
	Vector<String> geneNameVec = new Vector<String>();
	Vector<String> funCatVec = new Vector<String>();
	Vector<Integer> bgStrandVec = new Vector<Integer>();
	Vector<Integer> strandVec = new Vector<Integer>();
	Vector<Integer> spVec = new Vector<Integer>();
	Vector<Integer> epVec = new Vector<Integer>();
	Vector<String> orgVec = new Vector<String>();
	Vector<Integer> egIndexVec = new Vector<Integer>();;
	String locus;
	int seqLen;
	int circular;
	
	public Dbmanager07(int genomeOption, String orgName) {
		
		this.genomeOption = genomeOption;
		this.orgName = orgName;
		Connection con;
	    Statement stmt = null;
/*	    
		GetOrgName getOrgName = new GetOrgName();
		this.orgName = getOrgName.getorgName();
*/
		
	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select bgsp, bgep, bgstrand, geneName, egIndex, funcat, mgsp, mgep, mcsp, mcep" +
			// locus, seqLen, circular, 잠시 뺐음. 11.05.24
					" from bGenomepre where organism = '"+orgName+"';";
			rs = stmt.executeQuery(query);
			System.out.println("genomeOption= " + genomeOption);
			
			while(rs.next())
			{
//				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
//				giVec.add(Integer.parseInt(rs.getString("gi")));

				strandVec.add(Integer.parseInt(rs.getString("bgstrand")));
				geneNameVec.add(rs.getString("geneName"));
				egIndexVec.add(Integer.parseInt(rs.getString("egIndex")));
				funCatVec.add(rs.getString("funcat"));                 
//				locus = rs.getString("locus");
//				seqLen = Integer.parseInt(rs.getString("seqLen"));
//				circular = Integer.parseInt(rs.getString("circular"));
				
				if (genomeOption == 1) {
					spVec.add(Integer.parseInt(rs.getString("bgsp"))); 
					epVec.add(Integer.parseInt(rs.getString("bgep")));
				}
				if (genomeOption == 2) {
					spVec.add(Integer.parseInt(rs.getString("mgsp")));     
					epVec.add(Integer.parseInt(rs.getString("mgep")));
				}
				if (genomeOption == 3) {
					spVec.add(Integer.parseInt(rs.getString("mcsp")));       
					epVec.add(Integer.parseInt(rs.getString("mcep")));
				}
				if ((genomeOption == 2) || (genomeOption == 3)) {
					Vector<Integer> idxVec = new Vector<Integer>();
					for (int i = 0; i < spVec.size(); i++) 
						if (spVec.elementAt(i).intValue() == -1) idxVec.add(i);	
					for (int i = idxVec.size()-1; i >= 0; i--){ 
						 spVec.remove(idxVec.elementAt(i).intValue());
						 epVec.remove(idxVec.elementAt(i).intValue());
						 strandVec.remove(idxVec.elementAt(i).intValue());
						 geneNameVec.remove(idxVec.elementAt(i).intValue());
						 funCatVec.remove(idxVec.elementAt(i).intValue());
						 egIndexVec.remove(idxVec.elementAt(i).intValue());
					}
				}
			}
			System.out.println("spVec.size= " + spVec.size());
			rs.close();
			stmt.close();
			con.close();
			System.out.println("geneNameVec.size= " + geneNameVec.size() + ", " +
					"funCatVec.size= " + funCatVec.size());			
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}
	
	public String getLocus() {
		return locus;
	}
	
	public int getSeqLen() {
		return seqLen;
	}
	
	public int getCircular() {
		return circular;
	}
	
	public Vector<Integer> getgeneidVec(){
		return geneidVec;
	}
	
	public Vector<Integer> getgiVec(){
		return giVec;
	}
	
	public Vector<Integer> getSPVec(){
		return spVec;
	}
	
	public Vector<Integer> getEPVec(){
		return epVec;
	}
	
	public Vector<String> getGeneNameVec(){
		return geneNameVec;
	}
	
	public Vector<Integer> getStrandVec(){
		return strandVec;
	}
	
	public int getSP(int i) {                        // 유전자 시작 위치 반환
		return (spVec.elementAt(i).intValue());
	}
	
	public int getEP(int i) {                        // 유전자 끝위치 반환
		return (epVec.elementAt(i).intValue());
	}
	
	public int getGeneNumber() {                             // 유전자 개수 반환
		return geneNameVec.size();
	}
	
	public String getGeneName(int i) {                             // 유전자 개수 반환
		return (geneNameVec.elementAt(i).toString());
	}

	public Vector<String> getFunCatVec(){
		return funCatVec;
	}
	
	public int getStrand(int i) {                     // 유전자 방향      +1, -1
		
		return (strandVec.elementAt(i).intValue());
	}
	
	public Vector<Integer> getEgIndexVec() {
		return egIndexVec;
	}
	
	/*	
	public Vector<String> getfuncDefVec(){
		return funcDefVec;
	}
*/	
	public static void main(String args[]){
		int genomeOption = Integer.parseInt(args[0]);
		String orgName = args[1];
		new Dbmanager07(genomeOption, orgName);	
	}
}

