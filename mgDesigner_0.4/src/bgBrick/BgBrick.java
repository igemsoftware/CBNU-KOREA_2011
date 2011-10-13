/* 1-Jan-2011 김영창
 * 미생물 genome gb 파일과 필수 유전자 db를 이용하여 minimal genome을 만들고 gb 파일을 생성.
 */

package bgBrick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;
import org.biojavax.bio.seq.RichSequence;

public class BgBrick {                     // Making egBricks is easy!

	 RichSequence richSeq;
	 Vector<SymbolList> egBrickSym = new Vector<SymbolList>();
	 Vector<Integer> bgSPVec = new Vector<Integer>();       //genome 상의 모든 유전자 시작 위치
	 Vector<Integer> bgEPVec = new Vector<Integer>();
	 Vector<Integer> opSPVec = new Vector<Integer>();
	 Vector<Integer> opEPVec = new Vector<Integer>();
	 Vector<Integer> cdsSPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 시작 위치
	 Vector<Integer> cdsEPVec = new Vector<Integer>();
	 Vector<Integer> rRNASPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 시작 위치
	 Vector<Integer> rRNAEPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 끝 위치
	 Vector<Integer> tRNASPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 시작 위치
	 Vector<Integer> tRNAEPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 끝 위치
	 Vector<Integer> ncRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> ncRNAEPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 끝 위치
	 Vector<Integer> tmRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> tmRNAEPVec = new Vector<Integer>();  
	 Vector<Integer> miscRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> miscRNAEPVec = new Vector<Integer>(); 
	 
	 Vector<Integer> rRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> tRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> ncRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> tmRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> miscRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> pseudoIndexVec = new Vector<Integer>();

	 Vector<Integer> rrSPVec = new Vector<Integer>();       //genome 상의repeat_region 위치
	 Vector<Integer> rrEPVec = new Vector<Integer>(); 
	 public Vector<Integer> mpSPVec = new Vector<Integer>();       //genome 상의 mat_peptide 위치
	 public Vector<Integer> mpEPVec = new Vector<Integer>();  
	 Vector<Integer> meSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> meEPVec = new Vector<Integer>();  
	 Vector<Integer> rbsSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> rbsEPVec = new Vector<Integer>();
	 Vector<Integer> pribnowSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> pribnowEPVec = new Vector<Integer>();
	 Vector<Integer> recognitionSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> recognitionEPVec = new Vector<Integer>();
	 Vector<String> opLocusTagVec = new Vector<String>();
	 Vector<String> locusTagVec = new Vector<String>();

	Vector<Integer> brickLenVec = new Vector<Integer>();
	Vector<Integer> brickSPVec = new Vector<Integer>();
	Vector<Integer> brickEPVec = new Vector<Integer>();
	Vector<Integer> brickIDVec = new Vector<Integer>();
	Vector<String> brickSeqVec = new Vector<String>();

	public Vector<String> isEGVec = new Vector<String>();
	public Vector<String> isPseudoVec = new Vector<String>();
	public Vector<String> bgBrickTypeVec = new Vector<String>(); //CR, ICR
	public Vector<String> brickTypeVec = new Vector<String>(); //CDS, tRNA, rRNA, ncRNA, tmRNA, miscRNA, RBS, TP, PP, TT, PT
	public Vector<String> geneTypeVec = new Vector<String>();
	public Vector<String> spaceTypeVec = new Vector<String>();
	public Vector<String> strandVec = new Vector<String>();
	public Vector<String> funCatVec = new Vector<String>();
	public Vector<String> cogNumVec = new Vector<String>();
	public Vector<String> koNumVec = new Vector<String>();
	public Vector<String> goFunNumVec = new Vector<String>();
	public Vector<String> goProcNumVec = new Vector<String>();
	public Vector<String> goCompNumVec = new Vector<String>();
	public Vector<String> bgcogNumVec = new Vector<String>();
	public Vector<Integer> operonIDVec = new Vector<Integer>();
	public Vector<Integer> bgopIDVec = new Vector<Integer>();
	public Vector<String> geneNameVec = new Vector<String>();

	int seqLen;
	int brickID;
	String locus;
	static String nLoc;
	String sFile;
	String sSeq;
	String locusTag;
	String orgName;
	String isCircle;

	public BgBrick(String sFile){
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(sFile)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		locus = richSeq.getName();
		seqLen = richSeq.length();  
		sSeq = richSeq.seqString();
	}

