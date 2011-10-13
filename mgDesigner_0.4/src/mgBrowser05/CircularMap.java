package mgBrowser05;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import java.awt.geom.*;
import java.text.*;

public class CircularMap extends JComponent {
	
	GetInformation getInfo;
	Vector gbColorList;
	MonitorPanel monitorPanel;
	
	public static JScrollPane jsp = new JScrollPane();   // from DNADraw
	private Hashtable lineAttr;                            
	private Point location    = new Point();                                      
	private Dimension border  = new Dimension(150,100);
	private Dimension panelSize = new Dimension(690,690);
	private Dimension linearPanelSize = new Dimension(800,350);
	
	

	//����, 2�� � �� 3�� ����κ��� �ۼ��� �н�.
	GeneralPath generalpath[];
	double centerX;
	double centerY;
	double radius;

	//CircularBrowserComponent Ŭ������ ������
	public CircularMap(GetInformation getInfo, MonitorPanel monitor) {

	    lineAttr = new Hashtable();                   // from DNADraw
	    lineAttr.put("start",new Integer(0));
	    lineAttr.put("end",new Integer(4000));
	    lineAttr.put("lsize",new Integer(5));
	    lineAttr.put("circular",new Boolean(true));
		
		
		this.getInfo = getInfo;
		this.monitorPanel = monitor;
		init(); //component�� �ʱ�ȭ
	}

	void init() {
		
		char funcCat;
		int i;
		gbColorList = new Vector();
		
		String sFile = getInfo.getFileName();
		if ((sFile.startsWith("mc")) || (sFile.startsWith("mg"))) {
			String str;
			for (i = 0; i < getInfo.egFuncCatVec.size(); i++) {
				funcCat = getInfo.egFuncCatVec.elementAt(i).charAt(0);	
				str = (String)getInfo.egFuncCatVec.elementAt(i);
				if (str.endsWith("L")) funcCat = 'L';
				if (str.endsWith("D")) funcCat = 'D';
				
				switch (funcCat) {
				
				case 'A':
					gbColorList.add(new Color(255, 255, 255));
					break;
				case 'B':
					gbColorList.add(new Color(255, 255, 255));
					break;
				case 'C':
					gbColorList.add(new Color(102, 000, 153));
					break;
				case 'D':
					gbColorList.add(new Color(000, 000, 255));
					break;
				case 'E':
					gbColorList.add(new Color(204, 204, 000));
					break;
				case 'F':
					gbColorList.add(new Color(051, 102, 102));
					break;
				case 'G':
					gbColorList.add(new Color(204, 051, 255));
					break;
				case 'H':
					gbColorList.add(new Color(255, 255, 000));
					break;
				case 'I':
					gbColorList.add(new Color(153, 102, 255));
					break;
				case 'J':
					gbColorList.add(new Color(051, 051, 051));
					break;
				case 'K':
					gbColorList.add(new Color(051, 051, 000));
					break;
				case 'L':
					gbColorList.add(new Color(255, 000, 000));
					break;
				case 'M':
					gbColorList.add(new Color(000, 255, 000));
					break;
				case 'N':
					gbColorList.add(new Color(000, 255, 204));
					break;
				case 'O':
					gbColorList.add(new Color(255, 000, 255));
					break;	
				case 'P':
					gbColorList.add(new Color(051, 000, 000));
					break;
				case 'Q':
					gbColorList.add(new Color(051, 255, 153));
					break;
				case 'R':
					gbColorList.add(new Color(204, 102, 051));
					break;
				case 'S':
					gbColorList.add(new Color(255, 102, 000));
					break;
				case 'T':
					gbColorList.add(new Color(255, 000, 000));
					break;
				case 'U':
					gbColorList.add(new Color(255, 255, 255));
					break;	
				case 'V':
					gbColorList.add(new Color(255, 255, 255));
					break;	
				case 'W':
					gbColorList.add(new Color(255, 255, 255));
					break;	
				case 'Y':
					gbColorList.add(new Color(255, 255, 255));
					break;
				case 'Z':
					gbColorList.add(new Color(255, 255, 255));
					break;
				case 'X':
					gbColorList.add(new Color(255, 255, 255));
					break;
				default:
					System.out.println("Functional Categories not found");
					//gbColorList.add(colour);
					break;
				}
			}	
		}else{
			for (i = 0; i < getInfo.getGeneNumber(); i++) {
				if (getInfo.getStrand(i)== -1) 
					gbColorList.add(new Color(255, 0, 0));     //����
				else if (getInfo.getStrand(i)== +1) 
					gbColorList.add(new Color(0, 0, 255));     //û��
			}
		}

		System.out.println("gbColorList.size= " + gbColorList.size());
		
		this.addMouseMotionListener(                               //mouse�� gene���� �ö������� action�� �����Ѵ�.
				new MouseMotionAdapter() {
					public void mouseMoved(MouseEvent e) {
						try {
							if (e.getX() >= 0) {
								for (int i = 0; i < generalpath.length; i++) {
									//���콺�� X,Y ����Ʈ�� gene ���� ���� ���Ұ��.
									if (generalpath[i].contains((double) e.getX(), 
																(double) e.getY())) {
										//MonitorPanel�� geneStartLabel�� geneEndLabel�� ���� �ִ´�.
										monitorPanel.geneLabel.setText(
											"   Gene Name  : "+ getInfo.getGeneName(i));
										monitorPanel.geneStartLabel.setText(
											"   Gene Start : "+ getInfo.getSP(i));
										monitorPanel.geneEndLabel.setText(
											"   Gene End   : "+ getInfo.getEP(i));
									}
								}
							}
						} catch (NullPointerException ne) {
							ne.printStackTrace();
						}
					}
				}
			);
	}

