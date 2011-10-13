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
			System.out.println("������ �������� �ʽ��ϴ�.");
	}
	
	public void printFileMetadata(File file){
		System.out.println("����       �̸�: " + file.getName());
		System.out.println("���� �б⿩��: " + file.canRead());
		System.out.println("���� ���⿩��: " + file.canRead());
		System.out.println("���� ������: " + file.getAbsolutePath());
		System.out.println("���� �����: " + file.getPath());
	}
	
	public void printFileContents(File file){
		FileReader fr = null;
		//FileInputStream fis = null;
		System.out.println("���� ����");
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
			System.out.println("FileIODemo1.java_Line38_FileNotFoundException�� �߻��߽��ϴ�.");
		}catch(IOException ioe){
			System.out.println("FileIODemo1.java_Line40_IOException�� �߻��߽��ϴ�.");
		}finally{
			try{
				if(fr != null)
					fr.close();
			//	if(fis != null)
			//		fis.close();
			}catch(IOException ioe){
				System.out.println("FileIODemo1.java_Line48_IOException�� �߻��߽��ϴ�.");
			}
		}
	}
}