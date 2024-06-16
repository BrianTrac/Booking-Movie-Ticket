/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.view;

import Utility.DataStore;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Booking;
import model.Customer;
import Utility.GBCBuilder;
import Server.controller.ServerDataObserver;

public class CustomerServerPanel extends JPanel implements  ServerDataObserver {
    private JScrollPane scrollPane;
    private JTable customerTable;
    private JTextField searchTextField;
    private TableRowSorter<TableModel> rowSorter;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    
    public CustomerServerPanel() {
        setLayout(new GridBagLayout());
        initComponents();
        loadCustomerData(); 
    }
    
    private void initComponents() {
        GBCBuilder gbc = new GBCBuilder();
        
        customerTable = new JTable();
        scrollPane = new JScrollPane(customerTable);
        
        String[] columnNames = {"Username", "Password", "Phone", "Booking Count"};
        Object[][] data = new Object[][] {};
        customerTable.setModel(new DefaultTableModel(data, columnNames));
        customerTable.setDefaultEditor(Object.class, null);
        
        rowSorter = new TableRowSorter<>(customerTable.getModel());
        customerTable.setRowSorter(rowSorter);
        
        // Set the comparator for the third column to sort numerically
        rowSorter.setComparator(3, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        
        searchTextField = new SearchTextField(rowSorter);
    
        add(searchTextField, gbc.setGrid(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(20, 0, 10, 0));
        add(scrollPane, gbc.setGrid(0,1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0));
            
    }

    private void loadCustomerData() {
        List<Customer> customerList = DataStore.getCustomerList();
        List<Booking> bookingList = DataStore.getBookingList();

        Map<String, Integer> bookingCountMap = new HashMap<>();

        for (Customer customer : customerList) {
            bookingCountMap.put(customer.getId(), 0);
        }

        for (Booking booking : bookingList) {
            String customerId = booking.getCustomerId();
            bookingCountMap.put(customerId, bookingCountMap.get(customerId) + 1);
        }

        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0);

        for (Customer customer : customerList) {
            model.addRow(new Object[]{customer.getName(), customer.getPassword(), customer.getPhone(), bookingCountMap.get(customer.getId())});
        }

        // Reinitialize row sorter
        rowSorter = new TableRowSorter<>(customerTable.getModel());
        rowSorter.setComparator(3, Comparator.comparingInt(o -> Integer.valueOf(o.toString())));
        customerTable.setRowSorter(rowSorter);
        
    }

    @Override
    public void updateServerData() {
        // Ensure the update happens on the EDT
        SwingUtilities.invokeLater(this::loadCustomerData);
    }
    
}
