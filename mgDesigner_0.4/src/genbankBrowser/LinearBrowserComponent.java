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
		init(); //component를 초기화
	}

	public void init() {
		zoomSize = 200;	//200bp를 1pixel로 표시
		int startPoint = 0;
		int endPoint = 0;
		int length = 0;
		gbColorList = new Vector();
		// gene의 화면상의 좌표정보를 저장할 배열 정의
		rectangle2d = new Rectangle2D.Double[gbInfo.getCount()];

		for (int i = 0; i < gbInfo.getCount(); i++) {
			startPoint = gbInfo.getStartPos(i);
			endPoint = gbInfo.getEndPos(i);
			length = endPoint - startPoint;

			//Rectangle2D.Double의 인수로 x축, y축, width, height를 넣어준다.
			rectangle2d[i] = new Rectangle2D.Double(
					(startPoint / zoomSize),
					50,
					length / zoomSize,
					30);
			//gene의 색깔 정보 저장
			gbColorList.add(
					new Color( (int) (Math.random() * 255),
					(int) (Math.random() * 255),
					(int) (Math.random() * 255)));
		}

		//mouse가 gene위에 올라갔을때의 action을 정의한다.
		this.addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e) {
					try {
						if (e.getX() >= 0) {
							for (int i = 0; i < rectangle2d.length; i++) {
								//마우스의 X,Y 포인트가 gene 범위 내에 속할경우.
								if (rectangle2d[i].contains((double) e.getX(), 
															(double) e.getY())) {
									//MonitorPanel의 geneStartLabel과 geneEndLabel에 값을 넣는다.
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
		//gene을 그린다.
		drawGenes(g2);
		//ruler를 그린다.
		drawRuler(g2);
	}

	public void drawGenes(Graphics2D g2) {
		for (int i = 0; i < rectangle2d.length; i++) {
			//각 gene을 사각형으로 화면에 그린다.
			g2.setColor((Color)gbColorList.get(i));
			g2.fill(rectangle2d[i]);
		}
	}

	//gene의 위치를 표시할 Ruller.
	public void drawRuler(Graphics2D g2) {
		//Ruler의 최대값을 알아낸다.
		int lastvalue = gbInfo.getSeqLen();

		//Line의 굵기를 정의
		BasicStroke lStroke1 = new BasicStroke(1);
		BasicStroke lStroke2 = new BasicStroke(2);
		BasicStroke lStroke3 = new BasicStroke(3);

		Line2D line1 = new Line2D.Double();

		//Line의 Color와 글자체를 정의.
		g2.setColor(Color.black);
		g2.setFont(new Font("Times New Roman", 0, 10));
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//line클래스의 setLine 메소드를 이용하여 그려질 Line을 정의한다.
		g2.setStroke(lStroke2);
		line1.setLine(0, 100, lastvalue/zoomSize, 100);
		g2.draw(line1);

		// 시작과 끝 표시
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

		//각 5번 째는 중간선, 나머지는 작은선으로 표시
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
