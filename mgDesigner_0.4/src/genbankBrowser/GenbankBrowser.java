package genbankBrowser;

import java.awt.*;

public class GenbankBrowser {
	public static void main(String[] args) {
		GenbankBrowserFrame genbankBrowserFrame = new GenbankBrowserFrame();

		//Application 실행시 화면 중앙에서 시작하도록 설정
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = genbankBrowserFrame.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		genbankBrowserFrame.setLocation(
			(screenSize.width - frameSize.width) / 2,
			(screenSize.height - frameSize.height) / 2);

		genbankBrowserFrame.setVisible(true);
	}
}
