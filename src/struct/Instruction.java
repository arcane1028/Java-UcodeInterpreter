package struct;

public class Instruction {
    public int opcode;
    public int value1;
    public int value2;
    public int value3;

    public Instruction(int opcode, int value1, int value2, int value3) {
        this.opcode = opcode;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
