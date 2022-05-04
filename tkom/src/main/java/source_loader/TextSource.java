package source_loader;

import java.io.PushbackReader;
import java.io.StringReader;

public class TextSource extends AbstractSource{

    private final String text;

    public TextSource(String text) {
        super();
        this.text = text;
    }

    @Override
    public void load() {
        this.reader = new PushbackReader(
                new StringReader(this.text)
        );
    }
}
