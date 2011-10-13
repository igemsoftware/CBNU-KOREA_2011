/* 1-Jan-2011 김영창
 * 미생물 genome gb 파일과 필수 유전자 db를 이용하여 minimal genome을 만들고 gb 파일을 생성.
 */

package bgBrick;

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

public class FindTerm {                     // Making egBricks is easy!

	 Vector<Integer> rbsSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> rbsEPVec = new Vector<Integer>();
	 Vector<Integer> pribnowSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> pribnowEPVec = new Vector<Integer>();
	 Vector<Integer> recognitionSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> recognitionEPVec = new Vector<Integer>();

	Vector<Integer> brickLenVec = new Vector<Integer>();
	Vector<Integer> brickSPVec = new Vector<Integer>();
	Vector<Integer> brickEPVec = new Vector<Integer>();

	Vector<Integer> bgBrickIDVec = new Vector<Integer>();
	Vector<String> brickSeqVec = new Vector<String>();

	public Vector<String> brickTypeVec = new Vector<String>(); //CDS, tRNA, rRNA, ncRNA, tmRNA, miscRNA, RBS, TP, PP, TT, PT
	public Vector<String> strandVec = new Vector<String>();

	public Vector<Integer> bgopIDVec = new Vector<Integer>();
	public Vector<String> geneNameVec = new Vector<String>();

	public Vector<String> promIDVec = new Vector<String>();
	public Vector<String> promNameVec = new Vector<String>();
	public Vector<String> promStrandVec = new Vector<String>();
	public Vector<Integer> promSPVec = new Vector<Integer>();
	public Vector<Integer> promSPIndexVec = new Vector<Integer>();
	public Vector<Integer> pos_1Vec = new Vector<Integer>();
	public Vector<String> sigmaFacVec = new Vector<String>();
	public Vector<String> promSeqVec = new Vector<String>();
	public Vector<Integer> startPointVec = new Vector<Integer>();
	public Vector<String> nPromIDVec = new Vector<String>();
	public Vector<String> nPromNameVec = new Vector<String>();
	public Vector<String> nPromStrandVec = new Vector<String>();
	public Vector<String> nSigmaFacVec = new Vector<String>();
	public Vector<String> nPromSeqVec = new Vector<String>();
	
	public Vector<Integer> calcSPVec = new Vector<Integer>();
	public Vector<String> termIDVec = new Vector<String>();
	public Vector<Integer> termSPVec = new Vector<Integer>();
	public Vector<Integer> termEPVec = new Vector<Integer>();
	public Vector<String> termClassVec = new Vector<String>();
	public Vector<String> termSeqVec = new Vector<String>();

	public Vector<String> nTermIDVec = new Vector<String>();
	public Vector<Integer> nTermSPVec = new Vector<Integer>();
	public Vector<Integer> nTermEPVec = new Vector<Integer>();
	public Vector<String> nTermClassVec = new Vector<String>();
	public Vector<String> nTermSeqVec = new Vector<String>();
	
	public Vector<Integer> foundIdxVec = new Vector<Integer>();
	public Vector<Integer> foundNumVec = new Vector<Integer>();

