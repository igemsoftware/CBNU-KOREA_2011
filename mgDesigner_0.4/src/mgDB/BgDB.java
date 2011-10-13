package mgDB;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Vector;

import mgFile.MgFileCreator;

public class BgDB {
	
	Vector<Integer> bgSPVec = new Vector<Integer>();
	Vector<Integer> bgEPVec = new Vector<Integer>();
	Vector<String> bgStrandVec = new Vector<String>();
	Vector<String> geneNameVec = new Vector<String>();
	Vector<String> geneTypeVec = new Vector<String>();
	Vector<String> isPseudoVec = new Vector<String>();
	Vector<String> isEGVec = new Vector<String>();
	Vector<String> funCatVec = new Vector<String>();
	Vector<String> locusTagVec = new Vector<String>();
	Vector<String> cogNumVec = new Vector<String>();
	Vector<String> koNumVec = new Vector<String>();
	Vector<String> goFunNumVec = new Vector<String>();
	Vector<String> goProcNumVec = new Vector<String>();
	Vector<String> goCompNumVec = new Vector<String>();
//	Vector<String> giVec = new Vector<String>();
	
	Vector<Integer> cdsIndexVec = new Vector<Integer>();
	Vector<Integer> rRNAIndexVec = new Vector<Integer>();
	Vector<Integer> tRNAIndexVec = new Vector<Integer>();
	Vector<Integer> ncRNAIndexVec = new Vector<Integer>();
	Vector<Integer> tmRNAIndexVec = new Vector<Integer>();
	Vector<Integer> miscRNAIndexVec = new Vector<Integer>();
	Vector<Integer> pseudoIndexVec = new Vector<Integer>();
	Vector<Integer> egIndexVec = new Vector<Integer>();
	
	String sFile;
	String locus;
	String orgName;
	String isCircle;
	int seqLen;
	
