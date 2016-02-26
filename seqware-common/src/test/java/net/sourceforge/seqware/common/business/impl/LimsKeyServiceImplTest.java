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
package net.sourceforge.seqware.common.business.impl;

import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.model.LimsKey;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import net.sourceforge.seqware.common.business.LimsKeyService;
import org.joda.time.DateTime;

/**
 *
 * @author mlaszloffy
 */
// @ContextConfiguration("classpath:test-data-source.xml")
public class LimsKeyServiceImplTest extends AbstractTestCase {

    @Autowired
//    @Qualifier("LimsKeyService")
    LimsKeyService limsKeyService;

    public LimsKeyServiceImplTest() {
    }

    @Test
    public void testNotFound() {
        LimsKey key = limsKeyService.findByID(-1);
        assertNull(key);
    }

    @Test
    public void testInsert() {
        LimsKey key = new LimsKey();
        key.setProvider("1");
        key.setId("1");
        key.setVersion("1");
        key.setLastModified(DateTime.parse("2016-01-01T00:00:00Z"));

        Integer swid = limsKeyService.insert(key);
        LimsKey keyFoundBySwid = limsKeyService.findBySWAccession(swid);
        assertNotNull(keyFoundBySwid);
        assertEquals(key.getId(), keyFoundBySwid.getId());
        assertEquals(key.getLastModified(), keyFoundBySwid.getLastModified());

        LimsKey keyFoundById = limsKeyService.findByID(keyFoundBySwid.getLimsKeyId());
        assertEquals(keyFoundBySwid, keyFoundById);
    }

}
