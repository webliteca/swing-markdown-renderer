# Swing Markdown Renderer

A Swing `EditorKit` for rendering Markdown inside `JEditorPane`. Uses [commonmark-java](https://github.com/commonmark/commonmark-java) to parse Markdown into HTML, which is then displayed through Swing's built-in HTML rendering engine.

## Features

- Drop-in `EditorKit` for any `JEditorPane` with content type `text/markdown`
- GFM extensions: tables, strikethrough (`~~text~~`), and autolinks
- GitHub-inspired default stylesheet (headings, code blocks, blockquotes, tables, etc.)
- Round-trip support: original Markdown source is preserved and written back via `write()`
- Java 11+

## Usage

### Dependency

**Maven:**

```xml
<dependency>
    <groupId>ca.weblite</groupId>
    <artifactId>swing-markdown-renderer</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Rendering Markdown

```java
JEditorPane editor = new JEditorPane();
editor.setEditorKit(new MarkdownEditorKit());
editor.setText("# Hello World\nThis is **Markdown**.");
```

### Setting Markdown programmatically

```java
MarkdownEditorKit kit = new MarkdownEditorKit();
editor.setEditorKit(kit);
kit.setMarkdownText(editor, "# Title\nSome *content*.");
```

### Retrieving the original Markdown source

```java
String markdown = MarkdownEditorKit.getMarkdownText(editor);
```

### Converting Markdown to HTML

```java
MarkdownEditorKit kit = new MarkdownEditorKit();
String html = kit.renderToHtml("**bold** and *italic*");
```

## Demo Application

The project includes a live editor demo (`MarkdownEditorDemo`) with a split pane: type Markdown on the left, see rendered output on the right in real time.

### Running the demo

```bash
mvn compile exec:java -Dexec.mainClass=ca.weblite.markdown.MarkdownEditorDemo
```

Or with `javac` directly:

```bash
# Compile
javac -cp "lib/*" -d target/classes src/main/java/ca/weblite/markdown/*.java

# Run
java -cp "lib/*:target/classes" ca.weblite.markdown.MarkdownEditorDemo
```

## Building

### Prerequisites

- Java 11 or later
- Maven 3.6+

### Build and test

```bash
mvn clean test
```

### Package

```bash
mvn clean package
```

The JAR will be in `target/swing-markdown-renderer-1.0.0-SNAPSHOT.jar`.

## Project Structure

```
src/
  main/java/ca/weblite/markdown/
    MarkdownEditorKit.java      # The EditorKit implementation
    MarkdownEditorDemo.java     # Live preview demo application
  test/java/ca/weblite/markdown/
    MarkdownEditorKitTest.java  # Unit tests (20 tests)
```

## Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| commonmark | 0.21.0 | Markdown parser and HTML renderer |
| commonmark-ext-gfm-tables | 0.21.0 | GFM table support |
| commonmark-ext-gfm-strikethrough | 0.21.0 | GFM strikethrough support |
| commonmark-ext-autolink | 0.21.0 | Automatic URL linking |
| junit | 4.13.2 | Unit testing (test scope) |

## License

See [LICENSE](LICENSE) for details.
