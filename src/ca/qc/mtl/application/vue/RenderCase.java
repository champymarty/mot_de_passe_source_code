/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import ca.qc.mtl.application.modele.Modele;
import ca.qc.mtl.application.modele.Modele.TypeString;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author raphael
 */
public class RenderCase extends DefaultTableCellRenderer {

    private Modele mod;
    private JTable tableau;
    private int ranger;

    public RenderCase(Modele mod, JTable tableau) {
        this.mod = mod;
        this.tableau = tableau;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        System.out.println("Row: "+row+" colum: "+column);
        TypeString type;
        ranger = tableau.convertRowIndexToModel(row);
        switch (column) {
            case 0:
                type = TypeString.SERVICE;
                break;
            case 1:
                type = TypeString.NOM;
                break;
            case 2:
                type = TypeString.MOT_DE_PASSE;
                break;
            case 3:
                type = TypeString.AUTRE;
                break;
            default:
                type = null;
                break;
        }
        String text = (String) value;
        if (column != 0) {
             text = mod.decryptString(text, type, ranger);
        }
        if (column == 2 || column == 3) {
            boolean visible = mod.isVisible(ranger);
            if (visible) {
                setText(text);
            } else {
                String string = "";
                for (int i = 0; i < text.length(); i++) {
                    string = string + "*";
                }
                setText(string);
            }
        } else {
            setText(text);
        }
        this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        return this;
    }
}
