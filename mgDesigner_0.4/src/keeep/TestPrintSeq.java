package keeep;

public class TestPrintSeq {

	public static void main (String[] args) {		
	
		String mgSeq = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaagggggggggggggggggggggggggggaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaattttttttttttttttttttt" +
		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaccccccccccccccccccccccccccccccccccccccccaaaaaaaaaaaaaaaaaaaaaaaaa";
        String str;
        String str1;
        int i = 0;
        int j = 0;
		
		int num = mgSeq.length() / 60;     //1줄 당 60 nt, 수정 중!!!!!!  숫자 앞의 간격이 문제
		int mod = mgSeq.length() % 60;
		System.out.println(mgSeq.length() + ", " + num + ", " + mod);
		
		while (i < num) {                                                
			int k = 60 * j + 1;
			str = "";
			if (k-1 < 10) str = "        ";
			if (k-1 < 100) str = "       ";
			if (k-1 < 1000)	str = "      ";
			if (k-1 < 10000) str = "     ";
			if (k-1 < 100000) str = "    ";
			if (k-1 < 1000000) str = "   ";
			
			str = str + String.valueOf(k) + " ";
			str1 = "";
			str1 = mgSeq.substring(k-1, k-1+59);
			str = str + str1;
			System.out.println(str);
			j++;
			i++;
		}
		if (mod !=0) {
			int k = 60 * j + 1;
			str = "";
			if (k-1 < 10) str = "        ";
			if (k-1 < 100) str = "       ";
			if (k-1 < 1000)	str = "      ";
			if (k-1 < 10000) str = "     ";
			if (k-1 < 100000) str = "    ";
			if (k-1 < 1000000) str = "   ";
			
			str = str + String.valueOf(k) + " ";
			str1 = "";
			str1 = mgSeq.substring(k-1);
			str = str + str1;
			System.out.println(str);
		}
	}

}
