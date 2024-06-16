/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Booking;
import model.Customer;
import model.Movie;
import model.Seat;
import model.Session;


public class DataStore {
    private static final String FILENAME = "src/main/java/resources/bookingMovie.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static List<Customer> customerList = Collections.emptyList();
    private static List<Movie> movieList = Collections.emptyList();
    private static List<Seat> seatList = Collections.emptyList();
    private static List<Session> sessionList = Collections.emptyList();
    private static List<Booking> bookingList = Collections.emptyList();
    
    public static void loadData() {
        try {
            File dataFile = new File(FILENAME);
            
            if (dataFile.exists()) {
                Map<String, List<?>> dataMap = objectMapper.readValue(dataFile, new TypeReference<Map<String, List<?>>>() {});
                 
                customerList = objectMapper.convertValue(dataMap.get("customers"), TypeFactory.defaultInstance().constructCollectionType(List.class, Customer.class));
                movieList = objectMapper.convertValue(dataMap.get("movies"), TypeFactory.defaultInstance().constructCollectionType(List.class, Movie.class));
                seatList = objectMapper.convertValue(dataMap.get("seats"), TypeFactory.defaultInstance().constructCollectionType(List.class, Seat.class));
                sessionList = objectMapper.convertValue(dataMap.get("sessions"), TypeFactory.defaultInstance().constructCollectionType(List.class, Session.class));
                bookingList = objectMapper.convertValue(dataMap.get("bookings"), TypeFactory.defaultInstance().constructCollectionType(List.class, Booking.class));
               
            }
            else {
                System.out.println(dataFile + " does not exists");
            }
        } catch (IOException ex) {
            System.out.println("Error loading data from file: " + ex.getMessage());
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveData() {
        try {
            Map<String, List<?>> dataMap = Map.of(
                    "customers", customerList,
                    "movies", movieList,
                    "seats", seatList,
                    "sessions", sessionList,
                    "bookings", bookingList
            );
            
            // Enable pretty printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(FILENAME), dataMap);
        } catch (IOException ex) {
            System.err.println("Error saving data to file: " + ex.getMessage());
        }
    }
    
    public static void saveCustomerList() {
        try {
            File dataFile = new File(FILENAME);
            Map<String, List<?>> dataMap = objectMapper.readValue(dataFile, new TypeReference<Map<String, List<?>>>() {});
            
            //Update the customers list
            dataMap.put("customers", customerList);
            
            // Enable pretty printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(dataFile, dataMap);
            
        } catch (IOException ex) {
            System.err.println("Error saving customers to file: " + ex.getMessage());
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean saveBookingList(String customerId, String sessionId, String seatId, double price) {
        Booking booking = new Booking( "booking" + (bookingList.size() + 1), customerId, sessionId, seatId, price);
        bookingList.add(booking);
        
        try {
            File dataFile = new File(FILENAME);
            Map<String, List<?>> dataMap = objectMapper.readValue(dataFile, new TypeReference<Map<String, List<?>>>() {});
            
            //Update the bookings list
            dataMap.put("bookings", bookingList);
            
            // Enable pretty printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(dataFile, dataMap);
            
        } catch (IOException ex) {
            System.err.println("Error saving bookings to file: " + ex.getMessage());
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    public static boolean saveSessionList(String sessionId, List<String> bookedSeatList) {
        boolean flag = false;

    //    List<String> newBookedSeats = new ArrayList<>(); // List to collect new booked seats

        synchronized (sessionList) { // Synchronize on the sessionList to avoid concurrent modification
            for (Session session : sessionList) {
                if (session.getId().equals(sessionId)) {
                //    List<String> bookedSeatDataList = session.getBookedSeats();
                    
                    session.setBookedSeats(bookedSeatList);
                    // Collect the new seats to be added
//                    for (String bookedSeat : bookedSeatList) {
//                        newBookedSeats.add(bookedSeat);
//                    }

                    // Mark that we found and updated the session
                    flag = true;
                    break;
                }
            }

            // Now add all new booked seats after iteration
//            if (flag) {
//                for (Session session : sessionList) {
//                    if (session.getId().equals(sessionId)) {
//                        session.getBookedSeats().addAll(newBookedSeats);
//                        break;
//                    }
//                }
//            }
        }

        // Save the updated sessionList to the file
        try {
            File dataFile = new File(FILENAME);
            Map<String, List<?>> dataMap = objectMapper.readValue(dataFile, new TypeReference<Map<String, List<?>>>() {});

            // Update the sessions list
            dataMap.put("sessions", sessionList);

            // Enable pretty printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(dataFile, dataMap);

        } catch (IOException ex) {
            System.err.println("Error saving sessions to file: " + ex.getMessage());
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return flag;
    }

//    public static boolean saveSessionList(String sessionId, List<String> bookedSeatList) {
//        boolean flag = false;
//        
//        for (Session session : sessionList) {
//            if (session.getId().equals(sessionId)) {
//                List<String> bookedSeatDataList = session.getBookedSeats();
//                for (String bookedSeat : bookedSeatList) {
//                    bookedSeatDataList.add(bookedSeat);
//                }
//                
//                session.setBookedSeats(bookedSeatDataList);
//                flag = true;
//                break;
//            }
//        }
//        
//        try {
//            File dataFile = new File(FILENAME);
//            Map<String, List<?>> dataMap = objectMapper.readValue(dataFile, new TypeReference<Map<String, List<?>>>() {});
//            
//            //Update the sessions list
//            dataMap.put("sessions", sessionList);
//            
//            // Enable pretty printing
//            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//            objectMapper.writeValue(dataFile, dataMap);
//            
//        } catch (IOException ex) {
//            System.err.println("Error saving sessions to file: " + ex.getMessage());
//            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//        
//        return flag;
//    }
    
    public static List<Customer> getCustomerList() {
        return customerList;
    }
    
    public static List<Movie> getMovieList() {
        return movieList;
    }
    
    public static List<Session> getSessionList() {
        return sessionList;
    }
    
    public static List<Booking> getBookingList() {
        return bookingList;
    }
    
    public static void setCustomerList(List<Customer> customers) {
        customerList = customers;
    }

    public static void setMovieList(List<Movie> movieList) {
        DataStore.movieList = movieList;
    }

    public static void setSeatList(List<Seat> seatList) {
        DataStore.seatList = seatList;
    }

    public static void setSessionList(List<Session> sessionList) {
        DataStore.sessionList = sessionList;
    }

    public static void setBookingList(List<Booking> bookingList) {
        DataStore.bookingList = bookingList;
    }
    
    public static Session getSessionBySessionId(String sessionId) {
        for (Session session : sessionList) {
            if (session.getId().equals(sessionId)) {
                return session;
            }
        }
        
        return null;
    }
    
    public static List<Booking> getBookingListBySessionId(String sessionId) {
        List<Booking> bookingListBySessionId = new ArrayList<>();
        
        for (Booking booking : bookingList) {
            if (booking.getSessionId().equals(sessionId)) {
                bookingListBySessionId.add(booking);
            }
        }
        
        return bookingListBySessionId;
    }
    
    public static Customer getCustomerByCustomerId(String customerId) {
        for (Customer customer : customerList) {
            if (customer.getId().equals(customerId)) {
                return customer;
            }
        }
        
        return null;
    }
    
    public static Seat getSeatBySeatId(String seatId) {
        for (Seat seat : seatList) {
            if (seat.getId().equals(seatId)) {
                return seat;
            }
        }
        
        return null;
    }
    
    public static Movie getMovieByMovieId(String movieId) {
        for (Movie movie : movieList) {
            if (movie.getId().equals(movieId)) {
                return movie;
            }
        }
        
        return null;
    }
    
    public static List<Session> getSessionListByMovieId(String movieId) {
        List<Session> sessions = new ArrayList<>();
        for (Session session : sessionList) {
            if (session.getMovieId().equals(movieId)) {
                sessions.add(session);
            }
        }
        
        return sessions;
    }
    
    public static List<Session> getSessionListByDate(List<Session> sessions, String date) {
        List<Session> res = new ArrayList<>();
        LocalDate targetDate = LocalDate.parse(date);

        for (Session session : sessions) {
            LocalDateTime sessionDateTime = LocalDateTime.parse(session.getDatetime());
            if (sessionDateTime.toLocalDate().equals(targetDate)) {
                res.add(session);
            }
        }

        return res;
    }
    
    public static List<String> getMovieIdByDate(String date) {
        List<Session> sessions = getSessionListByDate(sessionList, date);
        Set<String> movieIdSet = new HashSet<>();

        for (Session session : sessions) {
            movieIdSet.add(session.getMovieId());
        }

        return new ArrayList<>(movieIdSet);
    }
    
    public static Session getSessionByDateTime(String datetime) {
        for (Session session : sessionList) {
            if (session.getDatetime().equals(datetime)) {
                return session;
            }
        }
        
        return null;
    }
    
    public static Customer getCustomerByCustomerName(String username) {
        for (Customer customer : customerList) {
            if (customer.getName().equals(username)) {
                return customer;
            }
        }
        return null;
    }
    
    public static List<Booking> getBookingListByCustomerId(String customerId) {
        List<Booking> historyBookingList = new ArrayList<>();
        System.out.println(customerId);
        
        for (Booking booking : bookingList) {
            if (booking.getCustomerId().equals(customerId)) {
                historyBookingList.add(booking);
            }
        }
        
        return historyBookingList;
    }
}
