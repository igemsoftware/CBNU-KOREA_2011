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
import org.biojavax.ontology.ComparableTerm;

class ExtractInformation {
	
	 RichSequence richSeq;

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
	 Vector<Integer> tmrnaSPVec = new Vector<Integer>();       //genome ���� tmRNA ������ ���� ��ġ
	 Vector<Integer> tmrnaEPVec = new Vector<Integer>();       //genome ���� tmRNA ������ �� ��ġ
 
	 Vector<String> geneNameVec = new Vector<String>();   //genome ���� ��� ������ �̸�
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> gGeneNameVec = new Vector<String>();
	 
	 Vector<String> cdsNameVec = new Vector<String>();
	 Vector<String> trnaNameVec = new Vector<String>();
	 Vector<String> rrnaNameVec = new Vector<String>();
	 Vector<String> ncrnaNameVec = new Vector<String>();
	 Vector<String> tmrnaNameVec = new Vector<String>();
	 Vector<Integer> strandVec = new Vector<Integer>();    //genome ���� ��� ������ ����
	 Vector<String> geneSynonymVec = new Vector<String>(); 
	 
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
	int geneNameNum = 0;
	
	ExtractInformation(String fileName) {                                //����ü �ڷῡ�� COG GO ������ �̸� ���Ǿ� , �깰 ��� �� ���� ���� ������ �����ϱ� ����

		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		richSeq.getAnnotation();
		
		locus = richSeq.getName();
		if(richSeq.getCircular()) 
			circular = "circular";  // circular ���� ��������
		seqLen = richSeq.length();  // ���� ���� ��������

		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        crSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
		    crEPVec.add(loc.getMax());
		    strandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()�� +, -��
		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	        locusTagVec.add((String) ra.getProperty("locus_tag"));
	        geneNameVec.add((String) ra.getProperty("locus_tag"));
	        ComparableTerm geneTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("gene");

	        String geneName = "";
	        boolean found = false;
	        for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){
	        	Note note = ni.next();
	        	if(note.getTerm().equals(geneTerm)){          //Check each note to see if it matches one of the required ComparableTerms
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
//		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
//		    trnaNameVec.add((String)ra.getProperty("gene")); 
	    }
		
		ff = new FeatureFilter.ByType("rRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        rrnaSPVec.add(loc.getMin());                                    // rRNA ���� ����
		    rrnaEPVec.add(loc.getMax());
//		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
//		    rrnaNameVec.add((String)ra.getProperty("gene")); 
	    }
		
		ff = new FeatureFilter.ByType("ncRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        ncrnaSPVec.add(loc.getMin());                                    // ncRNA ���� ����
		    ncrnaEPVec.add(loc.getMax());
//		    RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
//		    ncrnaNameVec.add((String)ra.getProperty("gene")); 
	    }
		
		ff = new FeatureFilter.ByType("tmRNA");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the tmRNA features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        tmrnaSPVec.add(loc.getMin());                                    // tmRNA ���� ����
		    tmrnaEPVec.add(loc.getMax());
//            RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
//		    tmrnaNameVec.add((String)ra.getProperty("gene")); 
	    }
		
		ff = new FeatureFilter.ByType("CDS");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
	    fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features
	        RichFeature rf = (RichFeature)fi.next();
	        Location loc = rf.getLocation();
	        cdsSPVec.add(loc.getMin());                                    // tmRNA ���� ����
		    cdsEPVec.add(loc.getMax());
	        RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	       // cdsNameVec.add((String)ra.getProperty("gene"));        //���� �����Ҷ� ������ �ζ������ ����ÿ� ����.


	        //String geneSynonym1 = (String) ra.getProperty("gene_synonym"); // �������� �ִ� ��� ó���� ǥ����
	       // ComparableTerm synonymTerm = Terms.getGeneSynonymTerm();         //Use BioJava defined ComparableTerms 
	        ComparableTerm productTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("product");//Create the required additional ComparableTerms
	        ComparableTerm proteinIDTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("protein_id");
	        ComparableTerm noteTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("note");
	           
