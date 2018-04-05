/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author 
 */
class MenueHintergrund extends JLabel {
    
    MenueHintergrund() {
        super();
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.black, 5));
    }
    
    @Override
    public void setBounds(int x, int y, int breite, int hoehe){
        super.setBounds(x, y, breite, hoehe);
        
        //hintergrundbild; das hier ist fast identisch mit dem bild laden in [VorgemerkteGerichteAnzeige], dass könnte man zusammenfassen
        String picUrl=VorgemerkteGerichteAnzeige.getUrl();
        try{
            URL url=getClass().getResource(picUrl);
            BufferedImage image = ImageIO.read(url);

            int offsetHorizontal=0;
            if(image.getWidth()>breite){
                int breiteOhneRand=(breite-10);
                offsetHorizontal=(image.getWidth()-breiteOhneRand)/2;
//EssensVorschlag.schreiben("breite: "+image.getWidth()+" ; offset: "+offsetHorizontal);
            }

            image=image.getSubimage(offsetHorizontal, 0, image.getWidth()-offsetHorizontal, image.getHeight());
            ImageIcon bild = new ImageIcon(image);
            setIcon(bild);
        } catch (IOException ex) {
            EssensVorschlag.schreiben("[MenueHintergrund] bild nicht geladen; "+picUrl);
        }
    }
}