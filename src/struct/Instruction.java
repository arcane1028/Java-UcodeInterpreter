package struct;

public class Instruction {
    public int opcode;
    public int value1;
    public int value2;
    public int value3;

    public Instruction() {
        this.opcode = -1;
        this.value1 = -1;
        this.value2 = -1;
        this.value3 = -1;
    }
}
