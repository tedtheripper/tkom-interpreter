package source_loader;

import source_loader.exception.SourceException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class FileSource implements Source{

    private final static char END_OF_LINE = '\n';

    private final String path;
    private PushbackReader reader;

    private long currentLine;
    private long currentColumn;
    private int currentCharacter;

    public FileSource(String path) {
        this.path = path;
        this.currentColumn = 0;
        this.currentLine = 1;
    }

    @Override
    public int getNextCharacter() throws IOException, SourceException {
        if (this.reader == null) {
            throw new SourceException("Reader has not been loaded properly");
        }

        if (((char)currentCharacter) == END_OF_LINE) {
            this.currentLine++;
            this.currentColumn = 1;
        } else {
            this.currentColumn++;
        }
        currentCharacter = this.reader.read();
        return currentCharacter;
    }

    @Override
    public long getCurrentLine() {
        return this.currentLine;
    }

    @Override
    public long getCurrentColumn() {
        return this.currentColumn;
    }

    @Override
    public void load() throws FileNotFoundException {
        FileReader fr = new FileReader(this.path);
        this.reader = new PushbackReader(fr);
    }
}
