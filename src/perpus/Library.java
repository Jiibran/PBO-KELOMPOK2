/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package perpus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Library implements Serializable {
    private List<Book> books;
    private List<Member> members;
    private static final String FILE_NAME = "library_data.ser";

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void addBook(Book book) { books.add(book); }
    public void registerMember(Member member) { members.add(member); }
    public List<Book> getBooks() { return books; }
    public List<Member> getMembers() { return members; }

    public boolean removeBook(String bookId) {
        return books.removeIf(book -> book.getId().equals(bookId));
    }

    public boolean removeMember(String memberId) {
        return members.removeIf(member -> member.getId().equals(memberId));
    }

    public boolean borrowBook(String bookId) {
        for (Book book : books) {
            if (book.getId().equals(bookId) && !book.isBorrowed()) {
                book.borrow();
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String bookId) {
        for (Book book : books) {
            if (book.getId().equals(bookId) && book.isBorrowed()) {
                book.returnBook();
                return true;
            }
        }
        return false;
    }

    public String getBooksList() {
        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
            sb.append(book).append("\n");
        }
        return sb.toString();
    }

    public String getMembersList() {
        StringBuilder sb = new StringBuilder();
        for (Member member : members) {
            sb.append(member).append("\n");
        }
        return sb.toString();
    }

    public void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Library loadFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Library) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Library();
        }
    }
}
