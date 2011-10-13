/* 1-Jan-2011 �迵â
 * �̻��� genome gb ���ϰ� �ʼ� ������ db�� �̿��Ͽ� minimal genome�� ����� gb ������ ����.
 */

package keeep;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;


import org.biojava.bio.Annotation;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Location;
import org.biojava.bio.symbol.SymbolList;
import org.biojavax.Note;
import org.biojavax.RichAnnotation;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequence.Terms;
import org.biojavax.ontology.ComparableTerm;

public class MgFileCreatorV2 {                     // Making egBricks is easy!

	RichSequence richSeq;
	Vector<SymbolList> egBrickSym = new Vector<SymbolList>();

	 Vector<Integer> crSPVec = new Vector<Integer>();       //genome ���� ��� ������ ���� ��ġ
	 Vector<Integer> crEPVec = new Vector<Integer>();       //genome ���� ��� ������ �� ��ġ
	 
	 Vector<Integer> cdsSPVec = new Vector<Integer>();       //genome ���� �ܹ��� ������ ���� ��ġ
	 Vector<Integer> cdsEPVec = new Vector<Integer>();       //genome ���� �ܹ��� ������ �� ��ġ
	 Vector<Integer> rrnaSPVec = new Vector<Integer>();       //genome ���� rRNA ������ ���� ��ġ
	 Vector<Integer> rrnaEPVec = new Vector<Integer>();       //genome ���� rRNA ������ �� ��ġ
	 Vector<Integer> trnaSPVec = new Vector<Integer>();       //genome ���� tRNA ������ ���� ��ġ
	 Vector<Integer> trnaEPVec = new Vector<Integer>();       //genome ���� tRNA ������ �� ��ġ
	 Vector<Integer> ncrnaSPVec = new Vector<Integer>();       //genome ���� ncRNA ������ ���� ��ġ
	 Vector<Integer> ncrnaEPVec = new Vector<Integer>();       //genome ���� ncRNA ������ �� ��ġ
	 Vector<Integer> tmrnaSPVec = new Vector<Integer>();       //genome ���� ncRNA ������ ���� ��ġ
	 Vector<Integer> tmrnaEPVec = new Vector<Integer>();  
	 
	 Vector<Integer> rrSPVec = new Vector<Integer>();       //genome ����repeat_region ��ġ
	 Vector<Integer> rrEPVec = new Vector<Integer>(); 
	 Vector<Integer> mpSPVec = new Vector<Integer>();       //genome ���� mat_peptide ��ġ
	 Vector<Integer> mpEPVec = new Vector<Integer>();  
	 Vector<Integer> meSPVec = new Vector<Integer>();       //genome ���� mobile_element ��ġ
	 Vector<Integer> meEPVec = new Vector<Integer>();  
	 Vector<Integer> rbsSPVec = new Vector<Integer>();       //genome ���� mobile_element ��ġ
	 Vector<Integer> rbsEPVec = new Vector<Integer>();
	 Vector<Integer> pribnowSPVec = new Vector<Integer>();       //genome ���� mobile_element ��ġ
	 Vector<Integer> pribnowEPVec = new Vector<Integer>();
	 Vector<Integer> recognitionSPVec = new Vector<Integer>();       //genome ���� mobile_element ��ġ
	 Vector<Integer> recognitionEPVec = new Vector<Integer>();

	 Vector<String> geneNameVec = new Vector<String>();   //genome ���� ��� ������ �̸�
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> gGeneNameVec = new Vector<String>();
	 Vector<Integer> gStrandVec = new Vector<Integer>();    //genome ���� ��� ������ ���� 
	 
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager���� ���� �� ������
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene�� ������ location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	//Vector<String> funcDefVec = new Vector<String>();

	Vector<Integer> cSPVec = new Vector<Integer>();       //essential gene database�� update !!!!!!!!!!!
	Vector<Integer> cEPVec = new Vector<Integer>();
	Vector<Integer> egSPVec = new Vector<Integer>();       //essential gene database�� ������ ������ genome ���� ������ ��ġ��Ű�� ���� !//db�� ��������.
	Vector<Integer> egEPVec = new Vector<Integer>();
	Vector<Integer> egStrandVec = new Vector<Integer>();
	Vector<Integer> egmpSPVec = new Vector<Integer>();       
	Vector<Integer> egmpEPVec = new Vector<Integer>();
	
	Vector<Integer> mgSPVec = new Vector<Integer>();       //MG������ new location, ������ ȯ�濡 ���� �������� �ڷ���
	Vector<Integer> mgEPVec = new Vector<Integer>();
	Vector<Integer> mgmpSPVec = new Vector<Integer>();       //MG������ new location
	Vector<Integer> mgmpEPVec = new Vector<Integer>();
	
