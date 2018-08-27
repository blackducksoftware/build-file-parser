package com.synopsys.integration.buildfileparser.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PomDependenciesHandler extends DefaultHandler {
    private final ExternalIdFactory externalIdFactory;

    private boolean parsingDependencies;
    private boolean parsingDependency;
    private boolean parsingGroup;
    private boolean parsingArtifact;
    private boolean parsingVersion;

    private String group;
    private String artifact;
    private String version;

    private final List<Dependency> dependencies = new ArrayList<>();

    public PomDependenciesHandler(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        System.out.println(qName);
        if ("dependencies".equals(qName)) {
            System.out.println("parsing deps");
            parsingDependencies = true;
        } else if ("dependency".equals(qName)) {
            parsingDependency = true;
        } else if ("groupId".equals(qName)) {
            parsingGroup();
        } else if ("artifactId".equals(qName)) {
            parsingArtifact();
        } else if ("version".equals(qName)) {
            parsingVersion();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("dependencies".equals(qName)) {
            System.out.println("DONE parsing deps");
            parsingDependencies = false;
        } else if ("dependency".equals(qName)) {
            parsingDependency = false;

            final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
            dependencies.add(new Dependency(artifact, version, externalId));
        } else {
            parsingNothingImportant();
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        if (parsingGroup) {
            group = new String(ch, start, length);
        } else if (parsingArtifact) {
            artifact = new String(ch, start, length);
        } else if (parsingVersion) {
            version = new String(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    private void parsingNothingImportant() {
        parsingGroup = false;
        parsingArtifact = false;
        parsingVersion = false;
    }

    private void parsingGroup() {
        parsingNothingImportant();
        parsingGroup = true;
    }

    private void parsingArtifact() {
        parsingNothingImportant();
        parsingArtifact = true;
    }

    private void parsingVersion() {
        parsingNothingImportant();
        parsingVersion = true;
    }
}
