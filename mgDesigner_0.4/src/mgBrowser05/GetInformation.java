/* 9-Jan-2011 김영창
 * 미생물 genome gb 파일과 필수 유전자 db를 이용하여 minimal genome을 만들고 gb 파일을 생성.
 */

package mgBrowser05;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import mgDB.DbmanagerV0;

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

public class GetInformation {                     // Making egBricks is easy!

	RichSequence richSeq;

	Vector<String> geneNameVec = new Vector<String>();   //genome 상의 모든 유전자 이름
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> gGeneNameVec = new Vector<String>();
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager에서 가져 온 데이터
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene의 본래의 location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	//Vector<String> funcDefVec = new Vector<String>();
	Vector<Integer> gegSPVec = new Vector<Integer>();
	Vector<Integer> gegEPVec = new Vector<Integer>();
	Vector<Integer> gSPVec = new Vector<Integer>();       //essential gene database의 유전자 순서를 genome 상의 순서와 일치시키기 위해 !//db에 저장하자.
	Vector<Integer> gEPVec = new Vector<Integer>();
	Vector<Integer> gStrandVec = new Vector<Integer>();
	Vector<String> egFuncCatVec = new Vector<String>();
	
	int seqLen;
	String locus;
	String circular;
	static String nLoc;
	static File tFile;
	String sFile;
	static String orgName;
	int geneNameNum = 0;
	
	public GetInformation(String fileName){
		
		sFile = fileName;
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(sFile)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}

