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
import java.util.ArrayList;
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
        Statement stmt = con.createStatement();
        int idCatalog = -1;
        int idKeyword = -1;

        try {

            String sqlCatalog = "INSERT INTO catalog(" + di.getAllKeyValueForCatalog()[0] + ") VALUES (" + di.getAllKeyValueForCatalog()[1] + ")";
           
            PreparedStatement ps1 = con.prepareStatement(sqlCatalog, Statement.RETURN_GENERATED_KEYS);
            ps1.executeUpdate();
            ResultSet rsCatalog = ps1.getGeneratedKeys();   
            if (rsCatalog.next()){
                idCatalog = rsCatalog.getInt(1);
            }
            System.out.println("LAST INSERTED ID CATALOG >>>>>"+idCatalog);
            
            String sqlKeywords = "INSERT INTO keywords(keyword) VALUES('" + kl.getAllKeywords()+"')";
            PreparedStatement ps2 = con.prepareStatement(sqlKeywords, Statement.RETURN_GENERATED_KEYS);
            ps2.executeUpdate();
            ResultSet rsKeyword = ps2.getGeneratedKeys();
            if(rsKeyword.next()){
                idKeyword = rsKeyword.getInt(1);
            }
            System.out.println("LAST INSERTED ID KEYWORDS >>>>>"+idKeyword);
            
            String sqlLink = "INSERT INTO link_keywords(id_key, id_catalog) (SELECT catalog.id, keywords.id FROM catalog, keywords WHERE catalog.id = '"+idCatalog+"' AND keywords.id = '"+idKeyword+"')";
            PreparedStatement ps3 = con.prepareStatement(sqlLink);
            ps3.executeUpdate();
            
        } catch (Exception e) {
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
            if (p.getProperty(PROP_PASSWORD) != null) {
                password = p.getProperty(PROP_PASSWORD);
            }
            if (p.getProperty(PROP_DATABASE) != null) {
                database = p.getProperty(PROP_DATABASE);
            }
            if (p.getProperty(PROP_HOST) != null) {
                host = p.getProperty(PROP_HOST);
            }
            if (p.getProperty(PROP_PORT) != null) {
                port = p.getProperty(PROP_PORT);
            } else {
                port = DEFAULT_PORT;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
