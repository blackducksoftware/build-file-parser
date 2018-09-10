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
package com.synopsys.integration.buildfileparser.parser.npm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.buildfileparser.BuildFileContext;
import com.synopsys.integration.buildfileparser.ParseResult;
import com.synopsys.integration.buildfileparser.parser.FileParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.hub.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

public class PackageLockJsonParser extends FileParser {
    private final Logger logger = LoggerFactory.getLogger(PackageLockJsonParser.class);

    private final Gson gson;
    private final boolean includeDevDependencies;

    public PackageLockJsonParser(final ExternalIdFactory externalIdFactory, final Gson gson, final boolean includeDevDependencies) {
        super(externalIdFactory);
        this.gson = gson;
        this.includeDevDependencies = includeDevDependencies;
    }

    @Override
    public BuildFileContext getBuildFileContext() {
        return BuildFileContext.PACKAGE_LOCK_JSON;
    }

    @Override
    public ParseResult parse(final InputStream inputStream) {
        final LazyExternalIdDependencyGraphBuilder lazyBuilder = new LazyExternalIdDependencyGraphBuilder();

        try {
            final String contents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final NpmProject npmProject = gson.fromJson(contents, NpmProject.class);
            logger.info("Processing project.");
            if (npmProject.dependencies != null) {
                logger.info(String.format("Found %d dependencies.", npmProject.dependencies.size()));
                npmProject.dependencies.forEach((name, npmDependency) -> {
                    if (shouldInclude(npmDependency, includeDevDependencies)) {
                        final DependencyId dependency = createDependencyId(name, npmDependency.version);
                        setDependencyInfo(dependency, name, npmDependency.version, lazyBuilder);
                        lazyBuilder.addChildToRoot(dependency);
                        if (npmDependency.requires != null) {
                            npmDependency.requires.forEach((childName, childVersion) -> {
                                final DependencyId childId = createDependencyId(childName, childVersion);
                                setDependencyInfo(childId, childName, childVersion, lazyBuilder);
                                lazyBuilder.addChildWithParent(childId, dependency);
                            });
                        }
                    }
                });
            } else {
                logger.info("Lock file did not have a 'dependencies' section.");
            }
            logger.info("Finished processing.");

            final NameVersion nameVersion = new NameVersion(npmProject.name, npmProject.version);
            final DependencyGraph dependencyGraph = lazyBuilder.build();
            return ParseResult.success(nameVersion, dependencyGraph);
        } catch (final IOException e) {
            logger.error("Could not get the gemfile contents: " + e.getMessage());
        }

        return ParseResult.failure();
    }

    private boolean shouldInclude(final NpmDependency npmDependency, final boolean includeDevDependencies) {
        boolean isDev = false;
        if (npmDependency.dev != null && npmDependency.dev == true) {
            isDev = true;
        }
        if (isDev) {
            return includeDevDependencies;
        }
        return true;
    }

    private DependencyId createDependencyId(final String name, final String version) {
        if (StringUtils.isNotBlank(version)) {
            return new NameVersionDependencyId(name, version);
        } else {
            return new NameDependencyId(name);
        }
    }

    private void setDependencyInfo(final DependencyId dependencyId, final String name, final String version, final LazyExternalIdDependencyGraphBuilder lazyBuilder) {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
        lazyBuilder.setDependencyInfo(dependencyId, name, version, externalId);
    }

}
