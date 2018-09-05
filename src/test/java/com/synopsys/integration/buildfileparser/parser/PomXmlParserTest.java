package com.synopsys.integration.buildfileparser.parser;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PomXmlParserTest {
    @Test
    public void testParsingPomFile() throws Exception {
        final InputStream pomInputStream = getClass().getResourceAsStream("/hub-teamcity-pom.xml");
        final PomXmlParser pomXmlParser = new PomXmlParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = pomXmlParser.parse(pomInputStream);
        assertTrue(dependencyGraph.getRootDependencies().size() > 0);
    }

}
