/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Utility.GBCBuilder;
import Utility.DataStore;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Booking;
import model.Customer;
import model.Movie;
import model.Session;
import Client.view.SeatMapClient;

public class HistoryClientPanel extends JPanel {
    private JScrollPane scrollPane;
    private JTable historyTable;
    private JTextField searchTextField;
    private static TableRowSorter<TableModel> rowSorter;
    
    public HistoryClientPanel() {
        setLayout(new GridBagLayout());
        initComponents();
        loadHistoryData();
    }
    
    private void initComponents() {
        GBCBuilder gbc = new GBCBuilder();
        
        historyTable = new JTable();
        scrollPane = new JScrollPane(historyTable);
        
        String[] columnNames = {"Username", "Movie", "ShowTime", "Seat", "Price"};
        Object[][] data = new Object[][] {};
        historyTable.setModel(new DefaultTableModel(data, columnNames));
        historyTable.setDefaultEditor(Object.class, null);
        
        rowSorter = new TableRowSorter<>(historyTable.getModel());
        historyTable.setRowSorter(rowSorter);
        rowSorter.setComparator(4, Comparator.comparingDouble(o -> Double.valueOf(o.toString())));
        
        searchTextField = new SearchTextField(rowSorter);
        
    
        add(searchTextField, gbc.setGrid(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(20, 0, 10, 0));
        add(scrollPane, gbc.setGrid(0,1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0));
            
    }
    
    private void loadHistoryData() {
        Customer customer = DataStore.getCustomerByCustomerId(MainScreenClientFrame.customerId);
        List<Booking> historyBooking = DataStore.getBookingListByCustomerId(customer.getId()); 
        
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        rowSorter.setComparator(4, Comparator.comparingDouble(o -> Double.valueOf(o.toString())));
        
        
        for (Booking booking : historyBooking) {
            Session session = DataStore.getSessionBySessionId(booking.getSessionId());
            Movie movie = DataStore.getMovieByMovieId(session.getMovieId());
            model.addRow(new Object[]{customer.getName(), movie.getTitle(), session.getDatetime(),  getBookedSeatNameString(booking.getSeatId()), booking.getPrice()});
        }
    }
    
    private String getBookedSeatNameString(String seatIdList) {
        String[] seatIds = seatIdList.split(",\\s*");
        String res = "";
        
        for (String seatId : seatIds) {
            if (!res.isEmpty()) {
                res += ", ";
            }
            res += SeatMapClient.getSeatLabel(seatId);
        }
        
        return res;
    } 
    
     public void reload() {
        removeAll();
        initComponents();
        loadHistoryData();
        revalidate();
        repaint();
    }
}
