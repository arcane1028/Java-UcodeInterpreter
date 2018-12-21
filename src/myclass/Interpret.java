package myclass;


import myenum.ProcessIndex;
import myenum.Opcode;
import myinterpreter.UcodeInterpreter;
import struct.Instruction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Formatter;

public class Interpret {
    private UcodeStack stack;
    private int arBase;
    private long tcycle;
    private long exeCount;
    private Instruction instructionBuffer[] = UcodeInterpreter.instructionBuffer;

    public Interpret() {
        this.stack = new UcodeStack(UcodeInterpreter.STACKSIZE);
        this.arBase = 4;
        this.tcycle = 0;
        this.exeCount = 0;

    }

    public void execute(int startAddress) throws IOException {
        int parms = 0;
        int temp = 0;
        int temp1 = 0;
        int pc = 0;
        //int returnAddress;

        pc = startAddress;
        System.out.println(" == Executing ...  ==");
        System.out.println(" == Result         ==");

        while (pc >= 0) {
            UcodeInterpreter.dynamicCnt[instructionBuffer[pc].opcode]++;
            if (UcodeInterpreter.executable[instructionBuffer[pc].opcode] == 1) exeCount++;
            tcycle += UcodeInterpreter.opcodeCycle[instructionBuffer[pc].opcode];
            switch (Opcode.values()[instructionBuffer[pc].opcode]) {
                case notop:
                    stack.push((stack.pop() == 0) ? 1 : 0);
                    break;
                case neg:
                    stack.push(-stack.pop());
                    break;
                case add:
                    stack.push(stack.pop() + stack.pop());
                    break;
                case divop:
                    temp = stack.pop();
                    if (temp == 0)
                        UcodeInterpreter.errmsg("execute()", "Divide by Zero ...");
                    stack.push(stack.pop() / temp);
                    break;
                case sub:
                    temp = stack.pop();
                    stack.push(stack.pop() - temp);
                    break;
                case mult:
                    stack.push(stack.pop() * stack.pop());
                    break;
                case modop:
                    temp = stack.pop();
                    stack.push(stack.pop() % temp);
                    break;
                case andop:
                    stack.push(stack.pop() & stack.pop());
                    break;
                case orop:
                    stack.push(stack.pop() | stack.pop());
                    break;
                case gt:
                    temp = stack.pop();
                    stack.push((stack.pop() > temp) ? 1 : 0);
                    break;
                case lt:
                    temp = stack.pop();
                    stack.push((stack.pop() < temp) ? 1 : 0);
                    break;
                case ge:
                    temp = stack.pop();
                    stack.push((stack.pop() >= temp) ? 1 : 0);
                    break;
                case le:
                    temp = stack.pop();
                    stack.push((stack.pop() <= temp) ? 1 : 0);
                    break;
                case eq:
                    temp = stack.pop();
                    stack.push((stack.pop() == temp) ? 1 : 0);
                    break;
                case ne:
                    temp = stack.pop();
                    stack.push((stack.pop() != temp) ? 1 : 0);
                    break;
                case swp:
                    temp = stack.pop();
                    temp1 = stack.pop();
                    stack.push(temp);
                    stack.push(temp1);
                    break;
                case lod:
                    stack.push(stack.get(findAddress(pc)));
                    break;
                case ldc:
                    stack.push(instructionBuffer[pc].value1);
                    break;
                case lda:
                    stack.push(findAddress(pc));
                    break;
                case str:
                    stack.set(findAddress(pc), stack.pop());
                    break;
                case ldi:
                    if ((stack.top() <= 0) || (stack.top() > UcodeInterpreter.STACKSIZE))
                        UcodeInterpreter.errmsg("execute()", "Illegal ldi instruction ...");
                    temp = stack.pop();
                    stack.push(temp);
                    stack.set(stack.top(), stack.get(temp));
                    break;
                case sti:
                    temp = stack.pop();
                    stack.set(stack.top(), temp);
                    break;
                case ujp:
                    pc = instructionBuffer[pc].value1 - 1;
                    break;
                case tjp:
                    if (stack.pop() != 0)
                        pc = instructionBuffer[pc].value1 - 1;
                    break;
                case fjp:
                    if (stack.pop() == 0)
                        pc = instructionBuffer[pc].value1 - 1;
                    break;
                case chkh:
                    temp = stack.pop();
                    if (temp > instructionBuffer[pc].value1)
                        UcodeInterpreter.errmsg("execute()", "High check failed...");
                    stack.push(temp);
                    break;
                case chkl:
                    temp = stack.pop();
                    if (temp < instructionBuffer[pc].value1)
                        UcodeInterpreter.errmsg("execute()", "Low check failed...");
                    stack.push(temp);
                    break;
                case ldp:
                    parms = stack.top() + 1;        // save sp
                    stack.spSet(stack.top() + 4);   // set a frame
                    break;
                case call:
                    if ((temp = instructionBuffer[pc].value1) < 0) predefinedProcess(temp);
                    else {
                        stack.set(parms + 2, pc + 1);
                        stack.set(parms + 1, arBase);
                        arBase = parms;
                        pc = instructionBuffer[pc].value1 - 1;
                    }
                    break;
                case retv:
                    temp = stack.pop();
                case ret:
                    stack.spSet(arBase - 1);
                    if (instructionBuffer[pc].opcode == Opcode.retv.ordinal())
                        stack.push(temp);
                    pc = stack.get(arBase + 2) - 1;
                    arBase = stack.get(arBase + 1);
                    break;
				  /*
          case stp:
                  returnAddress = stack[arBase+2] - 1;
                  stack.spSet(arBase - 1);
                  arBase = stack[arBase + 1];
                  break;
          case ret:
                  pc = returnAddress;
                  break;
				  */
                case proc:
                    // value 1: (size of paras + size of local vars)
                    // value 2: block number(base)
                    // value 3: static level => lexical level (static chain)
                    stack.spSet(arBase + instructionBuffer[pc].value1 + 3);
                    stack.set(arBase + 3, instructionBuffer[pc].value2);
                    for (temp = stack.get(arBase + 1);
                         stack.get(temp + 3) != instructionBuffer[pc].value3 - 1;
                         temp = stack.get(temp))
                        ;
                    stack.set(arBase, temp);
                    break;
                case endop:
                    pc = -2;
                    break;
                case bgn:
                    stack.spSet(stack.top() + instructionBuffer[pc].value1);
                    break;
		  /*
		  case ones:
                  temp = stack.pop();
                  stack.push(~temp);
                  break;
		  */
                case nop:
                case sym:
                    break;
                /* augmented operation code */
                case incop:
                    temp = stack.pop();
                    stack.push(++temp);
                    break;
                case decop:
                    temp = stack.pop();
                    stack.push(--temp);
                    break;
                case dupl:
                    temp = stack.pop();
                    stack.push(temp);
                    stack.push(temp);
                    break;
                case dump:
                    stack.dump();
                    break;
            }
            pc++;
        }
        System.out.println("");
        statistic();

    }

