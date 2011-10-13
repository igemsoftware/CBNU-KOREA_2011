package keeep;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import mgDB.Dbmanager;

import java.awt.geom.*;
import java.text.*;

public class CircularMap extends JComponent {

	Vector<String> geneNameVec = new Vector<String>();
	Vector<Integer> spVec = new Vector<Integer>();       //essential gene의 본래의 location
	Vector<Integer> epVec = new Vector<Integer>();
	Vector<Integer> strandVec = new Vector<Integer>();
	Vector<String> funCatVec = new Vector<String>();
	Vector gbColorList;
	Vector<Integer> egIndexVec = new Vector<Integer>();	
	
	
	MonitorPanel monitorPanel;
	String geneName;
	int genomeOption;
	
	public static JScrollPane jsp = new JScrollPane();   // from DNADraw
//	private Point location    = new Point();                                      
//	private Dimension border  = new Dimension(150,100);
	private Dimension panelSize = new Dimension(690,690);
	private Dimension linearPanelSize = new Dimension(800,350);
	private Hashtable lineAttr;   

	//직선, 2차 곡선 및 3차 곡선으로부터 작성된 패스.
	GeneralPath generalpath[];
	double centerX;
	double centerY;
	double radius;
	
	Dbmanager dm;
	
	private String orgName;
	private String viewOption;
	private String funColor;
	private int rColor;
	private int yColor;
	private int bColor;
	private int seqLen;

	//CircularBrowserComponent 클래스의 생성자
	public CircularMap(String viewOption, int seqLen, int genomeOption, MonitorPanel monitor, String orgName) {

		this.viewOption = viewOption;
		this.seqLen = seqLen;
		this.monitorPanel = monitor;
		this.genomeOption = genomeOption;
		this.orgName = orgName;
		
		init(); //component를 초기화
	}

	void init() {
		
		char funCat = 0;
		int i;
		gbColorList = new Vector();
		
		dm = new Dbmanager(genomeOption, orgName);         //필수 유전자 정보 DB에서 가져오기, oSPVec 생성
/*		
		bgSPVec = dm.getBgSPVec();
		bgEPVec = dm.getBgEPVec();
		funCatVec = dm.getFunCatVec();
		bgStrandVec = dm.getBgStrandVec();
		geneNameVec = dm.getGeneNameVec();
		mgSPVec = dm.getMgSPVec();
		mgEPVec = dm.getMgEPVec();
*/		
		spVec = dm.getSPVec();
		epVec = dm.getEPVec();
		strandVec = dm.getStrandVec();
		funCatVec = dm.getFunCatVec();
		egIndexVec = dm.getEgIndexVec();

		System.out.println("spVec.size= " + spVec.size());
		System.out.println("gbColorList.size= " + gbColorList.size());
		
		String str;
		if ((genomeOption == 1) && (viewOption == "OpenFile")) {
			for (i = 0; i < spVec.size(); i++) {
				if (strandVec.elementAt(i).intValue() == -1) 
					gbColorList.add(new Color(255, 0, 0));         //적색
				else if (strandVec.elementAt(i).intValue() == +1) 
					gbColorList.add(new Color(0, 0, 255));         //청색
			}
		}
			
    	if (((genomeOption != 1) && (viewOption == "OpenFile")) || (viewOption == "SynBDB")) {
				for (i = 0; i < funCatVec.size(); i++) {
					if (egIndexVec.elementAt(i).intValue() == +1) {
						funCat = funCatVec.elementAt(i).charAt(0);	    //funCat이 3개인 경우????????
						str = (String)funCatVec.elementAt(i);
						if ((str.startsWith("L")) || (str.endsWith("L")))
							funCat = 'L';
						else if ((str.startsWith("D")) || (str.endsWith("D")))
							funCat = 'D';
					}else funCat = 'X';

					funColor = getColor(funCat);
					rColor = Integer.parseInt(funColor.substring(0,3));
					yColor = Integer.parseInt(funColor.substring(3,6));
					bColor = Integer.parseInt(funColor.substring(6));
					gbColorList.add(new Color(rColor, yColor, bColor));  
				}
			}

		if (viewOption == "replication"){
			for (i = 0; i < funCatVec.size(); i++) {
				str = (String)funCatVec.elementAt(i);
				if ((str.startsWith("L")) || (str.endsWith("L")))
					funCat = 'L';
				else if ((str.startsWith("D")) || (str.endsWith("D")))
					funCat = 'D';
				else funCat = 'X';

				funColor = getColor(funCat);
				rColor = Integer.parseInt(funColor.substring(0,3));
				yColor = Integer.parseInt(funColor.substring(3,6));
				bColor = Integer.parseInt(funColor.substring(6));
				gbColorList.add(new Color(rColor, yColor, bColor)); 
			}
		}

		System.out.println();
		System.out.println("gbColorList.size= " + gbColorList.size());
		
		this.addMouseMotionListener(                               //mouse가 gene위에 올라갔을때의 action을 정의한다.
				new MouseMotionAdapter() {
					public void mouseMoved(MouseEvent e) {
						try {
							if (e.getX() >= 0) {
								for (int i = 0; i < generalpath.length; i++) {
									//마우스의 X,Y 포인트가 gene 범위 내에 속할경우.
									if (generalpath[i].contains((double) e.getX(), 
																(double) e.getY())) {
										//MonitorPanel의 geneStartLabel과 geneEndLabel에 값을 넣는다.
																			
										monitorPanel.geneLabel.setText(
											"Gene: " + dm.getGeneName(i) +
											"; Start: " + dm.getSP(i) +
											"; End: " + dm.getEP(i));
									}
								}
							}
						} catch (NullPointerException ne) {
							ne.printStackTrace();
						}
					}
				}
			);
		
//		if (viewOption != "OpenFile") repaint();
	}

