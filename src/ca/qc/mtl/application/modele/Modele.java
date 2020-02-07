/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.modele;

import ca.qc.mtl.application.controleur.ControleurApp;
import ca.qc.mtl.main.Client;
import ca.qc.mtl.main.Compte;
import ca.qc.mtl.util.Crypto;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Raphael
 */
public class Modele extends AbstractTableModel {

    private ArrayList<Compte> list;
    private ArrayList<Client> listClient;
    private final String[] entetes = {"Service", "Nom d'utilisateur", "Mot de passe", "Autre", "Mot de passe visible"};

    private String[] connection;
    private int pos;
    private ControleurApp cont;
    private String nom;

    private SortService sortS;
    private int timer, timerTempInitial;
    private boolean timerOn = false;

    public Modele(ControleurApp cont, ArrayList<Client> listClient, String[] connection, int pos, String nom, int timerTempInitial) {
        this.cont = cont;
        this.listClient = listClient;
        this.pos = pos;
        this.nom = nom;
        this.timerTempInitial = timerTempInitial;
        this.connection = connection;
        timer = this.timerTempInitial;
        if (this.timerTempInitial != 0) {
            timerOn = true;
        }
        list = this.listClient.get(pos).getList();
        sortS = new SortService(this);
        genererKeys();
    }

