package view;

import config.Config;
import controller.*;
import model.Story;
import org.json.JSONObject;
import service.GeminiAPIService;
import strategy.NarrativeStrategy;
import strategy.PoemStrategy;
import strategy.ScreenplayStrategy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class StoryGeneratorGUI extends JFrame {
    // Fields
    private JTextField promptField;
    private JButton promptButton;
    private JTextArea textArea;
    private JLabel statusLabel;
    private JSlider wordCountSlider;
    private JComboBox<String> modelBox;
    private JComboBox<String> complexityBox;
    private JComboBox<String> storyTypeBox;

    // Tree
    private JTree storyTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode topTreeNode;

    // Engine
    private GenerationEngine engine = new GenerationEngine(new NarrativeStrategy());

    // Connection
    private ServerConnection serverConnection;
    private boolean isConnected;

    // File handling
    private final SaveLoadHandler saveLoadHandler;

    public StoryGeneratorGUI() {
        Config.load();
        saveLoadHandler = SaveLoadHandler.getInstance();
        initializeGUI();
        initializeConnection();
    }

    /**
     * Creates the GUI for the program.
     */
    private void initializeGUI() {
        setTitle("Story Generator");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Center Panel - Text Section
        JPanel centerPanel = createTextPanel();

        // Bottom Panel - Prompt Section (with Status Label)
        JPanel statusPanel = createStatusPanel();
        JPanel bottomPanel = createPromptPanel();
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        // Left Panel - Story Tree Section
        JPanel leftPanel = createStoryTreePanel();

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        add(mainPanel);

        // Add window listener that cleans up resources upon closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    /**
     * Set-ups the connection to the server.
     */
    private void initializeConnection() {
        serverConnection = new ServerConnection();
        connectServer();
    }

    /**
     * Attempts to connect to the server.
     */
    private void connectServer() {
        try {
            serverConnection.connect();
            updateStatus("Connected to server");
            isConnected = true;
        } catch (IOException e) {
            showError("Failed to connect to server: " + e.getMessage());
            updateStatus("Not connected");
            isConnected = false;
        }
    }

    /**
     * Creates status panel that displays current status.
     * @return JPanel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        statusLabel = new JLabel("Connecting...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(statusLabel);

        return panel;
    }

    /**
     * Creates the prompt panel with text field and button.
     * @return JPanel - prompt panel
     */
    private JPanel createPromptPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Generate Story"));

        // Prompt field
        promptField = new JTextField();
        promptField.setFont(new Font("Arial", Font.PLAIN, 14));
        promptField.addActionListener(e -> {
            try {
                generateStory();
            } catch (IOException ex) {
                // TODO: Add error show function call here
            }
        });

        // Prompt button
        promptButton = new JButton("Prompt");
        promptButton.setFont(new Font("Arial", Font.BOLD, 14));
        promptButton.addActionListener(e -> {
            try {
                generateStory();
            } catch (IOException ex) {

            }
        });

        // Story Settings Panel
        JPanel settingsPanel = createSettingsPanel();

        panel.add(new JLabel("Prompt Query: "), BorderLayout.WEST);
        panel.add(promptField, BorderLayout.CENTER);
        panel.add(promptButton, BorderLayout.EAST);
        panel.add(settingsPanel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Creates a panel with all the settings needed for the story.
     * @return JPanel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        // COMPONENTS
        // Word Count Slider
        panel.add(createWCSliderPanel());
        panel.add(Box.createRigidArea(new Dimension(50, 0)));

        // Model Combo Box
        panel.add(createModelBoxPanel());
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Complexity Combo Box
        panel.add(createComplexityBoxPanel());
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Strategy Combo Box
        panel.add(createStoryTypeBoxPanel());

        return panel;
    }

    /**
     * Creates a panel with the word count slider.
     * @return JPanel
     */
    private JPanel createWCSliderPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        wordCountSlider = new JSlider(500, 3000, 1000);
        panel.add(new JLabel("Word Count: "), BorderLayout.WEST);
        panel.add(wordCountSlider, BorderLayout.CENTER);

        // Turn on labels at major tick marks
        wordCountSlider.setMajorTickSpacing(200);
        wordCountSlider.setMinorTickSpacing(50);
        wordCountSlider.setPaintTicks(true);
        wordCountSlider.setPaintLabels(true);

        return panel;
    }

    /**
     * Creates a panel with a combo box that lets you choose different Gemini models to use.
     * @return JPanel
     */
    private JPanel createModelBoxPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        modelBox = new JComboBox<>(GeminiAPIService.availableModels);
        panel.add(new JLabel("Model:"), BorderLayout.WEST);
        panel.add(modelBox, BorderLayout.CENTER);

        // Set it default to "gemini-2.5-flash-lite"
        modelBox.setSelectedIndex(1);

        return panel;
    }

    /**
     * Creates a panel with a combo box that lets you choose different complexities to use.
     * @return JPanel
     */
    private JPanel createComplexityBoxPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        complexityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        panel.add(new JLabel("Complexity:"), BorderLayout.WEST);
        panel.add(complexityBox, BorderLayout.CENTER);

        // Make it change the engine's complexity if changed
        complexityBox.addActionListener(e -> {
            int complexity = complexityBox.getSelectedIndex() + 1;
            engine.setComplexity(complexity);
            updateStatus("Set complexity to " + complexity);
        });

        // Set it default to "low"
        complexityBox.setSelectedIndex(0);
        engine.setComplexity(complexityBox.getSelectedIndex() + 1);

        return panel;
    }

    /**
     * Creates a panel with a combo box that lets you choose different strategy (story types) to use.
     * @return JPanel
     */
    private JPanel createStoryTypeBoxPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        storyTypeBox = new JComboBox<>(new String[]{"Narrative", "Screenplay", "Poem"});
        panel.add(new JLabel("Type:"), BorderLayout.WEST);
        panel.add(storyTypeBox, BorderLayout.CENTER);

        // Set it default to "Narrative"
        storyTypeBox.setSelectedIndex(0);
        changeStoryType((String) storyTypeBox.getSelectedItem());

        // Set action to change story type
        storyTypeBox.addActionListener(e -> {
            String type = (String) storyTypeBox.getSelectedItem();
            changeStoryType(type);
            updateStatus("Changed story type to " + type);
        });

        return panel;
    }

    /**
     * Changes the story type.
     * @param type - a story type
     */
    private void changeStoryType(String type) {
        switch(type) {
            case "Narrative" ->
                    engine.setStrategy(new NarrativeStrategy());
            case "Screenplay" ->
                    engine.setStrategy(new ScreenplayStrategy());
            case "Poem" ->
                    engine.setStrategy(new PoemStrategy());
            default ->
                    System.err.println("Invalid strategy set: " + type);
        }

        updateWordCountSlider();
    }

    /**
     * Updates the word count values depending on the story type.
     */
    private void updateWordCountSlider() {
        wordCountSlider.setMinimum(engine.getMinWordCount());
        wordCountSlider.setMaximum(engine.getMaxWordCount());
        wordCountSlider.setValue((wordCountSlider.getMaximum() + wordCountSlider.getMinimum()) / 2);
    }

    /**
     * Create text panel that shows the text the user is currently viewing.
     * @return JPanel
     */
    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        return panel;
    }

    /**
     * Creates the tree of story and its elements.
     * @return JPanel
     */
    private JPanel createStoryTreePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Story List"));

        // Create tree
        topTreeNode = new DefaultMutableTreeNode("Story");
        treeModel = new DefaultTreeModel(topTreeNode);
        storyTree = new JTree(treeModel);
        storyTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Create initial tree nodes
        createTreeNodes();

        // Add mouse listener for double clicks
        storyTree.addMouseListener(getTreeMouseListener());

        JScrollPane scrollPane = new JScrollPane(storyTree);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the story tree nodes for the tree.
     */
    private void createTreeNodes() {
        List<String> storyNames = saveLoadHandler.getStoryNames();
        for (String name : storyNames) {
            Story story = saveLoadHandler.loadStory(name);
            addStoryToTree(story);
        }
    }

    /**
     * Generates the story node and adds it to the tree.
     * @param story
     */
    private void addStoryToTree(Story story) {
        DefaultMutableTreeNode storyNode = new DefaultMutableTreeNode(story.getName());
        treeModel.insertNodeInto(storyNode, topTreeNode, topTreeNode.getChildCount());
        storyTree.scrollPathToVisible(new TreePath(storyNode.getPath()));

        storyNode.add(new DefaultMutableTreeNode("Story Info"));

        // Adding character nodes
        DefaultMutableTreeNode characters = new DefaultMutableTreeNode("Characters");
        storyNode.add(characters);
        List<String> characterNames = story.getCharacterNames();
        for (String characterName : characterNames) {
            DefaultMutableTreeNode character = new DefaultMutableTreeNode(characterName);
            characters.add(character);
        }

        // Adding location nodes
        DefaultMutableTreeNode locations = new DefaultMutableTreeNode("Locations");
        storyNode.add(locations);
        List<String> locationNames = story.getLocationNames();
        for (String locationName : locationNames) {
            DefaultMutableTreeNode location = new DefaultMutableTreeNode(locationName);
            locations.add(location);
        }
    }

    /**
     * Generates a story based on the prompt and shows it on the screen.
     * @throws IOException
     */
    private void generateStory() throws IOException {
        final String[] prompt = {promptField.getText().trim()};

        // Attempts to connect to the server if not connected
        if (!isConnected) {
            connectServer();
            // If still not connected then do not do anything.
            if (!isConnected) return;
        }

        // If the prompt is empty, then return an error message.
        if (prompt[0].isEmpty()) {
            showError("Prompt cannot be empty.");
            return;
        }

        promptField.setText(null);
        changeText(null);
        promptButton.setEnabled(false);
        updateStatus(String.format("Generating story with %s model... Please wait...", modelBox.getSelectedItem()));

        // Make the worker generate on another thread so the GUI does not have to freeze.
        SwingWorker<Story, Void> worker = new SwingWorker<Story, Void>() {
            @Override
            protected Story doInBackground() throws Exception {
                String model = (String) modelBox.getSelectedItem();
                int wordCount = wordCountSlider.getValue();

                // Create story
                prompt[0] = engine.construct(prompt[0], wordCount);
                String response = serverConnection.createStory(prompt[0], model);
                // Check for errors
                if (checkError(response)) {
                    updateStatus("Error while generating the story.");
                    return null;
                }

                // Extract story to JSON to Story Object
                updateStatus("Extracting story...");
                String storyJSON = serverConnection.extractJSONfromStory(response, model);
                // Check for errors
                if (checkError(response)) {
                    updateStatus("Error while extracting the story.");
                    return null;
                }

                Story story = Story.fromJSON(new JSONObject(storyJSON));
                story.setStory(response);

                return story;
            }

            @Override
            protected void done() {
                Story story = null;

                try {
                    story = get();

                    if (story != null) {
                        saveLoadHandler.saveStory(story);
                        addStoryToTree(story);
                        changeText(String.format("STORY NAME: %s\n\n%s", story.getName(), story.getStory()));
                        updateStatus("Generated the story.");
                    }
                } catch (Exception e) {
                    updateStatus("Error in generating the story.");
                } finally {
                    promptButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    /**
     * Changes the text within the text area.
     * @param text text to be changed to
     */
    private void changeText(String text) {
        textArea.setText(text);
    }

    /**
     * Changes the status label to the given text.
     * @param text - text to change
     */
    private void updateStatus(String text) {
        statusLabel.setText(text);
    }

    /**
     * Create a mouse listener that detects whenever the cursor double clicks on a node of the tree.
     * @return MouseListener
     */
    private MouseListener getTreeMouseListener() {
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = storyTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = storyTree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 2) {
                        doubleClick(selPath);
                    }
                }
            }
        };

        return ml;
    }

    /**
     * Double click behavior on node trees.
     * @param selPath - selected TreePath
     */
    private void doubleClick(TreePath selPath) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (treeModel.isLeaf(node)) {
            String text = (String) node.getUserObject();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            String parentText = (String) parentNode.getUserObject();

            switch(parentText) {
                case "Characters" -> {
                    DefaultMutableTreeNode grandparentNode = (DefaultMutableTreeNode) parentNode.getParent();
                    String storyName = (String) grandparentNode.getUserObject();

                    changeText(saveLoadHandler.loadStory(storyName).getCharacter(text).toString());
                }

                case "Locations" -> {
                    DefaultMutableTreeNode grandparentNode = (DefaultMutableTreeNode) parentNode.getParent();
                    String storyName = (String) grandparentNode.getUserObject();

                    changeText(saveLoadHandler.loadStory(storyName).getLocation(text).toString());
                }

                default -> {
                    changeText(saveLoadHandler.loadStory(parentText).toString());
                }
            }
        }

    }

    /**
     * Checks if there are errors in the response text.
     * @return true if there are errors, else false.
     */
    private boolean checkError(String response) {
        String firstWord = response.split(" ")[0];
        if (firstWord.equals("!ERROR:")) {
            showError("Error: " + response.substring(8));
            return true;
        }

        return false;
    }

    /**
     * Show error dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Cleanup resources
     */
    private void cleanup() {
        if (serverConnection != null) {
            serverConnection.disconnect();
        }
    }

    // Launch application
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            StoryGeneratorGUI gui = new StoryGeneratorGUI();
            gui.setVisible(true);
        });
    }
}