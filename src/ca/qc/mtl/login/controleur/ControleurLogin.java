/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.login.controleur;

import ca.qc.mtl.application.controleur.ControleurApp;
import ca.qc.mtl.main.Client;
import ca.qc.mtl.login.vue.InterfaceLogin;
import ca.qc.mtl.login.modele.ModeleLogin;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author macbookpro
 */
public class ControleurLogin {

    private ModeleLogin mod;
    private InterfaceLogin app;
    private ControleurApp cont;
    private String nom;

    public ControleurLogin() {
        mod = new ModeleLogin(this);
        app = new InterfaceLogin(mod);
    }

    public void connectionReussi(ArrayList<Client> list, int pos, String[] connection, String nom, int timer) {
        cont = new ControleurApp(this, list, pos, connection, nom, timer);
    }

    public void enregistrerDonner() {
        mod.enregistrerDonner();
    }

    public ModeleLogin getMod() {
        return mod;
    }

    public void updateInfo(String[] newConnection, String newHash, boolean recoverMode, String newNom) {
        cont.updateInfo(newConnection, newHash, recoverMode, newNom);
    }

    public void deconnecter() {
        cont = null;
        app = null;
        mod = null;
        System.gc();
        mod = new ModeleLogin(this);
        app = new InterfaceLogin(mod);
    }

    public void quiiter() {
        cont = null;
        app = null;
        mod = null;
        System.gc();
        System.exit(0);
    }

}
