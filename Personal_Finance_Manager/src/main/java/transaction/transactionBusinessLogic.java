/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package transaction;

import java.awt.HeadlessException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class transactionBusinessLogic {
    
    sessionCredential sessionData;
    
    public transactionBusinessLogic(sessionCredential sessionData) {
        this.sessionData = sessionData;
    }
    
    
    public void setupDateSpinner(javax.swing.JSpinner dateSpinner) {
        // Use Calendar to create a current date instance
        SpinnerDateModel spinnerModel = new SpinnerDateModel(
            Calendar.getInstance().getTime(),   // Initial date (now)
            null,                               // No minimum date
            null,                               // No maximum date
            Calendar.DAY_OF_MONTH               // Increment by day
        );

        dateSpinner.setModel(spinnerModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
    }
    
    
    public void setCategories(String defaultCategory, javax.swing.JComboBox<String> categoryCombo){
        try {
            List<String> categoryList = new transactionCRUD().getCategoriesForUser(sessionData.getUserName());
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
    
    /**
     *
     * @param amountField
     * @param dateSpinner
     * @param categoryCombo
     * @param parentInterface
     */
    public void addTransactionButtonLogic(
            javax.swing.JTextField amountField, 
            javax.swing.JSpinner dateSpinner, 
            javax.swing.JComboBox<String> categoryCombo,
            transaction parentInterface) {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (getCategoryID(categoryCombo) == -1) {
                JOptionPane.showMessageDialog(parentInterface,("Invalid category detected."),"Add category",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (new transactionCRUD().saveTransaction(amount, getDate(dateSpinner), sessionData.getUserID(), getCategoryID(categoryCombo))) {
                JOptionPane.showMessageDialog(parentInterface,("Transaction was added successfully"),"Add transaction",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentInterface,("Transaction Failed"),"Add transaction",JOptionPane.WARNING_MESSAGE);
            }  
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentInterface, "Invalid amount format", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentInterface, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(parentInterface, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getCategoryID(javax.swing.JComboBox<String> categoryCombo) throws SQLException {
        List<String> categoryList = new transactionCRUD().getCategoriesForUser(sessionData.getUserName());
        System.out.println(categoryList);
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
    
    private java.sql.Date getDate(javax.swing.JSpinner dateSpinner) {
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        if (utilDate == null) {
            return new java.sql.Date(System.currentTimeMillis());
        } 
        return new java.sql.Date(utilDate.getTime());
    }
    
    
    
}
