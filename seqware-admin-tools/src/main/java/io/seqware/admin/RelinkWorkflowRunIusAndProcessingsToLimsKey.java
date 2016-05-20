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
package io.seqware.admin;

import ca.on.oicr.gsi.provenance.model.SampleProvenance;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import net.sourceforge.seqware.common.business.SampleProvenanceService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.LimsKey;
import org.hibernate.Criteria;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import java.io.IOException;
import java.util.ArrayList;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import javax.naming.NamingException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import org.hibernate.SessionFactory;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import static com.google.common.base.Preconditions.checkNotNull;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This tool iterates over all WorkflowRuns that are linked to IUS and relinks the WorkflowRuns to a new IUS that is linked to a LimsKey.
 *
 * The tool does the following:
 * - get all SampleProvenance from SeqWare
 * - get all WorkflowRuns and the IUSes that are linked to the WorkflowRun or to the Processings
 * - verify that the IUSes have SampleProvenance
 * - verify that either all or none of the IUSes in the Processing-IUS link set are in the WorkflowRun-IUS link set
 * - relink the WorkflowRun by:
 * 1) create a new IUS for each existing IUS
 * 2) create a new LimsKey with the information from the existing IUS's SampleProvenance record
 * 3) link the WorkflowRun to the new IUS
 * 4) if necessary, link the Processing to the new IUS
 *
 * @author mlaszloffy
 */
@Component
public class RelinkWorkflowRunIusAndProcessingsToLimsKey {

    //All new LimsKeys and IUS createTstmp and updateTstmp will be set to this
    private Date timestamp;

    @Autowired
    @Qualifier("sessionFactory")
    SessionFactory sessionFactory;

