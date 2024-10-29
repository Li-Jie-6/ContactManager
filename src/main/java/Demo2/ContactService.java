package Demo2;

import java.sql.*;

public class ContactService {
    private ContactDAO contactDAO = new ContactDAO();

    public void addContact(String name, String address, String phone) {
        try {
            contactDAO.addContact(name, address, phone);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateContact(int contactId, String name, String address, String phone) {
        try {
            return contactDAO.updateContact(contactId, name, address, phone);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteContact(int contactId) {
        try {
            return contactDAO.deleteContact(contactId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet listContacts() {
        try {
            // 注意：这里不关闭Connection，ResultSet将依赖于这个连接
            Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ContactManager;user=sa;password=Lj20040204;");
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * FROM Contacts");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}