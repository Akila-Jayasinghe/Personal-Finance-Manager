/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package category_analyze;

import DataBase.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Akila Jayasinghe
 */
public class categoryAnalyzeCRUD {
    
    private final Connection conn = new databaseConnection().getConnection();
    
    public Map<String, Double> getCategoryTotals(int userId, int year, int month, String type) throws SQLException {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT c.Cat_name, SUM(t.Amount) as total " +
                    "FROM transaction_details t JOIN category c ON t.Cat_ID = c.Cat_ID " +
                    "WHERE t.User_ID = ? AND YEAR(t.Date) = ? " +
                    "AND MONTH(t.Date) = ? AND c.Cat_type = ? " +
                    "GROUP BY c.Cat_name";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            pstmt.setString(4, type);
        
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("Cat_name"), rs.getDouble("total"));
            }
        }
        return result;
    }
    
    
    public List<Object[]> getDataRows(int searchID, int year, int month, String type) throws SQLException {
        List<Object[]> dataRows = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.Trans_ID, c.Cat_name, t.Amount, t.`Date` " +
            "FROM transaction_details t, category c ");
        
        if (year > 0 || month > 0) {
            sql.append("WHERE ");
            if (year > 0) {
                sql.append("YEAR(t.`Date`) = ? ");
                if (month > 0) {
                    sql.append("AND MONTH(t.`Date`) = ? ");
                }
            } else {
                sql.append("MONTH(t.`Date`) = ? ");
            }
            sql.append("AND t.Cat_ID = c.Cat_ID AND t.User_ID = ? AND c.Cat_type = ?");
        }
        sql.append("ORDER BY t.`Date` DESC");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (year > 0) {
                pstmt.setInt(paramIndex++, year);
            }
            if (month > 0) {
                pstmt.setInt(paramIndex++, month);
            }
            pstmt.setInt(paramIndex++, searchID);
            pstmt.setString(paramIndex, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("Trans_ID");
                    row[1] = rs.getString("Cat_name");
                    row[2] = rs.getDouble("Amount");
                    row[3] = rs.getDate("Date");
                    dataRows.add(row);
                }
            }
        }
        return dataRows;
    }
    
}
