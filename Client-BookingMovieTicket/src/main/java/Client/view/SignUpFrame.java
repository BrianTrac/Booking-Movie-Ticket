/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import Client.view.LoginFrame;
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


public class SignUpFrame extends JFrame implements Observer{
    private JLabel titleLabel, usernameLabel, passwordLabel, confirmPasswordLabel, phoneLabel, loginLinkLabel;
    private JTextField usernameTextField, phoneTextField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton createAccountButton;
//    private static boolean signUpSuccess = false;
    
    public SignUpFrame() {
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GBCBuilder gbc = new GBCBuilder();
        
        titleLabel = new JLabel("Đăng ký");
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
        panel.add(passwordField,gbc.setGrid(0, 4).setSpan( 2, 1).setInsets(0, 95, 18, 95 ));
           
        confirmPasswordLabel = new JLabel("Nhập lại mật khẩu: ");
        confirmPasswordLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 14));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(210, 35));
        panel.add(confirmPasswordLabel, gbc.setGrid(0, 5).setSpan(1, 1).setInsets(0, 95, 6,  95 ));
        panel.add(confirmPasswordField,gbc.setGrid(0, 6).setSpan( 2, 1).setInsets(0, 95, 18, 95 ));
    
        phoneLabel = new JLabel("Số điện thoại: ");
        phoneLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 14));
        phoneTextField = new JTextField(20);
        phoneTextField.setPreferredSize(new Dimension(210, 35));
        panel.add(phoneLabel, gbc.setGrid(0, 7).setSpan(1, 1).setInsets(0, 95, 6,  125 ));
        panel.add(phoneTextField,gbc.setGrid(0, 8).setSpan( 2, 1).setInsets(0, 95, 50, 95 ));
    
        createAccountButton = new JButton("Tạo Tài Khoản");
        createAccountButton.setPreferredSize(new Dimension(210, 35));
        createAccountButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createAccountButton.setBackground(new Color(51, 51, 51));
        createAccountButton.setForeground(Color.white);
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
        
        panel.add(createAccountButton, gbc.setGrid(0, 9).setInsets(0, 95, 12, 95));
        
        loginLinkLabel = new JLabel("Đã có tài khoản? Đăng nhập!");
        loginLinkLabel.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 12));
        loginLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLoginLinkClick();
            }
        });
        
        panel.add(loginLinkLabel, gbc.setGrid(0, 10).setInsets(0, 95, 0, 50));
 
        add(panel);
        setTitle("Đăng ký");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // Cente
        setVisible(true);
    }
    
    private void handleSignUp() {
        String username = usernameTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String phone = phoneTextField.getText().trim();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(SignUpFrame.this, "Mật khẩu không trùng khớp. Vui lòng nhập lại mật khẩu.");
            return;
        }
        
    //    boolean success = SignUpController.signUp(username, password, phone);
        Main.getSocketClientController().signUp(username, password, phone);
         
    }
    
    private void handleLoginLinkClick() {
        this.dispose(); // Close current frame
        LoginFrame loginFrame = new LoginFrame();
        Main.getSocketClientController().addObserver(loginFrame);
        loginFrame.setVisible(true);
    }
    
    @Override
    public void update(String eventType, Object result) {
        if ("signUp".equals(eventType)) {
            if (result instanceof Boolean && (Boolean) result) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(SignUpFrame.this, "Đăng ký thành công!");
                    this.dispose();
                    LoginFrame loginFrame = new LoginFrame();
                    Main.getSocketClientController().addObserver(loginFrame);
                    loginFrame.setVisible(true);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(SignUpFrame.this, "Tên đăng nhập đã tồn tại.");
                });
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpFrame());
    }
}