    @Autowired
    SampleProvenanceService sampleProvenanceService;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Summary run() throws IOException {

        StatelessSession session = sessionFactory.openStatelessSession();

        //preload all required data for relinking
        Stopwatch timerPreload = Stopwatch.createStarted();

        System.out.println("Preloading started");
        Transaction preloadTx = session.beginTransaction();
        Map<String, SampleProvenance> sps = new HashMap<>();
        for (SampleProvenance sp : sampleProvenanceService.list()) {
            if (sps.put(sp.getSampleProvenanceId(), sp) != null) {
                throw new RuntimeException("Duplicate sample provenance, id = [" + sp.getSampleProvenanceId() + "]");
            }
        }

        List workflowRunIusRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("workflow_run_iuses.sql"), "UTF-8"))
                .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                .list();
        Map<Integer, RelinkingTask> relinkingTasksByWorkflowRunId = new HashMap<>();
        for (Object workflowRunIusRecord : workflowRunIusRecordList) {
            Map workflowRunIusRecordAsMap = (Map) workflowRunIusRecord;
            Integer workflowRunId = (Integer) workflowRunIusRecordAsMap.get("workflow_run_id");
            checkNotNull(workflowRunId);

            RelinkingTask re;
            if (relinkingTasksByWorkflowRunId.containsKey(workflowRunId)) {
                re = relinkingTasksByWorkflowRunId.get(workflowRunId);
            } else {
                re = new RelinkingTask();
                re.setWorkflowRunId(workflowRunId);
                relinkingTasksByWorkflowRunId.put(workflowRunId, re);
            }

            Integer iusId = (Integer) workflowRunIusRecordAsMap.get("ius_id");
            Integer iusSwid = (Integer) workflowRunIusRecordAsMap.get("ius_swid");
            Integer sampleProvenanceId = workflowRunIusRecordAsMap.get("sample_provenance_id") == null ? null : Integer.parseInt((String) workflowRunIusRecordAsMap.get("sample_provenance_id"));

            checkNotNull(iusId);
            checkNotNull(iusSwid);

            WorkflowRunIus wre = new WorkflowRunIus();
            wre.setWorkflowRunId(workflowRunId);
            wre.setIusId(iusId);
            wre.setIusSwid(iusSwid);
            wre.setSampleProvenanceId(sampleProvenanceId);

            re.addWorkflowRunIus(wre);
        }

        List workflowRunProcessingRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("workflow_run_processings.sql"), "UTF-8"))
                .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                .list();
        for (Object workflowRunProcessingRecord : workflowRunProcessingRecordList) {
            Map workflowRunProcessingRecordAsMap = (Map) workflowRunProcessingRecord;
            Integer workflowRunId = (Integer) workflowRunProcessingRecordAsMap.get("workflow_run_id");
            checkNotNull(workflowRunId);

            RelinkingTask relinkingTask = relinkingTasksByWorkflowRunId.get(workflowRunId);
            checkNotNull(relinkingTask);

            Integer iusId = (Integer) workflowRunProcessingRecordAsMap.get("ius_id");
            Integer iusSwid = (Integer) workflowRunProcessingRecordAsMap.get("ius_swid");
            Integer processingId = (Integer) workflowRunProcessingRecordAsMap.get("processing_id");
            checkNotNull(iusId);
            checkNotNull(iusSwid);
            checkNotNull(processingId);

            WorkflowRunProcessing wrp = new WorkflowRunProcessing();
            wrp.setWorkflowRunId(workflowRunId);
            wrp.setIusId(iusId);
            wrp.setIusSwid(iusSwid);
            wrp.setProcessingId(processingId);

            relinkingTask.addWorkflowRunProcessing(wrp);
        }
        preloadTx.commit();
        System.out.println("Preloading completed in " + timerPreload.stop());

        System.out.println("Relinking started");
        Stopwatch timerRelinking = Stopwatch.createStarted();
        Stopwatch timerBatch = Stopwatch.createStarted();
        int workflowRunsAlreadyLinked = 0;
        int workflowRunsRelinked = 0;
        int iter = 0;
        int totalSize = relinkingTasksByWorkflowRunId.values().size();
        List<Integer> workflowRunIdsThatWereNotRelinked = new ArrayList<>();
        for (RelinkingTask relinkingTask : relinkingTasksByWorkflowRunId.values()) {
            //progress reporting
            if (++iter % 5000 == 0) {
                long batchDuration = timerBatch.stop().elapsed(TimeUnit.MILLISECONDS) + 1;
                System.out.println("Relinking progress: " + iter + "/" + totalSize + " (" + 1000 * 5000 / batchDuration + "recs/sec)");
                timerBatch = Stopwatch.createStarted();
            }

            //check if workflow run is a candidate for relinking
            //ie check for records that have not already been relinked or do not need to be relinked (they were created with IUS-LimsKey links)
            Set<Integer> iusSwids = new HashSet<>();
            Set<Integer> sampleProvenanceIds = new HashSet<>();
            for (WorkflowRunIus wri : relinkingTask.getWorkflowRunIuses()) {
                if (wri.getSampleProvenanceId() != null) {
                    sampleProvenanceIds.add(wri.getSampleProvenanceId());
                } else {
                    iusSwids.add(wri.getIusSwid());
                }
            }
            if (iusSwids.equals(sampleProvenanceIds)) {
                Log.debug("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] has already been relinked");
                workflowRunsAlreadyLinked++;
                continue;
            }
            if (iusSwids.isEmpty() && !sampleProvenanceIds.isEmpty()) {
                Log.debug("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] was created with links to IUS-LimsKey");
                workflowRunsAlreadyLinked++;
                continue;
            }

            //workflow run is a candidate for relinking - the following checks verify if the following required assumptions hold:
            // 1) all IUS associated with workflow run have an associated SampleProvenance
            // 2) all Processing-IUS links are a subset of WorkflowRun-IUS links
            boolean okayToRelinkWorkflowRun = true;

            // 1) all IUS associated with workflow run have an associated SampleProvenance
            for (Integer iusSwid : relinkingTask.relatedIusSwids) {
                if (sps.get(iusSwid.toString()) == null) {
                    okayToRelinkWorkflowRun = false;
                    Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Not able to map IUS to SP");
                }
            }

            // 2) all Processing-IUS links are a subset of WorkflowRun-IUS links
            Set<Integer> workflowRunProcessingIusSwids = new HashSet<>();
            for (WorkflowRunProcessing wrp : relinkingTask.getWorkflowRunProcessings()) {
                workflowRunProcessingIusSwids.add(wrp.getIusSwid());
            }
            Set<Integer> workflowRunIusIusSwids = new HashSet<>();
            for (WorkflowRunIus wri : relinkingTask.getWorkflowRunIuses()) {
                workflowRunIusIusSwids.add(wri.getIusSwid());
            }
            if (!workflowRunIusIusSwids.containsAll(workflowRunProcessingIusSwids)) {
                okayToRelinkWorkflowRun = false;
                Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Processing-IUS links set contains records that are not in WorkflowRun-IUS links set");
            }

            //add workflow run id to exception set and do not relink workflow run if assumptions do not hold
            if (!okayToRelinkWorkflowRun) {
                workflowRunIdsThatWereNotRelinked.add(relinkingTask.workflowRunId);
                continue;
            }

            //relink all Processing-IUS and WorkflowRun-IUS in a transaction
            Transaction tx = session.beginTransaction();
            for (Integer iusSwid : relinkingTask.getRelatedIusSwids()) {
                SampleProvenance sp = sps.get(iusSwid.toString());
                checkNotNull(sp);

                LimsKey lk = new LimsKey();
                lk.setLastModified(sp.getLastModified());
                lk.setVersion(sp.getVersion());
                lk.setProvider("seqware");
                lk.setId(iusSwid.toString());
                lk.setCreateTimestamp(timestamp);
                lk.setUpdateTimestamp(timestamp);

                IUS ius = new IUS();
                ius.setLimsKey(lk);
                ius.setCreateTimestamp(timestamp);
                ius.setUpdateTimestamp(timestamp);

                session.insert(lk);
                session.insert(ius);

                Set<WorkflowRunIus> workflowRunIuses = relinkingTask.getWorkflowRunIuses(iusSwid);
                if (workflowRunIuses.isEmpty()) {
                    System.out.println("EMPTY INPUT IUSES");
                }
                for (WorkflowRunIus wri : workflowRunIuses) {
                    session.createSQLQuery("insert into ius_workflow_runs(ius_id,workflow_run_id) values (:iusId,:workflowRunId)")
                            .setParameter("iusId", ius.getIusId())
                            .setParameter("workflowRunId", wri.getWorkflowRunId())
                            .executeUpdate();
                }

                Set<WorkflowRunProcessing> workflowRunProcessings = relinkingTask.getWorkflowRunProcessings(iusSwid);
                for (WorkflowRunProcessing wrp : workflowRunProcessings) {
                    session.createSQLQuery("insert into processing_ius(ius_id,processing_id) values (:iusId,:processingId)")
                            .setParameter("iusId", ius.getIusId())
                            .setParameter("processingId", wrp.getProcessingId())
                            .executeUpdate();
                }
            }
            tx.commit();
            workflowRunsRelinked++;
        }
        System.out.println("Relinking completed in " + timerRelinking.stop());

        session.close();

        if (!workflowRunIdsThatWereNotRelinked.isEmpty()) {
            Log.warn(workflowRunIdsThatWereNotRelinked.size() + " workflow runs were not relinked:\n" + Joiner.on(",").join(workflowRunIdsThatWereNotRelinked));
        }

        Summary summary = new Summary();
        summary.setWorkflowRunsAnalyzed(relinkingTasksByWorkflowRunId.size());
        summary.setWorkflowRunsErrorState(workflowRunIdsThatWereNotRelinked.size());
        summary.setWorkflowRunsRelinked(workflowRunsRelinked);
        summary.setWorkflowRunsAlreadyLinked(workflowRunsAlreadyLinked);
        return summary;
    }

    private class RelinkingTask {

        private Integer workflowRunId;
        private final Set<Integer> relatedIusSwids = new HashSet<>();
        private final SetMultimap<Integer, WorkflowRunIus> workflowRunIuses = HashMultimap.create();
        private final SetMultimap<Integer, WorkflowRunProcessing> workflowRunProcessings = HashMultimap.create();

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public void setWorkflowRunId(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public void addWorkflowRunIus(WorkflowRunIus wri) {
            this.relatedIusSwids.add(wri.getIusSwid());
            this.workflowRunIuses.put(wri.getIusSwid(), wri);
        }

        public void addWorkflowRunProcessing(WorkflowRunProcessing wrp) {
            this.relatedIusSwids.add(wrp.getIusSwid());
            this.workflowRunProcessings.put(wrp.getIusSwid(), wrp);
        }

        public Set<Integer> getRelatedIusSwids() {
            return relatedIusSwids;
        }

        public Set<WorkflowRunIus> getWorkflowRunIuses(Integer iusSwid) {
            return workflowRunIuses.get(iusSwid);
        }

        public Set<WorkflowRunProcessing> getWorkflowRunProcessings(Integer iusSwid) {
            return workflowRunProcessings.get(iusSwid);
        }

        public Set<WorkflowRunIus> getWorkflowRunIuses() {
            return new HashSet<>(workflowRunIuses.values());
        }

        public Set<WorkflowRunProcessing> getWorkflowRunProcessings() {
            return new HashSet<>(workflowRunProcessings.values());
        }
    }

    private class WorkflowRunIus {

        private Integer workflowRunId;
        private Integer iusId;
        private Integer iusSwid;
        private Integer sampleProvenanceId;

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public void setWorkflowRunId(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public Integer getIusId() {
            return iusId;
        }

        public void setIusId(Integer iusId) {
            this.iusId = iusId;
        }

        public Integer getIusSwid() {
            return iusSwid;
        }

        public void setIusSwid(Integer iusSwid) {
            this.iusSwid = iusSwid;
        }

        public Integer getSampleProvenanceId() {
            return sampleProvenanceId;
        }

        public void setSampleProvenanceId(Integer sampleProvenanceId) {
            this.sampleProvenanceId = sampleProvenanceId;
        }

    }

    private class WorkflowRunProcessing {

        private Integer workflowRunId;
        private Integer iusId;
        private Integer iusSwid;
        private Integer processingId;

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public void setWorkflowRunId(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public Integer getIusId() {
            return iusId;
        }

        public void setIusId(Integer iusId) {
            this.iusId = iusId;
        }

        public Integer getIusSwid() {
            return iusSwid;
        }

        public void setIusSwid(Integer iusSwid) {
            this.iusSwid = iusSwid;
        }

        public Integer getProcessingId() {
            return processingId;
        }

        public void setProcessingId(Integer processingId) {
            this.processingId = processingId;
        }

    }

    public static class Summary {

        private int workflowRunsAnalyzed;
        private int workflowRunsAlreadyLinked;
        private int workflowRunsRelinked;
        private int workflowRunsErrorState;

        public int getWorkflowRunsAnalyzed() {
            return workflowRunsAnalyzed;
        }

        public void setWorkflowRunsAnalyzed(int workflowRunsAnalyzed) {
            this.workflowRunsAnalyzed = workflowRunsAnalyzed;
        }

        public int getWorkflowRunsAlreadyLinked() {
            return workflowRunsAlreadyLinked;
        }

        public void setWorkflowRunsAlreadyLinked(int workflowRunsAlreadyLinked) {
            this.workflowRunsAlreadyLinked = workflowRunsAlreadyLinked;
        }

        public int getWorkflowRunsRelinked() {
            return workflowRunsRelinked;
        }

        public void setWorkflowRunsRelinked(int workflowRunsRelinked) {
            this.workflowRunsRelinked = workflowRunsRelinked;
        }

        public int getWorkflowRunsErrorState() {
            return workflowRunsErrorState;
        }

        public void setWorkflowRunsErrorState(int workflowRunsErrorState) {
            this.workflowRunsErrorState = workflowRunsErrorState;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

    }

    public static void main(String args[]) throws IOException, IllegalStateException, NamingException {
        OptionParser parser = new OptionParser();
        parser.accepts("help").forHelp();
        parser.accepts("host", "Postgres DB host name/ip").withRequiredArg().required();
        parser.accepts("port", "Portgres DB port").withRequiredArg().required();
        parser.accepts("user", "Postgres DB user").withRequiredArg().required();
        parser.accepts("password", "Postgres DB password").withRequiredArg();
        parser.accepts("db-name", "Postgres DB name").withRequiredArg().required();
        parser.accepts("timestamp", "The timestamp that new IUS and LimsKey \"createdTstmp\" and \"updatedTstmp\" should be set to (eg. 2016-01-01T00:00:00) (Default: \"now\")").withRequiredArg();

        OptionSet options = parser.parse(args);

        if (options.has("help")) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        String host = options.valueOf("host").toString();
        String port = options.valueOf("port").toString();
        String user = options.valueOf("user").toString();
        String dbName = options.valueOf("db-name").toString();

        DateTime timestamp;
        if (options.has("timestamp")) {
            timestamp = DateTime.parse(options.valueOf("timestamp").toString());
        } else {
            timestamp = DateTime.now(DateTimeZone.UTC);
        }

        String password;
        if (options.has("password")) {
            password = options.valueOf("password").toString();
        } else {
            Console console = System.console();
            if (console != null) {
                password = new String(console.readPassword("Please enter Postgres DB password:\n"));
            } else {
                //support for running from within an ide
                InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                System.out.println("Please enter Postgres DB password:");
                password = reader.readLine();
            }
        }
        checkNotNull(password);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName);
        ds.setValidationQuery("SELECT 1");

        try {
            ds.getConnection().createStatement().execute("SELECT 1");
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage());
            System.exit(1);
        }

        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/jdbc/SeqWareMetaDB", ds);
        builder.activate();

        GenericApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");

        AbstractBeanDefinition someClassDefinition = BeanDefinitionBuilder.rootBeanDefinition(RelinkWorkflowRunIusAndProcessingsToLimsKey.class).getBeanDefinition();
        ctx.registerBeanDefinition("relinker", someClassDefinition);

        RelinkWorkflowRunIusAndProcessingsToLimsKey relinker = ctx.getBean(RelinkWorkflowRunIusAndProcessingsToLimsKey.class);
        relinker.setTimestamp(timestamp.toDate());
        Summary summary = relinker.run();
        System.out.println(summary.toString());
    }

}
