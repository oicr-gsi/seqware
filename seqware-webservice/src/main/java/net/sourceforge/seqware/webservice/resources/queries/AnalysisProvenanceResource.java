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
package net.sourceforge.seqware.webservice.resources.queries;

import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.resource.Get;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.CachedDocumentAutoUpdate;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResource extends BasicResource {

    private static final Logger LOG = Logger.getLogger(AnalysisProvenanceResource.class);

    private static final CachedDocumentAutoUpdate CACHE = new CachedDocumentAutoUpdate() {
        @Override
        public Document calculateDocument() {
            LOG.info("Updating analysis provenance");
            AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();
            JaxbObject<AnalysisProvenanceDtoList> jaxbTool = new JaxbObject<>();
            AnalysisProvenanceDtoList list = new AnalysisProvenanceDtoList();
            list.setAnalysisProvenanceDtos(service.list());
            return XmlTools.marshalToDocument(jaxbTool, list);
        }

    };

    @Get
    public void getXml() {
        //only allows one request to trigger update - all subsequent requests that occur during the current update will share the same result
        CACHE.processRequest(getResponse());
    }

}
