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
//*************************** �� ����� organism �̸��� �����ϴ� ������ �����ִ� �ڵ��ε� ���̿��ڹٸ� �̿��Ͽ� �����ϰ� �ٿ�������!	
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
					if (found) break;                  // ������ ������ ��찡 ����
				}
			}
				catch (IOException e) {
				e.printStackTrace();
			}
//****************************************************************************   StringTokenizer�� �̿����� ������!				
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

