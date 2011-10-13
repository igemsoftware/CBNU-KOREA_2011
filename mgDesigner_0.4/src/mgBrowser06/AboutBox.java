package mgBrowser06;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

class AboutBox extends JDialog {
	public AboutBox(Frame owner, String message) {
		super(owner, "About Program", true);
		ClassLoader classloader = getClass().getClassLoader();
		JLabel lbl = new JLabel(new ImageIcon(
			classloader.getResource("icons/dialog_info.png")));
		JPanel p = new JPanel();
		Border b1 = new BevelBorder(BevelBorder.LOWERED);

		Border b2 = new EmptyBorder(5, 5, 5, 5);
		lbl.setBorder(new CompoundBorder(b1, b2));
		p.add(lbl);
		getContentPane().add(p, BorderLayout.WEST);

		JTextArea txt = new JTextArea(message);
		txt.setBorder(new EmptyBorder(5,10,5,10));
		txt.setFont(new Font("Helvetica", Font.BOLD, 12));
		txt.setEditable(false);
		txt.setBackground(getBackground());
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(txt);

		getContentPane().add(p, BorderLayout.CENTER);

		JButton btOK = new JButton("OK");
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};

		btOK.addActionListener(lst);
		p = new JPanel();
		p.add(btOK);
		getContentPane().add(p, BorderLayout.SOUTH);
		pack();
		setResizable(false);
	}
}
