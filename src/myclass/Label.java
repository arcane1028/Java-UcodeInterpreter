package myclass;

import myenum.ProcessIndex;
import myenum.opcode;
import struct.Instruction;

public class Label {
    private LabelEntry[] labelTable;
    private int labelCount;
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


    public void insertLabel(String label, int value) {
        FixUpList node;
        int index;
        for (index = 0; index <= labelCount && labelTable[index].labelName.equals(label); index++) ;

        if (index > labelCount) {
            labelTable[index] = new LabelEntry(label, value, null);
            labelCount = index;
            /*
            labelTable[index].labelName = label;
            labelCount = index;
            labelTable[index].instructionList = null;
            */
        } else {
            labelTable[index].address = value;
            node = labelTable[index].instructionList;
            labelTable[index].instructionList = null;
            while (node != null) {
                instructionBuffer[node.instructionAddress].value1 = value;
                node = node.next;
            }
        }
    }

    public void findLabel(String label, int instruction) {
        FixUpList node;
        int index;
        for (index = 0; index <= labelCount && labelTable[index].labelName.equals(label); index++) ;

        if (index > labelCount) {
            labelTable[index].labelName = label;
            labelCount = index;
            node = new FixUpList(-1, null);
            /*
            if (node == null) {
                System.err.println("findLabel() " + " Out of memory -- new");
            }
            */
            labelTable[index].instructionList = node;
            node.instructionAddress = instruction;
            node.next = null;
        } else {
            node = labelTable[index].instructionList;
            if (node != null) {
                addFix(node, instruction);
            } else {
                instructionBuffer[instruction].value1 = labelTable[index].address;
            }
        }
    }

    private void addFix(FixUpList prev, int instruction) {
        FixUpList node;

        while (prev.next != null) {
            prev = prev.next;
        }
        node = new FixUpList(-1, null);
        /*
        if (node == null) {
            System.err.println("addFix() " + " Out of memory");
        }
        */
        node.instructionAddress = instruction;
        node.next = null;
        prev.next = node;
    }

    public void checkUndefinedLabel() {
        for (int index = 0; index <= labelCount; index++) {
            if (labelTable[index].address == ProcessIndex.UNDEFINED.getValue()) {
                System.err.println("undefined label  " + labelTable[index].labelName);
            }
        }
    }

    class FixUpList {
        int instructionAddress;
        FixUpList next;

        FixUpList(int instructionAddress, FixUpList next) {
            this.instructionAddress = instructionAddress;
            this.next = next;
        }
    }

    class LabelEntry {
        String labelName;
        int address;
        FixUpList instructionList;

        LabelEntry(String labelName, int address, FixUpList instructionList) {
            this.labelName = labelName;
            this.address = address;
            this.instructionList = instructionList;
        }
    }
}
