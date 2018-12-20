package myclass;

import myenum.ProcessIndex;

public class Label {
    private LabelEntry[] labelTable;
    int labelCount;


    public Label() {
        int MAXLABELS = 500;
        this.labelTable = new LabelEntry[MAXLABELS];
        this.labelCount = 2;

        int index;

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


    class FixUpList {
        int instructionAddress;
        FixUpList next;
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
