package source_loader;

import java.io.*;

public class InputLoader {

    private String filePath;
    private final SourceType sourceType;

    public InputLoader() {
        this.sourceType = SourceType.STANDARD_INPUT;
    }

    public InputLoader(String filePath) {
        this.filePath = filePath;
        this.sourceType = SourceType.FILE;
    }

    public PushbackReader loadFile() throws FileNotFoundException {
        if (this.sourceType == SourceType.FILE) {
            FileReader fr = new FileReader(this.filePath);
            return new PushbackReader(fr);
        }
        return null;
    }

    public PushbackReader loadInput(String input) {
        if (this.sourceType == SourceType.STANDARD_INPUT) {
            return new PushbackReader(new StringReader(input));
        }
        return null;
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public String getFilePath() {
        return this.filePath;
    }
}
