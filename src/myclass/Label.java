package myclass;

import myenum.ProcessIndex;
import struct.Instruction;

public class Label {
    private LabelEntry[] labelTable;
    int labelCount;
    public static Instruction[] instructionBuffer = new Instruction[2000];


    public Label() {
        int MAXLABELS = 500;
        this.labelTable = new LabelEntry[MAXLABELS];
        this.labelCount = 2;

        labelTable[0] = new LabelEntry(
                "read",
                ProcessIndex.READPROC.getValue(),
                null
        );
        labelTable[1] = new LabelEntry(
                "write",
                ProcessIndex.WRITEPROC.getValue(),
                null
        );
        labelTable[2] = new LabelEntry(
                "lf",
                ProcessIndex.LFPROC.getValue(),
                null
        );

        for (int i = 3; i < MAXLABELS; i++) {
            labelTable[i] = new LabelEntry(null, 0, null);
        }

    }

    private void addFix(FixUpList prev, int instruction) {

    }

    public void inserLabel(String label, int value) {
        FixUpList node;
        int index;
        for (index = 0; index <= labelCount && labelTable[index].labelName.equals(label); index++) ;

        if (index > labelCount) {
            labelTable[index].labelName = label;
            labelCount = index;
            labelTable[index].instructionList = null;
        } else {
            node = labelTable[index].instructionList;
            labelTable[index].instructionList = null;
            while (node != null) {
                instructionBuffer[node.instructionAddress].value1 = value;
                node = node.next;
            }
        }

    }

    public void findLabel(String label, int instruction) {

    }

    public void checkUndefinedLabel() {

    }

    class FixUpList {
        int instructionAddress;
        FixUpList next;

        public FixUpList(int instructionAddress, FixUpList next) {
            this.instructionAddress = instructionAddress;
            this.next = next;
        }
    }

    class LabelEntry {
        String labelName;
        int address;
        FixUpList instructionList;

        public LabelEntry(String labelName, int address, FixUpList instructionList) {
            this.labelName = labelName;
            this.address = address;
            this.instructionList = instructionList;
        }
    }
}
