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

import java.io.IOException;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceSqlResultDto;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceDAOHibernate extends HibernateDaoSupport implements AnalysisProvenanceDAO {

    private final String analysisProvenanceAllSql;

    public AnalysisProvenanceDAOHibernate() {
        try {
            analysisProvenanceAllSql = IOUtils.toString(this.getClass().getResourceAsStream("analysis_provenance_all.sql"), "UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public List<AnalysisProvenanceDto> list() {
        Session session = getSessionFactory().getCurrentSession();
        List<AnalysisProvenanceDto> dtos = session.createSQLQuery(analysisProvenanceAllSql)
                .setResultTransformer(Transformers.aliasToBean(AnalysisProvenanceSqlResultDto.class)).list();
        return dtos;
    }

}
