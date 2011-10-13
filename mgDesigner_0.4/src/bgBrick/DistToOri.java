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

public class DistToOri {                     // Making egBricks is easy!

	Vector<Integer> bgBrickIDVec = new Vector<Integer>();
	Vector<String> bgBrickTypeVec = new Vector<String>();
	Vector<String> brickTypeVec = new Vector<String>(); //CDS, tRNA, rRNA, ncRNA, tmRNA, miscRNA, RBS, TP, PP, TT, PT
	Vector<Integer> brickSPVec = new Vector<Integer>();
	Vector<Integer> brickEPVec = new Vector<Integer>();
	Vector<String> strandVec = new Vector<String>();
	Vector<String> isEGVec = new Vector<String>();
	Vector<Integer> distToOriVec = new Vector<Integer>();
	Vector<String> directionVec = new Vector<String>();
	
	Vector <Integer> EgpVec = new Vector <Integer>() ;
	Vector <Integer> EgmVec = new Vector <Integer>() ;
	Vector <Integer> PPVec = new Vector <Integer>() ;
	Vector <Integer> PMVec = new Vector <Integer>() ;
	Vector <Integer> MPVec = new Vector <Integer>() ;
	Vector <Integer> MMVec = new Vector <Integer>() ;
	
	Vector <Integer> CountEgpVec = new Vector <Integer>() ;
	Vector <Integer> CountEgmVec = new Vector <Integer>() ;
	Vector <Integer> CountPPVec = new Vector <Integer>() ;
	Vector <Integer> CountPMVec = new Vector <Integer>() ;
	Vector <Integer> CountMPVec = new Vector <Integer>() ;
	Vector <Integer> CountMMVec = new Vector <Integer>() ;
	
	Vector<String> GroupNameVec = new Vector<String>();
	Vector<String> CountNameVec = new Vector<String>();
	Vector <Integer> CalVec = new Vector <Integer>() ;
	Vector <Integer> SortVec = new Vector <Integer>() ;

		
	
	
	int seqLen;
	int oriSP = 190964;  //Run하기 전에 확인
	int oriEP = 144;
	int ter = 0;		
	
	void getCRBrickData() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String query;
    	
    	String tableName = "bgBrick_8601";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement();
			query = "select * from " + tableName + " where bgBrickType = 'CR'";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				bgBrickIDVec.add(Integer.parseInt(rs.getString("bgBrickID")));
				bgBrickTypeVec.add(rs.getString("bgBrickType"));
				brickTypeVec.add(rs.getString("brickType"));
				brickSPVec.add(Integer.parseInt(rs.getString("bgsp")));     
				brickEPVec.add(Integer.parseInt(rs.getString("bgep")));
				strandVec.add(rs.getString("Strand"));
				isEGVec.add(rs.getString("EG"));
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
	
	

	
	public void calcDistToOri() {


		int brickSP = 0;
		int brickEP = 0;
		int distToOri = 0;
		String direction = "";
		
		if (oriSP < oriEP & oriEP < (seqLen/2)) {
			ter = (seqLen/2) + (oriSP + oriEP)/2;
		}
		if (oriEP < oriSP){
			ter = (oriSP + oriEP)/2;
		}
		if ((seqLen/2)< oriSP & oriSP < oriEP){
			ter = (oriSP + oriEP)/2 - (seqLen/2);
		}
		
		System.out.println("ter= "+ter);
		
		
		for (int i = 0; i < brickSPVec.size(); i++) {

			brickSP = brickSPVec.elementAt(i).intValue();
			brickEP = brickEPVec.elementAt(i).intValue();
			if ( oriSP < oriEP & oriEP < (seqLen/2) ){
/*				if (strandVec.elementAt(i).toString().charAt(0) == '+') {
					if (brickEP <= oriSP) {
						distToOri = oriSP-brickSP;
						direction = "-";
					}
					if ((brickEP > oriSP) && (brickSP <= ter)) {
						distToOri = brickEP-oriEP;
						direction = "+";
					}
					if ((brickEP > oriSP) && (brickEP > ter)) {
						distToOri = seqLen-brickSP+oriSP;
						direction = "-";
					}
				}
				if (strandVec.elementAt(i).toString().charAt(0) == '-') {
*/					if (brickSP <= oriSP) {
						distToOri = oriSP-brickEP;
						direction = "-";
					}
					if ((brickSP > oriSP) && (brickEP <= ter)) {
						distToOri = brickEP-oriSP;
						direction = "+";
					}
					if ((brickSP > oriSP) && (brickSP > ter)) {
						distToOri = seqLen-brickEP+oriSP;
						direction = "-";
					}
//				}	
			}
			if ( oriEP < oriSP){				
/*				if (strandVec.elementAt(i).toString().charAt(0) == '+') {
					if (brickSP <= ter) {
						distToOri = brickEP - oriEP;
						direction = "+";
					}
					if (brickSP > ter) {
						distToOri = oriSP - brickSP;
						direction = "-";
					}
				}
				if (strandVec.elementAt(i).toString().charAt(0) == '-') {
*/					if (brickEP <= ter) {
						distToOri = brickSP - oriEP;
						direction = "+";
					}
					if (brickEP > ter) {
						distToOri = oriSP - brickEP;
						direction = "-";
					}
//				}	
			}
			if ((seqLen/2)< oriSP & oriSP < oriEP){				
/*				if (strandVec.elementAt(i).toString().charAt(0) == '+') {
					if (ter <= brickEP & brickSP <= oriSP) {
						distToOri = oriSP-brickSP;
						direction = "-";
					}
					if (oriEP<= brickEP & brickSP <= seqLen) {
						distToOri = brickEP - oriEP;
						direction = "+";
					}
					if (brickSP <= ter) {
						distToOri = seqLen-oriEP + brickEP;
						direction = "+";
					}
				}
				if (strandVec.elementAt(i).toString().charAt(0) == '-') {
*/					if (ter <= brickSP & brickEP <= oriSP) {
						distToOri = oriSP-brickEP;
						direction = "-";
					}
					if (oriEP<= brickSP & brickEP <= seqLen) {
						distToOri = brickSP - oriEP;
						direction = "+";
					}
					if (brickEP <= ter) {
						distToOri = seqLen-oriEP + brickSP;
						direction = "+";
					}
//				}
			}	
			
			distToOriVec.add(distToOri);
			directionVec.add(direction);
		}
	}
	
