package genbankBrowser;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.math.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.text.*;

public class CircularBrowserComponent extends JComponent {
	GenbankFileInfo gbInfo;
	Vector gbColorList;
	MonitorPanel monitorPanel;

	//����, 2�� � �� 3�� ����κ��� �ۼ��� �н�.
	GeneralPath generalpath[];
	double centerX;
	double centerY;
	double radius;

	//CircularBrowserComponent Ŭ������ ������
	public CircularBrowserComponent(GenbankFileInfo gbInfo, MonitorPanel monitor) {
		this.gbInfo = gbInfo;
		this.monitorPanel = monitor;
		init(); //component�� �ʱ�ȭ
	}

	void init() {
		gbColorList = new Vector();
		System.out.println(gbInfo.getCount());
		for (int i = 0; i < gbInfo.getCount(); i++) {
			gbColorList.add(
					new Color( (int) (Math.random() * 255),
					(int) (Math.random() * 255),
					(int) (Math.random() * 255)));
		}

		//mouse�� gene���� �ö������� action�� �����Ѵ�.
		this.addMouseMotionListener(
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
										"   Gene Name  : "+ gbInfo.getGeneName(i));
									monitorPanel.geneStartLabel.setText(
										"   Gene Start : "+ gbInfo.getStartPos(i));
									monitorPanel.geneEndLabel.setText(
										"   Gene End   : "+ gbInfo.getEndPos(i));
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

		//���� �߽� ��ǥ ����
		centerX = frameSize.width / 2;
		centerY = frameSize.height / 2;
		
		//���� ���� �� ª�� ���� �������� ������ ����
		if(frameSize.width < frameSize.height)
			radius = frameSize.width / 2 - 20;
		else
			radius = frameSize.height / 2 - 20;

		//����� ��ǥ�� �������� �̿��Ͽ� Ÿ���� �׸���.
		g2.draw(new Ellipse2D.Double(
					frameSize.width / 2 - radius, 
					frameSize.height / 2 - radius,
					2 * radius,
					2 * radius));

		//gene�� �׸��� �޼ҵ带 ȣ���Ѵ�.
		drawGenes(g2);

		//ruler�� �׸���.
		drawRuler(g2);
	}

	public void drawGenes(Graphics2D g2) {
		generalpath = new GeneralPath[gbInfo.getCount()];
		double a = 2 * Math.PI / gbInfo.getSeqLen();

		for (int k = 0; k < gbInfo.getCount(); k++) {
			double geneStartPoint = (double)(gbInfo.getStartPos(k));
			double geneEndPoint = (double)(gbInfo.getEndPos(k));

			//gene�� ǥ���� �ٰ����� �� ��ǥ�� �����Ѵ�.
			double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.9;
			double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.9;
			double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.95;
			double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.95;
			double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.95;
			double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.95;
			double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.9;
			double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.9;

			//��ǥ��� �ٰ����� ������ �� �� ���� ������ �����Ѵ�.
			GeneralPath gp = new GeneralPath();
			gp.moveTo( (float) x1, (float) y1);
			gp.lineTo( (float) x2, (float) y2);
			gp.lineTo( (float) x3, (float) y3);
			gp.lineTo( (float) x4, (float) y4);
			gp.lineTo( (float) x1, (float) y1);
			generalpath[k] = gp;

			//drawShape �޼ҵ�� �׸� �ٰ����� ���� �ִ´�.
			g2.setColor((Color)gbColorList.get(k));
			g2.fill(generalpath[k]);
		}
	}

	public void drawRuler(Graphics2D g2) {
		g2.setColor(Color.black);
		// ���� ����
		int lastvalue = gbInfo.getSeqLen();

		// ���� �ϳ��� �������� ���� ����
		int decimalPoint = 1;
		String tmp = lastvalue +"";
		for (int i=0; i<tmp.length()-2; i++)
			decimalPoint *= 10;

		// ���� ����
		int mat = lastvalue / decimalPoint;

		// ���� �ϳ��� �������� ����
		double range = 2 * Math.PI / lastvalue * decimalPoint;

		double t;
		NumberFormat nf = NumberFormat.getInstance();

		Line2D largecount = new Line2D.Double();
		Line2D smallcount = new Line2D.Double();

		for (int k = 0; k <= mat; k++) {
			//������ �� line���� ǥ��
			t = -range * k;
			largecount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.96,
					centerY - radius * Math.cos(t) * 0.96);
			g2.draw(largecount);

			//ȭ�鿡�� ������ �����ϵ��� ���� 5������ ��ġ ǥ��
			if(k%5==0){
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

		for (int k = 0; k < mat; k++) {
			//���� �ϳ��� �ٽ� ���� ���� 5���� ����
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
		// ������ �κ��� ���� ����
		for (double j = 1; j < 5; j++) {
			t = -range * mat + j * -range / 5;

			if(radius * Math.sin(t) < 0) {
				break;	// ���� ������ �Ѿ�� ������.
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
}
