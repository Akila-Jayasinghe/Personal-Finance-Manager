/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package session;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Akila Jayasinghe
 */
public class sessionCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public String getNickName_by_userName(String userName) throws SQLException {
        String sql = "SELECT Nick_name FROM users WHERE User_name = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString("Nick_name");
                    result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
                    return result;
                } else {
                    return null;
                }
            }
        }
    }
    
    public int getUserID_by_userName(String userName) throws SQLException {
        String sql = "SELECT User_ID FROM users WHERE User_name = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("User_ID") : -1;
            }
        }
    } 
    
}
