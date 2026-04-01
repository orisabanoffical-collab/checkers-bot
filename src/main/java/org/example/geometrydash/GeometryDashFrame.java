package org.example.geometrydash;

import javax.swing.JFrame;

public class GeometryDashFrame extends JFrame {
    public GeometryDashFrame() {
        setTitle("Geometry Dash - Java Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GeometryDashPanel());
        pack();
        setLocationRelativeTo(null);
    }
}
