/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
--- atm:
linux adaption | Umlaut handling: bundled all reading and writing and switched to utf-8
Issue Discription: On Linux the umlauts are just displayed as strange Symbols. 
    It is not an issue of display. It seems the file(from another Os) 
    is not read in the right format. The standart charset java uses to read and 
    write txts is platform dependent. So we inforce utf-8 encoding everywhere.
    (And then update the old date manually as it is just one set) Curing this i try
    to handle all data reading/writing stuff on one location called DataHandler
todo:
    * (outside of programm) update (my) data
done:
    * Move datahandling to DataHandler:
        * move all FileReaders;
        * move all FileWriters;
    * [DataHandler] Convert to try-With-Resources : should take care off closing the streams
    * switched reading and writing to utf-8

General Done:
*/
package essensvorschlag;

import static essensvorschlag.EssensVorschlag.liste;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import static essensvorschlag.EssensVorschlag.writeToLogConsole;

/**
 *
 * @author 
 */
public class EssensVorschlag {
    static ArrayList<Gericht> liste=new ArrayList();
    static ArrayList<Gericht> listeZwischenSpeicher=new ArrayList();
    static JFrame fenster=new JFrame("");
    static String pathToMealListFile=null;
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
    
    static String dataDirectoryPath;    // the path to the directory which contains the jar. This directory is used to store all the data(including diary, rezepts, etc.) created by the Tool; allready ends with a slash, so file(directory) names just need to be appended names
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            dataDirectoryPath = new File(
                    EssensVorschlag.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
                    ).getParentFile().getAbsolutePath()
                    +"/";
            writeToLogConsole("dataDirectoryPath  (computed path to jar): "+dataDirectoryPath);
            
            getOption();
            
            loadAlleHashTagsListe();
            loadVorgemerkteGerichte();
        
            checkRezepteOrdner();
            
            setupWindow();
        
            loadMealListData();
            
