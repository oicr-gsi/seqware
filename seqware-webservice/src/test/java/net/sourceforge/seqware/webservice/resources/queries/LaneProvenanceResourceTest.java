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
package net.sourceforge.seqware.webservice.resources.queries;

import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.restlet.representation.Representation;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceResourceTest extends DatabaseResourceTest {

    public LaneProvenanceResourceTest() {
        super("/reports/lane-provenance");
    }

    @Ignore
    @Override
    public void testDelete() {
    }

    @Ignore
    @Override
    public void testPost() {
    }

    @Ignore
    @Override
    public void testPut() {
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
        LaneProvenanceDtoList list = new LaneProvenanceDtoList();
        try {
            Representation rep = ClientResourceInstance.getChild("/reports/lane-provenance").get();
            list = (LaneProvenanceDtoList) XmlTools.unMarshal(new JaxbObject<>(), new LaneProvenanceDtoList(), rep.getText());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        Assert.assertEquals(30, list.getLaneProvenanceDtos().size());
    }

}
