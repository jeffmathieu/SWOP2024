package textr.directoryview;
import java.nio.file.Path;


public class FileLeafEntry extends Entry {
    private Path path;

    public FileLeafEntry(Path path, Entry parent) {
        this.path = path;
        this.parent = parent;

    }

    @Override
    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public Entry getParent() {
        return parent;
    }

}

