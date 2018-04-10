/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author 
 */
class HashTagLabel extends JLabel {
    String hashtag;
    String[] möglichkeiten = {"neutral", "nur", "ohne"};
    JComboBox dropDown = new JComboBox(möglichkeiten);

    /*
    HashTagLabel(String name, int x, int y) {
        super();
        hashtag = name;
        platzieren(x, y);
        JLabel text = new JLabel(hashtag);
        text.setBounds(0, 0, 100, 40);
        add(text);
        dropDown.setBounds(100, 0, 100, 40);
        dropDown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    //reagiert nur auf change
                    String selected = dropDown.getSelectedItem().toString();
                    EssensVorschlag.schreiben("hashtag " + hashtag + " : " + selected);
                    if (selected.equals("ohne")) {
                        EssensVorschlag.aktiveHashTagsOhne.add(hashtag);
                    } else if (selected.equals("nur")) {
                        EssensVorschlag.aktiveHashTagsOhne.remove(hashtag);
                    } else {
                        EssensVorschlag.aktiveHashTagsOhne.remove(hashtag);
                    }
                    EssensVorschlag.build();
                }
            }
        });
        add(dropDown);
    }
    */
    
    HashTagLabel(String name, String auswahlIn) {
        super();
        hashtag = name;
        JLabel text = new JLabel(hashtag);
        text.setBounds(0, 0, 100, 40);
        add(text);
        
        dropDown.setBounds(100, 0, 100, 40);
        
        String auswahl;
        if(auswahlIn==null){
            auswahl=möglichkeiten[0];
        }
        else{
            auswahl=auswahlIn;
        }
        dropDown.setSelectedItem(auswahl);
        
        dropDown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    //reagiert nur auf change
                    String selected = dropDown.getSelectedItem().toString();
            EssensVorschlag.writeToLogConsole("hashtag '" + hashtag + "' : " + selected);
                    if (selected.equals("ohne")) {
                        EssensVorschlag.aktiveHashTagsOhne.add(hashtag);
                        EssensVorschlag.aktiveHashTagsNur.remove(hashtag);
                    } else if (selected.equals("nur")) {
                        EssensVorschlag.aktiveHashTagsNur.add(hashtag);
                        EssensVorschlag.aktiveHashTagsOhne.remove(hashtag);
                    } else {
                        EssensVorschlag.aktiveHashTagsOhne.remove(hashtag);
                        EssensVorschlag.aktiveHashTagsNur.remove(hashtag);
                    }
                    EssensVorschlag.build();
                }
            }
        });
        add(dropDown);
        
    }

    void platzieren(int x, int y) {
        setBounds(x, y, 200, 40);
    }
}