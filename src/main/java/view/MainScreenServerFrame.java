/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.DataStore;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author DELL
 */
public class MainScreenServerFrame extends JFrame {
    
    public MainScreenServerFrame() {
        DataStore.loadData();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Movies", new MovieServerPanel());
        tabbedPane.addTab("Customers", new CustomerServerPanel());
        
        add(tabbedPane);
        setTitle("Main Server Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreenServerFrame());
    }
}
