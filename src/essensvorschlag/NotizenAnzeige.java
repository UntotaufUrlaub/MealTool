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
public class NotizenAnzeige extends JLabel{
    JTextArea text;
    JScrollPane scrollPane;
    JButton speichern;
    JButton verwerfen;
    static String inhalt=null;
    
    NotizenAnzeige() {
        super();

        if(inhalt==null){
            inhalt=EssensVorschlag.notizenLaden();
        }
        
        text = new JTextArea(5, 20);
        scrollPane = new JScrollPane(text);
        text.setEditable(true);
text.setText(inhalt);
        add(scrollPane);
        text.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                //gerichtGeaendert=true;
                inhalt=text.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //gerichtGeaendert=true;
                inhalt=text.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //gerichtGeaendert=true;
                inhalt=text.getText();
            }
        });
        /*
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
        */
        speichern = new JButton("speichern");
        speichern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.notizenSpeichern(inhalt);
                //gerichtGeaendert=false;
            }
        });
        add(speichern);
        
        verwerfen = new JButton("verwerfen");
        verwerfen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inhalt=EssensVorschlag.notizenLaden();
                //gerichtGeaendert=false;
                EssensVorschlag.build();
            }
        });
        add(verwerfen);
    }

    @Override
    public void setBounds(int x, int y, int breite, int hoehe) {
        super.setBounds(x, y, breite, hoehe);
        text.setBounds(0, 0, breite, hoehe - 40);
        scrollPane.setBounds(0, 0, breite, hoehe - 40);
        speichern.setBounds(0, hoehe - 40, 100, 40);
        verwerfen.setBounds(160,hoehe -40,100,40);
    }   
}
