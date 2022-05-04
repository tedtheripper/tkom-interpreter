package source_loader;

import source_loader.exception.SourceException;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Source {

    int getNextCharacter() throws IOException, SourceException;

    long getCurrentLine();

    long getCurrentColumn();

    void load() throws FileNotFoundException;
}
