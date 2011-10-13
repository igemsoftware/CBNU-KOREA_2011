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

public class CalcSPInBgBrick {                     // Making egBricks is easy!

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
	public Vector<String> bgBrickTypeVec = new Vector<String>();
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
	
	public Vector<Integer> foundIdxVec = new Vector<Integer>();
	public Vector<Integer> foundNumVec = new Vector<Integer>();
	
	public Vector<Integer> calcSPVec = new Vector<Integer>();
	public Vector<Integer> ssVec = new Vector<Integer>();
	
	Vector<Integer> nbgBrickIDVec = new Vector<Integer>();
	public Vector<String> nbgBrickTypeVec = new Vector<String>();
	public Vector<String> nbrickTypeVec = new Vector<String>(); //CDS, tRNA, rRNA, ncRNA, tmRNA, miscRNA, RBS, TP, PP, TT, PT
	Vector<Integer> nbrickSPVec = new Vector<Integer>();
	Vector<Integer> nbrickEPVec = new Vector<Integer>();
	public Vector<String> nstrandVec = new Vector<String>();
	
	int seqLen;

	void getCalcSP() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "promInGenome";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName;
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				calcSPVec.add(Integer.parseInt(rs.getString("calcSP")));
				promIDVec.add(rs.getString("promoter_id"));     
				promStrandVec.add(rs.getString("promoter_strand"));
//				if (rs.getString("pos_1") != null) pos_1Vec.add(Integer.parseInt(rs.getString("pos_1")));
//					else pos_1Vec.add(null);
				ssVec.add(Integer.parseInt(rs.getString("startPoint")));
			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println("ssVec= " + ssVec.size());
			System.out.println("calcSPVec= " + calcSPVec.size());
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

	void removeNull () {
		
		for (int i = calcSPVec.size()-1; i >= 0; i--) {
			if (calcSPVec.elementAt(i).intValue() <= 0) {
				calcSPVec.remove(i);
				promIDVec.remove(i);     
				promStrandVec.remove(i);
				ssVec.remove(i);
			}
		}
		System.out.println("calcSPVec= " + calcSPVec.size());
	}
	
	void getBgBrickData() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "bgBrick";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName;
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				bgBrickIDVec.add(Integer.parseInt(rs.getString("bgBrickID")));
				bgBrickTypeVec.add(rs.getString("bgBrickType"));
				brickTypeVec.add(rs.getString("brickType"));
				brickSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				brickEPVec.add(Integer.parseInt(rs.getString("bgep")));
				strandVec.add(rs.getString("Strand"));
				seqLen = Integer.parseInt(rs.getString("seqLen"));
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
	
