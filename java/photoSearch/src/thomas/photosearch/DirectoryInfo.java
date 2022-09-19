/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *DirectoryInfo est la classe nous permettant de gerer les données reçues par XMLTreeReader.
 * Les données de directory et resume sont ajoutés dans des hashmap.
 * Les données keyword sont ajoutées dans un arraylist.
 * Enfin, nous permet de définir le type (work ou stock) des données reçues.
 * @author thomas
 * @author eddy
 */
public class DirectoryInfo {

    public HashMap<String, String> directory = new HashMap();
    private HashMap<String, String> resume = new HashMap();
    private ArrayList<String> keywordsList = new ArrayList();
    public final static String TYPE_WORK = "WORK";
    public final static String TYPE_STOCK = "STOCK";
    public final static String TYPE_UNKNOW = "???";
    private int containerType;
    
/**
 * Directory getter
 * @return directory        Hashmap  contenant les données de directory
 */
    
    public HashMap<String, String> getDirectory() {
        return directory;
    }
    
/**
 * Permet de rassembler les couples key/value contenues dans les hashmap directory et resume.
 * Les données sont concaténées dans un tableau de string str_nv[] : les keys à l'index [0] et les values
 * à l'index [1].
 * @return str_nv       Pour String Name Value, tableau de string 
 */
    
    public String[] getAllKeyValueForCatalog() {
        //Intialisation de la variable str_nv, tableau de string contenant les couples key/value des hashmap 
        //directoryInfo
        String[] str_nv = new String[2];
        //Initialisation de la variable namestr, contenant les key sous forme de string
        String namestr = "";
        //initialisation de la variable valuestr, contenant les value sous forme de string
        String valuestr = "";
        //Boucle parcourt le hashmap directory
        for (Map.Entry<String, String> entry : directory.entrySet()) {
            //Recupere les key et les concatene
            namestr += entry.getKey() + ",";
            //Recupere les value et les concatene
            valuestr += "'" + entry.getValue().replace("'", "") + "',";
        }
        //Boucle parcourt le hashmap resume
        for (Map.Entry<String, String> entry : resume.entrySet()) {
            //Recupere les key et les concatene
            namestr += entry.getKey() + ",";
            //Recupere les value et les concatene
            valuestr += "'" + entry.getValue().replace("'", "") + "',";
        }
        //Recupere et concatene les resulats des boucles en un tableau de string.
        str_nv[0] = namestr.substring(0, namestr.length() - 1);
        str_nv[1] = valuestr.substring(0, valuestr.length() - 1);

        //System.out.println(">> " + str_nv[0] + ">> " + str_nv[1]);
        return str_nv;
    }
    
/**
 * Permet de recuperer les keywords contenue dans l'ArrayList keywords et les concaténer en un string.
 * Chaque keyword est séparé par un espace.
 * @return kw          String de keywords.
 */
    
    public String getAllKeywords() {
        String kw = "";
        Enumeration enumeration = Collections.enumeration(keywordsList);
        while (enumeration.hasMoreElements()) {
            kw += (String) enumeration.nextElement() + " ";
        }
        return kw;
    }
    
/**
 * getter de resume
 * @return resume       Hashmap contenant les données de resume.
 */
    
    public HashMap<String, String> getResume() {
        return resume;
    }
    
/**
 * getter de keyword
 * @return keywordsList         ArrayList contenant les keywords
 */
    
    public ArrayList<String> getKeyword() {
        return keywordsList;
    }
    
/**
 * Permet le découpage et l'ajout des keywords dans l'ArrayList keywordsList.
 * XMLTreeReader nous retourne un string où les keywords sont séparés par des espaces, 
 * StringTokeniser nous permet de découper cette string afin d'ajouter chaque keywords séparement dans
 * keywordsList
 * @param keywords          Les keywords retournés par XMLTreeReader
 * @return  keywordsList.size()         Retourne la taille de l'ArrayList keywordsList
 */
    
    public int addKeywords(String keywords) {
        // tokenizer découpage string
        StringTokenizer keys = new StringTokenizer(keywords, " ");
        while (keys.hasMoreElements()) {
            keywordsList.add(((String) keys.nextElement()).trim().toUpperCase());
        }
        return keywordsList.size();
    }
    
/**
 * Nous permet d'ajouter les couples key/value contenus dans les champs resume
 * retournés par XMLTreeReader dans le hashmap resume
 * @param name      Le nom de la key
 * @param value     La valeur associée
 * @return resume.put(name, value)
 */
    
    public String addResumeAttr(String name, String value) {
        return resume.put(name, value);
    }
    
/**
 *  Nous permet d'ajouter les couples key/value contenus dans les champs directory
 * retournés par XLTreeReader dans le hashmap directory
 * @param name          Le nom de la key
 * @param value         La valeur associée
 * @return directory.put(name, value)
 */
    
    public String addDirectoryAttr(String name, String value) {
        return directory.put(name, value);
    }
    
/**
 * Nous permet de définir le type (work ou stock) des données reçues.
 * @param containerType         Le type de données reçues
 */
    
    void setContainerType(int containerType) {
        switch (containerType) {
            case XMLTreeReader.CONTAINER_TYPE_WORK:
                this.containerType = 1;
                break;
            case XMLTreeReader.CONTAINER_TYPE_STOCK:
                this.containerType = 2;
                break;
            default:
                this.containerType = 0;
        }
    }
}