            build();
        }
        catch(Exception e){
            String errorMessage=writeToLogConsole(e);
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
    
    private static void loadMealListData(){
        try {
            pathToMealListFile=DataHandler.loadLocalTextFile("pfad.txt");
        } catch (FileNotFoundException ex) {
            setPfad();
        } catch (IOException ex) {
            writeToLogConsole(ex);
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        writeToLogConsole("path to the Meal_List_File: "+pathToMealListFile);

        try {
            String[] zeilenArray=DataHandler.loadTextFile(pathToMealListFile).split("\n");
            for(int i=0;i<zeilenArray.length;i++){
                zeilen.add(zeilenArray[i]);
            }
        } catch (FileNotFoundException ex) {
            writeToLogConsole(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            
            JOptionPane.showMessageDialog(fenster,"The stored location of the Meal_List_File "
                    + " seems to be wrong as the file cant be found;\n you get the oppertunity to browse for your file");
            setPfad();
            loadMealListData();
            return;
        } catch (IOException ex) {
            writeToLogConsole(ex);
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
                    writeToLogConsole("(checkVersion) zeile "+i+" : "+zeilen.get(i));
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
            writeToLogConsole("updated Data to "+zeilen.get(0));
        }
    }
    
    private static void getOption(){
        try {
            String[] zeilenArray=DataHandler.loadLocalTextFile("optionFile.txt").split("\n");
            writeToLogConsole("(getOption) OptionFile: gefunden");
            
            String optionVersion=zeilenArray[0];
            writeToLogConsole("(getOption) version: "+optionVersion);
            optionen[0]=zeilenArray[1].substring(15);
            writeToLogConsole("(getOption) schriftgröße: "+optionen[0]);
            
            optionen[1]=zeilenArray[2].substring(16);
            optionen[2]=zeilenArray[3].substring(16);
            optionen[3]=zeilenArray[4].substring(28);
            optionen[4]=zeilenArray[5].substring(33);
        } catch (FileNotFoundException ex) {
            writeToLogConsole("(getOption) OptionFile: currently creating");
            try {
                ArrayList<String> lineList=new ArrayList();

                lineList.add("V 1");
                
                lineList.add("schriftgröße : 12");
                optionen[0]="12";
                
                lineList.add("Fenster Pos X : 0");
                optionen[1]="0";
                
                lineList.add("Fenster Pos Y : 0");
                optionen[2]="0";
                
                lineList.add("Standart Background Farbe : 255,255,255");
                optionen[3]="255,255,255";
                
                lineList.add("PlanungsModus Background Farbe : 219,29,4");
                optionen[4]="219,29,4";
                
                writeToLogConsole("(getOption) finished writing");
                
                DataHandler.saveLocalTextFile(lineList, "optionFile.txt");
                
            } catch (IOException ex1) {
                writeToLogConsole("(getOption) OptionFile: Creation didnt work; aborted.");
                writeToLogConsole(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (IOException ex1) {
            writeToLogConsole(ex1.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    private static void loadAlleHashTagsListe(){
        try {
            String[] zeilenArray=DataHandler.loadLocalTextFile("hashTagFile.txt").split("\n");
            
            writeToLogConsole("(loadAlleHashTagsListe) hashTagFile: gefunden.");
            
            ArrayList<String> zeilen=new ArrayList();
            for(int i=0;i<zeilenArray.length;i++){
                    zeilen.add(zeilenArray[i]);
            }

            checkHashTagsListeVersion(zeilen);

            for(int i=0;i<zeilen.size();i++){
writeToLogConsole("(loadAlleHashTagsListe) "+zeilen.get(i));
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
            
        } catch (FileNotFoundException ex) {
            writeToLogConsole("(loadAlleHashTagsListe) hashTagFile: wurde erstellt.");
            try {
                ArrayList<String> linesList=new ArrayList();
                
                linesList.add("Version: 1");
                
                DataHandler.saveLocalTextFile(linesList, "hashTagFile.txt");
            } catch (IOException ex1) {
                writeToLogConsole(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return; //dadurch das hier abgebrochen wird, wird nur ein file mit "Version 1" und ansonsten nix erstellt. 
                    //Da das file leer ist passiert kein fehler beim einlesen(da ja nix eingelesen wird). 
                    //beim speichern wird dann automatisch alles in der richtigen version gespeichert
        } catch (IOException ex) {
            writeToLogConsole(ex);
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
            writeToLogConsole("HashTagsListe geupdated auf "+zeilen.get(0));
        }
    }
    
    private static void loadVorgemerkteGerichte(){
        try {
            String[] zeilenArray=DataHandler.loadLocalTextFile("VorgemerkteGerichteFile.txt").split("\n");
            
            writeToLogConsole("(loadVorgemerkteGerichte) VorgemerkteGerichteFile: gefunden.");
        
            String zeile=zeilenArray[0];
            if(!zeile.equals("Version: 1")){
                throw new RuntimeException("Fehler bei der VorgemerkteGerichteFile Version");
            }
            for(int j=1;j<zeilenArray.length;j++){
//schreiben("(loadAlleHashTagsListe) "+zeile);
                //if the remembered Meal is one of the list its gericht is used. If not a dummy gericht with the right name is created and used
                zeile=zeilenArray[j];
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
            }
        } catch (FileNotFoundException ex) {
            writeToLogConsole("(loadVorgemerkteGerichte) VorgemerkteGerichteFile: wurde erstellt.");
            try {
                ArrayList<String> linesList=new ArrayList();
                
                linesList.add("Version: 1");
                
                DataHandler.saveLocalTextFile(linesList, "VorgemerkteGerichteFile.txt");
            } catch (IOException ex1) {
                writeToLogConsole(ex.getMessage());
                Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (IOException ex1) {
            writeToLogConsole(ex1.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    static void speichern(){
        try {
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add(zeilen.get(0));
            
            for(int i=0;i<liste.size();i++){
                Gericht g=liste.get(i);
                String content=g.name+"#"+g.stand+"#"+g.wachstum+"#"+g.letztesEssen;
                //hashtags
                content=content+"#";
                for(int j=0;j<g.hashtags.size();j++){
                    content=content+g.hashtags.get(j)+";";
                }
                
                linesList.add(content);
            }

            DataHandler.saveTextFile(linesList, pathToMealListFile);
            
writeToLogConsole("(gerichte) gespeichert");
        } catch (IOException e) {
            writeToLogConsole(e.getMessage());
        }
    }
    
    static void alleHashTagsListeSpeichern(){
        try{
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add("Version: 2");
            for(int i=0;i<EssensVorschlag.alleHashTags.size();i++){
                linesList.add(alleHashTags.get(i).hashtag+"#"+alleHashTags.get(i).dropDown.getSelectedItem());
            }

            DataHandler.saveLocalTextFile(linesList, "hashTagFile.txt");
        }
        catch(Exception ex){
            writeToLogConsole(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void speichernVorgemerkteGerichteFile(){
        try{
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add("Version: 1");
            for(int i=0;i<EssensVorschlag.vorgemerkteGerichte.size();i++){
                linesList.add(vorgemerkteGerichte.get(i).name);
            }

            DataHandler.saveLocalTextFile(linesList, "VorgemerkteGerichteFile.txt");
        }
        catch(Exception ex){
            writeToLogConsole(ex.getMessage());
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void setPfad(){
        writeToLogConsole("(setPfad) a path could be selected or was created");
        
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
        pathToMealListFile = fc.getSelectedFile().getAbsolutePath();
        writePfad();
    }
    
    static void writePfad(){
        try {
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add(pathToMealListFile);
            writeToLogConsole("(writePfad) path: "+pathToMealListFile);

            DataHandler.saveLocalTextFile(linesList, "pfad.txt");
            
        } catch (IOException e) {
            writeToLogConsole(e.getMessage());
        }
    }
    
    static void neuesGerichtFile(){
        String gerichtFilePfad="gerichte(auto).txt";
        try{
            //erstellt das neue File;
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add("dummy1#0#1");
            linesList.add("dummy2#0#1");
            linesList.add("dummy3#0#1");
            linesList.add("dummy4#0#1");
            linesList.add("dummy5#0#1");

            pathToMealListFile=EssensVorschlag.dataDirectoryPath+gerichtFilePfad;
            DataHandler.saveTextFile(linesList, pathToMealListFile);
            
            writeToLogConsole("(neuesGerichtFile) path: "+pathToMealListFile);
            
            writePfad();
        }catch(IOException ioex){
            writeToLogConsole(ioex);
        }
    }
    
    static void build(){
        Collections.sort(liste);
        if(liste.get(0).stand>10000){
            writeToLogConsole("warnung: stand größer 10000");
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
                    writeToLogConsole(liste.get(zeiger).name+" füllt kategorie nudeln auf stand "+kategorien[0]);
                    if(kategorien[0]>2){
                        anzeigen=false;
                        writeToLogConsole(liste.get(zeiger).name+" nicht angezeigt; "
                                + "wegen voller kategorie nudeln");
                        liste.get(zeiger).anzahlAnzeigen--;
                    }
                }
                else if(liste.get(zeiger).name.contains("kartoffel")
                        || liste.get(zeiger).name.contains("Kartoffel")){
                    kategorien[1]++;
                    writeToLogConsole(liste.get(zeiger).name+" füllt kategorie kartoffeln auf stand "+kategorien[1]);
                    if(kategorien[1]>2){
                        anzeigen=false;
                        writeToLogConsole(liste.get(zeiger).name+" nicht angezeigt; "
                                + "wegen voller kategorie kartoffeln");
                        liste.get(zeiger).anzahlAnzeigen--;
                    }
                }
                else if(liste.get(zeiger).name.contains("reis")
                        || liste.get(zeiger).name.contains("Reis")){
                    kategorien[2]++;
                    writeToLogConsole(liste.get(zeiger).name+" füllt kategorie reis auf stand "+kategorien[2]);
                    if(kategorien[2]>2){
                        anzeigen=false;
                        writeToLogConsole(liste.get(zeiger).name+" nicht angezeigt; "
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
    
    static void writeToLogConsole(String text){
        System.out.println(text);
        textArea.append(text+"\n");
    }
    
    static String writeToLogConsole(Exception e){
        StringWriter sw= new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        
        String m=sw.toString();
        
        writeToLogConsole(m);
        return m;
    }
    
    static void tagebuchEintragen(String name){
        try{
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy MMM dd HH:mm EEE");
            
            String line=df.format(d)+" : \t"+name+System.getProperty("line.separator");

            DataHandler.appendOneLineLocalTextFile(line, TagebuchFilename);
        }
        catch(IOException ioe)
        {
            writeToLogConsole(ioe.getMessage());
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
    
    static void gerichtBearbeitenMenueAnzeigen(Gericht gericht){
writeToLogConsole("(gerichtBearbeitenMenueAnzeigen)");

    }
    
    static String rezeptLaden(String name){
        try {
            return DataHandler.loadLocalTextFile(rezeptOrdnerPfad+name+".txt");
        } catch (FileNotFoundException ex) {
            return "es gibt noch kein rezept";
        } catch (IOException ex) {
            writeToLogConsole(ex);
            throw new RuntimeException("check Log");
        }
    }
    
    static void rezeptSpeichern(String name, String rezeptEingabe){
        try {
            ArrayList<String> linesList=new ArrayList();
            
            linesList.add(rezeptEingabe);
            
            DataHandler.saveLocalTextFile(linesList, rezeptOrdnerPfad+name+".txt");
            
        } catch (IOException ex) {
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            writeToLogConsole(ex);
        }
    }
    
    static void checkRezepteOrdner(){
        String path=dataDirectoryPath+rezeptOrdnerPfad;
        File dir=new File(path);
        boolean existiert=dir.exists();
        writeToLogConsole("rezepte Ordner vorhanden? "+existiert);
        writeToLogConsole("(estimated path: "+path+")");
        if(existiert)return;
        boolean isErstellt=dir.mkdir();
        writeToLogConsole("rezepte Ordner erstellt? "+isErstellt);
    }
    
    static void anregungenSpeichern(String eingabe){
        try {
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add(eingabe);

            DataHandler.saveLocalTextFile(linesList, anregungenFilename);            
        } catch (IOException ex) {
            Logger.getLogger(EssensVorschlag.class.getName()).log(Level.SEVERE, null, ex);
            writeToLogConsole(ex);
        }
    }
    
    static String notizenLaden(){
        try {
            return DataHandler.loadLocalTextFile(notizenFilePfad);
        } catch (FileNotFoundException ex) {
            return "hier notizen einfuegen";
        } catch (IOException ex) {
            writeToLogConsole(ex);
            throw new RuntimeException("check Log");
        }
    }
    
    static void notizenSpeichern(String text){
        try {
            ArrayList<String> linesList=new ArrayList();
                
            linesList.add(text);

            DataHandler.saveLocalTextFile(linesList, notizenFilePfad); 
            
        } catch (IOException ex) {
            writeToLogConsole(ex);
        }
    }
}