	public String getColor(char funCat){
		
		switch (funCat) {
		
		case 'A':
			funColor = "252220252";       // "252, 220, 252"
			break;
		case 'B':
			funColor = "252220204";
			break;
		case 'C':
			funColor = "188252252";
			break;
		case 'D':
			funColor = "252252220";
			break;
		case 'E':
			funColor = "220252252";
			break;
		case 'F':
			funColor = "220236252";
			break;
		case 'G':
			funColor = "204252252";
			break;
		case 'H':
			funColor = "220220252";
			break;
		case 'I':
			funColor = "220204252";
			break;
		case 'J':
			funColor = "252204252";
			break;
		case 'K':
			funColor = "252220236";
			break;
		case 'L':
			funColor = "252220220";
			break;
		case 'M':
			funColor = "236252172";
			break;
		case 'N':
			funColor = "220252172";
			break;
		case 'O':
			funColor = "156252172";
			break;	
		case 'P':
			funColor = "204204252";
			break;
		case 'Q':
			funColor = "188204252";
			break;
		case 'R':
			funColor = "224224224";
			break;
		case 'S':
			funColor = "204204204";
			break;
		case 'T':
			funColor = "252252172";
			break;
		case 'U':
			funColor = "172252172";
			break;	
		case 'V':
			funColor = "252252188";
			break;	
		case 'W':
			funColor = "188252172";
			break;	
		case 'Y':
			funColor = "252252204";
			break;
		case 'Z':
			funColor = "204252172";
			break;
		case 'X':
			funColor = "255255255";
			break;
		default:
			funColor = "255255255";
			System.out.println("Functional Categories not found");
			break;
		}
		return funColor;
	}
	
	public void paint(Graphics g) {
		
		System.out.println("1");
		
		Graphics2D g2 = (Graphics2D) g;
		Dimension frameSize = this.getSize();
	
		centerX = frameSize.width / 2;                 //원의 중심 좌표 설정 
		centerY = frameSize.height / 2;
	
		if(frameSize.width < frameSize.height)         //폭과 높이 중 짧은 쪽을 기준으로 반지름 설정
			radius = frameSize.width / 2 - 20;         // - 20 원래 코드
		else
			radius = frameSize.height / 2 - 20;        // - 20 원래 코드
		
		g2.draw(new Ellipse2D.Double(                  //계산한 좌표와 반지름을 이용하여 타원을 그린다.
					frameSize.width / 2 - radius, 
					frameSize.height / 2 - radius,
					2 * radius,
					2 * radius));
		
		System.out.println("2");
		drawGenes(g2);         //gene을 그리는 메소드를 호출한다.
		drawRuler(g2);  		//ruler를 그린다.
		System.out.println("3");
	}

	public void drawGenes(Graphics2D g2) {
		generalpath = new GeneralPath[dm.getGeneNumber()];
		double a = 2 * Math.PI / seqLen;

		for (int k = 0; k < dm.getGeneNumber(); k++) {
			double geneStartPoint = (double)(dm.getSP(k));
			double geneEndPoint = (double)(dm.getEP(k));
			
			if (dm.getStrand(k)== -1) {                   //안쪽에 gene을 표시할 다각형의 각 좌표를 설정한다.
		
				double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.8;
				double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.8;
				double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.85;
				double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.85;
				double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.85;
				double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.85;
				double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.8;
				double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.8;
				
				GeneralPath gp = new GeneralPath();         //좌표대로 다각형을 설정한 후 그 영역 정보를 저장한다.
				gp.moveTo( (float) x1, (float) y1);
				gp.lineTo( (float) x2, (float) y2);
				gp.lineTo( (float) x3, (float) y3);
				gp.lineTo( (float) x4, (float) y4);
				gp.lineTo( (float) x1, (float) y1);
				generalpath[k] = gp;
		
				g2.setColor((Color)gbColorList.get(k));     //drawShape 메소드로 그린 다각형에 색을 넣는다.
				g2.fill(generalpath[k]);
				
			} else if (dm.getStrand(k)== +1) {                                       //바깥쪽에
								
				double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.9;
				double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.9;
				double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.95;
				double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.95;
				double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.95;
				double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.95;
				double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.9;
				double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.9;
			
				GeneralPath gp = new GeneralPath();       //좌표대로 다각형을 설정한 후 그 영역 정보를 저장한다.
				gp.moveTo( (float) x1, (float) y1);
				gp.lineTo( (float) x2, (float) y2);
				gp.lineTo( (float) x3, (float) y3);
				gp.lineTo( (float) x4, (float) y4);
				gp.lineTo( (float) x1, (float) y1);
				generalpath[k] = gp;
			
				g2.setColor((Color)gbColorList.get(k));     //drawShape 메소드로 그린 다각형에 색을 넣는다.
				g2.fill(generalpath[k]);
			}
		}
	}

