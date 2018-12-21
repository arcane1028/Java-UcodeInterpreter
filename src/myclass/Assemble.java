package myclass;

import myenum.Opcode;
import myinterpreter.UcodeInterpreter;
import struct.Instruction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Formatter;

import static myinterpreter.UcodeInterpreter.LABELSIZE;
import static myinterpreter.UcodeInterpreter.opcodeName;

public class Assemble {
    private int instrCnt;
    private char lineBuffer[];
    private int bufIndex;
    Label labelProcess;
    private char label[];
    public int startAddr;

    private BufferedReader inputFile = UcodeInterpreter.inputFile;
    private BufferedWriter outputFile = UcodeInterpreter.outputFile;
    private Formatter formatter;

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

        for (index = Opcode.notop.ordinal(); index < Opcode.none.ordinal(); index++)
            if (String.valueOf(mnemonic).equals(opcodeName[index]))
                break;
        if (index == Opcode.none.ordinal())
            System.err.println("error Illegal Opcode, " + mnemonic);
        return index;
    }

    private int getOperand() {
        int result;

        while (Character.isSpaceChar(lineBuffer[bufIndex]))
            bufIndex++;
        result = 0;
        while (Character.isDigit(lineBuffer[bufIndex]) && lineBuffer[bufIndex] != '\n')
            result = 10 * result + (lineBuffer[bufIndex++] - '0');
        return result;
    }

    private void instrWrite() {
        int i, j;
        char ch;
        formatter = new Formatter();
        try {
            outputFile.write("\\n line       object           ucode  source program\\n\\n");
            for (i = 1; i <= instrCnt; i++) {
                formatter.format("%-5s", i + "    (");
                formatter.format("%-2d", UcodeInterpreter.instructionBuffer[i].opcode);
                j = UcodeInterpreter.instructionBuffer[i].opcode;
                if (j == Opcode.chkl.ordinal() || j == Opcode.chkh.ordinal() ||
                        j == Opcode.ldc.ordinal() || j == Opcode.bgn.ordinal() ||
                        j == Opcode.ujp.ordinal() || j == Opcode.call.ordinal() ||
                        j == Opcode.fjp.ordinal() || j == Opcode.tjp.ordinal()) {
                    formatter.format("%-5d     ", UcodeInterpreter.instructionBuffer[i].value1);
                } else if (j == Opcode.lod.ordinal() || j == Opcode.str.ordinal() || j == Opcode.lda.ordinal() ||
                        j == Opcode.sym.ordinal() || j == Opcode.proc.ordinal()) {
                    formatter.format("%-5d", UcodeInterpreter.instructionBuffer[i].value1);
                    formatter.format("%-5d", UcodeInterpreter.instructionBuffer[i].value2);
                } else {
                    formatter.format("          ");
                }
                formatter.format(")     ");
                outputFile.write(formatter.toString());
                while ((ch = (char) inputFile.read()) != '\n') {
                    outputFile.write(ch);
                }
                outputFile.write("\n");
            }
            outputFile.write("\n\n\n   ****    Result    ****\n\n\n");
            formatter.flush();
            formatter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assemble() throws IOException {
        boolean done = false;
        boolean end = false;
        int n;

        System.out.println(" == Assembling ... ===");
        String eof = "";
        while ((eof = inputFile.readLine()) != null && !end) {
            instrCnt++;
            bufIndex = 0;

            if (!Character.isSpaceChar(lineBuffer[0])) {
                getLabel();
                labelProcess.insertLabel(String.valueOf(label), instrCnt);
            }
            n = getOpcode();
            UcodeInterpreter.instructionBuffer[instrCnt].opcode=n;
            UcodeInterpreter.staticCnt[n]++;

            switch (Opcode.values()[n]) {
                case chkl:
                case chkh:
                case ldc:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1=getOperand();
                    break;
                case lod:
                case str:
                case lda:
                case sym:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1=getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value2=getOperand();
                    break;
                case proc:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1=getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value2=getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value3=getOperand();
                    break;
                case bgn:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1=getOperand();
                    startAddr = instrCnt;
                    done = true;
                    break;
                case ujp:
                case call:
                case fjp:
                case tjp:
                    getLabel();
                    labelProcess.findLabel(String.valueOf(label), instrCnt);
                    break;
                case endop:
                    if (done) end = true;
            }
        }
        instrWrite();
    }
}
