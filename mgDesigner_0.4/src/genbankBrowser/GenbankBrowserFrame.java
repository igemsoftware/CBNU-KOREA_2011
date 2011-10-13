package genbankBrowser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class GenbankBrowserFrame extends JFrame {
	JPanel contentPane;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem fileOpenMenuItem;
	JMenuItem fileExitMenuItem;
	JMenu helpMenu;
	JMenuItem helpAboutMenuItem;

	MonitorPanel monitor;

	//GenbankBrowserFrame 클래스의 생성자
	public GenbankBrowserFrame() {
		init();
	}

	//GenbankBrowserFrame 클래스의 초기화 메소드
	private void init() /* throws Exception */ {
		this.contentPane = (JPanel)this.getContentPane();

		this.contentPane.setBackground(Color.white);
		this.setSize(new Dimension(800, 600));
		this.setTitle("Genbank Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		/* menuBar------------------ */
		fileMenu = new JMenu();
		fileOpenMenuItem = new JMenuItem();
		fileExitMenuItem = new JMenuItem();
		helpMenu = new JMenu();
		helpAboutMenuItem = new JMenuItem();

		// File->Open에 대한 이벤트 처리
		fileOpenMenuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openGenbankFile();
				}
			}
		);
		
		// File->Exit에 대한 이벤트 처리
		fileExitMenuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);
		
		// Help->About에 대한 이벤트 처리
		helpAboutMenuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openAboutBox();
				}
			}
		);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		fileMenu.add(fileOpenMenuItem);
		fileMenu.add(fileExitMenuItem);
		helpMenu.add(helpAboutMenuItem);

		fileMenu.setText("File");
		fileOpenMenuItem.setText("Open");
		fileExitMenuItem.setText("Exit");

		helpMenu.setText("Help");
		helpAboutMenuItem.setText("About");
		this.setJMenuBar(menuBar);
		/* ------------------------- */

		monitor = new MonitorPanel();

		//각각의 Object를 해당 위치에 배치.
		contentPane.add(monitor, BorderLayout.NORTH);
	}

	//File->Open 선택시 실행될 내용.
	void openGenbankFile(){
		//JFileChooser 객체 생성
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(GenbankBrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		File selectedFile = fc.getSelectedFile();

		// GenBank 파일에서 유전자목록을 추출한 결과
		GenbankFileInfo gbInfo 
				= new GenbankFileInfo(selectedFile.getPath());

		//기존에 add 되어 있는 Oject를 remove한다.
		while(contentPane.getComponentCount()>1){
			contentPane.remove(contentPane.getComponentCount()-1);
		}

		if (gbInfo.circular.equals("circular")) {
			CircularBrowserComponent circularBrowserComponent
				 = new CircularBrowserComponent(gbInfo, monitor);
			circularBrowserComponent.monitorPanel.locusLabel.setText(
				"   "+ gbInfo.locus
				+",  "+ gbInfo.getSeqLen()
				+"bp,  "+ gbInfo.circular);

			//circularBroserComponent를 적재한다.
			contentPane.add(
					circularBrowserComponent, BorderLayout.CENTER);
		} else {
			LinearBrowserComponent linearBrowserComponent
				 = new LinearBrowserComponent(gbInfo, monitor);
			linearBrowserComponent.monitorPanel.locusLabel.setText(
				"   "+ gbInfo.locus 
				+",  "+ gbInfo.getSeqLen() 
				+"bp,  "+ gbInfo.circular);

			//gene이 그려질 Component의 panel 사이즈를 정해준다.
			linearBrowserComponent.setPreferredSize(
				new Dimension(gbInfo.getSeqLen() / 200 + 50, 200));

			//scrollbar가 올려질 scrollPane1의 각종 property를 설정한다.
			JScrollPane scrollPane1 = new JScrollPane();
			scrollPane1.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.getViewport().setBackground(
										new Color(255, 255, 255));
			scrollPane1.setWheelScrollingEnabled(true);

			//linearBroserComponent를 적재한다.
			scrollPane1.getViewport().add(linearBrowserComponent, null);
			contentPane.add(scrollPane1, BorderLayout.CENTER);
		}
		contentPane.updateUI();
	}

	void openAboutBox() {
		AboutBox about = new AboutBox(this, "Genbank Browser version 0.1");
		Dimension d1 = about.getSize();
		Dimension d2 = this.getSize();
		int x = Math.max( (d2.width - d1.width) / 2, 0);
		int y = Math.max( (d2.height - d1.height) / 2, 0);
		about.setBounds(x + this.getX(), 
						y + this.getY(), 
						d1.width,	
						d1.height);
		about.show();
	}
}
