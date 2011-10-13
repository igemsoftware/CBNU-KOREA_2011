package mgFile;
import java.io.*;

import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;

public class FileIO {
	public static void main(String[] args) {

	    if (args.length != 1){
	      System.out.println("Usage: java ExtractInformation <file in Genbank or EMBL format>");
	      System.exit(1);
	    }
	    else {
	    	//formatTesT(args [0]);                         //new Class(args [0]);
	    }
	
		
		FileIO fileIO = new FileIO();
		File file = new File("CreatedFile.txt");
		if(fileIO.checkFile(file)){
			fileIO.createFile(file);
		}
	}
	
	public boolean checkFile(File file){
		if(file.exists()){
			System.out.println("동일한 파일이 존재합니다. 기존 파일을 무시하고 새로이 생성하시겠습니까?(y/n)");
			try{
				int ch = System.in.read();
				if((char)ch == 'n')
					return false;
			}catch(IOException ioe){
				System.out.println("FileIODemo6_라인20_IOException 발생");
			}			
		}		
		
		return true;
	}
	
	public void createFile(File file){			
		try{			
			if(file.createNewFile()){
				System.out.println("파일이 정상적으로 생성되었습니다.");
				PrintStream ps = new PrintStream(file);
				ps.println("파일의 내용");
			} else
				System.out.println("파일의 생성이 실패했습니다.");
		}catch(IOException ioe){
			System.out.println("FileIODemo6_라인36_IOException 발생");
		}
	}
	
	public void formatTest(File file) {
	
		RichSequence richSeq;
	    try {
	   	   richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(file)),null).nextRichSequence();  //Load the sequence file
	    	      //Load the sequence file
	      }
	      catch(FileNotFoundException fnfe){
	        System.out.println("FileNotFoundException: " + fnfe);
	      }
	      catch(BioException bioe1){
	        System.err.println("Not a Genbank sequence trying EMBL");
	        try  {
	          richSeq = RichSequence.IOTools.readEMBLDNA(new BufferedReader(new FileReader(file)),null).nextRichSequence();
	        }
	        catch(BioException bioe2){
	          System.err.println("Not an EMBL sequence either");
	          System.exit(1);
	        }
	        catch(FileNotFoundException fnfe){
	          System.out.println("FileNotFoundException: " + fnfe);
	        }
	      }
	
	
	
	
	
	
	
	}
	
}