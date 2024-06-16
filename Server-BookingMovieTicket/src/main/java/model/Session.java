/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {
    private String id;
    
    @JsonProperty("movie_id")
    private String movieId;
    
    private String datetime;
    
    @JsonProperty("booked_seats")
    private List<String> bookedSeats;

    public Session() {
    }

    public Session(String id, String movieId, String datetime, List<String> bookedSeats) {
        this.id = id;
        this.movieId = movieId;
        this.datetime = datetime;
        this.bookedSeats = bookedSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    @Override
    public String toString() {
        return "Session{" + "id=" + id + ", movieId=" + movieId + ", datetime=" + datetime + ", bookedSeats=" + bookedSeats + '}';
    }
    
}
