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
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Session;

/**
 *
 * @author DELL
 */
public class SeatGridServerPanel extends JPanel {
    private final String id;
    private JPanel screenPanel;
    private JPanel seatGridPanel;
    private JPanel legendPanel;
    private static final int ROW = 5;
    private static final int COL = 8;
    private final boolean isEditable;
    private List<String> bookedSeatList;
    private static List<String> selectedSeatList;
    
    public SeatGridServerPanel(String id, boolean isEditable) {
        this.id = id;
        this.isEditable = isEditable;
        initComponents();
    }
    
    private void initComponents() {
        
        createScreenPanel();
        createSeatGridPanel();
        createLegendPanel();
        
        setLayout(new BorderLayout());
        
        screenPanel.setPreferredSize(new Dimension(800, 30));
        seatGridPanel.setPreferredSize(new Dimension(800, 333));
        legendPanel.setPreferredSize(new Dimension(800, 100));
        
        add(screenPanel, BorderLayout.NORTH);
        add(seatGridPanel, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.SOUTH);
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
                
                
                gbc.setGrid(j, i);
                seatGridPanel.add(seatButtons[i][j], gbc);
            }
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
        Session session = DataStore.getSessionBySessionId(id);
        bookedSeatList = session.getBookedSeats();
    }
     
}
