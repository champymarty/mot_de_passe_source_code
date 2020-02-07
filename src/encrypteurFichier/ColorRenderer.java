/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypteurFichier;

import encrypteurFichier.Fichier.Statut;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class ColorRenderer extends JLabel  implements TableCellRenderer  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3173319120279751617L;

	private EncrypteurModele modele;
	private EncrypteurFenetre vue;
	
	public ColorRenderer(EncrypteurModele modele, EncrypteurFenetre vue) {
		this.modele = modele;
		this.vue = vue;
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
        setOpaque(true);
		Statut stat = modele.getFichierStatus(vue.convertRowIndexToModel(row));
		if(stat == Statut.ENCRYPTED) {
			this.setBackground(Color.GREEN);
		}else if(stat == Statut.NORMAL) {
			this.setBackground(Color.WHITE);
		}else if(stat == Statut.ERREUR) {
			this.setBackground(Color.RED);
		}
		this.setText((String)value.toString());
		if(column == 1) {
	        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		}else {
			setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		}
		return this;
	}

}
