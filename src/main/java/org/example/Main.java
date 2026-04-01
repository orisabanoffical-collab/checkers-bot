package org.example;

import org.example.geometrydash.GeometryDashFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GeometryDashFrame frame = new GeometryDashFrame();
            frame.setVisible(true);
        });
    }
}
