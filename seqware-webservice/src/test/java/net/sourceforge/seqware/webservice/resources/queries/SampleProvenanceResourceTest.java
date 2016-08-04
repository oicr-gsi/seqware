/*
 * Copyright (C) 2016 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.webservice.resources.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.lists.SampleProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Ignore;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;

/**
 *
 * @author mlaszloffy
 */
public class SampleProvenanceResourceTest extends DatabaseResourceTest {

    public SampleProvenanceResourceTest() {
        super("/reports/sample-provenance");
    }

    @Test
    public void testSomeMethod() {
    }

    @Ignore
    @Override
    public void testDelete() {
        // super.testDelete();
    }

    @Ignore
    @Override
    public void testPost() {
        // super.testPost();
    }

    @Ignore
    @Override
    public void testPut() {
        // super.testPut();
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");

        ExecutorService es = Executors.newFixedThreadPool(50);
        CompletionService<List<SampleProvenanceDto>> cs = new ExecutorCompletionService(es);
        List<Future<List<SampleProvenanceDto>>> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(cs.submit(new GetTask()));
        }

        while (tasks.size() > 0) {
            Future<List<SampleProvenanceDto>> completedTask = null;
            try {
                completedTask = cs.take();
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }

            try {
                if (completedTask == null) {
                    fail("Null completed task");
                } else {
                    tasks.remove(completedTask);
                    List<SampleProvenanceDto> dtos = completedTask.get();
                    assertNotNull(dtos);

                    Assert.assertEquals(22, dtos.size());
                }
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
        }

        es.shutdown();
    }

    private class GetTask implements Callable<List<SampleProvenanceDto>> {

        @Override
        public List<SampleProvenanceDto> call() throws Exception {
            SampleProvenanceDtoList list = null;
            try {
                Representation rep = ClientResourceInstance.getChild("/reports/sample-provenance").get();
                list = (SampleProvenanceDtoList) XmlTools.unMarshal(new JaxbObject<>(), new SampleProvenanceDtoList(), rep.getText());
                rep.exhaust();
                rep.release();
            } catch (ResourceException | IOException | SAXException e) {
                fail(e.getMessage());
            }
            if (list != null) {
                return list.getSampleProvenanceDtos();
            } else {
                return null;
            }
        }

    }

}