	Vector<Integer> brickSPVec = new Vector<Integer>();
	Vector<Integer> brickEPVec = new Vector<Integer>();
	Vector<String> brickIDVec1 = new Vector<String>();
	Vector<String> brickSeqVec1 = new Vector<String>();
	Vector<String> brickIDVec2 = new Vector<String>();
	Vector<String> brickSeqVec2 = new Vector<String>();
	
	Vector<Integer> nSpaceVec = new Vector<Integer>();
	Vector<String> SpacerSeqVec = new Vector<String>();
	Vector<String> strVec = new Vector<String>();
	
	Vector<String> cFuncCatVec = new Vector<String>();
	public Vector<String> egFuncCatVec = new Vector<String>();
	Vector<String> mgFuncCatVec = new Vector<String>();
	//Vector<Color> gColorCodeVec = new Vector<Color>();
	Vector<String> gColorCodeVec = new Vector<String>();
	
	int seqLen;
	int brickID;
	String locus;
	String circular;
	//boolean circular;
	static String nLoc;
	static File tFile;
	String sFile;
	String fileName;
	static String designerSeq;
	static String sSeq;
	static String orgName;
	int geneNameNum = 0;
	
	public MgFileCreatorV2(String fileName, String tFile){
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}

		locus = richSeq.getName();
		if(richSeq.getCircular()) circular = "circular";  
		seqLen = richSeq.length();  
		sSeq = richSeq.seqString();
		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        crSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
		    crEPVec.add(loc.getMax());
		    gStrandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()�� +, -��
	    
		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	        locusTagVec.add((String) ra.getProperty("locus_tag"));
	        geneNameVec.add((String) ra.getProperty("locus_tag"));
	        ComparableTerm geneTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("gene");
  
