/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.DataStore;
import controller.SocketServerController;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author DELL
 */
public class MainScreenServerFrame extends JFrame {
    private SocketServerController socketServerController;
    
    public MainScreenServerFrame() {
        DataStore.loadData();
        JTabbedPane tabbedPane = new JTabbedPane();
        CustomerServerPanel customerServerPanel = new CustomerServerPanel();
        MovieServerPanel movieServerPanel = new MovieServerPanel();
        
        tabbedPane.addTab("Movies", movieServerPanel);
        tabbedPane.addTab("Customers", customerServerPanel);
        
        add(tabbedPane);
        setTitle("Main Server Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        
        socketServerController = new SocketServerController();
        socketServerController.addServerDataObserver(customerServerPanel);
        socketServerController.addServerDataObserver(movieServerPanel);
        
        new Thread(socketServerController).start();
        
        // Add window close listener to stop server when closing window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopServerAndExit();
            }
        });
        

    }
    
    void stopServerAndExit() {
        socketServerController.stopServer();
        dispose(); // Close the JFrame
        System.exit(0); // Exit the application
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreenServerFrame());
    }
}
