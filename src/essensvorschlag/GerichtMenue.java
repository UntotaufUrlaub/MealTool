/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author 
 */
class GerichtMenue extends JLabel {
    
    int offsetlinks = 15;

    GerichtMenue(Gericht g) {
        super();
        //setBackground(Color.GREEN);
        //setOpaque(true);
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
        JLabel name = new JLabel(g.name);
        name.setBounds(offsetlinks, 5, 200, 40);
        add(name);
        
        JButton weg = new JButton("einmal aussetzen");
        weg.setToolTipText("schickt das gewählte gericht zum ende der liste (stand = 0) ohne seine Häufigkeit(wachstum) zu beinflussen");
        weg.setBounds(offsetlinks, 55, 150, 40);
        weg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.weg();
                EssensVorschlag.build();
            }
        });
        this.add(weg);
        
        JButton umbenennen = new JButton("umbenennen");
        umbenennen.setBounds(offsetlinks, 105, 120, 40);
        umbenennen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eingabe = JOptionPane.showInputDialog(null, "neuer name", g.name);
                if (eingabe == null) {
                } 
                else if(eingabe.contains("#")){
                    JOptionPane.showMessageDialog(EssensVorschlag.fenster, "Der name eines Gerichts darf kein # enthalten.\nUmbennen abgebrochen");
                }
                else {
                    
                    // umbennen des rezeptfiles falls es eins gibt
                    Path copySourcePath = Paths.get(EssensVorschlag.rezeptOrdnerPfad + g.name + ".txt");
                    Path copyTargetPath = Paths.get(EssensVorschlag.rezeptOrdnerPfad + eingabe + ".txt");
                    try {
                        Files.copy(copySourcePath, copyTargetPath);
                        Files.delete(copySourcePath);
                    } catch (NoSuchFileException ex1) {
                        EssensVorschlag.schreiben("(umbennenen) hatte noch kein rezept");
                    } catch (FileAlreadyExistsException ex2) {
                        EssensVorschlag.schreiben("(umbennenen) der name ist schon vergeben (und es gibt schon ein rezept dazu)");
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(GerichtMenue.class.getName()).log(Level.SEVERE, null, ex);
                        EssensVorschlag.schreiben(ex);
                    }

                    g.name=eingabe;
                }
                EssensVorschlag.build();
            }
        });
        this.add(umbenennen);
        
        JButton gerichtLöschenButton = new JButton("löschen");
        gerichtLöschenButton.setBounds(170, 105, 80, 40);
        gerichtLöschenButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int wirklichLöschen=JOptionPane.showConfirmDialog(null, 
                        "'"+g.name+"' wirklich unwiederruflich löschen?","Bestätigen",JOptionPane.YES_NO_OPTION);
                if(wirklichLöschen!=0){
                    EssensVorschlag.schreiben("Löschen von "+g.name+" abgebrochen.");
                    return;
                }
                EssensVorschlag.schreiben(g.name+" wurde gelöscht");
                EssensVorschlag.liste.remove(g);
                EssensVorschlag.build();
            }
        });
        this.add(gerichtLöschenButton);
        
        JButton addHashTag = new JButton("add #");
        addHashTag.setBounds(offsetlinks, 155, 70, 40);
        addHashTag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int anzahl = EssensVorschlag.alleHashTags.size();
                String[] selectionValues = new String[anzahl];
                for (int i = 0; i < anzahl; i++) {
                    selectionValues[i] = EssensVorschlag.alleHashTags.get(i).hashtag;
                }
                String selection = (String) JOptionPane.showInputDialog(null, "Welchen HashTag?", null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, null);
                if (selection != null) {
                    g.hashtags.add(selection);
                }
                EssensVorschlag.build();
            }
        });
        this.add(addHashTag);
        
        JButton removeHashTagButton = new JButton("remove #");
        removeHashTagButton.setBounds(offsetlinks, 205, 100, 40);
        removeHashTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = new String[g.hashtags.size()];
                for (int i = 0; i < g.hashtags.size(); i++) {
                    options[i] = g.hashtags.get(i);
                }
                String name = (String) JOptionPane.showInputDialog(null, "Welchen Tag?", null, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (name == null) {
                } else {
                    Predicate<String> predicate = (String h) -> h.equals(name);
                    g.hashtags.removeIf(predicate);
                }
                EssensVorschlag.build();
            }
        });
        add(removeHashTagButton);
        
        JButton vormerken = new JButton("vormerken");
        vormerken.setBounds(offsetlinks, 255, 100, 40);
        vormerken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.vorgemerkteGerichte.add(g);
                EssensVorschlag.build();
            }
        });
        add(vormerken);
        
        /*
        JButton boostButton = new JButton("boosten");
        boostButton.setBounds(150, 255, 100, 40);
        boostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.schreiben("boosten noch nicht implementiert, kommt aber bald");
            }
        });
        add(boostButton);
        */
        
        JButton rezeptAnzeigen = new JButton("Rezept Anzeigen");
        rezeptAnzeigen.setBounds(offsetlinks, 305, 130, 40);
        rezeptAnzeigen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.rezept = EssensVorschlag.rezeptLaden(g.name);
                EssensVorschlag.MenueAnzeigeZoneZustand = 4;
                EssensVorschlag.build();
            }
        });
        add(rezeptAnzeigen);
    }   
}