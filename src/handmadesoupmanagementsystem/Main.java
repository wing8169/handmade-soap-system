package handmadesoupmanagementsystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chin Jia Xiong
 */
public class Main extends javax.swing.JFrame {

    private final String mode;
    public static ArrayList<AddOns> selectedAddOns = new ArrayList<>();
    ArrayList<Oil> oilInfo = new ArrayList<>();
    SortedMap<Oil, Double> selectedOil = new TreeMap<>();  // TreeMap is sorted by Oil's INS solid
    double oilAmount, waterAmount;

    public Main(String mode) {
        // Design stage
        this.mode = mode;
        initComponents();
        setVisible(true);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(1000);
        setLocationRelativeTo(null);
        TableOperation.initTableDesign(jTable1);
        TableOperation.initTableDesign(jTable2);
        if (mode.equals("solid")) {
            jLabel79.setText("手工皂懒人包（固态皂）");
            jButton10.setText("切换液态皂");
            jTable1.getColumnModel().getColumn(jTable1.getColumn("KOH公克数").getModelIndex()).setHeaderValue("NaOH公克数");
            jTable1.getTableHeader().repaint();
        } else {
            jLabel79.setText("手工皂懒人包（液态皂）");
            jButton10.setText("切换固态皂");
        }
        // initialize database
        DatabaseOperation.createTable("oil",
                new String[]{"nameChi", "nameEng", "NaOH", "KOH", "INSsolid", "INSliquid", "price"},
                new Object[]{"NONE", "NONE", -1.0, -1.0, -1, -1, -1},
                new String[]{"string", "string", "double", "double", "integer", "integer", "double"});
        // history table
        DatabaseOperation.createTable("history",
                new String[]{"name", "mode", "oilAmount", "waterAmount", "selectedOilInfo"},
                new Object[]{"NONE", "NONE", -1.0, -1.0, ""},
                new String[]{"string", "string", "double", "double", "string"});
        // addOns table
        DatabaseOperation.createTable("addOns",
                new String[]{"nameChi", "nameEng", "price", "amount"},
                new Object[]{"NONE", "NONE", -1.0, -1.0},
                new String[]{"string", "string", "double", "double"});
        // clear rows
        TableOperation.clear(jTable1);
        // add all the oil from database to temp storage.
        ArrayList<Object[]> resultSet = DatabaseOperation.getData("oil");
        for (Object[] record : resultSet) {
            oilInfo.add(new Oil((Integer) record[0], (String) record[1], (String) record[2],
                    (Double) record[3], (Double) record[4],
                    (Integer) record[5], (Integer) record[6], (Double) record[7]));
        }
        // Load the histories saved.
        resultSet = DatabaseOperation.getData("history", mode);
        jComboBox1.removeAllItems();
        jComboBox1.addItem("");
        for (Object[] record : resultSet) {
            jComboBox1.addItem((String) record[1]);
        }
        // Listener for water amount
        jTextField89.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAmount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAmount();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAmount();
            }

