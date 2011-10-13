package mgFile;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter {

	public static void main(String[] args) {

		File f = new File(".\\data\\funcCat\\");
		File[] files = f.listFiles();
		for (int i = 0, j = 0; i < files.length; i++){
			if (files[i].getName().endsWith(".gb")) {
				System.out.println(j++ + 1 + ":" + files[i].getName());
			}
		}
	
		
		File dir = new File(".\\data\\funcCat\\");
		File[] file = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
		        if (name != null) {
		        	System.out.println(name);
		        	System.out.println(name.endsWith(".gb"));
		            return name.endsWith(".gb");
		          }
				return false;
			}
		});
	}
}
