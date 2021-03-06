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

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceSqlResultDto extends AnalysisProvenanceDto {

    public void setWorkflowAttributes(String workflowAttributes) {
        this.workflowAttributes = convertAttributesString(workflowAttributes);
    }

    public void setWorkflowRunAttributes(String workflowRunAttributes) {
        this.workflowRunAttributes = convertAttributesString(workflowRunAttributes);
    }

    public void setWorkflowRunInputFileIds(String workflowRunInputFileIds) {
        this.workflowRunInputFileIds = convertIntegerString(workflowRunInputFileIds);
    }

    public void setProcessingAttributes(String processingAttributes) {
        this.processingAttributes = convertAttributesString(processingAttributes);
    }

    public void setFileSize(BigInteger fileSize) {
        if (fileSize != null) {
            this.fileSize = fileSize.toString();
        }
    }

    public void setFileAttributes(String fileAttributes) {
        this.fileAttributes = convertAttributesString(fileAttributes);
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = new DateTime(lastModified);
    }

    public void setIusLimsKeys(String iusLimsKeys) {
        this.iusLimsKeys = convertIusLimsKeyString(iusLimsKeys);
    }

    public void setIusAttributes(String iusAttributes) {
        this.iusAttributes = convertAttributesString(iusAttributes);
    }

    private static SortedMap<String, SortedSet<String>> convertAttributesString(String attributesString) {
        if (attributesString == null || attributesString.isEmpty()) {
            return MapUtils.EMPTY_SORTED_MAP;
        } else {
            SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
            for (String keyValues : attributesString.split(";")) {
                String[] tmp = keyValues.split("=");
                String key = tmp[0];
                SortedSet<String> values = new TreeSet<>(Arrays.asList(tmp[1].split("&")));
                attrs.put(key, values);
            }
            return attrs;
        }
    }

    private static SortedSet<Integer> convertIntegerString(String integersString) {
        if (integersString == null || integersString.isEmpty()) {
            return SetUtils.EMPTY_SORTED_SET;
        } else {
            SortedSet<Integer> integers = new TreeSet<>();
            for (String intString : integersString.split(",")) {
                integers.add(Integer.parseInt(intString));
            }
            return integers;
        }
    }

    private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()
            .append(DateTimeFormat.forPattern("yyyy-MM-dd"))
            .appendOptional(
                    new DateTimeFormatterBuilder()
                    .appendLiteral(' ')
                    .append(DateTimeFormat.forPattern("HH:mm:ss"))
                    .appendOptional(
                            new DateTimeFormatterBuilder()
                            .append(DateTimeFormat.forPattern(".SSS"))
                            .toParser()
                    )
                    .append(DateTimeFormat.forPattern("ZZ"))
                    .toParser())
            .toFormatter();

    private static Set<IusLimsKey> convertIusLimsKeyString(String iusLimsKeyString) {
        if (iusLimsKeyString == null || iusLimsKeyString.isEmpty()) {
            return Collections.EMPTY_SET;
        } else {

            Set<IusLimsKey> iusLimsKeys = new HashSet<>();
            for (String record : iusLimsKeyString.split(";")) {
                String[] vals = record.split(",");

                IusLimsKeyDto dto = new IusLimsKeyDto();
                dto.setIusSWID(Integer.parseInt(vals[0]));

                LimsKeyDto lk = new LimsKeyDto();
                lk.setProvider(vals[1]);
                lk.setId(vals[2]);
                lk.setVersion(vals[3]);
                lk.setLastModified(FMT.parseDateTime(vals[4]).toDateTime(DateTimeZone.UTC));
                dto.setLimsKey(lk);

                iusLimsKeys.add(dto);
            }
            return iusLimsKeys;
        }
    }

}
