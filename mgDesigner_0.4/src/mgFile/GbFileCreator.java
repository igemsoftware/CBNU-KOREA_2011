/* 1-Jan-2011 김영창
 * 미생물 genome gb 파일과 필수 유전자 db를 이용하여 minimal genome을 만들고 gb 파일을 생성.
 */

package mgFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import mgDB.DbmanagerV0;

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
import org.biojavax.ontology.ComparableTerm;

public class GbFileCreator {                     // Making egBricks is easy!

	 RichSequence richSeq;
	 Vector<SymbolList> egBrickSym = new Vector<SymbolList>();

	 Vector<Integer> gSPVec = new Vector<Integer>();       //genome 상의 모든 유전자 시작 위치
	 Vector<Integer> gEPVec = new Vector<Integer>();
	
	 Vector<Integer> csSPVec = new Vector<Integer>();       //genome 상의 모든 유전자 시작 위치
	 Vector<Integer> csEPVec = new Vector<Integer>();       //genome 상의 모든 유전자 끝 위치
	 
	 Vector<Integer> cdsSPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 시작 위치
	 Vector<Integer> cdsEPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 끝 위치
	 Vector<Integer> rRNASPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 시작 위치
	 Vector<Integer> rRNAEPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 끝 위치
	 Vector<Integer> tRNASPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 시작 위치
	 Vector<Integer> tRNAEPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 끝 위치
	 Vector<Integer> ncRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> ncRNAEPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 끝 위치
	 Vector<Integer> tmRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> tmRNAEPVec = new Vector<Integer>();  
	 Vector<Integer> miscRNASPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> miscRNAEPVec = new Vector<Integer>(); 
	 
	 
	 Vector<Integer> egIndexVec = new Vector<Integer>();
	 Vector<Integer> cdsIndexVec = new Vector<Integer>();
	 Vector<Integer> rRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> tRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> ncRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> tmRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> miscRNAIndexVec = new Vector<Integer>();
	 Vector<Integer> pseudoIndexVec = new Vector<Integer>();
	 
	 Vector<Integer> gLineNumVec = new Vector<Integer>();
	 Vector<Integer> pseudoLineNumVec = new Vector<Integer>();
	 Vector<Integer> egLineNumVec = new Vector<Integer>();
	 
	 Vector<Integer> rrSPVec = new Vector<Integer>();       //genome 상의repeat_region 위치
	 Vector<Integer> rrEPVec = new Vector<Integer>(); 
	 public Vector<Integer> mpSPVec = new Vector<Integer>();       //genome 상의 mat_peptide 위치
	 public Vector<Integer> mpEPVec = new Vector<Integer>();  
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
	Vector<String> orgVec = new Vector<String>();
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
//	Vector<Integer> mcSPVec = new Vector<Integer>();       //MG에서의 new location
//	Vector<Integer> mcEPVec = new Vector<Integer>();
	
	Vector<Integer> brickSPVec = new Vector<Integer>();
	Vector<Integer> brickEPVec = new Vector<Integer>();
	Vector<String> brickIDVec1 = new Vector<String>();
	Vector<String> brickSeqVec1 = new Vector<String>();
	Vector<String> brickIDVec2 = new Vector<String>();
	Vector<String> brickSeqVec2 = new Vector<String>();
	
	Vector<Integer> nSpaceVec = new Vector<Integer>();
	Vector<String> SpacerSeqVec = new Vector<String>();
	Vector<String> strVec = new Vector<String>();
	
	public Vector<String> cFuncCatVec = new Vector<String>();
	public Vector<String> egFuncCatVec = new Vector<String>();
	public Vector<String> mgFuncCatVec = new Vector<String>();
	//Vector<Color> gColorCodeVec = new Vector<Color>();
	Vector<String> gColorCodeVec = new Vector<String>();
	
	int seqLen;
	int brickID;
	String locus;
	String circular;
	//boolean circular;
	static String nLoc;
//	static File tFile;
	String sFile;
	//String fileName;
	static String designerSeq;
	static String sSeq;
	public String orgName;
	int geneNameNum = 0;
	int oriLineNum = 0;
	
