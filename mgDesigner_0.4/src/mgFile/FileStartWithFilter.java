package mgFile;

import java.io.File;
import java.io.FilenameFilter;

public class FileStartWithFilter implements FilenameFilter{
    private String fileName;
    public FileStartWithFilter(String fileName){
        this.fileName = fileName;
    }

    public boolean accept(File dir, String name) {
        if (name != null) {
          return fileName.startsWith(name);
        }
        return false;
    }
    
	public static void main(String[] args) {
	    File dir = new File(File.separator + "data" + File.separator + "funcCat");
	    FilenameFilter filenameFilter = new FileStartWithFilter("NC");
	    File[] files = dir.listFiles(filenameFilter);
	    for (File file : files) {
	        System.out.print(file.getName()+"=");
	        System.out.print(file.isDirectory()+":");
	        System.out.println(file.isFile());
	    }
	}
}
