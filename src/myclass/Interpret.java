package myclass;

public class Interpret {
    private UcodeStack stack;
    private int arBase;
    private long tcycle;
    private long exeCount;

    public Interpret() {
        this.stack = new UcodeStack(UcodeStack.STACKSIZE);
        this.arBase = 4;
        this.tcycle = 0;
        this.exeCount = 0;
    }

    public void execute(int startAddress) {


    }

    private void predefinedProcess(int processIndex) {

    }

    private int findAddress(int n) {
        int temp;
        if (Label.instructionBuffer[n].value1 == 0) {
            System.out.println("findAddr() " + "Lexical level is zero ...");
            System.exit(1);
        } else if (Label.instructionBuffer[n].value2 < 0) {
            System.out.println("findAddr() " + "Negative offset ...");
            System.exit(1);
        }


        return -1;
    }

    private void statistic() {

    }


}
