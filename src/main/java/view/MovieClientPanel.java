/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.DataStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author DELL
 */
public class MovieClientPanel extends JPanel {
    private JPanel showTimeFilterPanel;
    private JPanel movieListPanel;
    private JScrollPane movieListScrollPane;
    private String startDateString = "2024-06-12";
    private JButton selectedButton;
    
   
    
    public MovieClientPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        createShowTimeFilterPanel();
        createMovieListPanel();
        updateMovieListPanel(startDateString);
        
        showTimeFilterPanel.setPreferredSize(new Dimension(600, 100));
        movieListPanel.setPreferredSize(new Dimension(600, 500));
        movieListScrollPane = new JScrollPane(movieListPanel);
        
        add(showTimeFilterPanel, BorderLayout.NORTH);
        add(movieListScrollPane, BorderLayout.CENTER);
    }
    
    private void createShowTimeFilterPanel() {
        showTimeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 35, 25));
        showTimeFilterPanel.setBackground(Color.WHITE);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDate startDate = LocalDate.parse(startDateString, formatter);
        
        for (int i = 0; i < 5; i++) {
            LocalDate nextDate = startDate.plusDays(i);
            String nextDateString = nextDate.format(formatter);
            
            // Create ShowTime Button
            JButton showTimeButton = new JButton(nextDateString);
            showTimeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            showTimeButton.setPreferredSize(new Dimension(110, 45));
            showTimeButton.setMinimumSize(new Dimension(110, 45));
            showTimeButton.setMaximumSize(new Dimension(110, 45));
  
            if (nextDate.equals(startDate)) {
                showTimeButton.setBackground(new Color(216,45,139));
                selectedButton = showTimeButton;
            }
            else {
                showTimeButton.setBackground(Color.WHITE);
            }
            showTimeButton.setFocusPainted(false);
            showTimeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    showTimeButtonActionPerformed(evt, showTimeButton);
                }
            });
            showTimeFilterPanel.add(showTimeButton);
        }
    }
    
    private void showTimeButtonActionPerformed(ActionEvent evt, JButton clickedButton) {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(Color.BLACK);
        }
        
        clickedButton.setBackground(new Color(216,45,139));
        clickedButton.setForeground(Color.WHITE);
        
        selectedButton = clickedButton;
        updateMovieListPanel(selectedButton.getText());
    }
    
    private void updateMovieListPanel(String date) {
        
        movieListPanel.removeAll();
        
        List<MovieInfoClientPanel> movieInfoList = getMovieInfoList(date);
        movieListPanel.setPreferredSize(new Dimension(600, 200 * movieInfoList.size()));
        
        for (MovieInfoClientPanel micp : movieInfoList) {
            movieListPanel.add(micp);
        }
        
        movieListPanel.revalidate();
        movieListPanel.repaint();
    }
    
    private List<MovieInfoClientPanel> getMovieInfoList(String date) {
        List<MovieInfoClientPanel> movieInfoList = new ArrayList<>();
        List<String> movieIdList = DataStore.getMovieIdByDate(date);
       
        for (String movieId : movieIdList) {
            movieInfoList.add(new MovieInfoClientPanel(movieId, date));
        }
        
        return movieInfoList;
    }
    
    private void createMovieListPanel() {
        movieListPanel = new JPanel();
        movieListPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10)); // Add some spacing between panels
        movieListPanel.setBackground(Color.LIGHT_GRAY);
        
        movieListScrollPane = new JScrollPane(movieListPanel);
        movieListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    }
    
    public void reload() {
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }
}
