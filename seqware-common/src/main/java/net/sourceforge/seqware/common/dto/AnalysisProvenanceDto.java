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
package net.sourceforge.seqware.common.dto;

import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
import net.sourceforge.seqware.common.model.adapters.IntegerSortedSet;
import net.sourceforge.seqware.common.model.adapters.IusLimsKeyAdapter;
import net.sourceforge.seqware.common.model.adapters.SortedMapOfSortedSetAdapter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement
public class AnalysisProvenanceDto implements AnalysisProvenance {

    protected String workflowName;
    protected String workflowVersion;
    protected Integer workflowId;
    protected SortedMap<String, SortedSet<String>> workflowAttributes;
    protected String workflowRunName;
    protected String workflowRunStatus;
    protected Integer workflowRunId;
    protected SortedMap<String, SortedSet<String>> workflowRunAttributes;
    protected SortedSet<Integer> workflowRunInputFileIds;
    protected String processingAlgorithm;
    protected Integer processingId;
    protected String processingStatus;
    protected SortedMap<String, SortedSet<String>> processingAttributes;
    protected String fileMetaType;
    protected Integer fileId;
    protected String filePath;
    protected String fileMd5sum;
    protected String fileSize;
    protected String fileDescription;
    protected SortedMap<String, SortedSet<String>> fileAttributes;
    protected Boolean skip;
    protected DateTime lastModified;
    protected Set<IusLimsKey> iusLimsKeys;
    protected SortedMap<String, SortedSet<String>> iusAttributes;

    @Override
    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    @Override
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    public void setWorkflowVersion(String workflowVersion) {
        this.workflowVersion = workflowVersion;
    }

    @Override
    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getWorkflowAttributes() {
        return workflowAttributes;
    }

    public void setWorkflowAttributes(SortedMap<String, SortedSet<String>> workflowAttributes) {
        this.workflowAttributes = workflowAttributes;
    }

    @Override
    public String getWorkflowRunName() {
        return workflowRunName;
    }

    public void setWorkflowRunName(String workflowRunName) {
        this.workflowRunName = workflowRunName;
    }

    @Override
    public String getWorkflowRunStatus() {
        return workflowRunStatus;
    }

    public void setWorkflowRunStatus(String workflowRunStatus) {
        this.workflowRunStatus = workflowRunStatus;
    }

    @Override
    public Integer getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Integer workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getWorkflowRunAttributes() {
        return workflowRunAttributes;
    }

    public void setWorkflowRunAttributes(SortedMap<String, SortedSet<String>> workflowRunAttributes) {
        this.workflowRunAttributes = workflowRunAttributes;
    }

    @XmlJavaTypeAdapter(IntegerSortedSet.class)
    @Override
    public SortedSet<Integer> getWorkflowRunInputFileIds() {
        return workflowRunInputFileIds;
    }

    public void setWorkflowRunInputFileIds(SortedSet<Integer> workflowRunInputFileIds) {
        this.workflowRunInputFileIds = workflowRunInputFileIds;
    }

    @Override
    public String getProcessingAlgorithm() {
        return processingAlgorithm;
    }

    public void setProcessingAlgorithm(String processingAlgorithm) {
        this.processingAlgorithm = processingAlgorithm;
    }

    @Override
    public Integer getProcessingId() {
        return processingId;
    }

    public void setProcessingId(Integer processingId) {
        this.processingId = processingId;
    }

    @Override
    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getProcessingAttributes() {
        return processingAttributes;
    }

    public void setProcessingAttributes(SortedMap<String, SortedSet<String>> processingAttributes) {
        this.processingAttributes = processingAttributes;
    }

    @Override
    public String getFileMetaType() {
        return fileMetaType;
    }

    public void setFileMetaType(String fileMetaType) {
        this.fileMetaType = fileMetaType;
    }

    @Override
    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getFileMd5sum() {
        return fileMd5sum;
    }

    public void setFileMd5sum(String fileMd5sum) {
        this.fileMd5sum = fileMd5sum;
    }

    @Override
    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getFileAttributes() {
        return fileAttributes;
    }

    public void setFileAttributes(SortedMap<String, SortedSet<String>> fileAttributes) {
        this.fileAttributes = fileAttributes;
    }

    @Override
    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @Override
    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    @XmlElementWrapper(name = "iusLimsKeys")
    @XmlElement(name = "iusLimsKey")
    @XmlJavaTypeAdapter(IusLimsKeyAdapter.class)
    @Override
    public Set<IusLimsKey> getIusLimsKeys() {
        return iusLimsKeys;
    }

    public void setIusLimsKeys(Set<IusLimsKey> keys) {
        this.iusLimsKeys = keys;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getIusAttributes() {
        return iusAttributes;
    }

    public void setIusAttributes(SortedMap<String, SortedSet<String>> iusAttributes) {
        this.iusAttributes = iusAttributes;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
