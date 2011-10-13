package mgFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EgInfo {
	
	void egInfoOut() {
		
		try {
			RandomAccessFile raf = new RandomAccessFile("egInfo.out", "rw");

			raf.seek(raf.length());
			raf.writeBytes("orgName");
			raf.writeBytes("\t");
			raf.writeBytes("form");
			raf.writeBytes("\t");
			//raf.writeBytes("\n" + "\n");
			raf.writeBytes("  LOCUS  " + "\t" + "GSeqLen" + "\t"); //GenomeSeqLen
			//raf.writeBytes("\n");
			raf.writeBytes("gSP" + "\t" + "csSP" + "\t");
			//raf.writeBytes("\n");
			//int sum = cdsSPVec.size() + tRNASPVec.size() + rRNASPVec.size() + ncRNASPVec.size() + tmRNASPVec.size()
			//							+ miscRNASPVec.size();
			raf.writeBytes("cdsSP" + "\t" +
					"tRNASP" + "\t" + "rRNASP" + "\t"+ 
					"ncRNASP" + "\t" + "tmRNASP" + "\t"+ 
					"miscRNASP" + "\t" + "SUM" + "\t");
			//raf.writeBytes("\n");
			
			//int dif = csSPVec.size() - sum;
			raf.writeBytes("nosearch" + "\t");
			raf.writeBytes("pseudoLineNum" + "\t" + "pseudoIndex" + "\t");
			//raf.writeBytes("\n");
			raf.writeBytes("oSP" + "\t" + "cSP" + "\t" +
					"egSP" + "\t" + "mgSP" + "\t" + 
					"funcClass" + "\t" + "egFuncCat" + "\t");
			//raf.writeBytes("\n");
			raf.writeBytes("gGeneName" + "\t" + "geneName" + "\t" + 
					"locusTag" + "\t" +
					"gStrand" + "\t");
			//raf.writeBytes("\n");
			raf.writeBytes("meSP" + "\t" +  "rrSP" +
					"\t" + "mpSP" +
					"\t" + "rbsSP" + "\t"+ "pribnowSP" + 
					"\t" + "recognitionSP" + "\t");
			//raf.writeBytes("\n");
			raf.writeBytes("designerSeqLen" );
			//raf.writeBytes("\n" + "\n");

			raf.writeBytes("\n");
			raf.close();
	
		 } catch (IOException e) {
		 }
	}
	
	
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
		EgInfo egInfo = new EgInfo();
		egInfo.egInfoOut();
	}
}
