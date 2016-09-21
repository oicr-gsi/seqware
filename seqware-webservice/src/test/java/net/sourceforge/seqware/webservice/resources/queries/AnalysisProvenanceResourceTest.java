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
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Ignore;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResourceTest extends DatabaseResourceTest {

    public AnalysisProvenanceResourceTest() {
        super("/reports/analysis-provenance");
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

        List<Future<GetResult>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CompletionService<GetResult> completionService = new ExecutorCompletionService(executorService);

        List<Callable> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            callables.add(new <AnalysisProvenanceDtoList>Get("/reports/analysis-provenance", new AnalysisProvenanceDtoList()));
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
                    if (data instanceof AnalysisProvenanceDtoList) {
                        List<AnalysisProvenanceDto> dtos = ((AnalysisProvenanceDtoList) data).getAnalysisProvenanceDtos();

                        assertNotNull(r.getDataLastModificationDate());
                        assertTrue(r.getResponseDate().isEqual(r.getDataLastModificationDate())
                                || r.getResponseDate().isAfter(r.getDataLastModificationDate()));

                        assertNotNull(dtos);
                        //+ 20 IUS without workflow runs
                        //+ 3 files attached to workflow run
                        //+ 2 workflow runs without files
                        //= 25 expected records
                        Assert.assertEquals(25, dtos.size());

                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
        }

        executorService.shutdown();
    }

}
