/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package allTransactions;

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
public class allTransactionsCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public List<Object[]> getTransactionsByDateRange(int userId, java.util.Date startDate, java.util.Date endDate, boolean income, boolean expense) throws SQLException {
        
        List<Object[]> dataRows = new ArrayList<>();
        String sql = "SELECT t.Trans_ID, c.Cat_name, c.Cat_type, t.Amount, t.Date " +
                 "FROM transaction_details t, category c " +
                 "WHERE t.Cat_ID=c.Cat_ID AND t.User_ID = ? AND t.Date BETWEEN ? AND ?";
        if (income ^ expense) {
            sql += " AND c.Cat_type = ?";
        }
        sql += " ORDER BY t.Date DESC";        

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, new java.sql.Date(startDate.getTime()));
            stmt.setDate(3, new java.sql.Date(endDate.getTime()));
            
            if (income ^ expense) {
                String type = income ? "credit" : "debit";
                stmt.setString(4, type);
            }
        
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
    
}
