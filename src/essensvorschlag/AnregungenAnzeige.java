/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author 
 */
public class AnregungenAnzeige extends JLabel{
    JTextArea text;
    JScrollPane scrollPane;
    JButton schliesen;
    JButton speichern;
    
    AnregungenAnzeige(){
        super();
        
        JLabel titel=new JLabel("Anregungen");
        titel.setBounds(10,5,100,30);
        add(titel);
        
        schliesen = new JButton("X");
        schliesen.setBackground(Color.RED);
        schliesen.setBounds(210, 5, 50, 30);
        schliesen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 0;
                EssensVorschlag.build();
            }
        });
        add(schliesen);
        speichern = new JButton("speichern");
        speichern.setBounds(100, 5, 100, 30);
        speichern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.anregungenSpeichern(text.getText());
            }
        });
        add(speichern);
        
        text = new JTextArea(5, 20);
        scrollPane = new JScrollPane(text);
        text.setEditable(true);
        text.setText(ladeAnregungen());
        add(scrollPane);
    }
    
    private String ladeAnregungen(){
        FileReader fr=null;
        try {
            
            fr = new FileReader(EssensVorschlag.dataDirectoryPath+EssensVorschlag.anregungenFilename);
            BufferedReader br=new BufferedReader(fr);
            
            String zeile=br.readLine();
            String ausgabe="";
            while(zeile!=null){
                ausgabe=ausgabe+zeile+"\n";
                zeile=br.readLine();
            }
            
            fr.close();
            return ausgabe;
        
        } catch (FileNotFoundException ex) {
            return "[AnregungenAnzeige](ladeAnregungen) nicht gefunden";
        } catch (IOException ex) {
            EssensVorschlag.writeToLogConsole(ex);
            return "Fehler";
        }
    }
    
    @Override
    public void setBounds(int x, int y, int breite, int hoehe) {
        super.setBounds(x, y, breite, hoehe);
        text.setBounds(0, 40, breite, hoehe - 40);
        scrollPane.setBounds(0, 40, breite, hoehe - 40);
    }
}