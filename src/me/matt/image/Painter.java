package me.matt.image;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Painter extends JPanel {

    private static final long serialVersionUID = 1L;

    private int x = 0, y = 0, width = 0, height = 0;

    private boolean dispose = false;

    public Painter() {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void clean() {
        dispose = true;
        this.repaint();
    }

    @Override
    public void paint(final Graphics graphics) {
        if (dispose) {
            return;
        }
        final Graphics2D g = (Graphics2D) graphics;
        g.setColor(new Color(0, 0, 0, 1));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", 0, 12));
        final FontMetrics fm = g.getFontMetrics();
        g.setClip(x, y, width, height);
        g.drawString(
                "Width: " + (width > 0 ? width : -width) + "px",
                x
                        + (width > 0 ? width - 5 : -5)
                        - (int) fm
                                .getStringBounds(
                                        "Width: "
                                                + (width > 0 ? width : -width)
                                                + "px", g).getWidth(),
                y
                        + (height > 0 ? height : 0)
                        - (int) fm.getStringBounds(
                                "Height: " + (height > 0 ? height : -height)
                                        + "px", g).getHeight() - 5);
        g.drawString(
                "Height: " + (height > 0 ? height : -height) + "px",
                x
                        + (width > 0 ? width - 5 : 0 - 5)
                        - (int) fm.getStringBounds(
                                "Height: " + (height > 0 ? height : -height)
                                        + "px", g).getWidth(), y
                        + (height > 0 ? height : 0) - 5);
        g.dispose();
    }

    public void update(final int x, final int y, final int width,
            final int height) {
        this.x = width > 0 ? x : x + width;
        this.y = height > 0 ? y : y + height;
        this.width = Math.abs(width);
        this.height = Math.abs(height);
        this.repaint();
    }
}
