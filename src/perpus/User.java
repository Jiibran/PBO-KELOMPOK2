package perpus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role; // "admin" atau "user"

    // Constructor
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter dan Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Menyimpan user ke database
    public void save() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, role);
        stmt.executeUpdate();
        conn.close();
    }

    // Menghapus user dari database
    public static void delete(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.executeUpdate();
        conn.close();
    }

    // Mendapatkan daftar semua user
    public static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM users";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            User user = new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
            );
            users.add(user);
        }
        conn.close();
        return users;
    }

    // Autentikasi user
    public static User authenticate(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
            );
            conn.close();
            return user;
        } else {
            conn.close();
            return null;
        }
    }
}
