/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Utility.DataStore;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.Customer;
import Client.controller.Observer;

/**
 *
 * @author DELL
 */
public class SeatMapClient extends JFrame {
    private final String id;
    private JPanel mainPanel;
    private JPanel containerPanel;
    private Observer observer;
    
    public SeatMapClient(String id) {
        this.id = id;
        initComponents();
    }
    
    private void initComponents() {
        containerPanel = new JPanel(new BorderLayout());
        
        mainPanel = new SeatGridClientPanel(id, true);    
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        
        observer = (Observer) mainPanel;
        Main.getSocketClientController().addObserver(observer);
        
        // Get Customer Name
        Customer customer = DataStore.getCustomerByCustomerId(MainScreenClientFrame.customerId);
        String customerName = customer.getName();
        
        add(containerPanel);
        setTitle("Client Seat Map: " + customerName);
        setSize(800, 645);
        setLocationRelativeTo(null);
        setVisible(true);
        
         // Add a window listener to handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.getSocketClientController().removeObserver(observer);
                dispose();
            }
        });
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
