/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author  */
class VorgemerktesGerichtLabel extends JLabel {
    
    VorgemerktesGerichtLabel(Gericht g) {
        JLabel name = new JLabel(g.name);
        name.setBounds(0, 0, 200, 30);
        //name.setOpaque(true);
        add(name);
        JButton löschen = new JButton("X");
        löschen.setBounds(200, 0, 50, 30);
        löschen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Predicate<Gericht> filter = (Gericht h) -> h.name.equals(g.name);
                EssensVorschlag.vorgemerkteGerichte.removeIf(filter);

                if (!EssensVorschlag.liste.contains(g)) {
                    if(!g.name.startsWith("(zutat) ")){
                        zuListeMöglicherInteressanterGerichtehinzufuegen(g.name);
                    }
                    else{
                        EssensVorschlag.writeToLogConsole("(ListeMöglicherInteressanterGerichte) nicht hinzugefuegt weil nur eine zutat");
                    }
                }
                else{
                    EssensVorschlag.writeToLogConsole("(ListeMöglicherInteressanterGerichte) schon ein Gericht");
                }
//EssensVorschlag.schreiben(test+"");
                EssensVorschlag.build();
            }
        });
        add(löschen);
    }

    static void zuListeMöglicherInteressanterGerichtehinzufuegen(String name) {
        try {
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy MMM dd");
            
            String line=df.format(d) + " : \t\t" + name + System.getProperty("line.separator");
            
            DataHandler.appendOneLineLocalTextFile(line, EssensVorschlag.anregungenFilename);
        } catch (IOException ioe) {
            EssensVorschlag.writeToLogConsole(ioe.getMessage());
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}