import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookShelfApp {
    private final String MAIN_PANEL = "Main Panel";
    private final String SEARCH_PANEL = "Search Panel";
    private final String ADD_BOOK_PANEL = "Add Book Panel";
    private final String RETURN_BOOK_PANEL = "Return Book Panel";
    private static final String BOOKSHELF_PANEL = "BookShelf Panel";

    private CardLayout cardLayout;
    private JPanel cards;
    private JFrame frame;

    private GridBagConstraints createBackButtonConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        return c;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Book Shelf");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(buildMainPanel(), MAIN_PANEL);
        cards.add(buildSearchPanel(), SEARCH_PANEL);
        cards.add(buildAddBookPanel(), ADD_BOOK_PANEL);
        cards.add(buildReturnBookPanel(), RETURN_BOOK_PANEL);
        cards.add(buildBookShelfPanel(), BOOKSHELF_PANEL);
        cardLayout.show(cards, MAIN_PANEL);
        frame.add(cards);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 1;

        // Add the logo to the center of the panel
        ImageIcon logo = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logo);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER; // Span the entire row
        mainPanel.add(logoLabel, c);

        // Add the buttons below the logo
        c.gridwidth = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;

        JButton searchButton = new JButton("Search");
        c.gridx = 0;
        mainPanel.add(searchButton, c);

        JButton addBookButton = new JButton("Add Book");
        c.gridx = 1;
        mainPanel.add(addBookButton, c);

        JButton bookShelfButton = new JButton("Bookshelf");
        c.gridx = 2;
        mainPanel.add(bookShelfButton, c);

        JButton returnBookButton = new JButton("Return Book");
        c.gridx = 3;
        mainPanel.add(returnBookButton, c);

        // Set the action listeners for the buttons
        searchButton.addActionListener(e -> cardLayout.show(cards, SEARCH_PANEL));
        bookShelfButton.addActionListener(e -> cardLayout.show(cards, BOOKSHELF_PANEL));
        addBookButton.addActionListener(e -> cardLayout.show(cards, ADD_BOOK_PANEL));
        returnBookButton.addActionListener(e -> cardLayout.show(cards, RETURN_BOOK_PANEL));

        return mainPanel;
    }


    private JPanel buildSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(cards, MAIN_PANEL));
        searchPanel.add(backButton);

        // Search field and button
        JPanel searchInputPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchPanel.add(searchInputPanel);

        // Radio buttons for search criteria
        JPanel radioButtonsPanel = new JPanel(new FlowLayout());
        JRadioButton byBookNameButton = new JRadioButton("By Book Name", true);
        JRadioButton byAuthorNameButton = new JRadioButton("By Author Name");
        JRadioButton byISBNButton = new JRadioButton("By ISBN");
        ButtonGroup searchCriteriaGroup = new ButtonGroup();
        searchCriteriaGroup.add(byBookNameButton);
        searchCriteriaGroup.add(byAuthorNameButton);
        searchCriteriaGroup.add(byISBNButton);
        radioButtonsPanel.add(byBookNameButton);
        radioButtonsPanel.add(byAuthorNameButton);
        radioButtonsPanel.add(byISBNButton);
        searchPanel.add(radioButtonsPanel);

        // Search results table
        String[] columnNames = {"Book Name", "Author", "ISBN", "Genre"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable searchResultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        searchPanel.add(scrollPane);

        // Search button action
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            tableModel.setRowCount(0); // Clear the table

            try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    String[] bookData = line.split(";");

                    boolean match = searchTerm.isEmpty()
                            || (byBookNameButton.isSelected() && bookData[2].toLowerCase().contains(searchTerm))
                            || (byAuthorNameButton.isSelected() && bookData[3].toLowerCase().contains(searchTerm))
                            || (byISBNButton.isSelected() && bookData[7].toLowerCase().contains(searchTerm));

                    if (match) {
                        found = true;
                        tableModel.addRow(new Object[]{
                                bookData[2],
                                bookData[3],
                                bookData[7],
                                bookData[4]
                        });
                    }
                }

                if (!found) {
                    JOptionPane.showMessageDialog(frame, "No books found with the given search keyword.");
                }

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return searchPanel;
    }

    private JPanel buildBookShelfPanel() {
        JPanel bookShelfPanel = new JPanel();
        bookShelfPanel.setLayout(new BoxLayout(bookShelfPanel, BoxLayout.Y_AXIS));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(cards, MAIN_PANEL));
        bookShelfPanel.add(backButton);

        // Bookshelf table
        String[] columnNames = {"Book ID", "Book Name", "Author", "ISBN", "Date Added", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This will make all cells non-editable
            }
        };
        JTable bookShelfTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookShelfTable);
        bookShelfPanel.add(scrollPane);

        // Populate table with book data
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(";");
                tableModel.addRow(new Object[]{
                        bookData[0], // Add the Book ID to the table
                        bookData[2],
                        bookData[3],
                        bookData[7],
                        bookData[5],
                        bookData[8]
                });
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Add MouseListener for double-clicks
        bookShelfTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookShelfTable.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2 && row != -1) {
                    String bookId = bookShelfTable.getValueAt(row, 0).toString(); // Get the Book ID from the table
                    String bookName = bookShelfTable.getValueAt(row, 1).toString();
                    String author = bookShelfTable.getValueAt(row, 2).toString();
                    int quantity = Integer.parseInt(bookShelfTable.getValueAt(row, 5).toString());
                    showBorrowPopup(bookId, bookName, author, quantity, tableModel);
                }
            }
        });

        return bookShelfPanel;
    }

    private void showBorrowPopup(String bookId, String bookName, String author, int quantity, DefaultTableModel booksTableModel) {
        if (quantity < 1) {
            JOptionPane.showMessageDialog(frame, "No available copies to borrow.");
            return;
        }

        JPanel borrowPanel = new JPanel(new GridLayout(6, 2));
        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField();
        borrowPanel.add(fullNameLabel);
        borrowPanel.add(fullNameField);
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JTextField phoneNumberField = new JTextField();
        borrowPanel.add(phoneNumberLabel);
        borrowPanel.add(phoneNumberField);
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        borrowPanel.add(emailLabel);
        borrowPanel.add(emailField);
        JLabel returnDateLabel = new JLabel("Date of Return:");
        JTextField returnDateField = new JTextField();
        borrowPanel.add(returnDateLabel);
        borrowPanel.add(returnDateField);
        int result = JOptionPane.showConfirmDialog(null, borrowPanel, "Borrow Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String fullName = fullNameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String email = emailField.getText();
            String returnDate = returnDateField.getText();

            // Generate borrow ID
            String borrowId = createID("booksBorrowed.txt");

            // Get current date as borrow date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String borrowDate = sdf.format(new Date());

            // Save data to booksBorrowed.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("booksBorrowed.txt", true))) {
                writer.write(String.join(";", borrowId, bookId, bookName, author, fullName, phoneNumber, email, borrowDate, returnDate));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Decrement the book quantity by 1
            updateBookQuantity(bookId, -1);

            // Refresh the table
            DefaultTableModel tableModel = (DefaultTableModel) booksTableModel;
            tableModel.setRowCount(0); // Clear the table

            try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] bookData = line.split(";");
                    tableModel.addRow(new Object[]{
                            bookData[0], // Add the Book ID to the table
                            bookData[2],
                            bookData[3],
                            bookData[7],
                            bookData[5],
                            bookData[8]
                    });
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(frame, "Book successfully borrowed!");
        }
    }

    private JPanel buildAddBookPanel() {
        JPanel addBookPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = createBackButtonConstraints();

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.addActionListener(e -> cardLayout.show(cards, MAIN_PANEL));
        addBookPanel.add(backButton, c);

        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.LINE_START;

        // First row: ID and Date Added (non-editable)
        addLabel(addBookPanel, "ID:", c, 0, 1);
        String bookID = createID("books.txt");
        JTextField idField = new JTextField(bookID);
        idField.setEditable(false);
        addTextField(addBookPanel, idField, c, 0, 2, 200, 30);

        addLabel(addBookPanel, "Date Added:", c, 1, 1);
        JTextField dateAddedField = new JTextField(getCurrentDate());
        dateAddedField.setEditable(false);
        addTextField(addBookPanel, dateAddedField, c, 1, 2, 200, 30);

        // Second row: Book Name and Author Name
        addLabel(addBookPanel, "Book Name:", c, 0, 3);
        JTextField bookNameField = new JTextField();
        addTextField(addBookPanel, bookNameField, c, 0, 4, 200, 30);

        addLabel(addBookPanel, "Author Name:", c, 1, 3);
        JTextField authorNameField = new JTextField();
        addTextField(addBookPanel, authorNameField, c, 1, 4, 200, 30);

        // Third row: Genre, Publication Year, and Edition
        addLabel(addBookPanel, "Genre:", c, 0, 5);
        JTextField genreField = new JTextField();
        addTextField(addBookPanel, genreField, c, 0, 6, 200, 30);

        addLabel(addBookPanel, "Publication Year:", c, 1, 5);
        JTextField publicationYearField = new JTextField();
        addTextField(addBookPanel, publicationYearField, c, 1, 6, 200, 30);

        addLabel(addBookPanel, "Edition:", c, 2, 5);
        JTextField editionField = new JTextField();
        addTextField(addBookPanel, editionField, c, 2, 6, 200, 30);

        // Fourth row: ISBN, Quantity, and Publication Name
        addLabel(addBookPanel, "ISBN:", c, 0, 7);
        JTextField isbnField = new JTextField();
        addTextField(addBookPanel, isbnField, c, 0, 8, 200, 30);

        addLabel(addBookPanel, "Quantity:", c, 1, 7);
        JTextField quantityField = new JTextField();
        addTextField(addBookPanel, quantityField, c, 1, 8, 200, 30);

        addLabel(addBookPanel, "Publication Name:", c, 2, 7);
        JTextField publicationNameField = new JTextField();
        addTextField(addBookPanel, publicationNameField, c, 2, 8, 200, 30);

        // Reset button
        JButton resetButton = new JButton("Reset");
        c.gridx = 0;
        c.gridy = 9;
        resetButton.addActionListener(e -> {
            bookNameField.setText("");
            authorNameField.setText("");
            genreField.setText("");
            publicationYearField.setText("");
            editionField.setText("");
            isbnField.setText("");
            quantityField.setText("");
            publicationNameField.setText("");
        });
        addBookPanel.add(resetButton, c);

        // Add Book button
        JButton addBookButton = new JButton("Add Book");
        c.gridx = 1;
        c.gridy = 9;
        addBookButton.addActionListener(e -> {
            saveBookToFile(idField.getText(), dateAddedField.getText(), bookNameField.getText(), authorNameField.getText(), genreField.getText(),
                    publicationYearField.getText(), editionField.getText(), isbnField.getText(),
                    quantityField.getText(), publicationNameField.getText());
            JOptionPane.showMessageDialog(frame, "Book saved successfully!");
            refreshAddBookPanel(idField, dateAddedField, bookNameField, authorNameField, genreField,
                    publicationYearField, editionField, isbnField, quantityField, publicationNameField);
        });
        addBookPanel.add(addBookButton, c);

        // Add a filler component to push the back button to the top
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 3;
        addBookPanel.add(new JLabel(""), c);

        return addBookPanel;
    }

    private void addLabel(JPanel panel, String labelText, GridBagConstraints c, int x, int y) {
        JLabel label = new JLabel(labelText);
        c.gridx = x;
        c.gridy = y;
        panel.add(label, c);
    }

    private void addTextField(JPanel panel, JTextField textField, GridBagConstraints c, int x, int y, int width, int height) {
        textField.setPreferredSize(new Dimension(width, height));
        c.gridx = x;
        c.gridy = y;
        panel.add(textField, c);
    }

    private void saveBookToFile(String id, String dateAdded, String bookName, String authorName, String genre, String publicationYear, String edition, String isbn, String quantity, String publicationName) {
        String separator = ";";
        String newLine = System.lineSeparator();
        String bookData = id + separator + dateAdded + separator + bookName + separator + authorName + separator + genre + separator + publicationYear + separator + edition + separator + isbn + separator + quantity + separator + publicationName + newLine;
        File file = new File("books.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(bookData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshAddBookPanel(JTextField idField, JTextField dateAddedField, JTextField bookNameField,
                                     JTextField authorNameField, JTextField genreField, JTextField publicationYearField,
                                     JTextField editionField, JTextField isbnField, JTextField quantityField,
                                     JTextField publicationNameField) {
        // Reset all fields except the ID and date added
        bookNameField.setText("");
        authorNameField.setText("");
        genreField.setText("");
        publicationYearField.setText("");
        editionField.setText("");
        isbnField.setText("");
        quantityField.setText("");
        publicationNameField.setText("");

        // Update the ID and date added fields with new values
        String id = createID("booksBorrowed.txt");
        dateAddedField.setText(getCurrentDate());
    }

    private String createID(String fileName) {
        int lastId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                int currentId = Integer.parseInt(data[0]);
                if (currentId > lastId) {
                    lastId = currentId;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("%07d", lastId + 1);
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private JPanel buildReturnBookPanel() {
        JPanel returnBookPanel = new JPanel();
        returnBookPanel.setLayout(new BoxLayout(returnBookPanel, BoxLayout.Y_AXIS));
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(cards, MAIN_PANEL));
        returnBookPanel.add(backButton);

        String[] columnNames = {"ID","Book ID", "Book Name", "Borrower", "Phone", "Date of Borrow", "Date of Return"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This will make all cells non-editable
            }
        };
        JTable borrowedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        returnBookPanel.add(scrollPane);

        try (BufferedReader reader = new BufferedReader(new FileReader("booksBorrowed.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(";");
                tableModel.addRow(new Object[]{
                        bookData[0],
                        bookData[1],
                        bookData[2],
                        bookData[4],
                        bookData[5],
                        bookData[7],
                        bookData[8]
                });
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        borrowedBooksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = borrowedBooksTable.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2 && row != -1) {
                    int result = JOptionPane.showConfirmDialog(frame, "Do you want to return this book?", "Return Book", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        String bookId = borrowedBooksTable.getValueAt(row, 0).toString();
                        removeBorrowedBook(bookId);
                        updateBookQuantity(bookId, 0);
                        tableModel.removeRow(row);
                        refreshTable(tableModel, "booksBorrowed.txt");
                        // Update book quantity in book.txt
                        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
                            String line;
                            List<String> lines = new ArrayList<>();
                            while ((line = reader.readLine()) != null) {
                                String[] bookData = line.split(";");
                                if (bookData[0].equals(bookId)) {
                                    int quantity = Integer.parseInt(bookData[8]) + 1;
                                    bookData[8] = Integer.toString(quantity);
                                    line = String.join(";", bookData);
                                }
                                lines.add(line);
                            }
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
                                for (String l : lines) {
                                    writer.write(l);
                                    writer.newLine();
                                }
                            }
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        return returnBookPanel;
    }

    private void updateBookQuantity(String bookId, int quantity) {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(";");
                if (bookData[0].equals(bookId)) {
                    int currentQuantity = Integer.parseInt(bookData[8]);
                    int newQuantity = currentQuantity + quantity;
                    bookData[8] = Integer.toString(newQuantity);
                    line = String.join(";", bookData);
                }
                lines.add(line);
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"));
            for (String newLine : lines) {
                writer.write(newLine + "\n");
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void removeBorrowedBook(String bookId) {
        deleteLineFromFile("booksBorrowed.txt", bookId);
    }

    private void deleteLineFromFile(String fileName, String target) {
        File inputFile = new File(fileName);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(target)) {
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete()) {
            System.out.println("Could not delete the original file");
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename the temporary file");
        }
    }

    private void refreshTable(DefaultTableModel tableModel, String fileName) {
        tableModel.setRowCount(0); // Clear the table

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(";");
                tableModel.addRow(new Object[]{
                        bookData[0],
                        bookData[1],
                        bookData[2],
                        bookData[4],
                        bookData[5],
                        bookData[7],
                        bookData[8]
                });
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}