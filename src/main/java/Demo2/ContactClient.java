package Demo2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ContactClient extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTable contactsTable;
    private DefaultTableModel tableModel;

    private ContactService contactService = new ContactService();

    public ContactClient() {
        setTitle("Contact Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeUI();
    }

    private void initializeUI() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(40);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField(40);
        inputPanel.add(addressField);

        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(40);
        inputPanel.add(phoneField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this::addContact);
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(this::promptForUpdate);
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::promptForDelete);
        buttonPanel.add(deleteButton);

        JButton listButton = new JButton("List");
        listButton.addActionListener(this::listContacts);
        buttonPanel.add(listButton);

        String[] columnNames = {"ID", "Name", "Address", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        contactsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void addContact(ActionEvent event) {
        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();

        contactService.addContact(name, address, phone);
        JOptionPane.showMessageDialog(this, "Contact added successfully!");
        listContacts(event);
    }

    private void promptForUpdate(ActionEvent event) {
        String contactId = JOptionPane.showInputDialog(this, "Enter Contact ID to Update:");
        if (contactId != null) {
            try {
                int id = Integer.parseInt(contactId);
                String name = nameField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();

                boolean updated = contactService.updateContact(id, name, address, phone);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "Contact updated successfully!");
                    listContacts(event);
                } else {
                    JOptionPane.showMessageDialog(this, "Contact update failed. Contact not found.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        }
    }

    private void promptForDelete(ActionEvent event) {
        String contactId = JOptionPane.showInputDialog(this, "Enter Contact ID to Delete:");
        if (contactId != null) {
            try {
                int id = Integer.parseInt(contactId);

                boolean deleted = contactService.deleteContact(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Contact deleted successfully!");
                    listContacts(event);
                } else {
                    JOptionPane.showMessageDialog(this, "Contact delete failed. Contact not found.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        }
    }

    private void listContacts(ActionEvent event) {
        ResultSet rs = contactService.listContacts();
        if (rs != null) {
            DefaultTableModel model = (DefaultTableModel) contactsTable.getModel();
            model.setRowCount(0);
            try {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("ContactId"), rs.getString("Name"), rs.getString("Address"), rs.getString("Phone")});
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error listing contacts: " + e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactClient client = new ContactClient();
            client.setVisible(true);
        });
    }
}