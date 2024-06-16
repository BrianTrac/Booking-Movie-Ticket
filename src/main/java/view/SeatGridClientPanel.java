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
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import model.Movie;
import model.Seat;
import model.Session;

/**
 *
 * @author DELL
 */
public class SeatGridClientPanel extends JPanel implements Observer {
    private final String sessionId;
    private JPanel screenPanel;
    private JPanel seatGridPanel;
    private JPanel legendPanel;
    private static final int ROW = 5;
    private static final int COL = 8;
    private final boolean isEditable;
    private List<String> bookedSeatList;    // booked seatId List
    private static List<String> selectedSeatList; // selected seatId List
    private List<String> selectedSeatNameList;  // SeatName List 
    private List<JButton> selectedSeatButtonList;
    private JPanel titlePanel;
    private JPanel bookedSeatPanel;
    private JPanel paymentPanel;
    private Session session;
    private Movie movie;
    private JLabel seatNumbersLabel;
    private JLabel paymentLabel;
    private double totalMoney = 0;
    private String selectedSeatNameString;
   
    
    public SeatGridClientPanel(String sessionId, boolean isEditable) {
        this.sessionId = sessionId;
        this.isEditable = isEditable;
        selectedSeatList = new ArrayList<>();
        selectedSeatNameList = new ArrayList<>();
        selectedSeatButtonList = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
       
        selectedSeatList.clear();
        selectedSeatNameList.clear();
        selectedSeatButtonList.clear();
        
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 465));
        
        createScreenPanel();
        createSeatGridPanel();
        createLegendPanel();
               
        screenPanel.setPreferredSize(new Dimension(800, 30));
        seatGridPanel.setPreferredSize(new Dimension(800, 333));
        legendPanel.setPreferredSize(new Dimension(800, 100));
        
        mainPanel.add(screenPanel, BorderLayout.NORTH);
        mainPanel.add(seatGridPanel, BorderLayout.CENTER);
        mainPanel.add(legendPanel, BorderLayout.SOUTH);
        
        JPanel infoPanel = new JPanel(new BorderLayout(10,10));
        infoPanel.setPreferredSize(new Dimension(800, 200));
        GBCBuilder gbc = new GBCBuilder();
        
        createTitlePanel();
        createBookedSeatPanel();
        createPaymentPanel();
        
        titlePanel.setPreferredSize(new Dimension(800, 60));
        bookedSeatPanel.setPreferredSize(new Dimension(800, 60));
        paymentPanel.setPreferredSize(new Dimension(800, 60));
        
        infoPanel.add(titlePanel, BorderLayout.NORTH);
        infoPanel.add(bookedSeatPanel, BorderLayout.CENTER);
        infoPanel.add(paymentPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
       
    }
    
    private void createScreenPanel() {
        screenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton screenButton = new JButton("Màn hình");
        screenButton.setFont(new Font("Arial", Font.BOLD, 16));
        screenButton.setBackground(Color.WHITE);    
        screenButton.setFocusPainted(false);
        screenButton.setPreferredSize(new Dimension(400, 30));
        screenPanel.add(screenButton);
        screenPanel.setBackground(new Color(38,38,38)); 
    }
    
    private void createSeatGridPanel() {
        // get seat booked
        getBookedSeatList();
        int hgap = 12;
        int vgap = 6;
        
        seatGridPanel = new JPanel(new GridBagLayout());
        GBCBuilder gbc = new GBCBuilder();
        gbc.setInsets(0, 0, 6, 12).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.NONE);
    
        seatGridPanel.setBackground(new Color(38, 38,38));
        JButton[][] seatButtons = new JButton[ROW][COL];
        Dimension buttonSize = new Dimension(60, 40);
                
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                String seatName = (char)('A' + i) + String.valueOf(j + 1);
            //    System.out.println(seatName);
                seatButtons[i][j] = new JButton(seatName);
                seatButtons[i][j].setForeground(Color.WHITE);
                seatButtons[i][j].setFocusPainted(false);
                seatButtons[i][j].setPreferredSize(buttonSize);
                seatButtons[i][j].setMinimumSize(buttonSize);
                seatButtons[i][j].setMaximumSize(buttonSize);
                
                if (i < 3) {
                    seatButtons[i][j].setBackground(new Color(114,46,209));
                }
                else {
                    seatButtons[i][j].setBackground(new Color(245,34,45));
                }
                
                for (String bookedSeat : bookedSeatList) {
                    if (bookedSeat.equals(SeatMapServer.calSeatId(seatName))) {
                        seatButtons[i][j].setBackground(Color.BLACK);
                        break;
                    }
                }
                
                if (isEditable) {
                    seatButtons[i][j].addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            seatButtonActionPerformed(evt);
                        }
                    });
                }
                gbc.setGrid(j, i);
                seatGridPanel.add(seatButtons[i][j], gbc);
            }
        }
    }
    
    private void seatButtonActionPerformed(ActionEvent evt) {
        JButton clickedButton = (JButton) evt.getSource();
        String seatId = SeatMapServer.calSeatId(clickedButton.getText());
        Seat seat = DataStore.getSeatBySeatId(seatId);
        
        if (clickedButton.getBackground().equals(new Color(245,34,45)) || clickedButton.getBackground().equals(new Color(114,46,209))) {
            clickedButton.setBackground(new Color(216,45,139));
            selectedSeatButtonList.add(clickedButton);
            selectedSeatNameList.add(clickedButton.getText());
            selectedSeatList.add(SeatMapServer.calSeatId(clickedButton.getText()));
            totalMoney += seat.getPrice();
            updateSeatNumbersLabel();
            updatePaymentLabel();
            
        } else if (clickedButton.getBackground().equals(new Color(216,45,139))){
            String type = clickedButton.getText().substring(0,1);
            if (type.equals("D") || type.equals("E")) {
                clickedButton.setBackground(new Color(245,34,45));
            }
            else {
                clickedButton.setBackground(new Color(114,46,209));
            }
            selectedSeatButtonList.remove(clickedButton);
            selectedSeatList.remove(SeatMapServer.calSeatId(clickedButton.getText()));
            selectedSeatNameList.remove(clickedButton.getText());
            totalMoney -= seat.getPrice();
            totalMoney = Double.max(totalMoney, 0);
            
            updateSeatNumbersLabel();
            updatePaymentLabel();
        }
        else {
            //
        }
    }
   
    public static List<String> getSelectedSeatList() {
        return selectedSeatList;
    }
    
    private void createLegendPanel() {
        legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 10)); // Left alignment, spacing
        legendPanel.setBackground(new Color(38, 38, 38));
        
        legendPanel.add(createLegendItem("Đã đặt", Color.BLACK));
        legendPanel.add(createLegendItem("Ghế VIP", new Color(245,34,45)));
        legendPanel.add(createLegendItem("Ghế thường", new Color(114,46,209))); 
        legendPanel.add(createLegendItem("Ghế bạn chọn", new Color(216,45,139)));
    }
    
    private JPanel createLegendItem(String labelText, Color squareColor) {
        JPanel panel = new JPanel(new BorderLayout(5, 0)); // 5px horizontal gap
        panel.setBackground(new Color(38, 38, 38));
        
        JPanel colorSquare = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(squareColor);
                g.fillRect(0, 0, getWidth(), getHeight()); // Fill the square
                g.setColor(Color.WHITE); // Color for the border
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1); // Draw the border
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(24, 24); // Fixed size
            }
        };
        panel.add(colorSquare, BorderLayout.WEST);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }
    
    private void getBookedSeatList() {
        DataStore.loadData();
        Session session = DataStore.getSessionBySessionId(sessionId);
        bookedSeatList = session.getBookedSeats();
        System.out.println("SeatGridClientPanel: " + bookedSeatList);
    }
    
     private void createTitlePanel() {
        titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        
        session = DataStore.getSessionBySessionId(sessionId);
        movie = DataStore.getMovieByMovieId(session.getMovieId());
        
        String infoMovieString = movie.getTitle() + "        " + session.getDatetime();
        JLabel infoMovieLabel = new JLabel(infoMovieString);
        infoMovieLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titlePanel.add(infoMovieLabel);
    }
    
    private void createBookedSeatPanel() {
        bookedSeatPanel = new JPanel(new BorderLayout());
        bookedSeatPanel.setBackground(Color.WHITE);
        
        JLabel seatLabel = new JLabel("Chỗ ngồi");
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookedSeatPanel.add(seatLabel, BorderLayout.WEST);
        
        JPanel seatInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center alignment, 5px horizontal gap
        seatNumbersLabel = new JLabel(""); // Initially empty
        seatInfoPanel.add(seatNumbersLabel);
        seatInfoPanel.setBackground(Color.WHITE);

        // Close Button
        JButton closeButton = new JButton("x");
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle closing/clearing seat selection
                clearAllSeatSelection();
            }
        });
        seatInfoPanel.add(closeButton);
        
        bookedSeatPanel.add(seatInfoPanel, BorderLayout.EAST);
    }
    
    private void clearAllSeatSelection() {
        // Create a copy of the list to avoid ConcurrentModificationException
        List<JButton> copyOfSelectedSeatButtonList = new ArrayList<>(selectedSeatButtonList);

        for (JButton button : copyOfSelectedSeatButtonList) {
            SwingUtilities.invokeLater(() -> button.doClick());
        }
    }
    
    private void updateSeatNumbersLabel() {
        selectedSeatNameString = "";
        
        for (String str : selectedSeatNameList) {
            if (!selectedSeatNameString.isEmpty()) {
                selectedSeatNameString += ", ";
            }
            selectedSeatNameString += str;
        }
        
        seatNumbersLabel.setText(selectedSeatNameString);
    }
     
    private void createPaymentPanel() {
        paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBackground(Color.WHITE);
        
        String paymentString = "Tổng tiền: " + totalMoney + "$" ;  
        paymentLabel = new JLabel(paymentString);
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentPanel.add(paymentLabel, BorderLayout.WEST);
        
        JButton paymentButton = new JButton("Mua vé");
        paymentButton.setFocusPainted(false);
        paymentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentButton.setBackground(new Color(216,45,139));
        paymentButton.setForeground(Color.WHITE);
        paymentButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt) {
                paymentButtonActionPerformed(evt);
            }
        });
    //    paymentButton.setPreferredSize(new Dimension(100, 50));
        paymentPanel.add(paymentButton, BorderLayout.EAST);
        
    }
    
    private void updatePaymentLabel() {
        paymentLabel.setText("Tổng tiền: " + totalMoney + "$" );
    }
    
    private void paymentButtonActionPerformed(ActionEvent evt) {
        int response = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn mua vé?",
                "Xác nhận mua vé",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            // Handle the payment processing here
            System.out.println("Payment confirmed");
        //    DataStore.saveBookingList(MainScreenClientFrame.customerId, sessionId, getSelectedSeatListString(), totalMoney);
        //    DataStore.saveSessionList(sessionId, selectedSeatList); 
        //    refreshPanel();
            Main.getSocketClientController().bookSeat(MainScreenClientFrame.customerId, sessionId, getSelectedSeatListString(), totalMoney, selectedSeatList);
        } else if (response == JOptionPane.NO_OPTION) {
            // Handle the case where the user does not confirm the payment
            System.out.println("Payment cancelled");
        }
    }
    
    private String getSelectedSeatListString() {
        String selectedSeatListString = "";
        
        for (String str : selectedSeatList) {
            if (!selectedSeatListString.isEmpty()) {
                selectedSeatListString += ", ";
            }
            selectedSeatListString += str;
        }
        
        return selectedSeatListString;
    } 
    
    private void refreshPanel() {
        selectedSeatNameString = "";
        totalMoney = 0;
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }
    
    @Override
    public void update(String evenType, Object result) {
        
        if ("bookSeat".equals(evenType)) {
            if (result instanceof String) {
                String[] parts = ((String) result).split("&");
                String status = parts[1].trim();
                System.out.println("bookSeat: " + status);
                switch(status) {
                    case "SUCCESS":
                        SwingUtilities.invokeLater(() ->{
                            JOptionPane.showMessageDialog(null, "Mua vé thành công");                            
                        });
                        refreshPanel();
                        break;
                    case "FAILED":
                        String unavailableSeat = parts[2];
                        System.out.println("Failed: " + unavailableSeat);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, unavailableSeat + " đã được mua trước");
                        });
                        break;
                    default:
                        System.out.println("bookSeat error: " + result);
                        break;
                }
            }
        }
        else if ("updateSeatMap".equals(evenType)) {
            if (result instanceof String) {
                String[] parts = ((String) result).split("&");
                String sessionIdNeededToUpdate = parts[1].trim();
                System.out.println("updateSeatmap: " + sessionIdNeededToUpdate);
                
                if (sessionIdNeededToUpdate.equals(sessionId)) {
                    refreshPanel();
                }
            }
        }
    }
}
