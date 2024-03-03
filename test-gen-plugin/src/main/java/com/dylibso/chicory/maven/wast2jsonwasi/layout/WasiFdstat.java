package com.dylibso.chicory.maven.wast2jsonwasi.layout;

public class WasiFdstat {
    private final int pointer;

    public WasiFdstat(int pointer) {
        this.pointer = pointer;
    }

    public static class Values {
        public WasiFiletype filetype;
        public short flags;
        public long baseRights;
        public long inheritingRights;
    }

    public static final Layout LAYOUT;
    public static final Layout.Field FS_FILETYPE, FS_FLAGS, FS_RIGHTS_BASE, FS_RIGHTS_INHERITING;

    static {
        var builder = Layout.builder();
        FS_FILETYPE = builder.field(Layout.CHAR);
        FS_FLAGS = builder.field(Layout.SHORT);
        FS_RIGHTS_BASE = builder.field(Layout.LONG);
        FS_RIGHTS_INHERITING = builder.field(Layout.LONG);
        LAYOUT = builder.build();
    }
}