	void bgDB() {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = conn.createStatement(); 
		
			String tableName ="bGenome";
			
			String bgIDField = "bgID";
//			String GIField = "GI";			
			String bgSPField = "bgSP";
			String bgEPField = "bgEP";
			String bgStrandField = "bgStrand";
			String geneNameField = "geneName";
			String locusTagField = "locus_tag";
			String geneTypeField = "geneType";
			String isPseudoField = "pseudoGene";
			String isEGField = "EG";
			String funCatField = "funCat";
			String cogNumField = "COG_Num";
			String koNumField = "KO_Num";
			String goFunNumField = "GO_FunNum";
			String goProcNumField = "GO_ProcNum";
			String goCompNumField = "GO_CompNum";
			String organismField = "organism";
			String locusField = "locus";
			String seqLenField = "seqLen";
			String isCircleField = "circular";

			String dropTableQuery = "drop table if exists " + tableName;
			stmt.executeUpdate(dropTableQuery);
			
			String createTableQuery = "create table " + tableName + " (" + 
//				bgIDField + " int, " + GIField + " varchar(20), " + bgSPField + " int, " + bgEPField + " int, " + bgStrandField + " char(1), " +
				bgIDField + " int, " + bgSPField + " int, " + bgEPField + " int, " + bgStrandField + " char(1), " +
				geneNameField + " varchar(30), " + locusTagField + " varchar(30), " + geneTypeField + " varchar(8), " + 
				isPseudoField + " char(1), " + isEGField + " char(1), " + funCatField + " varchar(3), " + cogNumField + " varchar(30), " + 
				koNumField + " varchar(30), " + goFunNumField + " varchar(30), " + goProcNumField + " varchar(30), " +
				goCompNumField + " varchar(30), " + locusField + " varchar(15), " + seqLenField + " int, " +
				isCircleField + " varchar(8), " + organismField + " varchar(80))";

			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName +
//					" (bgID, GI, bgSP, bgEP, bgStrand, geneName, locus_tag, geneType, pseudogene," +
					" (bgID, bgSP, bgEP, bgStrand, geneName, locus_tag, geneType, pseudogene," +
					" EG, funCat, COG_Num, KO_Num, GO_FunNum, GO_ProcNum, GO_CompNum," +
					" locus, seqLen, circular, organism)" +
					" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//					" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
		
			File f = new File(".\\data\\egGenome\\");
			File[] files = f.listFiles();
			for (int k = 0; k < files.length; k++){
				System.out.println(k + 1 + ", ");       //반복 현항을 파악하기 위헤!!!!!!!!
				if (files[k].getName().startsWith("NC_")) {                  //NC_.......gbk !!!!
					String sFile = files[k].getPath();
					//String sFile = ".\\data\\egGenome\\NC_000913.gbk";   //BgOneDB에서 수정해야 할 부분
					//fileName = ecoGenome                                //BgOneDB에서 수정해야 할 부분
					getInfo(sFile);
					System.out.println("geneTypeVec= " + geneTypeVec.size());

					for(int i = 0 ; i < bgSPVec.size() ; i++){
						pstmt.setInt(1, i + 1);
						pstmt.setInt(2, bgSPVec.elementAt(i).intValue());
						pstmt.setInt(3, bgEPVec.elementAt(i).intValue());
						pstmt.setString(4, bgStrandVec.elementAt(i).toString());
						pstmt.setString(5, geneNameVec.elementAt(i).toString());
						pstmt.setString(6, locusTagVec.elementAt(i).toString());
						pstmt.setString(7, geneTypeVec.elementAt(i).toString());
						pstmt.setString(8, isPseudoVec.elementAt(i).toString());
						pstmt.setString(9, isEGVec.elementAt(i).toString());
						pstmt.setString(10, funCatVec.elementAt(i).toString());
						pstmt.setString(11, cogNumVec.elementAt(i).toString());
						pstmt.setString(12, koNumVec.elementAt(i).toString());
						pstmt.setString(13, goFunNumVec.elementAt(i).toString());
						pstmt.setString(14, goProcNumVec.elementAt(i).toString());
						pstmt.setString(15, goCompNumVec.elementAt(i).toString());
						pstmt.setString(16, locus);
						pstmt.setInt(17, seqLen);
						pstmt.setString(18, isCircle);
						pstmt.setString(19, orgName);
//						pstmt.setString(20, giVec.elementAt(i).toString());
						
						pstmt.executeUpdate();
					}
				}
				printText();
			}
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
			if(conn != null){
				try{
					conn.close();
				}catch(SQLException e){}
			}		
		}	
	}
	
	public void getInfo(String sFile){
		
//		String sFile = ".\\data\\egGenome\\NC_000913.gbk";
		MgFileCreator mgFileCreator = new MgFileCreator(sFile);
		
		bgSPVec = mgFileCreator.getgSP();
		bgEPVec = mgFileCreator.getgEP();
		bgStrandVec = mgFileCreator.getStrandVec();
		geneNameVec = mgFileCreator.getGeneNameVec();
		locusTagVec = mgFileCreator.getLocusTagVec();
//		giVec = mgFileCreator.getgiVec();
		funCatVec = mgFileCreator.getEgFunCatVec();
		egIndexVec = mgFileCreator.getEgIndexVec();
		cdsIndexVec = mgFileCreator.getCDSIndexVec();
		tRNAIndexVec = mgFileCreator.gettRNAIndexVec();
		rRNAIndexVec = mgFileCreator.getrRNAIndexVec();
		tmRNAIndexVec = mgFileCreator.gettmRNAIndexVec();
		ncRNAIndexVec = mgFileCreator.getncRNAIndexVec();
		miscRNAIndexVec = mgFileCreator.getmiscRNAIndexVec();
		pseudoIndexVec = mgFileCreator.getPseudoIndexVec();	
		orgName =  mgFileCreator.getOrgName();
		System.out.println("cdsIndexVec= " + cdsIndexVec.size());
		
		locus = mgFileCreator.getLocusName();
		seqLen = mgFileCreator.getSeqLen();
		isCircle = mgFileCreator.getCircular();
		
		
		System.out.println("cdsIndexVec: " + cdsIndexVec.size() + ", " + "tRNAIndexVec: " + tRNAIndexVec.size() +
					", " + "rRNAIndexVec: " + rRNAIndexVec.size() + ", " + "ncRNAIndexVec: " + ncRNAIndexVec.size() +
					", " + "tmRNAIndexVec: " + tmRNAIndexVec.size() + ", " + "miscRNAIndexVec: " + miscRNAIndexVec.size());
			
		for (int i = 0; i < bgSPVec.size(); i++){        // making geneTypeVec
			boolean found =false;
			for (int j = 0; j < cdsIndexVec.size(); j++){
				if (i == cdsIndexVec.elementAt(j).intValue()) {
					found =true;
					geneTypeVec.add("CDS");
					break;
					}
				}
		
			for (int j = 0; j < tRNAIndexVec.size(); j++){		
				if (i == tRNAIndexVec.elementAt(j).intValue()){
					found =true;geneTypeVec.add("tRNA");break;}	}
		
			for (int j = 0; j < rRNAIndexVec.size(); j++){		
				if (i == rRNAIndexVec.elementAt(j).intValue()){		
					found =true;geneTypeVec.add("rRNA");break;} }
		
			for (int j = 0; j < ncRNAIndexVec.size(); j++){		
				if (i == ncRNAIndexVec.elementAt(j).intValue()){	
					found =true;geneTypeVec.add("ncRNA");break;} }
			
			for (int j = 0; j < tmRNAIndexVec.size(); j++){		
				if (i == tmRNAIndexVec.elementAt(j).intValue()){	
					found =true;geneTypeVec.add("tmRNA");break;} }
			
			for (int j = 0; j < miscRNAIndexVec.size(); j++){		
				if (i == miscRNAIndexVec.elementAt(j).intValue()){	
					found =true;geneTypeVec.add("miscRNA");break;} }
	
			if (!found) geneTypeVec.add("ND");
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making isEGVec,& funCatVec - 새로 만들 것!
			boolean found =false;
			for (int j = 0; j < egIndexVec.size(); j++){
				if (i == egIndexVec.elementAt(j).intValue()) {
					isEGVec.add("+");
					found = true;
					break;
				}
			}
			if (!found){
				isEGVec.add("-");
				funCatVec.add(i, "X");
			}
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making isPseudoVec
			boolean found =false;
			for (int j = 0; j < pseudoIndexVec.size(); j++){
				if (i == pseudoIndexVec.elementAt(j).intValue()) {
					isPseudoVec.add("+");
					found = true;
					break;
				}
			}
			if (!found) isPseudoVec.add("-");
		}
		
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making cogNumVec
			cogNumVec.add("");
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making koNumVec
			koNumVec.add("");
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making goFunNumVec
			goFunNumVec.add("");
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making goProcNumVec
			goProcNumVec.add("");
		}
		
		for (int i = 0; i < bgSPVec.size(); i++){        // making goCompNumVec
			goCompNumVec.add("");
		}
	}

	void printText() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("bGenome.txt", "rw");

			raf.seek(raf.length()); 

 			raf.writeBytes("bgID" + "\t");
