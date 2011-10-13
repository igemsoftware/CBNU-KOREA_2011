/* 1-Jan-2011 김영창
 * 미생물 genome gb 파일과 필수 유전자 db를 이용하여 minimal genome을 만들고 gb 파일을 생성.
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

	 Vector<Integer> crSPVec = new Vector<Integer>();       //genome 상의 모든 유전자 시작 위치
	 Vector<Integer> crEPVec = new Vector<Integer>();       //genome 상의 모든 유전자 끝 위치
	 
	 Vector<Integer> cdsSPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 시작 위치
	 Vector<Integer> cdsEPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 끝 위치
	 Vector<Integer> rrnaSPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 시작 위치
	 Vector<Integer> rrnaEPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 끝 위치
	 Vector<Integer> trnaSPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 시작 위치
	 Vector<Integer> trnaEPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 끝 위치
	 Vector<Integer> ncrnaSPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> ncrnaEPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 끝 위치
	 Vector<Integer> tmrnaSPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> tmrnaEPVec = new Vector<Integer>();  
	 
	 Vector<Integer> rrSPVec = new Vector<Integer>();       //genome 상의repeat_region 위치
	 Vector<Integer> rrEPVec = new Vector<Integer>(); 
	 Vector<Integer> mpSPVec = new Vector<Integer>();       //genome 상의 mat_peptide 위치
	 Vector<Integer> mpEPVec = new Vector<Integer>();  
	 Vector<Integer> meSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> meEPVec = new Vector<Integer>();  
	 Vector<Integer> rbsSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> rbsEPVec = new Vector<Integer>();
	 Vector<Integer> pribnowSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> pribnowEPVec = new Vector<Integer>();
	 Vector<Integer> recognitionSPVec = new Vector<Integer>();       //genome 상의 mobile_element 위치
	 Vector<Integer> recognitionEPVec = new Vector<Integer>();

	 Vector<String> geneNameVec = new Vector<String>();   //genome 상의 모든 유전자 이름
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> gGeneNameVec = new Vector<String>();
	 Vector<Integer> gStrandVec = new Vector<Integer>();    //genome 상의 모든 유전자 방향 
	 
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager에서 가져 온 데이터
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene의 본래의 location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	//Vector<String> funcDefVec = new Vector<String>();

	Vector<Integer> cSPVec = new Vector<Integer>();       //essential gene database의 update !!!!!!!!!!!
	Vector<Integer> cEPVec = new Vector<Integer>();
	Vector<Integer> egSPVec = new Vector<Integer>();       //essential gene database의 유전자 순서를 genome 상의 순서와 일치시키기 위해 !//db에 저장하자.
	Vector<Integer> egEPVec = new Vector<Integer>();
	Vector<Integer> egStrandVec = new Vector<Integer>();
	Vector<Integer> egmpSPVec = new Vector<Integer>();       
	Vector<Integer> egmpEPVec = new Vector<Integer>();
	
	Vector<Integer> mgSPVec = new Vector<Integer>();       //MG에서의 new location, 디자인 환경에 따라 가변적인 자료임
	Vector<Integer> mgEPVec = new Vector<Integer>();
	Vector<Integer> mgmpSPVec = new Vector<Integer>();       //MG에서의 new location
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
		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        crSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    crEPVec.add(loc.getMax());
		    gStrandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()은 +, -로
	    
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
		
		ff = new FeatureFilter.ByType("tRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        trnaSPVec.add(loc.getMin());                                    // tRNA 정보 추출
		    trnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("rRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rrnaSPVec.add(loc.getMin());                                    // rRNA 정보 추출
		    rrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("ncRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        ncrnaSPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    ncrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("tmRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the tmRNA features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        tmrnaSPVec.add(loc.getMin());                                    // tmRNA 정보 추출
		    tmrnaEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("repeat_region");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rrSPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    rrEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("mat_peptide");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        mpSPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    mpEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("mobile_element");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        meSPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    meEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("RBS");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rbsSPVec.add(loc.getMin());                                    // RBS 정보 추출
		    rbsEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("-10_signal");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        pribnowSPVec.add(loc.getMin());                                    // RBS 정보 추출
		    pribnowEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("-35_signal");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        recognitionSPVec.add(loc.getMin());                                    // RBS 정보 추출
		    recognitionEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("CDS");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features
	        RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        cdsSPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    cdsEPVec.add(loc.getMax());
		}
		
	}
		
	Vector<String> getgNameVec() {                 // 유전자 이름 목록 반환
		return geneNameVec;
	}

	Vector<Integer> getgSP() {                                 // 유전자 시작 위치 목록 반환
		return crSPVec;
	}
	
	Vector<Integer> getgEP() {                                 // 유전자 끝위치 목록반환
		return crEPVec;
	}
	
	String getGeneName(int i) {                       // 유전자 이름 반환
		return (String)(geneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // 유전자 개수 반환
		return geneNameVec.size();
	}
	
	int getgSP(int i) {                        // 유전자 시작 위치 반환
		return (crSPVec.elementAt(i));
	}
	
	int getgEP(int i) {                        // 유전자 끝위치 반환
		return (crEPVec.elementAt(i));
	}
/*	
	int getcSP(int i) {                        // 유전자 시작 위치 반환
		return (cSPVec.elementAt(i));
	}
*/	
	int getSeqLen() {                          // 서열 길이 반환
		return seqLen;
	}
	
	int getStrand(int i) {                     // 유전자 방향      +1, -1
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
//*************************** 이 블록은 organism 이름을 추출하는 과정을 보여주는 코드인데 바이오자바를 이용하여 간단하게 줄여보세요!	
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
//****************************************************************************   StringTokenizer를 이용하지 마세요!				
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
		funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
	}
	
	void compareEgGenome(){                    //필수 유전자 정보와 비교하기 위하여

		boolean duplicate = false;
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
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
							cFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
						}else{
							cFuncCatVec.add("X");
						}
					}
					if (foundNum > 1) duplicateNum = j;
				} 
			}
			if (foundNum > 1) {
				//System.out.println("gSP에 중복된 것:");
				//System.out.println("gSP= " + crSPVec.elementAt(duplicateNum).intValue() + ",  " + 
				//		"Gene Name= " + (String)gNameVec.elementAt(duplicateNum));
				duplicate = true;			
			}
			if (foundNum == 0) {                                              //SP에서 발견되지 않을 경우 EP 비교
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
							cFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
						}else{
							cFuncCatVec.add("X");
						}
						
						//System.out.println("oEP 비교하여 찾은 것:");
						//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
						//System.out.println("gSP= " + crSPVec.elementAt(j).intValue() + ", " + "gEP= " + crEPVec.elementAt(j).intValue());
						break;
					}
				}
				if (!found) {				
					//System.out.println("oSP에 있으나 gSP에는 없는 것:");
					//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
				}
			}
		}
		//if (!duplicate) System.out.println("oSP에 중복된 것은 발견되지 않았습니다.");
		//System.out.println("oSPVec.size= " + oSPVec.size() + ",  " + "cSPVec.size= " + cSPVec.size());
		
		duplicate = false;
		for (int i = 0; i < oEPVec.size(); i++){                         //  SP와 함께 EP도 비교!
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
				System.out.println("gEP에 중복된 것:");
				System.out.println("gEP= " + crEPVec.elementAt(duplicateNum).intValue() + ",  " + 
						"Gene Name= " + (String)geneNameVec.elementAt(duplicateNum));
				duplicate = true;			
			}
			if (foundNum == 0) {
				//System.out.println("oEP에는 있으나 gEP 에는 없는 것:");
				//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
			}
		}
		//if (!duplicate) System.out.println("oEP에 중복된 것은 발견되지 않았습니다.");
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
		
		for (int i = 0; i < crSPVec.size(); i++) {      // making minimal genome,   별개의 메소드로 나누자.
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
					//brickSeq = sSeq.substring(crSPVec.elementAt(i).intValue() - 1, crEPVec.elementAt(i-1).intValue() - 1 + 1);	//overlap seq 나타냄	
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
	
	public void getEGseq(){                                //essential gene egBricks 서열 추출

		int assembledBrickLen = 0;
		for (int i = 0; i < egSPVec.size(); i++) {      // making minimal genome,   별개의 메소드로 나누자.

			int upstream = 50;                     //Vector를 만들때 까지 임시 사용!!!!!!!! 만든 후에는 제거!!!!!!!
			int downstream = 50;

			if (i>0 && i < egSPVec.size()-1) {
				int nSpacer = egSPVec.elementAt(i).intValue() - egEPVec.elementAt(i-1).intValue() - 1;
				if (nSpacer < downstream + upstream) {
					upstream = nSpacer/2 + nSpacer%2;                            // 간격이 만약 홀수면  upstream 쪽에 하나 추가, 음수인 경우도 해결됨
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
			
			int upStrP = egSPVec.elementAt(i) - upstream;                          //Vector를 만들때 까지 임시 사용!!!!!!!! 만든 후에는 수정!!!!!!!
			int downStrP = egEPVec.elementAt(i) + downstream;
			
			mgSPVec.add((egSPVec.elementAt(i)-upStrP+1) + assembledBrickLen);       // (egSPVec.elementAt(i)-upStrP+1)= inBrickSP
			mgEPVec.add((egEPVec.elementAt(i)-upStrP+1) + assembledBrickLen);                // (egEPVec.elementAt(i)-upStrP+1)= inBrickEP
			
			assembledBrickLen = assembledBrickLen + (downStrP-upStrP+1);                            // (downStrP-upStrP+1) = 바로 앞의 egBrick 크기
			
			SymbolList sym = null;
			try {
				sym = richSeq.subList(upStrP, downStrP);         //겹쳐지게 되는 경우, 말단의 경우, circular
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			egBrickSym.add(sym);
		}				
	}
	
	public String joinEGseq() throws IllegalSymbolException{        // 필수 유전자 개개의 서열을 연결, mgSeq 생성
		
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
		Dbmanager dm = new Dbmanager();         //필수 유전자 정보 DB에서 가져오기, oSPVec 생성
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		//funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
*/		
		getEgData();
		compareEgGenome();                       //cSPVEC 생성
		
		try {                                  
			br = new BufferedReader(new FileReader(sFile));
			
		    String line;
		    int lineNum = 0;
		    int gIdx = 0;
		    int gSPNum = 0;
		    while ((line = br.readLine()) != null) {       // 필수 유전자의 행 번호를 알기 위하여
		    	lineNum+=1;
		    	if (line.startsWith("     gene")){    
		    		gLineNumVec.add(lineNum);                   //모든 유전자의  시작 행 번호를 벡터에 저장
			    	gSPNum = crSPVec.elementAt(gIdx).intValue();
					for (int i = 0; i < cSPVec.size(); i++){            //cSPVec 의 유전자 순서를  gSPVec의 순서대로 수정하기 위하여                 
						  if ((cSPVec.elementAt(i)).intValue() == gSPNum) {
			    			  egLineNumVec.add(lineNum);     // 687 개.   689가 맞는데 2개는?????????????????????????????????????????????????????
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
			
			getEGseq();                              //mgSPVec 생성
			designerSeq = joinEGseq(); 
			
			line="";
			lineNum = 0;
			try {
				line = br.readLine(); lineNum++;  //file pointer와  i를 정확하게 일치시켜야 함!!!!!!!!
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
				
				line = br.readLine(); lineNum++;  // DEFINITION 줄을 복사   
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
				
				line = br.readLine(); lineNum++;  //ACCESSION 줄을 복사  
				str = line.substring(0,12) + nLocus;
				bw.write(str); bw.write("\n"); 
	
				str = "";
				while (lineNum < gLineNumVec.elementAt(0).intValue()-1){      //Features, source 까지 모두 복사하는 경우, -1 해 주어야 함.
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
					if (line.startsWith("     source")) {                        //source 가 여러번 반복해서 나오는 경우가 있음
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
				while (lineNum < oriLineNum){                                        //필수 유전자의 feature 복사
					line = br.readLine(); lineNum++;
					boolean found = false;
					boolean flag =false;
		       		for (int j = 0; j < egIndexVec.size(); j++){
		       			int k = egIndexVec.elementAt(j).intValue()+1;                   //ArrayIndexOutOfBoundsException 발생을 피하기 위해
		       			if (k > gLineNumVec.size()) {k = k-1; flag = true;}          //ArrayIndexOutOfBoundsException 발생을 피하기 위해
				       	if (lineNum >= egLineNumVec.elementAt(j).intValue()) {
				       		if (lineNum < gLineNumVec.elementAt(k).intValue()){
				       		  found = true;
				       		  break;
				       		}
				       	}
					    if (flag) {                                             //마지막 유전자가 필수인 경우
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
								nLoc = String.valueOf(mgSPVec.elementAt(foundNum-1).intValue() + 3) +                    //수정 요!!!!!
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
                    
		        if (lineNum == oriLineNum) {br.readLine(); lineNum++; bw.write(line); bw.write("\n");}    // ORIGIN 복사
				br.close();

				str = "";
		        String str1;
		        int i = 0;
				while (i < designerSeq.length() / 60) {                                                
					int k = 60 * i + 1;                             //1줄 당 60 nt,   // 서열 복사
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
				if (designerSeq.length() % 60 != 0) {                //나머지를 맨 마지막 줄에
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
		
		for (int i = 0; i < crSPVec.size(); i++){        // cdsSP. trnaSP, rRNASP, ncrnaSP와 gSP 의 차이 검정.
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
			System.out.println("동일한 파일이 존재합니다. 기존 파일을 무시하고 새로이 생성하시겠습니까?(y/n)");
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
				System.out.println("파일을 새로 만듭니다.");
			} else
				System.out.println("파일의 생성이 실패했습니다.");
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

