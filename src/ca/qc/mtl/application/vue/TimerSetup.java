/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import ca.qc.mtl.application.modele.Modele;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author raphael
 */
public class TimerSetup extends JFrame {

    private Modele mod;
    private JComboBox cbo = new JComboBox(new String[]{"Aucun timer", "5 minutes", "10 minutes", "15 minutes", "30 minutes", "45 minutes", "60 minutes"});

    private JLabel lbl = new JLabel("Durée d'inactivitée avant la fermeture du programme");
    private JButton btnEn = new JButton("Enregistrer");
    private JButton btnClose = new JButton("Fermer");

    public TimerSetup(Modele mod) {
        this.mod = mod;
        setTitle("Choissir une durée de Timer");
        setSize(500, 125);
        creerInterface();
        creerMouseMouvements();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        setVisible(true);
    }

    private void creerInterface() {
        JPanel pnl = new JPanel();
        pnl.add(lbl);
        pnl.add(cbo);
        JPanel pnl2 = new JPanel();
        pnl2.add(btnEn);
        pnl2.add(btnClose);
        add(pnl, BorderLayout.CENTER);
        add(pnl2, BorderLayout.SOUTH);
        btnEn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mod.changerTimer(getTempsFromPosition(cbo.getSelectedIndex()));
                confirmMessage();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cbo.setSelectedIndex(getIndexFromTemps(mod.getTimerTempInitial()));
    }

    private int getTempsFromPosition(int pos) {
        switch (pos) {
            case 0:
                return 0;
            case 1:
                return 5 * 60;
            case 2:
                return 10 * 60;
            case 3:
                return 15 * 60;
            case 4:
                return 20 * 60;
            case 5:
                return 30 * 60;
            case 6:
                return 45 * 60;
            case 7:
                return 60 * 60;
            default:
                return 0;
        }
    }

    private int getIndexFromTemps(int temps) {
        switch (temps) {
            case 0:
                return 0;
            case 5 * 60:
                return 1;
            case 10 * 60:
                return 2;
            case 15 * 60:
                return 3;
            case 20 * 60:
                return 4;
            case 30 * 60:
                return 5;
            case 45 * 60:
                return 6;
            case 60 * 60:
                return 7;
            default:
                return 0;
        }

    }

    private void creerMouseMouvements() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
    }

    private void confirmMessage() {
        JOptionPane.showMessageDialog(this, "Le temps a été enregistré");
    }

}
