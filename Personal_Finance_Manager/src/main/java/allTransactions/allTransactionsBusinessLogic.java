/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package allTransactions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import session.sessionCredential;

/**
 *
 * @author Akila Jayasinghe
 */
public class allTransactionsBusinessLogic {
    
    private final allTransactionsCRUD databaseOperations = new allTransactionsCRUD();
    
    public void configureDateSpinner(javax.swing.JSpinner from, javax.swing.JSpinner to) {
        // Set date to beginning
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SpinnerDateModel dateModel = new SpinnerDateModel(calendar.getTime(), null, null, Calendar.DAY_OF_MONTH);
        from.setModel(dateModel);
        JSpinner.DateEditor dateEditor1 = new JSpinner.DateEditor(from, "dd - MMMM - yyyy");
        from.setEditor(dateEditor1);
        
        // Set date to ending
        SpinnerDateModel dateMode2 = new SpinnerDateModel();
        to.setModel(dateMode2);
        JSpinner.DateEditor dateEditor2 = new JSpinner.DateEditor(to, "dd - MMMM - yyyy");
        to.setEditor(dateEditor2);  
    }
    
    
    
    
    public void incomeExpenseBarChart(double incomeTotal, double expenseTotal, javax.swing.JPanel chartPanel) {
        // 1. Create dataset with absolute values
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Math.abs(incomeTotal), "Amount", "Income");
        dataset.addValue(Math.abs(expenseTotal), "Amount", "Expenses");

        // 2. Initialize or reuse ChartPanel
        ChartPanel chartPanelTemp;
        if (chartPanel instanceof ChartPanel) {
            chartPanelTemp = (ChartPanel) chartPanel;
        } else {
            chartPanelTemp = new ChartPanel(null) {
                @Override
                public Dimension getPreferredSize() {
                    return chartPanel.getSize();
                }
            };
            chartPanel.removeAll();
            chartPanel.setLayout(new BorderLayout());
            chartPanel.add(chartPanelTemp, BorderLayout.CENTER);
        }

