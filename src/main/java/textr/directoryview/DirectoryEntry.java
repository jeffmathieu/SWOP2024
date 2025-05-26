package textr.directoryview;
import java.io.IOException;
import java.util.List;

public abstract class DirectoryEntry extends Entry {



    public abstract List <Entry> getChildren() throws IOException;
}
