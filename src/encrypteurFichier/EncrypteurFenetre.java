/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class EncrypteurFenetre extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -8000149439207594886L;

    private JButton btnChooser = new JButton("Sélectionner un/des fichier(s) ou  un dossier");
    private JButton btnEncrypt = new JButton("Encrypter des documents valides");
    private JButton btnDecrypt = new JButton("Décrypter les documents valides");
    private JButton btnClose = new JButton("Fermer la fenêtre");
    private JButton btnVider = new JButton("Vider la liste");

    private JLabel lblNom = new JLabel("Filtre file path");
    private JLabel lblStatus = new JLabel("Filtre du status");
    private JLabel lblNb = new JLabel("     " + 0 + " fichier(s) sélectionné(s)");
    private JLabel lblNomFiltre = new JLabel("Filtre file path");

    private JTextField txt = new JTextField();
    String[] cboModel = {"", "ENCRYPTED", "NORMAL", "ERREUR"};
    private JComboBox cbo = new JComboBox(cboModel);

    private JTable tableau;
    private JScrollPane scroll;

    private EncrypteurModele modele;
    private EncrypteurControleur controleur;
    private TableRowSorter<EncrypteurModele> sorter;

    public EncrypteurFenetre(EncrypteurModele modele, EncrypteurControleur controleur) {
        this.modele = modele;
        this.controleur = controleur;
        setTitle("Encrypteur de fichiers");
        setSize(800, 600);
        this.modele = modele;
        tableau = new JTable(this.modele);
        scroll = new JScrollPane(tableau);
        tableau.setFillsViewportHeight(true);
        add(scroll, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        creerInterfaceBouton();
        creerEvents();
        creerSorter();
        tableau.setDefaultRenderer(String.class, new ColorRenderer(modele, this));
        double largeur = (double) tableau.getColumnModel().getTotalColumnWidth();
        double newLargeur = largeur * 4;
        tableau.getColumnModel().getColumn(0).setPreferredWidth((int) newLargeur);
        setVisible(true);
    }

    private void creerSorter() {
        sorter = new TableRowSorter<EncrypteurModele>(modele);
        tableau.setRowSorter(sorter);
    }

    private void creerInterfaceBouton() {

        JPanel pnlInterface = new JPanel(new GridLayout(4, 1));
        JPanel pnlChooser = new JPanel();
        JPanel pnlAction = new JPanel();
        JPanel pnlClear = new JPanel();
        JPanel pnlClose = new JPanel();
        JPanel pnlFilter = new JPanel();

        pnlChooser.add(btnChooser);
        pnlAction.add(btnEncrypt);
        pnlAction.add(btnDecrypt);
        pnlClear.add(btnVider);
        pnlClose.add(btnClose);

        pnlInterface.add(pnlChooser);
        pnlInterface.add(pnlAction);
        pnlInterface.add(pnlClear);
        pnlInterface.add(pnlClose);

        pnlFilter.add(lblNom);
        pnlFilter.add(txt);
        pnlFilter.add(lblStatus);
        pnlFilter.add(cbo);
        pnlFilter.add(lblNb);
        cbo.setSelectedIndex(0);
        txt.setPreferredSize(new Dimension(300, 25));

        add(pnlInterface, BorderLayout.SOUTH);
        add(pnlFilter, BorderLayout.NORTH);
    }

    private void creerEvents() {
        btnVider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modele.viderListe();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitter();
            }
        });
        btnChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!modele.isActionEnCours()) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Choississez un/des fichier(s) ou un dossier");
                    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    chooser.setMultiSelectionEnabled(true);
                    int choix = chooser.showSaveDialog(null);
                    if (choix == JFileChooser.APPROVE_OPTION) {
                        modele.addFichier(chooser.getSelectedFiles());
                    }
                }
            }
        });

        btnEncrypt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modele.encrypterFichier();
            }
        });
        btnDecrypt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modele.decryptFichier();
            }
        });
        txt.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub

            }
        });
        cbo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filtrer();
            }
        });
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                quitter();

            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void filtrer() {
        java.util.List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);
        filters.add(RowFilter.regexFilter(txt.getText(), 0));
        filters.add(RowFilter.regexFilter(cboModel[cbo.getSelectedIndex()], 1));
        RowFilter<Object, Object> serviceFilter = RowFilter.andFilter(filters);
        sorter.setRowFilter(serviceFilter);
        lblNb.setText("     " + tableau.getRowCount() + " fichier(s) sélectionné(s)");
    }

    public void trouverFichierAfficher() {
        modele.getListIndex().clear();
        for (int row = 0; row < tableau.getRowCount(); row++) {
            modele.getListIndex().add(tableau.convertRowIndexToModel(row));
        }
    }

    public int convertRowIndexToModel(int row) {
        return tableau.convertRowIndexToModel(row);
    }

    private void quitter() {
        String action = modele.quitter();
        if (!action.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "L'action '" + modele.getAction() + "' est en cours d'exécution. Fermer maintenant"
                    + "peut entrainer des corruptions de fichiers. \n Voulez-vous tout de mêmes quitter (vous pouvez voir le progret de l'operation dans"
                    + "la bar à proget) ?",
                    "Warning !", JOptionPane.YES_NO_OPTION);
            if (dialogResult == 0) {
                modele.setActionEnCours(false);
                modele.quitter();
            }
        }

    }

}
