package com.synopsys.integration.buildfileparser.parser;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.summary.DependencyGraphComparer;
import com.synopsys.integration.hub.bdio.graph.summary.DependencyGraphSummarizer;
import com.synopsys.integration.hub.bdio.graph.summary.GraphSummary;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class GemfileLockParserTest {
    private final DependencyGraphSummarizer dependencyGraphSummarizer = new DependencyGraphSummarizer(new Gson());
    private final DependencyGraphComparer dependencyGraphComparer = new DependencyGraphComparer(dependencyGraphSummarizer);

    @Test
    public void testParsingSmallGemfileLock() throws Exception {
        final String json = IOUtils.toString(getClass().getResourceAsStream("/expectedSmallParser_graph.json"), StandardCharsets.UTF_8);
        final GraphSummary expected = dependencyGraphSummarizer.fromJson(json);

        final GemfileLockParser gemfileLockParser = new GemfileLockParser(new ExternalIdFactory());

        final DependencyGraph dependencyGraph = gemfileLockParser.parse(getClass().getResourceAsStream("/small_gemfile_lock"));
        final GraphSummary actual = dependencyGraphSummarizer.fromGraph(dependencyGraph);

        assertTrue(dependencyGraphComparer.areEqual(expected, actual));
    }

    @Test
    public void testParsingGemfileLock() throws Exception {
        final String json = IOUtils.toString(getClass().getResourceAsStream("/expectedParser_graph.json"), StandardCharsets.UTF_8);
        final GraphSummary expected = dependencyGraphSummarizer.fromJson(json);

        final GemfileLockParser gemfileLockParser = new GemfileLockParser(new ExternalIdFactory());

        final DependencyGraph dependencyGraph = gemfileLockParser.parse(getClass().getResourceAsStream("/Gemfile.lock"));
        final GraphSummary actual = dependencyGraphSummarizer.fromGraph(dependencyGraph);

        assertTrue(dependencyGraphComparer.areEqual(expected, actual));
        assertEquals(8, dependencyGraph.getRootDependencies().size());

        final String json2 = IOUtils.toString(getClass().getResourceAsStream("/expectedPackager_graph.json"), StandardCharsets.UTF_8);
        final GraphSummary expected2 = dependencyGraphSummarizer.fromJson(json2);
        assertTrue(dependencyGraphComparer.areEqual(expected2, actual));
    }

}
