/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import java.util.UUID;

/**
 *
 * @author DELL
 */
public class Utility {
    public static String generateUniqueID() {
        return UUID.randomUUID().toString();
    }
}