	        String geneName = "";
	        boolean found = false;
	        for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){        //Iterate through the notes in the annotation 
	        	Note note = ni.next();
	        	if(note.getTerm().equals(geneTerm)){     //Check each note to see if it matches one of the required ComparableTerms
	        		geneName = note.getValue().toString();
	        		geneNameNum = geneNameNum +1;
	        		found = true;
	        	}
	        }
	        if (!found) geneName = "";
        	gGeneNameVec.add(geneName);
		}
		
		if (geneNameNum == geneNameVec.size()){
			geneNameVec.clear();
			for (int i = 0; i < gGeneNameVec.size(); i++) {
				geneNameVec.add((String)gGeneNameVec.elementAt(i));
			}
		}        
		
		ff = new FeatureFilter.ByType("tRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        trnaSPVec.add(loc.getMin());                                    // tRNA ���� ����
		    trnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("rRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rrnaSPVec.add(loc.getMin());                                    // rRNA ���� ����
		    rrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("ncRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        ncrnaSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    ncrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("tmRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the tmRNA features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        tmrnaSPVec.add(loc.getMin());                                    // tmRNA ���� ����
		    tmrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("repeat_region");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rrSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    rrEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("mat_peptide");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        mpSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    mpEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("mobile_element");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        meSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    meEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("RBS");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rbsSPVec.add(loc.getMin());                                    // RBS ���� ����
		    rbsEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("-10_signal");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        pribnowSPVec.add(loc.getMin());                                    // RBS ���� ����
		    pribnowEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("-35_signal");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        recognitionSPVec.add(loc.getMin());                                    // RBS ���� ����
		    recognitionEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("CDS");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features
	        RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        cdsSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    cdsEPVec.add(loc.getMax());
		}
		
	}
		
	Vector<String> getgNameVec() {                 // ������ �̸� ��� ��ȯ
		return geneNameVec;
	}

	Vector<Integer> getgSP() {                                 // ������ ���� ��ġ ��� ��ȯ
		return crSPVec;
	}
	
	Vector<Integer> getgEP() {                                 // ������ ����ġ ��Ϲ�ȯ
		return crEPVec;
	}
	
	String getGeneName(int i) {                       // ������ �̸� ��ȯ
		return (String)(geneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // ������ ���� ��ȯ
		return geneNameVec.size();
	}
	
	int getgSP(int i) {                        // ������ ���� ��ġ ��ȯ
		return (crSPVec.elementAt(i));
	}
	
	int getgEP(int i) {                        // ������ ����ġ ��ȯ
		return (crEPVec.elementAt(i));
	}
/*	
	int getcSP(int i) {                        // ������ ���� ��ġ ��ȯ
		return (cSPVec.elementAt(i));
	}
*/	
	int getSeqLen() {                          // ���� ���� ��ȯ
		return seqLen;
	}
	
	int getStrand(int i) {                     // ������ ����      +1, -1
		return (gStrandVec.elementAt(i));
	}
	
	String getLocusName() {
		return locus;
	}
	
	String getFileName() {
		return sFile;
	}

	public Vector<String> getegFuncCatVec() {
		return egFuncCatVec;
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
	
	void getEgData() {
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //�ʼ� ������ ���� DB���� ��������, oSPVec ����
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
	}
	
	void compareEgGenome(){                    //�ʼ� ������ ������ ���ϱ� ���Ͽ�

		boolean duplicate = false;
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
			int oSP = oSPVec.elementAt(i).intValue();
			int foundNum = 0;
			int duplicateNum = 0;
			String str;
			for (int j = 0; j < crSPVec.size(); j++){
				if (oSP == crSPVec.elementAt(j).intValue()) {
					//found = true;
					foundNum = foundNum +1;
					if (foundNum == 1) {
						cSPVec.add(crSPVec.elementAt(j).intValue());     
						cEPVec.add(crEPVec.elementAt(j).intValue());
						//egStrandVec.add(gStrandVec.elementAt(j).intValue());
						if (funcClassVec.elementAt(i).length() >= 8) {
							str = (String)funcClassVec.elementAt(i);
							cFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
						}else{
							cFuncCatVec.add("X");
						}
					}
					if (foundNum > 1) duplicateNum = j;
				} 
			}
			if (foundNum > 1) {
				//System.out.println("gSP�� �ߺ��� ��:");
				//System.out.println("gSP= " + crSPVec.elementAt(duplicateNum).intValue() + ",  " + 
				//		"Gene Name= " + (String)gNameVec.elementAt(duplicateNum));
				duplicate = true;			
			}
			if (foundNum == 0) {                                              //SP���� �߰ߵ��� ���� ��� EP ��
				boolean found = false;
				int oEP = oEPVec.elementAt(i).intValue();
				duplicateNum = 0;
				for (int j = 0; j < crEPVec.size(); j++){
					if (oEP == crEPVec.elementAt(j).intValue()) {
						found = true;
						cSPVec.add(crSPVec.elementAt(j).intValue());     
						cEPVec.add(crEPVec.elementAt(j).intValue());
						//egStrandVec.add(gStrandVec.elementAt(j).intValue());
						if (funcClassVec.elementAt(i).length() >= 8) {
							str = (String)funcClassVec.elementAt(i);
							cFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
						}else{
							cFuncCatVec.add("X");
						}
						
						//System.out.println("oEP ���Ͽ� ã�� ��:");
						//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
						//System.out.println("gSP= " + crSPVec.elementAt(j).intValue() + ", " + "gEP= " + crEPVec.elementAt(j).intValue());
						break;
					}
				}
				if (!found) {				
					//System.out.println("oSP�� ������ gSP���� ���� ��:");
					//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
				}
			}
		}
		//if (!duplicate) System.out.println("oSP�� �ߺ��� ���� �߰ߵ��� �ʾҽ��ϴ�.");
		//System.out.println("oSPVec.size= " + oSPVec.size() + ",  " + "cSPVec.size= " + cSPVec.size());
		
		duplicate = false;
		for (int i = 0; i < oEPVec.size(); i++){                         //  SP�� �Բ� EP�� ��!
			int oEP = oEPVec.elementAt(i).intValue();
			int foundNum = 0;
			int duplicateNum = 0;
			for (int j = 0; j < crEPVec.size(); j++){
				if (oEP == crEPVec.elementAt(j).intValue()) {
					foundNum = foundNum +1;
					if (foundNum > 1) duplicateNum = j;
				} 
			}
			if (foundNum > 1) {
				System.out.println("gEP�� �ߺ��� ��:");
				System.out.println("gEP= " + crEPVec.elementAt(duplicateNum).intValue() + ",  " + 
						"Gene Name= " + (String)geneNameVec.elementAt(duplicateNum));
				duplicate = true;			
			}
			if (foundNum == 0) {
				//System.out.println("oEP���� ������ gEP ���� ���� ��:");
				//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
			}
		}
		//if (!duplicate) System.out.println("oEP�� �ߺ��� ���� �߰ߵ��� �ʾҽ��ϴ�.");
		//System.out.println("oSPVec.size= " + oSPVec.size() + ",  " + "cSPVec.size= " + cSPVec.size());

	}       
	
	
	public String getNumStr(int num){
		
		String numStr = "";
		//System.out.format("%08d%n", num);
		int numLength;
		numLength = (int) (Math.log(num)/Math.log(10)) + 1;
		//System.out.println(numLength);

		for (int i = 0; i < 8 - numLength; i++) {
			numStr = numStr + "0";
		}
		numStr = numStr + num;
		//System.out.println(numStr);
		
		return numStr;
	}
	
	public void makeBrickDB() {
		
		for (int i = 0; i < crSPVec.size(); i++) {      // making minimal genome,   ������ �޼ҵ�� ������.
			String brickSeq = "";
			int nSpace = 0;
			int brickSP = 0;
			int brickEP = 0;
			if (i == 0) {
				nSpace = crSPVec.elementAt(i).intValue() - 1 + seqLen - crEPVec.elementAt(crSPVec.size()-1).intValue() - 1 - 1;
				brickSeq = sSeq.substring(crEPVec.elementAt(crEPVec.size()-1).intValue() - 1 + 1) + 
				sSeq.substring(0, crSPVec.elementAt(i).intValue() - 1);
				brickSP = (crEPVec.elementAt(crEPVec.size()-1).intValue() - 1 + 1) - seqLen;
				brickEP = crSPVec.elementAt(i).intValue() - 1;
			}
			if (i > 0 ) { 
				nSpace = crSPVec.elementAt(i).intValue() - 1 - crEPVec.elementAt(i-1).intValue() - 1 - 1;

				if (nSpace > 0) {
					brickSeq = sSeq.substring(crEPVec.elementAt(i-1).intValue() - 1 + 1, (crSPVec.elementAt(i).intValue()) - 1);
					brickSP = (crEPVec.elementAt(i-1).intValue() - 1 + 1);
					brickEP = crSPVec.elementAt(i).intValue() - 1 - 1;
				}
				if (nSpace == 0)
					brickSeq = "";
				if (nSpace < 0)
					brickSeq = "";
					//brickSeq = sSeq.substring(crSPVec.elementAt(i).intValue() - 1, crEPVec.elementAt(i-1).intValue() - 1 + 1);	//overlap seq ��Ÿ��	
			}
			nSpaceVec.add(nSpace);
			brickSeqVec1.add(brickSeq);
			brickIDVec1.add("IC" + getNumStr(i+1)); 
			brickSPVec.add(brickSP);
			brickEPVec.add(brickEP);
		}

		try {
			RandomAccessFile raf = new RandomAccessFile("ICBrickDB.fas", "rw");
			raf.seek(raf.length()); 
			for (int i = 0; i < brickSeqVec1.size(); i++) {
				nLoc = String.valueOf(brickSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(brickEPVec.elementAt(i).intValue());
				int nSpace = nSpaceVec.elementAt(i).intValue();
				raf.writeBytes(">" + brickIDVec1.elementAt(i) + ", " + nLoc + ", " + nSpace);
				raf.writeBytes("\n");
				raf.writeBytes(brickSeqVec1.elementAt(i));
				raf.writeBytes("\n");
			}
			raf.close();
			
			for (int i = 0; i < crSPVec.size(); i++) { 
				brickSeqVec2.add(sSeq.substring(crSPVec.elementAt(i).intValue()-1, (crEPVec.elementAt(i).intValue() - 1 + 1)));
				brickIDVec2.add("CR" + getNumStr(i+1));           // coding region
			}
			
			raf = new RandomAccessFile("CRBrickDB.fas", "rw");
			raf.seek(raf.length()); 
			for (int i=0; i < brickSeqVec2.size(); i++) {
				if (gStrandVec.elementAt(i).intValue() == 1){
					nLoc = String.valueOf(crSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(crEPVec.elementAt(i).intValue());
				}else if (gStrandVec.elementAt(i).intValue() == -1){
					nLoc = "complement(" + String.valueOf(crSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(crEPVec.elementAt(i).intValue()) + ")";
				}
				raf.writeBytes(">" + brickIDVec2.elementAt(i) + ", " + nLoc + ", " + brickSeqVec2.elementAt(i).length());
				raf.writeBytes("\n");
				raf.writeBytes(brickSeqVec2.elementAt(i));
				raf.writeBytes("\n");
			}
			raf.close();
		 } catch (IOException e) {
		 }
	}
	
	public void getEGseq(){                                //essential gene egBricks ���� ����

		int assembledBrickLen = 0;
		for (int i = 0; i < egSPVec.size(); i++) {      // making minimal genome,   ������ �޼ҵ�� ������.

			int upstream = 50;                     //Vector�� ���鶧 ���� �ӽ� ���!!!!!!!! ���� �Ŀ��� ����!!!!!!!
			int downstream = 50;

			if (i>0 && i < egSPVec.size()-1) {
				int nSpacer = egSPVec.elementAt(i).intValue() - egEPVec.elementAt(i-1).intValue() - 1;
				if (nSpacer < downstream + upstream) {
					upstream = nSpacer/2 + nSpacer%2;                            // ������ ���� Ȧ����  upstream �ʿ� �ϳ� �߰�, ������ ��쵵 �ذ��
					System.out.println(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
							", " +  "nSpacer =" + nSpacer + ", " + "upstream =" + upstream);
				}
				nSpacer = egSPVec.elementAt(i+1).intValue() - egEPVec.elementAt(i).intValue() - 1;
				if (nSpacer < downstream + upstream) {
					downstream = nSpacer/2;
					System.out.println(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
							", " +  "nSpacer =" + nSpacer + ", " + "downstream =" + downstream);
				}
			}
	
			if (i == egSPVec.size()-1)
				if (seqLen - egSPVec.elementAt(i).intValue() - 1 < upstream)
					downstream = seqLen - egSPVec.elementAt(i).intValue() - 1;

			if (i == 0) {
				if (egSPVec.elementAt(i).intValue() - 1 < upstream)
					upstream = egSPVec.elementAt(i).intValue() - 1;
			}
			
			int upStrP = egSPVec.elementAt(i) - upstream;                          //Vector�� ���鶧 ���� �ӽ� ���!!!!!!!! ���� �Ŀ��� ����!!!!!!!
			int downStrP = egEPVec.elementAt(i) + downstream;
			
			mgSPVec.add((egSPVec.elementAt(i)-upStrP+1) + assembledBrickLen);       // (egSPVec.elementAt(i)-upStrP+1)= inBrickSP
			mgEPVec.add((egEPVec.elementAt(i)-upStrP+1) + assembledBrickLen);                // (egEPVec.elementAt(i)-upStrP+1)= inBrickEP
			
			assembledBrickLen = assembledBrickLen + (downStrP-upStrP+1);                            // (downStrP-upStrP+1) = �ٷ� ���� egBrick ũ��
			
			SymbolList sym = null;
			try {
				sym = richSeq.subList(upStrP, downStrP);         //�������� �Ǵ� ���, ������ ���, circular
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			egBrickSym.add(sym);
		}				
	}
	
	public String joinEGseq() throws IllegalSymbolException{        // �ʼ� ������ ������ ������ ����, mgSeq ����
		
		String mgSeq = "";
		String str = "";
		int strLength = 0;
		for (int i = 0; i<egBrickSym.size(); i++) {                
			str = egBrickSym.elementAt(i).seqString();
			strLength = strLength + str.length();
			mgSeq = mgSeq + str;
		}
		Sequence seq = DNATools.createDNASequence(mgSeq, "mgSeq");
		
		System.out.println("mgSeq.length= " + seq.length() + ", " + "strLength= " + strLength);
		
		return mgSeq;	
	}
	
	public void getEgFeature(String sFile) throws IllegalSymbolException {
        
	    BufferedReader br;
		BufferedWriter bw = null;
		Vector<Integer> gLineNumVec = new Vector<Integer>();
		Vector<Integer> egLineNumVec = new Vector<Integer>();
		Vector<Integer> egIndexVec = new Vector<Integer>();
		int oriLineNum = 0;
		boolean complement = false;
/*		
		Dbmanager dm = new Dbmanager();         //�ʼ� ������ ���� DB���� ��������, oSPVec ����
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		//funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
*/		
		getEgData();
		compareEgGenome();                       //cSPVEC ����
		
		try {                                  
			br = new BufferedReader(new FileReader(sFile));
			
		    String line;
		    int lineNum = 0;
		    int gIdx = 0;
		    int gSPNum = 0;
		    while ((line = br.readLine()) != null) {       // �ʼ� �������� �� ��ȣ�� �˱� ���Ͽ�
		    	lineNum+=1;
		    	if (line.startsWith("     gene")){    
		    		gLineNumVec.add(lineNum);                   //��� ��������  ���� �� ��ȣ�� ���Ϳ� ����
			    	gSPNum = crSPVec.elementAt(gIdx).intValue();
					for (int i = 0; i < cSPVec.size(); i++){            //cSPVec �� ������ ������  gSPVec�� ������� �����ϱ� ���Ͽ�                 
						  if ((cSPVec.elementAt(i)).intValue() == gSPNum) {
			    			  egLineNumVec.add(lineNum);     // 687 ��.   689�� �´µ� 2����?????????????????????????????????????????????????????
			    			  egIndexVec.add(gIdx);
			    			  egSPVec.add(gSPNum);
			    			  egEPVec.add(crEPVec.elementAt(gIdx).intValue());
			    			  egStrandVec.add(gStrandVec.elementAt(gIdx).intValue());
			    			  //System.out.println(cFuncCatVec.elementAt(i));
			    			  egFuncCatVec.add((String)cFuncCatVec.elementAt(i));
			    			  break;
			    		  }
					}
					gIdx++;
		    	}
				if (line.startsWith("ORIGIN")) oriLineNum = lineNum;
		    }
			br.close();
			System.out.println("gLineNumVec.size= " + gLineNumVec.size() + ", " + "egLineNumVec.size= " + egLineNumVec.size() +
					", " + "egIndexVec.size= " + egIndexVec.size() + ", " + "egSPVec.size= " + egSPVec.size());

			br = new BufferedReader(new FileReader(sFile));
			bw = new BufferedWriter(new FileWriter(tFile));
			
			String nLocus;
			String nLength;
			String nDate;
			String nLoc;
			
			getEGseq();                              //mgSPVec ����
			designerSeq = joinEGseq(); 
			
			line="";
			lineNum = 0;
			try {
				line = br.readLine(); lineNum++;  //file pointer��  i�� ��Ȯ�ϰ� ��ġ���Ѿ� ��!!!!!!!!
//				oLocus = getLocusName();
				nLocus = tFile.getName();
				StringTokenizer tok = new StringTokenizer(nLocus, ".");
				nLocus = tok.nextToken();
/*				oDate = "";
				tok = new StringTokenizer(line);
				while (tok.hasMoreTokens()) {
			    	  oDate = tok.nextToken();
				}
*/			    
			    Date date = new Date();                      // Get today's date
			    Format formatter;
			    formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
			    nDate = formatter.format(date);

				nLength = String.valueOf(designerSeq.length());
			
				int nSpace = 28 - nLocus.length() -  nLength.length();
				String sSpace = ""; 
				for (int i=0; i < nSpace; i++)
					sSpace = sSpace + " ";

				String str = line.substring(0,12) + nLocus + sSpace + nLength
				+ line.substring(40,68)+ nDate;

				bw.write(str); bw.write("\n"); 
				
				line = br.readLine(); lineNum++;  // DEFINITION ���� ����   
//				line.replace("complete", "minimal");
				
				str = "DEFINITION  ";
				int num = 0;
				tok = new StringTokenizer(line.substring(12));
				int tc = tok.countTokens();
				while (tok.hasMoreTokens()) {
					num = num+1;
					String oDef = tok.nextToken();			    	 			    	
			    	if(oDef.charAt(oDef.length()-1) == ','){
		    			oDef = nLocus + ",";
			    	}
	    	  
			    	if(num < tc){			    	  
			    		str = str + oDef + " ";
			    	}else
			    		str = str + oDef;		
  				}				
				bw.write(str); bw.write("\n"); 
				
				line = br.readLine(); lineNum++;  //ACCESSION ���� ����  
				str = line.substring(0,12) + nLocus;
				bw.write(str); bw.write("\n"); 
	
				str = "";
				while (lineNum < gLineNumVec.elementAt(0).intValue()-1){      //Features, source ���� ��� �����ϴ� ���, -1 �� �־�� ��.
					line = br.readLine(); lineNum++;
					
					if (line.startsWith("SOURCE")) {
						String tokSource = "";
						str = "SOURCE      ";
						num = 0;
						tok = new StringTokenizer(line.substring(12));
						tc = tok.countTokens();
						while (tok.hasMoreTokens()) {
							num = num+1;
							tokSource = tok.nextToken();			    	 			    	
					    	if(num < tc){			    	  
					    		str = str + tokSource + " ";
					    	}else
					    		str = str + nLocus;		
		  				}
						line = str;
					}
/*					
					if (line.startsWith("     source")) {                        //source �� ������ �ݺ��ؼ� ������ ��찡 ����
						str = "     source          ";
						str = str + "1.." + String.valueOf(designerSeq.length());
						line = str;
					}
*/					
					bw.write(line); bw.write("\n");
				}
				
				nLoc = "";
				int foundNum = 0;
				int mpNum = 0;
				while (lineNum < oriLineNum){                                        //�ʼ� �������� feature ����
					line = br.readLine(); lineNum++;
					boolean found = false;
					boolean flag =false;
		       		for (int j = 0; j < egIndexVec.size(); j++){
		       			int k = egIndexVec.elementAt(j).intValue()+1;                   //ArrayIndexOutOfBoundsException �߻��� ���ϱ� ����
		       			if (k > gLineNumVec.size()) {k = k-1; flag = true;}          //ArrayIndexOutOfBoundsException �߻��� ���ϱ� ����
				       	if (lineNum >= egLineNumVec.elementAt(j).intValue()) {
				       		if (lineNum < gLineNumVec.elementAt(k).intValue()){
				       		  found = true;
				       		  break;
				       		}
				       	}
					    if (flag) {                                             //������ �����ڰ� �ʼ��� ���
					       	if (lineNum >= egLineNumVec.elementAt(j).intValue()) {
					       		if (lineNum < oriLineNum){
					       		  found = true;
					       		  break;
					       		}
					       	}
				    	}
			       	}
				    if (found) {
				    	if (line.startsWith("     gene")) {
							nLoc = "";
							foundNum = foundNum + 1;
							if (egStrandVec.elementAt(foundNum-1).intValue() == 1){
									nLoc = String.valueOf(mgSPVec.elementAt(foundNum-1).intValue()) +
									".." + String.valueOf(mgEPVec.elementAt(foundNum-1).intValue());
								}else if (egStrandVec.elementAt(foundNum-1).intValue() == -1){
									complement = true;								
									nLoc = "complement(" + String.valueOf(mgSPVec.elementAt(foundNum-1).intValue()) +
									".." + String.valueOf(mgEPVec.elementAt(foundNum-1).intValue()) + ")";
								}
							str = "     gene            ";
							str = str + nLoc;
							line = str;
						}
				    	
						if (line.startsWith("     CDS")) {
							str = "     CDS             ";
							str = str + nLoc;
							line = str;
				    	}else if (line.startsWith("     rRNA")) {
							str = "     rRNA            ";
							str = str + nLoc;
							line = str;
						}else if (line.startsWith("     tRNA")) {
							str = "     tRNA            ";	
							str = str + nLoc;
							line = str;
						}else if (line.startsWith("     ncRNA")) {
							str = "     ncRNA           ";
							str = str + nLoc;
							line = str;
						}else if (line.startsWith("     tmRNA")) {
							str = "     tmRNA           ";
							str = str + nLoc;
							line = str;
						}	
						if (line.startsWith("     mat_peptide")) {
							if (!complement){
								//nLoc = String.valueOf(mgmpSPVec.elementAt(mpNum).intValue()) +
								//".." + String.valueOf(mgmpEPVec.elementAt(mpNum).intValue());
								nLoc = String.valueOf(mgSPVec.elementAt(foundNum-1).intValue() + 3) +                    //���� ��!!!!!
								".." + String.valueOf(mgEPVec.elementAt(foundNum-1).intValue() - 3);
							}else{
								//nLoc = "complement(" + String.valueOf(mgmpSPVec.elementAt(mpNum).intValue()) +
								//".." + String.valueOf(mgmpEPVec.elementAt(mpNum).intValue()) + ")";
								nLoc = "complement(" + String.valueOf(mgSPVec.elementAt(foundNum-1).intValue() + 3) +
								".." + String.valueOf(mgEPVec.elementAt(foundNum-1).intValue() - 3) + ")";
								
							}
							str = "     mat_peptide     ";
							str = str + nLoc;
							line = str;	
							//mpNum = mpNum + 1;
						}
						
				    	bw.write(line);
				    	bw.write("\n");
				    }
		       	}
                    
		        if (lineNum == oriLineNum) {br.readLine(); lineNum++; bw.write(line); bw.write("\n");}    // ORIGIN ����
				br.close();

				str = "";
		        String str1;
		        int i = 0;
				while (i < designerSeq.length() / 60) {                                                
					int k = 60 * i + 1;                             //1�� �� 60 nt,   // ���� ����
					str = "";
					if (k-1 < 10) str = "        ";
					else if (k-1 < 100) str = "       ";
					else if (k-1 < 1000) str = "      ";
					else if (k-1 < 10000) str = "     ";
					else if (k-1 < 100000) str = "    ";
					else if (k-1 < 1000000) str = "   ";
					
					str = str + String.valueOf(k) + " ";
					str1 = "";
					str1 = designerSeq.substring(k-1, k-1+60);
					str = str + str1;
					
					bw.write(str); bw.write("\n");
					i++;
				}
				if (designerSeq.length() % 60 != 0) {                //�������� �� ������ �ٿ�
					int k = 60 * i + 1;
					str = "";
					if (k-1 < 10) str = "        ";
					if (k-1 < 100) str = "       ";
					if (k-1 < 1000)	str = "      ";
					if (k-1 < 10000) str = "     ";
					if (k-1 < 100000) str = "    ";
					if (k-1 < 1000000) str = "   ";
					
					str = str + String.valueOf(k) + " ";
					str1 = "";
					str1 = designerSeq.substring(k-1);
					str = str + str1;
					
					bw.write(str); bw.write("\n");
					bw.write("//");bw.write("\n");				
				}
				bw.close();
				
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(bw != null){
						try{
							bw.close();
						}catch (IOException ioe){
							ioe.printStackTrace();
					    }
				    }
			}
		}catch(IOException e) {
			  e.printStackTrace();
		}
	}
	
	void detectError(){
		
		for (int i = 0; i < crSPVec.size(); i++){        // cdsSP. trnaSP, rRNASP, ncrnaSP�� gSP �� ���� ����.
			int gSP = crSPVec.elementAt(i).intValue();
			boolean found =false;
			for (int j = 0; j < cdsSPVec.size(); j++){
				if (gSP == cdsSPVec.elementAt(j).intValue()) {
					found =true;break;}	}
		
			for (int j = 0; j < trnaSPVec.size(); j++){		
				if (gSP == trnaSPVec.elementAt(j).intValue()){
					found =true;break;}	}
		
			for (int j = 0; j < rrnaSPVec.size(); j++){		
				if (gSP == rrnaSPVec.elementAt(j).intValue()){		
					found =true;break;} }
		
			for (int j = 0; j < ncrnaSPVec.size(); j++){		
				if (gSP == ncrnaSPVec.elementAt(j).intValue()){	
					found =true;break;} }
			
			for (int j = 0; j < tmrnaSPVec.size(); j++){		
				if (gSP == tmrnaSPVec.elementAt(j).intValue()){	
					found =true;break;} }
	
			if (!found){
				System.out.println(crSPVec.elementAt(i).intValue());
				boolean flag = false;
				for (int j = 0; j < egSPVec.size(); j++){		
					if (gSP == egSPVec.elementAt(j).intValue()){	
						flag =true;break;}
					if (flag)System.out.println(egSPVec.elementAt(j).intValue());
				}
			}
		}
	}
	
	
	void makeMGgbk() {
/*	
		try {
			RandomAccessFile raf = new RandomAccessFile(tFile, "rw");

			for (int i=0; i < pointerGeneVec.size(); i++) {
				raf.seek(pointerStrVec.elementAt(i).intValue()); 
				raf.writeBytes((String)strVec.elementAt(i));			
			}
			raf.close();
	
		 } catch (IOException e) {
		 }
 */
	}
	
	public boolean checkFile(File file){
		if(file.exists()){
			System.out.println("������ ������ �����մϴ�. ���� ������ �����ϰ� ������ �����Ͻðڽ��ϱ�?(y/n)");
			try{
				int ch = System.in.read();
				if((char)ch == 'n')
					return false;
			}catch(IOException ioe){
			}			
		}		
		
		return true;
	}
	
	public void createFile(File file){			
		try{			
			if(file.createNewFile()){
				System.out.println("������ ���� ����ϴ�.");
			} else
				System.out.println("������ ������ �����߽��ϴ�.");
		}catch(IOException ioe){
		}
	}
	
	public static void main(String[] args) throws IllegalSymbolException {

	    if (args.length != 2){
		      System.out.println("Usage: java ExtractInformation <file in Genbank format>");
		      System.exit(1);
		    }else{
//		    	sFile = new File(args[0]);
		    	tFile = new File(args[1]);
		}
/*		
		FileIO fileIO = new FileIO();
		if(fileIO.checkFile(tFile)){
			fileIO.createFile(tFile);
		}
*/		
		String sFile = args[0];
		MgFileCreatorV2 mgFileCreator = new MgFileCreatorV2(sFile, "sample.gb");
		mgFileCreator.getOrgName(sFile);
		mgFileCreator.makeBrickDB();

		mgFileCreator.detectError();
//		mgFileCreator.getNumStr(int num);
		mgFileCreator.getEgFeature(sFile);
//		mgFileCreator.makeMGgbk();
		
		System.out.println("orgName= " + orgName);
		System.out.println("LOCUS= " + mgFileCreator.richSeq.getName() + ", " + "GenomeSeqLen= " + mgFileCreator.richSeq.length());
		System.out.println("crSPVec.size= " + mgFileCreator.crSPVec.size() + ", " + "cdsSPVec.size= " + mgFileCreator.cdsSPVec.size() + ", " +
				"rrnaSPVec.size= " + mgFileCreator.rrnaSPVec.size() + ", " + "trnaSPVec.size= " + mgFileCreator.trnaSPVec.size()
				 + ", " + "ncrnaSPVec.size= " + mgFileCreator.ncrnaSPVec.size() + ", " + "tmrnaSPVec.size= " + mgFileCreator.tmrnaSPVec.size());
		System.out.println("oSPVec.size= " + mgFileCreator.oSPVec.size() + ", " + "cSPVec.size= " + mgFileCreator.cSPVec.size() + ", " +
				"egSPVec.size= " + mgFileCreator.egSPVec.size() + ", " + "mgSPVec.size= " + mgFileCreator.mgSPVec.size() + 
				", " + "funcClassVec.size= " + mgFileCreator.funcClassVec.size() + ", " + "egFuncCatVec.size= " + mgFileCreator.egFuncCatVec.size());
		System.out.println("geneNameVec.size= " + mgFileCreator.geneNameVec.size() + ", " + "gGeneNameVec.size= " + mgFileCreator.gGeneNameVec.size() + ", " + 
				"locusTagVec.size= " + mgFileCreator.locusTagVec.size() + ", " +
				"gStrandVec.size= " + mgFileCreator.gStrandVec.size());
		System.out.println("meSPVec.size= " + mgFileCreator.meSPVec.size() + ", " +  "rrSPVec.size= " + mgFileCreator.rrSPVec.size() +
				", " + "mpSPVec.size= " + mgFileCreator.mpSPVec.size()  +
				", " + "rbsSPVec.size= " + mgFileCreator.rbsSPVec.size() + ", " + "pribnowSPVec.size= " + mgFileCreator.pribnowSPVec.size() + 
				", " + "recognitionSPVec.size= " + mgFileCreator.recognitionSPVec.size());
		System.out.println("designerSeqLen= " + designerSeq.length());
	}
}

