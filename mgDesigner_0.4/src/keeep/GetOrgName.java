package keeep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GetOrgName {

	String orgName = "";

	public GetOrgName(){

		try {
			FileReader fileReader = new FileReader("U00096V2.gb");
			BufferedReader reader = new BufferedReader(fileReader);
//*************************** 이 블록은 organism 이름을 추출하는 과정을 보여주는 코드인데 바이오자바를 이용하여 간단하게 줄여보세요!	
			String line=null;
			try {
				while((line=reader.readLine()) != null)
				{
					if(line.startsWith("                     /organism=\""))
					{
						String str[] =  line.split("organism=\"");
						//System.out.println(str[1]);
						String org[] = str[1].split("\"");
						//System.out.println(org[0]);
						orgName = org[0].substring(0);
						//System.out.println(orgName);
					}
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
		GetOrgName getOrgName = new GetOrgName();
		System.out.println(getOrgName.getorgName());
	}
}

