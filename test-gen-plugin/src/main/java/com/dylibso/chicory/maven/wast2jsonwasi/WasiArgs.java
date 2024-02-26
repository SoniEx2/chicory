package com.dylibso.chicory.maven.wast2jsonwasi;

import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiParams;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.types.Value;
import java.util.List;

public class WasiArgs extends WasiParams {
    public WasiArgs(List<String> params) {
        super(params);
    }

    public Value[] args_get(Instance instance, Value[] values) {
        return new Value[] {
            getData().write(instance, values[0].asInt(), values[1].asInt()).toValue()
        };
    }

    public Value[] args_sizes_get(Instance instance, Value[] values) {
        return new Value[] {
            getSizes().write(instance, values[0].asInt(), values[1].asInt()).toValue()
        };
    }
}
