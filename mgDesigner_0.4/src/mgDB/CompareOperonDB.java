package mgDB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class CompareOperonDB {

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
	
	public Vector<Integer> diffVec1 = new Vector<Integer>();
	public Vector<Integer> diffVec2 = new Vector<Integer>();
	public Vector<Integer> diffVec3 = new Vector<Integer>();
	public Vector<Integer> diffVec4 = new Vector<Integer>();
	public Vector<Integer> diffVec5 = new Vector<Integer>();
	public Vector<Integer> diffVec6 = new Vector<Integer>();
	
	
	void selectDifferentRow() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
/*			
			query = "select OperonID, TuID, OperonSoft.bgSP, OperonSoft.bgEP, operon.bgSP, operon.bgEP" +
					" into outfile 'D://eclipse//workspace//mgDesigner_0.4//compareOperonDB3.txt'" + 
					" from OperonSoft left outer join Operon on OperonSoft.bgSP=operon.bgSP";
			
			stmt.executeQuery(query);
			
			query = "select OperonID, TuID, OperonSoft.bgSP, OperonSoft.bgEP, operon.bgSP, operon.bgEP" +
					" into outfile 'D://eclipse//workspace//mgDesigner_0.4//compareOperonDB4.txt'" + 
					" from OperonSoft right outer join Operon on OperonSoft.bgSP=operon.bgSP";
				
			stmt.executeQuery(query);
*/			
			query = "select OperonID, bgSP, bgEP from Operon";
			
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				operonIDVec.add(Integer.parseInt(rs.getString("operonid")));
				opSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				opEPVec.add(Integer.parseInt(rs.getString("bgep")));

				
//				strandVec.add(rs.getString("strand"));
//				locusTagVec.add(rs.getString("locus_tag"));
//				funCatVec.add(rs.getString("funcat"));                 
//				orgVec.add(rs.getString("organism")); 
			
//					geneidVec.add(Integer.parseInt(rs.getString("geneid")));
//					giVec.add(Integer.parseInt(rs.getString("gi")));
//					egIndexVec.add(Integer.parseInt(rs.getString("egIndex")));

			}
			rs.close();
			
			query = "select isOperon, bgSP, bgEP from OperonSoft";
			rs = stmt.executeQuery(query);
			while(rs.next()){		
				isOperonVec.add(rs.getString("isOperon"));
				tuSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				tuEPVec.add(Integer.parseInt(rs.getString("bgep")));
			}
			
			rs.close();

			query = "select egIndex, bgSP, bgEP from bGenome";
			rs = stmt.executeQuery(query);
			while(rs.next()){
				
				int k = Integer.parseInt(rs.getString("egIndex"));
				if (k == +1) egIndexVec.add("+");
				else if (k == -1) egIndexVec.add("-");
				bgSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				bgEPVec.add(Integer.parseInt(rs.getString("bgep")));
			}
			
			rs.close();
			stmt.close();
			con.close();
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}
	
	void compareOperon(){
		
		boolean found = false;
		for (int i = 0; i < opSPVec.size(); i++){
			for (int j = 0; j < tuSPVec.size(); j++){
				if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(j).intValue()) && 
						(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(j).intValue())) {
					found = true;
					break;
				}else if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(j).intValue()) || 
						(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(j).intValue())) {
					diffVec1.add(i);
					diffVec2.add(j);
					found = true;
					break;
				}
			}
			if (!found)	opIndexVec.add(i);
			found = false;
		}
					
		found = false;
		for (int i = 0; i < tuSPVec.size(); i++){
			for (int j = 0; j < opSPVec.size(); j++){
				if ((opSPVec.elementAt(j).intValue() == tuSPVec.elementAt(i).intValue()) && 
						(opEPVec.elementAt(j).intValue() == tuEPVec.elementAt(i).intValue())) {
					found = true;
					break;
				}
			}
			if (!found)	tuIndexVec.add(i);
			found = false;
		}
	}
	
	void makeText() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("compareOperon.txt", "rw");

			raf.seek(raf.length()); 
