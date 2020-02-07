/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import ca.qc.mtl.application.controleur.ControleurApp;
import ca.qc.mtl.login.modele.ModeleLogin;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author raphael
 */
public class ChangerInformation extends JFrame {

    private ModeleLogin modeleLogin;
    private ControleurApp contApp;
    private JPanel pnlRegister = new JPanel();

    private JTextField txtNom = new JTextField();
    private JPasswordField txtPass = new JPasswordField();
    private JPasswordField txtConf = new JPasswordField();
    private JPasswordField txtNip = new JPasswordField();

    private JButton btnChang = new JButton("Update informations");
    private JButton btnAnnuler = new JButton("Annuler");

    private boolean recover;

    public ChangerInformation(ModeleLogin modeleLogin, boolean recover, ControleurApp contApp) {
        this.modeleLogin = modeleLogin;
        this.recover = recover;
        this.contApp = contApp;
        setSize(600, 250);
        setTitle("Réinitialiser les informations de connections");
        creerInterface();
        creerEvents();
        setLocationRelativeTo(null);
        if (recover) {
            btnAnnuler.setText("Quitter");
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }

        });
        setVisible(true);
    }

    private void creerInterface() {
        pnlRegister.setLayout(new GridLayout(4, 0));
        JPanel pnl = new JPanel(new GridLayout(1, 2));
        JPanel pnl2 = new JPanel(new GridLayout(1, 2));
        JPanel pnl3 = new JPanel(new GridLayout(1, 2));
        JPanel pnl4 = new JPanel(new GridLayout(1, 2));

        JLabel lbl = new JLabel("Nom: ");
        JLabel lbl2 = new JLabel("Mot de passe: ");
        JLabel lblComfirmer = new JLabel("Confirmation du mot de passe: ");
        JLabel lblNip = new JLabel("NIP (Optionel, renforce sécurité): ");

        pnl.add(lbl);
        pnl.add(txtNom);
        pnlRegister.add(pnl);

        pnl2.add(lbl2);
        pnl2.add(txtPass);
        pnlRegister.add(pnl2);

        pnl3.add(lblComfirmer);
        pnl3.add(txtConf);
        pnlRegister.add(pnl3);

        pnl4.add(lblNip);
        pnl4.add(txtNip);
        pnlRegister.add(pnl4);

        txtNom.setPreferredSize(new Dimension(200, 25));
        txtPass.setPreferredSize(new Dimension(200, 25));
        txtConf.setPreferredSize(new Dimension(200, 25));
        txtNip.setPreferredSize(new Dimension(200, 25));

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblComfirmer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        JPanel pnlTemp = new JPanel();
        pnlTemp.add(btnChang);
        pnlTemp.add(btnAnnuler);
        add(pnlTemp, BorderLayout.SOUTH);
        add(pnlRegister, BorderLayout.CENTER);

    }

    private void creerEvents() {
        btnChang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean valide = modeleLogin.creationComptePossible(txtNom.getText(), modeleLogin.charTabToString(txtPass.getPassword()), modeleLogin.charTabToString(txtConf.getPassword()), modeleLogin.charTabToString(txtNip.getPassword()));
                if (valide) {
                    if (!recover) {
                        contApp.arreterEdit();
                    }
                    modeleLogin.updateInformation(txtNom.getText(), modeleLogin.charTabToString(txtPass.getPassword()), modeleLogin.charTabToString(txtNip.getPassword()), recover);
                    if (recover) {
                        contApp.recoverSuccess();
                    }
                    contApp.changerTextLbl(txtNom.getText());
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, modeleLogin.getErreur());
                }
            }
        });
        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }

    private void exit() {
        if (recover) {
            System.exit(0);
        } else {
            dispose();
        }
    }

}
