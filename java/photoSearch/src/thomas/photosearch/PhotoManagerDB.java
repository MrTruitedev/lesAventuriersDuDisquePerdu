/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thomas
 */
public class PhotoManagerDB {

    //config properties 
    public final static String DEFAULT_PORT = "3306";
    public final static String PROP_HOST = "DB_SERVER";
    public final static String PROP_DATABASE = "DB_NAME";
    public final static String PROP_USER = "DB_USER";
    public final static String PROP_PASSWORD = "DB_PASSWORD";
    public final static String PROP_PORT = "DB_PORT";

    String host;
    String database;
    String user;
    String password;
    String port;
    String configFile;

    private Connection con = null;

    public PhotoManagerDB(String configFile) {
        this.configFile = configFile;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {

            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        readConfigFile();
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
        } catch (SQLException ex) {
            System.err.println("Erreur de connection base de données");
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void close() {
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void insert(DirectoryInfo di, DirectoryInfo kl) throws SQLException {
        // Initialisation des variables idCatalog et idKeyword utilisées pour remplir la table link_keywords
        int idCatalog = -1;
        int idKeyword = -1;

        try {
            //Requete d'insertion dans la table catalog
            //Recupere les infos contenues dans di[0](->key) et di[1](->values)
            String sqlCatalog = "INSERT INTO catalog(" + di.getAllKeyValueForCatalog()[0] + ") VALUES (" + di.getAllKeyValueForCatalog()[1] + ")" ;
            
            //Préparation de la requete et ajout d'un statement afin de récuperer l'id de la derniere ligne inserée
            PreparedStatement ps1 = con.prepareStatement(sqlCatalog, Statement.RETURN_GENERATED_KEYS);
            //Execution de la requete
            ps1.executeUpdate();
            //Recuperation de l'id de la derniere ligne inserée dans rsCatalog
            ResultSet rsCatalog = ps1.getGeneratedKeys();
            if (rsCatalog.next()) {
                idCatalog = rsCatalog.getInt(1);
            }
            System.out.println("LAST INSERTED ID CATALOG >>>>>" + idCatalog);
            //Initialisation de la variable splitKeywords
            //Recupere plusieurs chaines de caracteres puis la découpe suivant la regex " " afin de retourner un tableau contenant une chaine de caractere par ligne 
            String[] splitKeywords = kl.getAllKeywords().split(" ");
            //Boucle pour inserer les keywords dans la table keyword, un keyword par id
            for (int i = 0; i < splitKeywords.length; i++) {
                //Si splitKeywords ne contient pas de keyword on ne l'inscrit pas en bdd
                if (splitKeywords[i].length() != 0) {
                    //Initialisation de la variable size
                    //Elle permettra d'effectuer le test si le keyword est déja existant en bdd
                    int size;
                    //Recherche de l'id du mot clé que l'on voudrait inserer
                    String sqlKeywordsSelect = "SELECT id FROM `keywords` WHERE keyword = '" + splitKeywords[i] + "'";
                    Statement ps2 = con.createStatement();
                    ResultSet rsSelect = ps2.executeQuery(sqlKeywordsSelect);
                    rsSelect.last();
                    size = rsSelect.getRow();
                    rsSelect.beforeFirst();
                    //Si size = 0 cela indique que le mot clé que l'on essaie d'inserer n'existe pas en bdd
                    if (size == 0) {
                        //Requete d'insertion dans la table keyword
                        String sqlKeywordsInsert = "INSERT INTO keywords(keyword) VALUES ('" + splitKeywords[i] + "')";
                        System.out.println("KEYWORD>>>>> " + splitKeywords[i]);
                        //Préparation de la requete et ajout d'un statement afin de récuperer l'id de la derniere ligne inserée
                        PreparedStatement psKey = con.prepareStatement(sqlKeywordsInsert, Statement.RETURN_GENERATED_KEYS);
                        psKey.executeUpdate();
                          //Recuperation de l'id de la derniere ligne inserée dans rsKeyword
                        ResultSet rsKeyword = psKey.getGeneratedKeys();
                        if (rsKeyword.next()) {
                            idKeyword = rsKeyword.getInt(1);
                        }
                    }
                    //Requete d'insertion dans la table keyword
                    //Elle recupere le dernier id inseré dans la table catalog et l'associe aux id des keyword correspondant
                    String sqlLinkInsert = "INSERT INTO link_keywords(id_catalog, id_key) VALUES ('" + idCatalog + "', (SELECT keywords.id FROM keywords WHERE keyword = '" + splitKeywords[i] + "'))";
                    PreparedStatement psLink = con.prepareStatement(sqlLinkInsert);
                    psLink.execute();
                }
            }
            System.out.println("LAST INSERTED ID KEYWORDS >>>>>" + idKeyword);
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    private void readConfigFile() {
        try {
            FileReader reader = new FileReader(this.configFile);
            Properties p = new Properties();
            p.load(reader);

            //PROP USER
            if (p.getProperty(PROP_USER) != null) {
                user = p.getProperty(PROP_USER);
            }
            //PROP PASSWORD
            if (p.getProperty(PROP_PASSWORD) != null) {
                password = p.getProperty(PROP_PASSWORD);
            }
            //PROP DATABASE
            if (p.getProperty(PROP_DATABASE) != null) {
                database = p.getProperty(PROP_DATABASE);
            }
            //PROP HOST
            if (p.getProperty(PROP_HOST) != null) {
                host = p.getProperty(PROP_HOST);
            }
            //PROP PORT
            if (p.getProperty(PROP_PORT) != null) {
                port = p.getProperty(PROP_PORT);
            } else {
                port = DEFAULT_PORT;

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PhotoManagerDB.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(PhotoManagerDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}
