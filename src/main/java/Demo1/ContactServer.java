package Demo1;

import java.io.*;
import java.net.*;
import java.sql.*;

public class ContactServer {
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ContactServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is running and waiting for client connection...");
    }

    public void startServer() {
        try {
            socket = serverSocket.accept();
            System.out.println("Client connected.");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] data = inputLine.split(",");
                handleRequest(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRequest(String[] data) {
        if (data.length == 0) {
            out.println("Error: No operation specified.");
            return;
        }

        String operation = data[0];
        String name = "";
        String address = "";
        String phone = "";
        int contactId = -1;

        switch (operation) {
            case "ADD":
                if (data.length < 4) {
                    out.println("Error: Not enough parameters for ADD operation.");
                    return;
                }
                name = data[1];
                address = data[2];
                phone = data[3];
                addContact(name, address, phone);
                break;
            case "UPDATE":
                if (data.length < 5) {
                    out.println("Error: Not enough parameters for UPDATE operation.");
                    return;
                }
                contactId = Integer.parseInt(data[1]);
                name = data[2];
                address = data[3];
                phone = data[4];
                updateContact(contactId, name, address, phone);
                break;
            case "DELETE":
                if (data.length < 2) {
                    out.println("Error: No contact ID specified for DELETE operation.");
                    return;
                }
                contactId = Integer.parseInt(data[1]);
                deleteContact(contactId);
                break;
            case "LIST":
                listContacts();
                break;
            default:
                out.println("Error: Unknown operation.");
                break;
        }
    }

    private void addContact(String name, String address, String phone) {
        String sql = "INSERT INTO Contacts (Name, Address, Phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();
            out.println("Contact added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private void updateContact(int contactId, String name, String address, String phone) {
        String sql = "UPDATE Contacts SET Name = ?, Address = ?, Phone = ? WHERE ContactId = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phone);
            pstmt.setInt(4, contactId);
            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                out.println("Contact updated successfully!");
            } else {
                out.println("Contact update failed. Contact not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private void deleteContact(int contactId) {
        String sql = "DELETE FROM Contacts WHERE ContactId = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            int deletedRows = pstmt.executeUpdate();
            if (deletedRows > 0) {
                out.println("Contact deleted successfully!");
            } else {
                out.println("Contact delete failed. Contact not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private void listContacts() {
        String sql = "SELECT * FROM Contacts";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            StringBuilder response = new StringBuilder();
            while (rs.next()) {
                response.append(rs.getInt("ContactId")).append(";")
                        .append(rs.getString("Name")).append(";")
                        .append(rs.getString("Address")).append(";")
                        .append(rs.getString("Phone")).append(",");
            }
            out.println(response.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new ContactServer(6789).startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}