package com.synopsys.integration.buildfileparser;

import com.synopsys.integration.buildfileparser.exception.BuildFileContextNotFoundException;

public enum BuildFileContext {
    GRADLE,
    MAVEN,
    NPM,
    RUBYGEMS;

    public static BuildFileContext determineContextFromFilename(final String filename) throws BuildFileContextNotFoundException {
        if ("build.gradle".equalsIgnoreCase(filename)) {
            return GRADLE;
        } else if ("pom.xml".equalsIgnoreCase(filename)) {
            return MAVEN;
        } else if ("package-lock.json".equalsIgnoreCase(filename)) {
            return NPM;
        } else if ("Gemfile.lock".equalsIgnoreCase(filename)) {
            return RUBYGEMS;
        }

        throw new BuildFileContextNotFoundException(filename);
    }

}
