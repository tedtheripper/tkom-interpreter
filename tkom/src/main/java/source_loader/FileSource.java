package source_loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PushbackReader;

public class FileSource extends AbstractSource{

    private final String path;

    public FileSource(String path) {
        super();
        this.path = path;
    }

    @Override
    public void load() throws FileNotFoundException {
        FileReader fr = new FileReader(this.path);
        this.reader = new PushbackReader(fr);
    }
}