    private void genererKeys() {
        String generatedString = new String(listClient.get(pos).getSalt());
        for (Compte compte : list) {

            try {
                compte.setKey(Crypto.generateAESKeyFromPassword((connection[0] + connection[1] + connection[2] + generatedString + getStringHash(compte.getCacherHash())).toCharArray(), compte.getSalt()));
                compte.setKeyService(Crypto.generateAESKeyFromPassword((generatedString + connection[1] + getStringHash(compte.getCacherHashService()) + connection[0] + connection[2]).toCharArray(), compte.getSalt()));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getRowCount() {
        return list.size();
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
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 4) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return decryptString(list.get(rowIndex).getService(), TypeString.SERVICE, rowIndex);
            case 1:
                return list.get(rowIndex).getNom();
            case 2:
                return list.get(rowIndex).getMotDePasse();
            case 3:
                return list.get(rowIndex).getAutre();
            case 4:
                return list.get(rowIndex).isPassVisible();
            default:
                return null; //Ne devrait jamais arriver
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; //Toutes les cellules éditables
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue != null) {
            Compte compte = list.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    compte.setService((String) aValue);
                    if (compte.isChangementValeur()) {
                        String texte = decryptString(compte.getService(), TypeString.SERVICE, rowIndex);
                        compte.setIvService(Crypto.genererIv());
                        try {
                            compte.setService(Crypto.encryptStringWithAES(texte, compte.getKeyService(), Crypto.genererVecteurAESKey(compte.getIvService())));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                            compte.setService(ex.getMessage());
                        }
                    }
                    compte.changementValeurTraiter();
                    System.out.println("Val modifier: (" + rowIndex + "," + columnIndex + ")");

                    trierList();
                    fireTableDataChanged();
                    break;
                case 1:
                    compte.setNom((String) aValue);
                    if (compte.isChangementValeur()) {
                        String texte = decryptString(compte.getNom(), compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvNom()));
                        compte.setIvNom(Crypto.genererIv());
                        try {
                            compte.setNom(Crypto.encryptStringWithAES(texte, compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvNom())));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                            ex.printStackTrace();
                            compte.setNom("Erreur: " + ex.getMessage());
                        }
                        compte.changementValeurTraiter();
                        System.out.println("Val modifier: (" + rowIndex + "," + columnIndex + ")");
                    }
                    break;
                case 2:
                    compte.setMotDePasse((String) aValue);
                    if (compte.isChangementValeur()) {
                        String texte = decryptString(compte.getMotDePasse(), compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvPass()));
                        compte.setIvPass(Crypto.genererIv());
                        try {
                            compte.setMotDePasse(Crypto.encryptStringWithAES(texte, compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvPass())));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                            ex.printStackTrace();
                            compte.setMotDePasse("Erreur: " + ex.getMessage());
                        }
                        compte.changementValeurTraiter();
                        System.out.println("Val modifier: (" + rowIndex + "," + columnIndex + ")");
                    }
                    break;
                case 3:
                    compte.setAutre((String) aValue);
                    if (compte.isChangementValeur()) {
                        String texte = decryptString(compte.getAutre(), compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvAutre()));
                        if (texte.equals("")) {
                            compte.genenerStringAutreVide();
                        } else {
                            compte.setAutreVide(false);
                        }
                        compte.setIvAutre(Crypto.genererIv());
                        try {
                            compte.setAutre(Crypto.encryptStringWithAES(texte, compte.getKey(), Crypto.genererVecteurAESKey(compte.getIvAutre())));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
                            ex.printStackTrace();
                            compte.setAutre("Erreur: " + ex.getMessage());
                        }
                        compte.changementValeurTraiter();
                        System.out.println("Val modifier: (" + rowIndex + "," + columnIndex + ")");
                    }
                    break;
                case 4:
                    compte.setPassVisible((boolean) aValue);
                    fireTableRowsUpdated(cont.trouverLigneFenetreParRapportModele(pos), cont.trouverLigneFenetreParRapportModele(pos));
            }
        }
    }

    public void addCompte(Compte compte) {
        try {
            list.add(compte);
            String generatedString = new String(listClient.get(pos).getSalt());
            compte.setKey(Crypto.generateAESKeyFromPassword((connection[0] + connection[1] + connection[2] + generatedString + getStringHash(compte.getCacherHash())).toCharArray(), compte.getSalt()));
            compte.setKeyService(Crypto.generateAESKeyFromPassword((generatedString + connection[1] + getStringHash(compte.getCacherHashService()) + connection[0] + connection[2]).toCharArray(), compte.getSalt()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        compte.setService(encryptString(compte.getService(), TypeString.SERVICE, list.size() - 1));
        compte.setNom(encryptString(compte.getNom(), TypeString.NOM, list.size() - 1));
        compte.setMotDePasse(encryptString(compte.getMotDePasse(), TypeString.MOT_DE_PASSE, list.size() - 1));
        compte.setAutre(encryptString(compte.getAutre(), TypeString.AUTRE, list.size() - 1));
        trierList();
        fireTableDataChanged();
    }

    public void removeCompte(int rowIndex) {
        list.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void enregistrerDonner() {
        listClient.get(pos).setList(list);
        cont.enregistrerDonner();
    }

    public boolean isVisible(int pos) {
        return list.get(pos).isPassVisible();
    }

    public void toutVisible() {
        for (Compte c : list) {
            c.setPassVisible(true);
        }
        fireTableDataChanged();
    }

    public void toutInvisible() {
        for (Compte c : list) {
            c.setPassVisible(false);
        }
        fireTableDataChanged();
    }

    public void quitter() {
        toutInvisible();
        for (Client c : listClient) {
            c.deleteSafelyVariablesNotBeSave();
        }
        enregistrerDonner();
        cont.quitter();
    }

    public void deconnecter() {
        toutInvisible();
        for (Client c : listClient) {
            c.deleteSafelyVariablesNotBeSave();
        }
        Arrays.fill(nom.getBytes(), (byte) 0);
        for (int i = 0; i < connection.length; i++) {
            Arrays.fill(connection[i].getBytes(), (byte) 0);
        }
        enregistrerDonner();
        cont.deconnecter();
    }

    public void updateInfo(String[] newConnection, String newHash, boolean recoverMode, String newNom) {
        int i = 0;
        for (Compte compte : list) {
            compte.setService(decryptString(compte.getService(), TypeString.SERVICE, i));
            compte.setNom(decryptString(compte.getNom(), TypeString.NOM, i));
            compte.setMotDePasse(decryptString(compte.getMotDePasse(), TypeString.MOT_DE_PASSE, i));
            compte.setAutre(decryptString(compte.getAutre(), TypeString.AUTRE, i));
            i++;
        }
        listClient.get(pos).setHash(newHash);
        connection = newConnection;
        i = 0;
        for (Compte compte : list) {
            try {
                String generatedString = new String(listClient.get(pos).getSalt());
                compte.setKey(Crypto.generateAESKeyFromPassword((connection[0] + connection[1] + connection[2] + generatedString + getStringHash(compte.getCacherHash())).toCharArray(), compte.getSalt()));
                compte.setKeyService(Crypto.generateAESKeyFromPassword((generatedString + connection[1] + getStringHash(compte.getCacherHashService()) + connection[0] + connection[2]).toCharArray(), compte.getSalt()));

                compte.setService(encryptString(compte.getService(), TypeString.SERVICE, i));
                compte.setNom(encryptString(compte.getNom(), TypeString.NOM, i));
                compte.setMotDePasse(encryptString(compte.getMotDePasse(), TypeString.MOT_DE_PASSE, i));
                compte.setAutre(encryptString(compte.getAutre(), TypeString.AUTRE, i));

            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                ex.printStackTrace();
            }
            i++;
        }
        nom = newNom;
        if (!recoverMode) {
            fireTableDataChanged();
        }
    }

    public void enregistrerCleRecuperation(File file) {
        Object[] keys;
        IvParameterSpec ivSpec;
        String toSave = connection[0] + "°" + connection[1] + "¬" + connection[2];
        try {
            if (file.exists()) {
                file.delete();
            }

            System.out.println(toSave);
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            keys = Crypto.generateAESKey();
            ivSpec = (IvParameterSpec) keys[1];
            toSave = Crypto.encryptStringWithAES(toSave, (SecretKey) keys[0], ivSpec);
            listClient.get(pos).setAuthenfificateur(toSave);

            keys[1] = ivSpec.getIV();
            out.writeObject(keys);
            out.flush();
            out.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        file = null;
        keys = null;
        ivSpec = null;
        toSave = null;
    }

    public String getNom() {
        return nom;
    }

    public void recoverSuccess() {
        cont.recoverSuccess();
    }

    public String decryptString(String stringToDecrypt, TypeString type, int posCompte) {
        IvParameterSpec ivSpec = null;
        if (null != type) {
            switch (type) {
                case SERVICE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvService());
                    return decryptString(stringToDecrypt, list.get(posCompte).getKeyService(), ivSpec);
                case NOM:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvNom());
                    return decryptString(stringToDecrypt, list.get(posCompte).getKey(), ivSpec);
                case MOT_DE_PASSE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvPass());
                    return decryptString(stringToDecrypt, list.get(posCompte).getKey(), ivSpec);
                case AUTRE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvAutre());
                    return decryptString(stringToDecrypt, list.get(posCompte).getKey(), ivSpec);
                case AUTHENTIFICATEUR:
                    ivSpec = Crypto.genererVecteurAESKey(listClient.get(pos).getIvAuthentification());
                    return decryptString(stringToDecrypt, list.get(posCompte).getKey(), ivSpec);
                default:
                    return "";
            }
        }
        return "";
    }

    private String decryptString(String stringToDecrypt, SecretKey key, IvParameterSpec ivSpec) {
        try {
            return Crypto.decryptStringWithAES(stringToDecrypt, key, ivSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return "Decryption error: " + ex.getMessage();
        }
    }

    public int getPositionCompte(byte[] iv) {
        int pos = 0;
        boolean present = true;
        for (Compte compte : list) {
            byte[] ivTest = compte.getIvService();
            present = true;
            for (int i = 0; i < iv.length; i++) {
                if (ivTest[i] != iv[i]) {
                    present = false;
                }
            }
            if (present) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public String encryptString(String stringToEncrypt, TypeString type, int posCompte) {
        IvParameterSpec ivSpec = null;
        SecretKey key = null;
        if (type == TypeString.SERVICE) {
            key = list.get(posCompte).getKeyService();
        } else {
            key = list.get(posCompte).getKey();
        }
        if (null != type) {
            switch (type) {
                case SERVICE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvService());
                    break;
                case NOM:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvNom());
                    break;
                case MOT_DE_PASSE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvPass());
                    break;
                case AUTRE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvAutre());
                    break;
                case AUTHENTIFICATEUR:
                    ivSpec = Crypto.genererVecteurAESKey(listClient.get(pos).getIvAuthentification());
                    break;
                default:
                    break;
            }
        }

        try {
            return Crypto.encryptStringWithAES(stringToEncrypt, key, ivSpec);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return "Encyption error: " + ex.getMessage();
        }

    }

    public enum TypeString {
        SERVICE,
        NOM,
        MOT_DE_PASSE,
        AUTRE,
        AUTHENTIFICATEUR;
    }

    public Object[] getAESKey(TypeString type, int posCompte) {
        IvParameterSpec ivSpec = null;
        SecretKey key = null;
        if (type == TypeString.SERVICE) {
            key = list.get(posCompte).getKeyService();
        }else {
            key = list.get(posCompte).getKey();
        }
        if (null != type) {
            switch (type) {
                case SERVICE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvService());
                    break;
                case NOM:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvNom());
                    break;
                case MOT_DE_PASSE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvPass());
                    break;
                case AUTRE:
                    ivSpec = Crypto.genererVecteurAESKey(list.get(posCompte).getIvAutre());
                    break;
                case AUTHENTIFICATEUR:
                    ivSpec = Crypto.genererVecteurAESKey(listClient.get(pos).getIvAuthentification());
                    break;
                default:
                    break;
            }
        }
        Object[] tab = {key, ivSpec};
        return tab;
    }

    private void trierList() {
        list.sort(sortS);
    }

    public int getTimer() {
        return timer;
    }

    public int getTimerTempInitial() {
        return timerTempInitial;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void uneSecondePasser() {
        timer--;
        if (timer <= 0 && isTimerOn()) {
            quitter();
        }
        if (timer <= -10) {
            timer = -1;
        }
    }

    public void resetTimer() {
        timer = timerTempInitial;
    }

    public void changerTimer(int temps) {
        if (timerTempInitial == 0 && temps != 0) {
            timerOn = true;
        }
        if (temps == 0) {
            timerOn = false;
        }
        timerTempInitial = temps;
        timer = timerTempInitial;

        listClient.get(pos).setTimer(timerTempInitial);
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public void changerTentativeMax(int tentativeMax) {
        listClient.get(pos).setNbCoMax(tentativeMax);
    }

    public int getTentativeMax() {
        return listClient.get(pos).getNbCoMax();
    }

    public int getTentative() {
        return listClient.get(pos).getNbCo();
    }

    public byte[] getSalt() {
        return listClient.get(pos).getSalt();
    }

    public int getPos() {
        return pos;
    }

    public String generateRandomCacherHash() {
        String string = "";
        int stringLenght = 100;
        SecureRandom random = new SecureRandom();
        int nb;

        for (int i = 0; i < stringLenght; i++) {
            nb = random.nextInt(9);
            string = string + Integer.toString(nb);
        }
        return string;
    }

    private String getStringHash(String hid) {
        String string = "";
        for (int i = 0; i < hid.length(); i++) {
            string = string + connection[1].charAt(Integer.parseInt(String.valueOf(hid.charAt(i))));
        }
        return string;
    }

    public String[] getConnection() {
        return connection;
    }

    public String getService(int pos) {
        return list.get(pos).getService();
    }
}
