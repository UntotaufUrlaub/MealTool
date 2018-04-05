/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
--- atm:
*/
package essensvorschlag;

import static essensvorschlag.EssensVorschlag.liste;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static essensvorschlag.EssensVorschlag.schreiben;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

/**
 *
 * @author 
 */
public class EssensVorschlag {
    static ArrayList<Gericht> liste=new ArrayList();
    static ArrayList<Gericht> listeZwischenSpeicher=new ArrayList();
    static JFrame fenster=new JFrame("");
    static String pfad=null;
    static ArrayList<String> zeilen=new ArrayList();
    static JButton suche;
    static JButton go;
    static boolean booleanSuchen=false;
    static String zuSuchen="";
    static boolean suchTextMarkieren=false;
    static boolean planungsModus=false;
    static String version="1.22p";
    static String[] optionen=new String[5]; // 0: schriftgröße 
                                          // | 1: Fenster Pos X
                                          // | 2: Fenster Pos Y
                                          // | 3: Standart Background Farbe
                                          // | 4: PlanungsModus Background Farbe
    static JTextArea textArea = new JTextArea(5, 20);
    static ArrayList<String> aktiveHashTagsOhne=new ArrayList();
    static ArrayList<String> aktiveHashTagsNur=new ArrayList();
    static ArrayList<HashTagLabel> alleHashTags=new ArrayList();
    static int fensterBreite=1000;
    
    static int MenueAnzeigeZoneZustand=3;     // 0: leer;
                                              // 1: gericht bearbeiten;
                                              // 2: hashtag menue
                                              // 3: vorgemerkte Gerichte
                                              // 4: rezept
                                              // 5: statistiken
                                              // 6: anregungen
                                              // 7: Tagebuch
                                              // 8: notizen
    static Gericht zuBearbeitendesGericht;
    static ArrayList<Gericht> vorgemerkteGerichte=new ArrayList(7);
    static String rezept="";
    static String rezeptOrdnerPfad="Rezepte/";
    
    static String TagebuchFilename= "tagebuch.txt";
    static String anregungenFilename= "ListeMöglicherInteressanterGerichte.txt";
    static String notizenFilePfad= "Notizen.txt";
    static int[] kategorien=new int[3];     // 0: nudeln
                                            // 1: kartoffeln
                                            // 2: reis
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            getOption();
            
            loadAlleHashTagsListe();
            loadVorgemerkteGerichte();
        
            checkRezepteOrdner();
            
            setupWindow();
        
            loadData();
            
