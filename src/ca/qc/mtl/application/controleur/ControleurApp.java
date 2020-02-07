/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.controleur;

import ca.qc.mtl.application.modele.Modele;
import ca.qc.mtl.application.vue.App;
import ca.qc.mtl.application.vue.ChangerInformation;
import ca.qc.mtl.login.controleur.ControleurLogin;
import ca.qc.mtl.main.Client;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Raphael
 */
public class ControleurApp {

    private ControleurLogin controleurLog;
    private Modele modele;
    private App app;
    private String nom;

    public ControleurApp(ControleurLogin controleurLog, ArrayList<Client> list, int pos, String[] connection, String nom, int timer) {
        this.controleurLog = controleurLog;
        this.nom = nom;
        modele = new Modele(this, list, connection, pos, nom, timer);
        if (nom.equals("RECOVER_MODE")) {
            ChangerInformation recover = new ChangerInformation(this.controleurLog.getMod(), true, this);
        } else {
            app = new App(this);
        }

    }

    public void enregistrerDonner() {
        controleurLog.enregistrerDonner();
    }

    public ControleurLogin getControleurLog() {
        return controleurLog;
    }

    public Modele getModele() {
        return modele;
    }

    public void updateInfo(String[] newConnection, String newHash, boolean recoverMode, String newNom) {
        modele.updateInfo(newConnection, newHash, recoverMode, newNom);
    }

    public void recoverSuccess() {
        app = new App(this);
    }

    public void changerTextLbl(String nom) {
        app.changerNomLbl(nom);
    }

    public void deconnecter() {
        modele = null;
        app = null;
        controleurLog.deconnecter();
    }

    public void quitter() {
        app = null;
        modele = null;
        controleurLog.quiiter();

    }

    public void arreterEdit() {
        app.arreterEditing();
    }

    public int trouverLigneFenetreParRapportModele(int row) {
        return app.trouverLigneFenetreParRapportModele(row);
    }
}