//			raf.writeBytes("orgName= " + orgName);
//			raf.writeBytes("\n" + "\n");
			raf.writeBytes("ID" + "\t");
			raf.writeBytes("opID" + "\t");
			raf.writeBytes("opSP" + "\t");
			raf.writeBytes("opEP" + "\t");
//			raf.writeBytes("strandVec" + "\t");
//			raf.writeBytes("geneNameVec" + "\t");
//			raf.writeBytes("egIndexVec" + "\t");
//			raf.writeBytes("funCatVec" + "\t");
//			raf.writeBytes("mgspVec" + "\t");
			raf.writeBytes("tuID" + "\t");
			raf.writeBytes("tuSP" + "\t");
			raf.writeBytes("tuEP" + "\t");
			raf.writeBytes("\n");

			int a, i, j;
			for (a =0; a < diffVec1.size(); a++){
				raf.writeBytes(a+1 + "\t");
				i = diffVec1.get(a);
				j = diffVec2.get(a);
				raf.writeBytes(operonIDVec.elementAt(i).intValue()+ "\t");
				raf.writeBytes(opSPVec.elementAt(i).intValue()+ "\t");
				raf.writeBytes(opEPVec.elementAt(i).intValue()+ "\t");
//				raf.writeBytes(strandVec.elementAt(a).intValue() + "\t");
//				raf.writeBytes(geneNameVec.elementAt(a).toString() + "\t");
//				raf.writeBytes(egIndexVec.elementAt(a).intValue() + "\t");
//				raf.writeBytes(funCatVec.elementAt(a).toString() + "\t");
//				raf.writeBytes(mgspVec.elementAt(a).intValue() + "\t");
//				raf.writeBytes(mgepVec.elementAt(a).intValue() + "\t");
				raf.writeBytes(tuIDVec.elementAt(j).intValue()+ "\t");
				raf.writeBytes(tuSPVec.elementAt(j).intValue() + "\t");
				raf.writeBytes(tuEPVec.elementAt(j).intValue() + "\t");
				raf.writeBytes("\n");
			}
			a++;
			for (i =0; i < opIndexVec.size(); i++){
				raf.writeBytes(a++ + "\t");
				j = opIndexVec.elementAt(i).intValue();
				raf.writeBytes(operonIDVec.elementAt(j).intValue()+ "\t");
				raf.writeBytes(opSPVec.elementAt(j).intValue()+ "\t");
				raf.writeBytes(opEPVec.elementAt(j).intValue()+ "\t");
				raf.writeBytes("\n");
			}
			
			for (i =0; i < tuIndexVec.size(); i++){
				raf.writeBytes(a++ + "\t");
				j = tuIndexVec.elementAt(i).intValue();
				raf.writeBytes("\t" + "\t" + "\t");
				raf.writeBytes(tuIDVec.elementAt(j).intValue() + "\t");
				raf.writeBytes(tuSPVec.elementAt(j).intValue() + "\t");
				raf.writeBytes(tuEPVec.elementAt(j).intValue() + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
			

		} catch (IOException e ) {
	 }
	}
	
	void operonTobGenome(){
		
		boolean found = false;
		for (int i = 0; i < opSPVec.size(); i++){
			for (int j = 0; j < bgSPVec.size(); j++){
				if ((opSPVec.elementAt(i).intValue() == bgSPVec.elementAt(j).intValue()) && 
						(opEPVec.elementAt(i).intValue() == bgEPVec.elementAt(j).intValue())) {
					found = true;
					break;
				}else if ((opSPVec.elementAt(i).intValue() == bgSPVec.elementAt(j).intValue()) || 
						(opEPVec.elementAt(i).intValue() == bgEPVec.elementAt(j).intValue())) {
					diffVec3.add(i);
					diffVec4.add(j);
					boolean kFound = false;
					for (int k = 0; k < tuSPVec.size(); k++){
						if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(k).intValue()) || 
								(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(k).intValue()) ||
								(bgSPVec.elementAt(j).intValue() == tuSPVec.elementAt(k).intValue()) || 
								(bgEPVec.elementAt(j).intValue() == tuEPVec.elementAt(k).intValue())) {
							diffVec5.add(k);
							kFound = true;
						}
					}
					if (!kFound) diffVec5.add(-1);  //숫자를 맞추기 위해
					found = true;
					break;
				}
			}
			if (!found)	{
				opbgIndexVec.add(i);
				boolean kFound = false;
				for (int k = 0; k < tuSPVec.size(); k++){
					if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(k).intValue()) || 
							(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(k).intValue())) {
						diffVec6.add(k);
						kFound = true;
						break;
					}
				}
				if (!kFound) diffVec6.add(-1);  //숫자를 맞추기 위해
			}
			found = false;
		}
					
		found = false;
		for (int i = 0; i < bgSPVec.size(); i++){
			for (int j = 0; j < opSPVec.size(); j++){
				if ((opSPVec.elementAt(j).intValue() == bgSPVec.elementAt(i).intValue()) && 
						(opEPVec.elementAt(j).intValue() == bgEPVec.elementAt(i).intValue())) {
					found = true;
					break;
				}
			}
			if (!found)	bgopIndexVec.add(i);
			found = false;
		}
	}
	
	void opbgDiffText() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("opbgDiff.txt", "rw");

			raf.seek(raf.length()); 
