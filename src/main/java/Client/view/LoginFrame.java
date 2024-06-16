/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Utility.GBCBuilder;
import Client.view.Main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import Client.controller.Observer;

public class LoginFrame extends JFrame implements Observer{

    private JLabel titleLabel ,usernameLabel, passwordLabel, signUpLinkLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginFrame() {
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GBCBuilder gbc = new GBCBuilder();
        
        titleLabel = new JLabel("Đăng nhập");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(titleLabel, gbc.setGrid(0, 0).setSpan(2, 1).setInsets(10, 143, 41, 143 ));
        
        usernameLabel = new JLabel("Tên đăng nhập: ");
        usernameLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 14));
        usernameTextField = new JTextField();
        usernameTextField.setPreferredSize(new Dimension(210, 35));
        panel.add(usernameLabel, gbc.setGrid(0, 1).setSpan(1, 1).setInsets(0, 95, 6,  115 ));
        panel.add(usernameTextField,gbc.setGrid(0, 2).setSpan( 2, 1).setInsets(0, 95, 18, 95 ));
    
        passwordLabel = new JLabel("Mật khẩu: ");
        passwordLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(210, 35));
        panel.add(passwordLabel, gbc.setGrid(0, 3).setSpan(1, 1).setInsets(0, 95, 6,  145 ));
        panel.add(passwordField,gbc.setGrid(0, 4).setSpan( 2, 1).setInsets(0, 95, 49, 95 ));
        
        loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(210, 35));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(51, 51, 51));
        loginButton.setForeground(Color.white);        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        panel.add(loginButton, gbc.setGrid(0, 9).setInsets(0, 95, 12, 95));
        
        signUpLinkLabel = new JLabel("Chưa có tài khoản? Đăng ký ngay!");
        signUpLinkLabel.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 12));
        signUpLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSignUpLinkClick();
            }
        });
        
        panel.add(signUpLinkLabel, gbc.setGrid(0, 10).setInsets(0, 95, 0, 60));
 
        add(panel);
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null); // Cente
        setVisible(true);
    }
    
    private void handleLogin() {
        String username = usernameTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        Main.getSocketClientController().login(username, password);
    }

    private void handleSignUpLinkClick() {
        this.dispose(); // Close current frame
        SignUpFrame signUpFrame = new SignUpFrame();
        Main.getSocketClientController().addObserver(signUpFrame);
        signUpFrame.setVisible(true);
    }
    
    @Override
    public void update(String eventType, Object result) {

        if ("login".equals(eventType)) {
            if (result instanceof String) {
                
                String[] parts = ((String)result).split("&");
                String status = parts[0];
               
                
                switch ((String) status) {
                    case "SUCCESS" -> SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công!");
                            // Switch to the main application window
                            String customerId = parts[1];
                            new MainScreenClientFrame(customerId).setVisible(true);
                            this.dispose();
                        });
                    case "ALREADY_LOGGED_IN" -> JOptionPane.showMessageDialog(LoginFrame.this, "Đã đăng nhập ở thiết bị khác!");
                    case "INVALID_CREDENTIALS" -> JOptionPane.showMessageDialog(LoginFrame.this, "Tên đăng nhập hoặc mật khẩu không chính xác.");
                    default -> {
                    }
                }
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "ERROR!!!");                
            }
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
