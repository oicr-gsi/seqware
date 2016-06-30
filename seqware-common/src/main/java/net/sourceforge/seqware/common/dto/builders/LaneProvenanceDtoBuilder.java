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
package net.sourceforge.seqware.common.dto.builders;

import ca.on.oicr.gsi.provenance.model.LaneProvenance;
import ca.on.oicr.gsi.provenance.util.Versioning;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceDtoBuilder implements LaneProvenance {

    private Lane lane;
    private SequencerRun sequencerRun;

    public LaneProvenanceDtoBuilder setLane(Lane lane) {
        this.lane = lane;
        return this;
    }

    public LaneProvenanceDtoBuilder setSequencerRun(SequencerRun sequencerRun) {
        this.sequencerRun = sequencerRun;
        return this;
    }

    @Override
    public String getSequencerRunName() {
        if (sequencerRun != null) {
            return sequencerRun.getName();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Set<String>> getSequencerRunAttributes() {
        Map<String, Set<String>> attrs = new HashMap<>();
        if (sequencerRun != null) {
            for (SequencerRunAttribute attr : sequencerRun.getSequencerRunAttributes()) {
                Set<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return attrs;
    }

    @Override
    public String getSequencerRunPlatformModel() {
        if (sequencerRun != null && sequencerRun.getPlatform() != null) {
            return sequencerRun.getPlatform().getName();
        } else {
            return null;
        }
    }

    @Override
    public String getLaneNumber() {
        if (lane == null || lane.getLaneIndex() == null) {
            return null;
        } else {
            return lane.getLaneIndex().toString();
        }
    }

    @Override
    public Map<String, Set<String>> getLaneAttributes() {
        Map<String, Set<String>> attrs = new HashMap<>();
        if (lane != null) {
            for (LaneAttribute attr : lane.getLaneAttributes()) {
                Set<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return attrs;
    }
    
    @Override
    public Boolean getSkip() {
        if (lane != null && Boolean.TRUE.equals(lane.getSkip())) {
            return true;
        }
        if (sequencerRun != null && Boolean.TRUE.equals(sequencerRun.getSkip())) {
            return true;
        }
        return false;
    }

    @Override
    public String getLaneProvenanceId() {
        return lane.getSwAccession().toString();
    }

    @Override
    public String getVersion() {
        return Versioning.getSha256(this);
    }

    @Override
    public DateTime getLastModified() {
        DateTime lastModified = null;
        if (lane != null) {
            lastModified = ObjectUtils.max(lastModified,
                    lane.getCreateTimestamp() == null ? null : new DateTime(lane.getCreateTimestamp()),
                    lane.getUpdateTimestamp() == null ? null : new DateTime(lane.getUpdateTimestamp()));
        }
        if (sequencerRun != null) {
            lastModified = ObjectUtils.max(lastModified,
                    sequencerRun.getCreateTimestamp() == null ? null : new DateTime(sequencerRun.getCreateTimestamp()),
                    sequencerRun.getUpdateTimestamp() == null ? null : new DateTime(sequencerRun.getUpdateTimestamp()));
        }
        if (lastModified == null) {
            return null;
        } else {
            return lastModified.toDateTime(DateTimeZone.UTC);
        }
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
        return ToStringBuilder.reflectionToString((LaneProvenance) this);
    }

    public LaneProvenanceDto build() {
        LaneProvenanceDto dto = new LaneProvenanceDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
