/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import model.Customer;
import Utility.Utility;

public class SignUpController {
    
    public static boolean signUp(String username, String password, String phone) {
        
        DataStore.loadData();
        List<Customer> customerList = DataStore.getCustomerList();
        
        for (Customer customer : customerList) {
            if (customer.getName().equals(username)) {
                return false;
            }
        }
        
        String customerId = "customer" + (customerList.size() + 1);
        Customer newCustomer = new Customer(customerId, username, password, phone);
        customerList.add(newCustomer);
        
        DataStore.setCustomerList(customerList);
        DataStore.saveCustomerList();
        
        return true;
    }
}