//			raf.writeBytes("orgName= " + orgName);
//			raf.writeBytes("\n" + "\n");
			raf.writeBytes("ID" + "\t");
			raf.writeBytes("opID" + "\t");
			raf.writeBytes("opSP" + "\t");
			raf.writeBytes("opEP" + "\t");
//			raf.writeBytes("strandVec" + "\t");
//			raf.writeBytes("geneNameVec" + "\t");
//			raf.writeBytes("egIndexVec" + "\t");
//			raf.writeBytes("funCatVec" + "\t");
//			raf.writeBytes("mgspVec" + "\t");
			raf.writeBytes("isEG" + "\t");
			raf.writeBytes("bgSP" + "\t");
			raf.writeBytes("bgEP" + "\t");
			raf.writeBytes("isOperon" + "\t");
			raf.writeBytes("tuSP" + "\t");
			raf.writeBytes("tuEP" + "\t");
			raf.writeBytes("\n");

			int a, i, j, k;
			for (a =0; a < diffVec3.size(); a++){
				raf.writeBytes(a+1 + "\t");
				i = diffVec3.get(a);
				j = diffVec4.get(a);
				k = diffVec5.get(a);
				raf.writeBytes(operonIDVec.elementAt(i).intValue()+ "\t");
				raf.writeBytes(opSPVec.elementAt(i).intValue()+ "\t");
				raf.writeBytes(opEPVec.elementAt(i).intValue()+ "\t");
//				raf.writeBytes(strandVec.elementAt(a).intValue() + "\t");
//				raf.writeBytes(geneNameVec.elementAt(a).toString() + "\t");
//				raf.writeBytes(funCatVec.elementAt(a).toString() + "\t");
//				raf.writeBytes(mgspVec.elementAt(a).intValue() + "\t");
//				raf.writeBytes(mgepVec.elementAt(a).intValue() + "\t");
				raf.writeBytes(egIndexVec.elementAt(j).toString() + "\t");
				raf.writeBytes(bgSPVec.elementAt(j).intValue() + "\t");
				raf.writeBytes(bgEPVec.elementAt(j).intValue() + "\t");
				if (k != -1) {
					raf.writeBytes(isOperonVec.elementAt(k).toString()+ "\t");
					raf.writeBytes(tuSPVec.elementAt(k).intValue() + "\t");
					raf.writeBytes(tuEPVec.elementAt(k).intValue() + "\t");
				}else{
					raf.writeBytes("\t" + "\t" + "\t");
				}
				raf.writeBytes("\n");
			}
			a++;
			for (i =0; i < opbgIndexVec.size(); i++){
				raf.writeBytes(a++ + "\t");
				j = opbgIndexVec.elementAt(i).intValue();
				raf.writeBytes(operonIDVec.get(j) + "\t");
				raf.writeBytes(opSPVec.get(j) + "\t");
				raf.writeBytes(opEPVec.elementAt(j).intValue()+ "\t");
				raf.writeBytes("\t" + "\t" + "\t");
				k = diffVec6.elementAt(i).intValue();
				if (k != -1) {
					raf.writeBytes(isOperonVec.elementAt(k).toString()+ "\t");
					raf.writeBytes(tuSPVec.elementAt(k).intValue() + "\t");
					raf.writeBytes(tuEPVec.elementAt(k).intValue() + "\t");
				}else{
					raf.writeBytes("\t" + "\t" + "\t");
				}
				raf.writeBytes("\n");
			}
			
			raf.close();		
			

		} catch (IOException e ) {
	 }
	}
