package mgBrowser05;

import java.awt.*;
import javax.swing.*;

public class MonitorPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel locusLabel = new JLabel();
	JLabel geneLabel = new JLabel();
	JLabel geneStartLabel = new JLabel();
	JLabel geneEndLabel = new JLabel();

	//�� label�� �Ӽ��� �����Ѵ�.
	public MonitorPanel() {
		locusLabel.setFont(new Font("Dialog", 1, 12));
		geneLabel.setFont(new Font("Courier New", 1, 12));
		geneStartLabel.setFont(new Font("Courier New", 1, 12));
		geneEndLabel.setFont(new Font("Courier New", 1, 12));

		geneLabel.setText("   Gene Name");
		geneStartLabel.setText("   Gene Start");
		geneEndLabel.setText("   Gene End");

		this.setBackground(Color.gray);
		this.setLayout(new GridLayout(4, 1));

		//panel�� ������ label�� add�Ѵ�.
		this.add(locusLabel);
		this.add(geneLabel);
		this.add(geneStartLabel);
		this.add(geneEndLabel);

		this.setVisible(true);
	}
}
