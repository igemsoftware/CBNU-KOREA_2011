package mgBrowser06;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	JMenu zoomMenu;
	MonitorPanel designerMonitor;
	private int nCascade = 100;
	private String orgName;
	
	public DesignerBrowserFrame(int windowNum) {                  // BrowserFrame Ŭ������ ������
		DesignerBrowserFrame.windowNum = windowNum;
		this.designerPane = (JPanel)this.getContentPane();
		this.designerPane.setBackground(Color.white);
	    this.setLocation(590 + nCascade * DesignerBrowserFrame.windowNum, 0);          //minimal genome browser�� ��ġ
	   // DesignerBrowserFrame.windowNum = DesignerBrowserFrame.windowNum +1;
		this.setSize(new Dimension(590, 600));
		this.setTitle("" + "Circular Designer Browser");
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		fileMenu = new JMenu();                   // menuBar
		fileOpenMenuItem = new JMenuItem();
		fileSaveMenuItem = new JMenuItem();
		fileSaveAsMenuItem = new JMenuItem();
		fileExitMenuItem = new JMenuItem();
		
		zoomMenu = new JMenu();
		menuBar = new JMenuBar();
		
		menuBar.add(fileMenu);
		fileMenu.add(fileOpenMenuItem);
		fileMenu.add(fileSaveMenuItem);
		fileMenu.add(fileSaveAsMenuItem);
		fileMenu.add(fileExitMenuItem);
		menuBar.add(zoomMenu);
		
		fileMenu.setText("File");
		fileOpenMenuItem.setText("Open");
		fileOpenMenuItem.setEnabled(false);
		fileSaveMenuItem.setText("Save");
		fileSaveMenuItem.setEnabled(false);
		fileSaveAsMenuItem.setText("Save As");
		fileSaveAsMenuItem.setEnabled(false);
		fileExitMenuItem.setText("Exit");
		
		zoomMenu.setText("Zoom");
		zoomMenu.setEnabled(false);
		
/*		
		importMenuItem.addActionListener(                        // Essential Gene -> Minimal Genome�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMGFile();
				}
			}
		);
*/		
		fileExitMenuItem.addActionListener(                    // File->Exit�� ���� �̺�Ʈ ó��
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//System.exit(0);
					dispose();
				}
			}
		);
	
		this.setJMenuBar(menuBar);

		designerMonitor = new MonitorPanel();
		designerPane.add(designerMonitor, BorderLayout.NORTH);  //������ Object�� �ش� ��ġ�� ��ġ.

		openMGFile();
		
		}
	
	void openMGFile(){                                  
		
		JFileChooser fc = new JFileChooser();          //JFileChooser ��ü ����
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(DesignerBrowserFrame.this);
		if(returnVal != JFileChooser.APPROVE_OPTION ){
			return;
		}
		File selectedFile = fc.getSelectedFile();

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
					if (found) break;                  // ������ ������ ��찡 ����
				}
			}
				catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		DesignerGetInformation dGetInfo 
		= new DesignerGetInformation(selectedFile);

		while(designerPane.getComponentCount()>1){
			designerPane.remove(designerPane.getComponentCount()-1);
		}
		
		DesignerCircularMap designerCircular = new DesignerCircularMap(dGetInfo, designerMonitor, orgName);
		designerCircular.monitorPanel.locusLabel.setText(
				" " + dGetInfo.locus + ", "+ dGetInfo.seqLen + "bp; ");
		designerPane.add(designerCircular, BorderLayout.CENTER);   //circularBroserComponent�� �����Ѵ�.
	
		new DesignerLinearMap(selectedFile.getPath(), windowNum);
	}
	
}