/*	
	void regulonTobGenome(){
		
		boolean found = false;
		for (int i = 0; i < opSPVec.size(); i++){
			for (int j = 0; j < bgSPVec.size(); j++){
				if ((opSPVec.elementAt(i).intValue() == bgSPVec.elementAt(j).intValue()) && 
						(opEPVec.elementAt(i).intValue() == bgEPVec.elementAt(j).intValue())) {
					found = true;
					break;
				}else if ((opSPVec.elementAt(i).intValue() == bgSPVec.elementAt(j).intValue()) || 
						(opEPVec.elementAt(i).intValue() == bgEPVec.elementAt(j).intValue())) {
					diffVec3.add(i);
					diffVec4.add(j);
					boolean kFound = false;
					for (int k = 0; k < tuSPVec.size(); k++){
						if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(k).intValue()) || 
								(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(k).intValue()) ||
								(bgSPVec.elementAt(j).intValue() == tuSPVec.elementAt(k).intValue()) || 
								(bgEPVec.elementAt(j).intValue() == tuEPVec.elementAt(k).intValue())) {
							diffVec5.add(k);
							kFound = true;
						}
					}
					if (!kFound) diffVec5.add(-1);  //숫자를 맞추기 위해
					found = true;
					break;
				}
			}
			if (!found)	{
				opbgIndexVec.add(i);
				boolean kFound = false;
				for (int k = 0; k < tuSPVec.size(); k++){
					if ((opSPVec.elementAt(i).intValue() == tuSPVec.elementAt(k).intValue()) || 
							(opEPVec.elementAt(i).intValue() == tuEPVec.elementAt(k).intValue())) {
						diffVec6.add(k);
						kFound = true;
						break;
					}
				}
				if (!kFound) diffVec6.add(-1);  //숫자를 맞추기 위해
			}
			found = false;
		}
					
		found = false;
		for (int i = 0; i < bgSPVec.size(); i++){
			for (int j = 0; j < opSPVec.size(); j++){
				if ((opSPVec.elementAt(j).intValue() == bgSPVec.elementAt(i).intValue()) && 
						(opEPVec.elementAt(j).intValue() == bgEPVec.elementAt(i).intValue())) {
					found = true;
					break;
				}
			}
			if (!found)	bgopIndexVec.add(i);
			found = false;
		}
	}
*/	
	public static void main(String args[]){
		CompareOperonDB cd = new CompareOperonDB();	
		cd.selectDifferentRow();
//		cd.compareOperon();
//		cd.makeText();
		cd.operonTobGenome();
		cd.opbgDiffText();
	}
}