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
package com.synopsys.integration.buildfileparser.parser.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.buildfileparser.BuildFileContext;
import com.synopsys.integration.buildfileparser.ParseResult;
import com.synopsys.integration.buildfileparser.parser.FileParser;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class BuildGradleParser extends FileParser {
    private final Logger logger = LoggerFactory.getLogger(BuildGradleParser.class);

    public BuildGradleParser(final ExternalIdFactory externalIdFactory) {
        super(externalIdFactory);
    }

    @Override
    public BuildFileContext getBuildFileContext() {
        return BuildFileContext.BUILD_GRADLE;
    }

    @Override
    public ParseResult parse(final InputStream inputStream) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        try {
            final String sourceContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final AstBuilder astBuilder = new AstBuilder();
            final List<ASTNode> nodes = astBuilder.buildFromString(sourceContents);

            final DependenciesVisitor dependenciesVisitor = new DependenciesVisitor(externalIdFactory);
            for (final ASTNode node : nodes) {
                node.visit(dependenciesVisitor);
            }

            final List<Dependency> dependencies = dependenciesVisitor.getDependencies();
            dependencyGraph.addChildrenToRoot(dependencies);

            return ParseResult.success(dependencyGraph);
        } catch (final IOException e) {
            logger.error("Could not get the build file contents: " + e.getMessage());
        }

        return ParseResult.failure();
    }

}
