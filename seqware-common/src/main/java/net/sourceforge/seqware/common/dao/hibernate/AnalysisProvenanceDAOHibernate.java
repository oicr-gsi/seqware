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
package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import org.hibernate.transform.Transformers;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceDAOHibernate extends HibernateDaoSupport implements AnalysisProvenanceDAO {

    @Override
    public List<AnalysisProvenanceDto> list() {
        SQLQuery sq = getSession().createSQLQuery(
                "WITH modified_records AS (\n"
                + "--get files (and associated workflow runs) that have been modified\n"
                + "(SELECT wr.workflow_run_id workflow_run_id, w.workflow_id workflow_id, p.processing_id processing_id, f.file_id file_id \n"
                + "	FROM file f, processing_files pf, processing p, workflow_run wr, workflow w\n"
                + "	WHERE f.file_id=pf.file_id \n"
                + "		AND pf.processing_id = p.processing_id \n"
                + "		AND COALESCE(p.workflow_run_id, p.ancestor_workflow_run_id) = wr.workflow_run_id \n"
                + "		AND wr.workflow_id = w.workflow_id \n"
                + ")\n"
                + "UNION\n"
                + "--get workflow runs (and associated files) that have been modified\n"
                + "(SELECT wr.workflow_run_id workflow_run_id, w.workflow_id workflow_id, FILES.processing_id processing_id, FILES.file_id file_id\n"
                + "	FROM workflow_run wr \n"
                + "	INNER JOIN workflow w ON wr.workflow_id = w.workflow_id \n"
                + "	LEFT JOIN \n"
                + "	(SELECT COALESCE(p.workflow_run_id, p.ancestor_workflow_run_id) workflow_run_id, p.processing_id, f.file_id\n"
                + "		FROM file f \n"
                + "		LEFT JOIN processing_files pf ON f.file_id = pf.file_id \n"
                + "		LEFT JOIN processing p ON pf.processing_id = p.processing_id) FILES \n"
                + "	ON wr.workflow_run_id = FILES.workflow_run_id\n"
                + ")\n"
                + ")\n"
                + "\n"
                + "SELECT row_number() over() id, coalesce(\n"
                + "	(SELECT case when array_agg(i.sw_accession) = '{NULL}' then NULL else array_agg(i.sw_accession) end lims_ids \n"
                + "		FROM processing_ius pi, ius i\n"
                + "		WHERE modified_records.processing_id = processing_id AND pi.ius_id = i.ius_id\n"
                + "		GROUP BY pi.processing_id),\n"
                + "	(SELECT case when array_agg(i.sw_accession) = '{NULL}' then NULL else array_agg(i.sw_accession) end lims_ids \n"
                + "		FROM ius_workflow_runs iwr, ius i\n"
                + "		WHERE modified_records.workflow_run_id = workflow_run_id AND iwr.ius_id = i.ius_id\n"
                + "		GROUP BY iwr.workflow_run_id)\n"
                + ") lims_ids,\n"
                + "greatest(p.update_tstmp, wr.update_tstmp, w.update_tstmp) last_modified,\n"
                + "w.name workflow_name,\n"
                + "w.version workflow_version,\n"
                + "w.sw_accession workflow_swid, \n"
                + "(SELECT array_agg(tag||'='||value) \n"
                + "	FROM workflow_attribute \n"
                + "	WHERE modified_records.workflow_id = workflow_id \n"
                + "	GROUP BY workflow_id) workflow_attrs, \n"
                + "wr.name workflow_run_name, \n"
                + "wr.status workflow_run_status, \n"
                + "wr.sw_accession workflow_run_swid,\n"
                + "(SELECT array_agg(tag||'='||value) \n"
                + "	FROM workflow_run_attribute \n"
                + "	WHERE modified_records.workflow_run_id = workflow_run_id \n"
                + "	GROUP BY workflow_run_id) workflow_run_attrs,\n"
                + "(SELECT array_agg(f.sw_accession) \n"
                + "	FROM workflow_run_input_files wrif \n"
                + "	LEFT JOIN file f ON wrif.file_id = f.file_id \n"
                + "	WHERE modified_records.workflow_run_id = wrif.workflow_run_id \n"
                + "	GROUP BY wrif.workflow_run_id) workflow_run_input_files_swids,\n"
                + "p.algorithm processing_algorithm, \n"
                + "p.sw_accession processing_swid, \n"
                + "p.status processing_status,\n"
                + "(SELECT array_agg(tag||'='||value) \n"
                + "	FROM processing_attribute \n"
                + "	WHERE modified_records.processing_id = processing_id \n"
                + "	GROUP BY processing_id) processing_attrs,\n"
                + "f.meta_type file_meta_type, \n"
                + "f.sw_accession file_swid, \n"
                + "f.file_path file_path, \n"
                + "f.md5sum file_md5sum, \n"
                + "f.size file_size, \n"
                + "f.description file_description, \n"
                + "(SELECT array_agg(tag||'='||value) \n"
                + "	FROM file_attribute \n"
                + "	WHERE modified_records.file_id = file_id \n"
                + "	GROUP BY file_id) file_attrs\n"
                + "FROM modified_records \n"
                + "LEFT JOIN file f ON modified_records.file_id = f.file_id \n"
                + "LEFT JOIN workflow_run wr ON modified_records.workflow_run_id = wr.workflow_run_id \n"
                + "LEFT JOIN workflow w ON modified_records.workflow_id = w.workflow_id \n"
                + "LEFT JOIN processing p ON modified_records.processing_id = p.processing_id"
        );
//
//        List<AnalysisProvenanceDto> aps = sq.addEntity(AnalysisProvenance.class).list();
//        return aps;
//        sq.addScalar(string)
        return sq.setResultTransformer(Transformers.aliasToBean(AnalysisProvenanceDto.class)).list();
    }

}
