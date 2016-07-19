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

import ca.on.oicr.gsi.provenance.model.SampleProvenance;
import ca.on.oicr.gsi.provenance.util.Versioning;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
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
public class SampleProvenanceDtoBuilder implements SampleProvenance {

    private IUS ius;
    private Lane lane;
    private SequencerRun sequencerRun;
    private Sample sample;
    private List<Sample> parentSamples;
    private Experiment experiment;
    private Study study;

    public SampleProvenanceDtoBuilder setIus(IUS ius) {
        this.ius = ius;
        return this;
    }

    public SampleProvenanceDtoBuilder setLane(Lane lane) {
        this.lane = lane;
        return this;
    }

    public SampleProvenanceDtoBuilder setSequencerRun(SequencerRun sequencerRun) {
        this.sequencerRun = sequencerRun;
        return this;
    }

    public SampleProvenanceDtoBuilder setSample(Sample sample) {
        this.sample = sample;
        return this;
    }

    public SampleProvenanceDtoBuilder setParentSamples(List<Sample> parentSamples) {
        this.parentSamples = parentSamples;
        return this;
    }

    public SampleProvenanceDtoBuilder setExperiment(Experiment experiment) {
        this.experiment = experiment;
        return this;
    }

    public SampleProvenanceDtoBuilder setStudy(Study study) {
        this.study = study;
        return this;
    }

    @Override
    public String getStudyTitle() {
        return study.getTitle();
    }

    @Override
    public SortedMap<String, SortedSet<String>> getStudyAttributes() {
        SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
        for (StudyAttribute attr : study.getStudyAttributes()) {
            SortedSet<String> values = attrs.get(attr.getTag());
            if (values == null) {
                values = new TreeSet<>();
                attrs.put(attr.getTag(), values);
            }
            values.add(attr.getValue());
        }
        return attrs;
    }

    @Override
    public String getRootSampleName() {
        if (parentSamples == null || parentSamples.isEmpty()) {
            return null;
        } else {
            return Iterables.getLast(parentSamples).getName();
        }
    }

    @Override
    public String getParentSampleName() {
        List<String> parentSampleNames = new ArrayList<>();
        if (parentSamples != null) {
            for (Sample s : parentSamples) {
                parentSampleNames.add(s.getName());
            }
            return Joiner.on(":").join(parentSampleNames);
        } else {
            return null;
        }
    }

    @Override
    public String getSampleName() {
        if (sample != null) {
            return sample.getName();
        } else {
            return null;
        }
    }

    @Override
    public SortedMap<String, SortedSet<String>> getSampleAttributes() {
        SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
        if (parentSamples != null) {
            for (Sample s : parentSamples) {
                for (SampleAttribute attr : s.getSampleAttributes()) {
                    SortedSet<String> values = attrs.get(attr.getTag());
                    if (values == null) {
                        values = new TreeSet<>();
                        attrs.put(attr.getTag(), values);
                    }
                    values.add(attr.getValue());
                }
            }
        }
        if (sample != null) {
            for (SampleAttribute attr : sample.getSampleAttributes()) {
                SortedSet<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        if (ius != null) {
            for (IUSAttribute attr : ius.getIusAttributes()) {
                SortedSet<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return attrs;
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
    public SortedMap<String, SortedSet<String>> getSequencerRunAttributes() {
        SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
        if (sequencerRun != null) {
            for (SequencerRunAttribute attr : sequencerRun.getSequencerRunAttributes()) {
                SortedSet<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
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
            return Integer.toString(lane.getLaneIndex() + 1);
        }
    }

    @Override
    public SortedMap<String, SortedSet<String>> getLaneAttributes() {
        SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
        if (lane != null) {
            for (LaneAttribute attr : lane.getLaneAttributes()) {
                SortedSet<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return attrs;
    }

    @Override
    public String getIusTag() {
        if (ius != null) {
            return ius.getTag();
        } else {
            return null;
        }
    }
    
    @Override
    public Boolean getSkip() {
        if (ius != null && Boolean.TRUE.equals(ius.getSkip())) {
            return true;
        }
        if (sample != null && Boolean.TRUE.equals(sample.getSkip())) {
            return true;
        }
        if (lane != null && Boolean.TRUE.equals(lane.getSkip())) {
            return true;
        }
        if (sequencerRun != null && Boolean.TRUE.equals(sequencerRun.getSkip())) {
            return true;
        }
        //study skip is not supported
        //experiment skip is not supported
        if (parentSamples != null) {
            for (Sample s : parentSamples) {
                if (Boolean.TRUE.equals(s.getSkip())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((SampleProvenance) this);
    }

    @Override
    public String getSampleProvenanceId() {
        return ius.getSwAccession().toString();
    }

    @Override
    public String getVersion() {
        return Versioning.getSha256(this);
    }

    @Override
    public DateTime getLastModified() {
        DateTime lastModified = null;
        if (ius != null) {

            lastModified = ObjectUtils.max(lastModified,
                    ius.getCreateTimestamp() == null ? null : new DateTime(ius.getCreateTimestamp()),
                    ius.getUpdateTimestamp() == null ? null : new DateTime(ius.getUpdateTimestamp()));
        }
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
        if (sample != null) {
            lastModified = ObjectUtils.max(lastModified,
                    sample.getCreateTimestamp() == null ? null : new DateTime(sample.getCreateTimestamp()),
                    sample.getUpdateTimestamp() == null ? null : new DateTime(sample.getUpdateTimestamp()));
        }
        if (experiment != null) {
            lastModified = ObjectUtils.max(lastModified,
                    experiment.getCreateTimestamp() == null ? null : new DateTime(experiment.getCreateTimestamp()),
                    experiment.getUpdateTimestamp() == null ? null : new DateTime(experiment.getUpdateTimestamp()));
        }
        if (study != null) {
            lastModified = ObjectUtils.max(lastModified,
                    study.getCreateTimestamp() == null ? null : new DateTime(study.getCreateTimestamp()),
                    study.getUpdateTimestamp() == null ? null : new DateTime(study.getUpdateTimestamp()));
        }
        if (parentSamples != null) {
            for (Sample s : parentSamples) {
                lastModified = ObjectUtils.max(lastModified,
                        s.getCreateTimestamp() == null ? null : new DateTime(s.getCreateTimestamp()),
                        s.getUpdateTimestamp() == null ? null : new DateTime(s.getUpdateTimestamp()));
            }
        }
        if (lastModified == null) {
            return null;
        } else {
            return lastModified.toDateTime(DateTimeZone.UTC);
        }
    }
    
    @Override
    public DateTime getCreatedDate() {
        DateTime createdDate = null;
        if (lane != null) {
            createdDate = ObjectUtils.min(createdDate,
                    lane.getCreateTimestamp() == null ? null : new DateTime(lane.getCreateTimestamp()));
        }
        if (sequencerRun != null) {
            createdDate = ObjectUtils.min(createdDate,
                    sequencerRun.getCreateTimestamp() == null ? null : new DateTime(sequencerRun.getCreateTimestamp()));
        }
        if (createdDate == null) {
            return null;
        } else {
            return createdDate.toDateTime(DateTimeZone.UTC);
        }
    }

    public SampleProvenanceDto build() {
        SampleProvenanceDto dto = new SampleProvenanceDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
