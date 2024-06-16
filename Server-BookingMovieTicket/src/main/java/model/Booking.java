/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {
    private String id;
    
    @JsonProperty("customer_id")
    private String customerId;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("seat_id")
    private String seatId;
    
    private double price;

    public Booking() {
    }

    public Booking(String id, String customerId, String sessionId, String seatId, double price) {
        this.id = id;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.seatId = seatId;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Booking{" + "id=" + id + ", customerId=" + customerId + ", sessionId=" + sessionId + ", seatId=" + seatId + ", price=" + price + '}';
    }
    
}
