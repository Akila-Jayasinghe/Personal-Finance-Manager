/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login_registration;

import dashboard.dashboard;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class loginBusinessLogic {
    
    private final dbCRUD_loginRegister databaseOperations = new dbCRUD_loginRegister();
    
    public void loginProcess(javax.swing.JTextField userNameField, javax.swing.JPasswordField passwordField, javax.swing.JButton loginButton) {
        if (databaseOperations.validateUser(userNameField.getText(), passwordField.getText())) {
            
            // Start a session
            sessionCredential tempSession;
            try { tempSession = new sessionCredential(userNameField.getText()); }
            catch (SQLException ex) { tempSession = new sessionCredential(); }
            final sessionCredential currentSession = tempSession;
            
            // Open the next frame
            dashboard dashPage = new dashboard(currentSession);
            dashPage.setVisible(true);

            // Close the current frame
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(loginButton);
            currentFrame.dispose();

        } else {
            JOptionPane.showMessageDialog(null,("Invalid credentials detected.\nPlease re-check the user name and password"),"Log-in failed",JOptionPane.WARNING_MESSAGE);
        }
    }
    
}
