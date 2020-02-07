/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBar extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3330764529530474678L;
	
	private JProgressBar bar = new JProgressBar();
	private JLabel lbl = new JLabel();
	private JButton btn = new JButton("Cancel");
	private String element;
	private String action;
	
	private boolean actionCancel = false;
	boolean nombreConnu;
	private int progress;
	
	public ProgressBar(int minValue, int maxValue, String element, String action, boolean nombreConnu) {
		this.element = element;
		this.action = action;
		this.nombreConnu = nombreConnu;
		progress = minValue;
		setTitle("Progrès");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(400, 125);
        setLocationRelativeTo(null);
        bar.setMinimum(minValue);
        bar.setMaximum(maxValue);
        if(nombreConnu) {
        	lbl.setText(bar.getMinimum() + " " + element + " " + action + " sur " + bar.getMaximum());
            bar.setIndeterminate(false);
        }else {
            lbl.setText(bar.getMinimum() + " " + element + " " + action);
            bar.setIndeterminate(true);
        }
        creerInterface();
        creerEvents();
        setResizable(false);
		setVisible(true);
	}
	
	private void creerInterface() {
		JPanel pnlMilieu = new JPanel(new GridLayout(2,1));
		JPanel pnlBtn = new JPanel();
		JPanel pnlBar = new JPanel();
		JPanel pnlText = new JPanel();



		pnlBar.add(bar);
		pnlText.add(lbl);
		pnlMilieu.add(pnlBar);
		pnlMilieu.add(pnlText);
		
		pnlBtn.add(btn);
		
		add(pnlMilieu, BorderLayout.CENTER);
		add(pnlBtn, BorderLayout.SOUTH);
	}
	private void creerEvents() {
		btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				actionCancel = true;
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actionCancel = true;	
			}
		});
	}
	
	public void actionRealisée() {
        if(nombreConnu) {
        	bar.setValue(bar.getValue()+1);
        	lbl.setText(bar.getValue() + " " + element + " " + action + " sur " + bar.getMaximum());
        }else {
            lbl.setText(progress + " " + element + " " + action);
            progress++;
        }
	}
	public void tacheCompleter() {
		dispose();
	}
	public boolean isActionCancel() {
		if(actionCancel == true) {
			dispose();
		}
		return actionCancel;
	}
}
