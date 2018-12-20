package myenum;

public enum opcode {
    notop, neg,   incop, decop, dupl,  swp, add,  sub,   mult, divop,
    modop, andop, orop,  gt,    lt,   ge,  le,   eq,    ne,
    lod,   ldc,   lda,   ldi,   ldp,  str, sti,  ujp,   tjp,  fjp,
    call, ret,   retv,  chkh,  chkl, nop, proc, endop, bgn,  sym,
    dump,  none
}
