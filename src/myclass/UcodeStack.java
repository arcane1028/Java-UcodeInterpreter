package myclass;

public class UcodeStack {
    public static final int STACKSIZE = 2000;
    private int size;
    private int sp;
    private int stackArray[];

    public UcodeStack(int size) {
        stackArray = new int[size];
        sp = -1;
        push(-1);
        push(-1);
        push(-1);
        push(0);
        push(0);
        push(0);
        push(-1);
        push(1);
    }

    public void push(int value) {
        if (sp == STACKSIZE)
            System.err.println("error : push() Stack Overflow...");
        stackArray[++sp] = value;
    }

    public int pop() {
        if (sp == 0)
            System.err.println("error : pop() Stack Underflow...");
        return stackArray[sp--];
    }

    public int top() {
        return sp;
    }

    public void spSet(int n) {
        sp = n;
    }

    public void dump() {
        System.out.println("stack dump (address : value)");
        for (int i = sp - 10; i <= sp; ++i) {
            System.out.println(i + " : " + stackArray[i]);
        }
        System.out.println();
    }

    public int get(int index) {
        return stackArray[index];
    }

    public void set(int index, int value) {
        this.stackArray[index] = value;
    }

    // TODO delete operator overloading
    public int operator(int index) {
        return stackArray[index];
    }
}
