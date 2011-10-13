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

	//GenbankBrowserFrame Ŭ������ ������
	public GenbankBrowserFrame() {
		init();
	}

	//GenbankBrowserFrame Ŭ������ �ʱ�ȭ �޼ҵ�
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

		// File->Open�� ���� �̺�Ʈ ó��
		fileOpenMenuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openGenbankFile();
				}
			}
		);
		
		// File->Exit�� ���� �̺�Ʈ ó��
		fileExitMenuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);
		
		// Help->About�� ���� �̺�Ʈ ó��
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

		//������ Object�� �ش� ��ġ�� ��ġ.
		contentPane.add(monitor, BorderLayout.NORTH);
	}

	//File->Open ���ý� ����� ����.
	void openGenbankFile(){
		//JFileChooser ��ü ����
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(GenbankBrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		File selectedFile = fc.getSelectedFile();

		// GenBank ���Ͽ��� �����ڸ���� ������ ���
		GenbankFileInfo gbInfo 
				= new GenbankFileInfo(selectedFile.getPath());

		//������ add �Ǿ� �ִ� Oject�� remove�Ѵ�.
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

			//circularBroserComponent�� �����Ѵ�.
			contentPane.add(
					circularBrowserComponent, BorderLayout.CENTER);
		} else {
			LinearBrowserComponent linearBrowserComponent
				 = new LinearBrowserComponent(gbInfo, monitor);
			linearBrowserComponent.monitorPanel.locusLabel.setText(
				"   "+ gbInfo.locus 
				+",  "+ gbInfo.getSeqLen() 
				+"bp,  "+ gbInfo.circular);

			//gene�� �׷��� Component�� panel ����� �����ش�.
			linearBrowserComponent.setPreferredSize(
				new Dimension(gbInfo.getSeqLen() / 200 + 50, 200));

			//scrollbar�� �÷��� scrollPane1�� ���� property�� �����Ѵ�.
			JScrollPane scrollPane1 = new JScrollPane();
			scrollPane1.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.getViewport().setBackground(
										new Color(255, 255, 255));
			scrollPane1.setWheelScrollingEnabled(true);

			//linearBroserComponent�� �����Ѵ�.
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
