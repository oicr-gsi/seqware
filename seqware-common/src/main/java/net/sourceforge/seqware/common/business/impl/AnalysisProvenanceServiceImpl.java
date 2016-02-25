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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.AnalysisProvenanceDtoBuilder;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceServiceImpl implements AnalysisProvenanceService {

    private AnalysisProvenanceDAO analysisProvenanceDAO;
    private IUSDAO iusDAO;

    @Override
    public void setAnalysisProvenanceDAO(AnalysisProvenanceDAO analysisProvenanceDAO) {
        this.analysisProvenanceDAO = analysisProvenanceDAO;
    }

    @Override
    public void setIUSDAO(IUSDAO iusDAO) {
        this.iusDAO = iusDAO;
    }

    @Override
    public List<AnalysisProvenanceDto> list() {
        return buildList(iusDAO.list());
    }

    public static List<AnalysisProvenanceDto> buildList(Collection<IUS> iuses) {
        List<AnalysisProvenanceDto> aps = new ArrayList<>();
        Map<Integer, AnalysisProvenanceDtoBuilder> partialBuilders = new HashMap<>();

        for (IUS ius : iuses) {
            if (ius.getLimsKey() == null) {
                //this ius is not for linking to LIMS
                continue;
            }

            //get all Workflow Runs associated with the IUS
            Set<WorkflowRun> workflowRuns = ius.getWorkflowRuns();
            if (workflowRuns == null || workflowRuns.isEmpty()) {
                //create an Analysis Provenance (with only IUS info) if IUS has no associated Workflow Runs
                AnalysisProvenanceDtoBuilder ap = new AnalysisProvenanceDtoBuilder();
                ap.addIusLimsKey(getIusLimsKey(ius));
                aps.add(ap.build());
            } else {
                for (WorkflowRun workflowRun : workflowRuns) {
                    Integer workflowRunSwid = workflowRun.getSwAccession();
                    if (workflowRunSwid == null) {
                        throw new RuntimeException("null workflow run swid");
                    }
                    AnalysisProvenanceDtoBuilder ap = partialBuilders.get(workflowRunSwid);
                    if (ap == null) {
                        Set<Processing> processings = workflowRun.getProcessings();
                        if (processings == null || processings.isEmpty()) {
                            //do nothing
                        } else {
                            for (Processing processing : processings) {
                                Set<File> files = processing.getFiles();
                                if (files == null || files.isEmpty()) {
                                    // do nothing
                                } else {
                                    for (File file : files) {
                                        Set<IUS> processingIuses = processing.getIUS();
                                        ap = new AnalysisProvenanceDtoBuilder();
                                        ap.setWorkflowRun(workflowRun);
                                        ap.setWorkflow(workflowRun.getWorkflow());
                                        ap.setProcessing(processing);
                                        ap.setFile(file);

                                        if (processingIuses == null || processingIuses.isEmpty()) {
                                            //when the file was created, it was not linked to ius
                                            ap.addIusLimsKey(getIusLimsKey(ius));
                                            partialBuilders.put(workflowRunSwid, ap);
                                        } else {
                                            //when the file was created, it was linked to ius
                                            for (IUS i : processingIuses) {
                                                ap.addIusLimsKey(getIusLimsKey(i));
                                            }
                                            aps.add(ap.build());
                                            //do not add to workflowRunAps, this AP is complete
                                        }
                                    }
                                }
                            }
                        }
                        // if ap is still null, there were no processings and/or files associated with the workflow run
                        // create an AP with only workflow run information
                        if (ap == null) {
                            ap = new AnalysisProvenanceDtoBuilder();
                            ap.addIusLimsKey(getIusLimsKey(ius));
                            ap.setWorkflowRun(workflowRun);
                            ap.setWorkflow(workflowRun.getWorkflow());
                            partialBuilders.put(workflowRunSwid, ap);
                        }
                    } else {
                        ap.addIusLimsKey(getIusLimsKey(ius));
                    }

                }
            }
        }

        for (AnalysisProvenanceDtoBuilder ap : partialBuilders.values()) {
            aps.add(ap.build());
        }

        return aps;
    }

    private static IusLimsKeyDto getIusLimsKey(IUS ius) {
        IusLimsKeyDto ik = new IusLimsKeyDto();
        ik.setIusSWID(ius.getSwAccession());
        ik.setLimsKey(ius.getLimsKey());
        return ik;
    }

}
