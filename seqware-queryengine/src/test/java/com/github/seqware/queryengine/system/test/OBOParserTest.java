/*
 * Copyright (C) 2012 SeqWare
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
package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.system.importers.OBOImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Let's try parsing OBO files and loading them into our back-end.
 *
 * @author dyuen
 */
public class OBOParserTest {

    @Test
    public void importOBOTest() {
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/so.obo");
        SGID tagSetID = OBOImporter.mainMethod(new String[]{file.getAbsolutePath()});
        TagSpecSet tagSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tagSetID);
        // count is 2135 if we do not omit obselete terms and check for duplicates
        Assert.assertTrue("loaded incorrect number of SO terms, expected 1935 found " + tagSet.getCount(), tagSet.getCount() == 1935);
        // check that a few known sequence tags are present
        Assert.assertTrue("tandem_repeat not found in tagset", tagSet.containsKey("tandem_repeat"));
        Assert.assertTrue("5KB_upstream_variant not found in tagset", tagSet.containsKey("5KB_upstream_variant"));
        Assert.assertTrue("intergenic_variant not found in tagset", tagSet.containsKey("intergenic_variant"));
        Assert.assertTrue("500B_downstream_variant not found in tagset", tagSet.containsKey("500B_downstream_variant"));
        // check that the tags are linked properly back to their tag set
        Tag tandem_repeat = tagSet.get("tandem_repeat");
        Tag upstream_variant = tagSet.get("5KB_upstream_variant");
        Tag intergenic_variant = tagSet.get("intergenic_variant");
        Tag downstream_variant = tagSet.get("500B_downstream_variant");
        Assert.assertTrue(tandem_repeat.getTagSet().equals(tagSet));
        Assert.assertTrue(upstream_variant.getTagSet().equals(tagSet));
        Assert.assertTrue(intergenic_variant.getTagSet().equals(tagSet));
        Assert.assertTrue(downstream_variant.getTagSet().equals(tagSet));
        // check that tags built from these specifications are linked properly back to their tag set
        Tag build1 = tandem_repeat.toBuilder().build();
        //Tag build2 = tandem_repeat.toBuilder().setKey("new key").build(); // we do not allow changing keys
        Tag build3 = tandem_repeat.toBuilder().setPredicate("!=").build();
        Tag build4 = tandem_repeat.toBuilder().setValue("SO:00000000").build();
        
        Assert.assertTrue(build1.getTagSet().equals(tagSet));
        //Assert.assertTrue(build2.getTagSet().equals(tagSet));
        Assert.assertTrue(build3.getTagSet().equals(tagSet));
        Assert.assertTrue(build4.getTagSet().equals(tagSet));
        
    }
}
