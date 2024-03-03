package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.runtime.Instance;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class WasiParams {
    private final byte[][] params;

    public WasiParams(List<String> params) {
        this.params =
                params.stream()
                        .map(param -> param.getBytes(StandardCharsets.UTF_8))
                        .toArray(byte[][]::new);
    }

    protected int getCount() {
        return params.length;
    }

    protected int getSize() {
        return Arrays.stream(params).mapToInt(value -> value.length + 1).sum();
    }

    protected SizesResult getSizes() {
        return new SizesResult();
    }

    protected DataResult getData() {
        return new DataResult();
    }

    public class DataResult {
        protected DataResult() {}

        public WasiErrno write(Instance instance, int paramAddresses, int paramBuf) {
            for (int i = 0, addr = paramBuf; i < params.length; addr += params[i].length + 1, i++) {
                instance.memory().write(addr, params[i]);
                instance.memory().writeByte(addr + params[i].length, (byte) 0);
                instance.memory().writeI32(Layout.PTR32.arrayElement(paramAddresses, i), addr);
            }
            instance.memory().writeI32(Layout.PTR32.arrayElement(paramAddresses, params.length), 0);
            return WasiErrno.SUCCESS;
        }
    }

    public class SizesResult {
        protected SizesResult() {}

        public WasiErrno write(Instance instance, int count, int size) {
            instance.memory().writeI32(count, getCount());
            instance.memory().writeI32(size, getSize());
            return WasiErrno.SUCCESS;
        }
    }
}
