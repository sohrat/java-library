import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookShelfApp bookShelfApp = new BookShelfApp();
            bookShelfApp.createAndShowGUI();
        });
    }
}