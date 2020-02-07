/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import ca.qc.mtl.util.Crypto;
import encrypteurFichier.Fichier.Statut;
import java.io.File;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.SecretKey;
import javax.swing.table.AbstractTableModel;

public class EncrypteurModele extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = -9092038052478783235L;

    private final String[] entetes = {"Chemin d'accès du fichier", "Status"};
    private ArrayList<Fichier> list;
    private String[] indentificateurs;
    private EncrypteurControleur controleur;

    private boolean actionEnCours = false;
    private String action = "";
    private SecretKey key;

    private ArrayList<Integer> listIndex = new ArrayList<>();
    
    private ProgressBar bar;

    public EncrypteurModele(String[] indentificateurs, EncrypteurControleur controleur, byte[] salt) {
        list = new ArrayList<>();
        this.indentificateurs = indentificateurs;
        this.controleur = controleur;
        try {
            key = Crypto.generateAESKeyFromPassword((indentificateurs[1] + indentificateurs[0] + indentificateurs[2]).toCharArray(),
                    salt, 8000, Crypto.AES_KEY_SIZE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                return Object.class;
        }
    }
    
    @Override
    public int getColumnCount() {
        return entetes.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return list.get(rowIndex).getFilePath();
            case 1:
                return list.get(rowIndex).getStatut();
            default:
                return null; //Ne devrait jamais arriver
        }
    }

    public void addFichier(ArrayList<Fichier> ajouter) {
        list.addAll(ajouter);
        Collections.sort(list);
        actionTerminer();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    public void viderListe() {
        if (!actionEnCours) {
            actionEnCours = true;
            action = "Vider liste";
            list.clear();
            actionTerminer();
        }
    }

    public String quitter() {
        if (!actionEnCours) {
            bar.dispose();
            controleur.quit();
        }
        return action;
    }

    public String addFichier(File[] files) {
        if (!actionEnCours) {
            actionEnCours = true;
            bar = new ProgressBar(0, 0, "fichiers", "trouvés", false);
            CalculAvecProgressBar.trouverFichierSelectionner(files, this, bar);
            action = "Ajout de fichiers";
        }
        return action;
    }

    public String encrypterFichier() {
        if (!actionEnCours) {
            actionEnCours = true;
            controleur.filtrer();
            bar = new ProgressBar(0, trouverNbFichierStatus(Statut.NORMAL), "fichiers", "encryptés", true);
            CalculAvecProgressBar.encrypterFichier(this, bar);
            action = "Encryption";
        }
        return action;
    }

    public String decryptFichier() {
        if (!actionEnCours) {
            actionEnCours = true;
            controleur.filtrer();
            bar = new ProgressBar(0, trouverNbFichierStatus(Statut.ENCRYPTED), "fichiers", "décryptés", true);
            CalculAvecProgressBar.decrypterFichier(this, bar);
            action = "Décryption";
        }
        return action;
    }

    public ArrayList<Fichier> getList() {
        return list;
    }

    public SecretKey getKey() {
        return key;
    }

    public Statut getFichierStatus(int colonne) {
        return list.get(colonne).getStatut();
    }

    public void actionTerminer() {
        fireTableDataChanged();
        controleur.filtrer();
        actionEnCours = false;
        action = "";
    }

    public int isDejaPresent(String pathToFind) {
        int bas = 0;
        int haut = list.size() - 1;
        int centre;

        while (bas <= haut) {
            centre = (bas + haut) / 2;
            if (pathToFind.compareTo(list.get(centre).getFilePath()) < 0) {
                haut = centre - 1;
            } else if (pathToFind.compareTo(list.get(centre).getFilePath()) > 0) {
                bas = centre + 1;
            } else {
                return centre;
            }

        }
        return -1;
    }

    public ArrayList<Integer> getListIndex() {
        return listIndex;
    }

    public int trouverNbFichierStatus(Statut status) {
        int nb = 0;
        for (Integer index : listIndex) {
            if (list.get(index).getStatut() == status) {
                nb++;
            }
        }
        return nb;
    }

    public boolean isActionEnCours() {
        return actionEnCours;
    }

    public String getAction() {
        return action;
    }

    public void setActionEnCours(boolean actionEnCours) {
        this.actionEnCours = actionEnCours;
    }
}
