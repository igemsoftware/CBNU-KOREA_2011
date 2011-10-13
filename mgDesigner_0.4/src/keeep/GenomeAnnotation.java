package keeep;

import java.io.*;
import java.util.*;

import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;

import org.biojavax.Note;
import org.biojavax.RichAnnotation;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequence.Terms;
import org.biojavax.ontology.ComparableTerm;

class GenomeAnnotation {
	
	 RichSequence richSeq;

	 Vector<Integer> gSPVec = new Vector<Integer>();       //genome 상의 모든 유전자 시작 위치
	 Vector<Integer> gEPVec = new Vector<Integer>();       //genome 상의 모든 유전자 끝 위치
	 
	 Vector<Integer> cdsSPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 시작 위치
	 Vector<Integer> cdsEPVec = new Vector<Integer>();       //genome 상의 단백질 유전자 끝 위치
	 Vector<Integer> rrnaSPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 시작 위치
	 Vector<Integer> rrnaEPVec = new Vector<Integer>();       //genome 상의 rRNA 유전자 끝 위치
	 Vector<Integer> trnaSPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 시작 위치
	 Vector<Integer> trnaEPVec = new Vector<Integer>();       //genome 상의 tRNA 유전자 끝 위치
	 Vector<Integer> ncrnaSPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 시작 위치
	 Vector<Integer> ncrnaEPVec = new Vector<Integer>();       //genome 상의 ncRNA 유전자 끝 위치
	 
	 Vector<String> gGeneNameVec = new Vector<String>();   //genome 상의 모든 유전자 이름
	 Vector<String> gCDSNameVec = new Vector<String>();    //
	 
	 Vector<String> gLocusTagVec = new Vector<String>();
	 Vector<Integer> strandVec = new Vector<Integer>();    //genome 상의 모든 유전자 방향
	
	 Vector<Integer >cogNum = new Vector<Integer>();       // for COG

     Vector<String> go_FuncNum = new Vector<String>();     // for GO
	 Vector<String> go_Func = new Vector<String>();
     Vector<String> go_componentNum = new Vector<String>();
	 Vector<String> go_componentFunc = new Vector<String>();
     Vector<String> go_processNum = new Vector<String>();
	 Vector<String> go_processFunc = new Vector<String>();
	 		
	int seqLen;
	String locus;
	String circular;
	//boolean circular;
	String sFile;
	int SIZEofCOGID = 7;
	
	GenomeAnnotation(String fileName) {                                //유전체 자료에서 COG GO 유전자 이름 동의어 , 산물 기능 등 보다 많은 정보를 추출하기 위함
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		richSeq.getAnnotation();
		
		locus = richSeq.getName();
		if(richSeq.getCircular()) 
			circular = "circular";  // circular 여부 가져오기
		seqLen = richSeq.length();  // 서열 길이 가져오기

		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        gSPVec.add(loc.getMin());                                    // 유전체 서열 정보 추출
		    gEPVec.add(loc.getMax());
		    strandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()은 +, -로
		    
		    
		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	        gGeneNameVec.add((String)ra.getProperty("gene"));        //직접 실행할땐 되지만 부라우저로 실행시엔 오류. 위의 ByType을 CDS로 수정해서 수행하면 됨???????????
	        
	        gLocusTagVec.add((String) ra.getProperty("locus_tag"));
	       
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
		
		ff = new FeatureFilter.ByType("CDS");    //U00096V2 CDS features 4319, gene 4493, 차이는 RNA 유전자 등
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features
	        RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        cdsSPVec.add(loc.getMin());                                    // CDS 정보 추출
		    cdsEPVec.add(loc.getMax());

		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	        gCDSNameVec.add((String)ra.getProperty("gene"));        //직접 실행할땐 되지만 부라우저로 실행시엔 오류. 위의 ByType을 CDS로 수정해서 수행하면 됨???????????

//	        String locus = (String) ra.getProperty("locus_tag");
	        //String geneSynonym1 = (String) ra.getProperty("gene_synonym"); // 여러개가 있는 경우 처음만 표시함
	        ComparableTerm synonymTerm = Terms.getGeneSynonymTerm();         //Use BioJava defined ComparableTerms 
	        ComparableTerm productTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("product");//Create the required additional ComparableTerms
	        ComparableTerm proteinIDTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("protein_id");
	        ComparableTerm noteTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("note");
	           
	        String geneSynonymList[];      //Create empty strings
	        String geneSynonym = "";
	        String product = "";
	        String proteinID = "";
	        String note_tag = "";
	       
	        //Iterate through the notes in the annotation 
	        for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){
	        	Note note = ni.next();
	        	//System.out.println(note);
	        
	        	//Check each note to see if it matches one of the required ComparableTerms
	        	//if(note.getTerm().equals(geneTerm)){gene = note.getValue().toString();}    //  안됨
	        	if(note.getTerm().equals(synonymTerm)){           //여러개가 있는 경우 마지막만 표시함
	        		geneSynonym = note.getValue().toString();
	        	}
	        
		        if(note.getTerm().equals(noteTerm)){         //Check each note to see if it matches one of the required ComparableTerms
		            note_tag = note.getValue().toString();
		            note_tag = note_tag.trim();
					StringTokenizer tok = new StringTokenizer(note_tag, ";:-");
				   	while (tok.hasMoreTokens()) {
						if (tok.nextToken().equals("GO_process")) {
							tok.nextToken();
							go_processNum.add(tok.nextToken().trim());
							go_processFunc.add(tok.nextToken().trim());
						}
					}
		        }
	
		        if(note.getTerm().equals(productTerm)){
			          product = note.getValue().toString();
			          product = product.trim();
					  StringTokenizer tok = new StringTokenizer(product);
				      while (tok.hasMoreTokens()) {
				    	  String str = tok.nextToken();
				    	  str = str.trim();
				    	  if (str.length()== SIZEofCOGID){
					    	  if (str.charAt(0)=='C' & str.charAt(1)=='O' & str.charAt(2)=='G'){
					    		  cogNum.add(Integer.parseInt(str.substring(3)));
					    	  }
				    	  }
				      }
		        }
		        
		        if(note.getTerm().equals(proteinIDTerm)){
		          proteinID = note.getValue().toString();
		        }
	        }
	     
