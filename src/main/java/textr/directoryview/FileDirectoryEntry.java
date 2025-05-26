package textr.directoryview;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileDirectoryEntry extends DirectoryEntry {
    private Path Path;


    public FileDirectoryEntry(Path path) {
        this.Path = path;
    }

    public Path getCurrentDirectory() {
        return Path;
    }

    @Override
    public List<Entry> getChildren() throws IOException {
        List<Entry> entries = Files.list(Path).map(path -> {
            if (Files.isDirectory(path)) {
                return new FileDirectoryEntry(path);
            } else {
                return new FileLeafEntry(path, parent);
            }
        }).toList();
        return entries;
    }


    @Override
    public String getName() {
        return Path.getFileName().toString() + "/";
    }

    @Override
    public Entry getParent() {
        if (parent == null) {
            return new FileDirectoryEntry(Path.getParent());
        }
        else {
            return parent;
        }
    }


}

