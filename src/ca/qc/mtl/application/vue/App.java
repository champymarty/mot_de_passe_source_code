/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import ca.qc.mtl.application.controleur.ControleurApp;
import ca.qc.mtl.application.modele.Modele;
import ca.qc.mtl.application.modele.Stenography;
import ca.qc.mtl.main.Compte;
import ca.qc.mtl.util.Crypto;
import encrypteurFichier.EncrypteurControleur;
import encrypteurFichier.EncrypteurModele;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Raphael
 */
public class App extends JFrame {

    private Modele mod;
    private ControleurApp controleurApp;
    private JTable tableau;
    private JScrollPane scroll;

    private JLabel lblService = new JLabel("Service: ");
    private JLabel lblNom = new JLabel("Nom: ");
    private JLabel lblPass = new JLabel("Mot de passe: ");
    private JLabel lblAutre = new JLabel("Autre: ");
    private JLabel lblNomFiltre = new JLabel("Filtre nom service");

    private JTextField txtService = new JTextField();
    private JTextField txtNom = new JTextField();
    private JTextField txtPass = new JTextField();
    private JTextField txtAutre = new JTextField();
    private JTextField txtFIltre = new JTextField();

    private JButton btnAdd = new JButton("Ajouter");
    private JButton btnSupprimer = new JButton("Supprimer");

    private JMenuBar mnuBar = new JMenuBar();

    private JMenu mnuVisibilite = new JMenu("Visibilité");
    private JMenu mnuInfo = new JMenu("Information de connection");
    private JMenu mnuOutil = new JMenu("Outils");
    private JMenu mnuSecurite = new JMenu("Sécurité");
    private JMenu mnuLogin = new JMenu("Quitter");

    private JMenuItem mnuToutVisible = new JMenuItem("Tous visible");
    private JMenuItem mnuToutNonVisible = new JMenuItem("Tous cachés");
    private JMenuItem mnuChangerInfo = new JMenuItem("Changer les informations de connections");
    private JMenuItem mnuKeysRecup = new JMenuItem("Générer une clé de récupération (en cas d'oublie de mot de passe)");
    private JMenuItem mnuFile = new JMenuItem("Encrypter/Décrypter des fichiers");
    private JMenuItem mnuImage = new JMenuItem("Steganography (cacher des messages dans des images");
    private JMenuItem mnuTimerMod = new JMenuItem("Modifier le temps d'inactivité fermant l'application");
    private JMenuItem mnuConnection = new JMenuItem("Modifier le nombre d'échec de connection avant la supression des données");
    private JMenuItem mnuDeco = new JMenuItem("Se déconnecter");
    private JMenuItem mnuQuit = new JMenuItem("Quitter");

    private JLabel lblNomHaut;
    private EncrypteurControleur encrypteurControleur;
    private Stenography stenography;

    private TableRowSorter<Modele> sorter;

