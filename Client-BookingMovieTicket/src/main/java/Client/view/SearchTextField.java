/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SearchTextField extends JTextField {
    private final  TableRowSorter<TableModel> rowSorter;
    private static final boolean isFeatureAvailable = false;
    
    public SearchTextField(TableRowSorter<TableModel> rowSorter) {
        this.rowSorter = rowSorter;
        initComponent();
    }
    
    private void initComponent() {
        setText("Search");
        setPreferredSize(new Dimension(180, 30));
        if (!isFeatureAvailable) {
            setEnabled(false); // Disable the search text field
            setToolTipText("Search functionality is currently unavailable.");
        } else {
            addSearchFunctionality(this);
        }
    }
    
    private void addSearchFunctionality(JTextField searchTextField) {
        // Add focus listener to handle placeholder text
        searchTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchTextField.getText().equals("Search")) {
                    searchTextField.setText("");
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchTextField.getText().isEmpty()) {
                    searchTextField.setText("Search");
                }
            }
        });
        
        // Add DocumentListener to filter table based on search text
        searchTextField.getDocument().addDocumentListener(new SearchDocumentListener(this.rowSorter, this)); 
    }

    private static class SearchDocumentListener implements DocumentListener {
        private final TableRowSorter<TableModel> rowSorter;
        private final JTextField searchTextField;
        
        public SearchDocumentListener(TableRowSorter<TableModel> rowSorter, JTextField searchTextField) {
            this.rowSorter = rowSorter;
            this.searchTextField = searchTextField;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            search();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            search();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            search();
        }

        private void search() {
            String text = searchTextField.getText().trim();
        //    System.out.println("Search: " + text);
            if (text.trim().length() == 0 || text.equals("Search")) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }
}
