/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editTransactions;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Akila Jayasinghe
 */
public class editTransactionsCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public List<Object[]> getTransactionsByUserID(int userId) throws SQLException {
        
        List<Object[]> dataRows = new ArrayList<>();
        String sql = "SELECT t.Trans_ID, c.Cat_name, c.Cat_type, t.Amount, t.Date " +
                 "FROM transaction_details t, category c " +
                 "WHERE t.Cat_ID=c.Cat_ID AND t.User_ID = ? ORDER BY t.Date DESC";      

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
        
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getInt("Trans_ID");
                    row[1] = rs.getString("Cat_name");
                    row[2] = rs.getString("Cat_type");
                    row[3] = rs.getDouble("Amount");
                    row[4] = rs.getDate("Date");
                    dataRows.add(row);
                }
            }
        }
        return dataRows;
    }
    
    
    public List<String> getCategoriesForUser(String userName) throws SQLException {
        
        List<String> categories = new ArrayList<>();
        String sql = """
            SELECT 
                c.Cat_ID,
                c.Cat_name,
                c.Cat_type AS category_type 
            FROM category c
            WHERE 
                c.Cat_ID IN (SELECT Cat_ID FROM default_categories)
                OR 
                c.Cat_ID IN (
                    SELECT Cat_ID 
                    FROM user_defined_categories udc
                    JOIN users u ON udc.User_ID = u.User_ID
                    WHERE u.User_name = ? 
                )
            ORDER BY c.Cat_type, c.Cat_name;
        """;
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
        
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String temp = String.format("%d|", rs.getInt("Cat_ID"));
                    temp += (rs.getString("category_type").equals("debit") ? "-" : "+") + "|";
                    temp += rs.getString("Cat_name");
                    categories.add(temp);
                }
            }
        }
        return categories;
    }
    
    public List<String> getUsersCategories(String userName) throws SQLException {
        
        List<String> categories = new ArrayList<>();
        String sql = """
            SELECT 
                c.Cat_ID,
                c.Cat_name,
                c.Cat_type AS category_type 
            FROM category c
            WHERE 
                c.Cat_ID IN (
                    SELECT Cat_ID 
                    FROM user_defined_categories udc
                    JOIN users u ON udc.User_ID = u.User_ID
                    WHERE u.User_name = ?
                )
            ORDER BY c.Cat_type, c.Cat_name
        """;
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
        
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String temp = String.format("%d|", rs.getInt("Cat_ID"));
                    temp += (rs.getString("category_type").equals("debit") ? "-" : "+") + "|";
                    temp += rs.getString("Cat_name");
                    categories.add(temp);
                }
            }
        }
        return categories;
    }
    
    public boolean deleteCategoryByID(int Cat_ID) throws SQLException {
        String sql = "DELETE FROM category WHERE Cat_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Cat_ID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    
    public boolean deleteTransactionByID(int t_ID) throws SQLException {
        String sql = "DELETE FROM transaction_details WHERE Trans_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, t_ID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    
    public boolean updateCategoryByID(int Cat_ID, String newName, String type) throws SQLException {
        String sql = "UPDATE category SET Cat_name = ?, Cat_type = ? WHERE Cat_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, type);
            pstmt.setInt(3, Cat_ID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    
    public boolean updateTransaction(double amount, java.sql.Date date, int userId, int categoryId, int tID) throws SQLException {
        String sql = "UPDATE transaction_details SET Amount = ?, Date = ?, User_ID = ?, Cat_ID = ? WHERE Trans_ID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters
            pstmt.setDouble(1, amount);
            pstmt.setDate(2, date);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, categoryId);
            pstmt.setInt(5, tID);
            
            // Execute update
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
        
    }
    
    ;
    
}
