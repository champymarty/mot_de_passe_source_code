/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Raphael
 */
public class Client implements Serializable {

    private String hash;
    private ArrayList<Compte> list = new ArrayList<>();

    private String authenfificateur = null;
    private byte[] ivFile;
    private byte[] ivAuthentification;

    private byte[] salt;
    private int timer, nbCoMax, nbCo;

    private String randomPasswordMelangeur;

    public Client(String hash, byte[] salt, byte[] ivFile, byte[] ivAuthentification, String randomPasswordMelangeur) {
        this.hash = hash;
        this.salt = salt;
        this.ivFile = ivFile;
        this.ivAuthentification = ivAuthentification;
        this.randomPasswordMelangeur = randomPasswordMelangeur;
    }

    public void deleteSafelyVariablesNotBeSave() {
        for (Compte c : list) {
            c.deleteInfoANePasEnregistrer();
        }
    }

    public String getHash() {
        return hash;
    }

    public ArrayList<Compte> getList() {
        return list;
    }

    public void setList(ArrayList<Compte> list) {
        this.list = list;
    }

    public String getRandomPasswordMelangeur() {
        return randomPasswordMelangeur;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setAuthenfificateur(String authenfificateur) {
        this.authenfificateur = authenfificateur;
    }

    public String getAuthenfificateur() {
        return authenfificateur;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getNbCo() {
        return nbCo;
    }

    public int getNbCoMax() {
        return nbCoMax;
    }

    public void connectionSucces() {
        nbCo = 0;
    }

    public boolean connectionFailed() {
        if (nbCoMax != 0) {
            nbCo++;
        }
        if (nbCo >= nbCoMax && nbCoMax != 0) {
            list.clear();
            nbCo = 0;
            return true;
        }
        return false;
    }

    public void setNbCoMax(int nbCoMax) {
        this.nbCoMax = nbCoMax;
        nbCo = 0;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIvFile() {
        return ivFile;
    }

    public void setIvAuthentification(byte[] ivAuthentification) {
        this.ivAuthentification = ivAuthentification;
    }

    public void setIvFile(byte[] ivFile) {
        this.ivFile = ivFile;
    }

    public byte[] getIvAuthentification() {
        return ivAuthentification;
    }

}
