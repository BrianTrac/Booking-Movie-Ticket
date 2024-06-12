/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.DataStore;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Booking;
import model.Customer;

public class CustomerServerPanel extends JPanel {
    private JScrollPane scrollPane;
    private JTable customerTable;
    private JTextField searchTextField;
    private static TableRowSorter<TableModel> rowSorter;
    
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
    }
}
