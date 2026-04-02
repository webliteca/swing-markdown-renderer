package ca.weblite.markdown;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * A Swing {@link EditorKit} that renders Markdown content inside a {@link JEditorPane}.
 *
 * <p>This kit registers with the MIME type {@code text/markdown}. When markdown text is
 * loaded via {@link #read(Reader, Document, int)}, it is parsed using
 * <a href="https://github.com/commonmark/commonmark-java">commonmark-java</a> and
 * rendered as HTML through Swing's built-in {@link HTMLEditorKit} infrastructure.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * JEditorPane editor = new JEditorPane();
 * editor.setEditorKit(new MarkdownEditorKit());
 * editor.setText("# Hello World\nThis is **Markdown**.");
 * }</pre>
 *
 * <p>The original markdown source is preserved and can be retrieved with
 * {@link #getMarkdownText(JEditorPane)} or written back via
 * {@link #write(Writer, Document, int, int)}.</p>
 */
public class MarkdownEditorKit extends HTMLEditorKit {

    private static final String CONTENT_TYPE = "text/markdown";
    private static final String MARKDOWN_PROPERTY = "ca.weblite.markdown.source";

    private final List<Extension> extensions;
    private final org.commonmark.parser.Parser parser;
    private final HtmlRenderer renderer;
    private StyleSheet defaultStyleSheet;

    public MarkdownEditorKit() {
        extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create()
        );
        parser = org.commonmark.parser.Parser.builder()
                .extensions(extensions)
                .build();
        renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
        defaultStyleSheet = createDefaultStyleSheet();
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public Document createDefaultDocument() {
        HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
        StyleSheet styles = doc.getStyleSheet();
        styles.addStyleSheet(defaultStyleSheet);
        return doc;
    }

    @Override
    public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
        String markdown = readFully(in);
        String html = renderToHtml(markdown);

        doc.putProperty(MARKDOWN_PROPERTY, markdown);

        StringReader htmlReader = new StringReader(html);
        super.read(htmlReader, doc, pos);
    }

    @Override
    public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
        read(new InputStreamReader(in), doc, pos);
    }

    @Override
    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
        Object markdown = doc.getProperty(MARKDOWN_PROPERTY);
        if (markdown instanceof String) {
            out.write((String) markdown);
        } else {
            super.write(out, doc, pos, len);
        }
    }

    @Override
    public void write(OutputStream out, Document doc, int pos, int len) throws IOException, BadLocationException {
        write(new OutputStreamWriter(out), doc, pos, len);
    }

    /**
     * Renders a markdown string to an HTML fragment suitable for display.
     */
    public String renderToHtml(String markdown) {
        Node document = parser.parse(markdown);
        String body = renderer.render(document);
        return "<html><head></head><body>" + body + "</body></html>";
    }

    /**
     * Retrieves the original markdown source text from a {@link JEditorPane} that uses
     * this kit, or {@code null} if no markdown source is stored.
     */
    public static String getMarkdownText(JEditorPane editorPane) {
        Document doc = editorPane.getDocument();
        Object md = doc.getProperty(MARKDOWN_PROPERTY);
        return md instanceof String ? (String) md : null;
    }

    /**
     * Sets markdown content on a {@link JEditorPane} that uses this kit.
     * This parses the markdown, stores the source, and renders it.
     */
    public void setMarkdownText(JEditorPane editorPane, String markdown) {
        String html = renderToHtml(markdown);
        editorPane.setText(html);
        editorPane.getDocument().putProperty(MARKDOWN_PROPERTY, markdown);
    }

    /**
     * Returns the commonmark parser used by this kit.
     */
    public org.commonmark.parser.Parser getMarkdownParser() {
        return parser;
    }

    /**
     * Returns the commonmark {@link HtmlRenderer} used by this kit.
     */
    public HtmlRenderer getHtmlRenderer() {
        return renderer;
    }

    @Override
    public StyleSheet getStyleSheet() {
        return defaultStyleSheet;
    }

    private StyleSheet createDefaultStyleSheet() {
        StyleSheet ss = new StyleSheet();
        ss.addStyleSheet(super.getStyleSheet());

        ss.addRule("body { font-family: 'Segoe UI', Arial, Helvetica, sans-serif; "
                + "font-size: 14pt; margin: 8px 12px; color: #24292e; }");

        ss.addRule("h1 { font-size: 24pt; font-weight: bold; "
                + "margin-top: 16px; margin-bottom: 8px; "
                + "border-bottom: 1px solid #eaecef; padding-bottom: 4px; }");
        ss.addRule("h2 { font-size: 20pt; font-weight: bold; "
                + "margin-top: 14px; margin-bottom: 6px; "
                + "border-bottom: 1px solid #eaecef; padding-bottom: 3px; }");
        ss.addRule("h3 { font-size: 16pt; font-weight: bold; "
                + "margin-top: 12px; margin-bottom: 4px; }");
        ss.addRule("h4 { font-size: 14pt; font-weight: bold; "
                + "margin-top: 10px; margin-bottom: 4px; }");
        ss.addRule("h5 { font-size: 12pt; font-weight: bold; "
                + "margin-top: 8px; margin-bottom: 4px; }");
        ss.addRule("h6 { font-size: 11pt; font-weight: bold; color: #6a737d; "
                + "margin-top: 8px; margin-bottom: 4px; }");

        ss.addRule("p { margin-top: 4px; margin-bottom: 8px; }");

        ss.addRule("code { font-family: 'Consolas', 'Courier New', monospace; "
                + "font-size: 12pt; background-color: #f6f8fa; padding: 2px 4px; }");
        ss.addRule("pre { font-family: 'Consolas', 'Courier New', monospace; "
                + "font-size: 12pt; background-color: #f6f8fa; "
                + "padding: 10px; margin: 8px 0; "
                + "border: 1px solid #e1e4e8; }");

        ss.addRule("blockquote { margin-left: 0; padding-left: 12px; "
                + "border-left: 4px solid #dfe2e5; color: #6a737d; "
                + "margin-top: 4px; margin-bottom: 8px; }");

        ss.addRule("ul { margin-top: 4px; margin-bottom: 8px; margin-left: 20px; }");
        ss.addRule("ol { margin-top: 4px; margin-bottom: 8px; margin-left: 20px; }");
        ss.addRule("li { margin-top: 2px; margin-bottom: 2px; }");

        ss.addRule("a { color: #0366d6; text-decoration: underline; }");

        ss.addRule("hr { border: none; border-top: 1px solid #eaecef; "
                + "margin-top: 12px; margin-bottom: 12px; }");

        ss.addRule("table { border-collapse: collapse; margin: 8px 0; }");
        ss.addRule("th { border: 1px solid #dfe2e5; padding: 6px 12px; "
                + "font-weight: bold; background-color: #f6f8fa; }");
        ss.addRule("td { border: 1px solid #dfe2e5; padding: 6px 12px; }");

        ss.addRule("img { max-width: 100%; }");

        return ss;
    }

    private static String readFully(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[4096];
        int n;
        while ((n = reader.read(buf)) != -1) {
            sb.append(buf, 0, n);
        }
        return sb.toString();
    }
}
