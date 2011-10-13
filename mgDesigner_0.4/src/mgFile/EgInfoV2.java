package mgFile;

import java.io.File;

public class EgInfoV2 {
	
	public static void main(String[] args) {

		File f = new File(".\\data\\egGenome\\");
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++){
			if (files[i].getName().endsWith(".gbk")) {                  //gbk !!!!
				String sFile = files[i].getPath();
				MgFileCreator mgFileCreator = new MgFileCreator(sFile);
				McFileCreator mcFileCreator = new McFileCreator(sFile);
/*				
				System.out.println("orgName= " + mgFileCreator.orgName);
				System.out.println("LOCUS= " + mgFileCreator.richSeq.getName() + ", " + "GenomeSeqLen= " + mgFileCreator.richSeq.length());
				System.out.println("gSP= " + mgFileCreator.gSPVec.size() + ", " + "csSP= " + mgFileCreator.csSPVec.size());
				int sum = mgFileCreator.cdsSPVec.size() + mgFileCreator.tRNASPVec.size() + 
						mgFileCreator.rRNASPVec.size() + mgFileCreator.ncRNASPVec.size() +
						mgFileCreator.tmRNASPVec.size();
				System.out.println("cdsSP= " + mgFileCreator.cdsSPVec.size() + ", " +
						"tRNASP= " + mgFileCreator.tRNASPVec.size() + ", " + "rRNASP= " + mgFileCreator.rRNASPVec.size() +
						", " + "ncRNASP= " + mgFileCreator.ncRNASPVec.size() +
						", " + "tmRNASP= " + mgFileCreator.tmRNASPVec.size() + ", " + "SUM= " + sum);
				int dif = mgFileCreator.csSPVec.size() - sum;
				System.out.println("Coding Sequence - (CDS + tRNA + rRNA + ncRNA + tmRNA) = " + dif);
				
				System.out.println("pseudoLineNum= " + mgFileCreator.pseudoLineNumVec.size() + ", " + "pseudoIndex= " + mgFileCreator.pseudoIndexVec.size());
				System.out.println("oSP= " + mgFileCreator.oSPVec.size() + ", " + "cSP= " + mgFileCreator.cSPVec.size() + ", " +
						"egSP= " + mgFileCreator.egSPVec.size() + ", " + "mgSP= " + mgFileCreator.mgSPVec.size() + 
						", " + "funcClass= " + mgFileCreator.funcClassVec.size() + ", " + "egFuncCat= " + mgFileCreator.egFuncCatVec.size());
				System.out.println("gGeneName= " + mgFileCreator.gGeneNameVec.size() + ", " + "geneName= " + mgFileCreator.geneNameVec.size() + ", " + 
						"locusTag= " + mgFileCreator.locusTagVec.size() + ", " +
						"gStrand= " + mgFileCreator.gStrandVec.size());
				System.out.println("meSP= " + mgFileCreator.meSPVec.size() + ", " +  "rrSP= " + mgFileCreator.rrSPVec.size() +
						", " + "mpSP= " + mgFileCreator.mpSPVec.size()  +
						", " + "rbsSP= " + mgFileCreator.rbsSPVec.size() + ", " + "pribnowSP= " + mgFileCreator.pribnowSPVec.size() + 
						", " + "recognitionSP= " + mgFileCreator.recognitionSPVec.size());
				System.out.println("designerSeqLen= " + MgFileCreator.designerSeq.length());
*/				
			}
		}
	}
}
