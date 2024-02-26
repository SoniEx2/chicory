package com.dylibso.chicory.maven.wast2jsonwasi.layout;

public class WasiPrestat {
    public enum PreopenType {
        DIR;

        public static final Layout LAYOUT;

        static {
            Layout.Builder builder = Layout.builder();
            builder.field(Layout.CHAR);
            LAYOUT = builder.build();
        }
    }

    public static class DirVariant {
        private int len;

        public static final Layout LAYOUT;

        static {
            var builder = Layout.builder();
            builder.field(Layout.INT);
            LAYOUT = builder.build();
        }
    }

    public static final Layout LAYOUT;
    public static final Layout.Field TYPE_FIELD;
    public static final Layout.Field VARIANT_FIELD;

    static {
        Layout.Builder builder = Layout.builder();
        TYPE_FIELD = builder.field(PreopenType.LAYOUT);
        VARIANT_FIELD = builder.union(DirVariant.LAYOUT);
        LAYOUT = builder.build();
    }
}
