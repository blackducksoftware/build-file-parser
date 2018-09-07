package com.synopsys.integration.buildfileparser;

import org.junit.Test;

import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class BuildFileParserTest {
    @Test
    public void testBuildFileParser() throws Exception {
        final BuildFileParser buildFileParser = new BuildFileParser(new ExternalIdFactory(), new Gson());
    }

}
