package GBK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojavax.SimpleNamespace;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class NCtoDB_GBK {

	Connection conn;
	Connection conn2;
	PreparedStatement pstmt;
	PreparedStatement pstmt2;

	ResultSet rs;

	Set<String> pool_Oric;
	
	File[] childs;
	File[] childchilds;
	
	String insertQu;
	
	double dscale;
	
	String strFileName;
	
	String Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, DistToOri, Scale ,SeqLen;
	String OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation;
	
	int iGS, iSP, iEP, iTER;
	
	int testiGS, iType, iMin, iMax, iDTO, iScale;
	
	
	int iDtoErr;
	int iSaleErr;
	String Err;
	
	BufferedReader br = null;
	SimpleNamespace ns = null;
	

	void db_connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException");
		}

		try {
			String jdbcUrl = "jdbc:mysql://210.115.163.124:3306/gbk_1500";
			String jdbcUrl2 = "jdbc:mysql://210.115.163.124:3306/oric_1500";
			String userid = "root";
			String userPass = "1234";

			conn = DriverManager.getConnection(jdbcUrl, userid, userPass);
			conn2 = DriverManager.getConnection(jdbcUrl2, userid, userPass);

			System.out.println("제대로 연결되었습니다.");

		} catch (Exception e) {
			System.out.println("SQLExcetion : " + e.getMessage());
		}
	}

	void db_close() {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
					conn2.close();
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			conn = null;
		}
	}

	void getOricNameFromDB() throws SQLException {

		pool_Oric = new HashSet<String>();

		String strQu1 = "select RefSeq from ori_data";
		pstmt = conn2.prepareStatement(strQu1);
		rs = pstmt.executeQuery();

		while (rs.next()) {
			pool_Oric.add(rs.getString("RefSeq"));
		}
		

		pstmt.close();

		System.out.println("RefSeq가져오기 완료");
	}
	
	void initBS(){
		iGS = 0;
		iSP = 0;
		iEP = 0;
		iTER = 0;
		
		Type = "";
		GeneName = "";
		LocusTag = "";
		GeneRef = "";
		GeneSP = "";
		GeneEP = "";
		Strand = "";
		Direction = "";
		DistToOri = "";
		Scale = "";
		SeqLen = "";
		OriSP = "";
		OriEP = "";
		Ter = "";
		Note = "";
		Product = "";
		Protein = "";
		Xref = "";
		Locus = "";
		Definition = "";
		Translation = "";
		
		testiGS = 0;
		iType = 0;
		iMin = 0;
		iMax = 0;
		iDTO = 0;
		
		iScale = 0;
		dscale = 0;
		
		iDtoErr=0;
		iSaleErr=0;
		Err="";
		
	}
	
	void initBS2(){
		
		Type = "";
		GeneName = "";
		LocusTag = "";
		GeneRef = "";
		GeneSP = "";
		GeneEP = "";
		Strand = "";
		Direction = "";
		DistToOri = "";
		Scale = "";
		Note = "";
		Product = "";
		Protein = "";
		Xref = "";
		Translation = "";
		
		iMin = 0;
		iMax = 0;
		iDTO = 0;
		
		dscale = 0;
		iScale = 0;
	}
	
	
	void OricFileGet() throws SQLException, NoSuchElementException, BioException, IOException {
		
		getOricNameFromDB();
		
		File directory = new File(".\\GBK_FILE");

		// 하위디렉토리

		childs = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}
		});

		for (int i = 0; i < childs.length; i++) {
			// 하위디렉토리의 파일리스트 불러오기
			childchilds = childs[i].listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.isFile();
				}
			});

			if (childchilds != null) {
				for(int j =0; j<childchilds.length; j++){
					strFileName = childchilds[j].getName().replace(".gbk", "");
					
					
					initBS();
					
					// 해당 수치가 있는 경우
					if(pool_Oric.contains(strFileName)){
						
						
						String strQu4 = "Select GenomeSize, OricSP, OricEP, Ter From ori_data WHERE RefSeq=?";
						pstmt = conn2.prepareStatement(strQu4);
						pstmt.setString(1, strFileName);
						rs = pstmt.executeQuery();
						
						
						// 해당 수치 가져오기.
						// 2개의 경우가 있을경우 어쩌지????
						if(rs.next()){
							iGS = Integer.parseInt(rs.getString("GenomeSize"));
							iSP = Integer.parseInt(rs.getString("OricSP"));
							iEP = Integer.parseInt(rs.getString("OricEP"));
							iTER = Integer.parseInt(rs.getString("Ter"));
						}
						
						pstmt.close();
						
						if (iSP < iEP && iEP < (iGS / 2))
							iType = 1;
						else if (iEP < iSP)
							iType = 2;
						else if ((iGS / 2) < iSP && iSP < iEP)
							iType = 3;
						
						FileReader frgbk = new FileReader(childs[i] +"\\"+strFileName+".gbk");
						br = new BufferedReader(frgbk);
						ns = new SimpleNamespace("biojava");
						
						RichSequenceIterator rsi = RichSequence.IOTools.readGenbankDNA(br, ns);
						RichSequence rs = rsi.nextRichSequence();
						
						
						FeatureFilter ff = new FeatureFilter.ByType("source");
						FeatureHolder fh = rs.filter(ff);
						Iterator<Feature> fi = fh.features();
						RichFeature rf;
						
						
						if(fi.hasNext()){
							rf= (RichFeature) fi.next();
							testiGS = rf.getLocation().getMax();
						}
						
						// 종 내 같은값을 갖는것들.
						Locus = rs.getName(); // NC_00??
						Definition = rs.getTaxon().getDisplayName(); // 종 이름
						
						
						// 종 수치
						OriSP = Integer.toString(iSP);
						OriEP = Integer.toString(iEP);
						SeqLen = Integer.toString(iGS);
						Ter = Integer.toString(iTER);
						
						
						// 해당 수치와 NC파일이 같은 경우
						if(iGS == testiGS){
							
							System.gc();
							
							Runtime runtime = Runtime.getRuntime();
							System.out.println(runtime.maxMemory());
							System.out.println(runtime.freeMemory());
							
							
							// 테이블 삭제하기.
							String strQu2 = "drop table if exists " + strFileName;
							pstmt = conn.prepareStatement(strQu2);
							pstmt.executeUpdate();
							pstmt.close();
							
							
							// 테이블 생성하기.
							String strQu3 = "CREATE  TABLE `"+strFileName+"` ( `idNc` INT NOT NULL AUTO_INCREMENT , `Type` TEXT NULL , " +
									"`GeneName` TEXT NULL , `LocusTag` TEXT NULL , `GeneRef` TEXT NULL , `GeneSP` TEXT NULL , `GeneEP` TEXT NULL , " +
									"`Strand` TEXT NULL , `Direction` TEXT NULL , `DistToOri` TEXT NULL , `Scale` TEXT NULL, `SeqLen` TEXT NULL , `Cog` TEXT NULL , `OriSP` TEXT NULL , " +
									"`OriEP` TEXT NULL , `Ter` TEXT NULL , `Note` LONGTEXT NULL , `Product` TEXT NULL , `Protein` TEXT NULL , `Xref` TEXT NULL , " +
									"`Locus` TEXT NULL , `Definition` TEXT NULL , `Translation` LONGTEXT NULL , PRIMARY KEY (`idNc`) );";
							pstmt = conn.prepareStatement(strQu3);
							pstmt.executeUpdate();
							pstmt.close();
							
							
							System.out.print((i+1)+"-"+childs.length+"	"+ (j+1) +"-"+childchilds.length+"	"+strFileName + " 실행중...");
							
							// CDS먼저 탐색
							ff = new FeatureFilter.ByType("CDS");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "CDS";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							ff = new FeatureFilter.ByType("tRNA");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "tRNA";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							ff = new FeatureFilter.ByType("ncRNA");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "ncRNA";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
								
							}
							
							
							ff = new FeatureFilter.ByType("repeat_region");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "repeat_region";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							
							ff = new FeatureFilter.ByType("mat_peptide");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "mat_peptide";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							ff = new FeatureFilter.ByType("mobile_element");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "mobile_element";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							
							ff = new FeatureFilter.ByType("RBS");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "RBS";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin
											&& (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP
											&& (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP
											&& (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							ff = new FeatureFilter.ByType("-10_signal");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "-10_signal";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
								
							}
							
							
							ff = new FeatureFilter.ByType("-35_signal");
							fh = rs.filter(ff);
							fi = fh.features();
							
							while(fi.hasNext()){
								rf= (RichFeature) fi.next();
								
								initBS2();
								
								Type = "-10_signal";
								if(rf.getAnnotation().containsProperty("gene")){
									GeneName = rf.getAnnotation().getProperty("gene").toString();
									}
								
								
								if(rf.getAnnotation().containsProperty("locus_tag")){
									LocusTag = rf.getAnnotation().getProperty("locus_tag").toString();
									}
								
								
								if(rf.getRankedCrossRefs().toString().contains("GI")){
									GeneRef = rf.getRankedCrossRefs().toArray()[0].toString().substring(5).replace(".0", "");
									}
								
								
								iMin = rf.getLocation().getMin();
								iMax = rf.getLocation().getMax();
								GeneSP = Integer.toString(iMin);
								GeneEP = Integer.toString(iMax);
								
								// 스트랜드 정해주기
								if(rf.getStrand().getValue() == 1){
									Strand = "+";
								}
								else{
									Strand = "-";
								}
								
								// DistToOri 계산
								
								switch (iType) {

								case 1:
									if (iMin <= iSP && iMax <= iSP) {
										Direction = "-";
										iDTO = iSP - iMax;
									} else if (iEP < iMin
											&& ((iMin - iEP) < (iGS - iMax + iSP))) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin
											&& ((iMin - iEP) > (iGS - iMax + iSP))) {
										Direction = "-";
										iDTO = iGS - iMax + iSP;
									}
									break;

								case 2:
									if (iEP < iMin && (iMin - iEP) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iEP < iMin && (iMin - iEP) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;

								case 3:

									if (iEP <= iMin && iMax <= iGS) {
										Direction = "+";
										iDTO = iMin - iEP;
									} else if (iMax < iSP && (iGS - iEP + iMin) < (iSP - iMax)) {
										Direction = "+";
										iDTO = iGS - iEP + iMin;
									} else if (iMax < iSP && (iGS - iEP + iMin) > (iSP - iMax)) {
										Direction = "-";
										iDTO = iSP - iMax;
									}
									break;
								}

								DistToOri = Integer.toString(iDTO);
								if(iDTO < 0){
									iDtoErr++;
								}
								
								dscale = (double)iDTO / ((double)iGS/2);
								iScale = (int) (dscale*10);
								Scale = Integer.toString(iScale+1);
								if(iScale > 10){
									iSaleErr++;
								}
								
								if(rf.getAnnotation().containsProperty("note")){
									Note = rf.getAnnotation().getProperty("note").toString();
								}
								
								if(rf.getAnnotation().containsProperty("product")){
									Product = rf.getAnnotation().getProperty("product").toString();
								}
								
								if(rf.getAnnotation().containsProperty("protein_id")){
									Protein = rf.getAnnotation().getProperty("protein_id").toString();	
								}
								
								Xref = rf.getRankedCrossRefs().toString().replaceAll("\\(#[0-99]\\) ", "").replace("[", "").replace("]", "");
								
								if(rf.getAnnotation().containsProperty("translation")){
									
								Translation = rf.getAnnotation().getProperty("translation").toString();
								}

								
								insertQu = "Insert into " +strFileName+" ( Type, GeneName, LocusTag, GeneRef, GeneSP, GeneEP, Strand, Direction, " +
										"DistToOri, Scale, SeqLen, OriSP, OriEP, Ter, Note, Product, Protein, Xref, Locus, Definition, Translation ) " +
										"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								
								pstmt = conn.prepareStatement(insertQu);
								pstmt.setString(1, Type);
								pstmt.setString(2, GeneName);
								pstmt.setString(3, LocusTag);
								pstmt.setString(4, GeneRef);
								pstmt.setString(5, GeneSP);
								pstmt.setString(6, GeneEP);
								pstmt.setString(7, Strand);
								pstmt.setString(8, Direction);
								pstmt.setString(9, DistToOri);
								pstmt.setString(10, Scale);
								pstmt.setString(11, SeqLen);
								pstmt.setString(12, OriSP);
								pstmt.setString(13, OriEP);
								pstmt.setString(14, Ter);
								pstmt.setString(15, Note);
								pstmt.setString(16, Product);
								pstmt.setString(17, Protein);
								pstmt.setString(18, Xref);
								pstmt.setString(19, Locus);
								pstmt.setString(20, Definition);
								pstmt.setString(21, Translation);
								
								pstmt.executeUpdate();
							}
							
							
							br.close();
							frgbk = null;
							br = null;
							
							rsi = null;
							rs = null;
							
							fi = null;
							rf = null;
							fh = null;
							ff = null;
							
							Err = "iDTO에러 : "+iDtoErr+" iScale에러 : "+iSaleErr+" 완료";
							
							String ErrQu = "insert into LOG (Nc, Error) values (?,?)";
							pstmt2 = conn.prepareStatement(ErrQu);
							pstmt2.setString(1, strFileName);
							pstmt2.setString(2, Err);
							pstmt2.executeUpdate();
							
							System.out.println("...완료");
						}
						else{
							
							Err = "NC데이터와 Oric데이터 틀림";
							
							String ErrQu = "insert into LOG (Nc, Error) values (?,?)";
							pstmt2 = conn.prepareStatement(ErrQu);
							pstmt2.setString(1, strFileName);
							pstmt2.setString(2, Err);
							pstmt2.executeUpdate();
							
							System.out.println((i+1)+"-"+childs.length+"	"+ (j+1) +"-"+childchilds.length+"	"+strFileName+" [틀림]");
						}
						
						
						
					}
					//해당 수치가 없는 경우
					else {
						
						Err = "해당 데이터 Oric없음";
						
						String ErrQu = "insert into LOG (Nc, Error) values (?,?)";
						pstmt2 = conn.prepareStatement(ErrQu);
						pstmt2.setString(1, strFileName);
						pstmt2.setString(2, Err);
						pstmt2.executeUpdate();
						
						System.out.println((i+1)+"-"+childs.length+"	"+ (j+1) +"-"+childchilds.length+"	"+strFileName + " [없음]");
					}
				}
			}
		}

	}

	public static void main(String[] args) throws SQLException, NoSuchElementException, BioException, IOException {
		// TODO Auto-generated method stub

		NCtoDB_GBK n1 = new NCtoDB_GBK();
		n1.db_connect();
		n1.OricFileGet();
		n1.db_close();

	}

}
