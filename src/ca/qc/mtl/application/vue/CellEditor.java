/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import ca.qc.mtl.application.modele.Modele;
import ca.qc.mtl.application.modele.Modele.TypeString;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author raphael
 */
public class CellEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextField txt;
    private String texte;
    private Modele modele;
    private TypeString type;
    private int posCompte;
    private JTable tableau;

    public CellEditor(Modele modele, JTable tableau) {
        super();
        this.modele = modele;
        this.tableau = tableau;
        txt = new JTextField();
        txt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textUpdate();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return texte;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        texte = (String) value;
        Modele.TypeString type;
        switch (column) {
            case 0:
                type = Modele.TypeString.SERVICE;
                break;
            case 1:
                type = Modele.TypeString.NOM;
                break;
            case 2:
                type = Modele.TypeString.MOT_DE_PASSE;
                break;
            case 3:
                type = Modele.TypeString.AUTRE;
                break;
            default:
                type = null;
                break;
        }
        this.type = type;
        posCompte = tableau.convertRowIndexToModel(row);
        if (type != Modele.TypeString.SERVICE) {
            txt.setText(modele.decryptString((String) value, type, posCompte));
        } else {
            txt.setText((String) value);
        }
        return txt;
    }

    private void textUpdate() {
        texte = modele.encryptString(txt.getText(), type, posCompte);
    }
}