    private Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mod.uneSecondePasser();
        }
    });

    public App(ControleurApp controleurApp) {
        this.controleurApp = controleurApp;
        this.mod = this.controleurApp.getModele();
        lblNomHaut = new JLabel("Bienvenue " + mod.getNom());
        lblNomHaut.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        parametreFenetre();
        fermetureFenetre();
        creerTableau();
        creerEvents();
        creerInterfaceText();
        creerMenu();
        mouseMouvements();
        creerSorter();
        setVisible(true);
    }

    private void filtrer() {
        RowFilter<Modele, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter("(?i)" + "(?u)" + txtFIltre.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    private void creerTableau() {
        tableau = new JTable(mod);
        scroll = new JScrollPane(tableau);
        add(scroll, BorderLayout.CENTER);
        tableau.setFillsViewportHeight(true);
        tableau.setDefaultRenderer(String.class, new RenderCase(this.mod, tableau));
        tableau.setDefaultEditor(String.class, new CellEditor(this.mod, tableau));
        tableau.getColumnModel().getColumn(4).setPreferredWidth(tableau.getColumnModel().getTotalColumnWidth() / 30);
    }

    private void creerSorter() {
        sorter = new TableRowSorter<Modele>(mod);
        tableau.setRowSorter(sorter);
    }

    private void fermetureFenetre() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mod.quitter();
            }
        });
    }

    private void parametreFenetre() {
        setTitle("Gestionnaire de mots de passes");
        setSize(1200, 800);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        timer.start();
    }

    private void creerInterfaceText() {
        JPanel pnlBas = new JPanel(new GridLayout(2, 1));
        JPanel pnl = new JPanel();
        JPanel pnlBtn = new JPanel();
        JPanel pnlHaut = new JPanel(new GridLayout(2, 1));
        JPanel pnlFiltre = new JPanel();

        pnl.add(lblService);
        pnl.add(txtService);
        pnl.add(lblNom);
        pnl.add(txtNom);
        pnl.add(lblPass);
        pnl.add(txtPass);
        pnl.add(lblAutre);
        pnl.add(txtAutre);
        txtService.setPreferredSize(new Dimension(150, 25));
        txtNom.setPreferredSize(new Dimension(150, 25));
        txtPass.setPreferredSize(new Dimension(150, 25));
        txtAutre.setPreferredSize(new Dimension(150, 25));
        pnlBtn.add(btnAdd);
        pnlBtn.add(btnSupprimer);
        pnlBas.add(pnl);
        pnlBas.add(pnlBtn);
        add(pnlBas, BorderLayout.SOUTH);
        txtService.setBackground(Color.WHITE);
        txtNom.setBackground(Color.WHITE);
        txtPass.setBackground(Color.WHITE);
        txtAutre.setBackground(Color.WHITE);

        txtFIltre.setPreferredSize(new Dimension(300, 25));
        pnlFiltre.add(lblNomFiltre);
        pnlFiltre.add(txtFIltre);
        pnlHaut.add(pnlFiltre);
        pnlHaut.add(lblNomHaut);
        add(pnlHaut, BorderLayout.NORTH);

    }

    private void creerMenu() {
        mnuBar.add(mnuVisibilite);
        mnuBar.add(mnuInfo);
        mnuBar.add(mnuSecurite);
        mnuBar.add(mnuOutil);
        mnuBar.add(Box.createHorizontalGlue());
        mnuBar.add(mnuLogin);

        mnuOutil.add(mnuFile);
        mnuOutil.addSeparator();
        mnuOutil.add(mnuImage);
        mnuVisibilite.add(mnuToutVisible);
        mnuVisibilite.addSeparator();
        mnuVisibilite.add(mnuToutNonVisible);
        mnuInfo.add(mnuChangerInfo);
        mnuInfo.addSeparator();
        mnuInfo.add(mnuKeysRecup);
        mnuSecurite.add(mnuTimerMod);
        mnuSecurite.addSeparator();
        mnuSecurite.add(mnuConnection);
        mnuLogin.add(mnuDeco);
        mnuLogin.addSeparator();
        mnuLogin.add(mnuQuit);
        this.setJMenuBar(mnuBar);
    }

    private void creerEvents() {
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enlerverService();
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nouveauService();

            }
        });
        mnuToutVisible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mod.toutVisible();
            }
        });
        mnuToutNonVisible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mod.toutInvisible();
            }
        });
        mnuChangerInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangerInformation change = new ChangerInformation(controleurApp.getControleurLog().getMod(), false, controleurApp);
            }
        });
        mnuKeysRecup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser j = new JFileChooser(".");
                j.setDialogTitle("Choississez l'endroit ou vous voulez créer votre clé de récupération");
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int choix = j.showSaveDialog(null);
                if (choix == JFileChooser.APPROVE_OPTION) {
                    File fichierKeys = new File(j.getSelectedFile().getAbsolutePath(), "Cle_de_récupération_de_" + mod.getNom() + ".txt");
                    mod.enregistrerCleRecuperation(fichierKeys);
                    JOptionPane.showMessageDialog(null, "Emplacement:  " + fichierKeys.getAbsolutePath() + "\n" + "Nom:  " + fichierKeys.getName() + "\n" + "Note:  Après un changements des données de connections, la clé ne sera plus valide");
                } else {
                    System.out.println("cancel");
                }
            }
        });
        mnuFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encrypteurControleur = new EncrypteurControleur(mod.getConnection(), mod.getSalt());
            }
        });
        mnuImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stenography = new Stenography(mod);
            }
        });
        mnuTimerMod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TimerSetup timerSetup = new TimerSetup(mod);
            }
        });
        mnuConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectionSetup co = new ConnectionSetup(mod);
            }
        });
        mnuDeco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                mod.deconnecter();
            }
        });
        mnuQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mod.quitter();
            }
        });
        txtService.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nouveauService();
            }
        });
        txtNom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nouveauService();
            }
        });
        txtPass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nouveauService();
            }
        });
        txtAutre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nouveauService();
            }
        });
        txtFIltre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void enlerverService() {
        if (tableau.isEditing()) {
            tableau.getCellEditor().cancelCellEditing();
        }
        String textTemp = txtFIltre.getText();
        txtFIltre.setText("");
        int[] selection = tableau.getSelectedRows();

        for (int i = selection.length - 1; i >= 0; i--) {
            mod.removeCompte(selection[i]);
        }
        txtFIltre.setText(textTemp);
    }

    private void nouveauService() {
        if (!txtService.getText().equalsIgnoreCase("") && !txtNom.getText().equalsIgnoreCase("") && !txtPass.getText().equalsIgnoreCase("")) {
            if (tableau.isEditing()) {
                tableau.getCellEditor().cancelCellEditing();
            }
            mod.addCompte(new Compte(txtService.getText(), txtNom.getText(), txtPass.getText(), txtAutre.getText(), Crypto.genererIv(), Crypto.genererIv(), Crypto.genererIv(), Crypto.genererIv(), Crypto.generateSalt(Crypto.SALT_BYTE_AES), mod.generateRandomCacherHash(), mod.generateRandomCacherHash()));
            txtService.setText("");
            txtNom.setText("");
            txtPass.setText("");
            txtAutre.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Tous les champs doivent être remplis (Le champs 'Autre' est facultatif");
        }
    }

    private void mouseMouvements() {
        tableau.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        mnuBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        scroll.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });

    }

    public void changerNomLbl(String nom) {
        lblNomHaut.setText(nom);
    }

    public void arreterEditing() {
        if (tableau.isEditing()) {
            tableau.getCellEditor().cancelCellEditing();
        }
    }

    public void afficherService() {
        System.out.println("");
        System.out.println("APP:::");
        for (int i = 0; i < tableau.getRowCount(); i++) {
            System.out.println(mod.decryptString(mod.getService(tableau.convertRowIndexToModel(i)), Modele.TypeString.SERVICE, tableau.convertRowIndexToModel(i)));
        }
    }

    public int trouverLigneFenetreParRapportModele(int row) {
        return tableau.convertRowIndexToView(row);
    }
}
