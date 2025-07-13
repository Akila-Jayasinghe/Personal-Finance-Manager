/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package transaction;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Akila Jayasinghe
 */
public class transactionCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
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
    
    
    public boolean saveTransaction(double amount, java.sql.Date date, int userId, int categoryId) throws SQLException {
        String sql = "INSERT INTO transaction_details (Amount, Date, User_ID, Cat_ID) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters
            pstmt.setDouble(1, amount);
            pstmt.setDate(2, date);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, categoryId);
            
            // Execute update
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
        
    }
    
    
    
    public String getLastCategory(int searchID) throws SQLException {
        String sqlUserCategory = """
            SELECT category.Cat_name 
                FROM category, user_defined_categories 
                WHERE 
                    user_defined_categories.User_ID = ? 
                        AND 
                    user_defined_categories.Cat_ID = category.Cat_ID 
                ORDER BY 
                    category.Cat_ID 
                        DESC
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUserCategory)) {
            pstmt.setDouble(1, searchID);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getString("Cat_name");
        }
    }
    
    
    
    public boolean addUserDefinedCategory(String categoryName, boolean type, int userId) throws SQLException {    
        try {
            // 1. Insert into category table
            String sqlCategory = "INSERT INTO category (Cat_name, Cat_type) VALUES (?, ?)";
            PreparedStatement pstmtCategory = conn.prepareStatement(sqlCategory, Statement.RETURN_GENERATED_KEYS);
            pstmtCategory.setString(1, categoryName);
            pstmtCategory.setString(2, (type ? "credit" : "debit"));
        
            int affectedRows = pstmtCategory.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Creating category failed, no rows affected.");
                throw new SQLException("Creating category failed, no rows affected.");
            }
            System.out.println("Done category.");

            // 2. Get the auto-generated Cat_ID
            int newCatId;
            ResultSet generatedKeys = pstmtCategory.getGeneratedKeys();
            if (generatedKeys.next()) {
                newCatId = generatedKeys.getInt(1);
            } else {
                System.out.println("Creating category failed, no ID obtained.");
                throw new SQLException("Creating category failed, no ID obtained.");
            }

            // 3. Insert into user_defined_categories table
            String sqlUserCategory = "INSERT INTO user_defined_categories (Cat_ID, User_ID) VALUES (?, ?)";
            PreparedStatement pstmtUserCategory = conn.prepareStatement(sqlUserCategory);
            pstmtUserCategory.setInt(1, newCatId);
            pstmtUserCategory.setInt(2, userId);
        
            pstmtUserCategory.executeUpdate();
            System.out.println("Done user defined categories.");
            return true;

        } catch (SQLException e) {
            return false;
        }
    }
    
    
    
    
}
