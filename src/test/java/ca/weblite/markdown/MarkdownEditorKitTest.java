package ca.weblite.markdown;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.*;

import static org.junit.Assert.*;

public class MarkdownEditorKitTest {

    private MarkdownEditorKit kit;

    @Before
    public void setUp() {
        kit = new MarkdownEditorKit();
    }

    @Test
    public void testContentType() {
        assertEquals("text/markdown", kit.getContentType());
    }

    @Test
    public void testRenderHeading() {
        String html = kit.renderToHtml("# Hello");
        assertTrue("Expected <h1> tag", html.contains("<h1>Hello</h1>"));
    }

    @Test
    public void testRenderBold() {
        String html = kit.renderToHtml("This is **bold** text");
        assertTrue("Expected <strong> tag", html.contains("<strong>bold</strong>"));
    }

    @Test
    public void testRenderItalic() {
        String html = kit.renderToHtml("This is *italic* text");
        assertTrue("Expected <em> tag", html.contains("<em>italic</em>"));
    }

    @Test
    public void testRenderCode() {
        String html = kit.renderToHtml("Use `code` here");
        assertTrue("Expected <code> tag", html.contains("<code>code</code>"));
    }

    @Test
    public void testRenderCodeBlock() {
        String html = kit.renderToHtml("```\nint x = 1;\n```");
        assertTrue("Expected <pre> tag", html.contains("<pre>"));
        assertTrue("Expected <code> tag", html.contains("<code>"));
        assertTrue("Expected code content", html.contains("int x = 1;"));
    }

    @Test
    public void testRenderLink() {
        String html = kit.renderToHtml("[click](http://example.com)");
        assertTrue("Expected <a> tag with href",
                html.contains("<a href=\"http://example.com\">click</a>"));
    }

    @Test
    public void testRenderUnorderedList() {
        String html = kit.renderToHtml("- item1\n- item2\n- item3");
        assertTrue("Expected <ul> tag", html.contains("<ul>"));
        assertTrue("Expected <li> tags", html.contains("<li>item1</li>"));
        assertTrue("Expected <li> tags", html.contains("<li>item2</li>"));
    }

    @Test
    public void testRenderOrderedList() {
        String html = kit.renderToHtml("1. first\n2. second");
        assertTrue("Expected <ol> tag", html.contains("<ol>"));
        assertTrue("Expected <li> tags", html.contains("<li>first</li>"));
    }

    @Test
    public void testRenderBlockquote() {
        String html = kit.renderToHtml("> quoted text");
        assertTrue("Expected <blockquote> tag", html.contains("<blockquote>"));
        assertTrue("Expected quoted content", html.contains("quoted text"));
    }

    @Test
    public void testRenderTable() {
        String md = "| A | B |\n|---|---|\n| 1 | 2 |";
        String html = kit.renderToHtml(md);
        assertTrue("Expected <table> tag", html.contains("<table>"));
        assertTrue("Expected <th> tag", html.contains("<th>"));
        assertTrue("Expected <td> tag", html.contains("<td>"));
    }

    @Test
    public void testRenderStrikethrough() {
        String html = kit.renderToHtml("~~deleted~~");
        assertTrue("Expected <del> tag", html.contains("<del>deleted</del>"));
    }

    @Test
    public void testRenderHorizontalRule() {
        String html = kit.renderToHtml("---");
        assertTrue("Expected <hr /> tag", html.contains("<hr"));
    }

    @Test
    public void testRenderImage() {
        String html = kit.renderToHtml("![alt text](image.png)");
        assertTrue("Expected <img> tag", html.contains("<img"));
        assertTrue("Expected src attribute", html.contains("src=\"image.png\""));
        assertTrue("Expected alt attribute", html.contains("alt=\"alt text\""));
    }

    @Test
    public void testReadPreservesMarkdownSource() throws Exception {
        String markdown = "# Test\nHello **world**";
        Document doc = kit.createDefaultDocument();
        kit.read(new StringReader(markdown), doc, 0);

        Object stored = doc.getProperty("ca.weblite.markdown.source");
        assertEquals(markdown, stored);
    }

    @Test
    public void testWriteOutputsMarkdown() throws Exception {
        String markdown = "# Test\nHello **world**";
        Document doc = kit.createDefaultDocument();
        kit.read(new StringReader(markdown), doc, 0);

        StringWriter writer = new StringWriter();
        kit.write(writer, doc, 0, doc.getLength());
        assertEquals(markdown, writer.toString());
    }

    @Test
    public void testEditorPaneIntegration() throws Exception {
        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(kit);
        pane.setText("# Heading\n\nParagraph with **bold**.");

        String mdText = MarkdownEditorKit.getMarkdownText(pane);
        assertNotNull("Markdown source should be stored", mdText);
    }

    @Test
    public void testSetMarkdownText() throws Exception {
        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(kit);
        kit.setMarkdownText(pane, "# Hello\nWorld");

        String mdText = MarkdownEditorKit.getMarkdownText(pane);
        assertEquals("# Hello\nWorld", mdText);
    }

    @Test
    public void testCreateDefaultDocument() {
        Document doc = kit.createDefaultDocument();
        assertNotNull(doc);
    }

    @Test
    public void testComplexMarkdown() {
        String md = "# Title\n\n"
                + "A paragraph with **bold**, *italic*, and `code`.\n\n"
                + "## Lists\n\n"
                + "- Item 1\n"
                + "- Item 2\n"
                + "  - Nested\n\n"
                + "1. First\n"
                + "2. Second\n\n"
                + "> A blockquote\n\n"
                + "```java\npublic class Foo {}\n```\n\n"
                + "| Col1 | Col2 |\n|------|------|\n| A    | B    |\n\n"
                + "---\n\n"
                + "[Link](http://example.com)";
        String html = kit.renderToHtml(md);
        assertTrue(html.contains("<h1>"));
        assertTrue(html.contains("<h2>"));
        assertTrue(html.contains("<strong>"));
        assertTrue(html.contains("<em>"));
        assertTrue(html.contains("<code>"));
        assertTrue(html.contains("<ul>"));
        assertTrue(html.contains("<ol>"));
        assertTrue(html.contains("<blockquote>"));
        assertTrue(html.contains("<pre>"));
        assertTrue(html.contains("<table>"));
        assertTrue(html.contains("<hr"));
        assertTrue(html.contains("<a href"));
    }
}
