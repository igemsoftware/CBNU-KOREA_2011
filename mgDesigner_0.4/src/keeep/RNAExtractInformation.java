package keeep;

import java.io.*;
import java.util.*;

import org.biojava.bio.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.*;

import org.biojavax.Note;
import org.biojavax.RichAnnotation;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequence.Terms;
import org.biojavax.ontology.ComparableTerm;

class RNAExtractInformation {
	String locus;
	boolean circular;
	String strand;
	int seqLen;
	static Vector<String> geneList = new Vector<String>();		// ������ �̸� ���
	static Vector startList = new Vector();	// ������ ���� ��ġ ���
	static Vector endList = new Vector();		// ������ �� ��ġ ���
	RichSequence richSeq;   //Create the RichSequence object
	
	RNAExtractInformation(String fileName) {                                //ExtractInformation constructor
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();  //Load the sequence file
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		Annotation rAnn = richSeq.getAnnotation();
		// locus ��������
		locus = richSeq.getName();
		
		// circular ���� ��������
		circular = richSeq.getCircular();
		
		// ���� ���� ��������
		seqLen = richSeq.length();
		int SIZEofCOGID = 7;
	    
	    FeatureFilter ff = new FeatureFilter.ByType("gene");    //Filter the sequence on CDS features
	    FeatureHolder fh = richSeq.filter(ff);
	     
	    for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features
	      RichFeature rf = (RichFeature)fi.next();
	      //System.out.println(rf); 
	       
	      char strand = rf.getStrand().getToken();   //Get the strand orientation of the feature
	      //org.biojava.bio.seq.StrandedFeature.Strand strand = rf.getStrand(); //POSITIVE, NEGATIVE, UNKNOWN
	      
	      //String location = rf.getLocation().toString();      //Get the location of the feature
	      Location location = rf.getLocation();
	      startList.add(new Integer(location.getMin()));
		  endList.add(new Integer(location.getMax()));
		  
	      RichAnnotation ra = (RichAnnotation)rf.getAnnotation();      //Get the annotation of the feature
	      //System.out.println(ra);
	      geneList.add((String) ra.getProperty("gene"));
	      //ComparableTerm geneTerm = Terms.getGeneNameTerm();      
	      //ComparableTerm geneTerm = new RichSequence.Terms().getGeneNameTerm(); 
	      String locus = (String) ra.getProperty("locus_tag");
	      //String geneSynonym1 = (String) ra.getProperty("gene_synonym"); // �������� �ִ� ��� ó���� ǥ����
	      ComparableTerm synonymTerm = Terms.getGeneSynonymTerm();         //Use BioJava defined ComparableTerms 
	      ComparableTerm productTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("product");//Create the required additional ComparableTerms
	      ComparableTerm proteinIDTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("protein_id");
	      ComparableTerm noteTerm = RichObjectFactory.getDefaultOntology().getOrCreateTerm("note");
	      
	      //Create empty strings
	           
	      String geneSynonymList[];
	      String geneSynonym = "";
	      String product = "";
	      String proteinID = "";
	      String note_tag = "";
	      
	      Vector<Integer >cogNum = new Vector<Integer>(3);

	      Vector<String> go_FuncNum = new Vector<String>(3);
		  Vector<String> go_Func = new Vector<String>(3);
	      Vector<String> go_componentNum = new Vector<String>(3);
		  Vector<String> go_componentFunc = new Vector<String>(3);
	      Vector<String> go_processNum = new Vector<String>(3);
		  Vector<String> go_processFunc = new Vector<String>(3);
		  
	      //Iterate through the notes in the annotation 
	      for (Iterator <Note> ni = ra.getNoteSet().iterator(); ni.hasNext();){
	        Note note = ni.next();
	        //System.out.println(note);
	        
	        //Check each note to see if it matches one of the required ComparableTerms
	        //if(note.getTerm().equals(geneTerm)){gene = note.getValue().toString();}    //  �ȵ�
	        if(note.getTerm().equals(synonymTerm)){           //�������� �ִ� ��� �������� ǥ����
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
	      
/*	      System.out.print("locus: " + locus + "; " + "; " + "[" + location +
	    		  "]" + " " + strand + " ");

	      if (!(cogNum.isEmpty())){
	    	  System.out.print( "COG: ");
		      for (int i=0; i < cogNum.size(); i++){
		       	 int num = cogNum.elementAt(i).intValue();
		       	 System.out.print(num + " ");
		      }
	      }
	      
	      if (!(go_processNum.isEmpty())){
	    	  System.out.print( "GO_process: ");
		      for (int i=0; i < go_processNum.size(); i++){
		       	 String str1 = go_processNum.elementAt(i);
		         String str2 = go_processFunc.elementAt(i);
		       	 System.out.print( "GO:" + str1 + " - " + str2);
		      }
	      }
	      
	      //System.out.print(proteinID + "\t" + product);
	      //System.out.print("geneSynonym: " + geneSynonym);
	 
	      System.out.println("");
*/	    }
			
	}

	// ������ ���� ��ȯ
	int getCount() {
		return geneList.size();
	}

	// ������ ������ġ ��� ��ȯ
	Vector getStartPositions() {
		return startList;
	}

	// ������ �� ��ġ ��� ��ȯ
	Vector getEndPositions() {
		return endList;
	}

	// ������ �̸� ��� ��ȯ
	Vector getGeneNames() {
		return geneList;
	}

	// ���� ���� ��ȯ
	int getSeqLen() {
		return seqLen;
	}

	// ������ �̸� ��ȯ
	String getGeneName(int index) {
		return (String)(geneList.get(index));
	}

	// ������ ���� ��ġ ��ȯ
	int getStartPos(int index) {
		return ((Integer)(startList.get(index))).intValue();
	}

	// ������ ����ġ ��ȯ
	int getEndPos(int index) {
		return ((Integer)(endList.get(index))).intValue();
	}

	public static void main(String[] args) {

		RNAExtractInformation rAnn = new RNAExtractInformation("U00096.gb");
		for(int i=0; i<rAnn.getCount(); i++) {
			System.out.println(rAnn.getGeneName(i) +" : "+ rAnn.getStartPos(i) +", "+ rAnn.getEndPos(i) +";");
		}

		System.out.println(rAnn.getCount());
	}
}
