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

import java.util.Optional;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.util.NameVersion;

public class ParseResult {
    private final boolean success;
    private final Optional<NameVersion> nameVersion;
    private final DependencyGraph dependencyGraph;

    public static ParseResult success(final NameVersion nameVersion, final DependencyGraph dependencyGraph) {
        return new ParseResult(true, Optional.ofNullable(nameVersion), dependencyGraph);
    }

    public static ParseResult success(final DependencyGraph dependencyGraph) {
        return new ParseResult(true, Optional.empty(), dependencyGraph);
    }

    public static ParseResult failure() {
        return new ParseResult(false, Optional.empty(), null);
    }

    public ParseResult(final boolean success, final Optional<NameVersion> nameVersion, final DependencyGraph dependencyGraph) {
        this.success = success;
        this.nameVersion = nameVersion;
        this.dependencyGraph = dependencyGraph;
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<NameVersion> getNameVersion() {
        return nameVersion;
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

}
