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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gere les methodes relatives a la base de données PhotoCatalog. Permet la
 * connexion via la lecture d'un fichier config_file Permet d'inserer les
 * données de DirectoryInfo en bdd Permet de gerer des periodes (ici deux)
 * d'insertion, afin d'effacer les insertions les plus anciennes.
 *
 * @author thomas
 * @author eddy
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

    //Period properties
    public String periodMax = "";
    public String periodMin;
    public String currentPeriod = "";
    private int maxRecords = 2;

    /**
     * getter maxRecords
     *
     * @return maxRecords Le nombre de periodes maximum que l'on veut inscrire
     * en bdd
     */
    public int getMaxRecord() {
        return maxRecords;
    }

    /**
     * Setter maxRecords
     *
     * @param value Le nombre de periodes maximum que l'on veut inscrire en bdd
     */
    public void setMaxRecord(int value) {
        this.maxRecords = value;
    }

    /**
     * Setter configFile
     *
     * @param configFile
     */
    public PhotoManagerDB(String configFile) {
        this.configFile = configFile;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Methode de connection a la bdd PhotoCatalog
     */
    public void connect() {
        readConfigFile();
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
        } catch (SQLException ex) {
            System.err.println("Erreur de connection base de données");
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Methodes de fermeture de connection a la bdd
     */
    public void close() {
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PhotoManagerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Methode d'insertion en bdd Elle permet de remplir les differents champs
     * des tables catalog et keywords à partir des données de DirectoryInfo. La
     * table link_keywords est rempli a partir de l'id de la derniere entrée
     * dans catalog et des keywords qui lui sont associés.
     *
     * @param di Instance de l'objet DirectoryInfo, contenant les données de
     * directory et resume
     * @param kl Instance de l'objet DirectoryInfo, contenant les keywords.
     * @throws SQLException
     */
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
            //Récuperation de la date et heure d'éxecution du script
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

    /**
     * Methode de lecture du fichier configFile Ici, l'objet FileReader nous
     * permet de lier les differentes proprietés contenue dans configFile et les
     * lier aux attributs du constructeur de PhotoManagerDB
     */
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

    /**
     * Methode de gestion des periodes. Elle permet de tester combien de date
     * d'execution du script sont inscrites en bdd Si le nombre de dates est
     * superieur au nombre maximum de periodes autorisé, la date la plus
     * ancienne est supprimée
     */
    public void periodManager() {
        //Initialisation de la variable currentRecords, permet de récuperer le nombre de date differentes inscrites en bdd
        int currentRecords = 0;
        try {
            //Requete de selection des dates dans la table catalog
            String sqlPeriods = "select min(date) from catalog group by date order by date";
            //Préparation de la requete
            PreparedStatement psSelectPeriod = con.prepareStatement(sqlPeriods);
            //Execution de la requete
            psSelectPeriod.executeQuery();
            //Recuperation des données de la requete dans un resultSet rsPeriods
            ResultSet rsPeriods = psSelectPeriod.getResultSet();
            //Pointage sur la premiere entrée de resultSet
            rsPeriods.first();
            //Recuperation du contenu de la requete
            periodMin = rsPeriods.getString(1);
            //Pointage sur la derniere entrée de la requete
            rsPeriods.last();
            //Recuperation du nombre de ligne de rsPeriods, celà nous indique le nombre de dates
            //differentes inscrites en bdd
            currentRecords = rsPeriods.getRow();
        } catch (SQLException e) {
            System.err.println(e);
        }
        //Test si currentRecords >= maxRecords alors on supprime toutes les entrées des tables catalog et link_keywords
        //correspondant à la date la plus ancienne
        if (currentRecords >= maxRecords) {
            try {
                //Preparation de la requete delete link_keywords
                //On supprime de link_keywords toutes les entrées à l'id de catalog où l'id de catalog correspond à la date la plus ancienne
                String sqlDeleteLink = "DELETE FROM link_keywords WHERE id_catalog = ANY (SELECT id FROM catalog WHERE catalog.date = '" + periodMin + "')";
                //Préparation de la requete
                PreparedStatement psDeleteLink = con.prepareStatement(sqlDeleteLink);
                //Execution de la requete
                psDeleteLink.execute();
                
                //Preparation de la requete delete catalog
                //On supprime de catalog toutes les entrées correspondant à la date la plus ancienne
                String sqlDeletePeriodMin = "DELETE FROM catalog WHERE date = '" + periodMin + "'";
                 //Préparation de la requete
                PreparedStatement psDeletePeriodMin = con.prepareStatement(sqlDeletePeriodMin);
                 //Execution de la requete
                psDeletePeriodMin.execute();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}
