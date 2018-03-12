package ch.heigvd.res.lab01.impl.filters;

import ch.heigvd.res.lab01.impl.Utils;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());
  private int lineNumber = 1;
  private boolean lastWasENDL = false;


  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    this.write(str.toCharArray(), off, len);

  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    for(int i = off; i < off + len; i++){
      this.write(cbuf[i]);
    }
  }

  @Override
  public void write(int c) throws IOException {
    final char CARRIAGE = '\r';
    final char LINEFEED = '\n';

    //Is it the first call ?
    if(lineNumber == 1) {
      out.write(number());
    }

    //Do we need to write the number of the line ?
    //---------------------------------------------------------

    //On Windows and OSX
    if(c == CARRIAGE) {

      out.write(c);
      lastWasENDL = true;
    }

    //Does CARRIAGE happened before or another linebreak?
    else if(lastWasENDL && c != LINEFEED) {

      out.write(number());
      out.write(c);
      lastWasENDL = false;
    }

    //Are we on Linux or is the the end of a Windows break ?
    else if(c == LINEFEED) {

      out.write(c);
      out.write(number());

      if(lastWasENDL) {
        lastWasENDL = false;
      }
    }

    else {
      out.write(c);
    }

  }

  public String number(){
    StringBuilder lineTab = new StringBuilder();
    lineTab.append(lineNumber++);
    lineTab.append('\t');
    return lineTab.toString();
  }
}
