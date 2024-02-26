package com.dylibso.chicory.maven.wast2jsonwasi;

// FIXME this should really be RuntimeException...
// wow this is really horrible
public class ExitStatus extends Error {
    private final int exitCode;

    public ExitStatus(int exitCode) {
        super(null, null, false, false);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
