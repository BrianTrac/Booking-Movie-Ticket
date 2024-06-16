/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.view;

import Utility.GBCBuilder;
import Utility.DataStore;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

/**
 *
 * @author DELL
 */
public class InfoServerPanel extends JPanel {
    private final String id;
    private List<Booking> bookingListBySessionId;
    private JScrollPane scrollPane;
    private JTable infoTable;
    private JTextField searchTextField;
    private TableRowSorter<TableModel> rowSorter;
    
    public InfoServerPanel(String id) {
        this.id = id;
        setLayout(new GridBagLayout());
        initComponents();
        loadCustomerData();
    }
    
    private void initComponents() {
        GBCBuilder gbc = new GBCBuilder();
        
        infoTable = new JTable();
        scrollPane = new JScrollPane(infoTable);
       
        String[] columnNames = {"Customer", "Phone", "Booked Seats", "Price"};
        Object[][] data = new Object[][] {};
        infoTable.setModel(new DefaultTableModel(data, columnNames));
        infoTable.setDefaultEditor(Object.class, null);
        
        rowSorter = new TableRowSorter<>(infoTable.getModel());
        infoTable.setRowSorter(rowSorter);
       
        searchTextField = new SearchTextField(rowSorter);
        
        
        add(searchTextField, gbc.setGrid(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(20, 0, 10, 0));
        add(scrollPane, gbc.setGrid(0,1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0));
    
    }
    
    private void loadCustomerData() {
        bookingListBySessionId = DataStore.getBookingListBySessionId(id);
                  
        DefaultTableModel model = (DefaultTableModel) infoTable.getModel();
        model.setRowCount(0);
        
        for (Booking booking : bookingListBySessionId) {
            Customer customer = DataStore.getCustomerByCustomerId(booking.getCustomerId());
            String bookedSeatList = getBookedSeatList(booking.getSeatId());
            model.addRow(new Object[]{customer.getName(), customer.getPhone(), bookedSeatList, booking.getPrice()});
        }
    }
    
    private String getBookedSeatList(String bookedSeats) {
        String[] seatIds = bookedSeats.split(",\\s*");
        String res = "";
        for (String str : seatIds) {
            if (!res.isEmpty()) {
                res += ", ";
            }
     
            res += DataStore.getSeatBySeatId(str).getPosition();   
        }
        
        return res;
    }
}
