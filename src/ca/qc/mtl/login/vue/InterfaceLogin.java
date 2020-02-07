/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.login.vue;

import ca.qc.mtl.login.modele.ModeleLogin;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author macbookpro
 */
public class InterfaceLogin extends JFrame implements Observer {

    private ModeleLogin modele;

    private JButton btnLogin = new JButton("Login");
    private JButton btnQuitter = new JButton("Quitter");

    private JTextField txtNomLog = new JTextField();
    private JPasswordField txtPassLog = new JPasswordField();
    private JPasswordField txtNipLogin = new JPasswordField();
    private JTextField txtNomRegister = new JTextField();
    private JPasswordField txtPassRegister = new JPasswordField();
    private JPasswordField txtPassComfirm = new JPasswordField();
    private JPasswordField txtNipRegister = new JPasswordField();

    private JMenuBar mnuBar = new JMenuBar();

    private JMenu mnuInterface = new JMenu("Interface");
    private JMenu mnuOubli = new JMenu("J'ai oublié mon mot de passe");

    private ButtonGroup group = new ButtonGroup();
    private JRadioButtonMenuItem mnuLogin = new JRadioButtonMenuItem("Interface login");
    private JRadioButtonMenuItem mnuEnregistrer = new JRadioButtonMenuItem("Interface créer un nouveau compte");
    private JMenuItem mnuOubliKey = new JMenuItem("Vous posséder une clé de récupération");
    private JMenuItem mnuOubliSansKey = new JMenuItem("Vous n'avez pas de clé de récupération");

    private JPanel pnlLogin = new JPanel();
    private JPanel pnlRegister = new JPanel();

