package textr;


import io.github.btj.termios.Terminal;
import textr.json.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.List;

public class FileBuffer {
    private final String filePath;
    private Boolean dirty;
    private ArrayList<StringBuilder> contentBuffer = new ArrayList<StringBuilder>();
    private List<BufferObserver> observers = new ArrayList<>();
    private TextView originalTextView;
    private SimpleJsonString propertyName;

    /**
     * initializes filebuffer from given filepath
     * @param path filePath
     * @throws IOException
     */
    public FileBuffer(String path, TextView view) throws IOException {
        this.filePath = path;
        this.dirty=false;
        loadFileContent();
        addObserver(view);
    }

    public FileBuffer(String path) throws IOException {
        this.filePath = path;
        this.dirty=false;
        loadFileContent();
    }

    public FileBuffer(SimpleJsonString string, String content, TextView originalTextView){
        propertyName = string;
        contentBuffer.add(new StringBuilder(content));
        filePath = null;
        this.originalTextView = originalTextView;
        dirty = false;
    }

    /**
     * loads contents of this buffer in the terminal
     * @throws IOException
     */
    private void loadFileContent() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //iterate over the string and check for non-ascii
                for(char i:line.toCharArray()){
                    int b = i; //ascii code
                    if(b < 32 && b != 10 && b != 13 || 127 <= b){
                        Manager.io.clearScreen();
                        Manager.io.leaveRawInputMode();
                        throw new IOException("File contains non-ASCII characters");
                    }
                }
                contentBuffer.add(new StringBuilder(line));
            }
        }
    }

    /**
     * gets filepath of the file
     * @return filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * gets specific part of contentbuffer
     * @param start first line of part of contentbuffer
     * @param end last line of part of contentbuffer
     * @return contentBuffer
     */
    public ArrayList<StringBuilder> getFromToLine(int start, int end){
        ArrayList<StringBuilder> result = new ArrayList<StringBuilder>();
        for (int i=start; i<=end; i++){
            result.add(contentBuffer.get(i));
        }
        return result;
    }

    /**
     * gets length of buffer
     * @return contentBuffer.size()
     */
    public int getBufferLength(){ return contentBuffer.size(); }

    /**
     * looks for the longest line of the contentbuffer
     * @return longest line of contentBuffer
     */
    public int getLongestLineLength() {
        int maxLength = 0;
        for (StringBuilder line : contentBuffer) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }
        return maxLength;
    }

    /**
     * counts amount of characters in the contentbuffer
     * @return amount of characters in contentbuffer
     */
    public int getBufferAmountofChars() {
        int totalChars = 0;
        for (StringBuilder line : contentBuffer) {
            totalChars += line.length();
        }
        return totalChars;
    }

    /**
     * gets whole contentbuffer
     *
     * @return contentbuffer
     */
    public ArrayList<StringBuilder> getContentBuffer() { return contentBuffer; }
    public StringBuilder getLine(int i) {
        if (i < 0 ) {
            throw new RuntimeException("Index out of bounds");
        }
        // extend buffer if necessary
        if (i >= contentBuffer.size()) {
            for (int j = contentBuffer.size(); j <= i; j++) {
                contentBuffer.add(new StringBuilder(""));
            }
        }
        return contentBuffer.get(i);
    }

    public void removeLine(int index) {
        notifyLineMerged(index);
        contentBuffer.remove(index);
        dirty = true;
    }

    /**
     * changes line of content buffer
     * @param row line that needs to be changed
     * @param line replacement string
     */

    public void setContentBuffer(int row, StringBuilder line ){
        notifyObservers();
        dirty = true;
        this.contentBuffer.set(row, new StringBuilder(line));

    }

    public void addContentBuffer(int row, StringBuilder line){
        this.contentBuffer.add(row, new StringBuilder(line));
        dirty = true;
    }

    public void setContent(String content){
        ArrayList<StringBuilder> result = new ArrayList<StringBuilder>();
        result.add(new StringBuilder(content));
        dirty = true;
    }


    /**
     * sets true or fase to dirty if content buffer is changes from file
     * @param dirty true or false
     */
    public void setDirty (Boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * return if buffer is dirty
     * @return true or false
     */
    public Boolean getDirty(){
        return dirty;
    }

    /**
     * save fileBuffer
     * @throws IOException
     */
    public void save() throws IOException {
        if (filePath == null) {
            // Get the JSON object from the original TextView
            SimpleJsonObject jsonObject = originalTextView.jsonify();

            // Find the property in the JSON object that corresponds to the current FileBuffer
            SimpleJsonProperty property = jsonObject.get(propertyName.getValue());

            // Check if the property is not null and its value is an instance of SimpleJsonString
            if (property != null && property.value instanceof SimpleJsonString) {
                // Cast the value to SimpleJsonString and update its value
                SimpleJsonString propertyValue = (SimpleJsonString) property.value;
                propertyValue.value = getContentAsString(); // This is a new method that you need to implement

                // Update the original TextView with the modified JSON object
                originalTextView.updateJsonObject(jsonObject);
            }

            try (PrintWriter writer = new PrintWriter(originalTextView.getBuffer().getFilePath())) {
                for (StringBuilder line : contentBuffer) {
                    writer.println(line);
                }
            }
        } else {
            try (PrintWriter writer = new PrintWriter(filePath)) {
                for (StringBuilder line : contentBuffer) {
                    writer.println(line);
                }
            }
        }

        this.dirty = false;
        notifyObservers();
    }

    public TextView getOriginalTextView() {
        return originalTextView;
    }

    public String getContentAsString(){
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : contentBuffer) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }


    // Method to register observers
    public void addObserver(BufferObserver observer) {
        observers.add(observer);
    }

    // Method to remove observers
    public void removeObserver(BufferObserver observer) {
        observers.remove(observer);
    }

    // Method to notify observers of changes
    private void notifyObservers() {
        for (BufferObserver observer : observers) {
            observer.bufferChanged();
        }
    }

    private void notifyLineMerged(int row) {
        for (BufferObserver observer : observers) {
            observer.bufferLineMerged(row);
        }
    }
    private void notifyLineSplit(int row) {
        for (BufferObserver observer : observers) {
            observer.bufferLineSplit(row);
        }
    }

    public void lineSplit(int row) {
        notifyLineSplit(row);
    }

    public void lineMerged(int row)  {
        notifyLineMerged(row);
    }
}
