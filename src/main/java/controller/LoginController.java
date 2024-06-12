/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import model.Customer;


public class LoginController {
    public static boolean login(String username, String password) {
        DataStore.loadData();
        List<Customer> customerList = DataStore.getCustomerList();
        
        for (Customer customer : customerList) {
            if (customer.getName().equals(username) && customer.getPassword().equals(password)) {
                return true;
            }
        }
        
        return false;
    }
}
