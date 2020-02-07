/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import ca.qc.mtl.util.Crypto;
import encrypteurFichier.Fichier.Statut;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.SwingWorker;

public abstract class CalculAvecProgressBar {

    public static void trouverFichierSelectionner(File[] file, EncrypteurModele modele, ProgressBar bar) {

        SwingWorker sw1 = new SwingWorker() {

            @Override
            protected ArrayList<Fichier> doInBackground() throws Exception {
                ArrayList<File> listDir = new ArrayList();
                ArrayList<Fichier> filesSelectionner = new ArrayList();
                boolean sortir = false;
                int nb = 0;

                boolean actionCancel = false;

                File[] files = file;
                while (!sortir || actionCancel) {
                    if (nb != 0 && listDir.size() > 0) {
                        files = listDir.remove(0).listFiles();
                    }

                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            listDir.add(files[i]);
                        } else {
                            if (modele.isDejaPresent(files[i].getAbsolutePath()) == -1) {
                                filesSelectionner.add(new Fichier(files[i].getAbsolutePath()));
                                bar.actionRealisée();
                            }

                        }

                    }
                    if (listDir.isEmpty()) {
                        sortir = true;
                    }
                    nb++;
                    actionCancel = bar.isActionCancel();
                    if (actionCancel) {
                        
                        break;
                    }

                }
                if (!actionCancel) {
                    bar.tacheCompleter();
                    return filesSelectionner;
                } else {
                    bar.dispose();
                    modele.actionTerminer();
                    return null;
                }

            }

            @Override
            protected void done() {
                // this method is called when the background  
                // thread finishes execution 
                try {
                    ArrayList<Fichier> list = (ArrayList<Fichier>) get();
                    if (list != null) {
                        modele.addFichier(list);
                    }
                    modele.actionTerminer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        // executes the swingworker on worker thread 
        sw1.execute();
    }

    public static void encrypterFichier(EncrypteurModele modele, ProgressBar bar) {

        SwingWorker sw1 = new SwingWorker() {
            @Override
            protected ArrayList<Fichier> doInBackground() throws Exception {
                ArrayList<Fichier> list = modele.getList();
                ArrayList<Integer> listIndex = modele.getListIndex();

                boolean actionCancel = false;
                for (int i = 0; i < listIndex.size(); i++) {
                    if (list.get(listIndex.get(i)).getStatut() == Statut.NORMAL) {
                        list.get(listIndex.get(i)).setIv(Crypto.genererRandomIv());
                        IvParameterSpec ivSpec = Crypto.genererVecteurAESKey(list.get(listIndex.get(i)).getIv());
                        AESKey aESKey = new AESKey(modele.getKey(), ivSpec);
                        File file = new File(list.get(listIndex.get(i)).getFilePath());
                        try {
                            Crypto.encryptImage(aESKey, file, file);
                            list.get(listIndex.get(i)).setStatut(Statut.ENCRYPTED);

                        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                                | IOException e) {
                            list.get(listIndex.get(i)).setStatut(Statut.ERREUR);
                            list.get(listIndex.get(i)).setInfo("Erreur lors de l'encryption: " + e.getMessage());
                        }
                        bar.actionRealisée();
                        actionCancel = bar.isActionCancel();
                        if (actionCancel) {
                            break;
                        }
                    }
                }
                bar.tacheCompleter();
                modele.actionTerminer();
                bar.dispose();
                return null;
            }
        };
        sw1.execute();
    }

    public static void decrypterFichier(EncrypteurModele modele, ProgressBar bar) {

        SwingWorker sw1 = new SwingWorker() {
            @Override
            protected ArrayList<Fichier> doInBackground() throws Exception {
                ArrayList<Fichier> list = modele.getList();
                ArrayList<Integer> listIndex = modele.getListIndex();

                boolean actionCancel = false;
                for (int i = 0; i < listIndex.size(); i++) {
                    if (list.get(listIndex.get(i)).getStatut() == Statut.ENCRYPTED) {
                        IvParameterSpec ivSpec = new IvParameterSpec(list.get(listIndex.get(i)).getIv());
                        AESKey aESKey = new AESKey(modele.getKey(), ivSpec);
                        File file = new File(list.get(listIndex.get(i)).getFilePath());
                        try {
                            Crypto.decryptImage(aESKey, file, file);
                            list.get(listIndex.get(i)).setStatut(Statut.NORMAL);
                        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                                | IOException e) {
                            list.get(listIndex.get(i)).setStatut(Statut.ERREUR);
                            list.get(listIndex.get(i)).setInfo("Erreur lors de la décryption: " + e.getMessage());
                        }
                        bar.actionRealisée();
                        actionCancel = bar.isActionCancel();
                        if (actionCancel) {
                            break;
                        }
                    }
                }
                bar.tacheCompleter();
                modele.actionTerminer();
                bar.dispose();
                return null;
            }
        };
        sw1.execute();
    }
}