	public GbFileCreator(String sFile){
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(sFile)),null).nextRichSequence();
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
	        gSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    gEPVec.add(loc.getMax());
	        csSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    csEPVec.add(loc.getMax());
		    gStrandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()은 +, -로
	    
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
	        		if (geneName.length()>30) System.out.print(geneName + ", ");  //DB geneName field 크기 결정하기 위해
	        	}
	        }
	        if (!found) geneNameVec.add(locusTagVec.elementAt(geneNameNum-1).toString()); // 유전자의 gene name이 없는 경우는 대신 locus_tag을 사용
	        if (!found) geneName = "";
        	gGeneNameVec.add(geneName);    // 유전자의 gene name이 없는 경우는 ""을 사용
		}
		System.out.println("geneNameNum= " + geneNameNum); 
		System.out.println("geneNameVec.size= " + geneNameVec.size());
		
		ff = new FeatureFilter.ByType("tRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        tRNASPVec.add(loc.getMin());                                    // tRNA 정보 추출
		    tRNAEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("rRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rRNASPVec.add(loc.getMin());                                    // rRNA 정보 추출
		    rRNAEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("ncRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        ncRNASPVec.add(loc.getMin());                                    // ncRNA 정보 추출
		    ncRNAEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("tmRNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the tmRNA features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        tmRNASPVec.add(loc.getMin());                                    // tmRNA 정보 추출
		    tmRNAEPVec.add(loc.getMax());
	    }
		
		ff = new FeatureFilter.ByType("misc_RNA");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the tmRNA features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        miscRNASPVec.add(loc.getMin());                                    // tmRNA 정보 추출
		    miscRNAEPVec.add(loc.getMax());
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
		getFeatureIndex(sFile);
		correctSP();
		uncorrectedError();

		getOrgName(sFile);
		makeBrickDB();
		
		getEgData();
		compareEgGenome(); 
		makeEGVec(sFile);
		
		try {
			getEgFeature(sFile);
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		}
		makeEGInfoFile();
	}
		
	public Vector<String> getGeneNameVec() {                 // 유전자 이름 목록 반환
		return geneNameVec;
	}

	public Vector<Integer> getgSP() {                                 // 유전자 시작 위치 목록 반환
		return csSPVec;
	}
	
	public Vector<Integer> getgEP() {                                 // 유전자 끝위치 목록반환
		return csEPVec;
	}
	
	String getGeneName(int i) {                       // 유전자 이름 반환
		return (String)(geneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // 유전자 개수 반환
		return geneNameVec.size();
	}
	
	int getgSP(int i) {                        // 유전자 시작 위치 반환
		return (csSPVec.elementAt(i));
	}
	
	int getgEP(int i) {                        // 유전자 끝위치 반환
		return (csEPVec.elementAt(i));
	}
/*	
	int getcSP(int i) {                        // 유전자 시작 위치 반환
		return (cSPVec.elementAt(i));
	}
*/	
	int getSeqLen() {                          // 서열 길이 반환
		return seqLen;
	}
	
	public Vector<Integer> getStrandVec() {                     // 유전자 방향      +1, -1
		return gStrandVec;
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


	public Vector<Integer> getMgSPVec() { 
		return mgSPVec;
	}
	
	public Vector<Integer> getMgEPVec() {                                
		return mgEPVec;
	}
	
	public Vector<Integer> getEgIndexVec() {                                
		return egIndexVec;
	}
	public Vector<Integer> getEgSPVec() {                                
		return egSPVec;
	}
	public Vector<String> getOrgVec() {                                
		return orgVec;
	}
	
	public String getOrgName() {                                
		return orgName;
	}
	
	public Vector<String> getEgFunCatVec() {
		return egFuncCatVec;
	}

	void getFeatureIndex(String sFile){                                 // gene 시작점을 CS로 하지 않고 RBS로 한 경우
		
	    int lineNum = 0;
	    BufferedReader br;

		try {                                  
			br = new BufferedReader(new FileReader(sFile));
			
		    String line;
		    int gIdx = 0;
		    int pseudoFoundNum = 0;
		    while ((line = br.readLine()) != null) {       // 필수 유전자의 행 번호를 알기 위하여
		    	lineNum+=1;
		    	if (line.startsWith("     gene")) {
		    		gLineNumVec.add(lineNum);
		    		gIdx += 1;
		    		pseudoFoundNum = 0;
		    	}
		    	if (line.startsWith("                     /pseudo")) {
		    		pseudoFoundNum+=1;
		    		pseudoLineNumVec.add(lineNum);                // 두번씩 나옴
		    		if (pseudoFoundNum == 1) pseudoIndexVec.add(gIdx-1);
		    	}
		    	
				if (line.startsWith("     CDS")) cdsIndexVec.add(gIdx-1);
				else if (line.startsWith("     tRNA")) tRNAIndexVec.add(gIdx-1);
				else if (line.startsWith("     rRNA")) rRNAIndexVec.add(gIdx-1);
				else if (line.startsWith("     ncRNA")) ncRNAIndexVec.add(gIdx-1);
				else if (line.startsWith("     tmRNA")) tmRNAIndexVec.add(gIdx-1);
				else if (line.startsWith("     misc_RNA")) miscRNAIndexVec.add(gIdx-1);
				
				if (line.startsWith("ORIGIN")) {
					oriLineNum = lineNum;
					break;
				}
		    }
		    br.close(); 
		}catch (IOException e) {
			e.printStackTrace();	
		}
		System.out.println("cdsIndexVec: " + cdsIndexVec.size());
	}
	
	void deletePseudo(){                                 // gene 시작점을 CS로 하지 않고 RBS로 한 경우
	
		for (int i = pseudoIndexVec.size()-1; i >= 0; i--){
			csSPVec.remove(i);
			csEPVec.remove(i);
			gStrandVec.remove(i);
			locusTagVec.remove(i);
			geneNameVec.remove(i);
			gLineNumVec.remove(i);
		}
	}
	
	void correctSP() {             //gene은 있으나  cds,RNA 없는 경우가 있음, Haemophilus
		
		for (int i = 0; i < cdsIndexVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정. end point 는 ?????
			int cdsSP = cdsSPVec.elementAt(i).intValue();    //시작점을 RBS 기점을 한 data가 있음
			int cdsIdx = cdsIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(cdsIdx).intValue() != cdsSP) {
				csSPVec.remove(cdsIdx);
			    csSPVec.add(cdsIdx, cdsSP);
			}
		}
		
		for (int i = 0; i < tRNAIndexVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정 및 수정.
			int sp = tRNASPVec.elementAt(i).intValue();
			int idx = tRNAIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(idx).intValue() != sp) {
				csSPVec.remove(idx);
			    csSPVec.add(idx, sp);
			}
		}

		for (int i = 0; i < rRNAIndexVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정 및 수정.
			int sp = rRNASPVec.elementAt(i).intValue();
			int idx = rRNAIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(idx).intValue() != sp) {
				csSPVec.remove(idx);
			    csSPVec.add(idx, sp);
			}
		}
		
		for (int i = 0; i < ncRNAIndexVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정 및 수정.
			int sp = ncRNASPVec.elementAt(i).intValue();
			int idx = ncRNAIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(idx).intValue() != sp) {
				csSPVec.remove(idx);
			    csSPVec.add(idx, sp);
			}
		}
		
		for (int i = 0; i < tmRNAIndexVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정 및 수정.
			int sp = tmRNASPVec.elementAt(i).intValue();
			int idx = tmRNAIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(idx).intValue() != sp) {
				csSPVec.remove(idx);
			    csSPVec.add(idx, sp);
			}
		}
		
		for (int i = 0; i < miscRNAIndexVec.size(); i++){        // 차이 검정 및 수정.
			int sp = miscRNASPVec.elementAt(i).intValue();
			int idx = miscRNAIndexVec.elementAt(i).intValue();
			if (csSPVec.elementAt(idx).intValue() != sp) {
				csSPVec.remove(idx);
			    csSPVec.add(idx, sp);
			    System.out.println(miscRNASPVec.elementAt(i).intValue() + ", SPdiff found!");   
			}
		}
	}
	
	void uncorrectedError(){
		
		for (int i = 0; i < csSPVec.size(); i++){        // cdsSP. tRNASP, rRNASP, ncRNASP와 gSP 의 차이 검정.
			int gSP = csSPVec.elementAt(i).intValue();
			boolean found =false;
			for (int j = 0; j < cdsSPVec.size(); j++){
				if (gSP == cdsSPVec.elementAt(j).intValue()) {
					found =true;break;}	}
		
			for (int j = 0; j < tRNASPVec.size(); j++){		
				if (gSP == tRNASPVec.elementAt(j).intValue()){
					found =true;break;}	}
		
			for (int j = 0; j < rRNASPVec.size(); j++){		
				if (gSP == rRNASPVec.elementAt(j).intValue()){		
					found =true;break;} }
		
			for (int j = 0; j < ncRNASPVec.size(); j++){		
				if (gSP == ncRNASPVec.elementAt(j).intValue()){	
					found =true;break;} }
			
			for (int j = 0; j < tmRNASPVec.size(); j++){		
				if (gSP == tmRNASPVec.elementAt(j).intValue()){	
					found =true;break;} }
			
			for (int j = 0; j < miscRNASPVec.size(); j++){		
				if (gSP == miscRNASPVec.elementAt(j).intValue()){	
					found =true;break;} }
	
			if (!found){
				//System.out.println(csSPVec.elementAt(i).intValue());
				boolean flag = false;
				for (int j = 0; j < egSPVec.size(); j++){		
					if (gSP == egSPVec.elementAt(j).intValue()){	
						flag =true;break;}
//					if (flag) System.out.println(egSPVec.elementAt(j).intValue());
				}
			}
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
		funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
		
		System.out.println("oSPVec.size= " + oSPVec.size());
	}
	
	void compareEgGenome(){                    //필수 유전자 정보와 비교하기 위하여

		boolean duplicate = false;
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
			int oSP = oSPVec.elementAt(i).intValue();
			int foundNum = 0;
			int duplicateNum = 0;
			String str;
			for (int j = 0; j < csSPVec.size(); j++){
				if (oSP == csSPVec.elementAt(j).intValue()) {
					//found = true;
					foundNum = foundNum +1;
					if (foundNum == 1) {
						cSPVec.add(csSPVec.elementAt(j).intValue());     
						cEPVec.add(csEPVec.elementAt(j).intValue());
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
				//System.out.println("gSP= " + csSPVec.elementAt(duplicateNum).intValue() + ",  " + 
				//		"Gene Name= " + (String)gNameVec.elementAt(duplicateNum));
				duplicate = true;			
			}
			if (foundNum == 0) {                                              //SP에서 발견되지 않을 경우 EP 비교
				boolean found = false;
				int oEP = oEPVec.elementAt(i).intValue();
				duplicateNum = 0;
				for (int j = 0; j < csEPVec.size(); j++){
					if (oEP == csEPVec.elementAt(j).intValue()) {
						found = true;
						cSPVec.add(csSPVec.elementAt(j).intValue());     
						cEPVec.add(csEPVec.elementAt(j).intValue());
						//egStrandVec.add(gStrandVec.elementAt(j).intValue());
						if (funcClassVec.elementAt(i).length() >= 8) {
							str = (String)funcClassVec.elementAt(i);
							cFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
						}else{
							cFuncCatVec.add("X");
						}
						
						//System.out.println("oEP 비교하여 찾은 것:");
						//System.out.println("oSP= " + oSPVec.elementAt(i).intValue() + ", " + "oEP= " + oEPVec.elementAt(i).intValue());
						//System.out.println("gSP= " + csSPVec.elementAt(j).intValue() + ", " + "gEP= " + csEPVec.elementAt(j).intValue());
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
		System.out.println("oSPVec.size= " + oSPVec.size() + ",  " + "cSPVec.size= " + cSPVec.size());
		
		duplicate = false;
		for (int i = 0; i < oEPVec.size(); i++){                         //  SP와 함께 EP도 비교!
			int oEP = oEPVec.elementAt(i).intValue();
			int foundNum = 0;
			int duplicateNum = 0;
			for (int j = 0; j < csEPVec.size(); j++){
				if (oEP == csEPVec.elementAt(j).intValue()) {
					foundNum = foundNum +1;
					if (foundNum > 1) duplicateNum = j;
				} 
			}
			if (foundNum > 1) {
				System.out.println("gEP에 중복된 것:");
				System.out.println("gEP= " + csEPVec.elementAt(duplicateNum).intValue() + ",  " + 
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
		
		for (int i = 0; i < csSPVec.size(); i++) {      // making minimal genome,   별개의 메소드로 나누자.
			String brickSeq = "";
			int nSpace = 0;
			int brickSP = 0;
			int brickEP = 0;
			if (i == 0) {
				nSpace = csSPVec.elementAt(i).intValue() - 1 + seqLen - csEPVec.elementAt(csSPVec.size()-1).intValue() - 1 - 1;
				brickSeq = sSeq.substring(csEPVec.elementAt(csEPVec.size()-1).intValue() - 1 + 1) + 
				sSeq.substring(0, csSPVec.elementAt(i).intValue() - 1);
				brickSP = (csEPVec.elementAt(csEPVec.size()-1).intValue() - 1 + 1) - seqLen;
				brickEP = csSPVec.elementAt(i).intValue() - 1;
			}
			if (i > 0 ) { 
				nSpace = csSPVec.elementAt(i).intValue() - 1 - csEPVec.elementAt(i-1).intValue() - 1 - 1;

				if (nSpace > 0) {
					brickSeq = sSeq.substring(csEPVec.elementAt(i-1).intValue() - 1 + 1, (csSPVec.elementAt(i).intValue()) - 1);
					brickSP = (csEPVec.elementAt(i-1).intValue() - 1 + 1);
					brickEP = csSPVec.elementAt(i).intValue() - 1 - 1;
				}
				if (nSpace == 0)
					brickSeq = "";
				if (nSpace < 0)
					brickSeq = "";
					//brickSeq = sSeq.substring(csSPVec.elementAt(i).intValue() - 1, csEPVec.elementAt(i-1).intValue() - 1 + 1);	//overlap seq 나타냄	
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
			
			for (int i = 0; i < csSPVec.size(); i++) { 
				brickSeqVec2.add(sSeq.substring(csSPVec.elementAt(i).intValue()-1, (csEPVec.elementAt(i).intValue() - 1 + 1)));
				brickIDVec2.add("CR" + getNumStr(i+1));           // coding region
			}
			
			raf = new RandomAccessFile("CRBrickDB.fas", "rw");
			raf.seek(raf.length()); 
			for (int i=0; i < brickSeqVec2.size(); i++) {
				if (gStrandVec.elementAt(i).intValue() == 1){
					nLoc = String.valueOf(csSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(csEPVec.elementAt(i).intValue());
				}else if (gStrandVec.elementAt(i).intValue() == -1){
					nLoc = "complement(" + String.valueOf(csSPVec.elementAt(i).intValue()) +
					".." + String.valueOf(csEPVec.elementAt(i).intValue()) + ")";
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
	
	public Vector<Integer> getEGseq(){                                //essential gene egBricks 서열 추출

		try {
			RandomAccessFile raf = new RandomAccessFile("spacer.txt", "rw");

			raf.seek(raf.length()); 
			raf.writeBytes("orgName= " + orgName);
			raf.writeBytes("\n" + "\n");
			raf.writeBytes("LOCUS= " + richSeq.getName() + ", " + "GenomeSeqLen= " + richSeq.length());
			raf.writeBytes("\n");
		
			int assembledBrickLen = 0;
			for (int i = 0; i < egSPVec.size(); i++) {      // making minimal genome,   별개의 메소드로 나누자.
	
				int upstream = 50;                     //Vector를 만들때 까지 임시 사용!!!!!!!! 만든 후에는 제거!!!!!!!
				int downstream = 50;
	
				if (i>0 && i < egSPVec.size()-1) {
					int nSpacer = egSPVec.elementAt(i).intValue() - egEPVec.elementAt(i-1).intValue() - 1;
					if (nSpacer < downstream + upstream) {
						upstream = nSpacer/2 + nSpacer%2;                            // 간격이 만약 홀수면  upstream 쪽에 하나 추가, 음수인 경우도 해결됨
						//System.out.println(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
						//		", " +  "nSpacer =" + nSpacer + ", " + "upstream =" + upstream);
						raf.writeBytes(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
								", " +  "nSpacer =" + nSpacer + ", " + "upstream =" + upstream);
						raf.writeBytes("\n");
					}
					nSpacer = egSPVec.elementAt(i+1).intValue() - egEPVec.elementAt(i).intValue() - 1;
					if (nSpacer < downstream + upstream) {
						downstream = nSpacer/2;
						//System.out.println(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
						//		", " +  "nSpacer =" + nSpacer + ", " + "downstream =" + downstream);
						raf.writeBytes(i + ": " + "egSP =" + egSPVec.elementAt(i).intValue() +
								", " +  "nSpacer =" + nSpacer + ", " + "downstream =" + downstream);
						raf.writeBytes("\n");
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
			
			raf.close();
			
		} catch (IOException e) {
		}
		
		return mgSPVec;

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
	
	public void makeEGVec(String sFile) {

	    int sp;                            // 필수 유전자의 행 번호를 알기 위하여
	    for (int i = 0; i < csSPVec.size(); i++){      //모든 유전자의  시작 행 번호를 벡터에 저장
	    	sp = csSPVec.elementAt(i).intValue();
			for (int j = 0; j < cSPVec.size(); j++){            //cSPVec 의 유전자 순서를  gSPVec의 순서대로 수정하기 위하여                 
				  if ((cSPVec.elementAt(j)).intValue() == sp) {
	    			  egLineNumVec.add(gLineNumVec.elementAt(i).intValue());     // 687 개.   689가 맞는데 2개는?????????????????????????????????????????????????????
	    			  egIndexVec.add(i);
	    			  egSPVec.add(sp);
	    			  egEPVec.add(csEPVec.elementAt(i).intValue());
	    			  egStrandVec.add(gStrandVec.elementAt(i).intValue());
	    			  //System.out.println(cFuncCatVec.elementAt(j));
	    			  egFuncCatVec.add((String)cFuncCatVec.elementAt(j));
	    			  break;
	    		  }
			}
	    }
		
		System.out.println("gLineNumVec.size= " + gLineNumVec.size() + ", " + "egLineNumVec.size= " + egLineNumVec.size() +
				", " + "egIndexVec.size= " + egIndexVec.size() + ", " + "egSPVec.size= " + egSPVec.size() +
				", " + "gSPVec.size= " + gSPVec.size() + ", " + "egFunCatVec.size= " + egFuncCatVec.size());	
	}
	

	public void getEgFeature(String sFile) throws IllegalSymbolException {
        
	    BufferedReader br;
		BufferedWriter bw = null;
		boolean complement = false;
		
	 if (egSPVec.size() != 0) {
		try { 

		    String sFileName = sFile.substring(sFile.indexOf("NC_"));
			String tFile = ".\\data\\mGenome\\mg_" + sFileName;
		    System.out.println(tFile);
			br = new BufferedReader(new FileReader(sFile));
			bw = new BufferedWriter(new FileWriter(tFile));
			
			String nLocus = "";
			String nLength;
			String nDate;
			String nLoc;
			
			mgSPVec = getEGseq();                              //mgSPVec 생성
			designerSeq = joinEGseq(); 
			
			String line="";
			int lineNum = 0;
			try {
				while ((line = br.readLine()) != null) {       
			    	lineNum+=1;
					String str = "";
					boolean sourceFound = false;
					if (lineNum < gLineNumVec.elementAt(0).intValue()){      
				    	if (line.startsWith("LOCUS")){
				    		nLocus = "mg_" + locus;
				    
						    Date date = new Date();                      // Get today's date
						    Format formatter;
						    formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
						    nDate = formatter.format(date);
			
							nLength = String.valueOf(designerSeq.length());
						
							int nSpace = 28 - nLocus.length() -  nLength.length();
							String sSpace = ""; 
							for (int i=0; i < nSpace; i++)
								sSpace = sSpace + " ";
			
							str = line.substring(0,12) + nLocus + sSpace + nLength
							+ line.substring(40,68)+ nDate;
			
							bw.write(str); bw.write("\n"); 
				    	}else if ((line.startsWith("DEFINITION")) || (((lineNum ==3) && (!line.startsWith("ACCESSION"))))) { //2줄에  걸쳐 있는 경우가 있음
				    		if (line.indexOf("complete") > 0){
				    			str = line.replace("complete", "minimal");
				    			bw.write(str); bw.write("\n");
				    		}else{				    		
				    			bw.write(line); bw.write("\n");
				    		}
				    	}else if (line.startsWith("ACCESSION")){	
							str = line.substring(0,12) + nLocus;
							bw.write(str); bw.write("\n");
				    	}else if (line.startsWith("SOURCE")) {
							String tokSource = "";
							str = "SOURCE      ";
							int num = 0;
							StringTokenizer tok = new StringTokenizer(line.substring(12));
							int tc = tok.countTokens();
							while (tok.hasMoreTokens()) {
								num = num+1;
								tokSource = tok.nextToken();			    	 			    	
						    	if(num < tc){			    	  
						    		str = str + tokSource + " ";
						    	}else
						    		str = str + nLocus;		
			  				}
							bw.write(str); bw.write("\n");
						}else if ((!sourceFound) && (line.startsWith("     source"))) {  //source 가 여러번 반복해서 나오는 경우가 있음
							str = "     source          ";
							str = str + "1.." + String.valueOf(designerSeq.length());
							line = str;
							sourceFound = true;
							bw.write(str); bw.write("\n");
						}else{
							bw.write(line); bw.write("\n");
						}
					}
					
					if (lineNum >= gLineNumVec.elementAt(0).intValue()){
						nLoc = "";
						int foundNum = 0;
						while (lineNum < oriLineNum){                                        //필수 유전자의 feature 복사
							boolean found = false;
							for (int i = 0; i < egIndexVec.size(); i++){
								int j = egIndexVec.elementAt(i).intValue() + 1;    //eg 유전자 다음 유전자를 찾기 위해서 +1
								if (j < gLineNumVec.size()) {                      //제일 마지막 유전자가 eg 유전자인 경우는  oriLineNum으로
									if ((lineNum >= egLineNumVec.elementAt(i).intValue()) &&
										(lineNum < gLineNumVec.elementAt(j).intValue())) {
										found = true;
							       		break;
									}
								}else if ((lineNum >= egLineNumVec.elementAt(i).intValue()) &&
										(lineNum < oriLineNum)) {
						       		  found = true;
						       		  break;
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
								}else if (line.startsWith("     misc_RNA")) {
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
						    line = br.readLine(); lineNum++;          //첫 유전자가 egGene인 경우 때문에
				       	}
			                
				        if (lineNum == oriLineNum) {
				        	bw.write(line);
				        	bw.write("\n");
				        }
					}
					 if (lineNum > oriLineNum) break;
				}
				br.close();

				String str = "";
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
					else if (k-1 < 100) str = "       ";
					else if (k-1 < 1000) str = "      ";
					else if (k-1 < 10000) str = "     ";
					else if (k-1 < 100000) str = "    ";
					else if (k-1 < 1000000) str = "   ";
					
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
	 }else System.out.println("egSPVec.size= " + egSPVec.size() + ", so mg_file of " + sFile + " was not made!");
	}
	
	void makeEGInfoFile() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("egInfo.txt", "rw");

			raf.seek(raf.length());
	
			raf.writeBytes(orgName+ "\t");
			raf.writeBytes(" mg "+ "\t");
			//raf.writeBytes("\n" + "\n");
			raf.writeBytes(richSeq.getName() + "\t" +richSeq.length()+ "\t");
			//raf.writeBytes("\n");
			raf.writeBytes(gSPVec.size() + "\t"+ csSPVec.size()+ "\t");
			//raf.writeBytes("\n");
			int sum = cdsSPVec.size() + tRNASPVec.size() + rRNASPVec.size() + ncRNASPVec.size() + tmRNASPVec.size()
										+ miscRNASPVec.size();
			raf.writeBytes( cdsSPVec.size() + "\t" +
					tRNASPVec.size() + "\t"  + rRNASPVec.size() + "\t"+ 
					ncRNASPVec.size() + "\t" + tmRNASPVec.size() + "\t"+ 
					+ miscRNASPVec.size() + "\t" + sum+ "\t");
			//raf.writeBytes("\n");
			
			int dif = csSPVec.size() - sum;
			raf.writeBytes(dif+ "\t");
			raf.writeBytes(pseudoLineNumVec.size() + "\t" + pseudoIndexVec.size()+ "\t");
			//raf.writeBytes("\n");
			raf.writeBytes(oSPVec.size() + "\t" + cSPVec.size() + "\t" +
					egSPVec.size() + "\t" + mgSPVec.size() + "\t" + 
					funcClassVec.size() + "\t" + egFuncCatVec.size()+ "\t");
			//raf.writeBytes("\n");
			raf.writeBytes(gGeneNameVec.size() + "\t" + geneNameVec.size() + "\t" + 
					locusTagVec.size() + "\t" +
					gStrandVec.size()+ "\t");
			//raf.writeBytes("\n");
			raf.writeBytes(meSPVec.size() + "\t" + rrSPVec.size() +
					"\t" + mpSPVec.size()  +
					"\t" + rbsSPVec.size() + "\t"+ pribnowSPVec.size() + 
					"\t" + recognitionSPVec.size()+ "\t");
			//raf.writeBytes("\n");
			raf.writeBytes(""+designerSeq.length());
			//raf.writeBytes("\n" + "\n");
			raf.writeBytes("\n");
			raf.close();
	
		 } catch (IOException e) {
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
	
		GbFileCreator mgFileCreator = new GbFileCreator(sFile);
		
/*		
		System.out.println("gSPVec: " + mgFileCreator.gSPVec.size() + ", " + "csSPVec: " + mgFileCreator.csSPVec.size() + ", " + "cdsSPVec: " + mgFileCreator.cdsSPVec.size() + ", " +
				"tRNASPVec: " + mgFileCreator.tRNASPVec.size() +
				", " + "rRNASPVec: " + mgFileCreator.rRNASPVec.size() + ", " + "ncRNASPVec: " + mgFileCreator.ncRNASPVec.size() +
				", " + "tmRNASPVec: " + mgFileCreator.tmRNASPVec.size());

		System.out.println("cdsIndexVec: " + mgFileCreator.cdsIndexVec.size() + ", " + "pseudoLineNumVec: " + mgFileCreator.pseudoLineNumVec.size() + ", " + 
				"pseudoIndexVec: " + mgFileCreator.pseudoIndexVec.size() + ", " + "tRNAIndexVec: " + mgFileCreator.tRNAIndexVec.size() +
					", " + "rRNAIndexVec: " + mgFileCreator.rRNAIndexVec.size() + ", " + "ncRNAIndexVec: " + mgFileCreator.ncRNAIndexVec.size() +
					", " + "tmRNAIndexVec: " + mgFileCreator.tmRNAIndexVec.size());

		System.out.println("cdsIndexVec: " + mgFileCreator.cdsIndexVec.size() + ", " + "pseudoLineNumVec: " + mgFileCreator.pseudoLineNumVec.size() + ", " + 
				"tRNAIndexVec: " + mgFileCreator.tRNAIndexVec.size() +
					", " + "rRNAIndexVec: " + mgFileCreator.rRNAIndexVec.size() + ", " + "ncRNAIndexVec: " + mgFileCreator.ncRNAIndexVec.size() +
					", " + "tmRNAIndexVec: " + mgFileCreator.tmRNAIndexVec.size());

		System.out.println("csSPVec: " + mgFileCreator.csSPVec.size() + ", " + "cdsSPVec: " + mgFileCreator.cdsSPVec.size() +
				", " + "tRNASPVec: " + mgFileCreator.tRNASPVec.size() +
				", " + "rRNASPVec: " + mgFileCreator.rRNASPVec.size() + ", " + "ncRNASPVec: " + mgFileCreator.ncRNASPVec.size() +
				", " + "tmRNASPVec: " + mgFileCreator.tmRNASPVec.size());
*/		
//		System.out.println("Uncorrected error found: ");
//		mgFileCreator.uncorrectedError();
		
		System.out.println("orgName= " + mgFileCreator.orgName);
		System.out.println("LOCUS= " + mgFileCreator.richSeq.getName() + ", " + "GenomeSeqLen= " + mgFileCreator.richSeq.length());
		System.out.println("gSPVec= " + mgFileCreator.gSPVec.size() + ", " + "csSPVec= " + mgFileCreator.csSPVec.size() + ", " +
				"cdsSPVec= " + mgFileCreator.cdsSPVec.size() + ", " +
				"tRNASPVec= " + mgFileCreator.tRNASPVec.size() + ", " + "rRNASPVec= " + mgFileCreator.rRNASPVec.size() +
				", " + "ncRNASPVec= " + mgFileCreator.ncRNASPVec.size() + ", " + "tmRNASPVec= " + mgFileCreator.tmRNASPVec.size() +
				", " + "miscRNASPVec= " + mgFileCreator.miscRNASPVec.size());
		System.out.println("pseudoLineNumVec= " + mgFileCreator.pseudoLineNumVec.size() + ", " + "pseudoIndexVec= " + mgFileCreator.pseudoIndexVec.size());
		System.out.println("oSPVec= " + mgFileCreator.oSPVec.size() + ", " + "cSPVec= " + mgFileCreator.cSPVec.size() + ", " +
				"egSPVec= " + mgFileCreator.egSPVec.size() + ", " + "mgSPVec= " + mgFileCreator.mgSPVec.size() + 
				", " + "funcClassVec= " + mgFileCreator.funcClassVec.size() + ", " + "egFuncCatVec= " + mgFileCreator.egFuncCatVec.size());
		System.out.println("gGeneNameVec= " + mgFileCreator.gGeneNameVec.size() + ", " + "geneNameVec= " + mgFileCreator.geneNameVec.size() + ", " + 
				"locusTagVec= " + mgFileCreator.locusTagVec.size() + ", " +
				"gStrandVec= " + mgFileCreator.gStrandVec.size());
		System.out.println("meSPVec= " + mgFileCreator.meSPVec.size() + ", " +  "rrSPVec= " + mgFileCreator.rrSPVec.size() +
				", " + "mpSPVec= " + mgFileCreator.mpSPVec.size()  +
				", " + "rbsSPVec= " + mgFileCreator.rbsSPVec.size() + ", " + "pribnowSPVec= " + mgFileCreator.pribnowSPVec.size() + 
				", " + "recognitionSPVec= " + mgFileCreator.recognitionSPVec.size());
		System.out.println("designerSeqLen= " + designerSeq.length());
	}
}

