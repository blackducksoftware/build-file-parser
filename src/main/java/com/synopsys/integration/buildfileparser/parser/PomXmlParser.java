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
