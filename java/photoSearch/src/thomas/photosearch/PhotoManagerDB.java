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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String periodMax = "";
    public String periodMin;
    public String currentPeriod = "";
    private int records = 0;
    private int maxRecords = 2;

    public int getMaxRecord() {
        return maxRecords;
    }

    public void setMaxRecord(int value) {
        this.maxRecords = value;
    }

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

        try {
            //Requete d'insertion dans la table catalog
            //Recupere les infos contenues dans di[0](->key) et di[1](->values)
            String sqlCatalog = "INSERT INTO catalog(" + di.getAllKeyValueForCatalog()[0] + ") VALUES (" + di.getAllKeyValueForCatalog()[1] + ")";

            //Préparation de la requete et ajout d'un statement afin de récuperer l'id de la derniere ligne inserée
            PreparedStatement psInsertCatalog = con.prepareStatement(sqlCatalog, Statement.RETURN_GENERATED_KEYS);
            //Execution de la requete
            psInsertCatalog.executeUpdate();
            //Recuperation de l'id de la derniere ligne inserée dans rsCatalog
            ResultSet rsCatalog = psInsertCatalog.getGeneratedKeys();
            if (rsCatalog.next()) {
                idCatalog = rsCatalog.getInt(1);
            }
            currentPeriod = di.directory.get("date");
            //System.out.println("LAST INSERTED ID CATALOG >>>>>" + idCatalog);
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
                    Statement psSelectKeyword = con.createStatement();
                    ResultSet rsSelect = psSelectKeyword.executeQuery(sqlKeywordsSelect);
                    rsSelect.last();
                    size = rsSelect.getRow();
                    rsSelect.beforeFirst();
                    //Si size = 0 cela indique que le mot clé que l'on essaie d'inserer n'existe pas en bdd
                    if (size == 0) {
                        //Requete d'insertion dans la table keyword
                        String sqlKeywordsInsert = "INSERT INTO keywords(keyword) VALUES ('" + splitKeywords[i] + "')";
                        //System.out.println("KEYWORD>>>>> " + splitKeywords[i]);
                        //Préparation de la requete et ajout d'un statement afin de récuperer l'id de la derniere ligne inserée
                        PreparedStatement psInsertKeywords = con.prepareStatement(sqlKeywordsInsert, Statement.RETURN_GENERATED_KEYS);
                        psInsertKeywords.executeUpdate();
                        //Recuperation de l'id de la derniere ligne inserée dans rsKeyword
//                        ResultSet rsKeyword = psInsertKeywords.getGeneratedKeys();
//                        if (rsKeyword.next()) {
//                            idKeyword = rsKeyword.getInt(1);
//                        }
                    }
                    //Requete d'insertion dans la table keyword
                    //Elle recupere le dernier id inseré dans la table catalog et l'associe aux id des keyword correspondant
                    String sqlLinkInsert = "INSERT INTO link_keywords(id_catalog, id_key) VALUES ('" + idCatalog + "', (SELECT keywords.id FROM keywords WHERE keyword = '" + splitKeywords[i] + "'))";
                    PreparedStatement psInsertLink = con.prepareStatement(sqlLinkInsert);
                    psInsertLink.execute();
                }
            }
            //System.out.println("LAST INSERTED ID KEYWORDS >>>>>" + idKeyword);
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

    public void periodManager() throws ParseException {
        int currentRecords = 0;
        try {
            String sqlPeriods = "select DATE_FORMAT(min(date),'%Y%m%d%H%i') from catalog group by DATE_FORMAT(date,'%Y%m%d%H%i') order by DATE_FORMAT(date,'%Y%m%d%H%i')";
            PreparedStatement psSelectPeriod = con.prepareStatement(sqlPeriods);
            psSelectPeriod.executeQuery();
            ResultSet rsPeriods = psSelectPeriod.getResultSet();
            rsPeriods.last();
            currentRecords = rsPeriods.getRow();
            String resultPeriodMin = "select min(date) from catalog order by date";
            PreparedStatement psSelectPeriodMin = con.prepareStatement(resultPeriodMin);
            psSelectPeriodMin.executeQuery();
            ResultSet rsPeriodMin = psSelectPeriodMin.getResultSet();
            rsPeriodMin.first();
            periodMin = rsPeriodMin.getString(1);
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date result = dateFormatter.parse(periodMin);
            System.out.println("PERIODMIN >>>> " + periodMin);
        } catch (SQLException e) {
            System.err.println(e);
        }
        System.out.println("CURRENTRECORDS >>>> "+currentRecords);
        if (currentRecords >= maxRecords) {
            try {
                String sqlDeleteLink = "DELETE FROM link_keywords WHERE id_catalog = ANY (SELECT id FROM catalog WHERE catalog.date = '"+periodMin+"')";
                PreparedStatement psDeleteLink = con.prepareStatement(sqlDeleteLink);
                psDeleteLink.execute();
                String sqlDeletePeriodMin = "DELETE FROM catalog WHERE date = '" + periodMin + "'";
                PreparedStatement psDeletePeriodMin = con.prepareStatement(sqlDeletePeriodMin);
                psDeletePeriodMin.execute();

                
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}
