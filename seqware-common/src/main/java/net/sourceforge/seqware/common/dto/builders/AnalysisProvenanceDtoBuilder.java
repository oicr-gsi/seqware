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
package net.sourceforge.seqware.common.dto.builders;

import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LimsKeyDto;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceDtoBuilder implements AnalysisProvenance {

    private Workflow workflow;
    private WorkflowRun workflowRun;
    private Processing processing;
    private File file;
    private Boolean skip = false;
    private Set<IUS> iuses = new HashSet<>();

    public AnalysisProvenanceDtoBuilder setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setProcessing(Processing processing) {
        this.processing = processing;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setFile(File file) {
        this.file = file;
        return this;
    }

    @Override
    public String getSkip() {
        if (skip == null) {
            return null;
        } else {
            return skip.toString();
        }
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    @Override
    public String getWorkflowName() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getName();
        }
    }

    @Override
    public String getWorkflowVersion() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getVersion();
        }
    }

    @Override
    public Integer getWorkflowId() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getSwAccession();
        }
    }

    @Override
    public Map<String, Set<String>> getWorkflowAttributes() {
        if (workflow == null) {
            return Collections.EMPTY_MAP;
        } else {
            Map<String, Set<String>> map = new HashMap<>();
            for (WorkflowAttribute attr : workflow.getWorkflowAttributes()) {
                Set<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableMap(map);
        }
    }

    @Override
    public String getWorkflowRunName() {
        if (workflowRun == null) {
            return null;
        } else {
            return workflowRun.getName();
        }
    }

    @Override
    public String getWorkflowRunStatus() {
        if (workflowRun == null || workflowRun.getStatus() == null) {
            return null;
        } else {
            return workflowRun.getStatus().toString();
        }
    }

    @Override
    public Integer getWorkflowRunId() {
        if (workflowRun == null) {
            return null;
        } else {
            return workflowRun.getSwAccession();
        }
    }

    @Override
    public Map<String, Set<String>> getWorkflowRunAttributes() {
        if (workflowRun == null) {
            return Collections.EMPTY_MAP;
        } else {
            Map<String, Set<String>> map = new HashMap<>();
            for (WorkflowRunAttribute attr : workflowRun.getWorkflowRunAttributes()) {
                Set<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableMap(map);
        }
    }

    @Override
    public Set<Integer> getWorkflowRunInputFileIds() {
        if (workflowRun == null || workflowRun.getInputFileAccessions() == null) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.unmodifiableSet(workflowRun.getInputFileAccessions());
        }
    }

    @Override
    public String getProcessingAlgorithm() {
        if (processing == null) {
            return null;
        } else {
            return processing.getAlgorithm();
        }
    }

    @Override
    public Integer getProcessingId() {
        if (processing == null) {
            return null;
        } else {
            return processing.getSwAccession();
        }
    }

    @Override
    public Map<String, Set<String>> getProcessingAttributes() {
        if (processing == null) {
            return Collections.EMPTY_MAP;
        } else {
            Map<String, Set<String>> map = new HashMap<>();
            for (ProcessingAttribute attr : processing.getProcessingAttributes()) {
                Set<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableMap(map);
        }
    }

    @Override
    public String getProcessingStatus() {
        if (processing == null || processing.getStatus() == null) {
            return null;
        } else {
            return processing.getStatus().toString();
        }
    }

    @Override
    public String getFileMetaType() {
        if (file == null) {
            return null;
        } else {
            return file.getMetaType();
        }
    }

    @Override
    public Integer getFileId() {
        if (file == null) {
            return null;
        } else {
            return file.getSwAccession();
        }
    }

    @Override
    public Map<String, Set<String>> getFileAttributes() {
        if (file == null) {
            return Collections.EMPTY_MAP;
        } else {
            Map<String, Set<String>> map = new HashMap<>();
            for (FileAttribute attr : file.getFileAttributes()) {
                Set<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableMap(map);
        }
    }

    @Override
    public String getFilePath() {
        if (file == null) {
            return null;
        } else {
            return file.getFilePath();
        }
    }

    @Override
    public String getFileMd5sum() {
        if (file == null) {
            return null;
        } else {
            return file.getMd5sum();
        }
    }

    @Override
    public String getFileSize() {
        if (file == null) {
            return null;
        }
        if (file.getSize() == null) {
            return null;
        }
        return file.getSize().toString();
    }

    @Override
    public String getFileDescription() {
        if (file == null) {
            return null;
        } else {
            return file.getDescription();
        }
    }

    @Override
    public DateTime getLastModified() {
        if (processing == null) {
            return null;
        } else if (processing.getUpdateTimestamp() != null) {
            return new DateTime(processing.getUpdateTimestamp()).toDateTime(DateTimeZone.UTC);
        } else if (processing.getCreateTimestamp() != null) {
            return new DateTime(processing.getCreateTimestamp()).toDateTime(DateTimeZone.UTC);
        } else {
            return null;
        }
    }

    public void addIus(IUS ius) {
        this.iuses.add(ius);
    }

    public void addIuses(Set<IUS> iuses) {
        this.iuses.addAll(iuses);
    }

    @Override
    public Map<String, Set<String>> getIusAttributes() {
        Map<String, Set<String>> map = new HashMap<>();
        for (IUS ius : iuses) {
            for (IUSAttribute attr : ius.getIusAttributes()) {
                Set<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Set<IusLimsKey> getIusLimsKeys() {
        Set<IusLimsKey> iusLimsKeys = new HashSet<>();
        for (IUS ius : iuses) {
            IusLimsKeyDto ilk = new IusLimsKeyDto();
            ilk.setIusSWID(ius.getSwAccession());
            LimsKey lk = ius.getLimsKey();
            LimsKeyDto lkDto = new LimsKeyDto();
            lkDto.setId(lk.getId());
            lkDto.setVersion(lk.getVersion());
            lkDto.setLastModified(lk.getLastModified());
            lkDto.setProvider(lk.getProvider());
            ilk.setLimsKey(lkDto);
            iusLimsKeys.add(ilk);
        }
        return iusLimsKeys;
    }

    public AnalysisProvenanceDto build() {
        AnalysisProvenanceDto dto = new AnalysisProvenanceDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
