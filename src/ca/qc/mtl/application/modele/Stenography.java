/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.application.modele;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

/**
 *
 * @author Raphael
 */
public class Stenography extends JFrame {

    private final int LONGEUR = 500, LARGEUR = 500;
    private Modele mod;

    private JPanel pnlEncode = new JPanel(new BorderLayout());
    private JButton open = new JButton("Open"), embed = new JButton("Cacher le message"), save = new JButton("Save into new file"), reset = new JButton("Reset");
    private JTextArea message = new JTextArea(10, 3);
    private BufferedImage sourceImage = null, embeddedImage = null;
    private JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JScrollPane originalPane = new JScrollPane(), embeddedPane = new JScrollPane();

    //------------------
    private JPanel pnlDecode = new JPanel(new BorderLayout());
    private JButton openDecode = new JButton("Open"), decode = new JButton("Decode"), resetDecode = new JButton("Reset");
    private JTextArea messageDecode = new JTextArea(10, 3);
    private BufferedImage imageDecode = null;
    private JScrollPane imagePaneDecode = new JScrollPane();
    private JToolBar toolbar = new JToolBar();
    private JComboBox comboBox = new JComboBox(new String[]{"Cacher Message dans une image", "Trouver un message dans une image"});

    public Stenography(Modele mod) {
        super("Stegonographie app");
        this.mod = mod;
        assembleInterfaceDecode();
        assembleInterfaceEncode();
        this.setSize(LONGEUR, LARGEUR);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
//                getMaximumWindowBounds());
        add(pnlEncode);
        toolbar.add(comboBox);
        toolbar.setRollover(true);
        add(toolbar, BorderLayout.NORTH);
        eventsToolBar();
        mouseMouvement();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void eventsToolBar() {
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                changerInterface();
            }
        });
    }

    private void changerInterface() {
        if (comboBox.getSelectedIndex() == 0) {
            remove(pnlDecode);
            revalidate();
            repaint();
            add(pnlEncode);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        } else if (comboBox.getSelectedIndex() == 1) {
            remove(pnlEncode);
            revalidate();
            repaint();
            add(pnlDecode);
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        }

    }

    private void assembleInterfaceDecode() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(openDecode);
        p.add(decode);
        p.add(resetDecode);
        pnlDecode.add(p, BorderLayout.NORTH);
        openDecode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImageDecode();
            }
        });
        decode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decodeMessage();
            }
        });
        resetDecode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetInterfaceDecode();
            }
        });
        openDecode.setMnemonic('O');
        decode.setMnemonic('D');
        resetDecode.setMnemonic('R');

        p = new JPanel(new GridLayout(1, 1));
        p.add(new JScrollPane(messageDecode));
        messageDecode.setFont(new Font("Arial", Font.PLAIN, 12));
        p.setBorder(BorderFactory.createTitledBorder("Decoded message"));
        messageDecode.setEditable(false);
        pnlDecode.add(p, BorderLayout.SOUTH);

        imagePaneDecode.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
        pnlDecode.add(imagePaneDecode, BorderLayout.CENTER);
    }

    private void assembleInterfaceEncode() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(open);
        p.add(embed);
        p.add(save);
        p.add(reset);
        pnlEncode.add(p, BorderLayout.SOUTH);
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImageEncode();
            }
        });
        embed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                embedMessage();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetInterfaceEncode();
            }
        });
        open.setMnemonic('O');
        embed.setMnemonic('C');
        save.setMnemonic('S');
        reset.setMnemonic('R');

        p = new JPanel(new GridLayout(1, 1));
        p.add(new JScrollPane(message));
        message.setFont(new Font("Arial", Font.BOLD, 12));
        p.setBorder(BorderFactory.createTitledBorder("Message to be embedded"));
        pnlEncode.add(p, BorderLayout.NORTH);

        sp.setLeftComponent(originalPane);
        sp.setRightComponent(embeddedPane);
        originalPane.setBorder(BorderFactory.createTitledBorder("Original Image"));
        embeddedPane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
        pnlEncode.add(sp, BorderLayout.CENTER);
        sp.setDividerLocation(LONGEUR / 2);
    }

    private void openImageEncode() {
        java.io.File f = showFileDialog(true);
        try {
            sourceImage = ImageIO.read(f);
            JLabel l = new JLabel(new ImageIcon(sourceImage));
            originalPane.getViewport().add(l);
            this.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openImageDecode() {
        java.io.File f = showFileDialog(true);
        try {
            imageDecode = ImageIO.read(f);
            JLabel l = new JLabel(new ImageIcon(imageDecode));
            imagePaneDecode.getViewport().add(l);
            this.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private java.io.File showFileDialog(final boolean open) {
        JFileChooser fc = new JFileChooser("Open an image");
        javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                String name = f.getName().toLowerCase();
                if (open) {
                    return f.isDirectory() || name.endsWith(".jpg") || name.endsWith(".jpeg")
                            || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".tiff")
                            || name.endsWith(".bmp") || name.endsWith(".dib") || name.endsWith(".jfif");
                }
                return f.isDirectory() || name.endsWith(".png") || name.endsWith(".bmp");
            }

            @Override
            public String getDescription() {
                if (open) {
                    return "Image (*.jpg, *.jpeg, *.png, *.gif, *.tiff, *.bmp, *.dib, *.jfif)";
                }
                return "Image (*.png, *.bmp)";
            }
        };
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(ff);

        java.io.File f = null;
        if (open && fc.showOpenDialog(this) == fc.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        } else if (!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        }
        return f;
    }

    private void decodeMessage() {
        try {
            int len = extractInteger(imageDecode, 0, 0);
            byte b[] = new byte[len];
            for (int i = 0; i < len; i++) {
                b[i] = extractByte(imageDecode, i * 8 + 32, 0);
            }
            messageDecode.setText(new String(b));
        } catch (OutOfMemoryError ex) {
            messageDecode.setText("Aucun Message dans la photo     Erreur: " + ex.getMessage());
        } catch (Exception e) {
            messageDecode.setText("Aucun Message dans la photo     Erreur: " + e.getMessage());
        }

    }

    private void embedMessage() {
        String mess = message.getText();
        embeddedImage = sourceImage.getSubimage(0, 0,
                sourceImage.getWidth(), sourceImage.getHeight());
        embedMessage(embeddedImage, mess);
        JLabel l = new JLabel(new ImageIcon(embeddedImage));
        embeddedPane.getViewport().add(l);
        this.validate();
    }

    private void embedMessage(BufferedImage img, String mess) {
        int messageLength = mess.length();

        int imageWidth = img.getWidth(), imageHeight = img.getHeight(),
                imageSize = imageWidth * imageHeight;
        if (messageLength * 8 + 32 > imageSize) {
            JOptionPane.showMessageDialog(this, "Message is too long for the chosen image",
                    "Message too long!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        embedInteger(img, messageLength, 0, 0);

        byte b[] = mess.getBytes();
        for (int i = 0; i < b.length; i++) {
            embedByte(img, b[i], i * 8 + 32, 0);
        }
    }

    private void embedInteger(BufferedImage img, int n, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(n, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                count++;
            }
        }
    }

    private void embedByte(BufferedImage img, byte b, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(b, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                count++;
            }
        }
    }

    private int extractInteger(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        int length = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
                length = setBitValue(length, count, bit);
                count++;
            }
        }
        return length;
    }

    private byte extractByte(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        byte b = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
                b = (byte) setBitValue(b, count, bit);
                count++;
            }
        }
        return b;
    }

    private int setBitValue(int n, int location, int bit) {
        int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
        if (bv == bit) {
            return n;
        }
        if (bv == 0 && bit == 1) {
            n |= toggle;
        } else if (bv == 1 && bit == 0) {
            n ^= toggle;
        }
        return n;
    }

    private int getBitValue(int n, int location) {
        int v = n & (int) Math.round(Math.pow(2, location));
        return v == 0 ? 0 : 1;
    }

    private void resetInterfaceDecode() {
        messageDecode.setText("");
        imagePaneDecode.getViewport().removeAll();
        imageDecode = null;
        this.validate();
    }

    private void saveImage() {
        if (embeddedImage == null) {
            JOptionPane.showMessageDialog(this, "No message has been embedded!",
                    "Nothing to save", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.io.File f = showFileDialog(false);
        String name = f.getName();
        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        if (!ext.equals("png") && !ext.equals("bmp") && !ext.equals("dib")) {
            ext = "png";
            f = new java.io.File(f.getAbsolutePath() + ".png");
        }
        try {
            if (f.exists()) {
                f.delete();
            }
            ImageIO.write(embeddedImage, ext.toUpperCase(), f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetInterfaceEncode() {
        message.setText("");
        originalPane.getViewport().removeAll();
        embeddedPane.getViewport().removeAll();
        sourceImage = null;
        embeddedImage = null;
        sp.setDividerLocation(0.5);
        this.validate();
    }

    private void mouseMouvement() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        pnlEncode.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        pnlDecode.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        sp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        toolbar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        originalPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        embeddedPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        message.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        messageDecode.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });
        imagePaneDecode.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mod.resetTimer();
            }
        });

    }

}
