/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas;
import java.sql.*;

/**
 *
 * @author thomas
 */
public class Main {
    
    public static void main(String arg[]){
        
      System.out.println("coucou");
      System.out.println("nombre d'arguments ="+ arg.length);
      for (int i=0; i<arg.length; i++){
            System.out.println("arg("+i+")="+arg[i]);
        }
        Main m = new Main();
        m.start();
    }
    public void start(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://cyberlab:3306/PhotoCatalog","eddy","girafe70");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from catalog");
            while(rs.next()){
                System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3));
            }
            con.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }
}
