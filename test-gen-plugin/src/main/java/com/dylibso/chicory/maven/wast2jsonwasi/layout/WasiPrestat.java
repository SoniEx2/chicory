package com.dylibso.chicory.maven.wast2jsonwasi.layout;

import com.dylibso.chicory.runtime.Instance;

public class WasiPrestat {
    private final int pointer;

    public WasiPrestat(int pointer) {
        this.pointer = pointer;
    }

    public Writer setValue() {
        return new Writer();
    }

    public class Writer {
        private Writer() {}

        public void write(Instance instance, PrestatVariant variant) {
            if (variant instanceof DirVariant) {
                instance.memory()
                        .writeByte(TYPE_FIELD.at(pointer), (byte) PreopenType.DIR.ordinal());
                variant.setValue().write(instance, VARIANT_FIELD.at(pointer));
            }
        }
    }

    public enum PreopenType {
        DIR;

        public static final Layout LAYOUT;

        static {
            Layout.Builder builder = Layout.builder();
            builder.field(Layout.CHAR);
            LAYOUT = builder.build();
        }
    }

    public abstract static class PrestatVariant {
        public interface Writer {
            void write(Instance instance, int pointer);
        }

        public abstract Writer setValue();
    }

    public static class DirVariant extends PrestatVariant {
        private final int len;

        public DirVariant(int len) {
            this.len = len;
        }

        private class Writer implements PrestatVariant.Writer {
            @Override
            public void write(Instance instance, int pointer) {
                instance.memory().writeI32(LEN.at(pointer), len);
            }
        }

        @Override
        public PrestatVariant.Writer setValue() {
            return new Writer();
        }

        public static final Layout LAYOUT;
        public static final Layout.Field LEN;

        static {
            var builder = Layout.builder();
            LEN = builder.field(Layout.INT);
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
