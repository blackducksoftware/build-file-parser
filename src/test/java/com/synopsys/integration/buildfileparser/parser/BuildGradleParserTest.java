package com.synopsys.integration.buildfileparser.parser;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class BuildGradleParserTest {
    @Test
    public void testGettingGraphFromSimpleBuildGradle() {
        final InputStream buildGradleInputStream = getClass().getResourceAsStream("/simple_build.gradle.txt");

        final BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = buildGradleParser.parse(buildGradleInputStream);

        assertEquals(9, dependencyGraph.getRootDependencies().size());
    }

}
