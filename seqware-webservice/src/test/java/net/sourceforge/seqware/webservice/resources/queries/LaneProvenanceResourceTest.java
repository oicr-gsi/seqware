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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceResourceTest extends DatabaseResourceTest {

    public LaneProvenanceResourceTest() {
        super("/reports/lane-provenance");
    }

    @Ignore
    @Override
    public void testDelete() {
    }

    @Ignore
    @Override
    public void testPost() {
    }

    @Ignore
    @Override
    public void testPut() {
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");

        List<Future<GetResult>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CompletionService<GetResult> completionService = new ExecutorCompletionService(executorService);

        List<Callable> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            callables.add(new <LaneProvenanceDtoList>Get("/reports/lane-provenance", new LaneProvenanceDtoList()));
            callables.add(new Get("/reports/lane-provenance/refresh"));
            callables.add(new Get("/reports/lane-provenance/invalidate"));
        }

        Collections.shuffle(callables);
        for (Callable c : callables) {
            futures.add(completionService.submit(c));
        }

        while (futures.size() > 0) {

            Future<GetResult> completedTask = null;
            try {
                completedTask = completionService.take();
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }
            try {
                if (completedTask == null) {
                    fail("Null completed task");
                } else {
                    futures.remove(completedTask);
                    GetResult r = completedTask.get();

                    assertNotNull(r.getStatus());

                    assertNotNull(r.getRequestDate());
                    assertNotNull(r.getResponseDate());
                    assertTrue(r.getRequestDate().isEqual(r.getResponseDate())
                            || r.getRequestDate().isBefore(r.getResponseDate()));

                    Object data = r.getData();
                    if (data instanceof LaneProvenanceDtoList) {
                        List<LaneProvenanceDto> dtos = ((LaneProvenanceDtoList) data).getLaneProvenanceDtos();

                        assertNotNull(r.getDataLastModificationDate());
                        assertTrue(r.getResponseDate().isEqual(r.getDataLastModificationDate())
                                || r.getResponseDate().isAfter(r.getDataLastModificationDate()));

                        assertNotNull(dtos);
                        assertEquals(30, dtos.size());

                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
        }

        executorService.shutdown();
    }

}
