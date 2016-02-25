/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.webservice.resources.tables;

import java.util.Date;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;
import org.restlet.representation.Representation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class IusResourceTest extends DatabaseResourceTest {

    public IusResourceTest() {
        super("/ius");
    }

    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        Representation rep = null;
        try {
            IUS ius = new IUS();
            ius.setCreateTimestamp(new Date());
            ius.setSwAccession(6213);
            ius.setUpdateTimestamp(new Date());
            Sample s = new Sample();
            Lane l = new Lane();
            l.setLaneId(1);
            s.setSampleId(10);
            ius.setSample(s);
            ius.setLane(l);

            Document doc = XmlTools.marshalToDocument(new JaxbObject<IUS>(), ius);
            rep = resource.post(XmlTools.getRepresentation(doc));
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testPostWithLimsKey() {
        LimsKey lk = new LimsKey();
        lk.setId("1");
        lk.setLastModified(new DateTime());
        lk.setProvider("provider");
        lk.setVersion("1");

        try {
            Document doc = XmlTools.marshalToDocument(new JaxbObject<LimsKey>(), lk); //marshal to xml
            Representation rep = ClientResourceInstance.getChild("/limskey").post(XmlTools.getRepresentation(doc)); //post
            lk = (LimsKey) XmlTools.unMarshal(new JaxbObject<LimsKey>(), new LimsKey(), rep.getText());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        IUS ius = new IUS();
        ius.setLimsKey(lk);
        try {
            Document doc = XmlTools.marshalToDocument(new JaxbObject<IUS>(), ius); //object to xml
            Representation rep = resource.post(XmlTools.getRepresentation(doc)); //post
            ius = (IUS) XmlTools.unMarshal(new JaxbObject<IUS>(), new IUS(), rep.getText());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertNotNull(ius.getCreateTimestamp());
        assertNotNull(ius.getUpdateTimestamp());

        LimsKey returnedLimsKey = null;
        try {
            Representation rep = ClientResourceInstance.getChild("/ius/" + ius.getSwAccession() + "/limskey").get();
            returnedLimsKey = (LimsKey) XmlTools.unMarshal(new JaxbObject<LimsKey>(), new LimsKey(), rep.getText());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertEquals(lk, returnedLimsKey);
    }

    @Test()
    public void testPostWithMissingLimsKey() {
        LimsKey lk = new LimsKey();

        IUS ius = new IUS();
        ius.setLimsKey(lk); //set LimsKey that does not exist
        try {
            Document doc = XmlTools.marshalToDocument(new JaxbObject<IUS>(), ius); //object to xml
            Representation rep = resource.post(XmlTools.getRepresentation(doc)); //post
            ius = (IUS) XmlTools.unMarshal(new JaxbObject<IUS>(), new IUS(), rep.getText());
            rep.exhaust();
            rep.release();
            fail("Post should have failed - missing LimsKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
