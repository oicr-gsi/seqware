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

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
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
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import org.joda.time.DateTime;
import static org.junit.Assert.*;

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

    @Test
    public void getAllRecords() throws InterruptedException, ExecutionException {
        //20 IUS without LimsKey + 3 files attached to workflow run + 1 workflow run without files = 24 expected records
        assertEquals(24, aprs.list().size());
    }

    @Test
    public void singleRecordTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        DateTime expectedLastModified = DateTime.parse("2016-01-01T00:00:00Z");

        LimsKey limsKey = new LimsKey();
        limsKey.setProvider(expectedProvider);
        limsKey.setId(expectedId);
        limsKey.setVersion(expectedVersion);
        limsKey.setLastModified(expectedLastModified);
        limsKey = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey));

        IUS ius = new IUS();
        ius.setLimsKey(limsKey);
        ius = iusService.findBySWAccession(iusService.insert(ius));

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflow = workflowService.findBySWAccession(workflowService.insert(workflow));

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(new TreeSet<IUS>());
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRun = workflowRunService.findBySWAccession(workflowRunService.insert(workflowRun));

        //link workflow run and ius
        workflowRun.getIus().add(ius);
        workflowRunService.update(workflowRun);
        ius.getWorkflowRuns().add(workflowRun);
        iusService.update(ius);

        Processing processing = new Processing();
        processing.setAlgorithm(expectedProcessingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processing = processingService.findBySWAccession(processingService.insert(processing));
        workflowRun.getProcessings().add(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(expectedFilePath);
        file = fileService.findBySWAccession(fileService.insert(file));
        processing.getFiles().add(file);

        assertEquals(1, aprs.findForIus(ius).size());
        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aprs.findForIus(ius));
        assertEquals(expectedWorkflowName, ap.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap.getFilePath());

        assertEquals(1, ap.getIusLimsKeys().size());
        IusLimsKey ilk = Iterables.getOnlyElement(ap.getIusLimsKeys());
        assertEquals(ius.getSwAccession(), ilk.getIusSWID());

        ca.on.oicr.gsi.provenance.model.LimsKey lk = ilk.getLimsKey();
        assertEquals(expectedId, lk.getId());
        assertEquals(expectedLastModified, lk.getLastModified());
        assertEquals(expectedProvider, lk.getProvider());
        assertEquals(expectedVersion, lk.getVersion());
    }

    @Test
    public void multipleInputIusTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        DateTime expectedLastModified = DateTime.parse("2016-01-01T00:00:00Z");

        //first IusLimsKey
        LimsKey limsKey1 = new LimsKey();
        limsKey1.setProvider(expectedProvider);
        limsKey1.setId(expectedId);
        limsKey1.setVersion(expectedVersion);
        limsKey1.setLastModified(expectedLastModified);
        limsKey1 = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey1));
        IUS ius1 = new IUS();
        ius1.setLimsKey(limsKey1);
        ius1 = iusService.findBySWAccession(iusService.insert(ius1));

        //second IusLimsKey
        LimsKey limsKey2 = new LimsKey();
        limsKey2.setProvider(expectedProvider);
        limsKey2.setId(expectedId);
        limsKey2.setVersion(expectedVersion);
        limsKey2.setLastModified(expectedLastModified);
        limsKey2 = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey2));
        IUS ius2 = new IUS();
        ius2.setLimsKey(limsKey2);
        ius2 = iusService.findBySWAccession(iusService.insert(ius2));

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflow = workflowService.findBySWAccession(workflowService.insert(workflow));

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(new TreeSet<IUS>());
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRun = workflowRunService.findBySWAccession(workflowRunService.insert(workflowRun));

        //link workflow run and ius
        workflowRun.getIus().add(ius1);
        workflowRun.getIus().add(ius2);
        workflowRunService.update(workflowRun);
        ius1.getWorkflowRuns().add(workflowRun);
        iusService.update(ius1);
        ius2.getWorkflowRuns().add(workflowRun);
        iusService.update(ius2);

        Processing processing = new Processing();
        processing.setAlgorithm(expectedProcessingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processing = processingService.findBySWAccession(processingService.insert(processing));
        workflowRun.getProcessings().add(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(expectedFilePath);
        file = fileService.findBySWAccession(fileService.insert(file));
        processing.getFiles().add(file);

        assertEquals(1, aprs.findForIus(ius1).size());
        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aprs.findForIus(ius2));
        assertEquals(expectedWorkflowName, ap.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap.getFilePath());

        assertEquals(2, ap.getIusLimsKeys().size());
        for (IusLimsKey ilk : ap.getIusLimsKeys()) {
            ca.on.oicr.gsi.provenance.model.LimsKey lk = ilk.getLimsKey();
            assertEquals(expectedId, lk.getId());
            assertEquals(expectedLastModified, lk.getLastModified());
            assertEquals(expectedProvider, lk.getProvider());
            assertEquals(expectedVersion, lk.getVersion());
        }
    }

    @Test
    public void iusLinkedToProcessingTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        DateTime expectedLastModified = DateTime.parse("2016-01-01T00:00:00Z");

        //first IusLimsKey
        LimsKey limsKey1 = new LimsKey();
        limsKey1.setProvider(expectedProvider);
        limsKey1.setId(expectedId);
        limsKey1.setVersion(expectedVersion);
        limsKey1.setLastModified(expectedLastModified);
        limsKey1 = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey1));
        IUS ius1 = new IUS();
        ius1.setLimsKey(limsKey1);
        ius1 = iusService.findBySWAccession(iusService.insert(ius1));

        //second IusLimsKey
        LimsKey limsKey2 = new LimsKey();
        limsKey2.setProvider(expectedProvider);
        limsKey2.setId(expectedId);
        limsKey2.setVersion(expectedVersion);
        limsKey2.setLastModified(expectedLastModified);
        limsKey2 = limsKeyService.findBySWAccession(limsKeyService.insert(limsKey2));
        IUS ius2 = new IUS();
        ius2.setLimsKey(limsKey2);
        ius2 = iusService.findBySWAccession(iusService.insert(ius2));

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflow = workflowService.findBySWAccession(workflowService.insert(workflow));

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(new TreeSet<IUS>());
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRun = workflowRunService.findBySWAccession(workflowRunService.insert(workflowRun));

        //link workflow run and ius
        workflowRun.getIus().add(ius1);
        workflowRun.getIus().add(ius2);
        workflowRunService.update(workflowRun);
        ius1.getWorkflowRuns().add(workflowRun);
        iusService.update(ius1);
        ius2.getWorkflowRuns().add(workflowRun);
        iusService.update(ius2);

        //first processing+file - linked only to IUS1
        Processing processing1 = new Processing();
        processing1.setAlgorithm(expectedProcessingAlgorithm);
        processing1.setWorkflowRun(workflowRun);
        processing1.setFiles(new HashSet<File>());
        processing1 = processingService.findBySWAccession(processingService.insert(processing1));
        workflowRun.getProcessings().add(processing1);

        File file1 = new File();
        file1.setProcessings(ImmutableSortedSet.of(processing1));
        file1.setFilePath(expectedFilePath);
        file1 = fileService.findBySWAccession(fileService.insert(file1));
        processing1.getFiles().add(file1);

        processing1.getIUS().add(ius1);
        processingService.update(processing1);
        ius1.getProcessings().add(processing1);
        iusService.update(ius1);

        //second processing+file - linked only to IUS2
        Processing processing2 = new Processing();
        processing2.setAlgorithm(expectedProcessingAlgorithm);
        processing2.setWorkflowRun(workflowRun);
        processing2.setFiles(new HashSet<File>());
        processing2 = processingService.findBySWAccession(processingService.insert(processing2));
        workflowRun.getProcessings().add(processing2);

        File file2 = new File();
        file2.setProcessings(ImmutableSortedSet.of(processing2));
        file2.setFilePath(expectedFilePath);
        file2 = fileService.findBySWAccession(fileService.insert(file2));
        processing2.getFiles().add(file2);

        processing2.getIUS().add(ius2);
        processingService.update(processing2);
        ius2.getProcessings().add(processing2);
        iusService.update(ius2);

        assertEquals(1, aprs.findForIus(ius1).size());
        AnalysisProvenanceDto ap1 = Iterables.getOnlyElement(aprs.findForIus(ius1));
        assertEquals(expectedWorkflowName, ap1.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap1.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap1.getFilePath());

        assertEquals(1, ap1.getIusLimsKeys().size());
        IusLimsKey ilk1 = Iterables.getOnlyElement(ap1.getIusLimsKeys());
        assertEquals(ius1.getSwAccession(), ilk1.getIusSWID());

        ca.on.oicr.gsi.provenance.model.LimsKey lk1 = ilk1.getLimsKey();
        assertEquals(expectedId, lk1.getId());
        assertEquals(expectedLastModified, lk1.getLastModified());
        assertEquals(expectedProvider, lk1.getProvider());
        assertEquals(expectedVersion, lk1.getVersion());

        assertEquals(1, aprs.findForIus(ius2).size());
        AnalysisProvenanceDto ap2 = Iterables.getOnlyElement(aprs.findForIus(ius2));
        assertEquals(expectedWorkflowName, ap2.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap2.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap2.getFilePath());

        assertEquals(1, ap2.getIusLimsKeys().size());
        IusLimsKey ilk2 = Iterables.getOnlyElement(ap2.getIusLimsKeys());
        assertEquals(ius2.getSwAccession(), ilk2.getIusSWID());

        ca.on.oicr.gsi.provenance.model.LimsKey lk2 = ilk2.getLimsKey();
        assertEquals(expectedId, lk2.getId());
        assertEquals(expectedLastModified, lk2.getLastModified());
        assertEquals(expectedProvider, lk2.getProvider());
        assertEquals(expectedVersion, lk2.getVersion());
    }
}
