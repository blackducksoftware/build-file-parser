package com.synopsys.integration.buildfileparser.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.synopsys.integration.buildfileparser.ParseResult;
import com.synopsys.integration.buildfileparser.parser.npm.PackageLockJsonParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.summary.DependencyGraphSummarizer;
import com.synopsys.integration.hub.bdio.graph.summary.DependencyGraphSummaryComparer;
import com.synopsys.integration.hub.bdio.graph.summary.GraphSummary;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

public class PackageLockJsonParserTest {
    private final DependencyGraphSummarizer dependencyGraphSummarizer = new DependencyGraphSummarizer(new Gson());
    private final DependencyGraphSummaryComparer dependencyGraphComparer = new DependencyGraphSummaryComparer(dependencyGraphSummarizer);

    @Test
    public void testParsingPackageLock() throws Exception {
        final String json = IOUtils.toString(getClass().getResourceAsStream("/packageLockExpected_graph.json"), StandardCharsets.UTF_8);
        final GraphSummary expected = dependencyGraphSummarizer.fromJson(json);

        final PackageLockJsonParser packageLockJsonParser = new PackageLockJsonParser(new ExternalIdFactory(), new Gson(), true);

        final ParseResult parseResult = packageLockJsonParser.parse(getClass().getResourceAsStream("/package-lock.json"));
        final NameVersion nameVersion = parseResult.getNameVersion().get();
        final DependencyGraph dependencyGraph = parseResult.getDependencyGraph();
        final GraphSummary actual = dependencyGraphSummarizer.fromGraph(dependencyGraph);

        assertEquals(nameVersion.getName(), "knockout-tournament");
        assertEquals(nameVersion.getVersion(), "1.0.0");
        assertTrue(dependencyGraphComparer.areEqual(expected, actual));
    }

}
