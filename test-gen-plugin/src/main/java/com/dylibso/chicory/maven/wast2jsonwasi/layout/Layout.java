package com.dylibso.chicory.maven.wast2jsonwasi.layout;

public final class Layout implements Cloneable {
    private final int alignment;
    private final int size;

    private Layout(int size, int alignment) {
        this.size = size;
        this.alignment = alignment;
    }

    public int size() {
        return size;
    }

    public int alignment() {
        return alignment;
    }

    public static final class Field {
        private final int size, alignment, offset;

        private Field(int size, int alignment, int offset) {
            this.size = size;
            this.alignment = alignment;
            this.offset = offset;
        }

        public int size() {
            return size;
        }

        public int alignment() {
            return alignment;
        }

        public int offset() {
            return offset;
        }

        public int at(int base) {
            return base + offset;
        }
    }

    public static final class Builder {
        private int size = 0;
        private int alignment = 0;

        private Builder() {}

        public Field field(Layout layout) {
            return field(layout.size(), layout.alignment());
        }

        public Field field(int size, int alignment) {
            if (size <= 0 || alignment <= 0) {
                throw new IllegalArgumentException(
                        "both size and alignment must be greater than 0");
            }
            if (Integer.highestOneBit(alignment) != alignment) {
                throw new IllegalArgumentException("alignment must be a power of two");
            }
            if (size % alignment != 0) {
                throw new IllegalArgumentException("size must include padding");
            }
            // find the correct alignment for the new field
            int alignmentError = (alignment - (this.size % alignment)) % alignment;
            // add padding as needed
            this.size += alignmentError;
            // the field has the original size and alignment, but the correctly padded offset
            final Field field = new Field(size, alignment, this.size);
            // grow struct to encompass the new field
            this.size += field.size();
            // new alignment is whatever is bigger
            this.alignment = Math.max(this.alignment, alignment);
            return field;
        }

        public Field union(Layout... layout) {
            if (layout.length == 1) {
                return field(layout[0]);
            }
            throw new RuntimeException("unimplemented");
        }

        public Layout build() {
            // find how off we are from the *struct* alignment
            int alignmentError = (this.alignment - (this.size % this.alignment)) % this.alignment;
            // find the struct size with padding
            int paddedSize = this.size + alignmentError;
            return new Layout(paddedSize, this.alignment);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Layout clone() {
        try {
            return (Layout) super.clone();
        } catch (CloneNotSupportedException e) {
            // unreachable
            throw new RuntimeException(e);
        }
    }

    public int arrayElement(int base, int index) {
        return base + this.size * index;
    }

    public static final Layout INT = new Layout(4, 4);
    public static final Layout CHAR = new Layout(1, 1);
    public static final Layout SHORT = new Layout(2, 2);
    public static final Layout LONG = new Layout(8, 8);
    public static final Layout FLOAT = new Layout(4, 4);
    public static final Layout DOUBLE = new Layout(8, 8);
    public static final Layout PTR32 = INT.clone();
}
