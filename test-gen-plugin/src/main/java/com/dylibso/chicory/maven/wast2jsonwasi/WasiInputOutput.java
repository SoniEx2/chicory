package com.dylibso.chicory.maven.wast2jsonwasi;

import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiErrno;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiFdstat;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiFilestat;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiIovec;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiPrestat;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.types.Value;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WasiInputOutput {
    private List<Fildes> fdinfo;
    // transient does nothing here
    private transient byte[] dir_name_cache;
    private transient Fildes dir_name_cache_fildes;

    public static class Builder {
        private List<Fildes> fdinfo = new ArrayList<>();

        private Builder() {
            fdinfo.add(Fildes.null_source());
            fdinfo.add(Fildes.null_sink());
            fdinfo.add(Fildes.null_sink());
        }

        public Builder addPreopen(Fildes.PreopenDir preopen) {
            fdinfo.add(Objects.requireNonNull(preopen, "preopen may not be null"));
            return this;
        }

        public WasiInputOutput build() {
            return new WasiInputOutput(this);
        }
    }

    private WasiInputOutput(Builder builder) {
        this.fdinfo = new ArrayList<>(builder.fdinfo);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Value[] fd_prestat_get(Instance instance, Value[] values) {
        int fd = values[0].asInt();
        WasiPrestat prestat = new WasiPrestat(values[1].asInt());
        if (fd >= this.fdinfo.size()) {
            return new Value[] {WasiErrno.EBADF.toValue()};
        }
        Fildes fildes = this.fdinfo.get(fd);
        if (fildes instanceof Fildes.PreopenDir) {
            this.dir_name_cache =
                    ((Fildes.PreopenDir) fildes).getMountPoint().getBytes(StandardCharsets.UTF_8);
            this.dir_name_cache_fildes = fildes;
            int length = this.dir_name_cache.length;
            prestat.setValue().write(instance, new WasiPrestat.DirVariant(length));
            return new Value[] {WasiErrno.SUCCESS.toValue()};
        }
        throw new RuntimeException("unimplemented");
        // return new Value[] {Value.i32(WasiErrno.???.ordinal())};
    }

    public Value[] fd_fdstat_get(Instance instance, Value[] values) {
        int fd = values[0].asInt();
        var fdstat = new WasiFdstat(values[1].asInt());
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_fdstat_set_flags(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }

    public Value[] fd_prestat_dir_name(Instance instance, Value[] values) {
        int fd = values[0].asInt();
        int buffer = values[1].asInt();
        int size = values[2].asInt();
        if (fd >= this.fdinfo.size()) {
            return new Value[] {WasiErrno.EBADF.toValue()};
        }
        Fildes fildes = this.fdinfo.get(fd);
        if (fildes instanceof Fildes.PreopenDir) {
            byte[] dir_name;
            if (fildes == dir_name_cache_fildes) {
                dir_name = dir_name_cache;
            } else {
                dir_name =
                        ((Fildes.PreopenDir) fildes)
                                .getMountPoint()
                                .getBytes(StandardCharsets.UTF_8);
            }
            int length = Math.min(dir_name.length, size);
            instance.memory().write(buffer, dir_name, 0, length);
            return new Value[] {WasiErrno.SUCCESS.toValue()};
        }
        throw new RuntimeException("unimplemented");
        // return new Value[] {Value.i32(WasiErrno.???.ordinal())};
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
                return new Value[] {WasiErrno.fromIOException(e).toValue()};
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
        int dfd = values[0].asInt();
        int flags = values[1].asInt();
        int path = values[2].asInt();
        int pathLength = values[3].asInt();
        WasiFilestat filestat = new WasiFilestat(values[4].asInt());
        String stringPath = instance.memory().readString(path, pathLength);
        if (dfd >= this.fdinfo.size()) {
            return new Value[] {WasiErrno.EBADF.toValue()};
        }
        var fildes = this.fdinfo.get(dfd);
        if (!(fildes instanceof Fildes.PreopenDir)) {
            throw new RuntimeException("unimplemented");
        }
        WasiFilestat.Values stat;
        try {
            if ((flags & 1) == 0) {
                stat =
                        ((Fildes.PreopenDir) fildes)
                                .stat(stringPath, WasiLookupOption.NOFOLLOW_LINKS);
            } else {
                stat = ((Fildes.PreopenDir) fildes).stat(stringPath);
            }
        } catch (IOException e) {
            return new Value[] {WasiErrno.fromIOException(e).toValue()};
        }
        filestat.writeValues().write(instance, stat);
        return new Value[] {WasiErrno.SUCCESS.toValue()};
    }

    public Value[] path_open(Instance instance, Value[] values) {
        throw new RuntimeException("unimplemented");
    }
}
