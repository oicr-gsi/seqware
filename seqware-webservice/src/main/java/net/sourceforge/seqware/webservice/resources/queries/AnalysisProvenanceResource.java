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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import static net.sourceforge.seqware.webservice.resources.BasicRestlet.queryMap;
import org.restlet.resource.Get;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResource extends BasicResource {

    private static Document document;
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private final Logger log = Logger.getLogger(AnalysisProvenanceResource.class);

    @Get
    public void getXml() {
        Map<String, String[]> params = queryMap(getRequest());

        if (LOCK.writeLock().tryLock()) {
            try {
                log.info("Updating analysis provenance");
                AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();
                JaxbObject<AnalysisProvenanceDtoList> jaxbTool = new JaxbObject<>();
                AnalysisProvenanceDtoList list = new AnalysisProvenanceDtoList();
                list.setAnalysisProvenanceDtos(service.list());
                document = XmlTools.marshalToDocument(jaxbTool, list);
                getResponse().setEntity(XmlTools.getRepresentation(document));
            } finally {
                LOCK.writeLock().unlock();
            }
        } else {
            LOCK.readLock().lock();
            try {
                getResponse().setEntity(XmlTools.getRepresentation(document));
            } finally {
                LOCK.readLock().unlock();
            }
        }
    }
}
