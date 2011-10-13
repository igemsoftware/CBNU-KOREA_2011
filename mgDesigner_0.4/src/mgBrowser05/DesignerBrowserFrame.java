package mgBrowser05;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

public class DesignerBrowserFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int windowNum = 0;
	
//	Container container;
	
	JPanel designerPane;

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
	JMenuItem insertMenuItem;
	JMenuItem deleteMenuItem;
	JMenuItem substituteMenuItem;
	JMenuItem translocateMenuItem;
	JMenuItem recombinateMenuItem;
	
	MonitorPanel designerMonitor;
	
	private int nCascade = 100;
	
	public DesignerBrowserFrame(int windowNum) {                  // BrowserFrame 클래스의 생성자
		DesignerBrowserFrame.windowNum = windowNum;
		this.designerPane = (JPanel)this.getContentPane();
		this.designerPane.setBackground(Color.white);
	    this.setLocation(590 + nCascade * DesignerBrowserFrame.windowNum, 0);          //minimal genome browser의 위치
	   // DesignerBrowserFrame.windowNum = DesignerBrowserFrame.windowNum +1;
		this.setSize(new Dimension(590, 600));
		this.setTitle("" + "Circular Designer Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
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
		insertMenuItem = new JMenuItem();
		deleteMenuItem = new JMenuItem();
		substituteMenuItem = new JMenuItem();
		translocateMenuItem = new JMenuItem();
		recombinateMenuItem = new JMenuItem();
		
/*		
		importMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMGFile();
				}
			}
		);
*/		
		fileExitMenuItem.addActionListener(                    // File->Exit에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//System.exit(0);
					dispose();
				}
			}
		);
		
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewOptionsMenu);
		menuBar.add(designerMenu);
		
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
		designerMenu.add(insertMenuItem);
		designerMenu.add(deleteMenuItem);
		designerMenu.add(substituteMenuItem);
		designerMenu.add(translocateMenuItem);
		designerMenu.add(recombinateMenuItem);

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
		insertMenuItem.setText("Insert");
		deleteMenuItem.setText("Delete");
		substituteMenuItem.setText("Substitute");
		translocateMenuItem.setText("Translocate");
		recombinateMenuItem.setText("Recombinate");
		
		this.setJMenuBar(menuBar);

		designerMonitor = new MonitorPanel();
		designerPane.add(designerMonitor, BorderLayout.NORTH);  //각각의 Object를 해당 위치에 배치.

		openMGFile();
		
		}
	
	void openMGFile(){                                  
		
		JFileChooser fc = new JFileChooser();          //JFileChooser 객체 생성
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(DesignerBrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		File selectedFile = fc.getSelectedFile();

		DesignerGetInformation dGetInfo 
		= new DesignerGetInformation(selectedFile.getPath());

		while(designerPane.getComponentCount()>1){
			designerPane.remove(designerPane.getComponentCount()-1);
		}
		
		DesignerCircularMap designerCircular = new DesignerCircularMap(dGetInfo, designerMonitor);
		designerCircular.monitorPanel.locusLabel.setText(
				"   "+ dGetInfo.locus
				+",  "+ dGetInfo.getSeqLen()
				+"bp,  "+ dGetInfo.circular);
			
		designerPane.add(designerCircular, BorderLayout.CENTER);   //circularBroserComponent를 적재한다.
	
		new DesignerLinearMap(selectedFile.getPath(), windowNum);

	}
	
}
