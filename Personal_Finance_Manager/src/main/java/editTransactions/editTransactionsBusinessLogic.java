/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editTransactions;

import allTransactions.allTransactions;
import java.awt.Color;
import java.awt.Component;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class editTransactionsBusinessLogic {
    
    private final editTransactionsCRUD databaseOperations = new editTransactionsCRUD();
    
    public void incomeExpenseTable(
            javax.swing.JTable dataTable,
            sessionCredential sessionData,
            editTransactions parentInterface) {

        try {
            // 3. Get data from database
            List<Object[]> dbData = databaseOperations.getTransactionsByUserID(sessionData.getUserID());

            // 4. Prepare column names
            String[] columns = {"ID", "Category", "Type", "Amount", "Date"};

            // 5. Create model with formatted data
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 3 ? Double.class : Object.class;
                }
            };

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

            for (Object[] row : dbData) {
                Object[] formattedRow = Arrays.copyOf(row, row.length);
                if (row[3] != null) {
                    formattedRow[3] = String.format("LKR %,.2f", row[3]);
                }
                if (row[4] instanceof Date) {
                    formattedRow[4] = dateFormat.format((Date) row[4]);
                }
                model.addRow(formattedRow);
            }

            // 6. Set model and configure table
            dataTable.setModel(model);

            // Configure column widths
            TableColumnModel columnModel = dataTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(50);   // ID
            columnModel.getColumn(1).setPreferredWidth(150);  // Category
            columnModel.getColumn(2).setPreferredWidth(80);   // Type
            columnModel.getColumn(3).setPreferredWidth(100);  // Amount
            columnModel.getColumn(4).setPreferredWidth(100);  // Date

            // Custom renderer for all columns including Amount
            DefaultTableCellRenderer coloredRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, 
                            hasFocus, row, column);

                    String type = table.getModel().getValueAt(row, 2).toString();

                    if (!isSelected) {
                        if (type.equalsIgnoreCase("debit")) {
                            c.setBackground(new Color(255, 200, 200));  // Light red
                        } else if (type.equalsIgnoreCase("credit")) {
                            c.setBackground(new Color(200, 255, 200));  // Light green
                        } else {
                            c.setBackground(table.getBackground());
                        }
                    }

                    // Special handling for Amount column
                    if (column == 3) {
                        ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                    } else {
                        ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                    }

                    return c;
                }
            };

            // Apply the colored renderer to all columns
            for (int i = 0; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(coloredRenderer);
            }

            // Special right-align renderer just for Amount column (keeps coloring)
            DefaultTableCellRenderer rightAlignedColoredRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = coloredRenderer.getTableCellRendererComponent(table, value, 
                            isSelected, hasFocus, row, column);
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                    return c;
                }
            };
            
            dataTable.getColumnModel().getColumn(3).setCellRenderer(rightAlignedColoredRenderer);
        
        } catch (SQLException ex) {
            Logger.getLogger(allTransactions.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(parentInterface,
                "Error retrieving transactions: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    public void setCategoriesTransactions(String defaultCategory, javax.swing.JComboBox<String> categoryCombo, sessionCredential sessionData){
        try {
            List<String> categoryList = databaseOperations.getCategoriesForUser(sessionData.getUserName());
            categoryCombo.removeAllItems();
            categoryCombo.addItem("-- Select a Category --");
            int selectedIndex=0, loop=0;
            for (String category : categoryList) {
                String[] catData = category.split("\\|");
                loop++;
                if (defaultCategory.equalsIgnoreCase(catData[2])) {
                    selectedIndex = loop;
                }
                categoryCombo.addItem(catData[2].substring(0,1).toUpperCase() + catData[2].substring(1).toLowerCase());
            }
            categoryCombo.setSelectedIndex(selectedIndex);
        } catch (SQLException e) {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("-- Categories not found --");
            categoryCombo.setSelectedIndex(0);
        }
    }
    
    
    public void setCategoriesEdit(String defaultCategory, javax.swing.JComboBox<String> categoryCombo, sessionCredential sessionData){
        try {
            List<String> categoryList = databaseOperations.getUsersCategories(sessionData.getUserName());
            categoryCombo.removeAllItems();
            categoryCombo.addItem("-- Select a Category --");
            int selectedIndex=0, loop=0;
            for (String category : categoryList) {
                String[] catData = category.split("\\|");
                loop++;
                if (defaultCategory.equalsIgnoreCase(catData[2])) {
                    selectedIndex = loop;
                }
                categoryCombo.addItem(catData[2].substring(0,1).toUpperCase() + catData[2].substring(1).toLowerCase());
            }
            categoryCombo.setSelectedIndex(selectedIndex);
        } catch (SQLException e) {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("-- Categories not found --");
            categoryCombo.setSelectedIndex(0);
        }
    }
    
    public boolean getCategoryType(javax.swing.JComboBox<String> categoryCombo, sessionCredential sessionData) throws SQLException {
        List<String> categoryList = databaseOperations.getUsersCategories(sessionData.getUserName());
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory.equalsIgnoreCase("-- Select a Category --") || selectedCategory.equalsIgnoreCase("-- Categories not found --")) {
            return false;
        }
        for (String category : categoryList) {
            String[] catData = category.split("\\|");
            if (selectedCategory.equalsIgnoreCase(catData[2].trim())) {
                System.out.println(catData[1].trim());
                return ("+".equals(catData[1].trim()));
            }
        }
        return false;
    }
    
    
    
    public int getCategoryIDbyName(javax.swing.JComboBox<String> categoryCombo, sessionCredential sessionData) throws SQLException {
        List<String> categoryList = databaseOperations.getCategoriesForUser(sessionData.getUserName());
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory.equalsIgnoreCase("-- Select a Category --") || selectedCategory.equalsIgnoreCase("-- Categories not found --")) {
            return -1;
        }
        for (String category : categoryList) {
            String[] catData = category.split("\\|");
            if (selectedCategory.equalsIgnoreCase(catData[2].trim())) {
                return Integer.parseInt(catData[0].trim());
            }
        }
        return -1;
    }
    
    public boolean deleteCategory(javax.swing.JTextField searchID, editTransactions parentInterface) throws SQLException {
        int id;
        try {
            id = Integer.parseInt(searchID.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        try {
            if (databaseOperations.deleteCategoryByID(id)) {
                JOptionPane.showMessageDialog(parentInterface,("Category was deleted successfully"),"Delete category",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentInterface,("Category deletion failed."),"Delete category",JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(editTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    } 
    
    public boolean deleteTransaction(javax.swing.JTextField searchID, editTransactions parentInterface) throws SQLException {
        int t_id;
        try {
            t_id = Integer.parseInt(searchID.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        try {
            if (databaseOperations.deleteTransactionByID(t_id)) {
                JOptionPane.showMessageDialog(parentInterface,("Transaction record was deleted successfully"),"Delete transaction",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentInterface,("Transaction record deletion failed."),"Delete transaction",JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(editTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    } 
    
    
    public boolean updateCategory(
            javax.swing.JTextField searchID,
            javax.swing.JComboBox<String> typeField,
            javax.swing.JTextField newName,
            editTransactions parentInterface) throws SQLException {
        
        int id;
        try {
            id = Integer.parseInt(searchID.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        String type = (String) typeField.getSelectedItem();
        if (type.equalsIgnoreCase("Expense")) {
            type = "debit";
        } else {
            type = "credit";
        }
        System.out.println(type);
        
        String catName = newName.getText().trim();
        catName = catName.substring(0, 1).toUpperCase() + catName.substring(1).toLowerCase();
        
        try {
            if (databaseOperations.updateCategoryByID(id, catName, type)) {
                JOptionPane.showMessageDialog(parentInterface,("Category was updated successfully"),"Update category",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentInterface,("Category update failed."),"Update category",JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(editTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }    
    
    public boolean updateTransaction(
            javax.swing.JTextField searchIdField,
            javax.swing.JComboBox<String> catIdField,
            javax.swing.JTextField amountField,
            javax.swing.JSpinner dateField,
            sessionCredential sessionData,
            editTransactions parentInterface) throws SQLException {
        
        int tid, cid, uid;
        double amount;
        Date date;
        try {
            tid = Integer.parseInt(searchIdField.getText().trim());
            cid = getCategoryID(catIdField, sessionData);
            uid = sessionData.getUserID();
            amount = Double.parseDouble(amountField.getText().trim());
            date = getDate(dateField);
        } catch (NumberFormatException e) {
            return false;
        }
        
        try {
            if (databaseOperations.updateTransaction(amount, date, uid, cid, tid)) {
                JOptionPane.showMessageDialog(parentInterface,("Transaction was updated successfully"),"Update Transaction",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentInterface,("Transaction update failed."),"Update Transaction",JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(editTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }   
    
    private java.sql.Date getDate(javax.swing.JSpinner dateSpinner) {
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        if (utilDate == null) {
            return new java.sql.Date(System.currentTimeMillis());
        } 
        return new java.sql.Date(utilDate.getTime());
    }
    
    
    private int getCategoryID(javax.swing.JComboBox<String> categoryCombo, sessionCredential sessionData) throws SQLException {
        List<String> categoryList = databaseOperations.getCategoriesForUser(sessionData.getUserName());
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory.equalsIgnoreCase("-- Select a Category --") || selectedCategory.equalsIgnoreCase("-- Categories not found --")) {
            return -1;
        }
        for (String category : categoryList) {
            String[] catData = category.split("\\|");
            if (selectedCategory.equalsIgnoreCase(catData[2].trim())) {
                return Integer.parseInt(catData[0].trim());
            }
        }
        return -1;
    }
    
    
}
