package textr.directoryview;
import textr.*;
import textr.json.SimpleJsonObject;
import textr.json.SimpleJsonString;
import textr.json.SimpleJsonValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class DirectoryView extends View {

    private List<Entry> entries;
    private DirectoryEntry currentEntry;
    private int selectedEntryIndex = 0;
    private int horizontalScrollPosition;
    private int verticalScrollPosition;
    private int rowCursorPosition;
    private int columnCursorPosition;

    private Manager manager;

    public DirectoryView(int rowPosition, int columnPosition, int width, int height, boolean isActive) throws IOException {
        super(rowPosition, columnPosition, width, height, isActive);
        Path path = Paths.get("").toAbsolutePath();
        currentEntry = new FileDirectoryEntry(path);
        refreshEntries();
        rowCursorPosition = rowPosition + 1;
        columnCursorPosition = columnPosition + 1;
        verticalScrollPosition = 0;
        horizontalScrollPosition = 0;
    }

    public DirectoryView(int rowPosition, int columnPosition, int width, int height, boolean isActive, SimpleJsonObject json) throws IOException {
        super(rowPosition, columnPosition, width, height, isActive);
        currentEntry = new JsonDirectoryEntry(json);
        refreshEntries();
        rowCursorPosition = rowPosition + 1;
        columnCursorPosition = columnPosition + 1;
        verticalScrollPosition = 0;
        horizontalScrollPosition = 0;

        this.manager = manager;
    }


    @Override
    public void setActive(boolean b) {
        active = b;
        updateView();
    }

    @Override
    public void updateView() {
        updateBorder();
        updateStatusBar();
        updateText();
        Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
    }

    public void updateText() {
        int row = rowPosition + 1; // Start printing from the second row to leave space for the border
        int column = columnPosition + 1; // Start printing from the second column to leave space for the border
        int maxEntries = height - 4;
        for (int i = 0; i < maxEntries; i++) {
            if (i + verticalScrollPosition < entries.size()) {
                Entry myEntry = entries.get(i + verticalScrollPosition);
                String entryName;
                if (currentEntry.getParent() != null && myEntry.getName().equals(currentEntry.getParent().getName())) {
                    entryName = "..";
                } else {
                    entryName = myEntry.getName();
                }

                Manager.io.printText(row, column, entryName);
                row++; // Move to the next row for the next entry
            }
        }
    }

    public void updateBorder() {
        String borderCharacter = "+";
        if (active) {
            borderCharacter = "*";
        }

        // print borders
        for (int i = 0; i <= height; i++) {
            Manager.io.printText(rowPosition + i, columnPosition, borderCharacter);
            Manager.io.printText(rowPosition + i, columnPosition + width, borderCharacter);
        }

        for (int i = 0; i <= width; i++) {
            Manager.io.printText(rowPosition, columnPosition + i, borderCharacter);
            Manager.io.printText(rowPosition + height, columnPosition + i, borderCharacter);
        }
        Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
    }

    public void updateStatusBar() {
        String borderCharacter = "+";
        if (active) {
            borderCharacter = "*";
        }

        String statusString = String.format("Directorypath:%s", currentEntry.getName());

        String statusBorder = new String(new char[width]).replace('\0', (borderCharacter).charAt(0));

        // clean status bar row
        Manager.io.printText(rowPosition + height - 1, columnPosition + 2, new String(new char[width - 2]).replace('\0', ' '));

        Manager.io.printText(rowPosition + height - 1, columnPosition + 1, statusString);
        Manager.io.printText(rowPosition + height - 2, columnPosition + 1, statusBorder);
        Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
    }

    private void refreshEntries() {
        try {
            this.entries = new ArrayList<>(currentEntry.getChildren());
            if (currentEntry.getParent() != null) {
                this.entries.add(0, currentEntry.getParent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveCursor(int key) {
        switch (key) {
            case KeyInput.ARROW_UP:
                // Move up
                if (selectedEntryIndex > 0) {
                    selectedEntryIndex--;
                    if (rowCursorPosition > rowPosition + 1) {
                        rowCursorPosition--;
                    } else if (verticalScrollPosition > 0) {
                        verticalScrollPosition--;
                    }
                }
                break;
            case KeyInput.ARROW_DOWN:
                // Move down
                if (selectedEntryIndex < entries.size() - 1) {
                    selectedEntryIndex++;
                    if (rowCursorPosition < rowPosition + height - 4) {
                        rowCursorPosition++;
                    } else {
                        verticalScrollPosition++;
                    }
                }
                break;
            case KeyInput.ARROW_LEFT:
                // Scroll left

                break;
            case KeyInput.ARROW_RIGHT:
                // Scroll right

                break;
        }

        Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
    }

    public void clearView() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Manager.io.printText(rowPosition + i, columnPosition + j, " ");
            }
        }
    }

    public void openEntry() throws IOException {
        Entry selectedEntry = entries.get(selectedEntryIndex);
        if (selectedEntry instanceof DirectoryEntry) {
            currentEntry = (DirectoryEntry) selectedEntry;
            refreshEntries();
            selectedEntryIndex = 0;
            rowCursorPosition = rowPosition + 1;
            verticalScrollPosition = 0;
            clearView();
            updateView();
        } else {
            if (selectedEntry instanceof JsonLeafEntry){
                SimpleJsonValue value = ((JsonLeafEntry) selectedEntry).getValue();
                SimpleJsonString jsonString = (SimpleJsonString) value;
                String content = jsonString.getValue();

                // Check if the content contains an incorrect line separator
                if (content.contains("\r\n") || content.contains("\n\r")) {
                    // Ring the terminal bell
                    System.out.print("\007");
                    System.out.flush();
                    return;
                }

                FileBuffer buffer = new FileBuffer(jsonString, content, (TextView) Manager.getActiveLayout().getLeft().getView());
                TextView view = new TextView(rowPosition, columnPosition, width, height, active);
                view.setBuffer(buffer);
                LeafLayout leafLayout = new LeafLayout(rowPosition,columnPosition,width,height,view);
                manager.replaceActiveLayout(leafLayout);
            }

            else {
                // Open file
                FileBuffer existingBuffer = Manager.getFileBuffer(selectedEntry.getName().toString());
                if (existingBuffer != null) {
                    // If a FileBuffer already exists for the selected entry, create a new FileBufferView for the existing FileBuffer
                    TextView view = new TextView(rowPosition, columnPosition, width, height, true);
                    view.setBuffer(existingBuffer);
                    LeafLayout layout = new LeafLayout(rowPosition, columnPosition, width, height, view);
                    Manager.replaceActiveLayout(layout);
                } else {
                    // If no FileBuffer exists for the selected entry, create a new FileBuffer and a new FileBufferView
                    LeafLayout leafLayout = new LeafLayout(selectedEntry.getName().toString(), rowPosition, columnPosition, width, height);
                    Manager.replaceActiveLayout(leafLayout);
                }
                LeafLayout leafLayout = new LeafLayout(selectedEntry.getName().toString(), rowPosition, columnPosition, width, height);
                manager.replaceActiveLayout(leafLayout);
            }
        }

    }

}
