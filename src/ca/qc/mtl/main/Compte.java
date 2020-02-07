/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.main;

import java.io.Serializable;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import java.util.Arrays;

/**
 *
 * @author Raphael
 */
public class Compte implements Serializable {

    private String service, nom, motDePasse, autre;
    boolean passVisible = false;

    private byte[] ivService;
    private byte[] ivNom;
    private byte[] ivPass;
    private byte[] ivAutre;

    private boolean changementValeur = false;

    private SecretKey keyService;
    private SecretKey key;
    private byte[] salt;

    private boolean autreVide = false;

    private String cacherHashService, cacherHash;

    public Compte(String service, String nom, String motDePasse, String autre, byte[] ivService, byte[] ivNom, byte[] ivPass, byte[] ivAutre, byte[] salt, String cacherHashService, String cacherHash) {
        this.service = service;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.autre = autre;
        this.ivService = ivService;
        this.ivNom = ivNom;
        this.ivPass = ivPass;
        this.ivAutre = ivAutre;
        this.salt = salt;
        this.cacherHash = cacherHash;
        this.cacherHashService = cacherHashService;
        if (autre.equals("")) {
            System.out.println("Autre generated");
            SecureRandom random = new SecureRandom();
            byte[] array = new byte[100];
            random.nextBytes(array);
            String generatedString = new String(array);
            this.autre = generatedString;
            autreVide = true;
        }
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public void setChangementValeur(boolean changementValeur) {
        this.changementValeur = changementValeur;
    }

    public SecretKey getKey() {
        return key;
    }

    public SecretKey getKeyService() {
        return keyService;
    }

    public void setKeyService(SecretKey keyService) {
        this.keyService = keyService;
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getAutre() {
        if (autreVide) {
            return "";
        }
        return autre;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        if (!service.equals(this.service)) {
            changementValeur = true;
            this.service = service;
        }
    }

    public void setNom(String nom) {
        if (!nom.equals(this.nom)) {
            changementValeur = true;
            this.nom = nom;
        }
    }

    public void setMotDePasse(String motDePasse) {
        if (!motDePasse.equals(this.motDePasse)) {
            changementValeur = true;
            this.motDePasse = motDePasse;
        }
    }

    public void setAutre(String autre) {
        if (!autre.equals(this.autre)) {
            changementValeur = true;
            this.autre = autre;
            if (autreVide) {
                autreVide = false;
            }
        }
    }

    public void changementValeurTraiter() {
        changementValeur = false;
    }

    public boolean isPassVisible() {
        return passVisible;
    }

    public void setAutreVide(boolean autreVide) {
        this.autreVide = autreVide;
    }

    public void genenerStringAutreVide() {
        System.out.println("Autre generated");
        SecureRandom random = new SecureRandom();
        byte[] array = new byte[100];
        random.nextBytes(array);
        String generatedString = new String(array);
        this.autre = generatedString;
        autreVide = true;
    }

    public boolean isAutreVide() {
        return autreVide;
    }

    public boolean isChangementValeur() {
        return changementValeur;
    }

    public void setPassVisible(boolean passVisible) {
        this.passVisible = passVisible;
    }

    public byte[] getIvService() {
        return ivService;
    }

    public byte[] getIvPass() {
        return ivPass;
    }

    public byte[] getIvNom() {
        return ivNom;
    }

    public byte[] getIvAutre() {
        return ivAutre;
    }

    public void setIvService(byte[] ivService) {
        this.ivService = ivService;
    }

    public void setIvPass(byte[] ivPass) {
        this.ivPass = ivPass;
    }

    public void setIvNom(byte[] ivNom) {
        this.ivNom = ivNom;
    }

    public void setIvAutre(byte[] ivAutre) {
        this.ivAutre = ivAutre;
    }

    public void deleteInfoANePasEnregistrer() {
        key = null;
        keyService = null;

    }

    public String getCacherHashService() {
        return cacherHashService;
    }

    public String getCacherHash() {
        return cacherHash;
    }

}