// 			raf.writeBytes("gi" + "\t");
			raf.writeBytes("bgSP" + "\t");
			raf.writeBytes("bgEP" + "\t");
			raf.writeBytes("bgStrand" + "\t");
			raf.writeBytes("geneName" + "\t");
			raf.writeBytes("locus_tag" + "\t");
			raf.writeBytes("geneType" + "\t");
			raf.writeBytes("pseudoGene" + "\t");
			raf.writeBytes("EG" + "\t");
			raf.writeBytes("funCat" + "\t");
			raf.writeBytes("COG_Num" + "\t");
			raf.writeBytes("KO_Num" + "\t");
			raf.writeBytes("GO_FunNum" + "\t");
			raf.writeBytes("GO_ProcNum" + "\t");
			raf.writeBytes("GO_CompNum" + "\t");
			raf.writeBytes("locus" + "\t");
			raf.writeBytes("seqLen" + "\t");
			raf.writeBytes("circular" + "\t");
			raf.writeBytes("organism" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < bgSPVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
//				raf.writeBytes(giVec.elementAt(i).toString() + "\t");
				raf.writeBytes(bgSPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(bgEPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(bgStrandVec.elementAt(i).toString() + "\t");
				raf.writeBytes(geneNameVec.elementAt(i).toString()+ "\t");
				raf.writeBytes(locusTagVec.elementAt(i).toString()+ "\t");
				raf.writeBytes(geneTypeVec.elementAt(i).toString()+ "\t");
				raf.writeBytes(isPseudoVec.elementAt(i).toString() + "\t");
				raf.writeBytes(isEGVec.elementAt(i).toString() + "\t");
				raf.writeBytes(funCatVec.elementAt(i).toString() + "\t");
				raf.writeBytes(cogNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes(koNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes(goFunNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes(goProcNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes(goCompNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes(locus + "\t");
				raf.writeBytes(seqLen + "\t");
				raf.writeBytes(isCircle + "\t");
				raf.writeBytes(orgName + "\t");

				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
			
		}
	 }
	
	public static void main(String[] args) {
		
		BgDB bgDB = new BgDB();
		bgDB.bgDB();
	}
}