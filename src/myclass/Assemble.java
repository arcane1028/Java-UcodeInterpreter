package myclass;

import myenum.Opcode;

import static myinterpreter.UcodeInterpreter.LABELSIZE;
import static myinterpreter.UcodeInterpreter.opcodeName;

public class Assemble {
    private int instrCnt;
    private char lineBuffer[];
    private int bufIndex;
    Label labelProcess;
    private char label[];
    public int startAddr;

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

        for (index = Opcode.notop.ordinal(); index< Opcode.none.ordinal(); index++)
            if (String.valueOf(mnemonic).equals(opcodeName[index]))
                break;
        if (index== Opcode.none.ordinal())
            System.err.println("error Illegal Opcode, "+mnemonic);
        return index;
    }

    private int getOperand() {
        int result;

        while (Character.isSpaceChar(lineBuffer[bufIndex]))
            bufIndex++;
        result=0;
        while (Character.isDigit(lineBuffer[bufIndex])&&lineBuffer[bufIndex]!='\n')
            result=10*result+(lineBuffer[bufIndex++]-'0');
        return result;
    }

    private void instrWrite() {
        int i, j;
        char ch;
        // TODO dodo

    }

    public void assemble() {
        boolean done=false;
        boolean end=false;
        int n;

        System.out.println(" == Assembling ... ===");
    }

    public int startAddr() {
        return 0;
    }


}
