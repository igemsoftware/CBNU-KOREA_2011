package keeep;

import java.awt.*;
import javax.swing.*;

public class MyGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JPanel conPane;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem fileOpenMenuItem;
	JMenuItem fileExitMenuItem;

	JMenu designMenu;
	JMenuItem selectGeneMenuItem;
	JMenuItem funcGroupMenuItem;
	JMenuItem egMenuItem;
	
	JMenu smMenu;
	JMenuItem mcMenuItem;
	JMenuItem mgMenuItem;
	
	JMenu helpMenu;
	JMenuItem helpAboutMenuItem;

	Panel monitor;
	JLabel geneLabel;
	JLabel locusLabel;
	
	
	public MyGUI() {                  // BrowserFrame 클래스의 생성자
		init();
	}
	
	private void init() /* throws Exception */ { 
		
		Frame myFrame = new Frame();
	    JPanel mgPanel = new JPanel();
		this.conPane = (JPanel)this.getContentPane();
		mgPanel = (JPanel)this.getContentPane();
		
		
		
		this.conPane.setBackground(Color.white);
		this.setSize(new Dimension(1500, 800));
		this.setTitle("Genbank Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("" + "Essential Gene Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		myFrame.add(conPane);
		
		monitor = new Panel();
		locusLabel = new JLabel();
		geneLabel = new JLabel();
		monitor.add(geneLabel);
		monitor.add(locusLabel);
		geneLabel.setText("Essential Genes");
		locusLabel.setText("Minimal Chromosome");
		
		conPane.add(monitor);

		

		fileMenu = new JMenu();                   // menuBar
		fileOpenMenuItem = new JMenuItem();
		fileExitMenuItem = new JMenuItem();
	
		designMenu = new JMenu();
		selectGeneMenuItem = new JMenuItem();
		funcGroupMenuItem = new JMenuItem();
		egMenuItem = new JMenuItem();
		
		smMenu = new JMenu();
		mcMenuItem = new JMenuItem();
		mgMenuItem = new JMenuItem();
				
		helpMenu = new JMenu();
		helpAboutMenuItem = new JMenuItem();
	
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(designMenu);
		menuBar.add(smMenu);
		
		menuBar.add(helpMenu);
	
		fileMenu.add(fileOpenMenuItem);
		fileMenu.add(fileExitMenuItem);
		
		designMenu.add(selectGeneMenuItem);
		designMenu.add(funcGroupMenuItem);
		designMenu.add(egMenuItem);
		
		smMenu.add(mcMenuItem);
		smMenu.add(mgMenuItem);
	
		helpMenu.add(helpAboutMenuItem);
		

		fileMenu.setText("File");
		fileOpenMenuItem.setText("Open");
		fileExitMenuItem.setText("Exit");
		
		designMenu.setText("Designer");
		selectGeneMenuItem.setText("Select Genes");
		funcGroupMenuItem.setText("Select Functional Group");
		egMenuItem.setText("Essential Genes");
		
		smMenu.setText("Synthetic Map");
		mcMenuItem.setText("Minimal Chromosome Map");
		mgMenuItem.setText("Minimal Genome Map");
		
		helpMenu.setText("Help");
		helpAboutMenuItem.setText("About");
		this.setJMenuBar(menuBar);
		/* ------------------------- */

		monitor = new Panel();
        JPanel gPanel = new JPanel();

        JPanel lPanel = new JPanel();
        
		//각각의 Object를 해당 위치에 배치.
		
//        this.conPane.add(monitor, BorderLayout.NORTH);   //각각의 Object를 해당 위치에 배치.
        
 //       this.conPane.add(gPanel, BorderLayout.EAST);   
       
 //       this.conPane.add(mgPanel, BorderLayout.WEST);   
        
 //       this.conPane.add(lPanel, BorderLayout.SOUTH);   
        
		mgPanel.setBackground(Color.yellow);
		mgPanel.setSize(new Dimension(700, 600));
		
	
		JDesktopPane desktopPane = new JDesktopPane(); // 데스크탑 설정
	    this.conPane.add(desktopPane);  // 현재 프레임에 데스크탑 추가
	    //conPane.add(desktopPane1, BorderLayout.CENTER); 

		JInternalFrame internalFrame1 = new JInternalFrame();
		internalFrame1.getContentPane();       //내부 프레임을 생성
		desktopPane.add(internalFrame1); //원조 프레임에 내부 프레임을 추가한다.
		internalFrame1.setBackground(Color.lightGray);
		internalFrame1.setSize(700, 600);
		internalFrame1.setTitle("Genome Map");
		internalFrame1.setVisible(true);
/*
		//JDesktopPane desktopPane2 = new JDesktopPane(); // 데스크탑 설정
	   // conPane.add(desktopPane2);  // 현재 프레임에 데스크탑 추가
		JInternalFrame internalFrame2 = new JInternalFrame();
		internalFrame2.getContentPane();       //내부 프레임을 생성
		//desktopPane2.add(internalFrame2); //원조 프레임에 내부 프레임을 추가한다.
		mgPanel.add(internalFrame2);
		internalFrame2.setBackground(Color.lightGray);
		internalFrame2.setSize(700, 600);
		internalFrame2.setTitle("Minimal Genome Map");
		internalFrame2.setVisible(true);
*/
/*		
		JComponent c = (JComponent) internalFrame1.getContentPane();
	    c.add(new JButton(), BorderLayout.NORTH);
	    c.add(new JButton(), BorderLayout.CENTER);
*/	 
		
		
//		Insets insets = internalFrame1.getInsets();
//		insets.set(600, 600, 800, 800);
		
//		internalFrame2.pack();
		/*
				Rectangle bounds = new Rectangle(0, 0, -1, -1);
				     for (int i = 0; i < points.length; i++) {
				         bounds.add(points[i]);
				     }
				
				Rectangle bounds = new Rectangle(points[0]);
				     for (int i = 1; i < points.length; i++) {
				         bounds.add(points[i]);
				     }
		*/
				
//				internalFrame2.setNormalBounds(new Rectangle(800, 50, 1550, 600));	
	
	}
	
	public static void main(String[] args) {
		MyGUI myGUI = new MyGUI();

		//Application 실행시 화면 중앙에서 시작하도록 설정
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = myGUI.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		myGUI.setLocation(
				(screenSize.width - frameSize.width) / 2 ,    //화면 중앙
				(screenSize.height - frameSize.height) / 2);

		myGUI.setVisible(true);
	}
}

