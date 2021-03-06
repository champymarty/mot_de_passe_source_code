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
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author raphael
 */
public class ConnectionSetup extends JFrame {

    private Modele mod;
    private JComboBox cbo = new JComboBox(new String[]{"Essais illimité", "2 essais", "3 essais", "5 essais", "7 essais", "10 essais", "15 essais"});

    private JLabel lbl = new JLabel("Nombres de tentatives maximum dans l'interface de connection");
    private JButton btnEn = new JButton("Enregistrer");
    private JButton btnClose = new JButton("Fermer");

    public ConnectionSetup(Modele mod) {
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
                mod.changerTentativeMax(getTentativeMaxFromPosition(cbo.getSelectedIndex()));
                confirmMessage();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cbo.setSelectedIndex(getIndexFromTentativeMax(mod.getTentativeMax()));
    }

    private int getTentativeMaxFromPosition(int pos) {
        switch (pos) {
            case 0:
                return 0;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 5;
            case 4:
                return 7;
            case 5:
                return 10;
            case 6:
                return 15;
            default:
                return 0;
        }
    }

    private int getIndexFromTentativeMax(int tentativeMax) {
        switch (tentativeMax) {
            case 0:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 5:
                return 3;
            case 7:
                return 4;
            case 10:
                return 5;
            case 15:
                return 6;
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
        JOptionPane.showMessageDialog(this, "Le nombre de tentatives maximums a été enregistré");
    }
}