	void getData() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "bgenome";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName + " where locus = '" + locus + "'";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				bgSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				bgEPVec.add(Integer.parseInt(rs.getString("bgep")));
				geneNameVec.add(rs.getString("geneName"));
				locusTagVec.add(rs.getString("locus_tag"));
				geneTypeVec.add(rs.getString("geneType"));
				strandVec.add(rs.getString("bgstrand"));
				isEGVec.add(rs.getString("EG"));
				isPseudoVec.add(rs.getString("pseudoGene"));
				funCatVec.add(rs.getString("funcat")); 
				cogNumVec.add(rs.getString("COG_Num"));
				koNumVec.add(rs.getString("KO_Num"));
				goFunNumVec.add(rs.getString("GO_FunNum"));
				goProcNumVec.add(rs.getString("GO_ProcNum"));
				goCompNumVec.add(rs.getString("GO_CompNum"));
				isCircle = rs.getString("circular"); 
				orgName = rs.getString("organism"); 
			}
			rs.close();
			
			query = "select * from operon";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				operonIDVec.add(Integer.parseInt(rs.getString("OperonID")));     
				cogNumVec.add(rs.getString("COG_number"));
				opLocusTagVec.add(rs.getString("locus_tag"));
				opSPVec.add(Integer.parseInt(rs.getString("bgSP"))); 
				opEPVec.add(Integer.parseInt(rs.getString("bgEP"))); 
			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println("bgSPVec.size() = " + bgSPVec.size());
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

	public void makeVec() {                          //operonDB의 항목 수를 맞추기 위해
		
		boolean found = false;
		for (int i = 0; i < bgSPVec.size(); i++){
			for (int j = 0; j < opSPVec.size(); j++){
				if ((bgSPVec.elementAt(i).intValue() == opSPVec.elementAt(j).intValue())||
						(bgEPVec.elementAt(i).intValue() == opEPVec.elementAt(j).intValue())) {
					found = true;
					bgopIDVec.add(operonIDVec.elementAt(j).intValue());
					bgcogNumVec.add(cogNumVec.elementAt(j).toString());
					break;
				}
			}
			if (!found)	{
				bgopIDVec.add(-1);
				bgcogNumVec.add("");
			}
			found = false;
		}
		
		int countRBS = 0;
		int countTP = 0;
		int countPP = 0;
		int countTT = 0;
		int countPT = 0;
		int countICR = 0;
		char ch;
		int bgopID;
		
		for (int i = 0; i < bgSPVec.size(); i++) {              //spaceTypeVec
			bgopID = bgopIDVec.elementAt(i).intValue();
			ch = strandVec.elementAt(i).charAt(0);
			char ch1 = '0';
			int bgopID1 = 0;
			if (i == 0) {
				bgopID1 = bgopIDVec.elementAt(bgopIDVec.size()-1).intValue();
				ch1 = strandVec.elementAt(strandVec.size()-1).charAt(0);
			}
			if (i > 0 ) {
				bgopID1 = bgopIDVec.elementAt(i-1).intValue();
				ch1 = strandVec.elementAt(i-1).charAt(0);
			}
				
			if ((bgopID!= -1) && (bgopID == bgopID1)){
				if (geneTypeVec.elementAt(i).toString() == "CDS") {
					spaceTypeVec.add("RBS");
					countRBS++;
				}else{
					spaceTypeVec.add("ICR");
					countICR++;
				}
			}else if ((ch == '+') && (ch1 == '+')){
					spaceTypeVec.add("TP");
					countTP++;
			}else if  ((ch == '+') && (ch1  == '-')){
					spaceTypeVec.add("PP");
					countPP++;
			}else if ((ch  == '-') && (ch1 == '+')){
					spaceTypeVec.add("TT");
					countTT++;
			}else if ((ch  == '-') && (ch1  == '-')){
					spaceTypeVec.add("PT");
					countPT++;
			}else{
				spaceTypeVec.add("ICR");
				countICR++;
			}
		}

		int egPseudoNum = 0;
		for (int i = 0; i < bgSPVec.size(); i++) { 
			if (isEGVec.get(i) == "+" && isPseudoVec.get(i) =="+")
				egPseudoNum++;
		}
		
		System.out.println("egPseudoNum= " + egPseudoNum); 
		System.out.println("RBS= " + countRBS + ", TP= " + countTP + ", PP= " + countPP + ", TT= " + countTT + 
				", PT= " + countPT + ", ICR= " + countICR);
		System.out.println("spaceTypeVec= " + spaceTypeVec.size());
	}
	
	public void makeBgBrick() {
		
		int iCRBrickSP = 0;
		int iCRBrickEP = 0;
		int brickLen = 0;
		String brickSeq = "";
		
		System.out.println("strandVec= " + strandVec.size());
		
		for (int i = 0; i < bgSPVec.size(); i++) {  
			if (i == 0) {
				iCRBrickSP = (bgEPVec.elementAt(bgSPVec.size()-1).intValue() + 1);
				iCRBrickEP = bgSPVec.elementAt(i).intValue() - 1;
				brickLen = (seqLen - iCRBrickSP + 1) + iCRBrickEP;
				brickSeq = sSeq.substring(iCRBrickSP) + sSeq.substring(0, iCRBrickEP + 1);
			}
			if (i > 0 ) { 
				iCRBrickSP = (bgEPVec.elementAt(i-1).intValue() + 1);
				iCRBrickEP = bgSPVec.elementAt(i).intValue() - 1;
				brickLen = iCRBrickEP - iCRBrickSP + 1;
				if (iCRBrickEP >= iCRBrickSP) 				
					brickSeq = sSeq.substring(iCRBrickSP, iCRBrickEP + 1);
				else brickSeq = sSeq.substring(iCRBrickEP + 1, iCRBrickSP); //overlap seq 나타냄
			}
			
			brickLenVec.add(brickLen);
			brickSeqVec.add(brickSeq);
			brickIDVec.add(i * 2 + 1);
			brickSPVec.add(iCRBrickSP);
			brickEPVec.add(iCRBrickEP);
			brickTypeVec.add(spaceTypeVec.elementAt(i).toString());
			bgBrickTypeVec.add("ICR");

			brickLenVec.add((bgEPVec.elementAt(i).intValue() - (bgSPVec.elementAt(i).intValue()) + 1));
			brickSeqVec.add(sSeq.substring(bgSPVec.elementAt(i).intValue(), bgEPVec.elementAt(i).intValue() + 1));
			brickIDVec.add(i * 2 + 2);
			brickSPVec.add(bgSPVec.get(i));
			brickEPVec.add(bgEPVec.get(i));
			brickTypeVec.add(geneTypeVec.elementAt(i).toString());      //RNA 포함하여 수정!!!!!
			bgBrickTypeVec.add("CR");
		}

		try {
			RandomAccessFile raf = new RandomAccessFile("bgBrickDB.fas", "rw");
			raf.seek(raf.length()); 
			for (int i = 0; i < brickSeqVec.size(); i++) {
				nLoc = String.valueOf(brickSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(brickEPVec.elementAt(i).intValue());
				if (i % 2 != 0) {
					int j = i / 2;
					if (strandVec.elementAt(j).toString() == "-")
						nLoc = "complement(" + String.valueOf(brickSPVec.elementAt(i).intValue()) +
						".." + String.valueOf(brickEPVec.elementAt(i).intValue()) + ")";
				}
				brickLen = brickLenVec.elementAt(i).intValue();
				raf.writeBytes(">bgBrick" + brickIDVec.elementAt(i) + "[" + nLoc + "]" + brickLen);
				raf.writeBytes("\n");
				raf.writeBytes(brickSeqVec.elementAt(i));
				raf.writeBytes("\n");
			}
			raf.close();
		 } catch (IOException e) {
		 }
	}

	public void makeBgBrickDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
  
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement(); 
		
			String tableName ="bgBrick_8601";
			
			String bgBrickIDField = "bgBrickID";
			String bgBrickTypeField = "bgBrickType";  //CR, ICR
			String brickTypeField = "brickType";
			String bgSPField = "bgSP";
			String bgEPField = "bgEP";
			String brickLenField = "brickLen";
			String strandField = "Strand";
			String operonIDField = "operonID";
			String isPseudoField = "pseudoGene";
			String isEGField = "EG";
			String geneNameField = "geneName";
			String locusTagField = "locus_tag";
			String funCatField = "funCat";
			String cogNumField = "COG_Num";
			String organismField = "organism";
			String seqField = "sequence";
			String locusField = "locus";
			String seqLenField = "seqLen";
			String isCircleField = "circular";
			
			String dropTableQuery = "drop table if exists " + tableName;
				stmt.executeUpdate(dropTableQuery);
	
			String createTableQuery = "create table " + tableName + " (" + 
					bgBrickIDField + " int, " + bgBrickTypeField + " varchar(12), " + brickTypeField + " varchar(8), " +
					bgSPField + " int, " + bgEPField + " int, " + brickLenField + " int, " + strandField + " char(1), " +
					operonIDField + " int, " +	isPseudoField + " char(1), " +	isEGField + " char(1), " + geneNameField + " varchar(30), " +
					locusTagField + " varchar(30), " + funCatField + " varchar(3), " +	cogNumField + " varchar(30), " +
					locusField + " varchar(12), " + seqLenField + " int, " + 
					isCircleField + " char(1), " + organismField + " varchar(80), " + seqField + " longtext)";
			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName + " (bgBrickID, operonID, " +
						"bgSP, bgEP, Strand, locus_tag, funCat, organism, EG, bgBrickType, brickType, sequence, " +
						"locus, seqLen, COG_Num, geneName, brickLen, pseudoGene, circular)" +
						"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertQuery);
							
			for(int i = 0 ; i < brickSeqVec.size() ; i++){
				pstmt.setInt(1, i + 1);
				pstmt.setInt(3, brickSPVec.elementAt(i).intValue());
				pstmt.setInt(4, brickEPVec.elementAt(i).intValue());
				int j = i / 2;
				if (i % 2 != 0) {
					pstmt.setInt(2, bgopIDVec.elementAt(j).intValue());
					pstmt.setString(5, strandVec.elementAt(j).toString()); //수정 필요
					pstmt.setString(6, locusTagVec.elementAt(j).toString());
					pstmt.setString(7, funCatVec.elementAt(j).toString());
					pstmt.setString(9, isEGVec.elementAt(j).toString());
					pstmt.setString(15, bgcogNumVec.elementAt(j).toString());
					pstmt.setString(16, geneNameVec.elementAt(j).toString());
					pstmt.setString(18, isPseudoVec.elementAt(j).toString());
				}else{
					pstmt.setInt(2, -1);
					pstmt.setString(5, strandVec.elementAt(j).toString()); // 수정 필요. pt에 따라 세분
					pstmt.setString(6, "");
					pstmt.setString(7, "");
					pstmt.setString(9, "");
					pstmt.setString(15, "");
					pstmt.setString(16, "");
					pstmt.setString(18, "");
				}
				
//				System.out.println(brickSeqVec.elementAt(i).toString());
				pstmt.setString(8, orgName);
				pstmt.setString(10, bgBrickTypeVec.elementAt(i).toString());
				pstmt.setString(11, brickTypeVec.elementAt(i).toString());
				pstmt.setString(12, brickSeqVec.elementAt(i).toString());
				pstmt.setString(13, locus);
				pstmt.setInt(14, seqLen);
				pstmt.setInt(17, brickLenVec.elementAt(i).intValue());
				pstmt.setString(19, isCircle);
				pstmt.executeUpdate(); 
			}
			
//			System.out.println();	
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
	
	void printBgBrickInfo() {
	
		try {
			RandomAccessFile raf = new RandomAccessFile("bgBrickInfo_2771.txt", "rw");

			raf.seek(raf.length()); 

 			raf.writeBytes("bgBrickID" + "\t");
 			raf.writeBytes("bgBrickType" + "\t");
 			raf.writeBytes("brickType" + "\t");
			raf.writeBytes("bgSP" + "\t");
			raf.writeBytes("bgEP" + "\t");
			raf.writeBytes("brickLen" + "\t");
			raf.writeBytes("Strand" + "\t");
			raf.writeBytes("operonID" + "\t");
			raf.writeBytes("geneName" + "\t");
			raf.writeBytes("locus_tag" + "\t");
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

			for (int i =0; i < brickTypeVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
				raf.writeBytes(bgBrickTypeVec.elementAt(i).toString() + "\t");
	 			raf.writeBytes(brickTypeVec.elementAt(i).toString() + "\t");
				raf.writeBytes(brickSPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(brickEPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(brickLenVec.elementAt(i).intValue() + "\t");
				
				int j = i / 2;
				if (i % 2 != 0) {
					raf.writeBytes(bgopIDVec.elementAt(j).toString() + "\t");
					raf.writeBytes(strandVec.elementAt(j).toString() + "\t");
					raf.writeBytes(geneNameVec.elementAt(j).toString()+ "\t");
					raf.writeBytes(locusTagVec.elementAt(j).toString()+ "\t");
					raf.writeBytes(isPseudoVec.elementAt(j).toString() + "\t");
					raf.writeBytes(isEGVec.elementAt(j).toString() + "\t");
					raf.writeBytes(funCatVec.elementAt(j).toString() + "\t");
					raf.writeBytes(cogNumVec.elementAt(j).toString() + "\t");
				}else{
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
					raf.writeBytes("\t");
				}

//				raf.writeBytes(koNumVec.elementAt(i).toString() + "\t");
//				raf.writeBytes(goFunNumVec.elementAt(i).toString() + "\t");
//				raf.writeBytes(goProcNumVec.elementAt(i).toString() + "\t");
//				raf.writeBytes(goCompNumVec.elementAt(i).toString() + "\t");
				raf.writeBytes("\t");
				raf.writeBytes("\t");
				raf.writeBytes("\t");
				raf.writeBytes("\t");
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
	
	public static void main(String[] args) throws IllegalSymbolException {

		String sFile = "";
		if (args.length != 1){
		      System.out.println("Usage: java ExtractInformation <file in Genbank format>");
		      System.exit(1);
		    }else{
		    	sFile = args[0];
		}
	
		BgBrick bgBrick = new BgBrick(sFile);
		bgBrick.getData();
		bgBrick.makeVec();
		bgBrick.makeBgBrick();
		bgBrick.makeBgBrickDB();
		bgBrick.printBgBrickInfo();
	}
}

