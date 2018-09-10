/**
 * build-file-parser
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.buildfileparser;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.buildfileparser.exception.BuildFileContextNotFoundException;

public enum BuildFileContext {
    BUILD_GRADLE("build.gradle"),
    GEMFILE_LOCK("Gemfile.lock"),
    PACKAGE_LOCK_JSON("package-lock.json"),
    POM_XML("pom.xml");

    private static final Map<String, BuildFileContext> FILENAMES_TO_CONTEXTS = new HashMap<>();

    private final String filename;

    BuildFileContext(final String filename) {
        this.filename = filename;
    }

    static {
        EnumSet.allOf(BuildFileContext.class)
                .stream()
                .forEach(buildFileContext -> {
                    FILENAMES_TO_CONTEXTS.put(buildFileContext.filename, buildFileContext);
                });
    }

    public static BuildFileContext determineContextFromFilename(final String filename) throws BuildFileContextNotFoundException {
        if (!FILENAMES_TO_CONTEXTS.containsKey(filename)) {
            throw new BuildFileContextNotFoundException(filename);
        }

        return FILENAMES_TO_CONTEXTS.get(filename);
    }

    public static Set<String> getSupportedFilenames() {
        return FILENAMES_TO_CONTEXTS.keySet();
    }

}
