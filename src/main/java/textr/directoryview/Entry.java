package textr.directoryview;

public abstract class Entry {
    Entry parent;

    public abstract String getName();
    public abstract Entry getParent();
}
