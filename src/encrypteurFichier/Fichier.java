/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import ca.qc.mtl.util.Crypto;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;


/**
 *
 * @author rapha
 */
public class Fichier implements Serializable, Comparable<Fichier>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4364848701066299794L;
	public enum Statut {
        ENCRYPTED,
        NORMAL,
        ERREUR;
    }

    private Statut statut;
    private String info;
    private File file;
    private byte[] iv;

    public Fichier(String path) {
        file = new File(path);
        testerStatutFichier();
    }

    public void testerStatutFichier() {
        try {
            boolean encrypter = Crypto.isImageEncrypter(file);
            if (encrypter) {
                statut = Statut.ENCRYPTED;
                iv = Crypto.getIvParameterSpecFromImage(file).getIV();
            } else {
                statut = Statut.NORMAL;    
            }
        } catch (FileNotFoundException ex) {
            statut = Statut.ERREUR;
            info = "Le fichier n'est pas valide ou n'existe plus (possibilité de changement de nom)";
        } catch (IOException ex) {
            statut = Statut.ERREUR;
            info = ex.getMessage();
        }
    }

    public Statut getStatut() {
        return statut;
    }
    public void setStatut(Statut statut) {
		this.statut = statut;
	}
    public void setInfo(String info) {
		this.info = info;
	}

    public String getInfo() {
        return info;
    }
    
    public void setIv(byte[] iv) {
		this.iv = iv;
	}
    
    public File getFile() {
        return file;
    }
    
    public String getFilePath() {
    	return file.getAbsolutePath();
    }

    public byte[] getIv() {
        return iv;
    }
	@Override
	public int compareTo(Fichier o) {
        return file.getPath().compareToIgnoreCase(o.getFilePath());
	}
}