	public void makeDistToOriDB() {
		
		Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
  
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");
			stmt = con.createStatement(); 
		
			String tableName ="distToOri_8601";

			String bgBrickIDField = "bgBrickID";
			String bgBrickTypeField = "bgBrickType";
			String brickTypeField = "brickType";
			String brickSPField = "brickSP";
			String brickEPField = "brickEP";
			String strandField = "Strand";            //세분 할 필요!
			String isEGField = "EG"; 
			String distToOriField = "distToOri";
			String directionField = "direction";
			
			String dropTableQuery = "drop table if exists " + tableName;
				stmt.executeUpdate(dropTableQuery);
	
			String createTableQuery = "create table " + tableName + " (" + 
				bgBrickIDField + " int, " + bgBrickTypeField + " varchar(12), " +
				brickTypeField + " varchar(8), " +	brickSPField + " int, " + brickEPField + " int, " + 
				strandField + " char(1), " + isEGField + " char(1), " + distToOriField + " int, " +
				directionField + " char(1))";
			stmt.executeUpdate(createTableQuery);

			String insertQuery = "insert into " + tableName + " (" +
				"bgBrickID, bgBrickType, brickType, brickSP, brickEP, Strand, EG, distToOri, direction) " +
				"values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertQuery);
							
			for(int i = 0 ; i < brickSPVec.size() ; i++){
				pstmt.setInt(1, bgBrickIDVec.elementAt(i).intValue());
				pstmt.setString(2, bgBrickTypeVec.elementAt(i).toString());
				pstmt.setString(3, brickTypeVec.elementAt(i).toString());
				pstmt.setInt(4, brickSPVec.elementAt(i).intValue());
				pstmt.setInt(5, brickEPVec.elementAt(i).intValue());
				pstmt.setString(6, strandVec.elementAt(i).toString()); // 수정 필요. pt에 따라 세분
				pstmt.setString(7, isEGVec.elementAt(i).toString());
				pstmt.setInt(8, distToOriVec.elementAt(i).intValue());
				pstmt.setString(9, directionVec.elementAt(i).toString());
				pstmt.executeUpdate(); 
			}
			
			System.out.println();	
			System.out.println("PreparedStatement를 통한 가변 필드값 Insert 실행 완료.");	
		
			infoGroupping();
//			countNum();
			
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
	
	public void infoGroupping(){
		for(int j=0; j < bgBrickIDVec.size(); j++){
			if (isEGVec.elementAt(j).toString().equals ("+"))EgpVec.add(distToOriVec.elementAt(j).intValue());

			if (isEGVec.elementAt(j).toString().equals ("-"))EgmVec.add(distToOriVec.elementAt(j).intValue());
		
			// eg일때 & 아닐때 Ori로부터의 거리를 Vec에 ++
			
			if (isEGVec.elementAt(j).toString().equals ("+") 
				& strandVec.elementAt(j).toString().equals ("+")){
				PPVec.add(distToOriVec.elementAt(j).intValue());
			}
			if (isEGVec.elementAt(j).toString().equals ("+") 
				& strandVec.elementAt(j).toString().equals ("-")){
				PMVec.add(distToOriVec.elementAt(j).intValue());
			}
			if (isEGVec.elementAt(j).toString().equals ("-") 
				& strandVec.elementAt(j).toString().equals ("+")){
				MPVec.add(distToOriVec.elementAt(j).intValue());
			}
			if (isEGVec.elementAt(j).toString().equals ("-") 
				& strandVec.elementAt(j).toString().equals ("-")){
				MMVec.add(distToOriVec.elementAt(j).intValue());
			}
			System.out.println("EgpVec.size()= "+ EgpVec.size());		
			System.out.println("EgmVec.size()= "+ EgmVec.size());	
			System.out.println("PPVec.size()= "+ PPVec.size());
			System.out.println("PMVec.size()= "+ PMVec.size());
			System.out.println("MPVec.size()= "+ MPVec.size());
			
			System.out.println("MMVec.size()= "+ MMVec.size());
			
			// eg & Strand의 경우까지 고려.
/*			
		GroupNameVec.add("EgpVec");	
		GroupNameVec.add("EgmVec");			
		GroupNameVec.add("PPVec");			
		GroupNameVec.add("PMVec");			
		GroupNameVec.add("MPVec");			
		GroupNameVec.add("MMVec");		
//VectorName을 넣어놓은 Vector -> 각각의 String(VectorName)으로 그 Vector 를 지칭할수 있게???		
		CountNameVec.add("CountEgpVec");	
		CountNameVec.add("CountEgmVec");			
		CountNameVec.add("CountPPVec");			
		CountNameVec.add("CountPMVec");			
		CountNameVec.add("CountMPVec");			
		CountNameVec.add("CountMMVec");		
*/
		}		
	}
	
//	public void countNum(){
		
/*		
		for(int i = 0; i < GroupNameVec.size(); i++){
			
			CalVec.addAll(GroupNameVec.elementAt(i));
			SortVec.addAll(CountNameVec.elementAt(i));
				
			for(int a = 0 ; a < CalVec.size(); a++){
				for(int b = 0 ; b < 24 ; b++){
					if((b+1)*100000 <PPVec.elementAt(a).intValue() &
							PPVec.elementAt(a).intValue()< (b+2)*100000 ){
						SortVec.set(b,SortVec.elementAt(b)+1);
					}
				}	
			}
		
			CalVec.clear();
			SortVec.clear();
				
		}
*/	
/*	
		for (int c = 0; c < 24; c++){ 
			CountEgpVec.add(0);
			CountEgmVec.add(0);
			CountPPVec.add(0);
			CountPMVec.add(0);
			CountMPVec.add(0);
			CountMMVec.add(0);
		}	
		for(int a = 0 ; a < EgpVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b *100000 <=EgpVec.elementAt(a).intValue() &
						EgpVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountEgpVec.set(b,(CountEgpVec.elementAt(b))+1);
				}
			}
		}
		System.out.print("CountEgpVec.elementAt(12)= " + CountEgpVec.elementAt(12));
		for(int a = 0 ; a < EgmVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b*100000 <=EgmVec.elementAt(a).intValue() &
						EgmVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountEgmVec.set(b,(CountEgmVec.elementAt(b))+1);
				}
			}	
		}		
		System.out.print(" CountEgmVec.elementAt(12)= " + CountEgmVec.elementAt(12));
		for(int a = 0 ; a < PPVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b*100000 <=PPVec.elementAt(a).intValue() &
						PPVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountPPVec.set(b,(CountPPVec.elementAt(b))+1);
				}
			}	
		}
		System.out.println(" CountPPVec.elementAt(12)= " + CountPPVec.elementAt(12));
		for(int a = 0 ; a < PMVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b*100000 <=PMVec.elementAt(a).intValue() &
						PMVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountPMVec.set(b,(CountPMVec.elementAt(b))+1);
				}
			}	
		}
		System.out.print("CountPMVec.elementAt(12)= " + CountPMVec.elementAt(12));
		for(int a = 0 ; a < MPVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b*100000 <=MPVec.elementAt(a).intValue() &
						MPVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountMPVec.set(b,(CountMPVec.elementAt(b))+1);
				}
			}	
		}
		System.out.print(" CountMPVec.elementAt(12)= " + CountMPVec.elementAt(12));
		for(int a = 0 ; a < MMVec.size(); a++){
			for(int b = 0 ; b < 24 ; b++){
				if(b*100000 <=MMVec.elementAt(a).intValue() &
						MMVec.elementAt(a).intValue()< (b+1)*100000 ){
					CountMMVec.set(b,(CountMMVec.elementAt(b))+1);
				}
			}	
		}
		System.out.print(" CountMMVec.elementAt(12)= " + CountMMVec.elementAt(12));


		
	}
*/	

	
	void printDistToOriInfo() {
	
		try {
			RandomAccessFile raf = new RandomAccessFile("distToOriInfo_8601.txt", "rw");

			raf.seek(raf.length()); 

/*			raf.writeBytes("EG(+)" + "\t" +""+ "\t");
 			raf.writeBytes("EG(-)" + "\t" +""+ "\t");
			raf.writeBytes("EG(+)& St(+)" + "\t" +""+ "\t");
 			raf.writeBytes("EG(+)& St(-)" + "\t" +""+ "\t");
 			raf.writeBytes("EG(-)& St(+)" + "\t" +""+ "\t");
			raf.writeBytes("EG(-)& St(-)" + "\t" +""+ "\t");
 			raf.writeBytes("\n");
			
			raf.writeBytes("range" + "\t");
 			raf.writeBytes("Count" + "\t");
 			raf.writeBytes("range" + "\t");
			raf.writeBytes("Count" + "\t");
			raf.writeBytes("range" + "\t");
 			raf.writeBytes("Count" + "\t");
			raf.writeBytes("range" + "\t");
 			raf.writeBytes("Count" + "\t");
			raf.writeBytes("range" + "\t");
 			raf.writeBytes("Count" + "\t");
			raf.writeBytes("range" + "\t");
 			raf.writeBytes("Count" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < 24; i++){
						
								
				CountNameVec.add("CountEgpVec");	
				CountNameVec.add("CountEgmVec");			
				CountNameVec.add("CountPPVec");			
				CountNameVec.add("CountPMVec");			
				CountNameVec.add("CountMPVec");			
				CountNameVec.add("CountMMVec");		
								
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountEgpVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountEgmVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountPPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountPMVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountMPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(i+1 + "00000"+ "\t");
				raf.writeBytes(CountMMVec.elementAt(i).intValue() + "\t");

				raf.writeBytes("\n");
			}
*/			
			
			raf.writeBytes("OrgName" + "\t");
			raf.writeBytes("seqLen" + "\t");
			raf.writeBytes("oriSP" + "\t");
			raf.writeBytes("oriEP" + "\t");
			raf.writeBytes("Ter" + "\t");
			raf.writeBytes("\n");
			
			raf.writeBytes("NC_008601" + "\t");
			raf.writeBytes(seqLen + "\t");
			raf.writeBytes(oriSP + "\t");
			raf.writeBytes(oriEP + "\t");
			raf.writeBytes(ter + "\t");
			raf.writeBytes("\n");

			raf.writeBytes("bgBrickID" + "\t");
 			raf.writeBytes("bgBrickType" + "\t");
 			raf.writeBytes("brickType" + "\t");
			raf.writeBytes("brickSP" + "\t");
			raf.writeBytes("brickEP" + "\t");
			raf.writeBytes("Strand" + "\t");
			raf.writeBytes("EG" + "\t");
			raf.writeBytes("distToOri" + "\t");
			raf.writeBytes("direction" + "\t");
			raf.writeBytes("\n");

			for (int i =0; i < brickSPVec.size(); i++){
				raf.writeBytes(bgBrickIDVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(bgBrickTypeVec.elementAt(i).toString() + "\t");
				raf.writeBytes(brickTypeVec.elementAt(i).toString() + "\t");
				raf.writeBytes(brickSPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(brickEPVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(strandVec.elementAt(i).toString() + "\t");
				raf.writeBytes(isEGVec.elementAt(i).toString() + "\t");
				raf.writeBytes(distToOriVec.elementAt(i).intValue() + "\t");
				raf.writeBytes(directionVec.elementAt(i).toString() + "\t");
				raf.writeBytes("\n");
			}
			raf.close();		
		} catch (IOException e ) {
		}
	 }
	
	public static void main(String[] args) {

		DistToOri distToOri = new DistToOri();
		distToOri.getCRBrickData();
		distToOri.calcDistToOri();
		distToOri.makeDistToOriDB();
		distToOri.printDistToOriInfo();
		
		
	}
}

