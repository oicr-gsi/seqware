/*
 * Copyright (C) 2012 SeqWare
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.seqware.common.model.WorkflowRunStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.hibernate.WorkflowRunReport;
import net.sourceforge.seqware.common.hibernate.reports.WorkflowRunReportRow;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.Log;
import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * <p>
 * WorkflowRunReportResource class.
 * </p>
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunReportResource extends BasicRestlet {

    private static final String STATUS = "status";
    private final Logger logger = Logger.getLogger(WorkflowRunReportResource.class);
    private final ObjectMapper mapper;

    /**
     * <p>
     * Constructor for WorkflowRunReportResource.
     * </p>
     * 
     * @param context
     *            a {@link org.restlet.Context} object.
     */
    public WorkflowRunReportResource(Context context) {
        super(context);

        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        boolean showStdErr = false;
        boolean showStdOut = false;
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);
        Object wrId = request.getAttributes().get("workflowRunId");
        Object wId = request.getAttributes().get("workflowId");
        String path = request.getResourceRef().getPath();

        // checking if a user is requesting error log rather than summary report
        if (path.contains("stderr")) {
            showStdErr = true;
        } else if (path.contains("stdout")) {
            showStdOut = true;
        }

        Date earliestDate = new Date(0);
        Date latestDate = new Date();
        WorkflowRunStatus status = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        for (String key : queryValues.keySet()) {
            logger.debug("queryValues: " + key + " " + queryValues.get(key));
        }
        if (queryValues.containsKey("earliestDate")) {
            String dateString = queryValues.get("earliestDate");
            try {
                earliestDate = dateFormat.parse(dateString);
                logger.debug("Earliest date: " + earliestDate.toString());
            } catch (ParseException ex) {
                logger.error("Improperly formatted earliest date. Should be in the form yyyyMMdd");
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                        "Improperly formatted earliest date. Should be in the form yyyyMMdd: " + ex.getMessage());
            }
        }
        if (queryValues.containsKey("latestDate")) {
            String dateString = queryValues.get("latestDate");
            try {
                latestDate = dateFormat.parse(dateString);
                logger.debug("Earliest date: " + latestDate.toString());
            } catch (ParseException ex) {
                logger.error("Improperly formatted latest date. Should be in the form yyyyMMdd");
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                        "Improperly formatted latest date. Should be in the form yyyyMMdd: " + ex.getMessage());
            }
        }
        if (queryValues.containsKey(STATUS)) {
            try {
                status = WorkflowRunStatus.valueOf(queryValues.get(STATUS));
            } catch (IllegalArgumentException | NullPointerException e) {
                logger.error("Improper workflow run status: " + queryValues.get(STATUS));
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Improper status, should be one of "
                        + Arrays.toString(WorkflowRunStatus.values()));
            }
        }

        if (request.getMethod().compareTo(Method.GET) == 0) {
            WorkflowRunReport cfc = new WorkflowRunReport();
            cfc.setEarliestDate(earliestDate);
            cfc.setLatestDate(latestDate);
            cfc.setStatus(status);
            StringBuilder builder = getHeader();
            if (wId != null) {
                Collection<WorkflowRunReportRow> rows = cfc.getRunsFromWorkflow(parseClientInt(wId.toString()));
                for (WorkflowRunReportRow results : rows) {
                    toString(results, builder);
                }
            } else if (wrId != null) {
                WorkflowRunReportRow results = cfc.getSingleWorkflowRun(parseClientInt(wrId.toString()));
                // check to see if we're just returning the stderr/out or full report
                if (showStdErr) {
                    builder = new StringBuilder();
                    outputLogString(true, results, builder);
                } else if (showStdOut) {
                    builder = new StringBuilder();
                    outputLogString(false, results, builder);
                } else { // full report
                    toString(results, builder);
                }
            } else if (status != null) {
                Log.debug("getRunsByStatus " + status.toString());
                Collection<WorkflowRunReportRow> runsByStatus = cfc.getRunsByStatus(status);
                for (WorkflowRunReportRow results : runsByStatus) {
                    toString(results, builder);
                }
            } else if (wId == null && wrId == null) {
                String errMsg = "Improperly formatted, you need to provide a workflow ID or workflowRun ID";
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errMsg);
            } else {
                Collection<WorkflowRunReportRow> rows = cfc.getAllRuns();
                for (WorkflowRunReportRow results : rows) {
                    toString(results, builder);
                }
            }
            response.setEntity(builder.toString(), MediaType.TEXT_PLAIN);
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
    }

    /**
     * <p>
     * getHeader.
     * </p>
     * 
     * @return a {@link java.lang.StringBuilder} object.
     */
    public StringBuilder getHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append("Workflow").append("\t");
        builder.append("Workflow Run SWID").append("\t");
        builder.append("Workflow Run Status").append("\t");
        builder.append("Workflow Run Create Timestamp").append("\t");
        builder.append("Workflow Run Host").append("\t");
        builder.append("Workflow Run Working Dir").append("\t");
        builder.append("Workflow Run Engine ID").append("\t");
        builder.append("Library Sample Names").append("\t");
        builder.append("Library Sample SWIDs").append("\t");
        builder.append("Identity Sample Names").append("\t");
        builder.append("Identity Sample SWIDs").append("\t");
        builder.append("IUS-LimsKeys").append("\t");
        // following three fields are for "all"
        builder.append("Input File Meta-Types").append("\t");
        builder.append("Input File SWIDs").append("\t");
        builder.append("Input File Paths").append("\t");
        builder.append("Immediate Input File Meta-Types").append("\t");
        builder.append("Immediate Input File SWIDs").append("\t");
        builder.append("Immediate Input File Paths").append("\t");
        builder.append("Output File Meta-Types").append("\t");
        builder.append("Output File SWIDs").append("\t");
        builder.append("Output File Paths").append("\t");
        builder.append("Workflow Run Time").append("\t");

        builder.append("\n");
        return builder;
    }

    /**
     * <p>
     * outputLogString.
     * </p>
     * 
     * @param stdErr
     *            a boolean.
     * @param wrrr
     *            a {@link net.sourceforge.seqware.common.hibernate.reports.WorkflowRunReportRow} object.
     * @param builder
     *            a {@link java.lang.StringBuilder} object.
     * @return a {@link java.lang.String} object.
     */
    public String outputLogString(boolean stdErr, WorkflowRunReportRow wrrr, StringBuilder builder) {
        if (stdErr) {
            builder.append(wrrr.getWorkflowRun().getStdErr());
        } else {
            builder.append(wrrr.getWorkflowRun().getStdOut());
        }
        builder.append("\n");
        return builder.toString();

    }

    /**
     * <p>
     * toString.
     * </p>
     * 
     * @param wrrr
     *            a {@link net.sourceforge.seqware.common.hibernate.reports.WorkflowRunReportRow} object.
     * @param builder
     *            a {@link java.lang.StringBuilder} object.
     * @return a {@link java.lang.String} object.
     */
    public String toString(WorkflowRunReportRow wrrr, StringBuilder builder) {
        builder.append(wrrr.getWorkflowRun().getWorkflow().getName()).append(" ").append(wrrr.getWorkflowRun().getWorkflow().getVersion())
                .append("\t");
        builder.append(wrrr.getWorkflowRun().getSwAccession()).append("\t");
        builder.append(wrrr.getWorkflowRun().getStatus()).append("\t");
        builder.append(wrrr.getWorkflowRun().getCreateTimestamp().toString()).append("\t");
        builder.append(wrrr.getWorkflowRun().getHost()).append("\t");
        if (wrrr.getWorkflowRun().getCurrentWorkingDir() != null) builder.append(wrrr.getWorkflowRun().getCurrentWorkingDir());
        builder.append("\t");
        if (wrrr.getWorkflowRun().getStatusCmd() != null) builder.append(wrrr.getWorkflowRun().getStatusCmd());
        builder.append("\t");

        parseSamples(builder, wrrr.getLibrarySamples());
        parseSamples(builder, wrrr.getIdentitySamples());

        builder.append(parseIusLimsKeyDtos(wrrr.getIusLimsKeyDtos()));
        builder.append("\t");

        parseFiles(builder, wrrr.getAllInputFiles());
        parseFiles(builder, wrrr.getImmediateInputFiles());
        parseFiles(builder, wrrr.getOutputFiles());

        builder.append(wrrr.getTimeTaken());

        builder.append("\n");
        return builder.toString();

    }

    private void parseSamples(StringBuilder builder, Collection<Sample> samples) {
        StringBuilder sampleNames = new StringBuilder();
        StringBuilder sampleSWIDs = new StringBuilder();
        for (Sample s : samples) {
            if (s != null) {
                if (sampleNames.length() != 0) {
                    sampleNames.append(",");
                }
                if (sampleSWIDs.length() != 0) {
                    sampleSWIDs.append(",");
                }
                sampleNames.append(s.getName());
                sampleSWIDs.append(s.getSwAccession());
            }
        }
        builder.append(sampleNames.toString()).append("\t");
        builder.append(sampleSWIDs.toString()).append("\t");
    }

    private void parseFiles(StringBuilder builder, Collection<File> files) {
        StringBuilder fileTypes = new StringBuilder();
        StringBuilder filePaths = new StringBuilder();
        StringBuilder fileSWIDs = new StringBuilder();
        for (File f : files) {
            if (fileTypes.length() != 0) {
                fileTypes.append(",");
            }
            if (filePaths.length() != 0) {
                filePaths.append(",");
            }
            if (fileSWIDs.length() != 0) {
                fileSWIDs.append(",");
            }
            fileTypes.append(f.getMetaType());
            filePaths.append(f.getFilePath());
            fileSWIDs.append(f.getSwAccession());
        }

        builder.append(fileTypes.toString()).append("\t");
        builder.append(fileSWIDs.toString()).append("\t");
        builder.append(filePaths.toString()).append("\t");
    }

    private String parseIusLimsKeyDtos(Collection<IusLimsKeyDto> iusLimsKeyDtos) {
        try {
            return mapper.writeValueAsString(iusLimsKeyDtos);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
