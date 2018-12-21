package myclass;

import myenum.Opcode;
import myinterpreter.UcodeInterpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.StringTokenizer;

import static myinterpreter.UcodeInterpreter.opcodeName;

public class Assemble {
    private int instrCnt;
    private String line;
    private int bufIndex;
    private Label labelProcess;
    private String label;
    public int startAddr;

    private BufferedReader inputFile = UcodeInterpreter.inputFile;
    private BufferedWriter outputFile = UcodeInterpreter.outputFile;
    private Formatter formatter;

    public Assemble() {
        instrCnt = 0;
        labelProcess = new Label();
    }

    private void getLabel() {
        while (Character.isSpaceChar(line.charAt(bufIndex)))
            bufIndex++;

        StringTokenizer st = new StringTokenizer(line);
        if (bufIndex == 0) {
            label = st.nextToken();
        } else {
            st.nextToken();
            label = st.nextToken();
        }
    }


    private int getOpcode() {
        String mnemonic = "";
        int index = 0;

        bufIndex = 11;

        while ((bufIndex < line.length()) && !Character.isSpaceChar(line.charAt(bufIndex)))
            mnemonic += line.charAt(bufIndex++);

        for (index = Opcode.notop.ordinal(); index < Opcode.none.ordinal(); index++)
            if (opcodeName[index].contentEquals(mnemonic))
                break;
        if (index == Opcode.none.ordinal())
            System.err.println("error Illegal Opcode, " + mnemonic);
        return index;
    }

    private int getOperand() {
        int result;

        while (Character.isSpaceChar(line.charAt(bufIndex)))
            bufIndex++;
        result = 0;
        while ((bufIndex < line.length()) && Character.isDigit(line.charAt(bufIndex)))
            result = 10 * result + (line.charAt(bufIndex++) - '0');
        return result;
    }

    private void instrWrite() {
        int i, j;

        try {
            inputFile.reset();
            outputFile.write("\n line       object           ucode  source program\n\n");

            for (i = 1; i <= instrCnt; i++) {
                formatter = new Formatter();
                formatter.format("%-5s", i);
                formatter.format("    (");
                formatter.format("%-2d", UcodeInterpreter.instructionBuffer[i].opcode);
                j = UcodeInterpreter.instructionBuffer[i].opcode;
                if (j == Opcode.chkl.ordinal() || j == Opcode.chkh.ordinal() ||
                        j == Opcode.ldc.ordinal() || j == Opcode.bgn.ordinal() ||
                        j == Opcode.ujp.ordinal() || j == Opcode.call.ordinal() ||
                        j == Opcode.fjp.ordinal() || j == Opcode.tjp.ordinal()) {
                    formatter.format("%5d     ", UcodeInterpreter.instructionBuffer[i].value1);
                } else if (j == Opcode.lod.ordinal() || j == Opcode.str.ordinal() || j == Opcode.lda.ordinal() ||
                        j == Opcode.sym.ordinal() || j == Opcode.proc.ordinal()) {
                    formatter.format("%5d", UcodeInterpreter.instructionBuffer[i].value1);
                    formatter.format("%5d", UcodeInterpreter.instructionBuffer[i].value2);
                } else {
                    formatter.format("          ");
                }
                formatter.format(")     ");
                outputFile.write(formatter.toString());
                outputFile.write(inputFile.readLine());
                outputFile.write("\n");

            }
            outputFile.write("\n\n\n   ****    Result    ****\n\n\n");

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

        while ((line = inputFile.readLine()) != null && !end) {
            instrCnt++;
            bufIndex = 0;

            if (!Character.isSpaceChar(line.charAt(0))) {
                getLabel();
                labelProcess.insertLabel(String.valueOf(label), instrCnt);
            }
            n = getOpcode();
            UcodeInterpreter.instructionBuffer[instrCnt].opcode = n;
            UcodeInterpreter.staticCnt[n]++;

            switch (Opcode.values()[n]) {
                case chkl:
                case chkh:
                case ldc:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1 = getOperand();
                    break;
                case lod:
                case str:
                case lda:
                case sym:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1 = getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value2 = getOperand();
                    break;
                case proc:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1 = getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value2 = getOperand();
                    UcodeInterpreter.instructionBuffer[instrCnt].value3 = getOperand();
                    break;
                case bgn:
                    UcodeInterpreter.instructionBuffer[instrCnt].value1 = getOperand();
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
