/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.vue;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author raphael
 */
public class CellRenderBouton extends JButton implements TableCellRenderer {

    private String txt;

    public CellRenderBouton(String txt) {
        super(txt);
        this.txt = txt;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setBackground(Color.WHITE);
        this.setBorder(new LineBorder(Color.LIGHT_GRAY));
        return this;
    }

}
