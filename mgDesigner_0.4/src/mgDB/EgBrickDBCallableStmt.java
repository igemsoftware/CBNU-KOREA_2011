package mgDB;

import java.sql.*;
import java.io.UnsupportedEncodingException;

public class EgBrickDBCallableStmt{
	private boolean exisitObject = false;
	private Connection conn;
	private Statement stmt;
	private PreparedStatement insertPstmt;
	private ResultSet rs;

	public EgBrickDBCallableStmt(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/essentialgene", "root", "1234");		
			this.stmt = conn.createStatement();
			this.insertPstmt = conn.prepareStatement(                                //table 변경시 수정 필요한 부분
					"insert into bGenome(bgID, bgSP, bgEP, bgStrand, gi, " +
										"genename, eg, eFun, COG, funCat, " + 
										"goFun, goComp, goProc, ECNum, organism, " + 
										"mgSP, mgEP, mcSP, mcEP) " +
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	
		}catch(ClassNotFoundException e) {
			System.out.println("EgCallableStmt()에서 예외발생");
			System.out.println(e);
		}catch(SQLException e) {
			System.out.println("EgCallableStmt()에서 예외발생");
			System.out.println(e);

		}	
	}

	public void createObject(){
		if(this.exisitObject == false){
			this.dropbGenome();
			this.createbGenome();
			this.exisitObject = true;
		}
	}
	
	private void dropbGenome(){
		try {
			String dropTableQuery = "drop table if exists bGenome;";
			this.stmt.executeUpdate(dropTableQuery);
		}catch(SQLException e) {
			System.out.println("drop Table()에서 예외발생");
			System.out.println(e);
		}
		System.out.println("bGenome 테이블 삭제 완료.");
	}
	
	private void createbGenome(){                         //table 변경시 수정 필요한 부분
		try {
			String createTableQuery = "create table bGenome(" +
					"bgID int, bgSP int, bgEP int, bgStrand char(1), gi int, "+
					"genename varchar(8), eg char(1), eFun int, COG int, funCat varchar(3), " + 
					"goFun int, goComp int, goProc int, ECNum int, organism varchar(30), " + 
					"mgSP int, mgEP int, mcSP int, mcEP int)";
			this.stmt.executeUpdate(createTableQuery);
			System.out.println("bGenome 테이블 생성 완료.");
		}catch(SQLException e) {
			System.out.println("createTable()에서 예외발생");
			System.out.println(e);
		}
	}
	//table 변경시 수정 필요한 부분
	public void insertRecord(int bgID, int bgSP, int bgEP, String bgStrand, int gi, 
			String genename, String eg, int eFun, int COG, String funCat, 
			 int goFun, int goComp, int goProc, int ECNum, String organism,
			 int mgSP, int mgEP, int mcSP, int mcEP) throws UnsupportedEncodingException{
		try{
			this.insertPstmt.setInt(1, bgID);
			this.insertPstmt.setInt(2, bgSP);
			this.insertPstmt.setInt(3, bgEP);
			this.insertPstmt.setString(4, bgStrand);
			this.insertPstmt.setInt(5, gi);
			this.insertPstmt.setString(6, genename);
			this.insertPstmt.setString(7, eg);
			this.insertPstmt.setInt(8, eFun);
			this.insertPstmt.setInt(9, COG);
			this.insertPstmt.setString(10, funCat);
			this.insertPstmt.setInt(11, goFun);
			this.insertPstmt.setInt(12, goComp);
			this.insertPstmt.setInt(13, goProc);
			this.insertPstmt.setInt(14, ECNum);
			this.insertPstmt.setString(15, organism);
			this.insertPstmt.setInt(16, mgSP);
			this.insertPstmt.setInt(17, mgEP);
			this.insertPstmt.setInt(18, mcSP);
			this.insertPstmt.setInt(19, mcEP);			
			
			
			int insertedRecord = this.insertPstmt.executeUpdate();
			System.out.println(insertedRecord + " row(s) inserted.");
		}catch(SQLException e){
			System.out.println("insertRecord 에서 예외발생");
			System.out.println(e);
		}
	}

	public void printAllbGenome(){
		try{
			this.rs = this.stmt.executeQuery("select * from bGenome");
			while(this.rs.next()){
				System.out.print(rs.getInt(1) + "\t");
				System.out.print(rs.getInt(2) + "\t");
				System.out.print(rs.getInt(3) + "\t");
				System.out.print(rs.getString(4) + "\t");
				System.out.print(rs.getInt(5) + "\t");
				System.out.print(rs.getString(6) + "\t");
				System.out.print(rs.getString(7) + "\t");
				System.out.print(rs.getInt(8) + "\t");
				System.out.print(rs.getInt(9) + "\t");
				System.out.print(rs.getString(10) + "\t");
				System.out.print(rs.getInt(11) + "\t");
				System.out.print(rs.getInt(12) + "\t");
				System.out.print(rs.getInt(13) + "\t");
				System.out.print(rs.getInt(14) + "\t");
				System.out.print(rs.getString(15) + "\t");
				System.out.print(rs.getInt(16) + "\t");
				System.out.print(rs.getInt(17) + "\t");
				System.out.print(rs.getInt(18) + "\t");
				System.out.print(rs.getInt(19) + "\t");	
			}
			//rs.close();
			//stmt.close();
			//conn.close();
		}catch(SQLException e){
			System.out.println("printAllbGenome()에서 예외발생");
			System.out.println(e);
		}
	}
}