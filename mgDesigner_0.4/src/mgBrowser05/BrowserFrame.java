package mgBrowser05;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class BrowserFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int windowNum = 0;
	
	private JPanel conPane;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem fileOpenMenuItem;
	JMenuItem fileSaveMenuItem;
	JMenuItem fileSaveAsMenuItem;
	JMenuItem fileExitMenuItem;
	
	JMenu viewOptionsMenu;
	JMenu selectGeneMenu;
	
	JMenu funcGroupMenu;
	JMenuItem byCOGMenuItem;
	JMenu bySynBGroupMenu;
	JMenuItem replicationMenuItem;
	
	JMenu egMenu;
	JMenuItem byDEGMenuItem;
	JMenuItem byEGGSMenuItem;
	JMenuItem bySynBDBMenuItem;
	
	JMenu designerMenu;
	JMenuItem importMenuItem;
/*	
	JMenuItem insertMenuItem;
	JMenuItem deleteMenuItem;
	JMenuItem substituteMenuItem;
	JMenuItem translocateMenuItem;
	JMenuItem recombinateMenuItem;
*/
	JMenu helpMenu;
	JMenuItem helpAboutMenuItem;

	MonitorPanel monitor, designerMonitor;
	
	public BrowserFrame() {                  // BrowserFrame Ŭ������ ������
		
		init();
	}
	
	private void init() /* throws Exception */ {                  //BrowserFrame Ŭ������ �ʱ�ȭ �޼ҵ�
		
		this.conPane = (JPanel)this.getContentPane();
		this.conPane.setBackground(Color.white);
		this.setSize(new Dimension(590, 600));
		this.setTitle("" + "Circular Genome Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		fileMenu = new JMenu();                   // menuBar
		fileOpenMenuItem = new JMenuItem();
		fileSaveMenuItem = new JMenuItem();
		fileSaveAsMenuItem = new JMenuItem();
		fileExitMenuItem = new JMenuItem();
	
		viewOptionsMenu = new JMenu();
		
		selectGeneMenu = new JMenu();
		
		funcGroupMenu = new JMenu();
		byCOGMenuItem = new JMenuItem();
		bySynBGroupMenu = new JMenu();
		replicationMenuItem = new JMenuItem();
		
		egMenu = new JMenu();
		byDEGMenuItem = new JMenuItem();
		byEGGSMenuItem = new JMenuItem();
		bySynBDBMenuItem = new JMenuItem();
		
		designerMenu = new JMenu();
		importMenuItem = new JMenuItem();
		//insertMenuItem = new JMenuItem();
		//deleteMenuItem = new JMenuItem();
		//substituteMenuItem = new JMenuItem();
		//translocateMenuItem = new JMenuItem();
		//recombinateMenuItem = new JMenuItem();
		
		helpMenu = new JMenu();
		helpAboutMenuItem = new JMenuItem();
		
		fileOpenMenuItem.addActionListener(                        // File->Open�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openGenbankFile();
				}
			}
		);
		
		replicationMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome�� ���� �̺�Ʈ ó��
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openMCFile();
					}
				}
			);
		
		bySynBDBMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome�� ���� �̺�Ʈ ó��
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openEGFile();
					}
				}
			);
		
		importMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMGFile();
				}
			}
		);
		
		fileExitMenuItem.addActionListener(                    // File->Exit�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);
		
		helpAboutMenuItem.addActionListener(                      // Help->About�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openAboutBox();
				}
			}
		);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewOptionsMenu);
		menuBar.add(designerMenu);
		
		menuBar.add(helpMenu);

		fileMenu.add(fileOpenMenuItem);
		fileMenu.add(fileSaveMenuItem);
		fileMenu.add(fileSaveAsMenuItem);
		fileMenu.add(fileExitMenuItem);
		
		viewOptionsMenu.add(selectGeneMenu);
		viewOptionsMenu.add(funcGroupMenu);
		viewOptionsMenu.add(egMenu);
		
		funcGroupMenu.add(byCOGMenuItem);
		funcGroupMenu.add(bySynBGroupMenu);
		bySynBGroupMenu.add(replicationMenuItem);
				
		egMenu.add(byDEGMenuItem);
		egMenu.add(byEGGSMenuItem);
		egMenu.add(bySynBDBMenuItem);
		
		designerMenu.add(importMenuItem);
		//designerMenu.add(insertMenuItem);
		//designerMenu.add(deleteMenuItem);
		//designerMenu.add(substituteMenuItem);
		//designerMenu.add(translocateMenuItem);
		//designerMenu.add(recombinateMenuItem);

		helpMenu.add(helpAboutMenuItem);

		fileMenu.setText("File");
		fileOpenMenuItem.setText("Open");
		fileSaveMenuItem.setText("Save");
		fileSaveMenuItem.setEnabled(false);
		fileSaveAsMenuItem.setText("Save As");
		fileSaveAsMenuItem.setEnabled(false);
		fileExitMenuItem.setText("Exit");
		
		viewOptionsMenu.setText("View Options");
		
		selectGeneMenu.setText("Select Genes");
		
		funcGroupMenu.setText("Select Functional Group");
		byCOGMenuItem.setText("by COG");
		byCOGMenuItem.setEnabled(false);
		
		bySynBGroupMenu.setText("by SynB Group");
		replicationMenuItem.setText("Replication");
		
		egMenu.setText("Essential Genes");
		
		byDEGMenuItem.setText("by DEG");
		byDEGMenuItem.setEnabled(false);
		byEGGSMenuItem.setText("by EGGS");
		byEGGSMenuItem.setEnabled(false);
		bySynBDBMenuItem.setText("by SynB DB");
		
		designerMenu.setText("Designer");
		importMenuItem.setText("Import Template");
		//insertMenuItem.setText("Insert");
		//deleteMenuItem.setText("Delete");
		//substituteMenuItem.setText("Substitute");
		//translocateMenuItem.setText("Translocate");
		//recombinateMenuItem.setText("Recombinate");
	
		helpMenu.setText("Help");
		helpAboutMenuItem.setText("About");
		this.setJMenuBar(menuBar);

		monitor = new MonitorPanel();
		conPane.add(monitor, BorderLayout.NORTH);             //������ Object�� �ش� ��ġ�� ��ġ.
	}

	void openGenbankFile(){                                   //File->Open ���ý� ����� ����.
		
		JFileChooser fc = new JFileChooser();                 //JFileChooser ��ü ���� 
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(BrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		File selectedFile = fc.getSelectedFile();
			
		GetInformation getInfo = new GetInformation(selectedFile.getPath());  // GenBank ���Ͽ��� �����ڸ���� ������ ���
			
		while(conPane.getComponentCount()>1){               //������ add �Ǿ� �ִ� Oject�� remove�Ѵ�.
			conPane.remove(conPane.getComponentCount()-1);
		}

		CircularMap circularMap
			 = new CircularMap(getInfo, monitor);
		circularMap.monitorPanel.locusLabel.setText(
			"   "+ getInfo.locus
			+",  "+ getInfo.getSeqLen()
			+"bp,  "+ getInfo.circular);

		
		conPane.add(circularMap, BorderLayout.CENTER);   //circularBroserComponent�� �����Ѵ�.

		new LinearMap(selectedFile.getPath());
	}
	
	void openEGFile(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
	
	void openMGFile(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
	
	void openMCFile(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
	
	void openAboutBox() {
		AboutBox about = new AboutBox(this, "Minimal Genome Designer version 0.2");
		Dimension d1 = about.getSize();
		Dimension d2 = this.getSize();
		int x = Math.max( (d2.width - d1.width) / 2, 0);
		int y = Math.max( (d2.height - d1.height) / 2, 0);
		about.setBounds(x + this.getX(), 
						y + this.getY(), 
						d1.width,	
						d1.height);
		about.setVisible(true);
	}
}