/* 9-Jan-2011 �迵â
 * �̻��� genome gb ���ϰ� �ʼ� ������ db�� �̿��Ͽ� minimal genome�� ����� gb ������ ����.
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

	Vector<String> geneNameVec = new Vector<String>();   //genome ���� ��� ������ �̸�
	 Vector<String> locusTagVec = new Vector<String>();
	 Vector<String> gGeneNameVec = new Vector<String>();
	//Vector<Integer> gIDVec = new Vector<Integer>();   //Dbmanager���� ���� �� ������
	//Vector<Integer> giVec = new Vector<Integer>();
	Vector<Integer> oSPVec = new Vector<Integer>();       //essential gene�� ������ location
	Vector<Integer> oEPVec = new Vector<Integer>();
	Vector<String> funcClassVec = new Vector<String>();
	//Vector<String> funcDefVec = new Vector<String>();
	Vector<Integer> gegSPVec = new Vector<Integer>();
	Vector<Integer> gegEPVec = new Vector<Integer>();
	Vector<Integer> gSPVec = new Vector<Integer>();       //essential gene database�� ������ ������ genome ���� ������ ��ġ��Ű�� ���� !//db�� ��������.
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

		
		FeatureFilter ff = new FeatureFilter.ByType("gene");    //U00096V2 CDS features 4319, gene 4493, ���̴� RNA ������ ��
		FeatureHolder fh = richSeq.filter(ff);
		for (Iterator <Feature> fi = fh.features(); fi.hasNext();){      //Iterate through the CDS features 
			RichFeature rf = (RichFeature)fi.next();
			Location loc = rf.getLocation();
	        gSPVec.add(loc.getMin());                                    // ����ü ���� ���� ����
		    gEPVec.add(loc.getMax());
		    gStrandVec.add(rf.getStrand().getValue());   //Get the strand orientation of the feature .getToken()�� +, -��
	    
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
	    	gGeneNameVec.add(geneName);    // �������� gene name�� ���� ���� ""�� ���
	    }
		System.out.println("gSPVec.size= " + gSPVec.size() + ", " + "geneNameVec.size= " + geneNameVec.size() +
				", " + "gGeneNameVec.size= " + gGeneNameVec.size());
		
		getOrgName(sFile);
		getEgData();
		if ((sFile.startsWith("mc")) || (sFile.startsWith("mg"))) makeMgFuncCatVec();
		else makeEgFuncCatVec();
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
	
	int getSP(int i) {                        // ������ ���� ��ġ ��ȯ
		return (gSPVec.elementAt(i));
	}
	
	int getEP(int i) {                        // ������ ����ġ ��ȯ
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
	
	public void getEgData() {
		
		DbmanagerV0 dm = new DbmanagerV0(orgName);         //�ʼ� ������ ���� DB���� ��������, oSPVec ����
		
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
					str = str.substring(str.length()-1-1);      // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
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
	
	public void makeEgFuncCatVec() {                    //�ʼ� ������ ������ ���ϱ� ���Ͽ�

		for (int i = 0; i < gSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
			int gSP = gSPVec.elementAt(i).intValue();
			String str;
			boolean found = false;
			for (int j = 0; j < oSPVec.size(); j++){
				if (gSP == oSPVec.elementAt(j).intValue()) {
					found = true;
					if (gEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB ������ �������� ã�� ����
						System.out.println("reporting differences in endPoint, " + gEPVec.elementAt(i).intValue() +
								", " + oEPVec.elementAt(j).intValue());
					}
					
					if (funcClassVec.elementAt(j).length() >= 8) {
						str = (String)funcClassVec.elementAt(j);
						egFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
					}else egFuncCatVec.add("X");
					break;
				}
			}
			if (!found) {
				int gEP = gEPVec.elementAt(i).intValue();        // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
				for (int j = 0; j < oEPVec.size(); j++){
					if (gEP == oEPVec.elementAt(j).intValue()) {
						found = true;
						System.out.println("reporting differences in startPoint, " + gSPVec.elementAt(i).intValue() +
								", " + oSPVec.elementAt(j).intValue());
					
						if (funcClassVec.elementAt(j).length() >= 8) {
							str = (String)funcClassVec.elementAt(j);
							egFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
						}else egFuncCatVec.add("X");
						break;
					}
				}
			}
			if (!found) {
				egFuncCatVec.add("X");
			}
		}
		
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
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
	
	public void makeMgFuncCatVec() {                    //�ʼ� ������ ������ ���ϱ� ���Ͽ�

		for (int i = 0; i < gSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
			int gSP = gSPVec.elementAt(i).intValue();
			String str;
			boolean found = false;
			for (int j = 0; j < oSPVec.size(); j++){
				if (gSP == oSPVec.elementAt(j).intValue()) {
					found = true;
					if (gEPVec.elementAt(i).intValue() != oEPVec.elementAt(j).intValue()) {        //DB ������ �������� ã�� ����
						System.out.println("reporting differences in endPoint, " + gEPVec.elementAt(i).intValue() +
								", " + oEPVec.elementAt(j).intValue());
					}
					
					if (funcClassVec.elementAt(j).length() >= 8) {
						str = (String)funcClassVec.elementAt(j);
						egFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
					}else egFuncCatVec.add("X");
					break;
				}
			}
			if (!found) {
				int gEP = gEPVec.elementAt(i).intValue();        // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
				for (int j = 0; j < oEPVec.size(); j++){
					if (gEP == oEPVec.elementAt(j).intValue()) {
						found = true;
						System.out.println("reporting differences in startPoint, " + gSPVec.elementAt(i).intValue() +
								", " + oSPVec.elementAt(j).intValue());
					
						if (funcClassVec.elementAt(j).length() >= 8) {
							str = (String)funcClassVec.elementAt(j);
							egFuncCatVec.add(str.substring(str.length()-1-1)); // �� ī�װ��� ���� �ִ� ��쵵 ����!!!!!
						}else egFuncCatVec.add("X");
						break;
					}
				}
			}
			if (!found) {
				egFuncCatVec.add("X");
			}
		}
		
		for (int i = 0; i < oSPVec.size(); i++){                         // oSP�� gSP �� ���� ����. SP�� EP�� �ٲ�  �� ����. ���� SP�� �Բ� EP�� ��!
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

