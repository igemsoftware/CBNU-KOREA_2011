/* 1-Jan-2011 �迵â
 * �̻��� genome gb ���ϰ� �ʼ� ������ db�� �̿��Ͽ� minimal genome�� ����� gb ������ ����.
 */

package keeep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.symbol.Location;
import org.biojavax.Note;
import org.biojavax.RichAnnotation;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.ontology.ComparableTerm;

public class CreateMgFuncCat {                     // Making egBricks is easy!

	RichSequence richSeq;

	 public static Vector<String> geneNameVec = new Vector<String>();   //genome ���� ��� ������ �̸�
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> mgGeneNameVec = new Vector<String>();
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager���� ���� �� ������
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene�� ������ location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	//Vector<String> funcDefVec = new Vector<String>();
	Vector<Integer> gegSPVec = new Vector<Integer>();
	Vector<Integer> gegEPVec = new Vector<Integer>();
	Vector<Integer> mgSPVec = new Vector<Integer>();       //essential gene database�� ������ ������ genome ���� ������ ��ġ��Ű�� ���� !//db�� ��������.
	Vector<Integer> mgEPVec = new Vector<Integer>();
	Vector<Integer> mgStrandVec = new Vector<Integer>();
	public static Vector<String> mgFuncCatVec = new Vector<String>();
	
	int seqLen;
	String locus;
	String circular;
	static String nLoc;
	static File tFile;
	String sFile;
	static String orgName;
	int geneNameNum = 0;
	
	public CreateMgFuncCat(String fileName){
		
		
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
		if(richSeq.getCircular()) circular = "circular";  
		seqLen = richSeq.length();  

		
		ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        mgSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
		    mgEPVec.add(loc.getMax());
		    mgStrandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()�� +, -��
	    
		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	        locusTagVec.add((String) ra.getProperty("locus_tag"));
	        ComparableTerm geneTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("gene");
	        String geneName = "";
	        boolean found = false;
	        for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){        //Iterate through the notes in the annotation 
	        	Note note = ni.next();
	        	if(note.getTerm().equals(geneTerm)){     //Check each note to see if it matches one of the required ComparableTerms
	        		geneName = note.getValue().toString();
	        		geneNameNum = geneNameNum +1;
	        		geneNameVec.add(geneName);
	        		found = true;
	        	}
	        }
	        if (!found) geneNameVec.add(locusTagVec.elementAt(geneNameNum-1).toString()); // �������� gene name�� ���� ���� ��� locus_tag�� ���
	        if (!found) geneName = "";
	    	mgGeneNameVec.add(geneName);    // �������� gene name�� ���� ���� ""�� ���
	    }
	}
	
	Vector<String> getgNameVec() {                 // ������ �̸� ��� ��ȯ
		return geneNameVec;
	}

	String getGeneName(int i) {                       // ������ �̸� ��ȯ
		return (String)(geneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // ������ ���� ��ȯ
		return geneNameVec.size();
	}
	
	int getSeqLen() {                          // ���� ���� ��ȯ
		return seqLen;
	}
	
	int getStrand(int i) {                     // ������ ����      +1, -1
		return (mgStrandVec.elementAt(i));
	}
	
	String getLocusName() {
		return locus;
	}
	
	String getFileName() {
		return sFile;
	}

	public Vector<String> getmgFuncCatVec() {
		return mgFuncCatVec;
	}
	
	
	public String getOrgName(String sFile){

		try {
			FileReader fileReader = new FileReader(sFile);
			BufferedReader reader = new BufferedReader(fileReader);
//*************************** �� ����� organism �̸��� �����ϴ� ������ �����ִ� �ڵ��ε� ���̿��ڹٸ� �̿��Ͽ� �����ϰ� �ٿ�������!	
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
//****************************************************************************   StringTokenizer�� �̿����� ������!				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return orgName;
	}	
	
	public void getEgData() {
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //�ʼ� ������ ���� DB���� ��������, oSPVec ����
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
	}
	
	public void makeMgFuncCatVec() {                    //�ʼ� ������ ������ ���ϱ� ���Ͽ�
	

			for (int i = 0; i < gegSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
				int gegSP = gegSPVec.elementAt(i).intValue();
				String str;
				boolean found = false;
				for (int j = 0; j < oSPVec.size(); j++){
					if (gegSP == oSPVec.elementAt(j).intValue()) {
						found = true;
						if (gegEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB ������ �������� ã�� ����
							System.out.println("reporting differences in endPoint, " + gegEPVec.elementAt(i).intValue() +
									", " + oEPVec.elementAt(j).intValue());
						}
						
						if (funcClassVec.elementAt(j).length() >= 8) {
							str = (String)funcClassVec.elementAt(j);
							mgFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
						}else mgFuncCatVec.add("X");
						break;
					}
				}
				if (!found) {
					int gegEP = gegEPVec.elementAt(i).intValue();        // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
					for (int j = 0; j < oEPVec.size(); j++){
						if (gegEP == oEPVec.elementAt(j).intValue()) {
							found = true;
							System.out.println("reporting differences in startPoint, " + gegSPVec.elementAt(i).intValue() +
									", " + oSPVec.elementAt(j).intValue());
						
							if (funcClassVec.elementAt(j).length() >= 8) {
								str = (String)funcClassVec.elementAt(j);
								mgFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
							}else mgFuncCatVec.add("X");
							break;
						}
					}
				}
				if (!found) {
					mgFuncCatVec.add("X");
					System.out.println("StartPoint, " + gegSPVec.elementAt(i).intValue() +
							", and endPoint, " + gegEPVec.elementAt(i).intValue() + " of the GenBank file are not found in egDB");
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
				}
			}
						
			System.out.println("orgName= " + orgName);
			System.out.println("LOCUS= " + richSeq.getName() + ", " + "GenomeSeqLen= " + richSeq.length());
			System.out.println("gegSPVec.size= " + gegSPVec.size() + ", " + "oSPVec.size= " + oSPVec.size() +
					", " + "mgSPVec.size= " + mgSPVec.size() + ", " + "funcClassVec.size= " + funcClassVec.size() +
					", " + "mgFuncCatVec.size= " + CreateMgFuncCat.mgFuncCatVec.size());
			
	}       
	
	public static void main(String[] args) {

		String sFile = args[0];
		CreateMgFuncCat createMgFuncCat = new CreateMgFuncCat(sFile);
		createMgFuncCat.getOrgName(sFile);
		createMgFuncCat.getEgData();
		createMgFuncCat.makeMgFuncCatVec();
	}
}

