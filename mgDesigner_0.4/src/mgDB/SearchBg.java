package mgDB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class SearchBg {

	public static ResultSet rs;
	String query;
	String data;
	String orgName;
	//int genomeOption;
	public Vector<Integer> geneidVec = new Vector<Integer>();
	public Vector<Integer> giVec = new Vector<Integer>();
	public Vector<Integer> bgSPVec = new Vector<Integer>();
	public Vector<Integer> bgEPVec = new Vector<Integer>();
	public Vector<Integer> mgSPVec = new Vector<Integer>();
	public Vector<Integer> mgEPVec = new Vector<Integer>();
	public Vector<Integer> mcSPVec = new Vector<Integer>();
	public Vector<Integer> mcEPVec = new Vector<Integer>();
	public Vector<String> geneNameVec = new Vector<String>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<Integer> bgStrandVec = new Vector<Integer>();
	public Vector<Integer> strandVec = new Vector<Integer>();
	public Vector<Integer> bgspVec = new Vector<Integer>();
	public Vector<Integer> bgepVec = new Vector<Integer>();
	public Vector<Integer> mgspVec = new Vector<Integer>();
	public Vector<Integer> mgepVec = new Vector<Integer>();
	public Vector<Integer> mcspVec = new Vector<Integer>();
	public Vector<Integer> mcepVec = new Vector<Integer>();
	public Vector<String> orgVec = new Vector<String>();
	public Vector<Integer> egIndexVec = new Vector<Integer>();
	
	
		public SearchBg(String orgName) {
		
//		this.genomeOption = genomeOption;
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
					" from bGenome where organism = '"+ orgName + "';";
			rs = stmt.executeQuery(query);
			System.out.println("stmt= " + stmt);
			
			while(rs.next())
			{
//				geneidVec.add(Integer.parseInt(rs.getString("geneid")));
//				giVec.add(Integer.parseInt(rs.getString("gi")));
				
				strandVec.add(Integer.parseInt(rs.getString("bgstrand")));
				geneNameVec.add(rs.getString("geneName"));
				egIndexVec.add(Integer.parseInt(rs.getString("egIndex")));
				funCatVec.add(rs.getString("funcat"));                 
//				if (genomeOption == 1) {
					bgspVec.add(Integer.parseInt(rs.getString("bgsp"))); 
					bgepVec.add(Integer.parseInt(rs.getString("bgep")));
//				}
//				if (genomeOption == 2) {
					mgspVec.add(Integer.parseInt(rs.getString("mgsp")));     
					mgepVec.add(Integer.parseInt(rs.getString("mgep")));
//				}
//				if (genomeOption == 3) {
					mcspVec.add(Integer.parseInt(rs.getString("mcsp")));       
					mcepVec.add(Integer.parseInt(rs.getString("mcep")));
//				}
/*				if ((genomeOption == 2) || (genomeOption == 3)) {
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
*/			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println("geneNameVec.size() = " + geneNameVec.size());
			System.out.println("bgspVec.size() = " + bgspVec.size());
			
//			int t = 2;
//			System.out.println(bgspVec.elementAt(t).intValue());
//			System.out.println("geneNameVec.size= " + geneNameVec.size() + ", " +
//					"funCatVec.size= " + funCatVec.size());		
			
			makesearchFile();
			 
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

		void makesearchFile() {
			
			try {
				RandomAccessFile raf = new RandomAccessFile("searchgene.txt", "rw");

				raf.seek(raf.length()); 
						
				raf.writeBytes("orgName= " + orgName);
				raf.writeBytes("\n" + "\n");
		
				
				raf.writeBytes("num" + "\t");
				raf.writeBytes("bgspVec" + "\t");
				raf.writeBytes("bgepVec" + "\t");
				raf.writeBytes("strandVec" + "\t");
				raf.writeBytes("geneNameVec" + "\t");
				raf.writeBytes("egIndexVec" + "\t");
				raf.writeBytes("funCatVec" + "\t");
				raf.writeBytes("mgspVec" + "\t");
				raf.writeBytes("mgepVec" + "\t");
				raf.writeBytes("mcspVec" + "\t");
				raf.writeBytes("mcepVec" + "\t");
				raf.writeBytes("\n");
// test
//				int c=1;
//				raf.writeBytes(bgspVec.elementAt(c).intValue() + "\t");
				System.out.println(bgspVec.size());
				

 				int a;
 				for ( a =0; a < bgspVec.size(); a++){
					raf.writeBytes(a+1 + "\t");
					raf.writeBytes(bgspVec.elementAt(a).intValue()+ "\t");
					raf.writeBytes(bgepVec.elementAt(a).intValue()+ "\t");
					raf.writeBytes(strandVec.elementAt(a).intValue() + "\t");
					raf.writeBytes(geneNameVec.elementAt(a).toString() + "\t");
					raf.writeBytes(egIndexVec.elementAt(a).intValue() + "\t");
					raf.writeBytes(funCatVec.elementAt(a).toString() + "\t");
					raf.writeBytes(mgspVec.elementAt(a).intValue() + "\t");
					raf.writeBytes(mgepVec.elementAt(a).intValue() + "\t");
					raf.writeBytes(mcspVec.elementAt(a).intValue() + "\t");
					raf.writeBytes(mcepVec.elementAt(a).intValue() + "\t");
					raf.writeBytes("\n");
									
					System.out.println(a);
				}
				
				raf.close();		
				

			} catch (IOException e ) {
			 }
		}		
		

	public static void main(String args[]){
	
	    String orgName = "Mycoplasma pulmonis UAB CTIP";
		new SearchBg(orgName);
			

		
	}
}

