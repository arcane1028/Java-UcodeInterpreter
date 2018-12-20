package myenum;

public enum procIndex {
    READPROC(-1),
    WRITEPROC(-2),
    LFPROC(-3),
    UNDEFINED(-1000);
    private int value;
    procIndex(int value) {
        this.value = value;
    }
}
