package ca.weblite.markdown;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Example application demonstrating real-time Markdown rendering.
 * The left pane accepts raw Markdown input; the right pane shows the rendered output.
 */
public class MarkdownEditorDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarkdownEditorDemo::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Markdown Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        MarkdownEditorKit kit = new MarkdownEditorKit();

        // Left: raw markdown input
        JTextArea inputArea = new JTextArea();
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setMargin(new Insets(8, 8, 8, 8));

        // Right: rendered preview
        JEditorPane preview = new JEditorPane();
        preview.setEditorKit(kit);
        preview.setEditable(false);

        // Update preview whenever the input changes
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            private Timer debounce;

            private void scheduleUpdate() {
                if (debounce != null) {
                    debounce.stop();
                }
                debounce = new Timer(150, e -> {
                    String markdown = inputArea.getText();
                    kit.setMarkdownText(preview, markdown);
                    preview.setCaretPosition(0);
                });
                debounce.setRepeats(false);
                debounce.start();
            }

            @Override
            public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }

            @Override
            public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }

            @Override
            public void changedUpdate(DocumentEvent e) { scheduleUpdate(); }
        });

        JScrollPane inputScroll = new JScrollPane(inputArea);
        JScrollPane previewScroll = new JScrollPane(preview);

        // Labels
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(createLabel("Markdown"), BorderLayout.NORTH);
        leftPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(createLabel("Preview"), BorderLayout.NORTH);
        rightPanel.add(previewScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        frame.add(splitPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Load sample content
        inputArea.setText(SAMPLE_MARKDOWN);
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        label.setOpaque(true);
        label.setBackground(new Color(0xF0F0F0));
        return label;
    }

    private static final String SAMPLE_MARKDOWN =
            "# Markdown Editor Demo\n\n"
            + "Edit this text to see **real-time** rendering.\n\n"
            + "## Features\n\n"
            + "- **Bold** and *italic* text\n"
            + "- `Inline code` and code blocks\n"
            + "- [Links](http://example.com)\n"
            + "- Lists, blockquotes, and more\n\n"
            + "## Code Block\n\n"
            + "```java\n"
            + "public class Hello {\n"
            + "    public static void main(String[] args) {\n"
            + "        System.out.println(\"Hello, Markdown!\");\n"
            + "    }\n"
            + "}\n"
            + "```\n\n"
            + "## Table\n\n"
            + "| Feature       | Supported |\n"
            + "|---------------|:---------:|\n"
            + "| Headings      | Yes       |\n"
            + "| Bold/Italic   | Yes       |\n"
            + "| Code blocks   | Yes       |\n"
            + "| Tables        | Yes       |\n"
            + "| ~~Strike~~    | Yes       |\n\n"
            + "> Blockquotes work too!\n\n"
            + "---\n\n"
            + "*Powered by MarkdownEditorKit and commonmark-java*\n";
}
