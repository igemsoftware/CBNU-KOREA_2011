/* 9-Jan-2011 �迵â
 * �̻��� genome gb ���ϰ� �ʼ� ������ db�� �̿��Ͽ� minimal genome�� ����� gb ������ ����.
 */

package mgBrowser07;

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

public class DesignerGetInformation {

// Making egBricks is easy!

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
	String circular;
	static String nLoc;
	static File tFile;
	String sFile;
	static String orgName;
	int geneNameNum = 0;

	private String fileName;

	private String sFileName;

	public String locus;
	
	public DesignerGetInformation(File selectedFile){
		
		
		fileName = selectedFile.getPath();
		sFile = fileName;
		sFileName = selectedFile.getName();
		
		try {
			richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(fileName)),null).nextRichSequence();
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
	//	System.out.println("gSPVec.size= " + gSPVec.size() + ", " + "geneNameVec.size= " + geneNameVec.size() +
	//			", " + "gGeneNameVec.size= " + gGeneNameVec.size());
		
		getOrgName(sFile);
		getEgData();
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
	
	String getLocus() {
		return locus;
	}
	
	public String getFileName() {
		return sFile;
	}

	public String getOpenFileName() {
		return sFileName;
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
			 
//			 System.out.println(idxVec.size());

			 for (int i = idxVec.size()-1; i >= 0; i--){ 
				 oSPVec.remove(idxVec.elementAt(i).intValue());
				 oEPVec.remove(idxVec.elementAt(i).intValue());
				 funcClassVec.remove(idxVec.elementAt(i).intValue());
			 }
//			 System.out.println(funcClassVec.size());
			 for (int i = 0; i < funcClassVec.size(); i++){ 
				 System.out.print(funcClassVec.elementAt(i).toString() + ", ");
			 }
		}
	}
/*	
	public static void main(String[] args) {

		String sFile = args[0];
		DesignerGetInformation dGetInfo = new DesignerGetInformation(File selectedFile);
		dGetInfo.getOrgName(sFile);
		dGetInfo.getEgData();
}*/
}

