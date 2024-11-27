package perpus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class LibraryGUI extends JFrame {
    private User user;
    private DefaultTableModel booksTableModel;
    private DefaultTableModel membersTableModel;
    private DefaultTableModel borrowableBooksModel;
    private DefaultTableModel borrowedBooksModel;
    private DefaultTableModel usersTableModel;

    public LibraryGUI(User user) {
        this.user = user;

        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab "Books"
        JPanel booksPanel = createBooksPanel();
        tabbedPane.addTab("Books", booksPanel);

        // Tab "Members"
        // JPanel membersPanel = createMembersPanel();
        // tabbedPane.addTab("Members", membersPanel);

        // Tab "Borrow Book"
        JPanel borrowPanel = createBorrowBookPanel();
        tabbedPane.addTab("Borrow Book", borrowPanel);

        // Tab "Return Book"
        JPanel returnPanel = createReturnBookPanel();
        tabbedPane.addTab("Return Book", returnPanel);

        // Tab "Users" - only for admin
        if (user.getRole().equals("admin")) {
            JPanel usersPanel = createUsersPanel();
            tabbedPane.addTab("Users", usersPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            this.dispose();
            LoginGUI login = new LoginGUI();
            login.setVisible(true);
        });

        add(logoutButton, BorderLayout.SOUTH);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel untuk menampilkan buku
        booksTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Status"}, 0);
        JTable table = new JTable(booksTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk kontrol admin
        if (user.getRole().equals("admin")) {
            JPanel adminPanel = new JPanel();

            JButton addButton = new JButton("Add Book");
            addButton.addActionListener(e -> addBook());

            JButton deleteButton = new JButton("Delete Book");
            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String bookId = booksTableModel.getValueAt(selectedRow, 0).toString();
                    deleteBook(bookId);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a book to delete.");
                }
            });

            adminPanel.add(addButton);
            adminPanel.add(deleteButton);
            panel.add(adminPanel, BorderLayout.NORTH);
        }

        refreshBookList();
        return panel;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel untuk menampilkan member
        membersTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0);
        JTable table = new JTable(membersTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk kontrol admin
        if (user.getRole().equals("admin")) {
            JPanel adminPanel = new JPanel();

            JButton addButton = new JButton("Add Member");
            addButton.addActionListener(e -> addMember());

            JButton deleteButton = new JButton("Delete Member");
            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String memberId = membersTableModel.getValueAt(selectedRow, 0).toString();
                    deleteMember(memberId);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a member to delete.");
                }
            });

            adminPanel.add(addButton);
            adminPanel.add(deleteButton);
            panel.add(adminPanel, BorderLayout.NORTH);
        }

        refreshMemberList();
        return panel;
    }

    private JPanel createBorrowBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel untuk menampilkan buku yang tersedia
        borrowableBooksModel = new DefaultTableModel(new String[]{"ID", "Title", "Author"}, 0);
        JTable table = new JTable(borrowableBooksModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel();

        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String bookId = borrowableBooksModel.getValueAt(selectedRow, 0).toString();
                try {
                    Book.borrow(bookId, user.getUsername());
                    JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
                    refreshBookList();
                    refreshBorrowableBooks();
                    refreshBorrowedBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error borrowing book.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBorrowableBooks());

        buttonPanel.add(borrowButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBorrowableBooks();
        return panel;
    }

    private JPanel createReturnBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel untuk menampilkan buku yang dipinjam oleh user
        borrowedBooksModel = new DefaultTableModel(new String[]{"ID", "Title", "Author"}, 0);
        JTable table = new JTable(borrowedBooksModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel();

        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String bookId = borrowedBooksModel.getValueAt(selectedRow, 0).toString();
                try {
                    Book.returnBook(bookId, user.getUsername());
                    JOptionPane.showMessageDialog(this, "Book returned successfully!");
                    refreshBookList();
                    refreshBorrowedBooks();
                    refreshBorrowableBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error returning book.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to return.");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBorrowedBooks());

        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBorrowedBooks();
        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel untuk menampilkan user
        usersTableModel = new DefaultTableModel(new String[]{"Username", "Role"}, 0);
        JTable table = new JTable(usersTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk kontrol admin
        if (user.getRole().equals("admin")) {
            JPanel adminPanel = new JPanel();

            JButton addButton = new JButton("Add User");
            addButton.addActionListener(e -> addUser());

            JButton deleteButton = new JButton("Delete User");
            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String username = usersTableModel.getValueAt(selectedRow, 0).toString();
                    deleteUser(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a user to delete.");
                }
            });

            adminPanel.add(addButton);
            adminPanel.add(deleteButton);
            panel.add(adminPanel, BorderLayout.NORTH);
        }

        refreshUserList();
        return panel;
    }

    private void addBook() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        Object[] message = {
            "Book ID:", idField,
            "Title:", titleField,
            "Author:", authorField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            try {
                Book book = new Book(id, title, author);
                book.save();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                refreshBookList();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding book.");
            }
        }
    }

    private void deleteBook(String bookId) {
        try {
            Book.delete(bookId);
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
            refreshBookList();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book.");
        }
    }

    private void addMember() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        Object[] message = {
            "Member ID:", idField,
            "Name:", nameField,
            "Email:", emailField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            try {
                Member member = new Member(id, name, email);
                member.save();
                JOptionPane.showMessageDialog(this, "Member added successfully!");
                refreshMemberList();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding member.");
            }
        }
    }

    private void deleteMember(String memberId) {
        try {
            Member.delete(memberId);
            JOptionPane.showMessageDialog(this, "Member deleted successfully!");
            refreshMemberList();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting member.");
        }
    }

    private void refreshBookList() {
        try {
            booksTableModel.setRowCount(0);
            List<Book> books = Book.getAllBooks();
            for (Book book : books) {
                booksTableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.isBorrowed() ? "Borrowed" : "Available"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // private void refreshMemberList() {
    //     try {
    //         membersTableModel.setRowCount(0);
    //         List<Member> members = Member.getAllMembers();
    //         for (Member member : members) {
    //             membersTableModel.addRow(new Object[]{
    //                 member.getId(),
    //                 member.getName(),
    //                 member.getEmail()
    //             });
    //         }
    //     } catch (SQLException ex) {
    //         ex.printStackTrace();
    //     }
    // }

    private void refreshBorrowableBooks(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            List<Book> books = Book.getAllBooks();
            for (Book book : books) {
                if (!book.isBorrowed()) {
                    model.addRow(new Object[]{
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor()
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBorrowedBooks(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            List<Book> books = Book.getBorrowedBooksByUser(user.getUsername());
            for (Book book : books) {
                model.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBorrowableBooks() {
        try {
            borrowableBooksModel.setRowCount(0); // Clear existing data
            List<Book> books = Book.getAllBooks();
            for (Book book : books) {
                if (!book.isBorrowed()) {
                    borrowableBooksModel.addRow(new Object[]{
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor()
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBorrowedBooks() {
        try {
            borrowedBooksModel.setRowCount(0); // Clear existing data
            List<Book> books = Book.getBorrowedBooksByUser(user.getUsername());
            for (Book book : books) {
                borrowedBooksModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        String[] roles = { "admin", "user" };
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Role:", roleComboBox
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleComboBox.getSelectedItem().toString();
            try {
                User newUser = new User(username, password, role);
                newUser.save();
                JOptionPane.showMessageDialog(this, "User added successfully!");
                refreshUserList();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding user.");
            }
        }
    }

    private void deleteUser(String username) {
        try {
            User.delete(username);
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            refreshUserList();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user.");
        }
    }

    private void refreshUserList() {
        try {
            usersTableModel.setRowCount(0);
            List<User> users = User.getAllUsers();
            for (User u : users) {
                usersTableModel.addRow(new Object[]{
                    u.getUsername(),
                    u.getRole()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
