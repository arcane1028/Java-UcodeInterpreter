package myinterpreter;

import myclass.Assemble;
import myclass.Interpret;
import struct.Instruction;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class UcodeInterpreter {
    public static final int MAXINSTR = 2000;
    public static final int MAXLABELS = 500;
    public static final int STACKSIZE = 2000;
    public static final int LABELSIZE = 10;
    public static final int NO_OPCODES = 41;

    public static BufferedReader inputFile;
    public static BufferedWriter outputFile;

    public static final String opcodeName[] = {
            "notop", "neg", "inc", "dec", "dup", "swp", "add",
            "sub", "mult", "div", "mod", "and", "or", "gt",
            "lt", "ge", "le", "eq", "ne", "lod", "ldc",
            "lda", "ldi", "ldp", "str", "sti", "ujp", "tjp",
            "fjp", "call", "ret", "retv", "chkh", "chkl", "nop",
            "proc", "end", "bgn", "sym", "dump", "none"
    };
    public static final int executable[] = {
            /* notop */ 1, /* neg */  1, /* inc */  1, /* dec */  1, /* dup */  1,
            /* swp */   1, /* add */  1, /* sub */  1, /* mult */ 1, /* div */  1,
            /* mod */   1, /* and */  1, /* or */   1, /* gt */   1, /* lt */   1,
            /* ge */    1, /* le */   1, /* eq */   1, /* ne */   1, /* lod */  1,
            /* ldc */   1, /* lda */  1, /* ldi */  1, /* ldp */  1, /* str */  1,
            /* sti */   1, /* ujp */  1, /* tjp */  1, /* fjp */  1, /* call */ 1,
            /* ret */   1, /* retv */ 1, /* chkh */ 1, /* chkl */ 1, /* nop */  0,
            /* proc */  1, /* end */  0, /* bgn */  0, /* sym */  0, /* dump */ 1,
            /* none */  0
    };
    public static final int opcodeCycle[] = {
            /* notop */ 5, /* neg */   5, /* inc */  1, /* dec */   1, /* dup */    5,
            /* swp */  10, /* add */  10, /* sub */ 10, /* mult */ 50, /* div */  100,
            /* mod */ 100, /* and */  10, /* or */  10, /* gt */   20, /* lt */    20,
            /* ge */   20, /* le */   20, /* eq */  20, /* ne */   20, /* lod */    5,
            /* ldc */   5, /* lda */   5, /* ldi */ 10, /* ldp */  10, /* str */    5,
            /* sti */  10, /* ujp */  10, /* tjp */ 10, /* fjp */  10, /* call */  30,
            /* ret */  30, /* retv */ 30, /* chkh */ 5, /* chkl */  5, /* nop */    0,
            /* proc */ 30, /* end */   0, /* bgn */  0, /* sym */   0, /* dump */ 100,
            /* none */  0
    };

    public static long dynamicCnt[] = new long[NO_OPCODES];

    public static int staticCnt[] = new int[NO_OPCODES];

    public static Instruction instructionBuffer[] = new Instruction[MAXINSTR];

    public static void errmsg(String s, String s2) {
        System.out.println("Error: " + s + ": " + s2);
        System.exit(1);
    }

    public static void run(String filename) {

        for (int i = 0; i < instructionBuffer.length; i++) {
            instructionBuffer[i] = new Instruction();
        }

        if (filename == null)
            errmsg("run()", "Wrong filename");

        try {
            inputFile = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(filename)),
                            StandardCharsets.UTF_8
                    )
            );
            outputFile = new BufferedWriter(
                    new FileWriter(new File(filename.split("\\.")[0]+".lst"))
            );
            inputFile.mark(2000);
            Assemble sourceProgram = new Assemble();
            Interpret binaryProgram = new Interpret();

            sourceProgram.assemble();
            binaryProgram.execute(sourceProgram.startAddr);

            inputFile.close();
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
