package com.synopsys.integration.buildfileparser.parser;

import java.io.InputStream;

import org.junit.Test;

import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PomXmlParserTest {
    @Test
    public void testParsingPomFile() throws Exception {
        final InputStream pomInputStream = getClass().getResourceAsStream("/hub-teamcity-pom.xml");
        final PomXmlParser pomXmlParser = new PomXmlParser(new ExternalIdFactory());
        pomXmlParser.parse(pomInputStream);
    }

}
