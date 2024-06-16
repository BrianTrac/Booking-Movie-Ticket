/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Utility.DataStore;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Customer;

/**
 *
 * @author DELL
 */
public class MainScreenClientFrame extends JFrame {
    public static String customerId;
    
    public MainScreenClientFrame(String customerId) {
        this.customerId = customerId; 
        DataStore.loadData();
        Customer customer = DataStore.getCustomerByCustomerId(customerId);
        String customerName = customer.getName();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        MovieClientPanel movieClientPanel = new MovieClientPanel();
        HistoryClientPanel historyClientPanel = new HistoryClientPanel();
        tabbedPane.addTab("Movies", movieClientPanel);
        tabbedPane.addTab("History", historyClientPanel);
        
        tabbedPane.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == 0) {
                    movieClientPanel.reload();
                } else if (selectedIndex == 1) {
                    historyClientPanel.reload();
                }
            }
        });
        
        
        
        add(tabbedPane);
        setTitle("Client: " + customerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreenClientFrame(customerId));
    }
}
