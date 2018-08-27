package com.synopsys.integration.buildfileparser;

import java.util.Optional;

public enum BuildFileContext {
    GRADLE,
    MAVEN,
    NPM,
    RUBYGEMS;

    public static Optional<BuildFileContext> determineContextFromFilename(final String filename) {
        if ("build.gradle".equalsIgnoreCase(filename)) {
            return Optional.of(GRADLE);
        } else if ("pom.xml".equalsIgnoreCase(filename)) {
            return Optional.of(MAVEN);
        } else if ("package-lock.json".equalsIgnoreCase(filename)) {
            return Optional.of(NPM);
        } else if ("Gemfile.lock".equalsIgnoreCase(filename)) {
            return Optional.of(RUBYGEMS);
        }

        return null;
    }

}
