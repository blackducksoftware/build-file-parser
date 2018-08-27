package com.synopsys.integration.buildfileparser.exception;

public class BuildFileContextNotFoundException extends Exception {
    public BuildFileContextNotFoundException(final String filename) {
        super(String.format("A BuildFileContext could not be determined from: %s", filename));
    }

}
