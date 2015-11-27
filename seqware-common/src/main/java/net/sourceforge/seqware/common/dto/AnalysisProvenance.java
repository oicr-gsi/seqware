/*
 * Copyright (C) 2015 SeqWare
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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.sourceforge.seqware.common.dao.hibernate.type.IntegerArrayUserType;
import net.sourceforge.seqware.common.dao.hibernate.type.StringArrayUserType;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 *
 * @author mlaszloffy
 */
@TypeDefs({
    @TypeDef(
            name = "IntegerArrayUserType",
            typeClass = IntegerArrayUserType.class
    ),
    @TypeDef(
            name = "StringArrayUserType",
            typeClass = StringArrayUserType.class
    )
})
@Entity
@Immutable
public class AnalysisProvenance implements Serializable {

    @Id
    @Column(name = "id")
    Integer id;

    @Column(name = "lims_ids")
    @Type(type = "IntegerArrayUserType")
    Integer[] limsIds;

    @Column(name = "last_modified")
    String lastModified;

    @Column(name = "workflow_name")
    String workflowName;

    @Column(name = "workflow_version")
    String workflowVersion;

    @Column(name = "workflow_swid")
    Integer workflowSwid;

    @Column(name = "workflow_attrs")
    @Type(type = "StringArrayUserType")
    String[] workflowAttrs;

    @Column(name = "workflow_run_name")
    String workflowRunName;

    @Column(name = "workflow_run_status")
    String workflowRunStatus;

    @Column(name = "workflow_run_swid")
    Integer workflowRunSwid;

    @Column(name = "workflow_run_attrs")
    String workflowRunAttrs;

    @Column(name = "workflow_run_input_files_swids")
    @Type(type = "IntegerArrayUserType")
    Integer[] workflowRunInputFilesSwas;

    @Column(name = "processing_algorithm")
    String processingAlgorithm;

    @Column(name = "processing_swid")
    Integer processingSwa;

    @Column(name = "processing_status")
    String processingStatus;

    @Column(name = "processing_attrs")
    @Type(type = "StringArrayUserType")
    String[] processingAttrs;

    @Column(name = "file_meta_type")
    String fileMetaType;

    @Column(name = "file_swid")
    Integer fileSwa;

    @Column(name = "file_path")
    String filePath;

    @Column(name = "file_md5sum")
    String fileMd5sum;

    @Column(name = "file_size")
    String fileSize;

    @Column(name = "file_description")
    String fileDescription;

    @Column(name = "file_attrs")
    @Type(type = "StringArrayUserType")
    String[] fileAttrs;

}
