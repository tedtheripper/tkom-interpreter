package source_loader;

import common.Position;
import source_loader.exception.SourceException;

import java.io.IOException;
import java.io.PushbackReader;

public abstract class AbstractSource implements Source {

    protected final static char END_OF_LINE = '\n';

    protected PushbackReader reader;

    protected long currentLine;
    protected long currentColumn;
    protected int currentCharacter;

    protected AbstractSource() {
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
    public Position getCurrentPosition() {
        return new Position(this.currentLine, this.currentColumn);
    }
}