		locus = richSeq.getName();
		if(richSeq.getCircular()) circular = "circular";  
		seqLen = richSeq.length();  

		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
		FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        gSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    gEPVec.add(loc.getMax());
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
	        	}
	        }
        	if (!found) geneNameVec.add(locusTagVec.elementAt(geneNameNum-1).toString()); // 유전자의 gene name이 없는 경우는 대신 locus_tag을 사용
	        if (!found) geneName = "";
	    	gGeneNameVec.add(geneName);    // 유전자의 gene name이 없는 경우는 ""을 사용
	    }
		System.out.println("gSPVec.size= " + gSPVec.size() + ", " + "geneNameVec.size= " + geneNameVec.size() +
				", " + "gGeneNameVec.size= " + gGeneNameVec.size());
		
		getOrgName(sFile);
		getEgData();
		if ((sFile.startsWith("mc")) || (sFile.startsWith("mg"))) makeMgFuncCatVec();
		else makeEgFuncCatVec();
	}
	
	Vector<String> getgNameVec() {                 // 유전자 이름 목록 반환
		return geneNameVec;
	}

	String getGeneName(int i) {                       // 유전자 이름 반환
		return (String)(geneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // 유전자 개수 반환
		return geneNameVec.size();
	}
	
	int getSeqLen() {                          // 서열 길이 반환
		return seqLen;
	}
	
	int getStrand(int i) {                     // 유전자 방향      +1, -1
		return (gStrandVec.elementAt(i));
	}
	
	String getLocusName() {
		return locus;
	}
	
	public String getFileName() {
		return sFile;
	}

	public Vector<String> getegFuncCatVec() {
		return egFuncCatVec;
	}
	
	int getSP(int i) {                        // 유전자 시작 위치 반환
		return (gSPVec.elementAt(i));
	}
	
	int getEP(int i) {                        // 유전자 끝위치 반환
		return (gEPVec.elementAt(i));
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
	
	public void getEgData() {
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //필수 유전자 정보 DB에서 가져오기, oSPVec 생성
		
		//giVec = dm.getgiVec();                                                 
		//gIDVec = dm.getgIDVec();
		oSPVec = dm.getspVec();
		oEPVec = dm.getepVec();
		funcClassVec = dm.getfuncClassVec();
		//funcDefVec = dm.getfuncDefVec();
		
		if (sFile.startsWith("mc")) {
			Vector<Integer> idxVec = new Vector<Integer>();
			 boolean found;
			 String str = "";
			 for (int i = 0; i < funcClassVec.size(); i++){ 
				 found = false;
				if (funcClassVec.elementAt(i).length() >= 8) {
					str = (String)funcClassVec.elementAt(i);
					str = str.substring(str.length()-1-1);      // 두 카테고리에 속해 있는 경우도 있음!!!!!
					for (int j = 0; j < str.length(); j++) {
						if ((str.charAt(j) == 'L') || (str.charAt(j) == 'D')) {
							found = true;
							break;
						}
					}
				}
				if (!found) idxVec.add(i);	
			 }
			 
			 System.out.println(idxVec.size());
	
			 for (int i = idxVec.size()-1; i >= 0; i--){ 
				 oSPVec.remove(idxVec.elementAt(i).intValue());
				 oEPVec.remove(idxVec.elementAt(i).intValue());
				 funcClassVec.remove(idxVec.elementAt(i).intValue());
			 }
			 System.out.println(funcClassVec.size());
			 for (int i = 0; i < funcClassVec.size(); i++){ 
				 System.out.print(funcClassVec.elementAt(i).toString() + ", ");
			 }
		}
	}
	
	public void makeEgFuncCatVec() {                    //필수 유전자 정보와 비교하기 위하여

		for (int i = 0; i < gSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
			int gSP = gSPVec.elementAt(i).intValue();
			String str;
			boolean found = false;
			for (int j = 0; j < oSPVec.size(); j++){
				if (gSP == oSPVec.elementAt(j).intValue()) {
					found = true;
					if (gEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB 사이의 차이점을 찾기 위해
						System.out.println("reporting differences in endPoint, " + gEPVec.elementAt(i).intValue() +
								", " + oEPVec.elementAt(j).intValue());
					}
					
					if (funcClassVec.elementAt(j).length() >= 8) {
						str = (String)funcClassVec.elementAt(j);
						egFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
					}else egFuncCatVec.add("X");
					break;
				}
			}
			if (!found) {
				int gEP = gEPVec.elementAt(i).intValue();        // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
				for (int j = 0; j < oEPVec.size(); j++){
					if (gEP == oEPVec.elementAt(j).intValue()) {
						found = true;
						System.out.println("reporting differences in startPoint, " + gSPVec.elementAt(i).intValue() +
								", " + oSPVec.elementAt(j).intValue());
					
						if (funcClassVec.elementAt(j).length() >= 8) {
							str = (String)funcClassVec.elementAt(j);
							egFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
						}else egFuncCatVec.add("X");
						break;
					}
				}
			}
			if (!found) {
				egFuncCatVec.add("X");
			}
		}
		
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
			int oSP = oSPVec.elementAt(i).intValue();
			int oEP = oEPVec.elementAt(i).intValue();
			boolean found = false;
			for (int j = 0; j < gSPVec.size(); j++){
				int gSP = gSPVec.elementAt(j).intValue();
				int gEP = gEPVec.elementAt(j).intValue();
			
				if ((oSP == gSP) || (oEP == gEP)){
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
		System.out.println("oSPVec.size= " + oSPVec.size() +
				", " + "gSPVec.size= " + gSPVec.size() + ", " + "funcClassVec.size= " + funcClassVec.size() +
				", " + "egFuncCatVec.size= " + egFuncCatVec.size());
} 
	
	public void makeMgFuncCatVec() {                    //필수 유전자 정보와 비교하기 위하여

		for (int i = 0; i < gSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
			int gSP = gSPVec.elementAt(i).intValue();
			String str;
			boolean found = false;
			for (int j = 0; j < oSPVec.size(); j++){
				if (gSP == oSPVec.elementAt(j).intValue()) {
					found = true;
					if (gEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB 사이의 차이점을 찾기 위해
						System.out.println("reporting differences in endPoint, " + gEPVec.elementAt(i).intValue() +
								", " + oEPVec.elementAt(j).intValue());
					}
					
					if (funcClassVec.elementAt(j).length() >= 8) {
						str = (String)funcClassVec.elementAt(j);
						egFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
					}else egFuncCatVec.add("X");
					break;
				}
			}
			if (!found) {
				int gEP = gEPVec.elementAt(i).intValue();        // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
				for (int j = 0; j < oEPVec.size(); j++){
					if (gEP == oEPVec.elementAt(j).intValue()) {
						found = true;
						System.out.println("reporting differences in startPoint, " + gSPVec.elementAt(i).intValue() +
								", " + oSPVec.elementAt(j).intValue());
					
						if (funcClassVec.elementAt(j).length() >= 8) {
							str = (String)funcClassVec.elementAt(j);
							egFuncCatVec.add(str.substring(str.length()-1-1)); // 두 카테고리에 속해 있는 경우도 있음!!!!!
						}else egFuncCatVec.add("X");
						break;
					}
				}
			}
			if (!found) {
				egFuncCatVec.add("X");
			}
		}
		
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP와 gSP 의 차이 검정. SP와 EP는 바뀔  수 있음. 따라서 SP와 함께 EP도 비교!
			int oSP = oSPVec.elementAt(i).intValue();
			int oEP = oEPVec.elementAt(i).intValue();
			boolean found = false;
			for (int j = 0; j < gSPVec.size(); j++){
				int gSP = gSPVec.elementAt(j).intValue();
				int gEP = gEPVec.elementAt(j).intValue();
			
				if ((oSP == gSP) || (oEP == gEP)){
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
		System.out.println("oSPVec.size= " + oSPVec.size() +
				", " + "gSPVec.size= " + gSPVec.size() + ", " + "funcClassVec.size= " + funcClassVec.size() +
				", " + "egFuncCatVec.size= " + egFuncCatVec.size());
} 
	
	public static void main(String[] args) {

		String sFile = args[0];
		GetInformation getInfo = new GetInformation(sFile);
		getInfo.getOrgName(sFile);
		getInfo.getEgData();
		if ((sFile.startsWith("mc")) || (sFile.startsWith("mg"))) getInfo.makeMgFuncCatVec();
		else getInfo.makeEgFuncCatVec();
	}
}

