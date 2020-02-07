/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.modele;

import ca.qc.mtl.application.modele.Modele.TypeString;
import ca.qc.mtl.main.Compte;
import java.util.Comparator;

/**
 *
 * @author Raphael
 */
public class SortService implements Comparator {

    private Modele modele;

    public SortService(Modele modele) {
        this.modele = modele;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Compte fs = (Compte) o1;
        Compte fs2 = (Compte) o2;
        return modele.decryptString(fs.getService(), TypeString.SERVICE, modele.getPositionCompte(fs.getIvService())).compareToIgnoreCase(modele.decryptString(fs2.getService(), TypeString.SERVICE, modele.getPositionCompte(fs2.getIvService())));
    }
}