	public void findBgBrick() {

		int calcSP = 0;
		int brickSP = 0;
		int brickEP = 0;
		for (int i = 0; i < calcSPVec.size(); i++) {
			calcSP = calcSPVec.elementAt(i).intValue();
			boolean found = false;
			int foundNum = 0;
			for (int j = 0; j < brickSPVec.size(); j++) {
				brickSP = brickSPVec.elementAt(j).intValue();
				brickEP = brickEPVec.elementAt(j).intValue();
				if ((j == 0) && (calcSP > brickSP)) brickEP = brickEP + seqLen;
				if ((j == 0) && (calcSP < brickEP)) brickSP = brickSP - seqLen;
				if ((calcSP >= brickSP) && (calcSP <= brickEP)) {
					nbgBrickIDVec.add(bgBrickIDVec.get(j));
					nbgBrickTypeVec.add(bgBrickTypeVec.get(j));
					nbrickTypeVec.add(brickTypeVec.get(j));
					nbrickSPVec.add(brickSPVec.get(j));
					nbrickEPVec.add(brickEPVec.get(j));
					nstrandVec.add(strandVec.get(j));
					found = true;
					foundNum++;
				}
			}
			if (!found) {
				nbgBrickIDVec.add(null);
				nbgBrickTypeVec.add(null);
				nbrickTypeVec.add(null);
				nbrickSPVec.add(null);
				nbrickEPVec.add(null);
				nstrandVec.add(null);
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
					calcSPVec.add(k, calcSPVec.get(k)); 
					promIDVec.add(k, promIDVec.get(k));     
					promStrandVec.add(k, promStrandVec.get(k)); 
					ssVec.add(k, ssVec.get(k)); 
				}
			}
		}
		System.out.println("calcSPVec= " +calcSPVec.size());
		System.out.println("nbrickSPVec= " + nbrickSPVec.size());
		System.out.println("nbgBrickIDVec= " + nbgBrickIDVec.size());
	}
	
	public void makeCalcSPInBgBrickDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
  
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement(); 
		
			String tableName ="calcSPInBgBrick";
			
			String calcSPField = "calcSP";
			String promIDField = "promoter_id";
			String ssField = "startSite";
			String promStrandField = "promoter_strand";
			String bgBrickIDField = "bgBrickID";
			String bgBrickTypeField = "bgBrickType";
			String brickTypeField = "brickType";
			String brickSPField = "brickSP";
			String brickEPField = "brickEP";
			String strandField = "Strand";            //세분 할 필요!

			
			String dropTableQuery = "drop table if exists " + tableName;
				stmt.executeUpdate(dropTableQuery);
	
			String createTableQuery = "create table " + tableName + " (" + 
				calcSPField + " int, " + promIDField + " char(12), " + ssField + " int, " +
				promStrandField + " varchar(10), " + bgBrickIDField + " int, " + bgBrickTypeField + " varchar(12), " +
				brickTypeField + " varchar(8), " +	brickSPField + " int, " + brickEPField + " int, " + 
				strandField + " char(1))";
			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName + " (calcSP, promoter_id, startSite, promoter_strand, " +
				"bgBrickID, bgBrickType, brickType, brickSP, brickEP, Strand)" +
				"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertQuery);
							
			for(int i = 0 ; i < calcSPVec.size() ; i++){

				pstmt.setInt(1, calcSPVec.elementAt(i).intValue());
				pstmt.setString(2, promIDVec.elementAt(i).toString());
				pstmt.setInt(3, ssVec.elementAt(i).intValue());
				pstmt.setString(4, promStrandVec.elementAt(i).toString());
//				if (nbgBrickIDVec.get(i) != null)
					pstmt.setInt(5, nbgBrickIDVec.elementAt(i).intValue());
//					else pstmt.setInt(5, 0);
//				if (nbgBrickTypeVec.get(i) != null)
					pstmt.setString(6, nbgBrickTypeVec.elementAt(i).toString());
//					else pstmt.setString(6, null);
//				if (nbrickTypeVec.get(i) != null)
					pstmt.setString(7, nbrickTypeVec.elementAt(i).toString());
//					else pstmt.setString(7, null);
//				if (nbrickSPVec.get(i) != null)
					pstmt.setInt(8, nbrickSPVec.elementAt(i).intValue());
//					else pstmt.setInt(8, 0);
//				if (nbrickEPVec.get(i) != null)
					pstmt.setInt(9, nbrickEPVec.elementAt(i).intValue());
//					else pstmt.setInt(9, 0);
//				if (nstrandVec.get(i) != null)
					pstmt.setString(10, nstrandVec.elementAt(i).toString()); // 수정 필요. pt에 따라 세분
//					else pstmt.setString(10, null);
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
	
	void printCalcSPInBgBrickInfo() {
	
		try {
			RandomAccessFile raf = new RandomAccessFile("calcSPInBgBrickInfo.txt", "rw");

			raf.seek(raf.length()); 
			raf.writeBytes("calcSP" + "\t");
			raf.writeBytes("promoter_id" + "\t");
			raf.writeBytes("startSite" + "\t");
			raf.writeBytes("promoter_strand" + "\t");
 			raf.writeBytes("bgBrickID" + "\t");
 			raf.writeBytes("bgBrickType" + "\t");
 			raf.writeBytes("brickType" + "\t");
			raf.writeBytes("brickSP" + "\t");
			raf.writeBytes("brickEP" + "\t");
			raf.writeBytes("Strand" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < calcSPVec.size(); i++){
				raf.writeBytes(calcSPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(promIDVec.get(i) + "\t");
				raf.writeBytes(ssVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(promStrandVec.get(i) + "\t");
				
//				if (nbgBrickIDVec.get(i) != null) 
					raf.writeBytes(nbgBrickIDVec.elementAt(i).intValue() + "\t");
//				else raf.writeBytes("\t");
//				if (nbgBrickTypeVec.get(i) != null) 
					raf.writeBytes(nbgBrickTypeVec.elementAt(i).toString() + "\t");
//				else raf.writeBytes("\t");
//				if (nbrickTypeVec.get(i) != null) 
					raf.writeBytes(nbrickTypeVec.elementAt(i).toString() + "\t");
//				else raf.writeBytes("\t");
//				if (nbrickSPVec.get(i) != null) 
					raf.writeBytes(nbrickSPVec.elementAt(i).intValue() + "\t");
//				else raf.writeBytes("\t");
//				if (nbrickEPVec.get(i) != null) 
					raf.writeBytes(nbrickEPVec.elementAt(i).intValue() + "\t");
//				else raf.writeBytes("\t");
//				if (nstrandVec.get(i) != null) 
					raf.writeBytes(nstrandVec.elementAt(i).toString() + "\t");
//				else raf.writeBytes("\t");
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
		CalcSPInBgBrick findPT = new CalcSPInBgBrick();
		findPT.getCalcSP();
		findPT.removeNull();
		findPT.getBgBrickData();
		findPT.findBgBrick();
		findPT.makeCalcSPInBgBrickDB();
		findPT.printCalcSPInBgBrickInfo();
	}
}

