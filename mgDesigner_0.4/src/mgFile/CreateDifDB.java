/* 9-Jan-2011 김영창
 * 
 */

package mgFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Vector;

import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Location;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;

import mgDB.DbmanagerV0;

public class CreateDifDB {                     // Making egBricks is easy!

	RichSequence richSeq;
 
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager에서 가져 온 데이터
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene의 본래의 location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<Integer> gegSPVec = new Vector<Integer>();
	Vector<Integer> gegEPVec = new Vector<Integer>();
	Vector<Integer> mgSPVec = new Vector<Integer>();       //essential gene database의 유전자 순서를 genome 상의 순서와 일치시키기 위해 !//db에 저장하자.
	Vector<Integer> mgEPVec = new Vector<Integer>();

	String locus;
	static String nLoc;
	String sFile;
	static String orgName;
	
	public CreateDifDB(String fileName){
	
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(
					new FileReader(".\\data\\egView\\U00096EG.gb")),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        gegSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
	        gegEPVec.add(loc.getMax());
		}
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}

		locus = richSeq.getName();
		
		ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        mgSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    mgEPVec.add(loc.getMax());
		}
		
	}
		
	public String getOrgName(String sFile){

		try {
			FileReader fileReader = new FileReader(sFile);
			BufferedReader reader = new BufferedReader(fileReader);
			String line=null;
			try {
				boolean found = false;
				while((line=reader.readLine()) != null)	{
					if(line.startsWith("  ORGANISM")) {
						orgName = line.substring(12);
						found = true;
					}
					if (found) break;                  // 여러개 나오는 경우가 있음
				}
			}
				catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return orgName;
	}	
	
	void getEgData() {
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //필수 유전자 정보 DB에서 가져오기, oSPVec 생성
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		//funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
	}
	
	void makeDifDB() {                    //필수 유전자 정보와 비교하기 위하여
	
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile("difDB.txt", "rw");

			raf.seek(raf.length()); 
			raf.writeBytes(">" + locus + ", " + orgName);
			raf.writeBytes("\n");
	
			for (int i = 0; i < gegSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
				int gegSP = gegSPVec.elementAt(i).intValue();
				boolean found = false;
				for (int j = 0; j < oSPVec.size(); j++){
					if (gegSP == oSPVec.elementAt(j).intValue()) {
						found = true;
						if (gegEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB 사이의 차이점을 찾기 위해
							System.out.println("Difference in endPoint between GBK: " + gegEPVec.elementAt(i).intValue() +
									", and egDB: " + oEPVec.elementAt(j).intValue());
							raf.writeBytes("Difference in endPoint between GBK: " + gegEPVec.elementAt(i).intValue() +
									", and egDB: " + oEPVec.elementAt(j).intValue());
							raf.writeBytes("\n");
						}
						break;
					}
				}
				if (!found) {
					int gegEP = gegEPVec.elementAt(i).intValue();        // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
					for (int j = 0; j < oEPVec.size(); j++){
						if (gegEP == oEPVec.elementAt(j).intValue()) {
							found = true;
							System.out.println("Difference in startPoint between GBK: " + gegSPVec.elementAt(i).intValue() +
									", and egDB: " + oSPVec.elementAt(j).intValue());
							raf.writeBytes("Difference in startPoint between GBK: " + gegSPVec.elementAt(i).intValue() +
									", and egDB: " + oSPVec.elementAt(j).intValue());
							raf.writeBytes("\n");
							break;
						}
					}
				}
				if (!found) {
					System.out.println("StartPoint, " + gegSPVec.elementAt(i).intValue() +
							", and endPoint, " + gegEPVec.elementAt(i).intValue() + " of the GenBank file are not found in egDB");
					raf.writeBytes("StartPoint, " + gegSPVec.elementAt(i).intValue() +
							", and endPoint, " + gegEPVec.elementAt(i).intValue() + " of the GenBank file are not found in egDB");
					raf.writeBytes("\n");
				}
			}
			
			for (int i = 0; i < oSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
				int oSP = oSPVec.elementAt(i).intValue();
				int oEP = oEPVec.elementAt(i).intValue();
				boolean found = false;
				for (int j = 0; j < gegSPVec.size(); j++){
					int gegSP = gegSPVec.elementAt(j).intValue();
					int gegEP = gegEPVec.elementAt(j).intValue();
				
					if ((oSP == gegSP) || (oEP == gegEP)){
						found = true;
						break;
					}
				}
				if (!found) {
					System.out.println("StartPoint, " + oSPVec.elementAt(i).intValue() +
							", and endPoint, " + oEPVec.elementAt(i).intValue() + " of egDB are not found at the GenBank file");
					raf.writeBytes("StartPoint, " + oSPVec.elementAt(i).intValue() +
							", and endPoint, " + oEPVec.elementAt(i).intValue() + " of egDB are not found at the GenBank file");
					raf.writeBytes("\n");
				}
			}
						
			System.out.println("gegSPVec.size= " + gegSPVec.size() + ", " + "oSPVec.size= " + oSPVec.size() +
					", " + "mgSPVec.size= " + mgSPVec.size());
			raf.writeBytes("gegSPVec.size= " + gegSPVec.size() + ", " + "oSPVec.size= " + oSPVec.size() +
					", " + "mgSPVec.size= " + mgSPVec.size());
			raf.writeBytes("\n"); raf.writeBytes("\n");
			raf.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}       
	
	public static void main(String[] args) throws IllegalSymbolException {

	    if (args.length != 1){
		      System.out.println("Usage: java ExtractInformation <file in Genbank format>");
		      System.exit(1);
	    }
	
		String sFile = args[0];
		CreateDifDB createDifDB = new CreateDifDB(sFile);
		createDifDB.getOrgName(sFile);
		createDifDB.getEgData();
		createDifDB.makeDifDB();
	}
}

