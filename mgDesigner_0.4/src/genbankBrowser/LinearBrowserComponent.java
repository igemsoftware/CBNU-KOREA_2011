package genbankBrowser;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.math.*;
import java.awt.geom.*;

public class LinearBrowserComponent extends JComponent {
	int zoomSize;
	MonitorPanel monitorPanel;
	GenbankFileInfo gbInfo;
	Vector gbColorList;

	Rectangle2D.Double rectangle2d[];

	public LinearBrowserComponent(GenbankFileInfo gbInfo, MonitorPanel monitor) {
		this.gbInfo = gbInfo;
		this.monitorPanel = monitor;
		init(); //component�� �ʱ�ȭ
	}

	public void init() {
		zoomSize = 200;	//200bp�� 1pixel�� ǥ��
		int startPoint = 0;
		int endPoint = 0;
		int length = 0;
		gbColorList = new Vector();
		// gene�� ȭ����� ��ǥ������ ������ �迭 ����
		rectangle2d = new Rectangle2D.Double[gbInfo.getCount()];

		for (int i = 0; i < gbInfo.getCount(); i++) {
			startPoint = gbInfo.getStartPos(i);
			endPoint = gbInfo.getEndPos(i);
			length = endPoint - startPoint;

			//Rectangle2D.Double�� �μ��� x��, y��, width, height�� �־��ش�.
			rectangle2d[i] = new Rectangle2D.Double(
					(startPoint / zoomSize),
					50,
					length / zoomSize,
					30);
			//gene�� ���� ���� ����
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
							for (int i = 0; i < rectangle2d.length; i++) {
								//���콺�� X,Y ����Ʈ�� gene ���� ���� ���Ұ��.
								if (rectangle2d[i].contains((double) e.getX(), 
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
		//gene�� �׸���.
		drawGenes(g2);
		//ruler�� �׸���.
		drawRuler(g2);
	}

	public void drawGenes(Graphics2D g2) {
		for (int i = 0; i < rectangle2d.length; i++) {
			//�� gene�� �簢������ ȭ�鿡 �׸���.
			g2.setColor((Color)gbColorList.get(i));
			g2.fill(rectangle2d[i]);
		}
	}

	//gene�� ��ġ�� ǥ���� Ruller.
	public void drawRuler(Graphics2D g2) {
		//Ruler�� �ִ밪�� �˾Ƴ���.
		int lastvalue = gbInfo.getSeqLen();

		//Line�� ���⸦ ����
		BasicStroke lStroke1 = new BasicStroke(1);
		BasicStroke lStroke2 = new BasicStroke(2);
		BasicStroke lStroke3 = new BasicStroke(3);

		Line2D line1 = new Line2D.Double();

		//Line�� Color�� ����ü�� ����.
		g2.setColor(Color.black);
		g2.setFont(new Font("Times New Roman", 0, 10));
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//lineŬ������ setLine �޼ҵ带 �̿��Ͽ� �׷��� Line�� �����Ѵ�.
		g2.setStroke(lStroke2);
		line1.setLine(0, 100, lastvalue/zoomSize, 100);
		g2.draw(line1);

		// ���۰� �� ǥ��
		g2.setStroke(lStroke3);
		g2.drawString("0", 5, 113);
		line1.setLine(0, 93, 0, 107);
		g2.draw(line1);
		g2.drawString(lastvalue+"",
				(int) ( (lastvalue/ zoomSize) + 5),
				113);
		line1.setLine( (lastvalue/ zoomSize),
				93,
				(lastvalue/ zoomSize),
				107);
		g2.draw(line1);

		//�� 5�� °�� �߰���, �������� ���������� ǥ��
		int i = 1;
		while ( zoomSize * 50 * i < lastvalue) {
			if (i % 5 == 0) {
				g2.drawString(i * 10000 + "", 50 * i - 10, 123);
				g2.setStroke(lStroke2);
				line1.setLine(50 * i, 93, 50 * i, 107);
				g2.draw(line1);
			} else {
				g2.drawString(i * 10000 + "", 50 * i -10, 123);
				g2.setStroke(lStroke1);
				line1.setLine(50 * i, 93, 50 * i, 107);
				g2.draw(line1);
			}
			i++;
		}
	}
}