	        String geneSynonymList[];      //Create empty strings
	       // String geneSynonym = "";
	        String product = "";
	        String proteinID = "";
	        String note_tag = "";
	       
	        //Iterate through the notes in the annotation 
	        for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){
	        	Note note = ni.next();
	        	//System.out.println(note);
	        
	        	//Check each note to see if it matches one of the required ComparableTerms
	        	//if(note.getTerm().equals(geneTerm)){gene = note.getValue().toString();}    //  �ȵ�
	        	//if(note.getTerm().equals(synonymTerm)){           //�������� �ִ� ��� �������� ǥ����
	        	//	geneSynonym = note.getValue().toString();
	        	//}
	        
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
/*		
		for (int i = 0; i < crSPVec.size(); i++){        // makeGeneNameVec()
			int crSP = crSPVec.elementAt(i).intValue();

			for (int j = 0; j < cdsSPVec.size(); j++){
				if (crSP == cdsSPVec.elementAt(j).intValue()) {
					geneNameVec.add(cdsNameVec.elementAt(j));
					break;}	}
		
			for (int j = 0; j < trnaSPVec.size(); j++){		
				if (crSP == trnaSPVec.elementAt(j).intValue()){
					geneNameVec.add(trnaNameVec.elementAt(j));
					break;}	}
		
			for (int j = 0; j < rrnaSPVec.size(); j++){		
				if (crSP == rrnaSPVec.elementAt(j).intValue()){	
					geneNameVec.add(rrnaNameVec.elementAt(j));
					break;} }
		
			for (int j = 0; j < ncrnaSPVec.size(); j++){		
				if (crSP == ncrnaSPVec.elementAt(j).intValue()){
					geneNameVec.add(ncrnaNameVec.elementAt(j));
					break;} }
			
			for (int j = 0; j < tmrnaSPVec.size(); j++){		
				if (crSP == tmrnaSPVec.elementAt(j).intValue()){
					geneNameVec.add(tmrnaNameVec.elementAt(j));
					break;} }
		}	
*/		
	}

	Vector<String> getgGeneNameVec() {                 // ������ �̸� ��� ��ȯ
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
		return (strandVec.elementAt(i));
	}
	
	public static void main(String[] args) throws IllegalSymbolException {

		String sFile = args[0];
		ExtractInformation gAnn = new ExtractInformation(sFile);  //gAnn�� �ذ�!!!!!!!!!!!!!!!
/*
 		for(int i=0; i<gAnn.getGeneNumber(); i++) {               // ������ Ȯ���ϱ� ���� �ڵ�************************

			System.out.println(i + ", " +
				gAnn.getGeneName(i) +" : "+ 
				gAnn.getgSP(i) +", "+ 
				gAnn.getgEP(i) +";");
	    }
*/		
		System.out.println("LOCUS= " + gAnn.richSeq.getName() + ", " + "seqLen= " + gAnn.richSeq.length());
		System.out.println("crSPVec.size= " + gAnn.crSPVec.size() + ", " + "cdsSPVec.size= " + gAnn.cdsSPVec.size() + ", " +
				"rrnaSPVec.size= " + gAnn.rrnaSPVec.size() + ", " + "trnaSPVec.size= " + gAnn.trnaSPVec.size()
				 + ", " + "ncrnaSPVec.size= " + gAnn.ncrnaSPVec.size() + ", " + "tmrnaSPVec.size= " + gAnn.tmrnaSPVec.size());
		System.out.println("geneNameVec.size= " + gAnn.geneNameVec.size() +	", " + "gGeneNameVec.size= " + gAnn.gGeneNameVec.size() +	", " + 
				"locusTagVec.size= " + gAnn.locusTagVec.size() + ", " + "geneSynonymVec.size= " + gAnn.geneSynonymVec.size() +
				", " + "strandVec.size= " + gAnn.strandVec.size());
		System.out.println("tmrnaNameVec.size= " + gAnn.tmrnaNameVec.size() + ", " + "go_processNum.size= " + gAnn.go_processNum.size());
	}
}
