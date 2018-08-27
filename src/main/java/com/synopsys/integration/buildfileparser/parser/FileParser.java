package com.synopsys.integration.buildfileparser.parser;

import java.io.InputStream;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

abstract class FileParser {
    final ExternalIdFactory externalIdFactory;

    public FileParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public abstract DependencyGraph parse(InputStream inputStream);

}