    private void predefinedProcess(int processIndex) throws IOException {
        int data = -1;
        int temp;

        if (processIndex == ProcessIndex.READPROC.getValue()) {
            System.out.print(data);
            temp = stack.pop();
            stack.set(temp, data);
            stack.spSet(stack.top() - 4);
        } else if (processIndex == ProcessIndex.WRITEPROC.getValue()) {
            temp = stack.pop();
            System.out.print(" "+temp);
            UcodeInterpreter.outputFile.write(" " + temp);
            stack.spSet(stack.top() - 4);
        } else if (processIndex == ProcessIndex.LFPROC.getValue()) {
            UcodeInterpreter.outputFile.write("\n");
            System.out.print("\n");
        }
    }

    private int findAddress(int n) {
        int temp;
        if (instructionBuffer[n].value1 == 0) {
            UcodeInterpreter.errmsg("findAddr()", "Lexical level is zero ...");
        } else if (instructionBuffer[n].value2 < 0) {
            UcodeInterpreter.errmsg("findAddr()", "Negative offset ...");
        }
        for (temp = arBase;
             instructionBuffer[n].value1 != stack.get(temp + 3);
             temp = stack.get(temp)) {
            if ((temp > UcodeInterpreter.STACKSIZE) || (temp < 0))
                System.out.println(
                        "Lexical level :  " + instructionBuffer[n].value1 + "\n" +
                        "Offset        :  " + instructionBuffer[n].value2);
        }
        return (temp + instructionBuffer[n].value2 + 3);
    }

    private void statistic() throws IOException {

        BufferedWriter outputFile = UcodeInterpreter.outputFile;
        Formatter formatter;

        outputFile.write("\n\n   **********************\n\n");
        outputFile.write("\n\n\n                 #### Statistics ####\n");
        outputFile.write("\n\n    ****  Static Instruction Counts  ****\n\n\n");

        for (int i = 0, op = Opcode.notop.ordinal(); op <= Opcode.none.ordinal(); op++) {
            if (UcodeInterpreter.staticCnt[op] != 0) {
                formatter = new Formatter();
                formatter.format("%-5s  =  %-5d ", UcodeInterpreter.opcodeName[op], UcodeInterpreter.staticCnt[op]);
                outputFile.write(formatter.toString());
                i++;
                if (i % 4 == 0) outputFile.write("\n");
                formatter.close();
            }
        }
        outputFile.write("\n\n\n  ****  Dynamic instruction counts  ****\n\n\n");
        for (int i = 0, op = Opcode.notop.ordinal(); op <= Opcode.none.ordinal(); op++) {
            if (UcodeInterpreter.dynamicCnt[op] != 0) {
                formatter = new Formatter();
                formatter.format("%-5s  =  %-5d ", UcodeInterpreter.opcodeName[op], UcodeInterpreter.dynamicCnt[op]);
                i++;
                outputFile.write(formatter.toString());
                if (i % 4 == 0) outputFile.write("\n");
                formatter.close();
            }
        }
        outputFile.write("\n\n Executable instruction count  =   " + exeCount);
        outputFile.write("\n\n Total execution cycle         =   " + tcycle + "\n");

    }


}
