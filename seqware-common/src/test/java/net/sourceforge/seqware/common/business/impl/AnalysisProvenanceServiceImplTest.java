/*
 * Copyright (C) 2015 SeqWare
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
package net.sourceforge.seqware.common.business.impl;

import com.google.common.collect.ImmutableSortedSet;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import net.sourceforge.seqware.common.AbstractTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.test.context.ContextConfiguration;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LimsKeyService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author mlaszloffy
 */
//@ContextConfiguration("classpath:test-data-source.xml")
public class AnalysisProvenanceServiceImplTest extends AbstractTestCase {

    @Autowired
    @Qualifier("analysisProvenanceService")
    AnalysisProvenanceService aprs;

    @Autowired
    @Qualifier("limsKeyService")
    LimsKeyService limsKeyService;

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    @Autowired
    @Qualifier("workflowService")
    WorkflowService workflowService;

    @Autowired
    @Qualifier("workflowRunService")
    WorkflowRunService workflowRunService;

    @Autowired
    @Qualifier("processingService")
    ProcessingService processingService;

    @Autowired
    @Qualifier("fileService")
    FileService fileService;

    @Before
    public void setup() {
        LimsKey limsKey = new LimsKey();
        limsKey.setProvider("seqware");
        limsKey.setId("1");
        limsKey.setVersion("2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf");
        limsKey.setLastModified(DateTime.parse("2016-01-01T00:00:00Z"));
        limsKey = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey));

        IUS ius = new IUS();
        ius.setLimsKey(limsKey);
        ius = iusService.findBySWAccession(iusService.insert(ius));

        Workflow workflow = new Workflow();
        workflow.setName("test_workflow");
        workflow = workflowService.findBySWAccession(workflowService.insert(workflow));

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(new TreeSet<IUS>());
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRun = workflowRunService.findBySWAccession(workflowRunService.insert(workflowRun));

        //hmm
        workflowRun.getIus().add(ius);
        workflowRunService.update(workflowRun);

        ius.getWorkflowRuns().add(workflowRun);
        iusService.update(ius);

        Processing processing = new Processing();
        processing.setAlgorithm("test_algorithm");
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processing = processingService.findBySWAccession(processingService.insert(processing));

        workflowRun.getProcessings().add(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath("/tmp/file.out");
        file = fileService.findBySWAccession(fileService.insert(file));

        processing.getFiles().add(file);
    }

    @Test
    public void singleRecordTest() throws InterruptedException, ExecutionException {
        assertEquals(24, aprs.list().size());
//        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aprs.list());
//        assertEquals("test_workflow", ap.getWorkflowName());
//        assertEquals("test_algorithm", ap.getProcessingAlgorithm());
//        assertEquals("/tmp/file.out", ap.getFilePath());
//
//        ca.on.oicr.gsi.provenance.api.model.LimsKey lk = Iterables.getOnlyElement(ap.getIusLimsKeys()).getLimsKey();
//        assertEquals("seqware", lk.getProvider());
//        assertEquals("1", lk.getId());
//        assertEquals("2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf", lk.getVersion());
//        assertEquals(DateTime.parse("2016-01-01T00:00:00Z"), lk.getLastModified());
    }

}