        // 3. Create new chart or update existing one
        JFreeChart chart;
        if (chartPanelTemp.getChart() == null) {
            // Create new chart
            chart = ChartFactory.createBarChart(
                null,                   // No title
                null,                   // No x-axis label
                "Amount (LKR)",          // Y-axis label
                dataset,                 // Data
                PlotOrientation.VERTICAL,
                false,                   // No legend
                true,                    // Tooltips
                false                    // No URLs
            );

            // Configure plot
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            // Configure renderer
            BarRenderer renderer = new BarRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    String category = (String) dataset.getColumnKey(column);
                    if ("Income".equals(category)) {
                        return new Color(59, 134, 61); // Green
                    } else if ("Expenses".equals(category)) {
                        return new Color(184, 56, 50); // Red
                    } else {
                        return super.getItemPaint(row, column);
                    }
                }
            };
            renderer.setDrawBarOutline(false);
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter()); // Flat color

            // Show item labels (amounts) above bars
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
            renderer.setDefaultItemLabelPaint(Color.BLACK);

            plot.setRenderer(renderer);

            // Configure axes
            plot.getDomainAxis().setCategoryMargin(0.1);
            plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        } else {
            // Update existing chart
            chart = chartPanelTemp.getChart();
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setDataset(dataset);

            // Reapply renderer to apply colors and labels
            BarRenderer renderer = new BarRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    String category = (String) dataset.getColumnKey(column);
                    if ("Income".equals(category)) {
                        return new Color(59, 134, 61); // Green
                    } else if ("Expenses".equals(category)) {
                        return new Color(184, 56, 50); // Red
                    } else {
                        return super.getItemPaint(row, column);
                    }
                }
            };
            renderer.setDrawBarOutline(false);
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter()); // Flat color

            // Show item labels (amounts) above bars
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
            renderer.setDefaultItemLabelPaint(Color.BLACK);

            plot.setRenderer(renderer);
        }

        // 4. Update the chart panel
        chartPanelTemp.setChart(chart);

        // 5. Ensure proper sizing
        chartPanelTemp.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanelTemp.setMaximumDrawHeight(Integer.MAX_VALUE);

        // 6. Refresh the panel
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    
    
    
    public void incomeExpenseTable(
            javax.swing.JCheckBox jCheckBox1,
            javax.swing.JCheckBox jCheckBox2,
            javax.swing.JSpinner jSpinner1,
            javax.swing.JSpinner jSpinner2,
            javax.swing.JTable dataTable,
            sessionCredential sessionData,
            allTransactions parentInterface) {
        
        // 1. Validate checkbox selection
        if ((!jCheckBox1.isSelected()) && (!jCheckBox2.isSelected())) {
            JOptionPane.showMessageDialog(parentInterface,
                "Please select at least one transaction type (Income or Expense).",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validate date range
        java.util.Date StartDate = (java.util.Date) jSpinner1.getValue();
        java.util.Date EndDate = (java.util.Date) jSpinner2.getValue();
    
        if (StartDate == null || EndDate == null) {
            JOptionPane.showMessageDialog(parentInterface,
                "Please select valid dates.",
                "Invalid Dates",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (StartDate.after(EndDate)) {
            JOptionPane.showMessageDialog(parentInterface,
                "End date must be after start date.",
                "Invalid Date Range",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 3. Get data from database
            List<Object[]> dbData = databaseOperations.getTransactionsByDateRange(
                sessionData.getUserID(),
                StartDate,
                EndDate,
                jCheckBox1.isSelected(),
                jCheckBox2.isSelected()
            );

            // 4. Prepare column names
            String[] columns = {"ID", "Category", "Type", "Amount", "Date"};

            // 5. Create model with formatted data
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 3 ? Double.class : Object.class;
                }
            };

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

            for (Object[] row : dbData) {
                Object[] formattedRow = Arrays.copyOf(row, row.length);
                if (row[3] != null) {
                    formattedRow[3] = String.format("LKR %,.2f", row[3]);
                }
                if (row[4] instanceof Date) {
                    formattedRow[4] = dateFormat.format((Date) row[4]);
                }
                model.addRow(formattedRow);
            }

            // 6. Set model and configure table
            dataTable.setModel(model);

            // Configure column widths
            TableColumnModel columnModel = dataTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(50);   // ID
            columnModel.getColumn(1).setPreferredWidth(150);  // Category
            columnModel.getColumn(2).setPreferredWidth(80);   // Type
            columnModel.getColumn(3).setPreferredWidth(100);  // Amount
            columnModel.getColumn(4).setPreferredWidth(100);  // Date

            // Custom renderer for all columns including Amount
            DefaultTableCellRenderer coloredRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, 
                            hasFocus, row, column);

                    String type = table.getModel().getValueAt(row, 2).toString();

                    if (!isSelected) {
                        if (type.equalsIgnoreCase("debit")) {
                            c.setBackground(new Color(255, 200, 200));  // Light red
                        } else if (type.equalsIgnoreCase("credit")) {
                            c.setBackground(new Color(200, 255, 200));  // Light green
                        } else {
                            c.setBackground(table.getBackground());
                        }
                    }

                    // Special handling for Amount column
                    if (column == 3) {
                        ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                    } else {
                        ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                    }

                    return c;
                }
            };

            // Apply the colored renderer to all columns
            for (int i = 0; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(coloredRenderer);
            }

            // Special right-align renderer just for Amount column (keeps coloring)
            DefaultTableCellRenderer rightAlignedColoredRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = coloredRenderer.getTableCellRendererComponent(table, value, 
                            isSelected, hasFocus, row, column);
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                    return c;
                }
            };
            
            dataTable.getColumnModel().getColumn(3).setCellRenderer(rightAlignedColoredRenderer);
        
        } catch (SQLException ex) {
            Logger.getLogger(allTransactions.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(parentInterface,
                "Error retrieving transactions: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    public double getTransactionTotal(
            javax.swing.JSpinner fromDateSpinner, 
            javax.swing.JSpinner toDateSpinner, 
            boolean type,
            sessionCredential sessionData) {
    
        // 1. Validate and get dates
        java.util.Date startDate = (java.util.Date) fromDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) toDateSpinner.getValue();
    
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        try {
            List<Object[]> dbData = databaseOperations.getTransactionsByDateRange(
                sessionData.getUserID(),
                startDate,
                endDate,
                type,    
                !type    
            );

            // 3. Calculate total
            double total = 0.0;
            for (Object[] row : dbData) {
                if (row[3] instanceof Number number) {
                    total += number.doubleValue();
                }
            }

            return total;

        } catch (SQLException ex) {
            Logger.getLogger(allTransactions.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }
}
