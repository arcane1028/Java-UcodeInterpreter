package myclass;

import myenum.opcode;

public class Assemble {
    int LABELSIZE = 10;
    private int instrCnt;
    private char lineBuffer[];
    private int bufIndex;
    Label labelProcess;
    private char label[];
    public int startAddr;

    public static String[] opcodeName={
            "notop", "neg",  "inc", "dec", "dup", "swp", "add",
            "sub",   "mult", "div", "mod", "and", "or",  "gt",
            "lt",    "ge",   "le",  "eq",  "ne",  "lod", "ldc",
            "lda",   "ldi",  "ldp", "str", "sti", "ujp", "tjp",
            "fjp",   "call", "ret", "retv","chkh","chkl","nop",
            "proc",  "end",  "bgn", "sym", "dump","none"
    };

    public Assemble() {
        instrCnt = 0;
    }

    private void getLabel() {
        int i;
        while (Character.isSpaceChar(lineBuffer[bufIndex]))
            bufIndex++;
        for (i = 0; i <= LABELSIZE && !Character.isSpaceChar(label[i] = lineBuffer[bufIndex]); bufIndex++, i++) ;
        label[i] = '\0';
    }


    private int getOpcode() {
        char mnemonic[] = new char[5];
        int index;

        bufIndex = 11;
        index = 0;
        while (index < 5 && !Character.isSpaceChar(lineBuffer[bufIndex]))
            mnemonic[index++] = lineBuffer[bufIndex++];
        mnemonic[index] = '\0';

        for (index = opcode.notop.ordinal(); index<opcode.none.ordinal();index++)
            if (String.valueOf(mnemonic).equals(opcodeName[index]))
                break;
        if (index==opcode.none.ordinal())
            System.err.println("error Illegal opcode, "+mnemonic);
        return index;
    }

    private int getOperand() {
        return 0;
    }

    private void instrWrite() {

    }

    public void assemble() {

    }

    public int startAddr() {
        return 0;
    }


}
