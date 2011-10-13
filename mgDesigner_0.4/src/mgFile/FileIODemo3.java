package mgFile;
import java.io.*;
import java.util.Vector;

public class FileIODemo3 {
	
	Vector<String> strVec = new Vector<String>();
	Vector<Integer> pointerStrVec = new Vector<Integer>();
	
	
	public static void main(String[] args) {
		FileIODemo3 fileIODemo3 = new FileIODemo3();
		File file = new File("sample.gb");
		
		if(file.exists()){
			fileIODemo3.printFileMetadata(file);
			fileIODemo3.printFileContents(file);
		} else
			System.out.println("파일이 존재하지 않습니다.");
	}
	
	public void printFileMetadata(File file){
		System.out.println("파일       이름: " + file.getName());
		System.out.println("파일 읽기여부: " + file.canRead());
		System.out.println("파일 쓰기여부: " + file.canRead());
		System.out.println("파일 절대경로: " + file.getAbsolutePath());
		System.out.println("파일 상대경로: " + file.getPath());
	}
	
	public void printFileContents(File file){
		FileReader fr = null;
		//FileInputStream fis = null;
		System.out.println("파일 내용");
		try{
			fr = new FileReader(file);
			//fis = new FileInputStream(file);
			int pointerStr = 0;
			int strSize = 0;
			int readCount = 0;
			char[] buffer = new char[512];
			while((readCount = fr.read(buffer)) != -1){
				System.out.print(buffer);
				pointerStr = pointerStr + readCount;
				pointerStrVec.add(pointerStr);
			
			}
			//byte[] buffer = new byte[512];
			//while((readCount = fis.read(buffer)) != -1)
			//	System.out.write(buffer, 0, readCount);	
			
			System.out.println("");
			System.out.println("pointerStrVec.size= " + pointerStrVec.size() + ", " + pointerStrVec.elementAt(pointerStrVec.size()-1));
			
		}catch(FileNotFoundException fne){
			System.out.println("FileIODemo1.java_Line38_FileNotFoundException이 발생했습니다.");
		}catch(IOException ioe){
			System.out.println("FileIODemo1.java_Line40_IOException이 발생했습니다.");
		}finally{
			try{
				if(fr != null)
					fr.close();
			//	if(fis != null)
			//		fis.close();
			}catch(IOException ioe){
				System.out.println("FileIODemo1.java_Line48_IOException이 발생했습니다.");
			}
		}
	}
}