package com.dylibso.chicory.maven.wast2jsonwasi;

import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiFilestat;
import com.dylibso.chicory.maven.wast2jsonwasi.layout.WasiFiletype;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fildes {
    // TODO this is hard

    public static Fildes null_source() {
        return new Fildes();
    }

    public static Fildes null_sink() {
        return new Fildes();
    }

    public abstract static class PreopenDir extends Fildes {
        private final String mountPoint;

        public PreopenDir(String mountPoint) {
            this.mountPoint = mountPoint;
        }

        public final String getMountPoint() {
            return mountPoint;
        }

        public abstract WasiFilestat.Values stat(String stringPath, WasiLookupOption... options)
                throws IOException;

        /**
         * Basic openat-based implementation of WASI preopen.
         * <p>
         * This implementation does not support symlinks properly, because Java does not support openat2 and RESOLVE_BENEATH. Expect symlinks to misbehave!
         * <p>
         * (Hey, at least this does avoid TOCTOU.)
         */
        public static class FilesystemBacked extends PreopenDir {
            private final SecureDirectoryStream<Path> baseDir;

            public FilesystemBacked(String mountPoint, SecureDirectoryStream<Path> baseDir) {
                super(mountPoint);
                this.baseDir = Objects.requireNonNull(baseDir, "baseDir");
            }

            @Override
            public WasiFilestat.Values stat(String stringPath, WasiLookupOption... options)
                    throws IOException {
                var values = new WasiFilestat.Values();
                // FIXME hmm these are probably wrong
                BasicFileAttributeView fileAttributeView;
                if (Arrays.stream(options).anyMatch(x -> x == WasiLookupOption.NOFOLLOW_LINKS)) {
                    fileAttributeView =
                            baseDir.getFileAttributeView(
                                    Path.of(stringPath),
                                    BasicFileAttributeView.class,
                                    LinkOption.NOFOLLOW_LINKS);
                } else {
                    fileAttributeView =
                            baseDir.getFileAttributeView(
                                    Path.of(stringPath), BasicFileAttributeView.class);
                }
                BasicFileAttributes basicFileAttributes = fileAttributeView.readAttributes();
                values.size = basicFileAttributes.size();
                if (basicFileAttributes.isDirectory()) {
                    values.filetype = WasiFiletype.DIRECTORY;
                } else if (basicFileAttributes.isRegularFile()) {
                    values.filetype = WasiFiletype.REGULAR_FILE;
                } else if (basicFileAttributes.isSymbolicLink()) {
                    values.filetype = WasiFiletype.SYMBOLIC_LINK;
                } else if (basicFileAttributes.isOther()) {
                    values.filetype = WasiFiletype.UNKNOWN;
                }
                values.nlink = 1;
                values.ino = 0;
                values.dev = 0;
                values.ctim = basicFileAttributes.creationTime().to(TimeUnit.NANOSECONDS);
                values.atim = basicFileAttributes.lastAccessTime().to(TimeUnit.NANOSECONDS);
                values.mtim = basicFileAttributes.lastModifiedTime().to(TimeUnit.NANOSECONDS);
                return values;
            }
        }
    }
}
