package view;

import controller.GenerationEngine;
import controller.NarrativeStrategy;
import controller.SaveLoadHandler;
import controller.ServerConnection;
import model.Story;
import org.json.JSONObject;

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

    // Tree
    private JTree storyTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode topTreeNode;

    // Connection
    ServerConnection serverConnection;

    // File handling
    SaveLoadHandler saveLoadHandler;

    public StoryGeneratorGUI() {
        saveLoadHandler = SaveLoadHandler.getInstance();
        initializeGUI();
        initializeConnection();
    }

    /**
     * Creates the GUI for the program.
     */
    private void initializeGUI() {
        setTitle("Story Generator");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bottom Panel - Prompt Section (with Status Label)
        JPanel bottomPanel = createPromptPanel();
        bottomPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        // Center Panel - Text Section
        JPanel centerPanel = createTextPanel();

        // Left Panel - Story Tree Section
        JPanel leftPanel = createStoryTreePanel();

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
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
        try {
            serverConnection.connect();
            updateStatus("Connected to server");
        } catch (IOException e) {
//            showError("Failed to connect to server: " + e.getMessage());
            updateStatus("Not connected");
        }
    }

    /**
     * Creates the prompt panel with text field and button.
     * @return JPanel - prompt panel
     */
    private JPanel createPromptPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Generate Story"));

        promptField = new JTextField();
        promptField.setFont(new Font("Arial", Font.PLAIN, 14));
        promptField.addActionListener(e -> {
            try {
                generateStory();
            } catch (IOException ex) {
                // TODO: Add error show function call here
            }
        });

        promptButton = new JButton("Prompt");
        promptButton.setFont(new Font("Arial", Font.BOLD, 14));
        promptButton.addActionListener(e -> {
            try {
                generateStory();
            } catch (IOException ex) {
                // TODO: Add error show function call here
            }
        });

        panel.add(new JLabel("Prompt Query: "), BorderLayout.WEST);
        panel.add(promptField, BorderLayout.CENTER);
        panel.add(promptButton, BorderLayout.EAST);

        return panel;
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
        if (prompt[0].isEmpty()) {
            return;
        }

        promptField.setText(null);
        changeText(null);
        promptButton.setEnabled(false);
        updateStatus("Generating story... Please wait...");

        // Make the worker generate on another thread so the GUI does not have to freeze.
        SwingWorker<Story, Void> worker = new SwingWorker<Story, Void>() {
            @Override
            protected Story doInBackground() throws Exception {
                GenerationEngine engine = new GenerationEngine(new NarrativeStrategy());
                prompt[0] = engine.construct(prompt[0]);
                String response = serverConnection.createStory(prompt[0]);

                // Extract story to JSON to Story Object
                JSONObject storyJSON = serverConnection.extractJSONfromStory(response);
                Story story = Story.fromJSON(storyJSON);
                story.setStory(response);

                return story;
            }

            @Override
            protected void done() {
                Story story = null;
                try {
                    story = get();
                    saveLoadHandler.saveStory(story);
                    addStoryToTree(story);
                    changeText(String.format("STORY NAME: %s\n\n%s", story.getName(), story.getStory()));
                    updateStatus("Generated the story.");
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
     * Clears the tree nodes within the tree (by making a new tree).
     * @return the top node of the tree
     */
    private void clearTreeNodes() {
        topTreeNode.removeAllChildren();
    }

    /**
     * Public method to update the tree whenever there are new changes.
     */
    public void updateTree() {
        clearTreeNodes();
        createTreeNodes();
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