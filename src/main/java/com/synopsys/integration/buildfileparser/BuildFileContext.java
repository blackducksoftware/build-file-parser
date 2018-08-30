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
