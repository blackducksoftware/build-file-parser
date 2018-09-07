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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.synopsys.integration.buildfileparser.exception.BuildFileContextNotFoundException;
import com.synopsys.integration.buildfileparser.exception.PomXmlParserInstantiationException;
import com.synopsys.integration.buildfileparser.parser.FileParser;
import com.synopsys.integration.buildfileparser.parser.gradle.BuildGradleParser;
import com.synopsys.integration.buildfileparser.parser.maven.PomXmlParser;
import com.synopsys.integration.buildfileparser.parser.npm.PackageLockJsonParser;
import com.synopsys.integration.buildfileparser.parser.rubygems.GemfileLockParser;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class BuildFileParser {
    private final Map<BuildFileContext, FileParser> fileParsers = new HashMap<>();

    public static BuildFileParser createDefault() throws PomXmlParserInstantiationException {
        return new BuildFileParser(new ExternalIdFactory(), new Gson(), true);
    }

    public BuildFileParser(final ExternalIdFactory externalIdFactory, final Gson gson, final boolean includePackageLockJsonDevDependencies) throws PomXmlParserInstantiationException {
        final FileParser buildGradleParser = new BuildGradleParser(externalIdFactory);
        final FileParser pomXmlParser = new PomXmlParser(externalIdFactory);
        final FileParser packageLockJsonParser = new PackageLockJsonParser(externalIdFactory, gson, includePackageLockJsonDevDependencies);
        final FileParser gemfileLockParser = new GemfileLockParser(externalIdFactory);

        fileParsers.put(BuildFileContext.GRADLE, buildGradleParser);
        fileParsers.put(BuildFileContext.MAVEN, pomXmlParser);
        fileParsers.put(BuildFileContext.NPM, packageLockJsonParser);
        fileParsers.put(BuildFileContext.RUBYGEMS, gemfileLockParser);
    }

    public ParseResult parseInputStream(final InputStream inputStream, final BuildFileContext buildFileContext) {
        final FileParser fileParser = fileParsers.get(buildFileContext);
        return fileParser.parse(inputStream);
    }

    public ParseResult parseFile(final File file) throws BuildFileContextNotFoundException, FileNotFoundException {
        final InputStream inputStream = new FileInputStream(file);
        final BuildFileContext buildFileContext = BuildFileContext.determineContextFromFilename(file.getName());

        return parseInputStream(inputStream, buildFileContext);
    }

}
