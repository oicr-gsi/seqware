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

import ca.on.oicr.gsi.provenance.model.SampleProvenance;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
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
public class SampleProvenanceDto implements SampleProvenance {

    private String studyTitle;
    private SortedMap<String, SortedSet<String>> studyAttributes;
    private String rootSampleName;
    private String parentSampleName;
    private SortedMap<String, SortedSet<String>> parentSampleAttributes;
    private String sampleName;
    private String sampleOrganismCode;
    private SortedMap<String, SortedSet<String>> sampleAttributes;
    private String sequencerRunName;
    private SortedMap<String, SortedSet<String>> sequencerRunAttributes;
    private String sequencerRunPlatformModel;
    private String laneNumber;
    private SortedMap<String, SortedSet<String>> laneAttributes;
    private String iusTag;
    private Boolean skip;
    private String sampleProvenanceId;
    private String version;
    private DateTime lastModified;

    @XmlElement
    @Override
    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getStudyAttributes() {
        return studyAttributes;
    }

    public void setStudyAttributes(SortedMap<String, SortedSet<String>> studyAttributes) {
        this.studyAttributes = studyAttributes;
    }

    @XmlElement
    @Override
    public String getRootSampleName() {
        return rootSampleName;
    }

    public void setRootSampleName(String rootSampleName) {
        this.rootSampleName = rootSampleName;
    }

    @XmlElement
    @Override
    public String getParentSampleName() {
        return parentSampleName;
    }

    public void setParentSampleName(String parentSampleName) {
        this.parentSampleName = parentSampleName;
    }

    @XmlElement
    @Override
    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getSampleAttributes() {
        return sampleAttributes;
    }

    public void setSampleAttributes(SortedMap<String, SortedSet<String>> sampleAttributes) {
        this.sampleAttributes = sampleAttributes;
    }

    @XmlElement
    @Override
    public String getSequencerRunName() {
        return sequencerRunName;
    }

    public void setSequencerRunName(String sequencerRunName) {
        this.sequencerRunName = sequencerRunName;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getSequencerRunAttributes() {
        return sequencerRunAttributes;
    }

    public void setSequencerRunAttributes(SortedMap<String, SortedSet<String>> sequencerRunAttributes) {
        this.sequencerRunAttributes = sequencerRunAttributes;
    }

    @XmlElement
    @Override
    public String getSequencerRunPlatformModel() {
        return sequencerRunPlatformModel;
    }

    public void setSequencerRunPlatformModel(String sequencerRunPlatformModel) {
        this.sequencerRunPlatformModel = sequencerRunPlatformModel;
    }

    @XmlElement
    @Override
    public String getLaneNumber() {
        return laneNumber;
    }

    public void setLaneNumber(String laneNumber) {
        this.laneNumber = laneNumber;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    @Override
    public SortedMap<String, SortedSet<String>> getLaneAttributes() {
        return laneAttributes;
    }

    public void setLaneAttributes(SortedMap<String, SortedSet<String>> laneAttributes) {
        this.laneAttributes = laneAttributes;
    }

    @XmlElement
    @Override
    public String getIusTag() {
        return iusTag;
    }

    public void setIusTag(String iusTag) {
        this.iusTag = iusTag;
    }

    @XmlElement
    @Override
    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    @XmlElement
    @Override
    public String getSampleProvenanceId() {
        return sampleProvenanceId;
    }

    public void setSampleProvenanceId(String sampleProvenanceId) {
        this.sampleProvenanceId = sampleProvenanceId;
    }

    @XmlElement
    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @Override
    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
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
