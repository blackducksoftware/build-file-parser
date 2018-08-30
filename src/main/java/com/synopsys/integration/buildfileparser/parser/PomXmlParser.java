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
package com.synopsys.integration.buildfileparser.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PomXmlParser extends FileParser {
    private final SAXParser saxParser;
    private final PomDependenciesHandler pomDependenciesHandler;

    public PomXmlParser(final ExternalIdFactory externalIdFactory) throws ParserConfigurationException, SAXException {
        super(externalIdFactory);
        saxParser = SAXParserFactory.newInstance().newSAXParser();
        pomDependenciesHandler = new PomDependenciesHandler(externalIdFactory);
    }

    @Override
    public DependencyGraph parse(final InputStream inputStream) {
        try {
            saxParser.parse(inputStream, pomDependenciesHandler);
            final List<Dependency> dependencies = pomDependenciesHandler.getDependencies();
            for (final Dependency dependency : dependencies) {
                System.out.println(dependency.toString());
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

}
