/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login_registration;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Akila Jayasinghe
 */
public class dbCRUD_loginRegister {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public boolean addNewUser(String userName, String nickName, String plainTextPassword, register parentInterface) throws SQLException, IllegalArgumentException {
        
        // Input validation
        userName = userName == null ? "" : userName.trim();
        nickName = nickName == null ? "" : nickName.trim();
        plainTextPassword = plainTextPassword == null ? "" : plainTextPassword.trim();
        
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(parentInterface,("User name field is empty\nEnter a user name"),"User name not found",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (nickName.isEmpty()) {
            JOptionPane.showMessageDialog(parentInterface,("Nick name field is empty\nEnter a nick name"),"Nick name not found",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (plainTextPassword.isEmpty()) {
            JOptionPane.showMessageDialog(parentInterface,("Password field is empty\nEnter a password"),"Password not found",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check for existing username
        if (usernameExists(userName)) {
            JOptionPane.showMessageDialog(parentInterface,("User name that you entered is already exist.\nEnter a new user name"),"User name error",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Insert with plaintext password (WARNING: INSECURE)
        String sql = "INSERT INTO users (User_name, Nick_name, Password) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            pstmt.setString(2, nickName);
            pstmt.setString(3, plainTextPassword);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(parentInterface,("Registration process is successfully completed."),"Registration",JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            return false;
        }
    }
    
    private boolean usernameExists(String userName) throws SQLException {
        String sql = "SELECT User_ID FROM users WHERE LOWER(User_name) = LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public boolean validateUser(String userName, String password) {
        String sql = "SELECT User_ID, Password FROM users WHERE User_name = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    if (password.equals(storedPassword)) {
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
}