	public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		Dimension frameSize = this.getSize();
	
		centerX = frameSize.width / 2;                 //���� �߽� ��ǥ ���� 
		centerY = frameSize.height / 2;
	
		if(frameSize.width < frameSize.height)         //���� ���� �� ª�� ���� �������� ������ ����
			radius = frameSize.width / 2 - 20;         // - 20 ���� �ڵ�
		else
			radius = frameSize.height / 2 - 20;        // - 20 ���� �ڵ�
		
		g2.draw(new Ellipse2D.Double(                  //����� ��ǥ�� �������� �̿��Ͽ� Ÿ���� �׸���.
					frameSize.width / 2 - radius, 
					frameSize.height / 2 - radius,
					2 * radius,
					2 * radius));
		
		drawGenes(g2);         //gene�� �׸��� �޼ҵ带 ȣ���Ѵ�.
		drawRuler(g2);  		//ruler�� �׸���.
	}

	public void drawGenes(Graphics2D g2) {
		generalpath = new GeneralPath[getInfo.getGeneNumber()];
		double a = 2 * Math.PI / getInfo.getSeqLen();

		for (int k = 0; k < getInfo.getGeneNumber(); k++) {
			double geneStartPoint = (double)(getInfo.getSP(k));
			double geneEndPoint = (double)(getInfo.getEP(k));
			
			if (getInfo.getStrand(k)== -1) {                   //���ʿ� gene�� ǥ���� �ٰ����� �� ��ǥ�� �����Ѵ�.
		
				double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.8;
				double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.8;
				double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.85;
				double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.85;
				double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.85;
				double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.85;
				double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.8;
				double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.8;
				
				GeneralPath gp = new GeneralPath();         //��ǥ��� �ٰ����� ������ �� �� ���� ������ �����Ѵ�.
				gp.moveTo( (float) x1, (float) y1);
				gp.lineTo( (float) x2, (float) y2);
				gp.lineTo( (float) x3, (float) y3);
				gp.lineTo( (float) x4, (float) y4);
				gp.lineTo( (float) x1, (float) y1);
				generalpath[k] = gp;
		
				g2.setColor((Color)gbColorList.get(k));     //drawShape �޼ҵ�� �׸� �ٰ����� ���� �ִ´�.
				g2.fill(generalpath[k]);
				
			} else if (getInfo.getStrand(k)== +1) {                                       //�ٱ��ʿ�
								
				double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.9;
				double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.9;
				double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.95;
				double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.95;
				double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.95;
				double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.95;
				double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.9;
				double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.9;
			
				GeneralPath gp = new GeneralPath();       //��ǥ��� �ٰ����� ������ �� �� ���� ������ �����Ѵ�.
				gp.moveTo( (float) x1, (float) y1);
				gp.lineTo( (float) x2, (float) y2);
				gp.lineTo( (float) x3, (float) y3);
				gp.lineTo( (float) x4, (float) y4);
				gp.lineTo( (float) x1, (float) y1);
				generalpath[k] = gp;
			
				g2.setColor((Color)gbColorList.get(k));     //drawShape �޼ҵ�� �׸� �ٰ����� ���� �ִ´�.
				g2.fill(generalpath[k]);
			}
		}
	}

	public void drawRuler(Graphics2D g2) {
		
		g2.setColor(Color.black);
		
		int lastvalue = getInfo.getSeqLen();  // ���� ����
		int decimalPoint = 1;                 // ���� �ϳ��� �������� ���� ����
		String tmp = lastvalue +"";
		for (int i=0; i<tmp.length()-2; i++)
			decimalPoint *= 10;
	
		int mat = lastvalue / decimalPoint;   // ���� ����
		double range = 2 * Math.PI / lastvalue * decimalPoint;   // ���� �ϳ��� �������� ����
		double t;
		NumberFormat nf = NumberFormat.getInstance();

		Line2D largecount = new Line2D.Double();
		Line2D smallcount = new Line2D.Double();

		for (int k = 0; k <= mat; k++) {                   //������ �� line���� ǥ��
			t = -range * k;
			largecount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.96,
					centerY - radius * Math.cos(t) * 0.96);
			g2.draw(largecount);

			if(k%5==0){                                      			//ȭ�鿡�� ������ �����ϵ��� ���� 5������ ��ġ ǥ��
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

		for (int k = 0; k < mat; k++) {                //���� �ϳ��� �ٽ� ���� ���� 5���� ����
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
		
		for (double j = 1; j < 5; j++) {                   // ������ �κ��� ���� ����
			t = -range * mat + j * -range / 5;

			if(radius * Math.sin(t) < 0) {
				break;	                                  // ���� ������ �Ѿ�� ������.
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