	      if (!(cogNum.isEmpty())){
	    	  //System.out.print( "COG: ");
		      for (int i=0; i < cogNum.size(); i++){
		       	 int num = cogNum.elementAt(i).intValue();
		       	 //System.out.println(num + " ");
		      }
	      }
	      
	      if (!(go_processNum.isEmpty())){
	    	  //System.out.print( "GO_process: ");
		      for (int i=0; i < go_processNum.size(); i++){
		       	 String str1 = go_processNum.elementAt(i);
		         String str2 = go_processFunc.elementAt(i);
		       	 //System.out.println( "GO:" + str1 + " - " + str2);
		      }
	      }
	    }
	}
	
	Vector<String> getgGeneNameVec() {                 // 유전자 이름 목록 반환
		return gGeneNameVec;
	}
	
	Vector<Integer> getgSP() {                                 // 유전자 시작 위치 목록 반환
		return gSPVec;
	}
	
	Vector<Integer> getgEP() {                                 // 유전자 끝위치 목록반환
		return gEPVec;
	}
	
	String getGeneName(int i) {                       // 유전자 이름 반환
		return (String)(gGeneNameVec.elementAt(i));
	}
	
	int getGeneNumber() {                             // 유전자 개수 반환
		return gGeneNameVec.size();
	}
	
	int getgSP(int i) {                        // 유전자 시작 위치 반환
		return (gSPVec.elementAt(i));
	}
	
	int getgEP(int i) {                        // 유전자 끝위치 반환
		return (gEPVec.elementAt(i));
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
		return (strandVec.elementAt(i));
	}
	
	public static void main(String[] args) throws IllegalSymbolException {

		String sFile = args[0];
		GenomeAnnotation gAnn = new GenomeAnnotation(sFile);  //gAnn을 해결!!!!!!!!!!!!!!!
		
/*
 		for(int i=0; i<gAnn.getGeneNumber(); i++) {               // 오류를 확인하기 위한 코드************************

			System.out.println(i + ", " +
				gAnn.getGeneName(i) +" : "+ 
				gAnn.getgSP(i) +", "+ 
				gAnn.getgEP(i) +";");
	    }
*/		
		System.out.println("LOCUS= " + gAnn.richSeq.getName() + ", " + "seqLen= " + gAnn.richSeq.length());
		System.out.println("gSPVec.size= " + gAnn.gSPVec.size() + ", " + "cdsSPVec.size= " + gAnn.cdsSPVec.size() + ", " +
				"rrnaSPVec.size= " + gAnn.rrnaSPVec.size() + ", " + "trnaSPVec.size= " + gAnn.trnaSPVec.size()
				 + ", " + "ncrnaSPVec.size= " + gAnn.ncrnaSPVec.size());
				
		System.out.println("gGeneNameVec.size= " + gAnn.gGeneNameVec.size() + ", " +
				"gCDSNameVec.size= " + gAnn.gCDSNameVec.size() + ", " + "strandVec.size= " + gAnn.strandVec.size());
		System.out.println("cogNum.size= " + gAnn.cogNum.size() + ", " + "go_processNum.size= " + gAnn.go_processNum.size());
		System.out.println("gLocusTagVec.elementAt(0)= " + gAnn.gLocusTagVec.elementAt(0) + ", " + "gLocusTagVec.size= " + gAnn.gLocusTagVec.size());
	}
}
