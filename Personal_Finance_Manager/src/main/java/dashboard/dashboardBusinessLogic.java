/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashboard;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class dashboardBusinessLogic {
    
    private final dashboardCRUD databaseOperations = new dashboardCRUD();
    
    public void writeSummarry(
            javax.swing.JLabel heading,
            javax.swing.JLabel incomeMonth,
            javax.swing.JLabel balanceMonth,
            javax.swing.JLabel expenseMonth,
            javax.swing.JLabel income,
            javax.swing.JLabel balance,
            javax.swing.JLabel expense,
            sessionCredential sessionData) {
        
        // Welcome text
        heading.setText(String.format("Welcome Back, %s!", sessionData.getNickName()));
        
        // Set current month and year
        String currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        incomeMonth.setText(currentMonthYear);
        balanceMonth.setText(currentMonthYear);
        expenseMonth.setText(currentMonthYear);
        
        // Set Calculation for current month
        try {
            income.setText(String.format("%,.2f LKR", databaseOperations.monthIncome(sessionData.getUserName())));
            balance.setText(String.format("%,.2f LKR", databaseOperations.monthBalance(sessionData.getUserName())));
            expense.setText(String.format("%,.2f LKR", databaseOperations.monthExpence(sessionData.getUserName())));
        } catch (SQLException e) {
            income.setText("0000.00 LKR");
            balance.setText("0000.00 LKR");
            expense.setText("0000.00 LKR");
        }
        
    }
    
}
