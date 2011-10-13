package keeep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GetOrgNameV0 {

	String orgName = "";
	String sFile;
	public GetOrgNameV0(){

		try {
			
			
			
			
			
			FileReader fileReader = new FileReader(sFile);
			BufferedReader reader = new BufferedReader(fileReader);
//*************************** 이 블록은 organism 이름을 추출하는 과정을 보여주는 코드인데 바이오자바를 이용하여 간단하게 줄여보세요!	
			String line=null;
			try {
				boolean found = false;
				while((line=reader.readLine()) != null)	{
					if(line.startsWith("                     /organism=\"")) {
						String str[] =  line.split("organism=\"");
						String org[] = str[1].split("\"");
						orgName = org[0].substring(0);
						found = true;
					}
					if (found) break;                  // 여러개 나오는 경우가 있음
				}
			}
				catch (IOException e) {
				e.printStackTrace();
			}
//****************************************************************************   StringTokenizer를 이용하지 마세요!				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getorgName(){
		return orgName;
	}
	public static void main(String args[]){
		GetOrgNameV0 getOrgName = new GetOrgNameV0();
		System.out.println(getOrgName.getorgName());
	}
}

