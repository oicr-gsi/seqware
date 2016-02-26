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

import ca.on.oicr.gsi.provenance.api.model.SampleProvenance;
import ca.on.oicr.gsi.provenance.util.Versioning;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private Collection<Sample> parentSamples;
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

    public SampleProvenanceDtoBuilder setParentSamples(Collection<Sample> parentSamples) {
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
    public Map<String, Set<String>> getStudyAttributes() {
        Map<String, Set<String>> attrs = new HashMap<>();
        for (StudyAttribute attr : study.getStudyAttributes()) {
            Set<String> values = attrs.get(attr.getTag());
            if (values == null) {
                values = new HashSet<>();
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
    public Map<String, Set<String>> getParentSampleAttributes() {
        Map<String, Set<String>> attrs = new HashMap<>();
        if (parentSamples != null) {
            for (Sample s : parentSamples) {
                for (SampleAttribute attr : s.getSampleAttributes()) {
                    Set<String> values = attrs.get(attr.getTag());
                    if (values == null) {
                        values = new HashSet<>();
                        attrs.put(attr.getTag(), values);
                    }
                    values.add(attr.getValue());
                }
            }
        }
        return attrs;
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
    public String getSampleOrganismCode() {
        if (sample != null && sample.getOrganism() != null) {
            return sample.getOrganism().getName();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Set<String>> getSampleAttributes() {
        Map<String, Set<String>> attrs = new HashMap<>();
        if (sample != null) {
            for (SampleAttribute attr : sample.getSampleAttributes()) {
                Set<String> values = attrs.get(attr.getTag());
                if (values == null) {
                    values = new HashSet<>();
                    attrs.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        if (ius != null) {
            for (IUSAttribute attr : ius.getIusAttributes()) {
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
    public String getSequencerRunPlatformName() {
        if (sequencerRun.getPlatform() != null) {
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
    public String getIusTag() {
        if (ius != null) {
            return ius.getTag();
        } else {
            return null;
        }
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
    public String getSampleId() {
        return ius.getSwAccession().toString();
//        return sample.getSampleId().toString();
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
        return lastModified;
    }

    public SampleProvenanceDto build() {
        SampleProvenanceDto dto = new SampleProvenanceDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
