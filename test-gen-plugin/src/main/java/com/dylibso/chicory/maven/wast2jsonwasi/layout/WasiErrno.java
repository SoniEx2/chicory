package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.wasm.types.Value;

public enum WasiErrno {
    SUCCESS,
    E2BIG,
    EACCES,
    EADDRINUSE,
    EADDRNOTAVAIL,
    EAFNOSUPPORT,
    EAGAIN,
    EALREADY,
    EBADF,
    EBADMSG,
    EBUSY,
    ECANCELED,
    ECHILD,
    ECONNABORTED,
    ECONNREFUSED,
    ECONNRESET,
    EDEADLK,
    EDESTADDRREQ,
    EDOM,
    EDQUOT,
    EEXIST,
    EFAULT,
    EFBIG,
    EHOSTUNREACH,
    EIDRM,
    EILSEQ,
    EINPROGRESS,
    EINTR,
    EINVAL,
    EIO;

    /* TODO add all the rest of them */

    public Value toValue() {
        return Value.i32(this.ordinal());
    }
}
