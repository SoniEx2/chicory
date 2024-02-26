package com.dylibso.chicory.maven.wast2jsonwasi;

import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiErrno;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiIovec;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.types.Value;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class WasiInputOutput {
    private Fildes[] fdinfo;

    public static class Builder {
        private List<Fildes> fdinfo = new ArrayList<>();

        private Builder() {}

        public WasiInputOutput build() {
            return new WasiInputOutput(this);
        }
    }

    private WasiInputOutput(Builder builder) {
        this.fdinfo = builder.fdinfo.toArray(Fildes[]::new);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Value[] fd_prestat_get(Instance instance, Value[] values) {
        int fd = values[0].asInt();

        return new Value[] {Value.i32(WasiErrno.EBADF.ordinal())};
    }

    public Value[] fd_fdstat_get(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_fdstat_set_flags(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_prestat_dir_name(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_read(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_write(Instance instance, Value[] values) {
        // TODO this is not exactly compliant, but it is a good start.
        int fd = values[0].asInt();
        int iov = values[1].asInt();
        int iovCount = values[2].asInt();
        int pWritten = values[3].asInt();
        var iovArray = WasiIovec.arrayFrom(instance, iov, iovCount);
        OutputStream stream;
        if (fd == 1) {
            stream = System.out;
        } else if (fd == 2) {
            stream = System.err;
        } else {
            // return new Value[] {WasiErrno.EBADF.toValue()};
            throw new RuntimeException("unimplemented");
        }
        int bytesWritten = 0;
        for (WasiIovec wasiIovec : iovArray) {
            byte[] data = wasiIovec.readBytes().read(instance);
            try {
                stream.write(data);
                bytesWritten += data.length;
            } catch (IOException e) {
                return new Value[] {WasiErrno.EIO.toValue()};
            }
        }
        instance.memory().writeI32(pWritten, bytesWritten);
        return new Value[] {WasiErrno.SUCCESS.toValue()};
    }

    public Value[] fd_seek(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_close(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] path_filestat_get(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] path_open(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }
}
