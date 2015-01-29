package me.matt.image;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ScreenSnapper {
    
    static JWindow window;
    
    public static void main(final String[] args) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final Painter painter = new Painter();
        window = new JWindow() {
            private static final long serialVersionUID = 1L;

            {
                painter.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(final MouseEvent event) {
                        if (event.getButton() == MouseEvent.BUTTON1) {
                            ScreenSnapper.x = (int) event.getPoint().getX();
                            ScreenSnapper.y = (int) event.getPoint().getY();
                        } else {
                            window.dispose();
                            System.exit(0);
                        }
                    }

                    @Override
                    public void mouseReleased(final MouseEvent event) {
                        painter.clean();
                        window.dispose();
                        try {
                            if ((int) event.getPoint().getX() < ScreenSnapper.x) {
                                ScreenSnapper.width = ScreenSnapper.x
                                        - (int) event.getPoint().getX();
                                ScreenSnapper.x = (int) event.getPoint().getX();
                            }
                            if ((int) event.getPoint().getY() < ScreenSnapper.y) {
                                ScreenSnapper.height = ScreenSnapper.y
                                        - (int) event.getPoint().getY();
                                ScreenSnapper.y = (int) event.getPoint().getY();
                            }
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Image saved to "
                                            + ScreenSnapper.snap(
                                                    ScreenSnapper.x,
                                                    ScreenSnapper.y,
                                                    ScreenSnapper.width,
                                                    ScreenSnapper.height,
                                                    args.length > 0 ? args[0]
                                                            : null) + "!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }

                });
                painter.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(final MouseEvent event) {
                        if (ScreenSnapper.x + ScreenSnapper.y != 0) {
                            ScreenSnapper.width = (int) event.getPoint().getX()
                                    - ScreenSnapper.x;
                            ScreenSnapper.height = (int) event.getPoint()
                                    .getY() - ScreenSnapper.y;
                            painter.update(ScreenSnapper.x, ScreenSnapper.y,
                                    ScreenSnapper.width, ScreenSnapper.height);
                        }
                    }

                });
                setContentPane(painter);
                setSize(ScreenSnapper.getVirtualScreenBounds().width,
                        ScreenSnapper.getVirtualScreenBounds().height);
                setAlwaysOnTop(true);
                setBackground(new Color(0, 0, 0, 0));
                setLocation(ScreenSnapper.getVirtualScreenBounds().x,
                        ScreenSnapper.getVirtualScreenBounds().y);
            }
        };
        window.setFocusableWindowState(false);
        window.setFocusable(false);
        window.setVisible(true);
    }
    
    public static Rectangle getVirtualScreenBounds() {
        final GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        final GraphicsDevice lstGDs[] = ge.getScreenDevices();

        final Rectangle bounds = new Rectangle();
        for (final GraphicsDevice gd : lstGDs) {
            bounds.add(gd.getDefaultConfiguration().getBounds());
        }
        return bounds;
    }


    public static File snap(final int x, final int y, final int width,
            final int height, final String location) throws Exception {
        if (width == 0 || height == 0) {
            throw new Exception("Width and Height must be greater than 0, no image saved.");
        }
        final Robot robot = new Robot();
        final BufferedImage image = robot.createScreenCapture(new Rectangle(x,
                y, width, height));
        File path = location != null ? new File(location) : new File(
                System.getProperty("user.home"));
        File desktop;
        if (location == null && (desktop = new File(path, "Desktop")).exists()) {
            path = desktop;
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss");
        final Calendar cal = Calendar.getInstance();
        ImageIO.write(
                image,
                "PNG",
                path = new File(path, "Snap-"
                        + dateFormat.format(cal.getTime()) + ".png"));
        return path;
    }

    private static int x, y, width, height;
}