package me.matt.image;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ScreenSnapper {

	private static int x, y, width, height;

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final Painter painter = new Painter();
		JWindow window = new JWindow() {
			private static final long serialVersionUID = 1L;

			{
				painter.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent event) {
						if (event.getButton() == MouseEvent.BUTTON1) {
							x = (int) event.getPoint().getX();
							y = (int) event.getPoint().getY();
						} else {
							dispose();
							System.exit(0);
						}
					}

					@Override
					public void mouseReleased(MouseEvent event) {
						dispose();
						try {
							if ((int) event.getPoint().getX() < x) {
								width = x - (int) event.getPoint().getX();
								x = (int) event.getPoint().getX();
							}
							if ((int) event.getPoint().getY() < y) {
								height = y - (int) event.getPoint().getY();
								y = (int) event.getPoint().getY();
							}
							JOptionPane.showMessageDialog(
									null,
									"Image saved to "
											+ snap(x, y, width, height) + "!");
						} catch (AWTException | IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					}

				});
				painter.addMouseMotionListener(new MouseAdapter() {
					@Override
					public void mouseDragged(MouseEvent event) {
						if (x + y != 0) {
							width = (int) event.getPoint().getX() - x;
							height = (int) event.getPoint().getY() - y;
							painter.update(x, y, width, height);
						}
					}

				});
				setContentPane(painter);
				setSize(getVirtualScreenBounds().width,
						getVirtualScreenBounds().height);
				setAlwaysOnTop(true);
				setBackground(new Color(0, 0, 0, 0));
				setLocation(getVirtualScreenBounds().x,
						getVirtualScreenBounds().y);
			}
		};
		window.setVisible(true);
	}

	public static Rectangle getVirtualScreenBounds() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice lstGDs[] = ge.getScreenDevices();

		Rectangle bounds = new Rectangle();
		for (GraphicsDevice gd : lstGDs) {
			bounds.add(gd.getDefaultConfiguration().getBounds());
		}
		return bounds;
	}

	public static File snap(int x, int y, int width, int height)
			throws AWTException, IOException {
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(new Rectangle(x, y,
				width, height));
		File path = new File(System.getProperty("user.home"));
		File test;
		if ((test = new File(path, "Desktop")).exists()) {
			path = test;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd_HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		ImageIO.write(
				image,
				"PNG",
				path = new File(path, "Snap-"
						+ dateFormat.format(cal.getTime()) + ".png"));
		return path;
	}
}

class Painter extends JPanel {

	private static final long serialVersionUID = 1L;

	private int x = 0, y = 0, width = 0, height = 0;

	public Painter() {
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}

	public void update(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		repaint();
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(new Color(0, 0, 0, 1));
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(new Color(0, 0, 0, 50));
		g.fillRect(x, y, width, height);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Tahoma", 0, 12));
		FontMetrics fm = g.getFontMetrics();
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
}