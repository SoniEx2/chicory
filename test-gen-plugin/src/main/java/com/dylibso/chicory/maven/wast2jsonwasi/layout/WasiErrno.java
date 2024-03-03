package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.wasm.types.Value;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

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
    EIO,
    EISCONN,
    EISDIR,
    ELOOP,
    EMFILE,
    EMLINK,
    EMSGSIZE,
    EMULTIHOP,
    ENAMETOOLONG,
    ENETDOWN,
    ENETRESET,
    ENETUNREACH,
    ENFILE,
    ENOBUFS,
    ENODEV,
    ENOENT;

    /* TODO add all the rest of them */

    public Value toValue() {
        return Value.i32(this.ordinal());
    }

    public static WasiErrno fromIOException(IOException e) {
        if (e instanceof AccessDeniedException) {
            return EACCES;
        }
        if (e instanceof NoSuchFileException) {
            return ENOENT;
        }
        if (e instanceof FileAlreadyExistsException) {
            return EEXIST;
        }
        return EIO;
    }
}
