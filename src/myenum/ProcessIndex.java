package myenum;

public enum ProcessIndex {
    READPROC(-1),
    WRITEPROC(-2),
    LFPROC(-3),
    UNDEFINED(-1000);

    private int value;

    ProcessIndex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
