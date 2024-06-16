/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Client.controller.SocketClientController;
import javax.swing.SwingUtilities;

/**
 *
 * @author DELL
 */
public class Main {
    public static final String ADDRESS = "localhost";
    public static final int PORT = 12345;
    private static SocketClientController socketClientController;
    
    public static SocketClientController getSocketClientController() {
        if (socketClientController == null) {
            socketClientController = new SocketClientController(ADDRESS, PORT);
            if (!socketClientController.connectToServer()) {
                throw new RuntimeException("Failed to connect to server");
            }
        }
        return socketClientController;
    }
    
    public static void main(String[] args) {      
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            getSocketClientController().addObserver(loginFrame);
            loginFrame.setVisible(true);
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            socketClientController.disconnectFromServer();
        }));
   
    }
}
