package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.runtime.Instance;

/**
 * iovec (for reads) and ciovec (for writes) layout/deserializer.
 */
public class WasiIovec {
    public static final Layout.Field BUF_FIELD;
    public static final Layout.Field BUF_LEN_FIELD;
    public static final Layout LAYOUT;

    static {
        Layout.Builder builder = Layout.builder();
        BUF_FIELD = builder.field(Layout.PTR32);
        BUF_LEN_FIELD = builder.field(Layout.INT);
        LAYOUT = builder.build();
    }

    private final int bufAddr;
    private final int bufLen;

    private WasiIovec(int addr, int len) {
        this.bufAddr = addr;
        this.bufLen = len;
    }

    public static WasiIovec from(Instance instance, int pointer) {
        var addr = instance.memory().readI32(BUF_FIELD.at(pointer));
        var len = instance.memory().readI32(BUF_LEN_FIELD.at(pointer));
        return new WasiIovec(addr.asInt(), len.asInt());
    }

    public static WasiIovec[] arrayFrom(Instance instance, int pointer, int count) {
        var output = new WasiIovec[count];
        for (int i = 0; i < count; i++) {
            output[i] = WasiIovec.from(instance, WasiIovec.LAYOUT.arrayElement(pointer, i));
        }
        return output;
    }

    public int getBufLen() {
        return bufLen;
    }

    public int getBufAddr() {
        return bufAddr;
    }

    public Reader readBytes() {
        return new Reader();
    }

    public class Reader {
        private Reader() {}

        public byte[] read(Instance instance) {
            return instance.memory().readBytes(bufAddr, bufLen);
        }
    }

    // TODO reader/writer
}
