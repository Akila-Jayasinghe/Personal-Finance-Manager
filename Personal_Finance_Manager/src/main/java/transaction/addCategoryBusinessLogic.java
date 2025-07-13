/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package transaction;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class addCategoryBusinessLogic {
    
    public void addCategory(
            javax.swing.JTextField cName, 
            javax.swing.JRadioButton incomeRadio, 
            javax.swing.JRadioButton expenseRadio,
            sessionCredential sessionData,
            addCategories parentInterface) {
        
        // check String legtth
        String catName = cName.getText();
        if (catName.length() >= 50) {
            JOptionPane.showMessageDialog(
                    parentInterface,
                    ("Category name is too long.\nMaximum character limit is 50."),
                    "String length error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        // Format the category string
        catName = catName.substring(0, 1).toUpperCase() + catName.substring(1).toLowerCase();
        
        // Get the category type as boolean
        boolean catType = incomeRadio.isSelected();
        if ((!incomeRadio.isSelected()) && (!expenseRadio.isSelected())) {
            JOptionPane.showMessageDialog(
                    parentInterface,
                    ("Category type wasn't enetered.\nEnter the category type to identify how it effects to your savings."),
                    "String length error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // perform database operation
        try {
            if (new transactionCRUD().addUserDefinedCategory(catName, catType, sessionData.getUserID())) {
                JOptionPane.showMessageDialog(
                    parentInterface,
                    ("Category was successfully added."),
                    "Add category",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    parentInterface,
                    ("Category addition wasn't completed.\nDue to an unidentified error."),
                    "Add category error",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(addCategories.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
