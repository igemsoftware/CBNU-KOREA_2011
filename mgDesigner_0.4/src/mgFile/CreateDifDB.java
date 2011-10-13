/* 9-Jan-2011 �迵â
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
 
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager���� ���� �� ������
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene�� ������ location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<Integer> gegSPVec = new Vector<Integer>();
	Vector<Integer> gegEPVec = new Vector<Integer>();
	Vector<Integer> mgSPVec = new Vector<Integer>();       //essential gene database�� ������ ������ genome ���� ������ ��ġ��Ű�� ���� !//db�� ��������.
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
		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        gegSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
	        gegEPVec.add(loc.getMax());
		}
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}

		locus = richSeq.getName();
		
		ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        mgSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
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
					if (found) break;                  // ������ ������ ��찡 ����
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
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //�ʼ� ������ ���� DB���� ��������, oSPVec ����
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		//funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
	}
	
	void makeDifDB() {                    //�ʼ� ������ ������ ���ϱ� ���Ͽ�
	
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile("difDB.txt", "rw");

			raf.seek(raf.length()); 
			raf.writeBytes(">" + locus + ", " + orgName);
			raf.writeBytes("\n");
	
			for (int i = 0; i < gegSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
				int gegSP = gegSPVec.elementAt(i).intValue();
				boolean found = false;
				for (int j = 0; j < oSPVec.size(); j++){
					if (gegSP == oSPVec.elementAt(j).intValue()) {
						found = true;
						if (gegEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB ������ �������� ã�� ����
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
					int gegEP = gegEPVec.elementAt(i).intValue();        // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
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
			
			for (int i = 0; i < oSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
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

