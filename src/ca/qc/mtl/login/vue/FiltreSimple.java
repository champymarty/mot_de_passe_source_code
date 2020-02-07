/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.login.vue;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author raphael
 */
public class FiltreSimple extends FileFilter {

    private String description;
    private String extension;

    public FiltreSimple(String description, String extension) {
        if (description == null || extension == null) {
            throw new NullPointerException("La description (ou extension) ne peut être null.");
        }
        this.description = description;
        this.extension = extension;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String nomFichier = f.getName().toLowerCase();

        return nomFichier.endsWith(extension);
    }

    @Override
    public String getDescription() {
        return description;
    }

}
