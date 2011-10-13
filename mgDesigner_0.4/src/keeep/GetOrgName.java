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
//*************************** �� ����� organism �̸��� �����ϴ� ������ �����ִ� �ڵ��ε� ���̿��ڹٸ� �̿��Ͽ� �����ϰ� �ٿ�������!	
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
//****************************************************************************   StringTokenizer�� �̿����� ������!				
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

