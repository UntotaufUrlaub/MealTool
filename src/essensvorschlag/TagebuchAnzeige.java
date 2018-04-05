/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author 
 */
public class TagebuchAnzeige extends JLabel{
    JTextArea text;
    JScrollPane scrollPane;
    
    TagebuchAnzeige(){
        super();
        
        JLabel titel=new JLabel("Tagebuch");
        titel.setBounds(10,5,100,30);
        add(titel);
        
        text = new JTextArea(5, 20);
        scrollPane = new JScrollPane(text);
        text.setEditable(false);
        text.setText(ladeTageBuch());
        add(scrollPane);
    }
    
    private String ladeTageBuch(){
        FileReader fr=null;
        try {
            
            fr = new FileReader(EssensVorschlag.TagebuchFilename);
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
            return "[TagebuchAnzeige](ladeTageBuch) nicht gefunden";
        } catch (IOException ex) {
            EssensVorschlag.schreiben(ex);
            return "Fehler";
        }
    }
    
    public void setBounds(int x, int y, int breite, int hoehe) {
        super.setBounds(x, y, breite, hoehe);
        text.setBounds(0, 40, breite, hoehe - 40);
        scrollPane.setBounds(0, 40, breite, hoehe - 40);
    }
}