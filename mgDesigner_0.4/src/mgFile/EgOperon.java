package mgFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class EgOperon {
	
	public Vector<Integer> bgSPVec = new Vector<Integer>();
	public Vector<Integer> bgEPVec = new Vector<Integer>();
	public Vector<Integer> operonIDVec = new Vector<Integer>();
	public Vector<Integer> egOPIDVec = new Vector<Integer>();
	public Vector<Integer> egOPUniqueVec = new Vector<Integer>();
	public Vector<Integer> opUniqueIDVec = new Vector<Integer>();
	
	public Vector<Integer> opSPVec = new Vector<Integer>();
	public Vector<Integer> opEPVec = new Vector<Integer>();
	public Vector<String> strandVec = new Vector<String>();
	public Vector<String> productVec = new Vector<String>();
	public Vector<String> opStrandVec = new Vector<String>();
	public Vector<String> locusTagVec = new Vector<String>();
	public Vector<String> opLocusTagVec = new Vector<String>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<String> cogNumVec = new Vector<String>();	
	public Vector<String> orgVec = new Vector<String>();
	
	public Vector<Integer> geneCountVec = new Vector<Integer>();
	public Vector<Integer> egCountVec = new Vector<Integer>();
	public Vector<Integer> egPosVec = new Vector<Integer>();
	public Vector<Integer> tuIndexVec = new Vector<Integer>();
	public Vector<Integer> opbgIndexVec = new Vector<Integer>();
	public Vector<Integer> bgopIndexVec = new Vector<Integer>();
	public Vector<Integer> opegIndexVec = new Vector<Integer>();
	public Vector<Integer> egopIndexVec = new Vector<Integer>();

	public void getData() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from egbrick";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				egOPIDVec.add(Integer.parseInt(rs.getString("operonID"))); 
				bgSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				bgEPVec.add(Integer.parseInt(rs.getString("bgep")));
				strandVec.add(rs.getString("strand"));
				locusTagVec.add(rs.getString("locus_tag"));
				funCatVec.add(rs.getString("funcat"));                 
				orgVec.add(rs.getString("organism")); 
			}
			rs.close();
			
			query = "select * from Operon";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				operonIDVec.add(Integer.parseInt(rs.getString("operonid")));
				opSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				opEPVec.add(Integer.parseInt(rs.getString("bgep")));
				opStrandVec.add(rs.getString("Strand"));
				opLocusTagVec.add(rs.getString("locus_tag"));
				cogNumVec.add(rs.getString("COG_number"));                 
				productVec.add(rs.getString("Product")); 
			}
			rs.close();
			stmt.close();
			con.close();

			System.out.println("bgSPVec.size() = " + bgSPVec.size());
			System.out.println("operonIDVec.size() = " + operonIDVec.size());
			 
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

	public void genePerOperon(){
		
		opUniqueIDVec.add(operonIDVec.get(0));
		int geneCount = 1;
		for (int i = 1; i < operonIDVec.size(); i++){
			if (operonIDVec.elementAt(i-1).intValue() == operonIDVec.elementAt(i).intValue()) {
				geneCount++;
			}else{
				opUniqueIDVec.add(operonIDVec.get(i));
				geneCountVec.add(geneCount);
				geneCount = 1;
			}
			if (i == (operonIDVec.size()-1))
				geneCountVec.add(geneCount+1);
		}

		try {
			RandomAccessFile raf = new RandomAccessFile("genePerOperon.txt", "rw");

			raf.seek(raf.length());
			raf.writeBytes("Num" + "\t");
			raf.writeBytes("opID" + "\t");
			raf.writeBytes("gNum" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < opUniqueIDVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
				raf.writeBytes(opUniqueIDVec.get(i) + "\t");
				raf.writeBytes(geneCountVec.get(i) + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
	 	}
	}
	
	public void egPerOperon(){

		egOPUniqueVec.add(egOPIDVec.get(0));
		int geneCount = 1;
		for (int i = 1; i < egOPIDVec.size(); i++){
			if (egOPIDVec.elementAt(i-1).intValue() == egOPIDVec.elementAt(i).intValue()) {
				geneCount++;
			}else{
				egOPUniqueVec.add(egOPIDVec.get(i));
				egCountVec.add(geneCount);
				geneCount = 1;
			}
			if (i == (egOPIDVec.size()-1))
				egCountVec.add(geneCount+1);
		}

		try {
			RandomAccessFile raf = new RandomAccessFile("egPerOperon.txt", "rw");

			raf.seek(raf.length());
			raf.writeBytes("Num" + "\t");
			raf.writeBytes("opID" + "\t");
			raf.writeBytes("gNum" + "\t");
			raf.writeBytes("egNum" + "\t");
			raf.writeBytes("\n");
			
			for (int i =0; i < egOPUniqueVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
				raf.writeBytes(egOPUniqueVec.get(i) + "\t");
				
				for (int j = 0; j < opUniqueIDVec.size(); j++){
					if (egOPUniqueVec.elementAt(i).intValue() == opUniqueIDVec.elementAt(j).intValue()) {
						raf.writeBytes(geneCountVec.get(j) + "\t");
						break;
					}
				}

				raf.writeBytes(egCountVec.get(i) + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
		}	
	}
	
	public void egPosInOperon(){
		
		int geneCount = 0;
		for (int i = 0; i < egOPIDVec.size(); i++){
			for (int j = 0; j < operonIDVec.size(); j++){
				if (egOPIDVec.elementAt(i).intValue() == operonIDVec.elementAt(j).intValue()) {
					geneCount++;
					if ((bgSPVec.elementAt(i).intValue() == opSPVec.elementAt(j).intValue()) ||
							(bgEPVec.elementAt(i).intValue() == opEPVec.elementAt(j).intValue())){
						egPosVec.add(geneCount);
					}
				}
				if (egOPIDVec.elementAt(i).intValue() < operonIDVec.elementAt(j).intValue()) {	
					break;
				}
			}
			geneCount = 0;
		}
		System.out.println("egPosVec= " + egPosVec.size());
	}

	public void egOperonInfo(){
		
		try {
			RandomAccessFile raf = new RandomAccessFile("egOperonInfo.txt", "rw");
	
			raf.seek(raf.length());
			raf.writeBytes("egBrickID" + "\t");
			raf.writeBytes("operonID" + "\t");
			raf.writeBytes("gNum" + "\t");
			raf.writeBytes("egNum" + "\t");
			raf.writeBytes("egPos" + "\t");
			raf.writeBytes("\n");
			
			for (int i =0; i < egOPIDVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
				raf.writeBytes(egOPIDVec.get(i) + "\t");
				
				for (int j = 0; j < opUniqueIDVec.size(); j++){
					if (egOPIDVec.elementAt(i).intValue() == opUniqueIDVec.elementAt(j).intValue()) {
						raf.writeBytes(geneCountVec.get(j) + "\t");
						break;
					}
				}
				for (int j = 0; j < egOPUniqueVec.size(); j++){
					if (egOPIDVec.elementAt(i).intValue() == egOPUniqueVec.elementAt(j).intValue()) {
						raf.writeBytes(egCountVec.get(j) + "\t");
						break;
					}
				}
				raf.writeBytes(egPosVec.get(i) + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
		}
	}
/*	
	void egOperonInfo(){
		
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
/*	
	void egBrickText() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("opbgDiff.txt", "rw");

			raf.seek(raf.length()); 
//			raf.writeBytes("orgName= " + orgName);
//			raf.writeBytes("\n" + "\n");
 * 			raf.writeBytes("Num" + "\t");
			raf.writeBytes("opID" + "\t");
			raf.writeBytes("gNum" + "\t");
			raf.writeBytes("egNum" + "\t");
			raf.writeBytes("egPos" + "\t");
			raf.writeBytes("strand" + "\t");
			raf.writeBytes("strand" + "\t");
			raf.writeBytes("strand" + "\t");
 * 
 * 
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
*/
	public static void main(String args[]){
		EgOperon eb = new EgOperon();	
		eb.getData();
		eb.genePerOperon();
		eb.egPerOperon();
		eb.egPosInOperon();
		eb.egOperonInfo();
//		eb.egBrickID();
//		eb.egBrickText();
	}

}