            build();
        }
        catch(Exception e){
            String errorMessage=schreiben(e);
            JOptionPane.showMessageDialog(fenster, "Fehler aufgetreten \n(es kommt noch ein fenster mit der meldung; erscheint nur einmal)\n(screenshot empfohlen)");
            JOptionPane.showMessageDialog(fenster,errorMessage);
            System.exit(0);
        }
    }
    
    private static void setupWindow(){
        fenster.setBounds(Integer.parseInt(optionen[1]), Integer.parseInt(optionen[2]), fensterBreite, 400);
        fenster.setLayout(null);
        fenster.setVisible(true);
//fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenster.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                speichern();
                alleHashTagsListeSpeichern();
                speichernVorgemerkteGerichteFile();
                System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                }

            @Override
            public void windowClosed(WindowEvent e) {
                }

            @Override
            public void windowIconified(WindowEvent e) {
                }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }
    
    private static void loadData(){
        FileReader fr=null;
        BufferedReader br;
        try {
            fr = new FileReader("pfad.txt");
            br= new BufferedReader(fr);
            try {
                pfad=br.readLine();
            } catch (IOException ex) {
                schreiben(ex);
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            setPfad();
        }
        
        schreiben(pfad);

        try {
            fr=new FileReader(pfad);
        } catch (FileNotFoundException ex) {
            schreiben(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
        br=new BufferedReader(fr);
        
        String zeile;
        try {
            zeile=br.readLine();
            while(zeile!=null){
                zeilen.add(zeile);
                zeile=br.readLine();
            }
        } catch (IOException ex) {
            schreiben(ex);
        }
        
        checkGerichteVersion();
        
        for(int i=1;i<zeilen.size();i++){
            Gericht g=new Gericht(zeilen.get(i));
            liste.add(g);
        }
    }
    
    private static void checkGerichteVersion(){
        while(true){
            if(!zeilen.get(0).startsWith("Version:")){
                zeilen.add(0,"Version:0.1.1");
                for(int i=1;i<zeilen.size();i++){
                    zeilen.set(i,zeilen.get(i)+"#null");
                    schreiben("(checkVersion) zeile "+i+" : "+zeilen.get(i));
                }
            }
            else if(zeilen.get(0).equals("Version:0.1.1")){
                zeilen.set(0,"Version:0.1.2");
                for(int i=1;i<zeilen.size();i++){   
                    zeilen.set(i,zeilen.get(i)+"#");//Anhängen eines weiteren arguments
                }
            }
            else{
                return;
            }
            schreiben("updated Data to "+zeilen.get(0));
        }
    }
    
    private static void getOption(){
        FileReader fr;
        BufferedReader br;
        
        try {
            fr = new FileReader("optionFile.txt");
        } catch (FileNotFoundException ex) {
            schreiben("(getOption) OptionFile: wurde erstellt");
            FileWriter fw;
            BufferedWriter bw=null;
            try {
                fw = new FileWriter("optionFile.txt");
                bw = new BufferedWriter(fw);
                
                bw.write("V 1");
                bw.newLine();
                
                bw.write("schriftgröße : 12");
                bw.newLine();
                optionen[0]="12";
                
                bw.write("Fenster Pos X : 0");
                bw.newLine();
                optionen[1]="0";
                
                bw.write("Fenster Pos Y : 0");
                bw.newLine();
                optionen[2]="0";
                
                bw.write("Standart Background Farbe : 255,255,255");
                bw.newLine();
                optionen[3]="255,255,255";
                
                bw.write("PlanungsModus Background Farbe : 219,29,4");
                optionen[4]="219,29,4";
                
                schreiben("(getOption) geschrieben");
                
            } catch (IOException ex1) {
                schreiben(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
            finally{
                try {
                    if(bw!=null){
                        bw.close();
                    }
                } catch (IOException ex1) {
                    schreiben(ex.getMessage());
                    Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return;
        }
        
        schreiben("(getOption) OptionFile: gefunden");
        
        br=new BufferedReader(fr);
        try {
            String optionVersion=br.readLine();
            schreiben("(getOption) version: "+optionVersion);
            optionen[0]=br.readLine().substring(15);
            schreiben("(getOption) schriftgröße: "+optionen[0]);
            
            optionen[1]=br.readLine().substring(16);
            optionen[2]=br.readLine().substring(16);
            optionen[3]=br.readLine().substring(28);
            optionen[4]=br.readLine().substring(33);
        } catch (IOException ex1) {
            schreiben(ex1.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    private static void loadAlleHashTagsListe(){
        FileReader fr;
        BufferedReader br;
        
        try {
            fr = new FileReader("hashTagFile.txt");
        } catch (FileNotFoundException ex) {
            schreiben("(loadAlleHashTagsListe) hashTagFile: wurde erstellt.");
            FileWriter fw;
            BufferedWriter bw=null;
            try {
                fw = new FileWriter("hashTagFile.txt");
                bw = new BufferedWriter(fw);
                
                bw.write("Version: 1");
            } catch (IOException ex1) {
                schreiben(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
            finally{
                try {
                    if(bw!=null)bw.close();
                } catch (IOException ex1) {
                    schreiben(ex.getMessage());
                    Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return; //dadurch das hier abgebrochen wird, wird nur n file mit "Version 1" und ansonsten nix erstellt. 
                    //Da das file leer ist passiert kein fehler beim einlesen(da ja nix eingelesen wird). 
                    //beim speichern wird dann automatisch alles in der richtigen version gespeichert
        }
        
        schreiben("(loadAlleHashTagsListe) hashTagFile: gefunden.");
        
        br=new BufferedReader(fr);
        
        String zeile;
        ArrayList<String> zeilen=new ArrayList();
        try {
            zeile=br.readLine();
            while(zeile!=null){
                zeilen.add(zeile);
                zeile=br.readLine();
            }
        } catch (IOException ex) {
            schreiben(ex);
        }
        
        checkHashTagsListeVersion(zeilen);
            
        for(int i=0;i<zeilen.size();i++){
schreiben("(loadAlleHashTagsListe) "+zeilen.get(i));
            String[] trennung=zeilen.get(i).split("#");
            String name=trennung[0];
            String auswahl=trennung[1];
            EssensVorschlag.alleHashTags.add(new HashTagLabel(name,auswahl));
            if (auswahl.equals("ohne")) {
                EssensVorschlag.aktiveHashTagsOhne.add(name);
            } else if (auswahl.equals("nur")) {
                EssensVorschlag.aktiveHashTagsNur.add(name);
            }
        }
    }
    
    private static void checkHashTagsListeVersion(ArrayList<String> zeilen){
        while(true){
            if(zeilen.get(0).equals("Version: 1")){
                zeilen.set(0, "Version: 2");
                for(int i=1;i<zeilen.size();i++){
                    String oldLine=zeilen.get(i);
                    String newLine=oldLine+"#neutral";
                    zeilen.set(i, newLine);
                }

            }
            else if(zeilen.get(0).equals("Version: 2")){
                zeilen.remove(0);
                return;
            }
            else{
                throw new RuntimeException("Fehler bei der HashtagFile Version");
            }
            schreiben("HashTagsListe geupdated auf "+zeilen.get(0));
        }
    }
    
    private static void loadVorgemerkteGerichte(){
        FileReader fr;
        BufferedReader br;
        
        try {
            fr = new FileReader("VorgemerkteGerichteFile.txt");
        } catch (FileNotFoundException ex) {
            schreiben("(loadVorgemerkteGerichte) VorgemerkteGerichteFile: wurde erstellt.");
            FileWriter fw;
            BufferedWriter bw=null;
            try {
                fw = new FileWriter("VorgemerkteGerichteFile.txt");
                bw = new BufferedWriter(fw);
                
                bw.write("Version: 1");
            } catch (IOException ex1) {
                schreiben(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
            finally{
                try {
                    if(bw!=null)bw.close();
                } catch (IOException ex1) {
                    schreiben(ex.getMessage());
                    Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return;
        }
        
        schreiben("(loadVorgemerkteGerichte) VorgemerkteGerichteFile: gefunden.");
        
        br=new BufferedReader(fr);
        try {
            String zeile=br.readLine();
            if(zeile.equals("Version: 1")){
                zeile=br.readLine();
            }
            else{
                throw new RuntimeException("Fehler bei der VorgemerkteGerichteFile Version");
            }
            while(zeile!=null){
//schreiben("(loadAlleHashTagsListe) "+zeile);
                int zeiger=-1;
                for(int i=0;i<liste.size();i++){
                    if(zeile.equals(liste.get(i).name)){
                        zeiger=i;
                    }
                }
                if(zeiger==-1){
                    EssensVorschlag.vorgemerkteGerichte.add(new Gericht(zeile+"#0#0#null#"));
                }
                else{
                    EssensVorschlag.vorgemerkteGerichte.add(liste.get(zeiger));
                }
                zeile=br.readLine();
            }
        } catch (IOException ex1) {
            schreiben(ex1.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    static void speichern(){
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            String content;
            fw = new FileWriter(pfad);
            bw = new BufferedWriter(fw);
            bw.write(zeilen.get(0));
            bw.newLine();
            for(int i=0;i<liste.size();i++){
                Gericht g=liste.get(i);
                content=g.name+"#"+g.stand+"#"+g.wachstum+"#"+g.letztesEssen;
                //hashtags
                content=content+"#";
                for(int j=0;j<g.hashtags.size();j++){
                    content=content+g.hashtags.get(j)+";";
                }
                
                bw.write(content);
                bw.newLine();
            }
schreiben("(gerichte) gespeichert");
        } catch (IOException e) {
            schreiben(e.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                schreiben(ex.getMessage());
            }
        }
    }
    
    static void alleHashTagsListeSpeichern(){
        BufferedWriter bw;
        FileWriter fw;
        
        try{
            fw=new FileWriter("hashTagFile.txt");
            bw=new BufferedWriter(fw);
            bw.write("Version: 2");
            bw.newLine();
            for(int i=0;i<EssensVorschlag.alleHashTags.size();i++){
                bw.write(alleHashTags.get(i).hashtag+"#"+alleHashTags.get(i).dropDown.getSelectedItem());
                bw.newLine();
            }
            bw.close();
        }
        catch(Exception ex){
            schreiben(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void speichernVorgemerkteGerichteFile(){
        BufferedWriter bw;
        FileWriter fw;
        
        try{
            fw=new FileWriter("VorgemerkteGerichteFile.txt");
            bw=new BufferedWriter(fw);
            bw.write("Version: 1");
            bw.newLine();
            for(int i=0;i<EssensVorschlag.vorgemerkteGerichte.size();i++){
                bw.write(vorgemerkteGerichte.get(i).name);
                bw.newLine();
            }
            bw.close();
        }
        catch(Exception ex){
            schreiben(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void setPfad(){
        schreiben("setPfad");
        
        int fileAuswählen= JOptionPane.showConfirmDialog(
                            null,
                            "gerichte Liste wählen?\n(oder neu anlegen->nein)",
                            "An Insane Question",
                            JOptionPane.YES_NO_OPTION);
        
        if(fileAuswählen==1){//nicht auswälen-> neu erstellen;
            System.out.println("neu erstellen");
            neuesGerichtFile();
            return;
        }
        
        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        pfad = fc.getSelectedFile().getPath();
        writePfad();
    }
    
    static void writePfad(){
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter("pfad.txt");
            bw = new BufferedWriter(fw);
            bw.write(pfad);
        } catch (IOException e) {
            schreiben(e.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                schreiben(ex.getMessage());
            }
        }
    }
    
    static void neuesGerichtFile(){
        String gerichtFilePfad="gerichte(auto).txt";
        
        FileWriter fw;
        BufferedWriter bw;
        try{
            //erstellt das neue File;
            fw = new FileWriter(gerichtFilePfad);
            bw = new BufferedWriter(fw);
            bw.write("dummy1#0#1");
            bw.newLine();
            bw.write("dummy2#0#1");
            bw.newLine();
            bw.write("dummy3#0#1");
            bw.newLine();
            bw.write("dummy4#0#1");
            bw.newLine();
            bw.write("dummy5#0#1");
            bw.newLine();
            bw.close();
        }catch(IOException ioex){
            schreiben(ioex);
        }
        EssensVorschlag.pfad=gerichtFilePfad;
        writePfad();
    }
    
    static void build(){
        Collections.sort(liste);
        if(liste.get(0).stand>10000){
            schreiben("warnung: stand größer 10000");
        }
        show();
    }
    
    static void show(){
        EssensVorschlag.fenster.getContentPane().removeAll();
        for(int i=0;i<liste.size();i++){
            liste.get(i).updateLabel();
        }
        
        MenueLeiste menueLeiste=new MenueLeiste();
        menueLeiste.setBounds(0, 0, 700, 75);
        fenster.add(menueLeiste);
        
        MenueHintergrund menue=new MenueHintergrund();
        menue.setBounds(700, 0, 284, 363);
        fenster.add(menue);
        
        
        if(MenueAnzeigeZoneZustand==1){
            GerichtMenue gerichtMenue=new GerichtMenue(zuBearbeitendesGericht);
            gerichtMenue.setBounds(5,5,274,353);
            menue.add(gerichtMenue);
        }
        else if(MenueAnzeigeZoneZustand==2){
            HashTagMenue hashtagMenue=new HashTagMenue();
            hashtagMenue.setBounds(5,5,274,353);
            menue.add(hashtagMenue);
        }
        else if(MenueAnzeigeZoneZustand==3){
            VorgemerkteGerichteAnzeige vorgemerkteGerichteAnzeige=new VorgemerkteGerichteAnzeige();
            vorgemerkteGerichteAnzeige.setBounds(5,5,274,353);
            menue.add(vorgemerkteGerichteAnzeige);
        }
        else if(MenueAnzeigeZoneZustand==4){
            RezeptAnzeige rezeptAnzeige=new RezeptAnzeige(rezept);
            rezeptAnzeige.setBounds(5,5,274,353);
            menue.add(rezeptAnzeige);
        }
        else if(MenueAnzeigeZoneZustand==5){
            StatistikMenue statistikMenue=new StatistikMenue();
            statistikMenue.setBounds(5,5,274,353);
            menue.add(statistikMenue);
        }
        else if(MenueAnzeigeZoneZustand==6){
            AnregungenAnzeige anregungenAnzeige=new AnregungenAnzeige();
            anregungenAnzeige.setBounds(5,5,274,353);
            menue.add(anregungenAnzeige);
        }
        else if(MenueAnzeigeZoneZustand==7){
            TagebuchAnzeige tagebuchAnzeige=new TagebuchAnzeige();
            tagebuchAnzeige.setBounds(5,5,274,353);
            menue.add(tagebuchAnzeige);
        }
        else if(MenueAnzeigeZoneZustand==8){
            NotizenAnzeige notizenAnzeige=new NotizenAnzeige();
            notizenAnzeige.setBounds(5,5,274,353);
            menue.add(notizenAnzeige);
        }
        
        JButton einmaligEssenButton=new JButton("'Einmaliges Essen' eintragen");
        einmaligEssenButton.setBounds(450, 105, 200, 40);
        einmaligEssenButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String name=JOptionPane.showInputDialog(null, "Wie heißt das Gericht?");
                if(name==null){return;}
                tagebuchEintragen(name);
                VorgemerktesGerichtLabel.zuListeMöglicherInteressanterGerichtehinzufuegen(name);
            }
        });
        fenster.add(einmaligEssenButton);
        
        JLabel versionLabel=new JLabel("V "+EssensVorschlag.version);
        versionLabel.setBounds(600, 70, 70, 40);
        fenster.add(versionLabel);
        
        textArea.setBounds(0, 370, fensterBreite-15, 190);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0, 370, fensterBreite-15, 190);
        textArea.setEditable(false);
        textArea.select(Integer.MAX_VALUE, 0);  //das sorgt dafür dass das scrollbar im log feld immer ganz unten ist.
        
        fenster.add(scrollPane);
        
        if(planungsModus){
            fenster.getContentPane().setBackground(getColor(optionen[4]));
        }
        else{
            fenster.getContentPane().setBackground(getColor(optionen[3]));
        }
        
        suche=new JButton();
        suche.setBounds(10, 105, 130, 40);
        fenster.add(suche);
        
        if(EssensVorschlag.booleanSuchen){
            suche.setText("suche beenden");
            suche.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    EssensVorschlag.booleanSuchen=false;
                    for(int i=0;i<liste.size();i++){
                        liste.get(i).setStandartVergleicher();
                    }
                    suchTextMarkieren=true;
                    build();
                }
            });
            
            JTextField textfeld=new JTextField(zuSuchen);
            textfeld.setBounds(150, 105, 200, 40);
            textfeld.addKeyListener(new KeyListener(){ //dieser key listener soll "enter" abfangen und ddafür die suche starten               @Override
                public void keyTyped(KeyEvent e) {                }
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
//schreiben("enter");
                        suchen(textfeld.getText());
                    }
                }
                public void keyReleased(KeyEvent e) {}
            });
            if(suchTextMarkieren){
                textfeld.select(0, zuSuchen.length());
            }
            
            EssensVorschlag.fenster.add(textfeld);
            
            go=new JButton("go");
            go.setBounds(360, 105, 70, 40);
            go.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    suchen(textfeld.getText());
                }
            });
            fenster.add(go);
            textfeld.requestFocus();
        }
        else{
            suche.setText("suche starten");
            suche.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    EssensVorschlag.booleanSuchen=true;
                    
                    for(int i=0;i<liste.size();i++){
                        liste.get(i).sucheBeendet();
                    }
                    
                    build();
                }
            });
        }
        
// anzeigen der vier gericht vorschläge;        
        if(booleanSuchen&&!zuSuchen.equals("")){// wenn gesucht wird, wird nicht nach hashtags gefilltert; isst ausgeschalltet; muss erst ermittelt werden was da sinn macht
            for(int i=0;i<4;i++){//die 4 ist die anzahl der gerichte die dargestellt werden im fenster
                liste.get(i).setBounds(0, i*40+160, fensterBreite, 40);

                liste.get(i).anzahlAnzeigen++;
                fenster.add(liste.get(i));
            }
        }
        else{
            int zeiger=0;
            kategorien[0]=0;
            kategorien[1]=0;
            kategorien[2]=0;
            for(int i=0;i<4;i=i){//die 4 ist die anzahl der gerichte die dargestellt werden im fenster
                if(booleanSuchen){
                    liste.get(zeiger).anzahlAnzeigen++;
                }
                // wenn sich dass hier ändert dann muss auch im statistik menue die kopie angepasst wereden; bei anzahl angezeigte gerichte
                boolean anzeigen=false;
                if(!hatOhneHashTag(liste.get(zeiger))){
                    if(aktiveHashTagsNur.isEmpty()){
                        anzeigen=true;
                    }
                    else{
                        if(hatNurHashTag(liste.get(zeiger))){
                            anzeigen=true;
                        }
                    }
                }
                
                //checken in welcher kategorie;
                if(liste.get(zeiger).name.contains("nudel")
                        || liste.get(zeiger).name.contains("Nudel")){
                    kategorien[0]++;
                    schreiben(liste.get(zeiger).name+" füllt kategorie nudeln auf stand "+kategorien[0]);
                    if(kategorien[0]>2){
                        anzeigen=false;
                        schreiben(liste.get(zeiger).name+" nicht angezeigt; "
                                + "wegen voller kategorie nudeln");
                        liste.get(zeiger).anzahlAnzeigen--;
                    }
                }
                else if(liste.get(zeiger).name.contains("kartoffel")
                        || liste.get(zeiger).name.contains("Kartoffel")){
                    kategorien[1]++;
                    schreiben(liste.get(zeiger).name+" füllt kategorie kartoffeln auf stand "+kategorien[1]);
                    if(kategorien[1]>2){
                        anzeigen=false;
                        schreiben(liste.get(zeiger).name+" nicht angezeigt; "
                                + "wegen voller kategorie kartoffeln");
                        liste.get(zeiger).anzahlAnzeigen--;
                    }
                }
                else if(liste.get(zeiger).name.contains("reis")
                        || liste.get(zeiger).name.contains("Reis")){
                    kategorien[2]++;
                    schreiben(liste.get(zeiger).name+" füllt kategorie reis auf stand "+kategorien[2]);
                    if(kategorien[2]>2){
                        anzeigen=false;
                        schreiben(liste.get(zeiger).name+" nicht angezeigt; "
                                + "wegen voller kategorie reis");
                        liste.get(zeiger).anzahlAnzeigen--;
                    }
                }
                
                if(anzeigen){
                    liste.get(zeiger).setBounds(0, i*40+160, fensterBreite, 40);

                    fenster.add(liste.get(zeiger));
                    i++;
                }
                
                zeiger++;
                if(zeiger==liste.size()){
                    break;
                }
            }
        }
        
        JLabel todo=new JLabel("    Coming soon:");
        todo.setBackground(Color.orange);
        todo.setOpaque(true);
        todo.setBounds(0, 320, fensterBreite, 50);
        fenster.add(todo);
        
        EssensVorschlag.fenster.revalidate();
        EssensVorschlag.fenster.repaint();
    }
    
    static void suchen(String zuSuchenIn){
        /*
        for(int i=0;i<4;i++){
            liste.get(i).anzahlAnzeigen++;
        }
        */
        
        suchTextMarkieren=false;
        zuSuchen=zuSuchenIn;
        for(int i=0;i<liste.size();i++){
            liste.get(i).suchAbstand=wortAbstand(zuSuchen,liste.get(i).name);
            liste.get(i).suchAbstand=liste.get(i).suchAbstand+liste.get(i).anzahlAnzeigen*3;
            liste.get(i).setVergleicher(new Vergleicher(){
                @Override
                int vergleichen(Gericht a, Gericht b) {
                    return a.suchAbstand-b.suchAbstand;
                }
            });
        }
        build();
    }
    
    static boolean hatNurHashTag(Gericht g){
        for(int i=0;i<EssensVorschlag.aktiveHashTagsNur.size();i++){
            if(g.hashtags.contains(aktiveHashTagsNur.get(i))) return true;
        }
        return false;
    }
    
    static boolean hatOhneHashTag(Gericht g){
        for(int i=0;i<EssensVorschlag.aktiveHashTagsOhne.size();i++){
            if(g.hashtags.contains(aktiveHashTagsOhne.get(i))) return true;
        }
        return false;
    }
    
    static Color getColor(String e){
        String[] t=e.split(",");
        return new Color(Integer.parseInt(t[0]),
                Integer.parseInt(t[1]),
                Integer.parseInt(t[2]));
    }
    
    static int wortAbstand(String aIn, String bIn){
        String a=aIn.toLowerCase();
        String b=bIn.toLowerCase();
        if(b.startsWith(a)){return 0;}
        else if(b.contains(a)){return 1;}
        else{return 2;}
    }
    
    static void schreiben(String text){
        System.out.println(text);
        textArea.append(text+"\n");
    }
    
    static String schreiben(Exception e){
        StringWriter sw= new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        
        String m=sw.toString();
        
        schreiben(m);
        return m;
    }
    
    static void tagebuchEintragen(String name){
        FileWriter fw=null;
        try{
            fw = new FileWriter(TagebuchFilename,true); //the true will append the new data
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy MMM dd HH:mm EEE");
            fw.write(df.format(d)+" : \t"+name+System.getProperty("line.separator"));//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            schreiben(ioe.getMessage());
            System.err.println("IOException: " + ioe.getMessage());
        }
        finally{
            try {
                fw.close();
            } catch (IOException ex) {
                EssensVorschlag.schreiben(ex.getMessage());
                Logger.getLogger(Gericht.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    static void gerichtBearbeitenMenueAnzeigen(Gericht gericht){
schreiben("(gerichtBearbeitenMenueAnzeigen)");

    }
    
    static String rezeptLaden(String name){
        FileReader fr;
        BufferedReader br;
        
        try {
            fr=new FileReader(rezeptOrdnerPfad+name+".txt");
        } catch (FileNotFoundException ex) {
            return "es gibt noch kein rezept";
        }
        br=new BufferedReader(fr);
        String ausgabe="";
        try {
            String zeile=br.readLine();
            while(zeile!=null){
                ausgabe=ausgabe.concat(zeile+"\n");
                zeile=br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            schreiben(ex);
            throw new RuntimeException("check Log");
        }
        return ausgabe;
    }
    
    static void rezeptSpeichern(String name, String rezeptEingabe){
        FileWriter fw;
        BufferedWriter bw;
        
        try {
            fw=new FileWriter(rezeptOrdnerPfad+name+".txt");
            bw=new BufferedWriter(fw);
            bw.write(rezeptEingabe);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            schreiben(ex);
        }
    }
    
    static void checkRezepteOrdner(){
        File dir=new File(rezeptOrdnerPfad);
        boolean existiert=dir.exists();
        schreiben("rezepte Ordner vorhanden? "+existiert);
        if(existiert)return;
        boolean isErstellt=dir.mkdir();
        schreiben("rezepte Ordner erstellt? "+isErstellt);
    }
    
    static void anregungenSpeichern(String eingabe){
        FileWriter fw;
        BufferedWriter bw;
        
        try {
            fw=new FileWriter(anregungenFilename);
            bw=new BufferedWriter(fw);
            bw.write(eingabe);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            schreiben(ex);
        }
    }
    
    static String notizenLaden(){
        FileReader fr;
        BufferedReader br;
        
        try {
            fr=new FileReader(notizenFilePfad);
        } catch (FileNotFoundException ex) {
            return "hier notizen einfuegen";
        }
        br=new BufferedReader(fr);
        String ausgabe="";
        try {
            String zeile=br.readLine();
            while(zeile!=null){
                ausgabe=ausgabe.concat(zeile+"\n");
                zeile=br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            schreiben(ex);
            throw new RuntimeException("check Log");
        }
        return ausgabe;
    }
    
    static void notizenSpeichern(String text){
        FileWriter fw;
        BufferedWriter bw;
        
        try {
            fw=new FileWriter(notizenFilePfad);
            bw=new BufferedWriter(fw);
            bw.write(text);
            bw.close();
        } catch (IOException ex) {
            schreiben(ex);
        }
    }
}