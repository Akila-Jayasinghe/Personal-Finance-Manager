/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashboard;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 * @author Akila Jayasinghe
 */
public class dashboardCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public double monthIncome(String userName) throws SQLException {
        String sql = """
            SELECT SUM(t.Amount) 
            FROM transaction_details t
            JOIN users u ON t.User_ID = u.User_ID
            JOIN category c ON t.Cat_ID = c.Cat_ID
            WHERE u.User_name = ? 
            AND c.Cat_type = 'credit'
            AND t.Date BETWEEN ? AND ?
            """;
        
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            pstmt.setDate(2, Date.valueOf(firstOfMonth));
            pstmt.setDate(3, Date.valueOf(today));
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
    
    public double monthExpence(String userName) throws SQLException {
        String sql = """
            SELECT SUM(t.Amount) 
            FROM transaction_details t
            JOIN users u ON t.User_ID = u.User_ID
            JOIN category c ON t.Cat_ID = c.Cat_ID
            WHERE u.User_name = ? 
            AND c.Cat_type = 'debit'
            AND t.Date BETWEEN ? AND ?
            """;
        
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName.toLowerCase());
            pstmt.setDate(2, Date.valueOf(firstOfMonth));
            pstmt.setDate(3, Date.valueOf(today));
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
    
    public double monthBalance(String userName) throws SQLException {
        double income = monthIncome(userName);
        double expence = monthExpence(userName);
        return (income-expence);
    }
    
}
