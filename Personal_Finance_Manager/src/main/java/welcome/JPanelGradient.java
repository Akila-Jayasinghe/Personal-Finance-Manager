/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package welcome;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Akila Jayasinghe
 */
class JPanelGradient extends JPanel {
        
    @Override
    protected void paintComponent(Graphics g) {
            
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
            
        Color color1 = new Color(245, 138, 255);
        Color color2 = new Color(194, 209, 255);
        GradientPaint gp = new GradientPaint(0, 0, color1, 180, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);           
        
    }
    
}
