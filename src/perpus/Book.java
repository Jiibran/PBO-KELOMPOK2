package perpus;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private boolean isBorrowed;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isBorrowed() { return isBorrowed; }

    public void borrow() { this.isBorrowed = true; }
    public void returnBook() { this.isBorrowed = false; }

    public void save() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO books (id, title, author, isBorrowed) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, id);
        stmt.setString(2, title);
        stmt.setString(3, author);
        stmt.setBoolean(4, isBorrowed);
        stmt.executeUpdate();
        conn.close();
    }

    public static List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM books";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Book book = new Book(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("author")
            );
            book.setBorrowed(rs.getBoolean("isBorrowed"));
            books.add(book);
        }
        conn.close();
        return books;
    }

    // Menambahkan metode delete
    public static void delete(String id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE FROM books WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, id);
        stmt.executeUpdate();
        conn.close();
    }

    // Menambahkan metode borrow
    public static void borrow(String bookId, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String updateBookQuery = "UPDATE books SET isBorrowed = TRUE WHERE id = ?";
        PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
        updateBookStmt.setString(1, bookId);
        updateBookStmt.executeUpdate();

        String insertBorrowingQuery = "INSERT INTO borrowings (book_id, username) VALUES (?, ?)";
        PreparedStatement insertBorrowingStmt = conn.prepareStatement(insertBorrowingQuery);
        insertBorrowingStmt.setString(1, bookId);
        insertBorrowingStmt.setString(2, username);
        insertBorrowingStmt.executeUpdate();

        conn.close();
    }

    // Menambahkan metode returnBook
    public static void returnBook(String bookId, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String updateBookQuery = "UPDATE books SET isBorrowed = FALSE WHERE id = ?";
        PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
        updateBookStmt.setString(1, bookId);
        updateBookStmt.executeUpdate();

        String deleteBorrowingQuery = "DELETE FROM borrowings WHERE book_id = ? AND username = ?";
        PreparedStatement deleteBorrowingStmt = conn.prepareStatement(deleteBorrowingQuery);
        deleteBorrowingStmt.setString(1, bookId);
        deleteBorrowingStmt.setString(2, username);
        deleteBorrowingStmt.executeUpdate();

        conn.close();
    }

    public static List<Book> getBorrowedBooksByUser(String username) throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT b.* FROM books b INNER JOIN borrowings br ON b.id = br.book_id WHERE br.username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Book book = new Book(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("author")
            );
            book.setBorrowed(rs.getBoolean("isBorrowed"));
            books.add(book);
        }
        conn.close();
        return books;
    }

    @Override
    public String toString() {
        return "Book[ID=" + id + ", Title=" + title + ", Author=" + author + "]";
    }

    public void setBorrowed(boolean isBorrowed) {
        this.isBorrowed = isBorrowed;
    }
}
