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
package net.sourceforge.seqware.common.business.impl;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.common.business.SampleProvenanceService;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.SampleProvenanceDtoBuilder;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Sample;

/**
 *
 * @author mlaszloffy
 */
public class SampleProvenanceServiceImpl implements SampleProvenanceService {

    private IUSDAO iusDAO;

    @Override
    public void setIUSDAO(IUSDAO iusDAO) {
        this.iusDAO = iusDAO;
    }

    @Override
    public List<SampleProvenanceDto> list() {
        return buildList(iusDAO.list());
    }

    public static List<SampleProvenanceDto> buildList(Collection<IUS> iuses) {
        List<SampleProvenanceDtoBuilder> spbs = new ArrayList<>();
        for (IUS ius : iuses) {
            Sample sample = ius.getSample();
            if (sample == null) {
                continue;
            }
            SampleProvenanceDtoBuilder sp = new SampleProvenanceDtoBuilder();
            sp.setIus(ius);
            sp.setLane(ius.getLane());
            sp.setSequencerRun(ius.getLane().getSequencerRun());
            sp.setSample(sample);
            sp.setExperiment(sample.getExperiment());
            sp.setStudy(sample.getExperiment().getStudy());

            List<Sample> parentSamples = new ArrayList<>();
            //Sample parentSample = Iterables.getOnlyElement(sample.getParents(), null);
            Sample parentSample = Iterables.getLast(sample.getParents(), null);
            while (parentSample != null) {
                parentSamples.add(parentSample);
                //parentSample = Iterables.getOnlyElement(sample.getParents(), null);
                parentSample = Iterables.getLast(parentSample.getParents(), null);
            }
            sp.setParentSamples(parentSamples);

            spbs.add(sp);
        }

        List<SampleProvenanceDto> sps = new ArrayList<>();
        for (SampleProvenanceDtoBuilder spb : spbs) {
            sps.add(spb.build());
        }

        return sps;
    }

}
