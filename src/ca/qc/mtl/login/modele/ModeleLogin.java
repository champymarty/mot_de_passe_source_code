/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.login.modele;

import ca.qc.mtl.main.Client;
import ca.qc.mtl.login.controleur.ControleurLogin;
import ca.qc.mtl.main.Compte;
import ca.qc.mtl.util.Crypto;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JOptionPane;

/**
 *
 * @author macbookpro
 */
public class ModeleLogin extends Observable {

    private ArrayList<Client> list;

    private boolean aucunClient = false;
    private boolean creationCompte = false;

    private boolean tentativeLogin = false;
    private boolean tentativeEnregistrement = false;
    private boolean loginValide = false;
    private boolean enregistrementValide = false;

    private String erreur;

    private ControleurLogin controleur;

    private String nom;
    private int pos;

    public ModeleLogin(ControleurLogin controleur) {
        this.controleur = controleur;
        lectureDesDonnes();
        
        if (list == null) {
            aucunClient = true;
            creationCompte = true;
            list = new ArrayList<>();
        } else if (list.isEmpty()) {
            aucunClient = true;
            creationCompte = true;
        }
        verificationDesDonne();
    }

    private void majObservers() {
        setChanged();
        notifyObservers();
    }
    
    private void verificationDesDonne(){
        boolean safe = true;
        for (Client client:list) {
            for (Compte compte:client.getList()) {
                if (compte.getKey() != null || compte.getKeyService() != null) {
                    safe = false;
                }
            }
        }
        if (!safe) {
            JOptionPane.showMessageDialog(null, "WARNING!!! \n Une clé d'encryption a été écrit dans les données. Si le message persiste au lancement de l'application, ceci peut"
                    + "permettre a une personne de décrypter au minimum une case du tableau de mots de passes. \n Si"
                    + "le message n'apparait plus, le program a réglé le problème tout seul, sinon veuillez contacter le créateur pour report le bug à l'adresse couriel suivante:"
                    + "raphaelstjean@yahoo.ca", "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void enregistrerDonner() {
        try {
            FileOutputStream fileOut = new FileOutputStream("clients.bin");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(list);
            out.flush();
            out.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lectureDesDonnes() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("clients.bin"));
            list = (ArrayList) ois.readObject();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
    }

    public boolean isAucunClient() {
        return aucunClient;
    }

    public boolean isCreationCompte() {
        return creationCompte;
    }

    public boolean isLoginValide() {
        return loginValide;
    }

    public void boutonClick(String nom, char[] tab, char[] tabComfirm, char[] tabNip) {
        String pass = charTabToString(tab);
        String passConf = charTabToString(tabComfirm);
        String passNip;
        if (tabNip == null) {
            passNip = "";
        } else {
            passNip = charTabToString(tabNip);
        }
        if (creationCompte) {
            tentativeEnregistrement = true;
            enregistrementValide = creationComptePossible(nom, pass, passConf, passNip);
            if (enregistrementValide) {
                ajouterUnCompte(nom, pass, passNip);
            }

        } else {
            tentativeLogin = true;
            loginValide = false;
            int i = 0;
            int posSuccess = -1;
            for (Client client : list) {
                if (Crypto.validatePassword(nom + pass + passNip + getStringHash(client.getRandomPasswordMelangeur(), pass), client.getHash())) {
                    loginValide = true;
                    posSuccess = i;
                    pos = posSuccess;
                    break;
                }
                i++;
            }
            if (loginValide) {
                for (Client client : list) {
                    client.connectionSucces();
                }
                this.nom = nom;
                controleur.connectionReussi(list, posSuccess, new String[]{nom, pass, passNip}, nom, list.get(posSuccess).getTimer());
            } else {
                boolean dateToBeErase = false;
                for (Client client : list) {
                    if (client.connectionFailed()) {
                        dateToBeErase = true;
                    }
                }
                if (dateToBeErase) {
                    enregistrerDonner();
                }

            }
        }
        majObservers();
    }

    public boolean creationComptePossible(String nom, String pass, String passConf, String nip) {
        boolean valide = false;
        if (nom.equalsIgnoreCase("")) {
            erreur = "Le champ nom de peut pas être vide";
        } else if (pass.equalsIgnoreCase("")) {
            erreur = "Le champ mot de passe de peut pas être vide";
        } else if (nom.length() < 8) {
            erreur = "Le nom doit contenir plus grand que 7 caractères";
        } else if (pass.length() < 8) {
            erreur = "Le mot de passe doit contenir plus grand que 7 caractères";
        } else if (!pass.equals(passConf)) {
            erreur = "Le mot de passe doit être le même que la confirmation de mot de passe";
        } else if (compteExisteDeja(nom + pass + nip)) {
            erreur = "Ce compte existe déjà !";
        } else if (nom.equalsIgnoreCase(pass)) {
            erreur = "Le nom d'utilisateur et le mot de passe ne doivent pas être les mêmes";
        } else if (nip.equals(nom)) {
            erreur = "Le nom ne doit pas être le même que le NIP";
        } else if (nip.equals(pass)) {
            erreur = "Le mot de passe ne doit pas être le même que le NIP";
        } else if (nom.equals("RECOVER_MODE")) {
            erreur = "Vous ne pouvez pas choisir ce nom";
        } else {
            valide = true;
            erreur = null;
        }
        return valide;
    }

    private void ajouterUnCompte(String nom, String pass, String passNip) {
        String randomPasswordMelangeur = Crypto.genererRandomPasswordMelangeur(200);
        String hash = Crypto.generateStrongPasswordHash(nom + pass + passNip + getStringHash(randomPasswordMelangeur, pass));
        byte[] salt = Crypto.generateSalt(Crypto.SALT_BYTE_AES);
        Client client = new Client(hash, salt, Crypto.genererIv(), Crypto.genererIv(), randomPasswordMelangeur);
        list.add(client);
    }

    private String getStringHash(String melangeur, String password) {
        String string = "";
        for (int i = 0; i < melangeur.length(); i++) {
            string = string + password.charAt(Integer.parseInt(String.valueOf(melangeur.charAt(i))));
        }
        return string;
    }

    private boolean compteExisteDeja(String newPass) {
        boolean existe = false;
        for (Client c : list) {
            if (Crypto.validatePassword(newPass, c.getHash())) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public String charTabToString(char[] tab) {
        if (tab == null) {
            return "";
        }
        String string = "";
        for (int i = 0; i < tab.length; i++) {
            string = string + tab[i];
        }
        return string;
    }

    public void setCreationCompte(boolean creationCompte) {
        this.creationCompte = creationCompte;
    }

    public boolean isEnregistrementValide() {
        return enregistrementValide;
    }

    public void setEnregistrementValide(boolean enregistrementValide) {
        this.enregistrementValide = enregistrementValide;
    }

    public boolean isTentativeEnregistrement() {
        return tentativeEnregistrement;
    }

    public boolean isTentativeLogin() {
        return tentativeLogin;
    }

    public void changerInterface() {
        if (creationCompte) {
            creationCompte = false;
        } else {
            creationCompte = true;
        }
        majObservers();
    }

    public void enregistrementTerminer() {
        tentativeEnregistrement = false;
        enregistrementValide = false;
    }

    public void loginTerminer() {
        tentativeLogin = false;
        loginValide = false;
    }

    public String getErreur() {
        return erreur;
    }

    public void updateInformation(String nom, String pass, String nip, boolean recover) {
        String newHash = Crypto.generateStrongPasswordHash(nom + pass + nip + getStringHash(list.get(pos).getRandomPasswordMelangeur(), pass));
        String[] newConnection = {nom, pass, nip};

        controleur.updateInfo(newConnection, newHash, recover, nom);
    }

    public boolean ouvrirFichierCleRecuperation(File file) {
        Object[] resultat = null;
        Object[] keyRecover = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            keyRecover = (Object[]) ois.readObject();
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
        resultat = validerKeyPair(keyRecover);
        if ((int) resultat[0] == -1) {
            return false;
        } else {
            int pos = (int) resultat[0];
            String co = (String) resultat[1];
            String[] temp2 = co.split("°");
            String[] temp3 = temp2[1].split("¬");
            String[] connection = new String[3];
            connection[0] = temp2[0];
            connection[1] = temp3[0];
            if (temp3.length > 1) {
                connection[2] = temp3[1];
            } else {
                connection[2] = "";
            }
            for (int i = 0; i < connection.length; i++) {
                System.out.println(connection[i]);
            }
            tentativeLogin = true;
            loginValide = false;
            int i = 0;
            int posSuccess = -1;
            for (Client client : list) {
                if (Crypto.validatePassword(connection[0] + connection[1] + connection[2] + getStringHash(client.getRandomPasswordMelangeur(), connection[1]), client.getHash())) {
                    loginValide = true;
                    posSuccess = i;
                    break;
                }
                i++;
            }
            if (loginValide) {

                for (Client client : list) {
                    client.connectionSucces();
                }
                this.nom = connection[0];
                controleur.connectionReussi(list, pos, connection, "RECOVER_MODE", list.get((int) resultat[0]).getTimer());
                resultat = null;
                keyRecover = null;
                connection = null;

                temp2 = null;
                temp3 = null;
            } else {
                boolean dateToBeErase = false;
                for (Client client : list) {
                    if (client.connectionFailed()) {
                        dateToBeErase = true;
                    }
                }
                if (dateToBeErase) {
                    enregistrerDonner();
                }

            }
            controleur.connectionReussi(list, pos, connection, "RECOVER_MODE", list.get((int) resultat[0]).getTimer());
            resultat = null;
            if (loginValide) {
                return true;
            } else {
                return false;
            }
        }

    }

    private Object[] validerKeyPair(Object[] keyRecover) {
        int i = 0;
        Object[] retour = new Object[2];
        for (Client client : list) {
            try {
                System.out.println(client.getAuthenfificateur());
                IvParameterSpec ivSpec = Crypto.genererVecteurAESKey((byte[]) keyRecover[1]);
                String connectionString = Crypto.decryptStringWithAES(client.getAuthenfificateur(), (SecretKey) keyRecover[0], ivSpec);
                retour[0] = i;
                retour[1] = connectionString;
                return retour;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalArgumentException ex) {
            }
            i++;
        }
        retour[0] = -1;
        retour[1] = null;
        return retour;
    }

}