    private int temps = 10 * 60;
    private Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            temps--;
            if (temps <= 0) {
                modele.enregistrerDonner();
                System.exit(0);
            }
        }
    });

    public InterfaceLogin(ModeleLogin modele) {
        this.modele = modele;
        modele.addObserver(this);
        setTitle("Authentification");
        setSize(400, 200);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        creerMenu();
        creerInterface();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                modele.enregistrerDonner();
                System.exit(0);
            }
        });
        timer.start();
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        changerPanneau();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (modele.isCreationCompte()) {
            btnLogin.setText("Creer un utilisateur");
        } else {
            btnLogin.setText("Login");
        }
        if (modele.isLoginValide() && modele.isTentativeLogin()) {
            modele.loginTerminer();
            timer.stop();
            temps = 10 * 60;
            dispose();

        } else if (!modele.isLoginValide() && modele.isTentativeLogin()) {
            JOptionPane.showMessageDialog(this, "Information invalide", "Status de connection", JOptionPane.ERROR_MESSAGE);
            modele.loginTerminer();
        }
        if (modele.isEnregistrementValide() && modele.isTentativeEnregistrement()) {
            JOptionPane.showMessageDialog(this, "Creation d'un compte avec succès");
            modele.enregistrementTerminer();
            modele.changerInterface();
            mnuLogin.setSelected(true);

        } else if (!modele.isEnregistrementValide() && modele.isTentativeEnregistrement()) {
            JOptionPane.showMessageDialog(this, modele.getErreur());
            modele.enregistrementTerminer();

        }
    }

    private void creerInterface() {
        creerEvents();
        creerInterfaceLogin();
        creerInterfaceRegister();
        creerInterfaceBas();
        if (modele.isAucunClient()) {
            add(pnlRegister, BorderLayout.CENTER);
            setSize(600, 250);
        } else {
            add(pnlLogin, BorderLayout.CENTER);
            setSize(400, 200);
        }
    }

    private void creerInterfaceLogin() {
        pnlLogin.setLayout(new GridLayout(3, 0));
        JPanel pnl = new JPanel(new GridLayout(1, 2));
        JPanel pnl2 = new JPanel(new GridLayout(1, 2));
        JPanel pnl3 = new JPanel(new GridLayout(1, 2));
        JLabel lbl = new JLabel("Nom: ");
        JLabel lbl2 = new JLabel("Mot de passe: ");
        JLabel lblNip = new JLabel("NIP: ");

        pnl.add(lbl);
        pnl.add(txtNomLog);
        pnlLogin.add(pnl);

        pnl2.add(lbl2);
        pnl2.add(txtPassLog);
        pnlLogin.add(pnl2);

        pnl3.add(lblNip);
        pnl3.add(txtNipLogin);
        pnlLogin.add(pnl3);

        txtNomLog.setPreferredSize(new Dimension(200, 25));
        txtPassLog.setPreferredSize(new Dimension(200, 25));
        txtNipLogin.setPreferredSize(new Dimension(200, 25));

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        txtNomLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        txtPassLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        txtNipLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

    }

    private void creerInterfaceRegister() {
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
        pnl.add(txtNomRegister);
        pnlRegister.add(pnl);

        pnl2.add(lbl2);
        pnl2.add(txtPassRegister);
        pnlRegister.add(pnl2);

        pnl3.add(lblComfirmer);
        pnl3.add(txtPassComfirm);
        pnlRegister.add(pnl3);

        pnl4.add(lblNip);
        pnl4.add(txtNipRegister);
        pnlRegister.add(pnl4);

        txtNomRegister.setPreferredSize(new Dimension(200, 25));
        txtPassRegister.setPreferredSize(new Dimension(200, 25));
        txtPassComfirm.setPreferredSize(new Dimension(200, 25));
        txtNipRegister.setPreferredSize(new Dimension(200, 25));

        txtNomRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        txtPassRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        txtPassComfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        txtNipRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblComfirmer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    }

    private void creerInterfaceBas() {
        JPanel pnl2 = new JPanel();
        pnl2.add(btnLogin);
        pnl2.add(btnQuitter);
        add(pnl2, BorderLayout.SOUTH);
        if (modele.isCreationCompte()) {
            btnLogin.setText("Créer un compte");
        } else {
            btnLogin.setText("Login");
        }
    }

    private void creerMenu() {
        group.add(mnuLogin);
        group.add(mnuEnregistrer);
        mnuInterface.add(mnuLogin);
        mnuInterface.addSeparator();
        mnuInterface.add(mnuEnregistrer);
        mnuOubli.add(mnuOubliKey);
        mnuOubli.addSeparator();
        mnuOubli.add(mnuOubliSansKey);

        mnuBar.add(mnuInterface);
        mnuBar.add(mnuOubli);
        mnuLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changerInterface();
            }
        });
        mnuEnregistrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changerInterface();
            }
        });
        mnuOubliKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setDialogTitle("Sélectionner votre clé de récupération");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setApproveButtonText("Ouvrir");
                FiltreSimple filtreTxt = new FiltreSimple("Fichier texte", ".txt");
                chooser.addChoosableFileFilter(filtreTxt);
                chooser.setFileFilter(filtreTxt);
                int choix = chooser.showSaveDialog(null);

                if (choix == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    boolean resultatValide = modele.ouvrirFichierCleRecuperation(file);
                    if (!resultatValide) {
                        JOptionPane.showMessageDialog(null, "Cette clé de récupération n'est pas valide");
                    } else {
                        JOptionPane.showMessageDialog(null, "Clé de récupération valide " + "\n" + " Note: Après les changements d'informations de connection, votre clé de sera plus valide");
                        setVisible(false);
                    }
                } else {
                    System.out.println("cancel");
                }
            }
        });
        mnuOubliSansKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Il est impossible de récupérer un compte sans une clé de récupération");
            }
        });
        setJMenuBar(mnuBar);
        if (modele.isCreationCompte()) {
            mnuEnregistrer.setSelected(true);
        } else {
            mnuLogin.setSelected(true);
        }
    }

    private void changerInterface() {
        modele.changerInterface();
        changerPanneau();
    }

    private void changerPanneau() {
        if (modele.isCreationCompte()) {
            remove(pnlLogin);
            revalidate();
            repaint();
            add(pnlRegister);
            setSize(600, 250);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        } else {
            remove(pnlRegister);
            revalidate();
            repaint();
            add(pnlLogin);
            setSize(400, 200);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        }
    }

    private void creerEvents() {
        btnQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modele.enregistrerDonner();
                System.exit(0);
            }
        });
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (modele.isCreationCompte()) {
            modele.boutonClick(txtNomRegister.getText(), txtPassRegister.getPassword(), txtPassComfirm.getPassword(), txtNipRegister.getPassword());
        } else {
            modele.boutonClick(txtNomLog.getText(), txtPassLog.getPassword(), null, txtNipLogin.getPassword());
        }
    }

}
