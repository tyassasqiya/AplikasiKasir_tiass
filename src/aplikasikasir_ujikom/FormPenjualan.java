/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package aplikasikasir_ujikom;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import java.util.Date;
/**
 *
 * @author LENOVO
 */
public class FormPenjualan extends javax.swing.JFrame {
Connection konek;
PreparedStatement pst, pst2;
ResultSet rst;
int inputstok, inputstok2, inputharga, inputjumlah, kurangistok, tambahstok;
String harga, idproduk,  idprodukpenjualan, iddetail, jam, tanggal, sub_total;
    /**
     * Creates new form FormPenjualan
     */
    public FormPenjualan() {
        initComponents();
        konek = Koneksi.koneksiDB();
        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
        detail();
        tampilWaktu();
        autonumber();
        penjumlahan();
    }
    
    private void simpan() {
        String tgl = txtTanggal.getText();
        String jam = txtJam.getText();
        
        try {
           String sql = "Insert into penjualan(PenjualanID, DetailID, TanggalPenjualan, JamPenjualan, TotalHarga) value (?,?,?,?,?)"; 
           pst = konek.prepareStatement(sql);
           pst.setString(1, txtIdPenjualan.getText());
           pst.setString(2, iddetail);
           pst.setString(3, tgl);
           pst.setString(4, jam);
           pst.setString(5, txtTotal.getText());
           pst.execute();
           JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");
        }
    }
    
    private void total() {
        int total, bayar, kembalian;
        total = Integer.parseInt(txtBayar.getText());
        bayar = Integer.parseInt(txtTotal.getText());
        kembalian = total - bayar;
        String ssub = String.valueOf(kembalian);
        txtKembalian.setText(ssub);
    }
    
    public void clear() {
        txtJumlah.setText("");
    }
    
    public void cari() {
        try {
            String sql = "select * from produk where ProdukID LIKE '%"+txtIdProduk.getText()+"%'";
            pst = konek.prepareStatement(sql);
            rst = pst.executeQuery();
            tblProduk.setModel(DbUtils.resultSetToTableModel(rst));
        } catch (Exception e) {
           JOptionPane.showMessageDialog(null, e); 
        }
    }
    
    private void kurangi_stok() {
        int qty;
        qty = Integer.parseInt(txtJumlah.getText());
        kurangistok = inputstok - qty;
    }
    
    private void subtotal() {
        int jumlah, sub;
        jumlah = Integer.parseInt(txtJumlah.getText());
        sub = (jumlah * inputharga);
        sub_total= String.valueOf(sub);
    }
    
    public void tampilWaktu() {
        Thread clock = new Thread() {
            public void run() {
                for(;;) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat Jam = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat Tanggal = new SimpleDateFormat("yyyy-MM-dd");
                    txtJam.setText(Jam.format(cal.getTime()));
                    txtTanggal.setText(Tanggal.format(cal.getTime()));
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MenuUtama.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        clock.start();
    }
    
    public void tambah_stok() {
        tambahstok = inputjumlah + inputstok2;
        try {
            String update = "update produk set Stok='"+tambahstok+"' where ProdukID='"+idproduk+"'";
            pst2 = konek.prepareStatement(update);
            pst2.execute();
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e);   
        }
    }
    
    public void ambil_stok() {
        try {
           String sql = "select * from produk where ProdukID='"+idproduk+"'";
           pst = konek.prepareStatement(sql);
           rst = pst.executeQuery();
           if (rst.next()) {
               String stok = rst.getString(("Stok"));
               inputstok2 = Integer.parseInt(stok);
           }
        } catch (Exception e) {
           JOptionPane.showMessageDialog(null, e);    
        }
    }
    
    public void penjumlahan() {
        int totalBiaya= 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) tblPenjualan.getModel();
        int jumlah = tblPenjualan.getRowCount();
        for (int i=0; i<jumlah; i++) {
            subtotal = Integer.parseInt(dataModel.getValueAt(1, 5).toString());
            totalBiaya += subtotal;
        }
        txtTotal.setText(String.valueOf(totalBiaya));
    }
    
