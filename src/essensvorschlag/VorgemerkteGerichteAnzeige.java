/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author 
 */
class VorgemerkteGerichteAnzeige extends JLabel {
    static int urlIndex=0; 
    
    VorgemerkteGerichteAnzeige() {
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
        
        JLabel name = new JLabel("Vorgemerkte Gerichte");
        name.setBounds(15, 5, 170, 40);
        add(name);
        
        JButton textVormerken = new JButton("text vormerken");
        textVormerken.setBounds(15, 45, 130, 30);
        textVormerken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(null, "Wie heißt das Gericht?");
                if (name != null) {
                    EssensVorschlag.vorgemerkteGerichte.add(new Gericht(name + "#0#0#null#"));
                    EssensVorschlag.build();
                }
            }
        });
        add(textVormerken);
        
        JButton zutatVormerken = new JButton("zutat add");
        zutatVormerken.setBounds(150, 45, 110, 30);
        zutatVormerken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(null, "Wie heißt die zutat?");
                if (name != null) {
                    EssensVorschlag.vorgemerkteGerichte.add(new Gericht("(zutat) "+name + "#0#0#null#"));
                    EssensVorschlag.build();
                }
            }
        });
        add(zutatVormerken);
        
        ArrayList<Gericht> vorgemerkteGerichte = EssensVorschlag.vorgemerkteGerichte;
        for (int i = 0; i < vorgemerkteGerichte.size(); i++) {
            VorgemerktesGerichtLabel vorgemerktesGerichtLabel; 
            vorgemerktesGerichtLabel=new VorgemerktesGerichtLabel(vorgemerkteGerichte.get(i));
            vorgemerktesGerichtLabel.setBounds(15, 90 + i * 40, 250, 30);
            add(vorgemerktesGerichtLabel);
        }
    }
    
    @Override
    public void setBounds(int x, int y, int breite, int hoehe){
        super.setBounds(x, y, breite, hoehe);
        
        //hintergrundbild
        String picUrl=getUrl();
        try{
            URL url=getClass().getResource(picUrl);
            BufferedImage image = ImageIO.read(url);

            int offsetHorizontal=0;
            if(image.getWidth()>breite){
                offsetHorizontal=(image.getWidth()-breite)/2;
//EssensVorschlag.schreiben("breite: "+image.getWidth()+" ; offset: "+offsetHorizontal);
            }

            image=image.getSubimage(offsetHorizontal, 0, image.getWidth()-offsetHorizontal, image.getHeight());
            ImageIcon bild = new ImageIcon(image);
            setIcon(bild);
        } catch (IOException ex) {
            EssensVorschlag.writeToLogConsole("[VorgemerkteGerichteAnzeige] bild nicht geladen; "+picUrl);
        }
    }
    
    public static String getUrl(){
        ArrayList<String> urls=new ArrayList();
        urls.add("Cosmetic_icon_Tempest's_Wrath.png");
        urls.add("Phoenix.png");
        urls.add("pizza.jpg");
        
        int start=1;
        if(urlIndex<start){
            urlIndex=(int)(Math.random()*(urls.size()-start))+start;
        }
        else{
            if(Math.random()<0.05){
                urlIndex=(int)(Math.random()*(urls.size()-start))+start;
            }
        }
        return "Files/Bilder/"+urls.get(urlIndex);
    }
}