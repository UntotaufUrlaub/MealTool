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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author  */
class MenueLeiste extends JLabel {
    
    MenueLeiste() {
        super();
        setBackground(Color.BLUE);
        setOpaque(true);
        
        JButton hinzufuegen = new JButton("Hinzufügen");
        hinzufuegen.setBounds(10, 5, 100, 30);
        hinzufuegen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.writeToLogConsole("!Warnung! Falls der planungsmodus aktiv ist dann könnte das hinzufuegen nicht funktunieren");
                String name = JOptionPane.showInputDialog(null, "Wie heißt das Gericht?");
                if (name == null) {
                    return;
                } //abbrechen weil der abbrechen knopf gedrückt wurde;
                if(name.contains("#")){
                    JOptionPane.showMessageDialog(EssensVorschlag.fenster, "Der name eines Gerichts darf kein # enthalten.\nHinzufuegen abgebrochen");
                    return;
                }
                String wachstum = JOptionPane.showInputDialog(null, "Wie oft (Wachstum)?");
                if (wachstum == null) {
                    return;
                } //abbrechen weil der abbrechen knopf gedrückt wurde;
                int intWachstum;
                try {
                    intWachstum = Integer.parseInt(wachstum);
                } catch (NumberFormatException ex) {
                    EssensVorschlag.writeToLogConsole("hinzufuegen; wachstum; NumberFormatException");
                    intWachstum = 1;
                }
                int bald = JOptionPane.showConfirmDialog(null, "bald?", "An Insane Question", JOptionPane.YES_NO_OPTION);
                if (bald == 1) {
                    //nicht bald
                    bald = 0;
                } else {
                    //bald
                    bald = EssensVorschlag.liste.get(4).stand;
                }
                EssensVorschlag.writeToLogConsole("(hinzufuegen) stand: " + bald);
                EssensVorschlag.liste.add(new Gericht(name + "#" + bald + "#" + intWachstum + "#null#"));
                EssensVorschlag.build();
            }
        });
        add(hinzufuegen);
        
        JButton planungsmodusButton = new JButton();
        planungsmodusButton.setBounds(120, 5, 200, 30);
        if (EssensVorschlag.planungsModus) {
            planungsmodusButton.setText("PlanungsModus aktiviert");
            planungsmodusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //deaktivieren
                    EssensVorschlag.planungsModus = false;
                    EssensVorschlag.liste.clear();
                    for (int i = 0; i < EssensVorschlag.listeZwischenSpeicher.size(); i++) {
                        EssensVorschlag.liste.add(new Gericht(EssensVorschlag.listeZwischenSpeicher.get(i)));
                    }
                    EssensVorschlag.build();
                }
            });
        } else {
            planungsmodusButton.setText("PlanungsModus deaktiviert");
            planungsmodusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //aktivieren
                    EssensVorschlag.planungsModus = true;
                    EssensVorschlag.listeZwischenSpeicher.clear();
                    for (int i = 0; i < EssensVorschlag.liste.size(); i++) {
                        EssensVorschlag.listeZwischenSpeicher.add(new Gericht(EssensVorschlag.liste.get(i)));
                    }
                    EssensVorschlag.build();
                }
            });
        }
        add(planungsmodusButton);
        
        JButton log = new JButton("Log");
        log.setBounds(330, 5, 70, 30);
        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame fenster = EssensVorschlag.fenster;
                if (fenster.getBounds().height == 400) {
                    fenster.setBounds(fenster.getX(), fenster.getY(), fenster.getWidth(), 600);
                } else {
                    fenster.setBounds(fenster.getX(), fenster.getY(), fenster.getWidth(), 400);
                }
            }
        });
        add(log);
        
        JButton hashtagEinblenden = new JButton("#");
        hashtagEinblenden.setBounds(410, 5, 60, 30);
        hashtagEinblenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 2;
                EssensVorschlag.build();
            }
        });
        add(hashtagEinblenden);
        
        JButton vorgemerktesEinblenden = new JButton("vorgemerktes");
        vorgemerktesEinblenden.setBounds(480, 5, 130, 30);
        vorgemerktesEinblenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 3;
                EssensVorschlag.build();
            }
        });
        add(vorgemerktesEinblenden);
        
        JButton statistikenEinblenden=new JButton("Stats");
        statistikenEinblenden.setBounds(620, 5, 70, 30);
        statistikenEinblenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 5;
                EssensVorschlag.build();
            }
        });
        add(statistikenEinblenden);
        
        JButton listeMöglicherInteresannterGerichteButton=new JButton("Anregungen");
        listeMöglicherInteresannterGerichteButton.setBounds(10, 40, 110, 30);
        listeMöglicherInteresannterGerichteButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 6;
                EssensVorschlag.build();
            }
        });
        add(listeMöglicherInteresannterGerichteButton);
        
        JButton tagebuchButton=new JButton("Tagebuch");
        tagebuchButton.setBounds(130, 40, 90, 30);
        tagebuchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 7;
                EssensVorschlag.build();
            }
        });
        add(tagebuchButton);
        
        JButton notizenButton=new JButton("Notizen");
        notizenButton.setBounds(230, 40, 80, 30);
        notizenButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.MenueAnzeigeZoneZustand = 8;
                EssensVorschlag.build();
            }
        });
        add(notizenButton);
    }   
}