/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essensvorschlag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *
 * @author duke
 */
public class DataHandler {
    
    public static String loadLocalTextFile(String path) 
            throws FileNotFoundException, IOException
    {
        return loadTextFile(EssensVorschlag.dataDirectoryPath+path);
    }
    
    public static String loadTextFile(String path) 
            throws FileNotFoundException, IOException
    {
        String ausgabe;
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path),"UTF8"))) {
            String zeile=br.readLine();
            ausgabe = "";
            if(zeile!=null){
                ausgabe=zeile;
                zeile=br.readLine();
                while(zeile!=null){
                    ausgabe=ausgabe+"\n"+zeile;
                    zeile=br.readLine();
                }
            }
        }
        
        return ausgabe;
    }
    
    public static void saveLocalTextFile(ArrayList<String> lines, String path) 
            throws IOException
    {
        saveTextFile(lines,EssensVorschlag.dataDirectoryPath+path);
    }
    
    public static void saveTextFile(ArrayList<String> lines, String path) 
            throws IOException
    {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), "UTF8"))) 
        {
            if(lines.size()>0){
                bw.write(lines.get(0));
            }
            for(int i=1;i<lines.size();i++){
                bw.newLine();
                bw.write(lines.get(i));
            }
        }
    }
    
    public static void appendOneLineLocalTextFile(String line, String path) 
            throws IOException
    {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(EssensVorschlag.dataDirectoryPath+path, true), // true to append
                    "UTF8"                 // Set encoding
                    )
                )
            ) 
        {
            bw.write(line);
        }
    }
}
