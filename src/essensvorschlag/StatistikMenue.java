/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author 
 */
public class StatistikMenue extends JLabel{
    JTextArea text;
    JScrollPane scrollPane;
    
    private int NochNieGegesseneGerichteInDerAnzeige=1;
    private int AngezeigteGerichteAnzahl=1;
    
    StatistikMenue(){
        super();
        JButton schliesen = new JButton("X");
        schliesen.setBackground(Color.RED);
        schliesen.setBounds(200, 5, 50, 40);
        schliesen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 0;
                EssensVorschlag.build();
            }
        });
        add(schliesen);
        
        JLabel titel=new JLabel("Statistiken");
        titel.setBounds(15,5,100,40);
        add(titel);
    }
    
    @Override
    public void setBounds(int x, int y, int breite, int hoehe){
        super.setBounds(x, y, breite, hoehe);
        
        text = new JTextArea(5, 20);
        scrollPane = new JScrollPane(text);
        text.setEditable(false);
        add(scrollPane);
        text.setBounds(0, 50, breite, hoehe - 50);
        scrollPane.setBounds(0, 50, breite, hoehe - 50);
        
        addGesammtesWachstum();
        addAngezeigteGerichteAnzahl();
        addNochNieGegesseneGerichteInDerAnzeigeAnzahl();
        addNochNieGegesseneGerichteInDerAnzeigeAnteil();
        addNullWachstumAngezeigteGerichteAnzahl();
        addGerichteMitWachstumNullUndUngegessenAnzahl();
        text.append("\n");
        addNochNieGegesseneGerichteAnzahl();
        addGerichteAnzahl();
    }
    
    private void addGesammtesWachstum(){
        int summe=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            summe=summe+EssensVorschlag.liste.get(i).wachstum;
        }
        text.append("Gesammtes Wachstum: "+summe);
        text.append("\n");
    }
    
    private void addGerichteAnzahl(){
        int gerichtAnzahl=EssensVorschlag.liste.size();
        text.append("Gerichte Anzahl: "+gerichtAnzahl);
        text.append("\n");
    }
    
    private void addAngezeigteGerichteAnzahl(){
        int zaehler=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            if(!EssensVorschlag.hatOhneHashTag(EssensVorschlag.liste.get(i))){
                if(EssensVorschlag.aktiveHashTagsNur.isEmpty()){
                    zaehler++;
                }
                else{
                    if(EssensVorschlag.hatNurHashTag(EssensVorschlag.liste.get(i))){
                        zaehler++;
                    }
                }
            }
        }
        AngezeigteGerichteAnzahl=zaehler;
        text.append("Anzahl Angezeigte Gerichte: "+zaehler);
        text.append("\n");
    }

    private void addNochNieGegesseneGerichteAnzahl() {
        int summe=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            try {
                EssensVorschlag.liste.get(i).tageAbstand();
            } catch (ParseException ex) {
                summe++;
            }
        }
        text.append("Anzahl ungegessener Gerichte: "+summe);
        text.append("\n");
    }
    
    private void addNochNieGegesseneGerichteInDerAnzeigeAnzahl(){
        int zaehler=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            if(!EssensVorschlag.hatOhneHashTag(EssensVorschlag.liste.get(i))){
                if(EssensVorschlag.aktiveHashTagsNur.isEmpty()){
                    try {
                        EssensVorschlag.liste.get(i).tageAbstand();
                    } catch (ParseException ex) {
                        zaehler++;
                    }
                }
                else{
                    if(EssensVorschlag.hatNurHashTag(EssensVorschlag.liste.get(i))){
                        try {
                            EssensVorschlag.liste.get(i).tageAbstand();
                        } catch (ParseException ex) {
                            zaehler++;
                        }
                    }
                }
            }
        }
        NochNieGegesseneGerichteInDerAnzeige=zaehler;
        text.append("Anzahl ungegessener Gerichte in der \nmomentanen Auswahl: "+zaehler);
        text.append("\n");
    }
    
    private void addNochNieGegesseneGerichteInDerAnzeigeAnteil(){
        int anteil=(int)Math.round(NochNieGegesseneGerichteInDerAnzeige/(AngezeigteGerichteAnzahl*1.0)*100);
        text.append("Anteil ungegessener Gerichte: "+anteil+"%");
        text.append("\n");
    }
    
    private void addNullWachstumAngezeigteGerichteAnzahl(){
        int zaehler=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            if(!EssensVorschlag.hatOhneHashTag(EssensVorschlag.liste.get(i))){
                if(EssensVorschlag.aktiveHashTagsNur.isEmpty()){
                    if(EssensVorschlag.liste.get(i).wachstum==0){
                        zaehler++;
                    }
                }
                else{
                    if(EssensVorschlag.hatNurHashTag(EssensVorschlag.liste.get(i))){
                        if(EssensVorschlag.liste.get(i).wachstum==0){
                            zaehler++;
                        }
                    }
                }
            }
        }
        AngezeigteGerichteAnzahl=zaehler;
        text.append("Anzahl Angezeigte Gerichte mit wachstum null: "+zaehler);
        text.append("\n");
    }
    
    private void addGerichteMitWachstumNullUndUngegessenAnzahl(){
        int zaehler=0;
        for(int i=0;i<EssensVorschlag.liste.size();i++){
            if(!EssensVorschlag.hatOhneHashTag(EssensVorschlag.liste.get(i))){
                if(EssensVorschlag.aktiveHashTagsNur.isEmpty()){
                    if(EssensVorschlag.liste.get(i).wachstum==0){
                        if(EssensVorschlag.liste.get(i).wachstum==0){
                            try {
                                EssensVorschlag.liste.get(i).tageAbstand();
                            } catch (ParseException ex) {
                                zaehler++;
                            }
                        }
                    }
                }
                else{
                    if(EssensVorschlag.hatNurHashTag(EssensVorschlag.liste.get(i))){
                        if(EssensVorschlag.liste.get(i).wachstum==0){
                            try {
                                EssensVorschlag.liste.get(i).tageAbstand();
                            } catch (ParseException ex) {
                                zaehler++;
                            }
                        }
                    }
                }
            }
        }
        AngezeigteGerichteAnzahl=zaehler;
        text.append("Anzahl Angezeigte Gerichte mit wachstum \nnull und ungegessen: "+zaehler);
        text.append("\n");
    }
}