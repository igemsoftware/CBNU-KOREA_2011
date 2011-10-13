package keeep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.biojava.bio.symbol.IllegalSymbolException;

public class MgFeatureCreator {

	File file;
	static File sFile;
	static File tFile;
	String line;
	
	Vector<String> strVec = new Vector<String>();
	Vector<Integer> pointerStrVec = new Vector<Integer>();
	
	public MgFeatureCreator() {
	
		try {
			
		    makePointerVec();
	   
			RandomAccessFile raf = new RandomAccessFile(tFile, "rw");

			for (int i=0; i < strVec.size(); i++) {
				raf.seek(pointerStrVec.elementAt(i).intValue()); 
				//String str = strVec.elementAt(i);
				//System.out.println(str);
				raf.writeBytes((String)strVec.elementAt(i));			
			}
			System.out.println("raf.length= " + raf.length());
			raf.close();
	
		 } catch (IOException e) {
		 }
	 }

	public void makePointerVec(){
		FileReader fr = null;

		try{
			fr = new FileReader(sFile);
			
			int pointerStr = 0;
			pointerStrVec.add(pointerStr);         //시작점을 미리 넣어 줌
			int strSize = 0;
			int readCount = 0;
			char[] buffer = new char[512];
			String str;	
			while((readCount = fr.read(buffer)) != -1){	
				
				
				
				str = new String(buffer);
				strVec.add(str);
				buffer  = new char[512];           //마지막에 뒤에 남은 바로 앞의 char 제거하기 위하여
				
				pointerStr = pointerStr + readCount;
				pointerStrVec.add(pointerStr);
			
			}
			
			System.out.println("pointerStrVec.size= " + pointerStrVec.size() + ", " + pointerStrVec.elementAt(pointerStrVec.size()-1));
			System.out.println("strVec.size= " + strVec.size());
						
		}catch(FileNotFoundException fne){
			System.out.println("FileIODemo1.java_Line38_FileNotFoundException이 발생했습니다.");
		}catch(IOException ioe){
			System.out.println("FileIODemo1.java_Line40_IOException이 발생했습니다.");
		}finally{
			try{
				if(fr != null)
					fr.close();
			}catch(IOException ioe){
				System.out.println("FileIODemo1.java_Line48_IOException이 발생했습니다.");
			}
		}
	
/*		
		pointerStrVec.add(33);
		strVec.add(" 573948");
*/
	}
	
	public static void main(String[] args) throws IllegalSymbolException {

	    if (args.length != 2){
		      System.out.println("Usage: java MgFeatureCreator <file in Genbank format>");
		      System.exit(1);
		    }else{
		    	sFile = new File(args[0]);
		    	tFile = new File(args[1]);
		}
	
	    new MgFeatureCreator();
	}
}
