package org.apache.uima.tm.dltk.formatter;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.apache.uima.tm.dltk.formatter.internal.TextMarkerFormattedPrinter;
import org.apache.uima.tm.dltk.internal.core.parser.TextMarkerSourceParser;
import org.apache.uima.tm.dltk.internal.core.parsers.TextMarkerParser;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.formatter.AbstractScriptFormatter;
import org.eclipse.dltk.formatter.FormatterDocument;
import org.eclipse.dltk.ui.formatter.FormatterException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


public class TextMarkerFormatter extends AbstractScriptFormatter {

  class DummyReporter implements IProblemReporter {

    private boolean gotProblems = false;

    @Override
    public Object getAdapter(Class adapter) {
      return null;
    }

    @Override
    public void reportProblem(IProblem problem) {
      setProblems(true);
    }

    public void setProblems(boolean gotProblems) {
      this.gotProblems = gotProblems;
    }

    public boolean gotProblems() {
      return gotProblems;
    }
  };

  protected static final String[] INDENTING = { TextMarkerFormatterConstants.INDENT_BLOCK,
      TextMarkerFormatterConstants.INDENT_STRUCTURE };

  // protected static final String[] BLANK_LINES = { };

  private final String lineDelimiter;

  public TextMarkerFormatter(String lineDelimiter, Map preferences) {
    super(preferences);
    this.lineDelimiter = lineDelimiter;
  }

  public TextEdit format(String source, int offset, int length, int indent)
          throws FormatterException {
    String input = source;
    // TODO implement useful format for code snippets
    // final String input = source.substring(offset, offset + length);
    if (input == "") {
      return null;
    }
    TextMarkerSourceParser tmsp = new TextMarkerSourceParser();

    DummyReporter reporter = new DummyReporter();

    ModuleDeclaration md = tmsp.parse("format.tm".toCharArray(), source.toCharArray(), reporter);
    CommonTokenStream tokenStream = tmsp.getTokenStream();

    if (!reporter.gotProblems()) {

      BitSet bs = new BitSet();
      bs.add(TextMarkerParser.LINE_COMMENT);
      bs.add(TextMarkerParser.COMMENT);
      List<CommonToken> comments = tokenStream.getTokens(0, tokenStream.size(), bs);

      final String output = format(input, md, comments, indent);
      if (output != null) {
        if (!input.equals(output)) {
          return new ReplaceEdit(0, source.length(), output);
          // return new ReplaceEdit(offset, length, output);
          // if (!equalsIgnoreBlanks(new StringReader(input), new StringReader(output))) {
          // // return new MultiTextEdit();
          // return new ReplaceEdit(offset, length, output);
          // } else {
          // // TODO reset throwing exception
          // return new MultiTextEdit();
          // // throw new IllegalArgumentException("illegal format context");
          // // System.err.println("illegal format context");
          // }
        } else {
          return new MultiTextEdit();
        }
      }
    }
    return null;
  }

  /**
   * @param input
   * @param md
   * @param indent
   * @return the formatted code
   */
  private String format(String input, ModuleDeclaration md, List<CommonToken> comments, int indent) {
    final FormatterDocument document = createDocument(input);

    // compute new (formatted) document structure
    TextMarkerFormattedPrinter tmfp = new TextMarkerFormattedPrinter(document, lineDelimiter,
            createIndentGenerator(), comments, this);
    try {
      md.traverse(tmfp);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return tmfp.getOutput();

    // // compute new (formatted) document structure
    // final TextMarkerFormatterNodeBuilder builder = new TextMarkerFormatterNodeBuilder();
    // IFormatterContainerNode root = builder.build(md, document);
    // // new TextMarkerFormatterNodeRewriter(result, document).rewrite(root);
    // IFormatterContext context = new FormatterContext(indent);
    // FormatterWriter writer = new FormatterWriter(document, lineDelimiter,
    // createIndentGenerator());
    // try {
    // root.accept(context, writer);
    // writer.flush(context);
    // return writer.getOutput();
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
  }

  /**
   * Factory method for FormatterDocuments that represent the content with formatting options.<br>
   * No formatting operations done in this method and no formatting done with the content of the
   * formatterDocument in general.
   * 
   * @param input
   * @return
   */
  private FormatterDocument createDocument(String input) {
    FormatterDocument document = new FormatterDocument(input);
    for (int i = 0; i < INDENTING.length; ++i) {
      document.setBoolean(INDENTING[i], getBoolean(INDENTING[i]));
    }
    document.setInt(TextMarkerFormatterConstants.FORMATTER_TAB_SIZE,
            getInt(TextMarkerFormatterConstants.FORMATTER_TAB_SIZE));
    return document;
  }

  private boolean equalsIgnoreBlanks(Reader inputReader, Reader outputReader) {
    LineNumberReader input = new LineNumberReader(inputReader);
    LineNumberReader output = new LineNumberReader(outputReader);
    for (;;) {
      final String inputLine = readLine(input);
      final String outputLine = readLine(output);
      if (inputLine == null) {
        if (outputLine == null) {
          return true;
        } else {
          return false;
        }
      } else if (outputLine == null) {
        return false;
      } else if (!inputLine.equals(outputLine)) {
        return false;
      }
    }
  }

  private String readLine(LineNumberReader reader) {
    String line;
    do {
      try {
        line = reader.readLine();
      } catch (IOException e) {
        return null; // just doing sth.
      }
      if (line == null) {
        return line;
      }
      line = line.trim();
    } while (line.length() == 0);
    return line;
  }

  @Override
  public int getInt(String key) {
    return super.getInt(key);
  }

  @Override
  protected boolean getBoolean(String key) {
    return super.getBoolean(key);
  }

  @Override
  protected String getString(String key) {
    return super.getString(key);
  }

}