	void getTerminator() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "terminator";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/regulondb", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName;
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				termIDVec.add(rs.getString("terminator_id"));     
				termSPVec.add(Integer.parseInt(rs.getString("terminator_posleft")));
				termEPVec.add(Integer.parseInt(rs.getString("terminator_posright")));
//				if (rs.getString("pos_1") != null) pos_1Vec.add(Integer.parseInt(rs.getString("pos_1")));
//					else pos_1Vec.add(null);
				termClassVec.add(rs.getString("terminator_class"));
				termSeqVec.add(rs.getString("terminator_sequence")); 
			}
			rs.close();
			stmt.close();
			con.close();

			System.out.println("termSeqVec= " + termSeqVec.size());
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

	void getBrickData() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "bgBrick";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName + " where brickType = 'TP' or brickType = 'PP' or " +
					"brickType = 'TT' or brickType = 'PT'";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				bgBrickIDVec.add(Integer.parseInt(rs.getString("bgBrickID")));  
				brickSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				brickEPVec.add(Integer.parseInt(rs.getString("bgep")));
				brickLenVec.add(Integer.parseInt(rs.getString("brickLen")));
				brickTypeVec.add(rs.getString("brickType"));
				strandVec.add(rs.getString("Strand"));
				brickSeqVec.add(rs.getString("sequence"));
			}
			rs.close();
			stmt.close();
			con.close();

			System.out.println("bgBrickIDVec= " + bgBrickIDVec.size());
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}
	
	public void findCalcSP() {

		String str1 = "";
		String str2 = "";
		for (int i = 0; i < brickSeqVec.size(); i++) {
			str1 = brickSeqVec.elementAt(i).toString();
			boolean found = false;
			int foundNum = 0;
			for (int j = 0; j < termSeqVec.size(); j++) {
				int x = 0;
				if (termSeqVec.get(j) != null) {
					str2 = termSeqVec.elementAt(j).toString();
					str2 = str2.toLowerCase();
					x = str1.indexOf(str2); 
					if (x >= 0 ) {
						calcSPVec.add(brickSPVec.elementAt(i).intValue() + x + 1);
						nTermIDVec.add(termIDVec.elementAt(j).toString());
						nTermSPVec.add(termSPVec.elementAt(j).intValue());
						nTermEPVec.add(termEPVec.elementAt(j).intValue());
						nTermSeqVec.add(termSeqVec.elementAt(j).toString());
						nTermClassVec.add(termClassVec.get(j));
					
						found = true;
						foundNum++;
					}
				}
			}
			if (!found) {
				calcSPVec.add(null);
				nTermIDVec.add(null);
				nTermSPVec.add(null);
				nTermEPVec.add(null);	
				nTermSeqVec.add(null);
				nTermClassVec.add(null);
			}
			if (foundNum > 1) {
				foundIdxVec.add(i);
				foundNumVec.add(foundNum);
				System.out.println("foundNum= " + foundNum);
			}
		}
		int foundNum = 0;
		for (int i = 0; i < foundIdxVec.size(); i++) {
			if (foundIdxVec.get(i) != null) foundNum++;
		}
		if (foundNum != 0) {
			for (int i = foundNum-1; i >= 0; i--) {
				int k = foundIdxVec.elementAt(i).intValue();
				for (int j = 0; j < foundNumVec.elementAt(i).intValue()-1; j++) {
					bgBrickIDVec.add(k, bgBrickIDVec.get(k));  
					brickSPVec.add(k, brickSPVec.get(k));      
					brickEPVec.add(k, brickEPVec.get(k)); 
					brickLenVec.add(k, brickLenVec.get(k)); 
					brickTypeVec.add(k, brickTypeVec.get(k)); 
					strandVec.add(k, strandVec.get(k)); 
					brickSeqVec.add(k, brickSeqVec.get(k)); 
				}
			}
		}
		
		System.out.println("brickSeqVec= " + brickSeqVec.size());
		System.out.println("termSeqVec= " + termSeqVec.size());
		System.out.println("termSPVec= " + termSPVec.size());
		System.out.println("nTermSeqVec= " + nTermSeqVec.size());
		System.out.println("calcSPVec= " + calcSPVec.size());
	}
	
	public void makeTermBrickDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
  
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement(); 
		
			String tableName ="termBrick";
			
			String termBrickIDField = "termBrickID";
			String bgBrickIDField = "bgBrickID";
			String brickTypeField = "brickType";
			String brickSPField = "brickSP";
			String brickEPField = "brickEP";
			String brickLenField = "brickLen";
			String strandField = "Strand";            //세분 할 필요!
			String calcSPField = "calcSP";
			String termIDField = "terminator_id";
			String termSPField = "termSP";
			String termEPField = "termEP";
			String termClassField = "terminator_class";
			String termLenField = "terminator_length";

			String termSeqField = "terminator_sequence";
			String brickSeqField = "brick_sequence";
			
			String dropTableQuery = "drop table if exists " + tableName;
				stmt.executeUpdate(dropTableQuery);
	
			String createTableQuery = "create table " + tableName + " (" + termBrickIDField + " int, " +
				bgBrickIDField + " int, " + brickTypeField + " varchar(8), " +	brickSPField + " int, " +
				brickEPField + " int, " + brickLenField + " int, " + strandField + " char(1), " +
				calcSPField + " int, " + termIDField + " char(12), " + termSPField + " int, " +
				termEPField + " int, "  + termClassField + " varchar(30), " +
				termLenField + " int, " + termSeqField + " varchar(200), " + 
				brickSeqField + " text)";
			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName + " (termBrickID, bgBrickID, " +
						"brickType, brickSP, brickEP, brickLen, Strand, calcSP, terminator_id, " +
						"termSP, termEP, terminator_class, terminator_length, terminator_sequence, brick_sequence)" +
						"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertQuery);
							
			for(int i = 0 ; i < nTermSeqVec.size() ; i++){
				pstmt.setInt(1, i + 1);
				pstmt.setInt(2, bgBrickIDVec.elementAt(i).intValue());
				pstmt.setString(3, brickTypeVec.elementAt(i).toString());
				pstmt.setInt(4, brickSPVec.elementAt(i).intValue());
				pstmt.setInt(5, brickEPVec.elementAt(i).intValue());
				pstmt.setInt(6, brickLenVec.elementAt(i).intValue());
				pstmt.setString(7, strandVec.elementAt(i).toString()); // 수정 필요. pt에 따라 세분
				
				if (calcSPVec.get(i) != null) pstmt.setInt(8, calcSPVec.elementAt(i).intValue());
					else pstmt.setInt(8, 0);
				if (nTermIDVec.get(i) != null) pstmt.setString(9, nTermIDVec.elementAt(i).toString());
					else pstmt.setString(9, null);
				if (nTermSPVec.get(i) != null) pstmt.setInt(10, nTermSPVec.elementAt(i).intValue());
					else pstmt.setInt(10, 0);
				if (nTermEPVec.get(i) != null) pstmt.setInt(11, nTermEPVec.elementAt(i).intValue());
					else pstmt.setInt(11, 0);
				if (nTermClassVec.get(i) != null) pstmt.setString(12, nTermClassVec.elementAt(i).toString());
					else pstmt.setString(12, null);
				if (nTermSeqVec.get(i) != null) {
					pstmt.setInt(13, nTermSeqVec.elementAt(i).length());
					pstmt.setString(14, nTermSeqVec.elementAt(i).toString());
				}else{
					pstmt.setInt(13, 0);
					pstmt.setString(14, null);
				}

				pstmt.setString(15, brickSeqVec.elementAt(i).toString());

				pstmt.executeUpdate(); 
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
			if(con != null){
				try{
					con.close();
				}catch(SQLException e){}
			}
		}
	}	
	
	void printTermBrickInfo() {
	
		try {
			RandomAccessFile raf = new RandomAccessFile("termBrickInfo.txt", "rw");

			raf.seek(raf.length()); 

 			raf.writeBytes("termBrickID" + "\t");
 			raf.writeBytes("bgBrickID" + "\t");
 			raf.writeBytes("brickType" + "\t");
			raf.writeBytes("brickSP" + "\t");
			raf.writeBytes("brickEP" + "\t");
			raf.writeBytes("brickLen" + "\t");
			raf.writeBytes("Strand" + "\t");
			raf.writeBytes("calcSP" + "\t");
			raf.writeBytes("terminator_id" + "\t");
			raf.writeBytes("termSP" + "\t");
			raf.writeBytes("termEP" + "\t");
			raf.writeBytes("terminator_class" + "\t");
			
			raf.writeBytes("terminator_length" + "\t");
			
			raf.writeBytes("terminator_sequence" + "\t");
			raf.writeBytes("brick_sequence" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < termSeqVec.size(); i++){
				raf.writeBytes(i+1 + "\t");
				raf.writeBytes(bgBrickIDVec.elementAt(i).intValue() + "\t");
	 			raf.writeBytes(brickTypeVec.elementAt(i).toString() + "\t");
				raf.writeBytes(brickSPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(brickEPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(brickLenVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(strandVec.elementAt(i).toString() + "\t");
				
				if (calcSPVec.get(i) != null) raf.writeBytes(calcSPVec.elementAt(i).intValue() + "\t");
					else raf.writeBytes("\t");
				if (nTermIDVec.get(i) != null) raf.writeBytes(nTermIDVec.get(i) + "\t");
					else raf.writeBytes("\t");
				if (termSPVec.get(i) != null) raf.writeBytes(termSPVec.elementAt(i).intValue() + "\t");
					else raf.writeBytes("\t");
				if (termEPVec.get(i) != null) raf.writeBytes(termEPVec.elementAt(i).intValue() + "\t");
					else raf.writeBytes("\t");
				if (termClassVec.get(i) != null) raf.writeBytes(termClassVec.get(i) + "\t");
					else raf.writeBytes("\t");

				if (nTermSeqVec.get(i) != null) {
					raf.writeBytes(nTermSeqVec.get(i).length() + "\t");
					raf.writeBytes(nTermSeqVec.get(i) + "\t");
				}else{
					raf.writeBytes("\t");
					raf.writeBytes("\t");
				}

				raf.writeBytes(brickSeqVec.get(i) + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
			
		}
	 }
	
	
	public String reverseComplement(String str){
		
		String str1 = "";
		for (int i = str.length()-1; i >= 0; i--) {
			if (str.charAt(i) == 'a') str1 = str1 + "t";
			if (str.charAt(i) == 't') str1 = str1 + "a";
			if (str.charAt(i) == 'g') str1 = str1 + "c";
			if (str.charAt(i) == 'c') str1 = str1 + "g";
		}
		return str1;
	}
	
	public static void main(String[] args) throws IllegalSymbolException {

		String sFile = "";
		if (args.length != 1){
		      System.out.println("Usage: java ExtractInformation <file in Genbank format>");
		      System.exit(1);
		    }else{
		    	sFile = args[0];
		}
//		FindPT findPT = new FindPT(sFile);
		FindTerm findPT = new FindTerm();
		findPT.getTerminator();
		findPT.getBrickData();
		findPT.findCalcSP();
		findPT.makeTermBrickDB();
		findPT.printTermBrickInfo();

	}
}

