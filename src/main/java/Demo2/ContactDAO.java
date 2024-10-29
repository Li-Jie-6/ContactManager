package Demo2;

import java.sql.*;

public class ContactDAO {
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;";

    public void addContact(String name, String address, String phone) throws SQLException {
        String sql = "INSERT INTO Contacts (Name, Address, Phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();
        }
    }

    public boolean updateContact(int contactId, String name, String address, String phone) throws SQLException {
        String sql = "UPDATE Contacts SET Name = ?, Address = ?, Phone = ? WHERE ContactId = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phone);
            pstmt.setInt(4, contactId);
            int updatedRows = pstmt.executeUpdate();
            return updatedRows > 0;
        }
    }

    public boolean deleteContact(int contactId) throws SQLException {
        String sql = "DELETE FROM Contacts WHERE ContactId = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            int deletedRows = pstmt.executeUpdate();
            return deletedRows > 0;
        }
    }

    public ResultSet listContacts() throws SQLException {
        String sql = "SELECT * FROM Contacts";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            return stmt.executeQuery(sql);
        }
    }
}