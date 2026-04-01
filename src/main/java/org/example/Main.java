package org.example;

import org.example.checkers.CheckersFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckersFrame frame = new CheckersFrame();
            frame.setVisible(true);
        });
    }
}
