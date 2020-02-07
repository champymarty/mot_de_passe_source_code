/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

/**
 *
 * @author rapha
 */
public class EncrypteurControleur {

    private EncrypteurModele encrypteurModele;
    private EncrypteurFenetre encrypteurFenetre;
    private String[] indentificateurs;

    public EncrypteurControleur(String[] indentificateurs, byte[] salt) {
        this.indentificateurs = indentificateurs;
        encrypteurModele = new EncrypteurModele(indentificateurs, this, salt);
        encrypteurFenetre = new EncrypteurFenetre(encrypteurModele, this);
    }

    public void filtrer() {
        encrypteurFenetre.filtrer();
        encrypteurFenetre.trouverFichierAfficher();
    }

    public void quit() {
        encrypteurFenetre.setVisible(false);
        encrypteurFenetre.dispose();
        encrypteurModele = null;
    }
}