            public void updateAmount() {
                try {
                    oilAmount = Double.parseDouble(jTextField90.getText());
                    waterAmount = Double.parseDouble(jTextField89.getText());
                } catch (Exception e) {
                    oilAmount = 0;
                    waterAmount = 0;
                }
                generateTable();
            }
        });
        // oil amount listener, same as water amount listener
        jTextField90.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAmount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAmount();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAmount();
            }

            public void updateAmount() {
                try {
                    oilAmount = Double.parseDouble(jTextField90.getText());
                    waterAmount = Double.parseDouble(jTextField89.getText());
                } catch (Exception e) {
                    oilAmount = 0;
                    waterAmount = 0;
                }
                generateTable();
            }
        });
        // history listener
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jComboBox1.getSelectedItem().toString().isEmpty()) {
                    return;
                }
                Object[] resultSet = DatabaseOperation.getHistoricData("history", jComboBox1.getSelectedItem().toString());
                jTextField90.setText(Double.toString((Double) resultSet[3]));
                jTextField89.setText(Double.toString((Double) (resultSet[4])));
                selectedOil.clear();
                String selectedOilString = (String) resultSet[5];
                StringTokenizer st = new StringTokenizer(selectedOilString, ",");
                // add all the historied oil into temp storage
                while (st.hasMoreTokens()) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken());
                    int oilId = Integer.parseInt(st2.nextToken());
                    Oil smallOil = null;
                    for (Oil o : oilInfo) {
                        if (o.getId() == oilId) {
                            smallOil = o;
                            break;
                        }
                    }
                    double pct = Double.parseDouble(st2.nextToken());
                    selectedOil.put(smallOil, pct);
                }
                generateTable();
                updateOilTable();
            }
        });
        generateTable();
    }

    public void updateOilTable() {
        // clean the table
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        int count = 1;
        for (Oil o : selectedOil.keySet()) {
            model.addRow(new Object[]{count, o.getNameChi(), selectedOil.get(o)});
            count++;
        }
    }

    public final void generateTable() {
        // if amount successfully updated, user can start adding oil
        try {
            oilAmount = Double.parseDouble(jTextField90.getText());
            waterAmount = Double.parseDouble(jTextField89.getText());
        } catch (Exception e) {
            oilAmount = 0;
            waterAmount = 0;
        }
        // clean the table no matter what
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        // try to draw the table
        int i = 1;
        double NaOHTotal = 0, INSTotal = 0, pctTotal = 0, oilTotal = 0;
        for (Oil o : selectedOil.keySet()) {
            String nameChi = o.getNameChi();
            String pct = String.format("%.2f", selectedOil.get(o));
            String oilAmnt = String.format("%.2f", (selectedOil.get(o) * oilAmount / 100.0f));
            String NaOH;
            if (mode.equals("solid")) {
                NaOH = String.format("%.4f", o.getNaOH());
            } else {
                NaOH = String.format("%.4f", o.getKOH());
            }
            String NaOHAmnt = String.format("%.2f", (Double.parseDouble(NaOH) * Double.parseDouble(oilAmnt)));
            String ins;
            if (mode.equals("solid")) {
                ins = Integer.toString(o.getINSsolid());
            } else {
                ins = Integer.toString(o.getINSliquid());
            }
            String currentInsTotal = String.format("%.2f", (Double.parseDouble(ins) * Double.parseDouble(pct) / 100.0f));
            NaOHTotal += Double.parseDouble(NaOHAmnt);
            INSTotal += Double.parseDouble(currentInsTotal);
            pctTotal += Double.parseDouble(pct);
            oilTotal += Double.parseDouble(oilAmnt);
            model.addRow(new Object[]{i, nameChi, pct, oilAmnt, NaOH, NaOHAmnt, ins, currentInsTotal});
            i++;
        }
        model.addRow(new Object[]{null, null, String.format("%.2f", pctTotal), String.format("%.2f", oilTotal), null, String.format("%d", Math.round(NaOHTotal)), null, String.format("%d", Math.round(INSTotal))});
        model.addRow(new Object[]{null, null, null, null, null, String.format("水 = %d", Math.round(Math.round(NaOHTotal) * waterAmount)), null, null});
        double grandTotal = Math.round(NaOHTotal) + oilTotal + Math.round(NaOHTotal * waterAmount);
        model.addRow(new Object[]{String.format("总重量 = %d", Math.round(grandTotal)), null, null, null, null, null, null, null});
        model.addRow(new Object[]{"实际重量 = ", null, null, null, null, null, null, null, null});
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel79 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        jTextField89 = new javax.swing.JTextField();
        jTextField90 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel83 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("手工皂懒人包");
        setResizable(false);

        jPanel4.setBackground(new java.awt.Color(255, 153, 204));

        jLabel78.setFont(new java.awt.Font("Arial Unicode MS", 1, 24)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(0, 0, 0));
        jLabel78.setText("油量总量 : ");

        jLabel98.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(102, 102, 102));

        jTable1.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.", "油品", "百分比", "油品公克数", "皂化价", "KOH公克数", "INS值", "INS合计"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(0, 0, 0));
        jTable1.setRowHeight(24);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane11.setViewportView(jTable1);

        jButton7.setBackground(new java.awt.Color(0, 153, 255));
        jButton7.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton7.setText("添加油品");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 153, 255));
        jButton8.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton8.setText("继续");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel79.setFont(new java.awt.Font("Arial Unicode MS", 1, 36)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(0, 0, 0));
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel79.setText("手工皂懒人包（固态皂）");

        jButton9.setBackground(new java.awt.Color(0, 153, 255));
        jButton9.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton9.setText("删除选定油品");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel81.setFont(new java.awt.Font("Arial Unicode MS", 1, 24)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(0, 0, 0));
        jLabel81.setText("水量倍数 : ");

        jTextField89.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N

        jTextField90.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N

        jButton10.setBackground(new java.awt.Color(0, 153, 255));
        jButton10.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton10.setForeground(new java.awt.Color(0, 0, 0));
        jButton10.setText("切换液态皂");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Arial Unicode MS", 0, 24)); // NOI18N

        jLabel83.setFont(new java.awt.Font("Arial Unicode MS", 1, 24)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(0, 0, 0));
        jLabel83.setText("导入配方 :");

        jTable2.setFont(new java.awt.Font("Verdana", 0, 24)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "油品", "百分比"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setRowHeight(36);
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.setSurrendersFocusOnKeystroke(true);
        jScrollPane12.setViewportView(jTable2);

        jButton12.setBackground(new java.awt.Color(0, 153, 255));
        jButton12.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton12.setText("更改选定油品的百分比");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(255, 0, 0));
        jButton13.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("清空所有格子");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(0, 153, 255));
        jButton11.setFont(new java.awt.Font("Arial Unicode MS", 1, 18)); // NOI18N
        jButton11.setText("保存配方");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(129, 129, 129)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(179, 179, 179))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(jLabel81))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addComponent(jLabel98))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 1030, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(329, 329, 329)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel79, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                                .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel78)
                                    .addGap(4, 4, 4)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel83)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton13))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(279, 279, 279)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton10)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jButton7)
                        .addGap(39, 39, 39)
                        .addComponent(jButton9)
                        .addGap(40, 40, 40)
                        .addComponent(jButton12))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton11)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(jLabel98))
        );

        jScrollPane1.setViewportView(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1083, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1127, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        JComboBox oilBox = new JComboBox();
        JTextField pctField = new JTextField();
        // add the available oil
        for (Oil oil : oilInfo) {
            if (!selectedOil.containsKey(oil)) {
                if ((mode.equals("solid") && oil.getNaOH() == -1 && oil.getINSsolid() == -1) || (mode.equals("liquid") && oil.getKOH() == -1 && oil.getINSliquid() == -1)) {
                    continue;
                }
                oilBox.addItem(oil.getNameChi());
            }
        }
        final JComponent[] inputs = new JComponent[]{
            new JLabel("油"),
            oilBox,
            new JLabel("油百分比"),
            pctField
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "Oil Info", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Oil selected = null;
                // grab selected-oil-related data from storage
                for (Oil o : oilInfo) {
                    if (o.getNameChi().equals(oilBox.getSelectedItem().toString())) {
                        selected = o;
                        break;
                    }
                }
                if (selected == null) {
                    JOptionPane.showMessageDialog(this, "没有可用的油，请重试。");
                } else {
                    // if data successfully grabbed
                    double currentPct = Double.parseDouble(pctField.getText());
                    if (currentPct <= 0 || currentPct > 100) {
                        JOptionPane.showMessageDialog(this, "百分比超出限制。");
                    } else {
                        // add and try generate table
                        selectedOil.put(selected, currentPct);
                        generateTable();
                        updateOilTable();
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "百分比格式不对！");
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        PriceList.go(selectedOil, oilAmount);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        int row = jTable2.getSelectedRow();
        // if a row is selected
        if (row != -1) {
            String nameChi = jTable2.getModel().getValueAt(row, jTable2.getColumn("油品").getModelIndex()).toString();
            // confimation
            int rslt = JOptionPane.showConfirmDialog(this, "确认删除油品 " + nameChi + "？", "Delete", JOptionPane.OK_CANCEL_OPTION);
            // if yes
            if (rslt == JOptionPane.OK_OPTION) {
                // remove the record from database
                Oil tmp = null;
                for (Oil o : selectedOil.keySet()) {
                    if (o.getNameChi().equals(nameChi)) {
                        tmp = o;
                        break;
                    }
                }
                if (tmp != null) {
                    selectedOil.remove(tmp);
                    generateTable();
                    updateOilTable();
                }
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // change mode
        this.dispose();
        if (mode.equals("solid")) {
            Main.go("liquid");
        } else {
            Main.go("solid");
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        int row = jTable2.getSelectedRow();
        // if a row is selected
        if (row != -1) {
            String nameChi = jTable2.getModel().getValueAt(row, jTable2.getColumn("油品").getModelIndex()).toString();
            // find the selected oil data from storage
            Oil tmp = null;
            for (Oil o : selectedOil.keySet()) {
                if (o.getNameChi().equals(nameChi)) {
                    tmp = o;
                    break;
                }
            }
            JComboBox oilBox = new JComboBox();
            JTextField pctField = new JTextField();
            oilBox.addItem(nameChi);
            oilBox.setEnabled(false);
            double crtPct = selectedOil.get(tmp);
            pctField.setText(Double.toString(crtPct));
            final JComponent[] inputs = new JComponent[]{
                new JLabel("油"),
                oilBox,
                new JLabel("油百分比"),
                pctField
            };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Oil Info", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // if data successfully grabbed
                    double currentPct = Double.parseDouble(pctField.getText());
                    if (currentPct <= 0 || currentPct > 100) {
                        JOptionPane.showMessageDialog(this, "百分比超出限制。");
                    } // if the oil can fit
                    else {
                        // add and try generate table
                        selectedOil.replace(tmp, currentPct);
                        generateTable();
                        updateOilTable();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "百分比格式不对！");
                }
            }
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void reset() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        DefaultTableModel model2 = (DefaultTableModel) jTable2.getModel();
        model2.setRowCount(0);
        selectedAddOns.clear();
        selectedOil.clear();
        jTextField89.setText("");
        jTextField89.setEnabled(true);
        jTextField90.setText("");
        jTextField90.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        updateOilTable();
        generateTable();
    }

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset everything?", "Reset", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            reset();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        int result = JOptionPane.showConfirmDialog(null, "是否储存当前配方？");
        // if the data is complete
        if (result == JOptionPane.OK_OPTION) {
            boolean completed = false;
            ArrayList<Object[]> resultSet = DatabaseOperation.getData("history");
            ArrayList<String> namelist = new ArrayList<>();
            for (Object[] record : resultSet) {
                namelist.add((String) record[1]);
            }
            JTextField nameField = new JTextField();
            final JComponent[] askName = new JComponent[]{
                new JLabel("请命名配方 : "),
                nameField,};
            int result2 = JOptionPane.showConfirmDialog(null, askName, "Oil Info", JOptionPane.PLAIN_MESSAGE);
            if (result2 == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                if (namelist.contains(name)) {
                    int result3 = JOptionPane.showConfirmDialog(this, "该配方名字已经存在，是否覆盖之前的纪录？");
                    if (result3 == JOptionPane.OK_OPTION) {
                        updateHistory(name);
                    } else {
                        JOptionPane.showMessageDialog(this, "已取消保存。");
                    }
                } else {
                    addHistory(name);
                }
            }
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void addHistory(String name) {
        String selectedOilInfo = "";
        int count = 0;
        for (Oil o : selectedOil.keySet()) {
            count++;
            selectedOilInfo += (Integer.toString(o.getId()) + " " + selectedOil.get(o));
            if (count != selectedOil.size()) {
                selectedOilInfo += ",";
            }
        }
        DatabaseOperation.insertData("history",
                new String[]{"name", "mode", "oilAmount", "waterAmount", "selectedOilInfo"},
                new Object[]{name, mode, oilAmount, waterAmount, selectedOilInfo},
                new String[]{"string", "string", "double", "double", "string"});
        // notification
        JOptionPane.showMessageDialog(this, "手工皂配方 " + name + " 储存成功!");
        // update combobox
        jComboBox1.addItem(name);
    }
    
    private void updateHistory(String name){
        String selectedOilInfo = "";
        int count = 0;
        for (Oil o : selectedOil.keySet()) {
            count++;
            selectedOilInfo += (Integer.toString(o.getId()) + " " + selectedOil.get(o));
            if (count != selectedOil.size()) {
                selectedOilInfo += ",";
            }
        }
        DatabaseOperation.updateHistoricData(
                "history", 
                new String[]{"mode", "oilAmount", "waterAmount", "selectedOilInfo"}, 
                new Object[]{mode, oilAmount, waterAmount, selectedOilInfo}, 
                name,
                new String[]{"string", "double", "double", "string"});
        // notification
        JOptionPane.showMessageDialog(this, "手工皂配方 " + name + " 储存成功!");
    }

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main("solid").setVisible(true);
            }
        });
    }

    public static void go(String mode) {
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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main(mode).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField89;
    private javax.swing.JTextField jTextField90;
    // End of variables declaration//GEN-END:variables
}
