/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import java.sql.SQLException;
import java.text.ParseException;

/**
 *
 * @author thomas
 */
public class PhotoSearch {

    //CONFIG_FILE : Prévoir cette constante dans un fichier de config
    //final static public String CONFIG_FILE = "/etc/gettrees/gettrees_config";
    final static public String CONFIG_FILE = "D:\\user\\tutu\\Desktop\\Stage\\lesAventuriersDuDisquePerdu\\java\\photoSearch\\gettrees_config";

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException, ParseException {
        if (args.length != 2) {
            System.err.println("Give 2 XML files (WORK and STOCK) AND A TIME PERIOD");
            System.exit(1);
        }
        PhotoSearch ps = new PhotoSearch(args[0], args[1]);
    }

    public PhotoSearch(String xmlFileStock, String xmlFileWork) throws SQLException, ParseException {
        PhotoManagerDB pm = new PhotoManagerDB(CONFIG_FILE);
        pm.connect();
        pm.periodManager();
        
        //Analyse STOCK file       
        XMLTreeReader tr_s = new XMLTreeReader(xmlFileStock);
        
        for(int i=0 ; i<tr_s.getLength(); i++){
            DirectoryInfo di = tr_s.getLine(i);
            DirectoryInfo kl = tr_s.getLine(i);
            //System.out.println(di.getDirectory().get("path"));
           
            pm.insert(di, kl);
            
        }
        
          //Analyse WORK file       
        XMLTreeReader tr_w = new XMLTreeReader(xmlFileWork);
        for(int i=0 ; i<tr_w.getLength(); i++){
            DirectoryInfo di = tr_w.getLine(i);
            DirectoryInfo kl = tr_s.getLine(i);
            //....
            //pm.periodManager(di);
            pm.insert(di, kl);            
        }

        
    }

}
