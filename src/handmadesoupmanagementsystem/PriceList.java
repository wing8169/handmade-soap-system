package handmadesoupmanagementsystem;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.SortedMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chin Jia Xiong
 */
public class PriceList extends javax.swing.JFrame {

    ArrayList<Object[]> addOns;
    SortedMap<Oil, Double> selectedOil;
    double oilAmount;

    /**
     * Creates new form PriceList
     *
     * @param selectedOil oil selected from Main
     * @param oilAmount amount of oil used
     */
    public PriceList(SortedMap<Oil, Double> selectedOil, double oilAmount) {
        //  parse data in
        this.selectedOil = selectedOil;
        this.oilAmount = oilAmount;
        // design setting
        initComponents();
        setLocationRelativeTo(null);
        TableOperation.initTableDesign(jTable1);
        // filter
        TableOperation.initTableFilter(jTable1, jTextField89, "物品");
        // get addOns data
        addOns = DatabaseOperation.getData("addOns");
        updateTable();
    }

    private void updateTable() {
        // clear table row
        TableOperation.clear(jTable1);
        // initialize table data
        ArrayList<Object[]> tableData = new ArrayList<>();
        // oil data
        for (Oil oil : selectedOil.keySet()) {
            double currentAmount = Double.parseDouble(String.format("%.2f", selectedOil.get(oil) / 100.0 * oilAmount));
            double currentPrice = oil.getPrice();
            if (currentPrice > 0) {
                double finalPrice = currentPrice / 1000.0 * currentAmount;
                tableData.add(new Object[]{oil.getNameChi(), String.format("%.2f / 1000克", currentPrice), currentAmount, String.format("%.2f", finalPrice)});
            } else {
                tableData.add(new Object[]{oil.getNameChi(), "-", currentAmount, String.format("%.2f", 0.00f)});
            }
        }
        // addOns data
        for (AddOns addons : Main.selectedAddOns) {
            if (addons.getPrice() > 0) {
                double finalPrice = addons.getPrice() / addons.getOriginalAmount() * addons.getFinalAmount();
                tableData.add(new Object[]{addons.getName(), String.format("%.2f / %.2f克",
                    addons.getPrice(), addons.getOriginalAmount()), addons.getFinalAmount(), String.format("%.2f", finalPrice)});
            } else {
                tableData.add(new Object[]{addons.getName(), "-", addons.getFinalAmount(), 0.0});
            }
        }
        TableOperation.add(jTable1, tableData);
        double total = 0;
        try {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                total += Double.parseDouble(model.getValueAt(jTable1.convertRowIndexToModel(i), jTable1.getColumn("价格(RM)").getModelIndex()).toString());
            }
            model.addRow(new Object[]{"", "", "", "总价", String.format("%.2f", total)});
        } catch (NumberFormatException e) {

        }
    }

    private boolean alreadySelected(Object[] addOn) {
        for (AddOns selectedAddOn : Main.selectedAddOns) {
            if (selectedAddOn.getName().equals((String) addOn[1])) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jTextField89 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 153, 204));

        jLabel98.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(102, 102, 102));

        jTable1.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "物品", "成本(RM)", "用量(克/升)", "价格(RM)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(36);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jScrollPane11.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
        }

        jButton10.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton10.setText("加添加物");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton11.setText("移除添加物");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jTextField89.setFont(new java.awt.Font("Arial Unicode MS", 0, 24)); // NOI18N

        jLabel79.setFont(new java.awt.Font("Arial Unicode MS", 1, 24)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(0, 0, 0));
        jLabel79.setText("搜索: ");

        jLabel1.setFont(new java.awt.Font("Arial Unicode MS", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("价格表");

        jButton8.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton8.setText("打印");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton13.setText("关闭");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane11)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(271, 271, 271)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(84, 84, 84)
                                        .addComponent(jLabel79))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(159, 159, 159)
                                        .addComponent(jLabel1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField89, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(347, 347, 347)
                                .addComponent(jLabel98))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(311, 311, 311)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton13))
                .addGap(22, 22, 22)
                .addComponent(jLabel98))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        JComboBox addOnBox = new JComboBox();
        JTextField amountField = new JTextField();
        // add the available add ons
        for (Object[] addOn : addOns) {
            if (!alreadySelected(addOn) && (double) addOn[3] > 0) {
                addOnBox.addItem((String) addOn[1]);
            }
        }
        final JComponent[] inputs = new JComponent[]{
            new JLabel("添加物"),
            addOnBox,
            new JLabel("用量"),
            amountField
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "增加添加物", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                AddOns selected = null;
                // grab selected-oil-related data from storage
                for (Object[] addOn : addOns) {
                    if (((String) addOn[1]).equals(addOnBox.getSelectedItem().toString())) {
                        selected = new AddOns((String) addOn[1], Double.parseDouble(amountField.getText()),
                                (double) addOn[3], (double) addOn[4]);
                        break;
                    }
                }
                Main.selectedAddOns.add(selected);
                updateTable();
            } catch (HeadlessException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "用量格式不对！");
            }
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JComboBox addOnBox = new JComboBox();
        // add the selected add ons
        for (AddOns selectedAddOn : Main.selectedAddOns) {
            addOnBox.addItem(selectedAddOn.getName());
        }
        final JComponent[] inputs = new JComponent[]{
            new JLabel("添加物"),
            addOnBox
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "移除添加物", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // grab selected add-on data from storage
            for (AddOns selectedAddOn : Main.selectedAddOns) {
                if (selectedAddOn.getName().equals(addOnBox.getSelectedItem().toString())) {
                    Main.selectedAddOns.remove(Main.selectedAddOns.indexOf(selectedAddOn));
                    break;
                }
            }
            updateTable();
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    public void printComponent(Component component) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName(" Print Component ");

        pj.setPrintable(new Printable() {
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                component.paint(g2);
                return Printable.PAGE_EXISTS;
            }
        });
        if (pj.printDialog() == false) {
            return;
        }

        try {
            pj.print();
        } catch (PrinterException ex) {
            // handle exception
        }
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        c.insets = new Insets(10,10,10,10);
//        JTable testTable = new JTable(10,2);
        panel.add(jTable1, c);

        printComponent(panel);
        // create pdf file
//        try {
//            // header design
//            MessageFormat header = new MessageFormat("价格表");
//            // footer design
//            MessageFormat footer = new MessageFormat("Page {0,number,integer}");
//            MyPrintable mp = new MyPrintable(jTable1.getPrintable(JTable.PrintMode.FIT_WIDTH, header, footer));
//            jTable1.print(JTable.PrintMode.FIT_WIDTH, header, footer);
//        } catch (PrinterException ex) {
//            ex.printStackTrace();
//        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton13ActionPerformed

    public static void go(SortedMap<Oil, Double> selectedOil, double oilAmount) {
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
            java.util.logging.Logger.getLogger(PriceList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PriceList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PriceList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PriceList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PriceList(selectedOil, oilAmount).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField89;
    // End of variables declaration//GEN-END:variables
}
