/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package category_analyze;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class categoryAnalyzeBusinessLogic {
    
    private final categoryAnalyzeCRUD databaseOperations = new categoryAnalyzeCRUD();
    
    public void initializeTableWithSampleData(javax.swing.JTable dataTable) {
        // Sample column names
        String[] columns = {"ID", "Name", "Value", "Date"};
    
        // Sample data
//        Object[][] data = {
//            {1, "Product A", 1250.50, "2023-01-15"},
//            {2, "Product B", 3200.75, "2023-02-20"},
//            {3, "Service X", 500.00, "2023-03-10"}
//        };
//    
//        dataTable.setModel(new DefaultTableModel(data, columns));
    }

    public void initializeYearSpinner(javax.swing.JSpinner yearSpinner, javax.swing.JComboBox<String> monthComboBox) {
        yearSpinner.setModel(new SpinnerNumberModel(Year.now().getValue(), 0, 9999, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "####");
        yearSpinner.setEditor(editor);
        yearSpinner.setValue(Year.now().getValue());
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
    } 
    
    private int getMonth(javax.swing.JComboBox<String> monthComboBox) {
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        if (selectedMonth.equalsIgnoreCase("January")) {
            return 1;
        } else if (selectedMonth.equalsIgnoreCase("February")) {
            return 2;
        } else if (selectedMonth.equalsIgnoreCase("March")) {
            return 3;
        } else if (selectedMonth.equalsIgnoreCase("April")) {
            return 4;
        } else if (selectedMonth.equalsIgnoreCase("May")) {
            return 5;
        } else if (selectedMonth.equalsIgnoreCase("June")) {
            return 6;
        } else if (selectedMonth.equalsIgnoreCase("July")) {
            return 7;
        } else if (selectedMonth.equalsIgnoreCase("August")) {
            return 8;
        } else if (selectedMonth.equalsIgnoreCase("September")) {
            return 9;
        } else if (selectedMonth.equalsIgnoreCase("October")) {
            return 10;
        } else if (selectedMonth.equalsIgnoreCase("November")) {
            return 11;
        } else if (selectedMonth.equalsIgnoreCase("December")) {
            return 12;
        }
        return 0;
    }
    
    private int getYear(javax.swing.JSpinner yearSpinner) {
        return ((Number) yearSpinner.getValue()).intValue();
    }
    
    public void generatePieChart(
            javax.swing.JComboBox<String> monthComboBox, 
            javax.swing.JSpinner yearSpinner,
            javax.swing.JComboBox<String> typeComboBox,
            sessionCredential sessionData,
            javax.swing.JPanel chartPanel,
            categoryAnalyze parentInterface
    ) throws SQLException {
        
        try {
            // 1. Get parameters
            int month = getMonth(monthComboBox);
            if (month == 0) { return; } 
            int year = getYear(yearSpinner);
            String type = typeComboBox.getSelectedItem().equals("Expenses") ? "debit" : "credit";

            // 3. Get data
            Map<String, Double> categoryData = databaseOperations.getCategoryTotals(
                sessionData.getUserID(),
                year,
                month,
                type
            );

            // 4. Debug print data
            categoryData.forEach((k,v) -> System.out.println(k + ": " + v));

            if (categoryData.isEmpty()) {
                JOptionPane.showMessageDialog(parentInterface,
                    "No data found for selected criteria",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 5. Create dataset
            DefaultPieDataset dataset = new DefaultPieDataset();
            categoryData.forEach(dataset::setValue);

            // 6. Create chart with enhanced styling
            String title = type.equals("debit") ? "Expenses" : "Income";
            JFreeChart chart = ChartFactory.createPieChart(
                title + " - " + ((String) monthComboBox.getSelectedItem()) + ", " + year,
                dataset,
                true, true, false
            );

            // Configure the plot to show percentages
            PiePlot plot = (PiePlot) chart.getPlot();  // Note: Correct capitalization of PiePlot

            plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",  // Label format
                new DecimalFormat("0.00"),  // Value format - corrected class name
                new DecimalFormat("0.0%")   // Percentage format - corrected pattern
            ));
        
            // Make labels more readable
            plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200)); // semi-transparent white
            plot.setLabelShadowPaint(null); // remove shadow for cleaner look
            plot.setLabelOutlinePaint(null); // remove outline
            plot.setLabelLinkMargin(0.1); // adjust label connection line margin

            // 7. Set a better color palette
            plot.setSectionPaint(0, new Color(79, 129, 189));  // blue
            plot.setSectionPaint(1, new Color(192, 80, 77));   // red
            plot.setSectionPaint(2, new Color(155, 187, 89));  // green
            plot.setSectionPaint(3, new Color(128, 100, 162)); // purple
            plot.setSectionPaint(4, new Color(247, 150, 70));  // orange
            plot.setSectionPaint(5, new Color(75, 172, 198));  // light blue
            plot.setSectionPaint(6, new Color(169, 169, 169)); // gray
            // Add more colors if you have more categories

            // Make the chart transparent
            plot.setBackgroundPaint(null);
            chart.setBackgroundPaint(null);

            // 8. Create and configure chart panel
            ChartPanel chPanel = new ChartPanel(chart) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(600, 400);
                }
            };
            chPanel.setBackground(Color.WHITE);

            // 9. Clear and update UI
            chartPanel.removeAll();
            chartPanel.add(chPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();

        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(parentInterface,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

    }
    
    
    public void generateTable(
            javax.swing.JTable dataTable,
            sessionCredential sessionData,
            javax.swing.JComboBox<String> monthComboBox, 
            javax.swing.JSpinner yearSpinner,
            javax.swing.JComboBox<String> typeComboBox,
            categoryAnalyze parentInterface) {
        
        // Table generation
        try {
            // 1. Get data from database/service
            List<Object[]> dbData = databaseOperations.getDataRows(
                    sessionData.getUserID(), 
                    getYear(yearSpinner), 
                    getMonth(monthComboBox),
                    typeComboBox.getSelectedItem().equals("Expenses") ? "debit" : "credit"
            );
        
            // 2. Prepare column format
            String[] columns = {"ID", "Category", "Amount", "Date"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd - MMM - yyyy");
        
            for (Object[] row : dbData) {
                Object[] formattedRow = Arrays.copyOf(row, row.length);
            
                // Format amount (assuming column index 2 for Amount)
                if (row[2] != null) {
                    formattedRow[2] = String.format("LKR %,.2f", row[2]);
                }
            
                // Format date (assuming column index 3 for Date)
                if (row[3] instanceof Date) {
                    formattedRow[3] = dateFormat.format((Date) row[3]);
                }
            
                model.addRow(formattedRow);
            }
    
            // 4. Set model and configure table
            dataTable.setModel(model);
        
            // 4. Adjust column widths
            dataTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            dataTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Category
            dataTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
            dataTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentInterface,
                "Error loading data: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } 
    }
    
}
