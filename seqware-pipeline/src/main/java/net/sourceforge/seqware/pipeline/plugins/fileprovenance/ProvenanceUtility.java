/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins.fileprovenance;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;

/**
 * Convenience methods that can be shared by utilities that access the FileProvenance report
 * 
 * @author dyuen
 */
public class ProvenanceUtility {

    public enum HumanProvenanceFilters {

        LANE_SWID("lane-SWID", "Lane sw_accession", true, FileProvenanceParam.lane), IUS_SWID("ius-SWID", "IUS sw_accession", true,
                FileProvenanceParam.ius), STUDY_NAME("study-name", "Full study name", false, FileProvenanceParam.study), SAMPLE_NAME(
                "sample-name", "Full sample name", false, FileProvenanceParam.sample), ROOT_SAMPLE_NAME("root-sample-name",
                "Full root sample name", false, FileProvenanceParam.root_sample), SEQUENCER_RUN_NAME("sequencer-run-name",
                "Full sequencer run name", false, FileProvenanceParam.sequencer_run), ORGANISM("organism", "organism id", true,
                FileProvenanceParam.organism), WORKFLOW_RUN_STATUS("workflow-run-status", "Workflow run status", true,
                FileProvenanceParam.workflow_run_status), PROCESSING_STATUS("processing-status", "Processing status", true,
                FileProvenanceParam.processing_status), PROCESSING("processing-SWID", "processing sw_accession", true,
                FileProvenanceParam.processing),
        LANE_NAME("lane-name", "Full lane name", false, FileProvenanceParam.lane);

        public final String human_str;
        public final String desc;
        public final boolean standard;
        public final FileProvenanceParam mappedParam;

        private HumanProvenanceFilters(String human_str, String desc, boolean standard, FileProvenanceParam mappedParam) {
            this.human_str = human_str;
            this.desc = desc;
            this.standard = standard;
            this.mappedParam = mappedParam;
        }

        @Override
        public String toString() {
            if (human_str == null) {
                return super.toString();
            }
            return human_str;
        }
    }

    public static final String ALL = "all";
    private static final String AT_LEAST_ONE_OF = "At least one of " + Arrays.toString(HumanProvenanceFilters.values()) + "  or " + ALL
            + " is required. ";

    public static Map<String, OptionSpec> configureFileProvenanceParams(OptionParser parser) {
        Map<String, OptionSpec> specMap = new HashMap<>();
        for (HumanProvenanceFilters filter : HumanProvenanceFilters.values()) {
            ArgumentAcceptingOptionSpec<String> studyTitleSpec = parser
                    .acceptsAll(Arrays.asList(filter.toString()),
                            filter.toString() + ". " + AT_LEAST_ONE_OF + "Specify multiple names by repeating --" + filter.toString())
                    .withRequiredArg().ofType(String.class);
            specMap.put(filter.toString(), studyTitleSpec);
        }
        parser.acceptsAll(Arrays.asList(ALL), "Operate across everything. " + AT_LEAST_ONE_OF);
        return specMap;
    }

    public static boolean checkForValidOptions(OptionSet set) {
        boolean hasConstraint = false;
        for (HumanProvenanceFilters filter : HumanProvenanceFilters.values()) {
            if (set.hasArgument(filter.toString())) {
                hasConstraint = true;
            }
        }
        boolean hasAll = false;
        if (set.has(ALL)) {
            hasAll = true;
        }
        return hasConstraint ^ hasAll;
    }

    public static Map<FileProvenanceParam, List<String>> convertOptionsToMap(OptionSet options, Metadata metadata) {
        Map<FileProvenanceParam, List<String>> map = new EnumMap<>(FileProvenanceParam.class);
        if (options.has(ALL)) {
            /**
             * nothing special
             */
        } else {
            for (HumanProvenanceFilters filter : HumanProvenanceFilters.values()) {
                List<String> swaStrings = new ArrayList<>();
                List<?> swaValues = options.valuesOf(filter.toString());
                if (filter.standard) {
                    swaStrings = new ArrayList<>();
                    for (Object swa : swaValues) {
                        swaStrings.add(String.valueOf(swa));
                    }
                } else if (filter == HumanProvenanceFilters.STUDY_NAME) {
                    for (String value : (List<String>) swaValues) {
                        List<Study> studiesByName = metadata.getStudyByName(value);
                        // User must be notified if the sample name or root sample name was invalid (could not be found).
                        if (studiesByName == null || studiesByName.size() == 0) {
                            String errorText = "The study with the name \"" + value + "\" could not be found.";
                            Log.error(errorText);
                            throw new NotFoundException(errorText);
                        }
                        for (Study study : studiesByName) {
                            swaStrings.add(String.valueOf(study.getSwAccession()));
                        }
                        
                    }
                } else if (filter == HumanProvenanceFilters.SAMPLE_NAME || filter == HumanProvenanceFilters.ROOT_SAMPLE_NAME) {
                    for (String value : (List<String>) swaValues) {
                        List<Sample> samplesByName = metadata.getSampleByName(value);
                        // User must be notified if the sample name or root sample name was invalid (could not be found).
                        if (samplesByName == null || samplesByName.size() == 0) {
                            String errorText = "The sample with the name \"" + value + "\" could not be found.";
                            Log.error(errorText);
                            throw new NotFoundException(errorText);
                        }
                        for (Sample sample : samplesByName) {
                            swaStrings.add(String.valueOf(sample.getSwAccession()));
                        }
                        
                    }
                } else if (filter == HumanProvenanceFilters.SEQUENCER_RUN_NAME) {
                    for (String value : (List<String>) swaValues) {
                        SequencerRun run = metadata.getSequencerRunByName(value);
                        swaStrings.add(String.valueOf(run.getSwAccession()));
                    }
                } else if (filter == HumanProvenanceFilters.LANE_NAME) {
                    //not supported by seqware
                } else {
                    throw new RuntimeException("No handler for filter type = [" + filter.toString() + "]");
                }
                map.put(filter.mappedParam, new ImmutableList.Builder<String>().addAll(swaStrings).build());
            }
        }
        return map;
    }
}
