package textr;

public interface BufferObserver {
    void bufferChanged();
    void bufferLineSplit(int row);
    void bufferLineMerged(int row);
}
