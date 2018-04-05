/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                        EssensVorschlag.schreiben("(ListeMöglicherInteressanterGerichte) nicht hinzugefuegt weil nur eine zutat");
                    }
                }
                else{
                    EssensVorschlag.schreiben("(ListeMöglicherInteressanterGerichte) schon ein Gericht");
                }
//EssensVorschlag.schreiben(test+"");
                EssensVorschlag.build();
            }
        });
        add(löschen);
    }

    static void zuListeMöglicherInteressanterGerichtehinzufuegen(String name) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(EssensVorschlag.anregungenFilename, true); //the true will append the new data
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy MMM dd");
            fw.write(df.format(d) + " : \t\t" + name + System.getProperty("line.separator")); //appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            EssensVorschlag.schreiben(ioe.getMessage());
            System.err.println("IOException: " + ioe.getMessage());
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                EssensVorschlag.schreiben(ex);
                Logger.getLogger(Gericht.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}