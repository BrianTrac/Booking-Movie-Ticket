/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import model.Customer;
import model.Session;

/**
 *
 * @author DELL
 */
public class ClientHandlerThread implements  Runnable {
    private final Socket clientSocket;
    private final Map<String, Object> seatLocks;
    private final Set<ClientHandlerThread> clients;
    private final ReentrantReadWriteLock dataLock;
    private final Map<String, Customer> customers;
    private final Map<String, Session> sessions;
    private final Map<String, Customer> loggedInCustomers;
    private PrintWriter out;
    private BufferedReader in;
    private Customer currentCustomer;
    
    public ClientHandlerThread(Socket socket, Map<String, Object> seatLocks, Set<ClientHandlerThread> clients, ReentrantReadWriteLock dataLock, Map<String, Customer> loggedInCustomers, Map<String, Session> sessions, Map<String, Customer> customers) {
        this.clientSocket = socket;
        this.seatLocks = seatLocks;
        this.clients = clients;
        this.dataLock = dataLock;
        this.loggedInCustomers = loggedInCustomers;
        this.sessions = sessions;
        this.customers = customers;
    }
    
    public void closeSocket() {
        try {
            if (currentCustomer != null) {
                loggedInCustomers.remove(currentCustomer.getName());
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
             
            this.out = out;
            String request;
            
            while ((request = in.readLine()) !=  null) {
                if (request.startsWith("BOOK_SEAT")) {
                    handleBookSeat(request);
                } 
                else if (request.startsWith("SIGNUP")) {
                    handleSignUp(request);
                } 
                else if (request.startsWith("LOGIN")) {
                    handleLogin(request);
                } 
                else {
                    //
                }
            }
      
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(this);
            // Print out client connection details
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort();
            System.out.println("Client disconnected: " + clientAddress + ":" + clientPort);
            closeSocket();
        }
    }
    
    private void handleSignUp(String request) {
        dataLock.writeLock().lock();
        try  {
            String[] parts = request.split("&");
            String username = parts[1];
            String password = parts[2];
            String phone = parts[3];
            
            System.out.println("Processing signup: " + request);
            
            List<Customer> customerList = DataStore.getCustomerList();
            if (DataStore.getCustomerByCustomerName(username) != null) {
                out.println("SIGNUP&USERNAME_EXISTS");
                out.flush();
                System.out.println("Username exists: " + username);
            } 
            else {
                String customerId = "customer" + (customerList.size() + 1);
                Customer newCustomer = new Customer(customerId, username, password, phone);
                customerList.add(newCustomer);
                DataStore.setCustomerList(customerList);
                SocketServerController.notifyServerDataObservers();
                
                out.println("SIGNUP&SUCCESS");
                out.flush();
                System.out.println("Signup successful for: " + username);
            }   
            
        } finally {
            dataLock.writeLock().unlock();
        }
    }
    
    private void handleLogin(String request) {

        String username;
        String password;
        
        System.out.println("Processing signup: " + request);

        dataLock.writeLock().lock(); // Acquire write lock for updating logged-in state
        try {
            String[] parts = request.split("&");
            username = parts[1];
            password = parts[2];
            
            Customer customer = DataStore.getCustomerByCustomerName(username);
            
            if (customer != null && customer.getPassword().equals(password)) {
                
                // Check if user is already logged in
                if (loggedInCustomers.containsKey(username)) {
                    out.println("LOGIN&ALREADY_LOGGED_IN");
                    out.flush();
                    System.out.println("Already logged in");
                    return;
                }
                
                // Update current session's logged-in state
                loggedInCustomers.put(customer.getName(), customer);
                currentCustomer = customer;
                out.println("LOGIN&SUCCESS&" + customer.getId());
                out.flush();
                System.out.println("Login success: " + customer.getId());
            } else {
                out.println("LOGIN&INVALID_CREDENTIALS");
                out.flush();
                System.out.println("Login invalid credentials");
            }
        } finally {
            dataLock.writeLock().unlock(); // Release write lock
        }
    }

    
    private void handleBookSeat(String request) {
        System.out.println(request);
        
        String[] parts = request.split("&");
        String customerId = parts[1];
        String sessionId = parts[2];
        String seatIds = parts[3];
        String[] seatIdArray = seatIds.split(", "); 
        double totalMoney = Double.parseDouble(parts[4]);
        List<String> bookSeats = Arrays.asList(parts[5].split(","));
        
        System.out.println("bookSeats: " + bookSeats);
        
        boolean allSeatsAvailable = true;
        List<String> unavailableSeats = new ArrayList<>();

        dataLock.readLock().lock();
        try {
            Session session = DataStore.getSessionBySessionId(sessionId);
            System.out.println("Before booked: " + session);
            if (session == null) {
                out.println("BOOK_SEAT&SESSION_NOT_FOUND");
                return;
            }
            for (String seatId : seatIdArray) {
                if (session.getBookedSeats().contains(seatId)) {
                    unavailableSeats.add(seatId);
                    allSeatsAvailable = false;
                }
            }
        } finally {
            dataLock.readLock().unlock();
        }
        
        if (allSeatsAvailable) {
            List<String> seatKeys = new ArrayList<>();
            for (String seatId : seatIdArray) {
                seatKeys.add(sessionId + "_" + seatId);
            }
            
            boolean bookingSuccessful = true;
            StringBuilder responseMessage = new StringBuilder();
            Session session = DataStore.getSessionBySessionId(sessionId);
            System.out.println("Before session: " + session);
            // used to rollback if failed when booking
            List<String> bookedSeats = new ArrayList<>();
            
            for (String seatKey : seatKeys) {
                seatLocks.computeIfAbsent(seatKey, k -> new Object());
                synchronized (seatLocks.get(seatKey)) {
                    dataLock.writeLock().lock();
                    try {
                        String seatId = seatKey.split("_")[1];
                        if (session.getBookedSeats().contains(seatId)) {
                            responseMessage.append(seatId).append(",");
                            bookingSuccessful = false;
                        }
                        else {
                            bookedSeats.add(seatId);
                            session.getBookedSeats().add(seatId);
                        }
                    } finally {
                        dataLock.writeLock().unlock();
                    }
                }
            }
            System.out.println("After session: " + session);
            System.out.println("bookedSeats: " + bookedSeats);
            
            if (bookingSuccessful) {
                dataLock.writeLock().lock();
                try {
                    DataStore.saveSessionList(sessionId, session.getBookedSeats());
                    DataStore.saveBookingList(customerId, sessionId, seatIds, totalMoney);
                    out.println("BOOK_SEAT&SUCCESS");
                    SocketServerController.notifyServerDataObservers();
                    broadcastUpdate("UPDATE_SEATMAP&" + sessionId);
                }
                finally {
                    dataLock.writeLock().unlock();
                }
            }
            else {
                // rollback in case of failure
                dataLock.writeLock().lock();
                try {
                    session.getBookedSeats().removeAll(bookedSeats);
                    out.println("BOOK_SEAT&FAILED&" + responseMessage);
                } 
                finally {
                    dataLock.writeLock().unlock();
                }
            }
        }  else {
            out.println("BOOK_SEAT&FAILED&" + String.join(",", unavailableSeats));
        }
        Session session = DataStore.getSessionBySessionId(sessionId);
        System.out.println("After booked: " + session);
    }
    
    private void broadcastUpdate(String request) {
        for (ClientHandlerThread client : clients) {
            if (client != this) {
                client.sendMessage(request);
            }
        }
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
}
