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
import org.biojavax.bio.seq.RichSequence;

public class FindPromoterInGenome {                     // Making egBricks is easy!

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
	public Vector<Integer> calcSPVec = new Vector<Integer>();

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

	RichSequence richSeq;
	String sSeq;
	
	public FindPromoterInGenome(String sFile){
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(sFile)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		sSeq = richSeq.seqString();
	}
	
	void getPromoter() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "promoter";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/regulondb", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName;
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				promIDVec.add(rs.getString("promoter_id"));     
				promNameVec.add(rs.getString("promoter_name"));
				promStrandVec.add(rs.getString("promoter_strand"));
				if (rs.getString("pos_1") != null) pos_1Vec.add(Integer.parseInt(rs.getString("pos_1")));
					else pos_1Vec.add(null);
				sigmaFacVec.add(rs.getString("sigma_factor"));
				promSeqVec.add(rs.getString("promoter_sequence")); 
			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println("pos_1Vec= " + pos_1Vec.size());
			System.out.println("sigmaFacVec= " + sigmaFacVec.size());
			System.out.println("promSeqVec= " + promSeqVec.size());
		} catch (SQLException se) {
				se.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
		}
	}

	public void findPromSP() {

		String str1 = "";
		for (int i = 0; i < promSeqVec.size(); i++) {
			boolean found = false;
			int foundNum = 0;

				int x = 0;
				if (promSeqVec.get(i) != null) {
					str1 = promSeqVec.elementAt(i).toString();
					str1 = str1.toLowerCase();
					if (promStrandVec.elementAt(i).toString().equals ("reverse"))
						str1 = reverseComplement(str1);
					x = sSeq.indexOf(str1); 
					if (x >= 0 ) {
						calcSPVec.add(x + 1);

						found = true;
						foundNum++;
					}

			}
			if (!found) {
				calcSPVec.add(null);

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
					promIDVec.add(k, promIDVec.get(k));  
					promNameVec.add(k, promNameVec.get(k));      
					promStrandVec.add(k, promStrandVec.get(k)); 
					pos_1Vec.add(k, pos_1Vec.get(k));
					sigmaFacVec.add(k, sigmaFacVec.get(k)); 
					promSeqVec.add(k, promSeqVec.get(k)); 
				}
			}
		}
		
		System.out.println("promSeqVec= " + promSeqVec.size());
		System.out.println("calcSPVec= " + calcSPVec.size());

	}
	
	public void makePromInGenomeDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
  
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement(); 
		
			String tableName ="promInGenome";
           //세분 할 필요!
			String calcSPField = "calcSP";
			String promIDField = "promoter_id";
			String pos_1Field = "startPoint";
			String sigmaFacField = "sigma_factor";
			String promStrandField = "promoter_strand";
			String promLenField = "promoter_length";

			String promSeqField = "promoter_sequence";

			
			String dropTableQuery = "drop table if exists " + tableName;
				stmt.executeUpdate(dropTableQuery);
	
			String createTableQuery = "create table " + tableName + " (" + calcSPField + " int, " +
				promIDField + " char(12), " + pos_1Field + " int, " + promStrandField + " varchar(10), " +
				sigmaFacField + " varchar(80), " + promLenField + " int, " + promSeqField + " varchar(200))";
			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName + " (calcSP, promoter_id, " +
						"startPoint, promoter_strand, sigma_factor, promoter_length, promoter_sequence)" +
						"values(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertQuery);
							
			for(int i = 0 ; i < promSeqVec.size() ; i++){
				
				if (calcSPVec.get(i) != null) pstmt.setInt(1, calcSPVec.elementAt(i).intValue());
					else pstmt.setInt(1, 0);
				if (promIDVec.get(i) != null) pstmt.setString(2, promIDVec.elementAt(i).toString());
					else pstmt.setString(2, null);
				if (pos_1Vec.get(i) != null) pstmt.setInt(3, pos_1Vec.elementAt(i).intValue());
					else pstmt.setInt(3, 0);
				if (promStrandVec.get(i) != null) pstmt.setString(4, promStrandVec.elementAt(i).toString());
					else pstmt.setString(4, null);
				if (sigmaFacVec.get(i) != null) pstmt.setString(5, sigmaFacVec.elementAt(i).toString());
					else pstmt.setString(5, null);
				if (promSeqVec.get(i) != null) {
					pstmt.setInt(6, promSeqVec.elementAt(i).length());
					pstmt.setString(7, promSeqVec.elementAt(i).toString());
				}else{
					pstmt.setInt(6, 0);
					pstmt.setString(7, null);
				}

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
	
	void printPromInGenomeInfo() {
	
		try {
			RandomAccessFile raf = new RandomAccessFile("promInGenomeInfo.txt", "rw");

			raf.seek(raf.length()); 

			raf.writeBytes("calcSP" + "\t");
			raf.writeBytes("promoter_id" + "\t");
			raf.writeBytes("startPoint" + "\t");
			raf.writeBytes("promoter_strand" + "\t");
			raf.writeBytes("sigma_factor" + "\t");
			
			raf.writeBytes("promoter_length" + "\t");
			
			raf.writeBytes("promoter_sequence" + "\t");

			raf.writeBytes("\n");

			for (int i =0; i < promSeqVec.size(); i++){
				
				if (calcSPVec.get(i) != null) raf.writeBytes(calcSPVec.elementAt(i).intValue() + "\t");
					else raf.writeBytes("\t");
				if (promIDVec.get(i) != null) raf.writeBytes(promIDVec.get(i) + "\t");
					else raf.writeBytes("\t");
				if (pos_1Vec.get(i) != null) raf.writeBytes(pos_1Vec.elementAt(i).intValue() + "\t");
					else raf.writeBytes("\t");
				if (promStrandVec.get(i) != null) raf.writeBytes(promStrandVec.get(i) + "\t");
					else raf.writeBytes("\t");
				if (sigmaFacVec.get(i) != null) raf.writeBytes(sigmaFacVec.get(i) + "\t");
					else raf.writeBytes("\t");

				if (promSeqVec.get(i) != null) {
					raf.writeBytes(promSeqVec.get(i).length() + "\t");
					raf.writeBytes(promSeqVec.get(i) + "\t");
				}else{
					raf.writeBytes("\t");
					raf.writeBytes("\t");
				}

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

		FindPromoterInGenome findPT = new FindPromoterInGenome(sFile);
		findPT.getPromoter();
		findPT.findPromSP();
		findPT.makePromInGenomeDB();
		findPT.printPromInGenomeInfo();

	}
}

