package mgBrowser06;

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
	
	JMenu compareGenomeMenu;
	JMenuItem compareToMGMenuItem;
	JMenuItem compareToMCMenuItem;
	
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
	
	//String viewOption[]={"OpenFile", "SynBDB", "replication", "import"};

	public String viewOption = "";
	int genomeOption;
	public String locus;
	public int seqLen;
	public String circular;
	public File selectedFile;
	LinearMap linearMap;
	public String orgName;
	public int initNum = 0;
	
	public BrowserFrame() {                  // BrowserFrame 클래스의 생성자
		initNum++;
		init();
		System.out.println("initNum = " + initNum);
	}
	
	private void init() /* throws Exception */ {                  //BrowserFrame 클래스의 초기화 메소드
		
		initNum++;
		System.out.println("initNum = " + initNum);
	
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
		
		compareGenomeMenu = new JMenu();
		compareToMGMenuItem = new JMenuItem ();
		compareToMCMenuItem = new JMenuItem() ;
		
		designerMenu = new JMenu();
		importMenuItem = new JMenuItem();
		//insertMenuItem = new JMenuItem();
		//deleteMenuItem = new JMenuItem();
		//substituteMenuItem = new JMenuItem();
		//translocateMenuItem = new JMenuItem();
		//recombinateMenuItem = new JMenuItem();
		
		helpMenu = new JMenu();
		helpAboutMenuItem = new JMenuItem();
		
	

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewOptionsMenu);
		menuBar.add(compareGenomeMenu);
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
		
		compareGenomeMenu.add(compareToMGMenuItem);
		compareGenomeMenu.add(compareToMCMenuItem);
		
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
		replicationMenuItem.setEnabled(false);
		egMenu.setText("Essential Genes");
		
		byDEGMenuItem.setText("by DEG");
		byDEGMenuItem.setEnabled(false);
		byEGGSMenuItem.setText("by EGGS");
		byEGGSMenuItem.setEnabled(false);
		bySynBDBMenuItem.setText("by SynB DB");
		bySynBDBMenuItem.setEnabled(false);
		
		compareGenomeMenu.setText("CompareGenome");
		compareToMGMenuItem.setText("compareToMG");
		compareToMCMenuItem.setText("compareToMC");
		
		
		designerMenu.setText("Designer");
		importMenuItem.setText("Import Template");
		//insertMenuItem.setText("Insert");
		//deleteMenuItem.setText("Delete");
		//substituteMenuItem.setText("Substitute");
		//translocateMenuItem.setText("Translocate");
		//recombinateMenuItem.setText("Recombinate");
	
		helpMenu.setText("Help");
		helpAboutMenuItem.setText("About");
		
		fileOpenMenuItem.addActionListener(                        // File->Open에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewOption = "OpenFile";
					openGenbankFile();
					genomeOption = getGenomeOption();

					if (genomeOption == 1) {
						bySynBDBMenuItem.setEnabled(true);
						replicationMenuItem.setEnabled(true);
					}
					if (genomeOption == 2) 
						replicationMenuItem.setEnabled(true);
					
					getOrgName();
					drawMap(genomeOption, viewOption);
				}
			}
		);

		replicationMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewOption = "replication";
					openGenbankFile();
					genomeOption = getGenomeOption();
					
					getOrgName();
					drawMap(genomeOption, viewOption);
					//	openMCFile();
				}
			}
		);
		
		bySynBDBMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewOption = "SynBDB";
					openGenbankFile();
					genomeOption = getGenomeOption();
					if (genomeOption == 1) {
						replicationMenuItem.setEnabled(true);
					}
					if (genomeOption == 2) 
						replicationMenuItem.setEnabled(true);
					
					getOrgName();
					drawMap(genomeOption, viewOption);
					//openEGFile();
				}
			}
		);
	
		compareToMGMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openDesinerBrowserFrame();
					}
				}
			);		

		compareToMCMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openDesinerBrowserFrame();
					}
				}
			);				
		
		importMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewOption = "import";
					genomeOption = getGenomeOption();
					getOrgName();
					drawMap(genomeOption, viewOption);
//						openMGFile();
				}
			}
		);
		
		fileExitMenuItem.addActionListener(                    // File->Exit에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);
		
		helpAboutMenuItem.addActionListener(                      // Help->About에 대한 이벤트 처리
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openAboutBox();
				}
			}
		);
		
		this.setJMenuBar(menuBar);

		monitor = new MonitorPanel();
		conPane.add(monitor, BorderLayout.NORTH);             //각각의 Object를 해당 위치에 배치.
	}

	void openGenbankFile(){                                   //File->Open 선택시 실행될 내용.
		
		JFileChooser fc = new JFileChooser();                 //JFileChooser 객체 생성 
		fc.setCurrentDirectory(new File("."));

		int returnVal = fc.showOpenDialog(BrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		selectedFile = fc.getSelectedFile();	
	}
		
	int getGenomeOption(){

		String sFile = selectedFile.getName();
		System.out.println("sFile= " + sFile);
		
		if (sFile.startsWith("NC")) {
			genomeOption = 1;
		}else if (sFile.startsWith("mg")) {
			genomeOption = 2;
		}else if (sFile.startsWith("mc")) {
			genomeOption = 3;
		}
		
		return genomeOption;	
			
	}
	
	
	void getOrgName(){

		try {
			FileReader fileReader = new FileReader(selectedFile);
			BufferedReader reader = new BufferedReader(fileReader);
			String line=null;
			try {
				boolean found = false;
				while((line=reader.readLine()) != null)	{
					if(line.startsWith("  ORGANISM")) {
						orgName = line.substring(12);
						found = true;
					}
					if (found) break;                  // 여러개 나오는 경우가 있음
				}
			}
				catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}	
	

	void drawMap(int genomeOption, String viewOption){                                   //File->Open 선택시 실행될 내용.

//		if (viewOption == "OpenFile") {	
			linearMap = new LinearMap(selectedFile.getPath());
//		}
		
		locus = linearMap.getLocus();
		seqLen = linearMap.getSeqLen();
		circular = linearMap.circular;
		
		System.out.println("seqLen= " + seqLen);

		System.out.println("viewOption= " + viewOption);
		
		//if (genomeOption ==1){
		while(conPane.getComponentCount()>1){               //기존에 add 되어 있는 Oject를 remove한다.
			conPane.remove(conPane.getComponentCount()-1);
			
		}
			
		CircularMap circularMap
			 = new CircularMap(viewOption, seqLen, genomeOption, monitor, orgName);
		
		circularMap.monitorPanel.locusLabel.setText(
			" " + locus + ", "+ seqLen + "bp; ");

		conPane.add(circularMap, BorderLayout.CENTER);   //circularBroserComponent를 적재한다.

	}
	
	public void openDesinerBrowserFrame(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
/*	
	void openMGFile(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
	
	void openMCFile(){
		
		new DesignerBrowserFrame(windowNum);
		windowNum = windowNum +1;
	}
*/	
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
