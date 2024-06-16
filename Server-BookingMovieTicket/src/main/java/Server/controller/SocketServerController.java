/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.controller;

import Utility.DataStore;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Customer;
import model.Session;

/**
 *
 * @author DELL
 */
public class SocketServerController implements Runnable {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, Object> seatLocks = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock dataLock = new ReentrantReadWriteLock();
    private final Set<ClientHandlerThread> clients = ConcurrentHashMap.newKeySet();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Customer> loggedInCustomers = new ConcurrentHashMap<>();
    private static final List<ServerDataObserver> serverDataObservers = new ArrayList<>();
    private boolean running;
    
    public SocketServerController() {
    //       initializeCustomers();
    //       initializeSessions();
    }

    public void startServer() {
        running = true;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);
            while(running) {
                Socket socket = serverSocket.accept();
                
                 // Print out client connection details
                String clientAddress = socket.getInetAddress().getHostAddress();
                int clientPort = socket.getPort();
                System.out.println("Client connected: " + clientAddress + ":" + clientPort);

                
                // Submit client handling task to the executor service
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(socket, seatLocks, clients, dataLock, loggedInCustomers, sessions, customers);
                clients.add(clientHandlerThread);
                executorService.execute(clientHandlerThread);
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketServerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            shutdown();
        }
        
    }
    
    public void stopServer() {
        running = false;
        shutdown();
    }
    
    private void shutdown() {
        saveDataOnShutDown();
        executorService.shutdown(); // Shutdown the executor service
        try {
            // Wait for all threads to finish
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logger.getLogger(SocketServerController.class.getName()).log(Level.SEVERE, null, e);
        }

        // Close all client sockets
        for (ClientHandlerThread client : clients) {
            client.closeSocket();
        }

        clients.clear(); // Clear the client set
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        startServer();
    }
    
    private void saveDataOnShutDown() {
    //    DataStore.setCustomerList(new ArrayList<>(customers.values()));
    //    DataStore.setSessionList(new ArrayList<>(sessions.values()));
        DataStore.saveData();
    }
    
    public void addServerDataObserver(ServerDataObserver observer) {
        serverDataObservers.add(observer);
    }

    public static void notifyServerDataObservers() {
        for (ServerDataObserver observer : serverDataObservers) {
            observer.updateServerData();
        }
    }
    
        
    private void initializeSessions() {
        List<Session> sessionList = DataStore.getSessionList();
        for (Session session : sessionList) {
            sessions.put(session.getId(), session);
        }
    }
    
    private void initializeCustomers() {
        List<Customer> customerList = DataStore.getCustomerList();
        for (Customer customer : customerList) {
            customers.put(customer.getId(), customer);
        }
    }
}
