/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author DELL
 */
public class SeatMapClient extends JFrame {
    private final String id;
    private JPanel mainPanel;
    private JPanel containerPanel;
    
    public SeatMapClient(String id) {
        this.id = id;
        initComponents();
    }
    
    private void initComponents() {
        containerPanel = new JPanel(new BorderLayout());
        
        mainPanel = new SeatGridClientPanel(id, true);    
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        
        add(containerPanel);
        setTitle("Seat Map Client");
        setSize(800, 645);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static String calSeatId(String text) {
        // Extract row and column parts
        char rowLetter = text.charAt(0); 
        int columnNumber = Integer.parseInt(text.substring(1)); 
        int rowNumber = rowLetter - 'A';

        // Calculate seat ID (rows start at 0, columns start at 1)
        int seatId = rowNumber * 8 + columnNumber;

        // Return seat ID in the desired format
        return "seat" + seatId;
    }
}
