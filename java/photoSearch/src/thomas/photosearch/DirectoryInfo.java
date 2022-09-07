/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author thomas
 */
public class DirectoryInfo {

    public HashMap<String, String> directory = new HashMap();
    private HashMap<String, String> resume = new HashMap();
    private ArrayList<String> keywordsList = new ArrayList();
    private int containerType;
    public final static String TYPE_WORK = "WORK";
    public final static String TYPE_STOCK = "STOCK";
    public final static String TYPE_UNKNOW = "???";

    public HashMap<String, String> getDirectory() {
        return directory;
    }

    public String[] getAllKeyValueForCatalog() {
        String sqlInsertKey = "";
        String directoryKeys = "";
        String resumeKeys = "";
        String[] str_nv = new String[2];
        String namestr = "";
        String valuestr = "";
        for (Map.Entry<String, String> entry : directory.entrySet()) {
            namestr += entry.getKey() + ",";
            valuestr += "'" + entry.getValue() + "',";
        }
         for (Map.Entry<String, String> entry : resume.entrySet()) {
            namestr += entry.getKey() + ",";
            valuestr += "'" + entry.getValue() + "',";
        }
        str_nv[0] = namestr.substring(0, namestr.length() - 1);
        str_nv[1] = valuestr.substring(0, valuestr.length() - 1);

        System.out.println(">> " + str_nv[0] + ">> " + str_nv[1]);

        return str_nv;
//        for(String i : directory.keySet()){
//           directoryKeys += i+", ";
//        }
//        for(String j : resume .keySet()){
//            resumeKeys += j+", ";
//        }
//        sqlInsertKey = directoryKeys+resumeKeys;
//         return sqlInsertKey;
//  
    }

//    public String getAllValueForCatalog() {
//        String sqlInsertValue = "";
//        String directoryValue = "";
//        String resumeValue = "";
//        for (String strd : directory.values()) {
//            directoryValue += strd + ", ";
//        }
//        for (String strr : resume.values()) {
//            resumeValue += strr + ", ";
//        }
//        sqlInsertValue = directoryValue + resumeValue;
//        return sqlInsertValue;
//    }

    public HashMap<String, String> getResume() {
        return resume;
    }

    public ArrayList<String> getKeyword() {
        return keywordsList;
    }

    public int addKeywords(String keywords) {
        // tokenizer découpage string
        StringTokenizer keys = new StringTokenizer(keywords, " ");
        while (keys.hasMoreElements()) {
            keywordsList.add(((String) keys.nextElement()).trim().toUpperCase());
        }
        return keywordsList.size();
    }

    public String addResumeAttr(String name, String value) {
        return resume.put(name, value);
    }

    public String addDirectoryAttr(String name, String value) {
        return directory.put(name, value);
    }

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
