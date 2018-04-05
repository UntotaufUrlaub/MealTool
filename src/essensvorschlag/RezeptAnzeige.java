/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author 
 */
class RezeptAnzeige extends JLabel {
    JTextArea text;
    JScrollPane scrollPane;
    JButton schliesen;
    JButton speichern;

    boolean gerichtGeaendert=false;
    
    RezeptAnzeige(String rezept) {
        super();
//EssensVorschlag.schreiben("[RezeptAnzeige] rezept: " + rezept);
        text = new JTextArea(5, 20);
        scrollPane = new JScrollPane(text);
        text.setEditable(true);
        text.setText(rezept);
        add(scrollPane);
        text.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                gerichtGeaendert=true;
                EssensVorschlag.rezept=text.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                gerichtGeaendert=true;
                EssensVorschlag.rezept=text.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                gerichtGeaendert=true;
                EssensVorschlag.rezept=text.getText();
            }
        });
        
        schliesen = new JButton("X");
        schliesen.setBackground(Color.RED);
        schliesen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gerichtGeaendert){
                    int trotzdemSchliesen=JOptionPane.showConfirmDialog(null, "das gericht ist momentan ungespeichert\ntrotzdem schliesen?","Bestätigen",JOptionPane.YES_NO_OPTION);
                    if(trotzdemSchliesen!=0){
                        return;
                    }
                }
                EssensVorschlag.MenueAnzeigeZoneZustand = 0;
                EssensVorschlag.build();
            }
        });
        add(schliesen);
        speichern = new JButton("speichern");
        speichern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.rezeptSpeichern(EssensVorschlag.zuBearbeitendesGericht.name, text.getText());
                gerichtGeaendert=false;
            }
        });
        add(speichern);
    }

    @Override
    public void setBounds(int x, int y, int breite, int hoehe) {
        super.setBounds(x, y, breite, hoehe);
        text.setBounds(0, 0, breite, hoehe - 40);
        scrollPane.setBounds(0, 0, breite, hoehe - 40);
        schliesen.setBounds(breite - 60, hoehe - 40, 60, 40);
        speichern.setBounds(0, hoehe - 40, 100, 40);
    }   
}