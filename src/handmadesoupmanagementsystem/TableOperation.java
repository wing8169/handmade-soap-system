package handmadesoupmanagementsystem;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Chin Jia Xiong
 */
public class TableOperation {
    
    /**
     * Initialize table design
     * @param table table to be implemented
     */
    public static void initTableDesign(JTable table){
        table.getTableHeader().setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        table.setRowHeight(40);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
    
    /**
     * Initialize table filter
     * @param table table to be filtered
     * @param textField text field for entering filtering text
     * @param columnName column name of table to be filtered
     */
    public static void initTableFilter(JTable table, JTextField textField, String columnName){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<TableModel> sorter = new TableRowSorter(model);
        table.setRowSorter(sorter);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void filter() {
                if (textField.getText().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter(textField.getText(), table.getColumn(columnName).getModelIndex()));
                    } catch (Exception e) {
                        sorter.setRowFilter(null);
                    }
                }
            }
        });
    }
    
    /**
     * clear everything in table
     * @param table table to be cleared
     */
    public static void clear(JTable table){
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
    }
    
    /**
     * remove selected row in table
     * @param table table to be cleared
     * @param row row to be removed
     */
    public static void clear(JTable table, int row){
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.removeRow(row);
    }
    
    /**
     * add data set into table
     * @param table table to be added
     * @param records records to be added
     */
    public static void add(JTable table, ArrayList<Object[]> records){
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int num = 1;
        for(Object[] record: records){
            Object[] recordWithNum = new Object[record.length+1];
            recordWithNum[0] = num;
            System.arraycopy(record, 0, recordWithNum, 1, record.length);
            model.addRow(recordWithNum);
            num++;
        }
    }
}
