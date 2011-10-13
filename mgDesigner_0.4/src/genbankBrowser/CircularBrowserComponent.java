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

	//직선, 2차 곡선 및 3차 곡선으로부터 작성된 패스.
	GeneralPath generalpath[];
	double centerX;
	double centerY;
	double radius;

	//CircularBrowserComponent 클래스의 생성자
	public CircularBrowserComponent(GenbankFileInfo gbInfo, MonitorPanel monitor) {
		this.gbInfo = gbInfo;
		this.monitorPanel = monitor;
		init(); //component를 초기화
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

		//mouse가 gene위에 올라갔을때의 action을 정의한다.
		this.addMouseMotionListener(
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

		//원의 중심 좌표 설정
		centerX = frameSize.width / 2;
		centerY = frameSize.height / 2;
		
		//폭과 높이 중 짧은 쪽을 기준으로 반지름 설정
		if(frameSize.width < frameSize.height)
			radius = frameSize.width / 2 - 20;
		else
			radius = frameSize.height / 2 - 20;

		//계산한 좌표와 반지름을 이용하여 타원을 그린다.
		g2.draw(new Ellipse2D.Double(
					frameSize.width / 2 - radius, 
					frameSize.height / 2 - radius,
					2 * radius,
					2 * radius));

		//gene을 그리는 메소드를 호출한다.
		drawGenes(g2);

		//ruler를 그린다.
		drawRuler(g2);
	}

	public void drawGenes(Graphics2D g2) {
		generalpath = new GeneralPath[gbInfo.getCount()];
		double a = 2 * Math.PI / gbInfo.getSeqLen();

		for (int k = 0; k < gbInfo.getCount(); k++) {
			double geneStartPoint = (double)(gbInfo.getStartPos(k));
			double geneEndPoint = (double)(gbInfo.getEndPos(k));

			//gene을 표시할 다각형의 각 좌표를 설정한다.
			double x1 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.9;
			double y1 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.9;
			double x2 = centerX - radius * Math.sin(-a * geneStartPoint) * 0.95;
			double y2 = centerY - radius * Math.cos(-a * geneStartPoint) * 0.95;
			double x3 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.95;
			double y3 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.95;
			double x4 = centerX - radius * Math.sin(-a * geneEndPoint) * 0.9;
			double y4 = centerY - radius * Math.cos(-a * geneEndPoint) * 0.9;

			//좌표대로 다각형을 설정한 후 그 영역 정보를 저장한다.
			GeneralPath gp = new GeneralPath();
			gp.moveTo( (float) x1, (float) y1);
			gp.lineTo( (float) x2, (float) y2);
			gp.lineTo( (float) x3, (float) y3);
			gp.lineTo( (float) x4, (float) y4);
			gp.lineTo( (float) x1, (float) y1);
			generalpath[k] = gp;

			//drawShape 메소드로 그린 다각형에 색을 넣는다.
			g2.setColor((Color)gbColorList.get(k));
			g2.fill(generalpath[k]);
		}
	}

	public void drawRuler(Graphics2D g2) {
		g2.setColor(Color.black);
		// 서열 길이
		int lastvalue = gbInfo.getSeqLen();

		// 눈금 하나의 서열상의 길이 설정
		int decimalPoint = 1;
		String tmp = lastvalue +"";
		for (int i=0; i<tmp.length()-2; i++)
			decimalPoint *= 10;

		// 눈금 개수
		int mat = lastvalue / decimalPoint;

		// 눈금 하나의 지도상의 길이
		double range = 2 * Math.PI / lastvalue * decimalPoint;

		double t;
		NumberFormat nf = NumberFormat.getInstance();

		Line2D largecount = new Line2D.Double();
		Line2D smallcount = new Line2D.Double();

		for (int k = 0; k <= mat; k++) {
			//눈금을 긴 line으로 표시
			t = -range * k;
			largecount.setLine(
					centerX - radius * Math.sin(t),
					centerY - radius * Math.cos(t),
					centerX - radius * Math.sin(t) * 0.96,
					centerY - radius * Math.cos(t) * 0.96);
			g2.draw(largecount);

			//화면에서 구분이 가능하도록 눈금 5개마다 위치 표시
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
			//눈금 하나를 다시 작은 눈금 5개로 나눔
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
		// 마지막 부분의 작은 눈금
		for (double j = 1; j < 5; j++) {
			t = -range * mat + j * -range / 5;

			if(radius * Math.sin(t) < 0) {
				break;	// 서열 범위를 넘어서면 끝낸다.
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
