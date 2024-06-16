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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import model.Movie;
import model.Session;

public class MovieInfoClientPanel extends JPanel{
    private final String movieId;
    private final String date;
    private final String srcImg = "src\\main\\java\\resources\\assets\\";
    private String image;
    private String title;
    private String genre;
    private List<String> showtimeList = new ArrayList<>();
    private JButton imageButton;
    private JLabel titleLabel;
    private JLabel genreLabel;
    private List<JButton> showtimeButtonList = new ArrayList<>();
    
     
    public MovieInfoClientPanel(String movieId, String date) {
        this.movieId = movieId;
        this.date = date;
    //    System.out.println(movieId + " " + date);
        
        getMovieData();
        initComponents();
    }
    
    private void initComponents() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(800, 180));
        
        ImageIcon imageIcon = new ImageIcon(srcImg + image);
        imageButton = new JButton(imageIcon);
        imageButton.setPreferredSize(new Dimension(140, 180));
        imageButton.setBackground(Color.WHITE);
        imageButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(image + " clicked");
            }
        });
        
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(150, 180)); // Ensure the WEST part is fully occupied
        westPanel.add(imageButton, BorderLayout.CENTER);
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0)); // Add gaps around the button
        westPanel.setBackground(Color.WHITE);
        
        add(westPanel, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
    
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(titleLabel);
        centerPanel.add(genreLabel);
        
        JPanel showtimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 20));
        showtimePanel.setBackground(Color.WHITE);
        
        for (String showtime : showtimeList) {
            JButton showtimeButton = new JButton(showtime);
            showtimeButton.setFocusPainted(false);
            showtimeButton.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
            showtimeButton.setPreferredSize(new Dimension(90, 30));
            
            // Create the border (color, thickness, rounded corners)
            Border roundedLineBorder = BorderFactory.createLineBorder(new Color(2, 132, 199), 2, true);
            showtimeButton.setBorder(roundedLineBorder);
            showtimeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    showtimeButtonActionPerformed(evt, showtimeButton);
                }
            });
            
            showtimeButtonList.add(showtimeButton);
            showtimePanel.add(showtimeButton);
        }
        
        infoPanel.add(centerPanel, BorderLayout.CENTER);
        infoPanel.add(showtimePanel, BorderLayout.SOUTH);
        infoPanel.setBackground(Color.WHITE);
        
        add(infoPanel, BorderLayout.CENTER);
          
    }
    
    private void showtimeButtonActionPerformed(ActionEvent evt, JButton clickedButton) {
        String datetimeString = date + "T" + clickedButton.getText();
        Session session = DataStore.getSessionByDateTime(datetimeString);
    //    System.out.println(session.getId());
        new SeatMapClient(session.getId()).setVisible(true);
    }
    
    private void getMovieData() {
        Movie movie = DataStore.getMovieByMovieId(movieId);
        title = movie.getTitle();
        genre = movie.getGenre();
        image = movie.getImage();
        
        List<Session> sessionListByMovieIdAndDate = DataStore.getSessionListByDate(DataStore.getSessionListByMovieId(movieId), date);
           
        for (Session session : sessionListByMovieIdAndDate) {
            showtimeList.add(session.getDatetime().split("T")[1]);
        }
       
    }
    
    
 
}
