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

import java.util.Map;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import static net.sourceforge.seqware.webservice.resources.BasicRestlet.queryMap;
import org.restlet.resource.Get;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResource extends BasicResource {

    @Get
    public void getXml() {
        Map<String, String[]> params = queryMap(getRequest());

        AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();

        JaxbObject<AnalysisProvenanceDtoList> jaxbTool = new JaxbObject<>();
        AnalysisProvenanceDtoList list = new AnalysisProvenanceDtoList();
        list.setAnalysisProvenanceDtos(service.list());
        Document line = XmlTools.marshalToDocument(jaxbTool, list);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

}
