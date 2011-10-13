package mgBrowser07;

public class MinimalGenomeDesigner {
	
	public static void main(String[] args) {
		
		BrowserFrame browserFrame = new BrowserFrame();
		browserFrame.setLocation(0,0);
		browserFrame.setVisible(true);
	}
}
/*Heap Space 문제 해결 
 * 구동전에 Run As/Run Configurations/Arguments/VM arguments 창에 
 * -Xms256m -Xms1024m 입력*/