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

import ca.on.oicr.gsi.provenance.model.LaneProvenance;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
import net.sourceforge.seqware.common.model.adapters.MapOfSetAdapter;
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
public class LaneProvenanceDto implements LaneProvenance {

    private String sequencerRunName;
    private Map<String, Set<String>> sequencerRunAttributes;
    private String sequencerRunPlatformModel;
    private String laneNumber;
    private Map<String, Set<String>> laneAttributes;
    private String laneProvenanceId;
    private String version;
    private DateTime lastModified;

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
    public String getLaneProvenanceId() {
        return laneProvenanceId;
    }

    public void setLaneProvenanceId(String laneProvenanceId) {
        this.laneProvenanceId = laneProvenanceId;
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
