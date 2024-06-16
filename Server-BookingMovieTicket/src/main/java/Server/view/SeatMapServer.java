/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SeatMapServer extends JFrame {
    private final String id;
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JScrollPane scrollPane;
    private JPanel containerPanel;
    
    public SeatMapServer(String id) {
        this.id = id;
        initComponents();
    }
    
    private void initComponents() {
        containerPanel = new JPanel(new BorderLayout());
        
        mainPanel = new SeatGridServerPanel(id, false);
        infoPanel = new InfoServerPanel(id);
    
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        containerPanel.add(infoPanel, BorderLayout.SOUTH);
    
        scrollPane = new JScrollPane(containerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
        setTitle("Seat Map");
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
    
    public static String getSeatLabel(String seatId) {
        // Extract the seat number from the seat ID
        int seatNumber = Integer.parseInt(seatId.substring(4)); // "seat3" -> 3

        // Calculate the row number and column number
        int rowNumber = (seatNumber - 1) / 8;
        int columnNumber = (seatNumber - 1) % 8 + 1; // Column number starts at 1

        // Convert the row number back to a row letter
        char rowLetter = (char) ('A' + rowNumber);

        // Return the seat label in the desired format
        return rowLetter + String.valueOf(columnNumber);
    }
    
}
