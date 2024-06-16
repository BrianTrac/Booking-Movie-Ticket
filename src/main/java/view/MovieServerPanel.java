/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.DataStore;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Booking;
import model.Movie;
import model.Session;

/**
 *
 * @author DELL
 */
public class MovieServerPanel extends JPanel implements ServerDataObserver {
    private JScrollPane scrollPane;
    private JTable movieTable;
    private JTextField searchTextField;
    private TableRowSorter<TableModel> rowSorter;
    private List<Session> sessionList;
    private List<Booking> bookingList;
    private List<Movie> movieList;
   
    public MovieServerPanel() {
        setLayout(new GridBagLayout());
        initComponents();
        loadMovieData();
    }
    
    private void initComponents() {
        GBCBuilder gbc = new GBCBuilder();
        
        movieTable = new JTable();
        scrollPane = new JScrollPane(movieTable);
       
        String[] columnNames = {"Id", "Movie", "Genre", "DateTime", "No BookedSeat", "Revenue"};
        Object[][] data = new Object[][] {};
        movieTable.setModel(new DefaultTableModel(data, columnNames));
        movieTable.setDefaultEditor(Object.class, null);
        addTableMouseListener(movieTable);
        
        rowSorter = new TableRowSorter<>(movieTable.getModel());
        movieTable.setRowSorter(rowSorter);
        
        // Set the comparator for the columns to sort numerically
        rowSorter.setComparator(4, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        rowSorter.setComparator(5, Comparator.comparingDouble(o -> Double.valueOf(o.toString())));
        
        searchTextField = new SearchTextField(rowSorter);
        
        add(searchTextField, gbc.setGrid(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(20, 0, 10, 0));
        add(scrollPane, gbc.setGrid(0,1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0));
    }

    
    private void loadMovieData() {
        sessionList = DataStore.getSessionList();
        bookingList = DataStore.getBookingList();
        movieList = DataStore.getMovieList();
        Map<String, List<Object>> calSessionDataMap = new HashMap<>();
        
        for (Session session : sessionList) {
            calSessionDataMap.put(session.getId(), new ArrayList(Arrays.asList(0, 0.0)));
        }
        
        for (Booking booking : bookingList) {     
            int numOfBookedSeats = (int) calSessionDataMap.get(booking.getSessionId()).get(0) + calculateNOBookedSeats(booking.getSeatId());
            double price = (double) calSessionDataMap.get(booking.getSessionId()).get(1) + booking.getPrice();
            calSessionDataMap.put(booking.getSessionId(), Arrays.asList(numOfBookedSeats ,price));
        }
        
        DefaultTableModel model = (DefaultTableModel) movieTable.getModel();
        model.setRowCount(0);
        
        for (Session session : sessionList) {
            model.addRow(new Object[]{session.getId(), getMovie(session.getMovieId()).getTitle(), getMovie(session.getMovieId()).getGenre(), session.getDatetime(), calSessionDataMap.get(session.getId()).get(0), calSessionDataMap.get(session.getId()).get(1) });
        }
        
        rowSorter = new TableRowSorter<>(movieTable.getModel());
        rowSorter.setComparator(4, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        rowSorter.setComparator(5, Comparator.comparingDouble(o -> Double.valueOf(o.toString())));
        movieTable.setRowSorter(rowSorter);
    }
    
    private Movie getMovie(String movieId) {
        for (Movie movie : movieList) {
            if (movie.getId().equals(movieId)) {
                return movie;
            }
        }
        return null;
    }
    
    private int calculateNOBookedSeats(String bookedSeatList) {
        String[] seatIds = bookedSeatList.split(",\\s*");
        return seatIds.length;
    }
    
    private static void addTableMouseListener(JTable movieTable) {
        movieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = movieTable.getSelectedRow();
                    
                    String id = (String) movieTable.getValueAt(selectedRow, 0);
                    
                    new SeatMapServer(id).setVisible(true);
                }
            }
        });
    }

    @Override
    public void updateServerData() {
        SwingUtilities.invokeLater(this::loadMovieData);
    }
}
