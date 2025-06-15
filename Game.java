import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Game {
    public static class Controller {
        final JFrame window;
        Model model;
        View view;

        public Controller(Model model) {
            this.window = new JFrame("Memory");
            this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.window.setResizable(false);
            this.window.setMinimumSize(new Dimension(600, 600));
            this.reset(model);
        }

        public void reset(Model model) {
            this.model = model;
            this.view = new View(model, this);
            this.window.setVisible(false);
            this.window.setContentPane(view);
            this.window.pack();
            this.window.revalidate();
            this.window.repaint();
            this.window.setLocationRelativeTo(null);
            for (JButton button : this.model.getButtons()) {
                button.addActionListener(new ButtonActionListener(this));
            }
            Utilities.timer(200, (ignored) -> this.window.setVisible(true));
        }

        public JFrame getWindow() {
            return this.window;
        }

        public Model getModel() {
            return this.model;
        }

        public View getView() {
            return this.view;
        }
    }

    public static class Model {
        static final String[] AVAILABLE_IMAGES = new String[]{"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png"};
        static final Integer MAX_REGISTERED_SCORES = 10;
        final ArrayList<Float> scores;
        final ArrayList<JButton> buttons;
        final int columns;
        int tries;
        boolean gameStarted;

        public Model(int columns) {
            this.columns = columns;
            this.buttons = new ArrayList<>();
            this.scores = new ArrayList<>();
            this.tries = 8;
            this.gameStarted = false;
            int numberOfImages = columns * columns;
            int pairsNeeded = numberOfImages / 2;
            if (numberOfImages % 2 != 0) {
                throw new IllegalArgumentException("Grid size must be even for pairing (e.g., 4x4, 6x6).");
            }
            ArrayList<String> selectedImages = new ArrayList<>();
            for (int i = 0; i < Math.min(pairsNeeded, AVAILABLE_IMAGES.length); i++) {
                selectedImages.add(AVAILABLE_IMAGES[i]);
            }
            ArrayList<String> imageList = new ArrayList<>();
            for (String image : selectedImages) {
                imageList.add(image);
                imageList.add(image);
            }
            Collections.shuffle(imageList);
            for (String reference : imageList) {
                this.buttons.add(new MemoryButton(reference));
                System.out.println("Added image: " + reference);
            }
        }

        public int getColumns() {
            return columns;
        }

        public ArrayList<JButton> getButtons() {
            return buttons;
        }

        public int getTries() {
            return tries;
        }

        public void decrementTries() {
            this.tries--;
        }

        public boolean isGameStarted() {
            return this.gameStarted;
        }

        public void startGame() {
            this.gameStarted = true;
        }
    }

    public static class View extends JPanel {
        final JLabel triesLabel;
        private final Image backgroundImage;

        public View(Model model, Controller controller) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.backgroundImage = Utilities.loadImage("hogwarts_background.png");
            this.setBackground(new Color(0x1B263B));

            this.triesLabel = new JLabel("", SwingConstants.CENTER);
            this.triesLabel.setFont(new Font("MV Boli", Font.BOLD, 24));
            this.triesLabel.setForeground(new Color(0xC0C0C0));
            this.triesLabel.setBackground(new Color(0x1B263B));
            this.triesLabel.setOpaque(true);
            this.triesLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            JPanel imagePanel = new JPanel();
            int columns = model.getColumns();
            imagePanel.setLayout(new GridLayout(columns, columns, 0, 0));
            imagePanel.setBackground(new Color(0x1B263B));
            for (JButton button : model.getButtons()) {
                imagePanel.add(button);
            }

            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new BorderLayout(20, 0));
            controlPanel.setBackground(new Color(0x1B263B));
            controlPanel.setPreferredSize(new Dimension(600, 80));
            controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Create a sub-panel for the buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonPanel.setBackground(new Color(0x1B263B));

            JButton restartButton = new JButton();
            Image restartIcon = Utilities.loadImage("restart.png");
            if (restartIcon != null) {
                restartButton.setIcon(new ImageIcon(restartIcon.getScaledInstance(38, 38, Image.SCALE_SMOOTH)));
            } else {
                restartButton.setText("Restart");
                System.out.println("Warning: restart.png not found, using text fallback");
            }
            restartButton.setPreferredSize(new Dimension(50, 50));
            restartButton.setBackground(new Color(0x1B263B));
            restartButton.setBorder(BorderFactory.createEmptyBorder());
            restartButton.setContentAreaFilled(false);
            restartButton.setBorderPainted(false);
            restartButton.setFocusPainted(false);
            restartButton.addActionListener(e -> controller.reset(new Model(model.getColumns())));
            buttonPanel.add(restartButton);

            JButton homeButton = new JButton();
            Image homeIcon = Utilities.loadImage("home.png");
            if (homeIcon != null) {
                homeButton.setIcon(new ImageIcon(homeIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            } else {
                homeButton.setText("Home");
                System.out.println("Warning: home.png not found, using text fallback");
            }
            homeButton.setPreferredSize(new Dimension(50, 50));
            homeButton.setBackground(new Color(0x1B263B));
            homeButton.setBorder(BorderFactory.createEmptyBorder());
            homeButton.setContentAreaFilled(false);
            homeButton.setBorderPainted(false);
            homeButton.setFocusPainted(false);
            homeButton.addActionListener(e -> {
                controller.getWindow().dispose();
                SwingUtilities.invokeLater(() -> new Game.HomePage());
            });
            buttonPanel.add(homeButton);

            controlPanel.add(buttonPanel, BorderLayout.WEST);
            controlPanel.add(this.triesLabel, BorderLayout.CENTER);

            controlPanel.setAlignmentX(CENTER_ALIGNMENT);
            this.add(controlPanel);
            this.add(imagePanel);
            this.setTries(model.getTries());
            this.revalidate();
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.out.println("Warning: hogwarts_background.png not found");
            }
        }

        public void setTries(int triesLeft) {
            this.triesLabel.setText("Attempts Remaining: " + triesLeft);
        }
    }

    public static class ReferencedIcon extends ImageIcon {
        final String reference;

        public ReferencedIcon(Image image, String reference) {
            super(image);
            this.reference = reference;
        }

        public String getReference() {
            return reference;
        }
    }

    public static class MemoryButton extends JButton {
        static final String IMAGE_PATH = "";
        static final Image NO_IMAGE = Utilities.loadImage("no_image.png");

        public MemoryButton(String reference) {
            Image image = Utilities.loadImage(IMAGE_PATH + reference);
            if (image == null) {
                image = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
                System.out.println("Warning: Image not found: " + reference);
            }
            Dimension dimension = new Dimension(120, 120);
            this.setPreferredSize(dimension);
            this.setIcon(new ImageIcon(NO_IMAGE != null ? NO_IMAGE : new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB)));
            this.setDisabledIcon(new ReferencedIcon(image, reference));
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setContentAreaFilled(false);
            this.setFocusPainted(false);
            this.setBorderPainted(false);
        }
    }

    public static class Dialogs {
        public static void showLoseDialog(JFrame window) {
            UIManager.put("OptionPane.background", new Color(0x1B263B));
            UIManager.put("Panel.background", new Color(0x1B263B));
            UIManager.put("OptionPane.messageForeground", new Color(0xC0C0C0));
            UIManager.put("Button.background", new Color(0x1B263B));
            UIManager.put("Button.foreground", new Color(0xC0C0C0));
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(0xC0C0C0), 2));
            UIManager.put("OptionPane.border", BorderFactory.createLineBorder(new Color(0xC0C0C0), 4));

            JOptionPane.showMessageDialog(window, "You lost, try again!", "Error", JOptionPane.INFORMATION_MESSAGE);
        }

        public static void showWinDialog(JFrame window, Model model) {
            UIManager.put("OptionPane.background", new Color(0x1B263B));
            UIManager.put("Panel.background", new Color(0x1B263B));
            UIManager.put("OptionPane.messageForeground", new Color(0xC0C0C0));
            UIManager.put("Button.background", new Color(0x1B263B));
            UIManager.put("Button.foreground", new Color(0xC0C0C0));
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(0xC0C0C0), 2));
            UIManager.put("OptionPane.border", BorderFactory.createLineBorder(new Color(0xC0C0C0), 4));

            String message = String.format("Congrats you won!!");
            JOptionPane.showMessageDialog(window.getContentPane(), message, "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static class ButtonActionListener implements ActionListener {
        final Controller controller;
        final Model model;
        final View view;
        final JFrame window;
        static int disabledButtonCount = 0;
        static JButton lastDisabledButton = null;
        static final Image TRAP_IMAGE = Utilities.loadImage("no_image.png");
        final ReferencedIcon trap;

        public ButtonActionListener(Controller controller) {
            this.controller = controller;
            this.model = controller.getModel();
            this.view = controller.getView();
            this.window = controller.getWindow();
            this.trap = new ReferencedIcon(TRAP_IMAGE != null ? TRAP_IMAGE : new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB), "no_image.png");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);
            ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
            disabledButtonCount++;
            if (!model.isGameStarted()) {
                model.startGame();
            }
            if (disabledButtonCount == 2) {
                ReferencedIcon thatIcon = (ReferencedIcon) lastDisabledButton.getDisabledIcon();
                boolean isPair = thisIcon.getReference().equals(thatIcon.getReference());
                if (!isPair) {
                    model.decrementTries();
                    view.setTries(model.getTries());
                    JButton lastButton = lastDisabledButton;
                    Utilities.timer(500, ((ignored) -> {
                        button.setEnabled(true);
                        lastButton.setEnabled(true);
                    }));
                }
                disabledButtonCount = 0;
            }
            ArrayList<JButton> enabledButtons = (ArrayList<JButton>) model.getButtons().stream().filter(Component::isEnabled).collect(Collectors.toList());
            if (enabledButtons.size() == 0) {
                controller.reset(new Model(controller.getModel().getColumns()));
                Dialogs.showWinDialog(window, model);
            }
            lastDisabledButton = button;
            if (model.getTries() == 0) {
                controller.reset(new Model(controller.getModel().getColumns()));
                Dialogs.showLoseDialog(window);
                Utilities.timer(1000, (ignored) -> model.getButtons().forEach(btn -> btn.setEnabled(false)));
            }
        }
    }

    public static class Utilities {
        static final ClassLoader cl = Utilities.class.getClassLoader();

        public static void timer(int delay, ActionListener listener) {
            Timer t = new Timer(delay, listener);
            t.setRepeats(false);
            t.start();
        }

        public static Image loadImage(String s) {
            Image image = null;
            try {
                InputStream resourceStream = cl.getResourceAsStream(s);
                if (resourceStream != null) {
                    ImageInputStream imageStream = ImageIO.createImageInputStream(resourceStream);
                    image = ImageIO.read(imageStream);
                }
            } catch (IOException e) {
                System.out.println("Failed to load image: " + s);
                e.printStackTrace();
            }
            return image != null ? image : new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public static class HomePage extends JFrame {
        public HomePage() {
            System.out.println("Initializing HomePage...");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setResizable(false);
            setMinimumSize(new Dimension(600, 600));

            JPanel panel = new JPanel() {
                private final Image backgroundImage = Utilities.loadImage("home_page.png");

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        g.setColor(new Color(0x1B263B));
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                }
            };
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(0x1B263B));

            JButton startButton = new JButton("START");
            startButton.setFont(new Font("Gineva", Font.BOLD, 17));
            startButton.setForeground(new Color(0,0,0));
            startButton.setBackground(new Color(0x1B263B));
            startButton.setBorder(BorderFactory.createEmptyBorder());
            startButton.setContentAreaFilled(false);
            startButton.setBorderPainted(false);
            startButton.setFocusPainted(false);
            startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            startButton.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new Game.Controller(new Game.Model(4)));
            });

            panel.add(Box.createVerticalStrut(219));
            panel.add(Box.createVerticalStrut(200));
            panel.add(startButton);

            add(panel);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
            System.out.println("HomePage initialized successfully.");
        }
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println("Starting application...");
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> {
            try {
                new Game.HomePage();
            } catch (Exception e) {
                System.err.println("Failed to initialize HomePage: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}