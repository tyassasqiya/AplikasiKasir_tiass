/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aplikasikasir_ujikom;
import javax.swing.JOptionPane;
import java.sql.DriverManager;
import java.sql.Connection;
/**
 *
 * @author LENOVO
 */
public class Koneksi {
    Connection koneksi = null;
    
   public static Connection koneksiDB() {
      try {
           Class.forName("com.mysql.jdbc.Driver");
           Connection koneksi = DriverManager.getConnection("jdbc:mysql://localhost:3306/kasir_tias", "root", "");
           return koneksi;
      } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Tidak terkoneksi ke database");
          return null;
      }
   } 
}
