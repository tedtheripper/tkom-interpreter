package source_loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Scanner;

public class FileLoader {

    private String filePath;
    private final SourceType sourceType;

    public FileLoader() {
        this.sourceType = SourceType.STANDARD_INPUT;
    }

    public FileLoader(String filePath) {
        this.filePath = filePath;
        this.sourceType = SourceType.FILE;
    }

    public PushbackReader loadFile() throws FileNotFoundException {
        if (this.sourceType == SourceType.FILE) {
            FileReader fr = new FileReader(this.filePath);
            return new PushbackReader(fr);
        } else {
            return new PushbackReader(new StringReader(readLine()));
        }
    }

    public String readLine() {
        var input = new Scanner(System.in);
        return input.nextLine();
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public String getFilePath() {
        return this.filePath;
    }
}
