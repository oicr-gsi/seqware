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
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
import net.sourceforge.seqware.common.model.adapters.MapOfSetAdapter;
import org.joda.time.DateTime;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement
public class SampleProvenanceDto implements SampleProvenance {

    private String studyTitle;
    private Map<String, Set<String>> studyAttributes;
    private String rootSampleName;
    private String parentSampleName;
    private Map<String, Set<String>> parentSampleAttributes;
    private String sampleName;
    private String sampleOrganismCode;
    private Map<String, Set<String>> sampleAttributes;
    private String sequencerRunName;
    private Map<String, Set<String>> sequencerRunAttributes;
    private String sequencerRunPlatformModel;
    private String laneNumber;
    private Map<String, Set<String>> laneAttributes;
    private String iusTag;
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

    @XmlJavaTypeAdapter(MapOfSetAdapter.class)
    @Override
    public Map<String, Set<String>> getStudyAttributes() {
        return studyAttributes;
    }

    public void setStudyAttributes(Map<String, Set<String>> studyAttributes) {
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

    @XmlJavaTypeAdapter(MapOfSetAdapter.class)
    @Override
    public Map<String, Set<String>> getSampleAttributes() {
        return sampleAttributes;
    }

    public void setSampleAttributes(Map<String, Set<String>> sampleAttributes) {
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

    @XmlJavaTypeAdapter(MapOfSetAdapter.class)
    @Override
    public Map<String, Set<String>> getSequencerRunAttributes() {
        return sequencerRunAttributes;
    }

    public void setSequencerRunAttributes(Map<String, Set<String>> sequencerRunAttributes) {
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

    @XmlJavaTypeAdapter(MapOfSetAdapter.class)
    @Override
    public Map<String, Set<String>> getLaneAttributes() {
        return laneAttributes;
    }

    public void setLaneAttributes(Map<String, Set<String>> laneAttributes) {
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

}
