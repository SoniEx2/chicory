package com.dylibso.chicory.maven.wast2jsonwasi;

import static com.dylibso.chicory.wasm.types.ValueType.I32;
import static com.dylibso.chicory.wasm.types.ValueType.I64;

import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.HostImports;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.WasmFunctionHandle;
import com.dylibso.chicory.wasm.types.Value;
import com.dylibso.chicory.wasm.types.ValueType;
import java.util.Arrays;
import java.util.List;

public class Wast2JsonImports extends HostImports {
    public Wast2JsonImports(WasiArgs args, WasiEnv env, WasiInputOutput io) {
        super(
                new HostFunction[] {
                    wasi_fn(args::args_get, "args_get", I32, I32, I32),
                    wasi_fn(args::args_sizes_get, "args_sizes_get", I32, I32, I32),
                    wasi_fn(env::environ_get, "environ_get", I32, I32, I32),
                    wasi_fn(env::environ_sizes_get, "environ_sizes_get", I32, I32, I32),
                    wasi_fn(io::fd_prestat_get, "fd_prestat_get", I32, I32, I32),
                    wasi_fn(io::fd_fdstat_get, "fd_fdstat_get", I32, I32, I32),
                    wasi_fn(io::fd_fdstat_set_flags, "fd_fdstat_set_flags", I32, I32, I32),
                    wasi_fn(io::fd_prestat_dir_name, "fd_prestat_dir_name", I32, I32, I32, I32),
                    wasi_fn(io::fd_read, "fd_read", I32, I32, I32, I32, I32),
                    wasi_fn(io::fd_write, "fd_write", I32, I32, I32, I32, I32),
                    wasi_fn(io::fd_seek, "fd_seek", I32, I32, I64, I32, I32),
                    wasi_fn(io::fd_close, "fd_close", I32, I32),
                    wasi_fn(
                            io::path_filestat_get,
                            "path_filestat_get",
                            I32,
                            I32,
                            I32,
                            I32,
                            I32,
                            I32),
                    wasi_fn(
                            io::path_open,
                            "path_open",
                            I32,
                            I32,
                            I32,
                            I32,
                            I32,
                            I32,
                            I64,
                            I64,
                            I32,
                            I32),
                    wasi_fn(Wast2JsonImports::proc_exit, "proc_exit", null, I32)
                });
    }

    private static HostFunction wasi_fn(
            WasmFunctionHandle handle, String name, ValueType result, ValueType... args) {
        return new HostFunction(
                handle,
                "wasi_snapshot_preview1",
                name,
                Arrays.asList(args),
                result == null ? List.of() : List.of(result));
    }

    private static Value[] proc_exit(Instance instance, Value... values) {
        throw new ExitStatus(values[0].asInt());
    }
}
