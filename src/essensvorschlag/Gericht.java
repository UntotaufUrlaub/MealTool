/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author 
 */
class Gericht extends JLabel implements Comparable {
    
    String name;
    int stand;
    int wachstum;
    String letztesEssen;
    JLabel label;
    JButton essen;
    JButton downVote;
    JButton upVote;
    Vergleicher vergleicher;
    int suchAbstand = 0;
    int anzahlAnzeigen = 0;
    ArrayList<String> hashtags = new ArrayList();

    Gericht(String zeile){
        setValues(zeile);
        setAndAddLabelsButtons();
        setStandartVergleicher();
    }

    Gericht(Gericht g) {
        name = g.name;
        stand = g.stand;
        wachstum = g.wachstum;
        letztesEssen = g.letztesEssen;
        vergleicher = g.vergleicher;
        suchAbstand = g.suchAbstand;
        anzahlAnzeigen = g.anzahlAnzeigen;
        hashtags = g.hashtags; // das ist nur eine referez copy; achtung
        setAndAddLabelsButtons();
    }
    
   
    void setVergleicher(Vergleicher v) {
        vergleicher = v;
    }

    void setStandartVergleicher() {
        vergleicher = new Vergleicher() {
            @Override
            int vergleichen(Gericht a, Gericht b) {
                return b.stand - a.stand;
            }
        };
    }

    void sucheBeendet() {
        suchAbstand = 0;
        anzahlAnzeigen = 0;
        setStandartVergleicher();
    }

    private void setValues(String zeile){
        String[] temp = zeile.split("#", -1);
        name=temp[0];
        stand = (int) Integer.parseInt(temp[1]);
        wachstum = (int) Integer.parseInt(temp[2]);
        letztesEssen = temp[3];
        String[] tempHashTags = temp[4].split(";");
        for (int i = 0; i < tempHashTags.length; i++) {
            if (!tempHashTags[i].equals("")) {
                //wenn eine leere liste gesplittet wird entsteht ein "" und das braucht man nicht als hashtag
                hashtags.add(tempHashTags[i]);
            }
        }
    }

    private void setAndAddLabelsButtons() {
        label = new JLabel();
        //label.setBackground(Color.red);
        //label.setOpaque(true);
        label.setBounds(10, 0, 350, 40);
        label.setFont(new Font(Font.BOLD + "", 1, Integer.parseInt(EssensVorschlag.optionen[0])));
        updateLabel();
        this.add(label);
        essen = new JButton("essen");
        essen.setBounds(370, 0, 70, 40);
        essen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Gericht> liste;
                if (EssensVorschlag.planungsModus) {
                    liste = EssensVorschlag.listeZwischenSpeicher;
                } else {
                    liste = EssensVorschlag.liste;
                }
                if (liste.size() < 5) {
                    return;
                }
                //die ersten beiden schleifen sortiert die liste wieder standart mäßig, damit dann alle ausser die höchsten vier wachsen
                ArrayList<Vergleicher> vergleicher = new ArrayList(liste.size());
                for (int i = 0; i < liste.size(); i++) {
                    vergleicher.add(liste.get(i).vergleicher);
                }
                for (int i = 0; i < liste.size(); i++) {
                    liste.get(i).setStandartVergleicher();
                }
                Collections.sort(liste);
                //wachsen aller ausser der ersten vier;
                for (int i = 0; i < 4; i++) {
                    EssensVorschlag.schreiben("nicht wachsen: " + i + ": " + liste.get(i).name);
                }
                for (int i = 4; i < liste.size(); i++) {
                    liste.get(i).wachsen();
                }
                //wieder sortieren gemäß des sortieres der vor dem wachsen gesetzt war
                for (int i = 0; i < liste.size(); i++) {
                    liste.get(i).setVergleicher(vergleicher.get(i));
                }
                essen();
                EssensVorschlag.build();
            }
        });
        this.add(essen);
        downVote = new JButton("weniger");
        downVote.setBounds(450, 0, 80, 40);
        downVote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downVote();
                EssensVorschlag.build();
            }
        });
        this.add(downVote);
        upVote = new JButton("öfter");
        upVote.setBounds(540, 0, 70, 40);
        upVote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upVote();
                EssensVorschlag.build();
            }
        });
        this.add(upVote);
        Gericht selbst = this;
        JButton bearbeiten = new JButton("...");
        bearbeiten.setBounds(620, 0, 70, 40);
        bearbeiten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EssensVorschlag.zuBearbeitendesGericht = selbst;
                EssensVorschlag.MenueAnzeigeZoneZustand = 1;
                EssensVorschlag.build();
            }
        });
        this.add(bearbeiten);
    }

    void updateLabel() {
        String tageAbstand;
        try {
            tageAbstand = tageAbstand() + "";
        } catch (ParseException ex) {
            tageAbstand = "null ";
        }
        label.setText(name + " ( " + tageAbstand + "d )" + " (W:" + wachstum + ") (S:" + stand + ")");
    }

    int tageAbstand() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd.MM.yy");
        Date d1;
        Date d2 = new Date();
        d1 = format.parse(letztesEssen);
        long diff = d2.getTime() - d1.getTime();
        int temp = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return temp;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object o) {
        return vergleicher.vergleichen(this, (Gericht) o);
    }

    public void essen() {
        EssensVorschlag.schreiben("essen: " + name);
        stand = (int) (Math.round(Math.random() * stand / 7));
        EssensVorschlag.schreiben("planungsModus: " + EssensVorschlag.planungsModus);
        if (EssensVorschlag.planungsModus) {
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        this.letztesEssen = df.format(new Date());
        EssensVorschlag.tagebuchEintragen(name);
        //wachstum=wachstum+1;
    }

    public void wachsen() {
        stand = stand + this.wachstum;
    }

    public void downVote() {
        //stand=(int)Math.round(stand*0.5);
        int gewachseneTageApprox=stand/wachstum;
        wachstum = Math.max(1, (int) Math.round((wachstum - 1) * 0.8));
        stand=gewachseneTageApprox*wachstum;    //das gericht(der stand) wird runter skaliert auf das neue wachstum;
    }

    public void upVote() {
        int altesWachstum=wachstum;
        
        int maxWachstum = 0;
        ArrayList<Gericht> liste = EssensVorschlag.liste;
        for (int i = 0; i < liste.size(); i++) {
            maxWachstum = Math.max(maxWachstum, liste.get(i).wachstum);
        }
        int distanz = maxWachstum - wachstum;
        wachstum = (int) Math.round(wachstum + distanz * 0.3) + 1;
        //label.setText(name+" ("+wachstum+")");
        
        //stand skalieren
        int gewachseneTageApprox=stand/altesWachstum;
        stand=gewachseneTageApprox*wachstum;
    }

    public void weg() {
        EssensVorschlag.schreiben("weg: " + name);
        stand = (int) (Math.round(Math.random() * stand / 7));
    }   
}