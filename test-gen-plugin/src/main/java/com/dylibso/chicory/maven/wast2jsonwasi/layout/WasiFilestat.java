package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;

public class WasiFilestat {
    private final int pointer;

    public static class Values {
        public long dev;
        public long ino;
        public WasiFiletype filetype;
        public long nlink;
        public long size;
        public long atim;
        public long mtim;
        public long ctim;
    }

    public WasiFilestat(int pointer) {
        this.pointer = pointer;
    }

    public WriteValues writeValues() {
        return new WriteValues();
    }

    public class WriteValues {
        private WriteValues() {}

        public void write(Instance instance, Values values) {
            Memory memory = instance.memory();
            memory.writeLong(DEV.at(pointer), values.dev);
            memory.writeLong(INO.at(pointer), values.ino);
            memory.writeLong(NLINK.at(pointer), values.nlink);
            memory.writeLong(SIZE.at(pointer), values.size);
            memory.writeLong(ATIM.at(pointer), values.atim);
            memory.writeLong(MTIM.at(pointer), values.mtim);
            memory.writeLong(CTIM.at(pointer), values.ctim);
            memory.writeByte(FILETYPE.at(pointer), (byte) values.filetype.ordinal());
        }
    }

    public static final Layout LAYOUT;
    public static final Layout.Field DEV, INO, FILETYPE, NLINK, SIZE, ATIM, MTIM, CTIM;

    static {
        var builder = Layout.builder();
        DEV = builder.field(Layout.LONG);
        INO = builder.field(Layout.LONG);
        FILETYPE = builder.field(Layout.CHAR);
        NLINK = builder.field(Layout.LONG);
        SIZE = builder.field(Layout.LONG);
        ATIM = builder.field(Layout.LONG);
        MTIM = builder.field(Layout.LONG);
        CTIM = builder.field(Layout.LONG);
        LAYOUT = builder.build();
    }
}