    public void autonumber() {
        try {
          String sql = "SELECT MAX(RIGHT(PenjualanID,3)) AS NO FROM penjualan";
          pst = konek.prepareStatement(sql);
          rst = pst.executeQuery();
          while (rst.next()) {
              if (rst.first() == false) {
                  txtIdPenjualan.setText("IDP001");
              } else {
                  rst.last();
                  int auto_id = rst.getInt(1) + 1;
                  String no = String.valueOf(auto_id);
                  int NomorJual = no.length();
                  for (int j = 0; j < 3 - NomorJual; j++) {
                      no = "0" + no;
                  }
                  txtIdPenjualan.setText("IDP" + no);
              }
          }
          rst.close();
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Gagal Menambah ID Penjualan");     
        }
    }
    
    public void detail() {
        try {
          String ID_Detail = txtIdPenjualan.getText();
          String kd = "D" + ID_Detail;
          String sql = "select * from detailpenjualan where DetailID='"+kd+"'";
          pst = konek.prepareStatement(sql);
          rst = pst.executeQuery();
          tblPenjualan.setModel(DbUtils.resultSetToTableModel(rst));
        } catch (Exception e) {
           JOptionPane.showMessageDialog(null, e); 
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtIdProduk = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduk = new javax.swing.JTable();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtIdPenjualan = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPenjualan = new javax.swing.JTable();
        btnHapus = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        txtKembalian = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtJam = new javax.swing.JTextField();
        txtTanggal = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        jTextField7.setText("jTextField7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("MingLiU-ExtB", 1, 24)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-transaction-list-30 (1).png")); // NOI18N
        jLabel1.setText("FORM TRANSAKSI PENJUALAN");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 30, 340, 30);

        txtIdProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdProdukActionPerformed(evt);
            }
        });
        getContentPane().add(txtIdProduk);
        txtIdProduk.setBounds(20, 90, 210, 30);

        jLabel2.setFont(new java.awt.Font("MingLiU-ExtB", 1, 14)); // NOI18N
        jLabel2.setText("Masukan ID Produk");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 70, 140, 18);

        btnCari.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCari.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-search-20.png")); // NOI18N
        btnCari.setText("CARI");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        getContentPane().add(btnCari);
        btnCari.setBounds(250, 90, 100, 27);

        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdukMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduk);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 130, 452, 130);
        getContentPane().add(jTextField3);
        jTextField3.setBounds(650, 30, 64, 22);
        getContentPane().add(jTextField4);
        jTextField4.setBounds(730, 30, 64, 22);
        getContentPane().add(txtJumlah);
        txtJumlah.setBounds(480, 220, 64, 30);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Jumlah");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(490, 200, 50, 16);

        btnTambah.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-add-20.png")); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        getContentPane().add(btnTambah);
        btnTambah.setBounds(560, 220, 40, 30);

        jLabel5.setFont(new java.awt.Font("MingLiU-ExtB", 1, 18)); // NOI18N
        jLabel5.setText("ID Penjualan");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(10, 270, 190, 23);
        getContentPane().add(txtIdPenjualan);
        txtIdPenjualan.setBounds(170, 270, 190, 30);

        jLabel6.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 14)); // NOI18N
        jLabel6.setText("Data Produk");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(10, 310, 140, 19);

        tblPenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblPenjualan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPenjualanMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPenjualan);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(10, 330, 470, 130);

        btnHapus.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-delete-10.png")); // NOI18N
        btnHapus.setText("HAPUS");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        getContentPane().add(btnHapus);
        btnHapus.setBounds(500, 340, 90, 40);

        btnKeluar.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-logout-10.png")); // NOI18N
        btnKeluar.setText("KELUAR");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        getContentPane().add(btnKeluar);
        btnKeluar.setBounds(700, 463, 100, 40);

        btnBayar.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\icons8-trolley-15.png")); // NOI18N
        btnBayar.setText("BAYAR");
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });
        getContentPane().add(btnBayar);
        btnBayar.setBounds(570, 463, 100, 40);

        jLabel7.setFont(new java.awt.Font("MingLiU-ExtB", 1, 18)); // NOI18N
        jLabel7.setText("Total");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(10, 480, 60, 16);
        getContentPane().add(txtTotal);
        txtTotal.setBounds(70, 470, 220, 40);

        jLabel8.setFont(new java.awt.Font("MingLiU-ExtB", 1, 14)); // NOI18N
        jLabel8.setText("Bayar");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(10, 560, 37, 16);
        getContentPane().add(txtBayar);
        txtBayar.setBounds(70, 550, 140, 30);
        getContentPane().add(txtKembalian);
        txtKembalian.setBounds(370, 550, 150, 30);

        jLabel9.setFont(new java.awt.Font("MingLiU-ExtB", 1, 14)); // NOI18N
        jLabel9.setText("Kembalian");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(280, 560, 90, 18);

        jLabel10.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\ktias.jpg")); // NOI18N
        getContentPane().add(jLabel10);
        jLabel10.setBounds(0, 0, 860, 610);
        getContentPane().add(txtJam);
        txtJam.setBounds(1070, 40, 64, 22);
        getContentPane().add(txtTanggal);
        txtTanggal.setBounds(1150, 40, 64, 22);

        jLabel11.setIcon(new javax.swing.ImageIcon("C:\\Users\\LENOVO\\Downloads\\ktias.jpg")); // NOI18N
        jLabel11.setText("jLabel11");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(860, 0, 580, 610);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProdukActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
       this.dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
      cari();  
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
       subtotal();
       kurangi_stok();
       try{
         String ID_detail=txtIdPenjualan.getText();
         iddetail="D"+ ID_detail;
         String sql="insert into detailpenjualan (DetailID,ProdukID,Harga,JumlahProduk,Subtotal) value (?,?,?,?,?)";
         String update="update produk set Stok='"+kurangistok+"' where ProdukID='"+idproduk+"'";
         pst=konek.prepareStatement(sql);
         pst2=konek.prepareStatement(update);
         pst.setString(1, iddetail);
         pst.setString(2, idproduk);
         pst.setString(3, harga);
         pst.setString(4, txtJumlah.getText());
         pst.setString(5, sub_total);
         pst.execute();
         pst2.execute();
       } catch (Exception e) {
           JOptionPane.showMessageDialog(null, e);
       }
       detail();
       penjumlahan();
       cari();
       clear();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
      try {
          String sql="delete from detailpenjualan where ProdukID=?";
          pst=konek.prepareStatement(sql);
          pst.setString(1, idprodukpenjualan);
          pst.execute();
      } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e);
      }
      detail();
      penjumlahan();
      tambah_stok();
      cari();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        total();
        simpan();
        autonumber();
        detail();
        txtTotal.setText("");
        txtBayar.setText("");
        txtKembalian.setText("");
        txtIdProduk.setText("");
        cari();
    }//GEN-LAST:event_btnBayarActionPerformed

    private void tblProdukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdukMouseClicked
      try {
         int row=tblProduk.getSelectedRow();
         String tabel_klik=(tblProduk.getModel().getValueAt(row, 0).toString());
         String sql="select * from produk where ProdukID='"+tabel_klik+"'";
         pst=konek.prepareStatement(sql);
         rst=pst.executeQuery();
         if(rst.next());
         idproduk=rst.getString(("ProdukID"));
         String stok=rst.getString(("Stok"));
         inputstok= Integer.parseInt(stok);
         harga=rst.getString(("Harga"));
         inputharga= Integer.parseInt(harga);
      } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e); 
      }
    }//GEN-LAST:event_tblProdukMouseClicked

    private void tblPenjualanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPenjualanMouseClicked
        try {
           int row=tblPenjualan.getSelectedRow();
            idprodukpenjualan=(tblPenjualan.getModel().getValueAt(row, 1).toString());
            String sql="select * from detailpenjualan where ProdukID='"+idprodukpenjualan+"'";
            pst=konek.prepareStatement(sql);
            rst=pst.executeQuery();
            if(rst.next());
            String jumlah=rst.getString(("JumlahProduk"));
            inputjumlah=Integer.parseInt(jumlah);
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e);     
        }
    }//GEN-LAST:event_tblPenjualanMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTable tblPenjualan;
    private javax.swing.JTable tblProduk;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtIdPenjualan;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtJam;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtTanggal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