	public void drawRuler(Graphics2D g2) {
		
		g2.setColor(Color.black);
		
		int lastvalue = seqLen;  // 서열 길이
		int decimalPoint = 1;                 // 눈금 하나의 서열상의 길이 설정
		String tmp = lastvalue +"";
		for (int i=0; i<tmp.length()-2; i++)
			decimalPoint *= 10;
	
		int mat = lastvalue / decimalPoint;   // 눈금 개수
		double range = 2 * Math.PI / lastvalue * decimalPoint;   // 눈금 하나의 지도상의 길이
		double t;
		NumberFormat nf = NumberFormat.getInstance();

		Line2D largecount = new Line2D.Double();
		Line2D smallcount = new Line2D.Double();

		for (int k = 0; k <= mat; k++) {                   //눈금을 긴 line으로 표시
			t = -range * k;
			largecount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.96,
					centerY - radius * Math.cos(t) * 0.96);
			g2.draw(largecount);

			if(k%5==0){                                      			//화면에서 구분이 가능하도록 눈금 5개마다 위치 표시
				t = -range * k;
				if ( (centerX - radius * Math.sin(t) * 1.02) >= centerX) {
					if (centerY - radius * Math.cos(t) * 1.02 >= centerY) {
						g2.drawString(nf.format(decimalPoint * k),
							(int) (centerX - radius * Math.sin(t) * 1.02),
							(int) (centerY - radius * Math.cos(t) * 1.02) + 10);
					} else {
						g2.drawString(nf.format(decimalPoint * k),
							(int) (centerX - radius * Math.sin(t) * 1.02),
							(int) (centerY - radius * Math.cos(t) * 1.02));
					}
				} else {
					if (centerY - radius * Math.cos(t) * 1.02 >= centerY) {
						g2.drawString(nf.format(decimalPoint * k),
							(int) (centerX - radius * Math.sin(t) * 1.02) - 50,
							(int) (centerY - radius * Math.cos(t) * 1.02) + 10);
					} else {
						g2.drawString(nf.format(decimalPoint * k),
							(int) (centerX - radius * Math.sin(t) * 1.02) - 50,
							(int) (centerY - radius * Math.cos(t) * 1.02));
					}
				} //else
			}//if(k%5==0)
		}

		for (int k = 0; k < mat; k++) {                //눈금 하나를 다시 작은 눈금 5개로 나눔
			for (double j = 1; j < 5; j++) {
				t = -range * k + j * -range / 5;

				smallcount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.98,
					centerY - radius * Math.cos(t) * 0.98);
				g2.draw(smallcount);
			}
		}
		
		for (double j = 1; j < 5; j++) {                   // 마지막 부분의 작은 눈금
			t = -range * mat + j * -range / 5;

			if(radius * Math.sin(t) < 0) {
				break;	                                  // 서열 범위를 넘어서면 끝낸다.
			} else {
				smallcount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.98,
					centerY - radius * Math.cos(t) * 0.98);
				g2.draw(smallcount);
			}
		}
	}
	
	  protected void zoomIn()
	  {
	    int wid = getWidth();
	    wid     = wid+(int)(wid*0.1);
	    int hgt = getHeight();
	    if(isCircular())
	      hgt = hgt+(int)(hgt*0.1);
	    zoom(wid,hgt);
	  }


	  protected void zoomOut()
	  {
	    int wid = getWidth();
	    wid     = wid-(int)(wid*0.1);
	    int hgt = getHeight();  
	    if(isCircular())
	      hgt = hgt-(int)(hgt*0.1);
	    zoom(wid,hgt);
	  }

	  
	  private void zoom(int wid, int hgt)
	  {
	    if(isCircular())
	    {
	      panelSize = new Dimension(wid,hgt);
	      setPreferredSize(panelSize);
	      setSize(panelSize);
	    }
	    else
	    {
	      linearPanelSize = new Dimension(wid,hgt);
	      setPreferredSize(linearPanelSize);
	      setSize(linearPanelSize);
	    }
	    repaint();
	  }


	  protected boolean isCircular()
	  {
	    return ((Boolean)lineAttr.get("circular")).booleanValue();
	  }
}
