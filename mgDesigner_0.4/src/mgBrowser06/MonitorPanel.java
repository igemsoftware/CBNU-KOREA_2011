package mgBrowser06;

import java.awt.*;
import javax.swing.*;

public class MonitorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JLabel locusLabel = new JLabel();
	JLabel geneLabel = new JLabel();


	public MonitorPanel() {
		locusLabel.setFont(new Font("Dialog", 1, 12));
		geneLabel.setFont(new Font("Dialog", 1, 11));

		this.setBackground(Color.lightGray);
		this.setLayout(new GridLayout(1, 2));
		this.add(locusLabel);
		this.add(geneLabel);
		this.setVisible(true);
	}
}
