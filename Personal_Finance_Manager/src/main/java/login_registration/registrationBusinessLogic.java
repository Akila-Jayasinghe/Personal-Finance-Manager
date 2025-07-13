/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login_registration;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Akila Jayasinghe
 */
public class registrationBusinessLogic {
    
    private final dbCRUD_loginRegister databaseOperations = new dbCRUD_loginRegister();
    
    public void registerNewUser(
            javax.swing.JTextField userNameField, 
            javax.swing.JTextField nickNameField, 
            javax.swing.JPasswordField passwordField1, 
            javax.swing.JPasswordField passwordField2,
            javax.swing.JButton regButton,
            register parentInterface) {
        
        String userName = userNameField.getText();
        String nickName = nickNameField.getText();
        String password = passwordField2.getText();
        String rePassword = passwordField1.getText();
        
        try {
            if (!userName.trim().equals("")) {
                if (!nickName.trim().equals("")) {
                    if (password.equals(rePassword)) {
                        if (databaseOperations.addNewUser(userName, nickName, password, parentInterface)) {
                            // Open the next frame
                            login loginPage = new login();
                            loginPage.setVisible(true);

                            // Close the current frame
                            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(regButton);
                            currentFrame.dispose();
                        } else {
//                            JOptionPane.showMessageDialog(parentInterface,("Unidentified error was ocurred"),"Error",JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentInterface,("Password fields are not matched"),"Password error",JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentInterface,("Nick name not found."),"Nick name error",JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentInterface,("User name not found."),"User name error",JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            Logger.getLogger(register.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
