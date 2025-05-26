package textr.json;

// this code is largely based on the code included in the assignment

sealed public abstract class SimpleJsonValue permits SimpleJsonString, textr.json.SimpleJsonObject {
    /**
     * The property for which this is the value, or null if this is a toplevel value.
     */
    public SimpleJsonProperty property;

}

