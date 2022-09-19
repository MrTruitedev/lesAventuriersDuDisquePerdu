/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import java.sql.SQLException;
import java.text.ParseException;

/**
 *PhotoSearch est notre classe de base nous permettant de gerer toutes nos autres classes.
 * C'est ce qui nous permet d'executer nos methodes dans l'ordre afin de remplir notre base de données
 * PhotoCatalog avec les données contenue dans les fichiers xml retournés par le script gettrees.
 * Ce script nous renvoie une arborescence du serveur contenant les informations de chaque dossier.
 * @author thomas
 * @autor eddy
 */
public class PhotoSearch {

    //CONFIG_FILE : Prévoir cette constante dans un fichier de config
    //final static public String CONFIG_FILE = "/etc/gettrees/gettrees_config";
    final static public String CONFIG_FILE = "D:\\user\\tutu\\Desktop\\Stage\\lesAventuriersDuDisquePerdu\\java\\photoSearch\\gettrees_config";

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws SQLException, ParseException {
        if (args.length != 2) {
            System.err.println("Give 2 XML files (WORK and STOCK)");
            System.exit(1);
        }
        PhotoSearch ps = new PhotoSearch(args[0], args[1]);
    }
    /**
     * Nous permet de lancer les methodes associées à nos objets.
     * PhotoManagerDB est l'objet associé a notre base de données. Il nous permet la connection à la base,
     * la gestion des dates d'execution du programme, la suppression et l'insertion des données.
     * XMLTreeReader nous permet d'analyser et de parser les données contenues dans nos fichiers xml.
     * DirectoryInfo nous permet de classer les données retournées afin de les inserer en BDD.
     * 
     * @param xmlFileStock           Le fichier xml retourné par gettrees provenant du dossier stock
     * @param xmlFileWork            Le fichier xml retourné par gettres provenant du dossier work
     * @throws SQLException
     * @throws ParseException 
     */
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
