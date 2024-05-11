package icm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class IcmCalcView implements Runnable {

    private static final String DOUBLE_REGEX = "[0-9]{1,13}(\\.[0-9]*)?";

    JFrame frame;
    Container contentPane;

    GridBagLayout layout;
    GridBagConstraints gbc;

    // 1 Top Labels
    JLabel playerStacksLabel;
    JLabel playerPayoutsLabel;

    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu helpMenu;
    JMenuItem loadStacksFromFile;
    JMenuItem aboutMenuItem;

    // 2 Player stacks & prize payouts lists
    DefaultListModel<Double> playerStacksModel;
    DefaultListModel<Double> playerPayoutsModel;
    JList<Double> playerStacksList;
    JList<Double> playerPayoutsList;
    JScrollPane playerStackScroller;
    JScrollPane playerPayoutScroller;

    // 3 Components for input/output row
    JPanel buttonAndFieldPanel;

    BoxLayout boxLayout;

    JTextField playerStacksInput;
    JTextField playerPayoutsInput;

    JButton addPlayerButton;
    JButton removePlayerButton;
    JButton addPayoutButton;
    JButton removePayoutButton;

    // 4 Results text area
    JTextArea resultsTextArea;
    JScrollPane resultsScroller;

    // 5 Components for Calculate button row
    JPanel separator;
    JButton calculateButton;


    private IcmCalcModel model;

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     *
     * @return
     */
    public void createAndShowGUI() {
        model = new IcmCalcModel();


//        JMenuBar menuBar;
//        JMenu fileMenu;
//        JMenu helpMenu;
//        JMenuItem loadStacksFromFile;
//        JMenuItem aboutMenuItem;

        //Create and set up the window.
        frame = new JFrame("ICM Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = frame.getContentPane();

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        loadStacksFromFile = new JMenuItem("Load stacks...");
        aboutMenuItem = new JMenuItem("About");
        fileMenu.add(loadStacksFromFile);
        helpMenu.add(aboutMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        frame.setLayout(layout);

        // J Gui Items
        playerStacksLabel = new JLabel("Player Stacks");
        playerPayoutsLabel = new JLabel("Prize Payouts");

        playerStacksModel = new DefaultListModel<>();
        playerPayoutsModel = new DefaultListModel<>();
        playerStacksList = new JList<>(playerStacksModel);
        playerPayoutsList = new JList<>(playerPayoutsModel);
        playerStacksList.setLayoutOrientation(JList.VERTICAL);
        playerPayoutsList.setLayoutOrientation(JList.VERTICAL);

        buttonAndFieldPanel = new JPanel();

        playerStacksInput = new JTextField();
        playerPayoutsInput = new JTextField();

        addPlayerButton = new JButton("+");
        removePlayerButton = new JButton("-");
        addPayoutButton = new JButton("+");
        removePayoutButton = new JButton("-");

        boxLayout = new BoxLayout(buttonAndFieldPanel, BoxLayout.X_AXIS);
        buttonAndFieldPanel.setLayout(boxLayout);

        buttonAndFieldPanel.add(playerStacksInput);
        buttonAndFieldPanel.add(addPlayerButton);
        buttonAndFieldPanel.add(removePlayerButton);
        buttonAndFieldPanel.add(playerPayoutsInput);
        buttonAndFieldPanel.add(addPayoutButton);
        buttonAndFieldPanel.add(removePayoutButton);

        resultsTextArea = new JTextArea(10, 10);
        resultsTextArea.setText("Results will appear here... ");
        resultsTextArea.setEditable(false);
        resultsTextArea.setLineWrap(true);

        playerStackScroller = new JScrollPane(playerStacksList);
        playerPayoutScroller = new JScrollPane(playerPayoutsList);
        resultsScroller = new JScrollPane(resultsTextArea);

        separator = new JPanel();
        calculateButton = new JButton("Calculate");

        // 1 x 3 for Labels
        gbc.gridwidth = 3;
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(playerStacksLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        contentPane.add(playerPayoutsLabel, gbc);

        // 3 x 3 for scroll lists
        gbc.gridwidth = 3;
        gbc.gridheight = 3;

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(playerStackScroller, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        contentPane.add(playerPayoutScroller, gbc);

        // 6 x 1 for Button and field pane
        gbc.gridwidth = 6;
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPane.add(buttonAndFieldPanel, gbc);

        // 6 x 3 for results area
        gbc.gridwidth = 6;
        gbc.gridheight = 3;

        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPane.add(resultsScroller, gbc);

        // 3 x 1 for separator
        gbc.gridwidth = 3;
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 8;
        contentPane.add(separator, gbc);

        // 2 x 1 for button
        gbc.gridwidth = 2;
        gbc.gridheight = 1;

        gbc.gridx = 3;
        gbc.gridy = 8;
        contentPane.add(calculateButton, gbc);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        addEventListeners();
    }

    private void addEventListeners() {
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Programmed by Isaac Flaum, algorithm idea by trojanrabbit on 2+2 forums");
        });

        loadStacksFromFile.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();

            fc.showOpenDialog(null);

            File selectedFile = fc.getSelectedFile();

            playerStacksModel.clear();

            try (Stream<String> stream = Files.lines(selectedFile.toPath())) {
                stream.forEach(line -> playerStacksModel.addElement(Double.parseDouble(line)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        addPlayerButton.addActionListener(e -> {
            String playerStackText = playerStacksInput.getText();

            if (!playerStackText.isBlank() && playerStackText.matches(DOUBLE_REGEX)) {
                playerStacksModel.addElement(Double.parseDouble(playerStackText));
            }
        });

        addPayoutButton.addActionListener(e -> {
            String payoutText = playerPayoutsInput.getText();

            if (!payoutText.isBlank() && payoutText.matches(DOUBLE_REGEX)) {
                playerPayoutsModel.addElement(Double.parseDouble(payoutText));
            }
        });

        removePlayerButton.addActionListener(e -> {
            if (playerStacksModel.isEmpty()) {
                return;
            }
            if (playerStacksList.getSelectedIndex() != -1) {
                playerStacksModel.remove(playerStacksList.getSelectedIndex());
            } else {
                playerStacksModel.remove(playerStacksModel.size() - 1);
            }
        });

        removePayoutButton.addActionListener(e -> {
            if (playerPayoutsModel.isEmpty()) {
                return;
            }
            if (playerPayoutsList.getSelectedIndex() != -1) {
                playerPayoutsModel.remove(playerPayoutsList.getSelectedIndex());
            } else {
                playerPayoutsModel.remove(playerPayoutsModel.size() - 1);
            }
        });

        calculateButton.addActionListener(e -> {
            double[] payouts = getValuesFromListModel(playerPayoutsModel);
            double[] players = getValuesFromListModel(playerStacksModel);

            if (players.length == 0 || payouts.length == 0) {
                return;
            }

            double[] equities = model.getEquities(payouts, players, 1_000_000);
            resultsTextArea.setText(Arrays.toString(equities));
        });
    }

    private double[] getValuesFromListModel(DefaultListModel<Double> model) {
        int modelSize = model.getSize();
        double[] values = new double[modelSize];

        for (int i = 0; i < modelSize; i++) {
            values[i] = model.getElementAt(i);
        }

        return values;
    }

    @Override
    public void run() {
        createAndShowGUI();
    }
}
