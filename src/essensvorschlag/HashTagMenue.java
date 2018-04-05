/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author 
 */
class HashTagMenue extends JLabel {
    
    HashTagMenue() {
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
        ArrayList<HashTagLabel> alleHashTags = EssensVorschlag.alleHashTags;
        for (int i = 0; i < alleHashTags.size(); i++) {
            alleHashTags.get(i).platzieren(15, 105 + i * 40);
            add(alleHashTags.get(i));
        }
        
        JButton addHashTagButton = new JButton("add #");
        addHashTagButton.setBounds(15, 55, 100, 40);
        addHashTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(null, "Wie heißt der Tag?");
                if (name == null) {
                } 
                else if(name.contains("#")){
                    JOptionPane.showMessageDialog(EssensVorschlag.fenster, "Der name eines Hashtags darf kein # enthalten.\nHinzufuegen des Hashtags abgebrochen");
                }
                else if(name.contains(";")){
                    JOptionPane.showMessageDialog(EssensVorschlag.fenster, "Der name eines Hashtags darf kein ; enthalten.\nHinzufuegen des Hashtags abgebrochen");
                }
                else {
                    alleHashTags.add(new HashTagLabel(name,null));
                }
                EssensVorschlag.build();
            }
        });
        add(addHashTagButton);
        
        JButton removeHashTagButton = new JButton("remove #");
        removeHashTagButton.setBounds(120, 55, 100, 40);
        removeHashTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = new String[alleHashTags.size()];
                for (int i = 0; i < alleHashTags.size(); i++) {
                    options[i] = alleHashTags.get(i).hashtag;
                }
                String name = (String) JOptionPane.showInputDialog(null, "Welchen Tag?", null, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (name == null) {
                } else {
                    Predicate<HashTagLabel> personPredicate = (HashTagLabel h) -> h.hashtag.equals(name);
                    alleHashTags.removeIf(personPredicate);
                }
                EssensVorschlag.build();
            }
        });
        add(removeHashTagButton);
    }   
}