package Demo1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

public class ContactClient extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTable contactsTable;
    private DefaultTableModel tableModel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ContactClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            setTitle("Contact Client");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            initializeUI();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to connect to server.");
            System.exit(1);
        }
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

        sendRequest("ADD," + name + "," + address + "," + phone);
        sendRequest("LIST");
    }

    private void promptForUpdate(ActionEvent event) {
        String contactId = JOptionPane.showInputDialog(this, "Enter Contact ID to Update:");
        if (contactId != null) {
            int id = Integer.parseInt(contactId);
            String name = nameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();

            sendRequest("UPDATE," + id + "," + name + "," + address + "," + phone);
            sendRequest("LIST");
        }
    }

    private void promptForDelete(ActionEvent event) {
        String contactId = JOptionPane.showInputDialog(this, "Enter Contact ID to Delete:");
        if (contactId != null) {
            int id = Integer.parseInt(contactId);

            sendRequest("DELETE," + id);
            sendRequest("LIST");
        }
    }

    private void listContacts(ActionEvent event) {
        sendRequest("LIST");
    }

    private void sendRequest(String request) {
        out.println(request);
        try {
            String response = in.readLine();
            if (response.startsWith("Error:")) {
                JOptionPane.showMessageDialog(this, response.substring(6));
            } else if (request.startsWith("ADD")) {
                JOptionPane.showMessageDialog(this, "Contact added successfully!");
            } else if (request.startsWith("LIST")) {
                DefaultTableModel model = (DefaultTableModel) contactsTable.getModel();
                model.setRowCount(0);
                String[] contacts = response.split(",");
                for (String contact : contacts) {
                    String[] data = contact.split(";");
                    if (data.length == 4) {
                        model.addRow(new Object[]{data[0], data[1], data[2], data[3]});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error communicating with the server.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactClient client = new ContactClient("localhost", 6789);
            client.setVisible(true);
        });
    }
}