package source_loader;

import common.Position;
import source_loader.exception.SourceException;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Source {

    int getNextCharacter() throws IOException, SourceException;

    long getCurrentLine();

    long getCurrentColumn();

    Position getCurrentPosition();

    void load() throws FileNotFoundException;
}
