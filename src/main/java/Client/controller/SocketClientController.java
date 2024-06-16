package Client.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class SocketClientController implements Subject {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ResponseListener responseListener;
    private volatile boolean running;
    private List<Observer> observers;
    
    public SocketClientController(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventType, Object result) {
        for (Observer observer : observers) {
            observer.update(eventType, result);
        }
    }
    
    public boolean connectToServer() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseListener = new ResponseListener();
            new Thread(responseListener).start();
            running = true;
            return true;
            
        } catch (IOException ex) {
            Logger.getLogger(SocketClientController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void disconnectFromServer() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendRequest(String request) {
        if (out != null) {
            out.println(request);
            out.flush();
            System.out.println("Sent request: " + request);
        }
    }

    public void login(String username, String password) {
        sendRequest("LOGIN&" + username + "&" + password);
    }
    
    public void signUp(String username, String password, String phone) {
        sendRequest("SIGNUP&" + username + "&" + password + "&" + phone);
    }
    
    public void bookSeat(String customerId, String sessionId, String seatId, double totalMoney, List<String> bookedSeatList) {
        // Convert the list of booked seats to a comma-separated string
        StringBuilder bookedSeatsBuilder = new StringBuilder();
        for (String seat : bookedSeatList) {
            bookedSeatsBuilder.append(seat).append(",");
        }

        // Remove the last comma if the list is not empty
        if (bookedSeatsBuilder.length() > 0) {
            bookedSeatsBuilder.setLength(bookedSeatsBuilder.length() - 1);
        }

        // Format the request string
        String request = String.format("BOOK_SEAT&%s&%s&%s&%.2f&%s",
                customerId, sessionId, seatId, totalMoney, bookedSeatsBuilder.toString());

        // Send the request to the server
        sendRequest(request);
    }
    
    
    private class ResponseListener implements Runnable {

        @Override
        public void run() {
            try {
                String response;
                while (running && (response = in.readLine()) != null) {
                    System.out.println("Received response: " + response);
                    handleResponse(response);
                }
            } catch (IOException ex) {
                if (running) {
                    Logger.getLogger(SocketClientController.class.getName()).log(Level.SEVERE, null, ex);
                } 
            } finally {
                    System.out.println("ResponseListener terminated.");
            }
        }
        
        private void handleResponse(String response) {
            if (response.startsWith("LOGIN")) {
                handleLoginResponse(response);
                
            }
            else if (response.startsWith("SIGNUP")) {
                handleSignUpResponse(response);
            }
            else if (response.startsWith("BOOK_SEAT")) {
                handleBookSeatResponse(response);
            }
            else if (response.startsWith("UPDATE_SEATMAP")) {
                handleUpdateSeatMapResponse(response);
            }
            else {
                // Handle unknown or unexpected responses
                System.out.println("Unexpected response: " + response);
            }
            
        }
        
        private void handleLoginResponse(String response) {
            String[] parts = response.split("&");
            String status = parts[1];
            String result = status;
            
            if (parts.length < 3) {
                System.out.println("Invalid login response format");
            }
            else { 
                switch (status) {
                    case "SUCCESS":
                        String customerId = parts[2];
                        result += "&" + customerId;
                        System.out.println("Login successful");
                        break;
                    case "ALREADY_LOGGED_IN":
                        System.out.println("User already logged in");
                        break;
                    case "INVALID_CREDENTIALS":
                        System.out.println("Invalid username or password");
                        break;
                    default:
                        System.out.println("Unknown login response: " + status);
                        break;
                }
            }
            
            notifyObservers("login", result);
        }

        private void handleSignUpResponse(String response) {
            String[] parts = response.split("&");
            boolean success = false;
            
            if (parts.length < 2) {
                System.out.println("Invalid signup response format");
            }
            else {
                String status = parts[1];
            
                switch (status) {
                    case "SUCCESS":
                        System.out.println("Sign up successful");
                        success = true;
                        break;
                    case "USERNAME_EXISTS":
                        System.out.println("Username already exists");
                        break;
                    default:
                        System.out.println("Unknown signup response: " + status);
                        break;
                }
            }
            
            notifyObservers("signUp", success);
        }
        
        private void handleBookSeatResponse(String response) {
            String[] parts = response.split("&");
            
            String status = parts[1];
            
            switch (status) {
                case "SUCCESS":
                    System.out.println(response);
                    break;
                case "FAILED":
                    System.out.println(response);
                    break;
                default:
                    System.out.println("Unknown book seat response: " + status);
            }
            
            notifyObservers("bookSeat", response);
        }
        
        private void handleUpdateSeatMapResponse(String response) {
            String[] parts = response.split("&");
            notifyObservers("updateSeatMap", response);
        }
    }
      
     
}
