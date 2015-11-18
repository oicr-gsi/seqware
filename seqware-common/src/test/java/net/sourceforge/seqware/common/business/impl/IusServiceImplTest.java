package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>
 * IusServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class IusServiceImplTest extends AbstractTestCase {

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    /**
     * <p>
     * testAssociatedWorkflowRuns.
     * </p>
     */
    @Test
    public void testAssociatedWorkflowRuns() {
        IUS ius = iusService.findByID(4);
        Set<WorkflowRun> iuses = ius.getWorkflowRuns();
        Log.info("Count " + iuses.size());
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // List<IUS> found = iusService.findByCriteria("Test IUS 2", true);
        // assertEquals(1, found.size());

        // Case sensitive search
        // Make sure that there is no data with name in lower case
        // found = iusService.findByCriteria("test ius 2", true);
        // assertEquals(0, found.size());
        // Insensitive search
        // found = iusService.findByCriteria("test ius 2", false);
        // assertEquals(1, found.size());
        // Test SW accession
        List<IUS> found = iusService.findByCriteria("4765", false);
        assertEquals(1, found.size());
    }
}
