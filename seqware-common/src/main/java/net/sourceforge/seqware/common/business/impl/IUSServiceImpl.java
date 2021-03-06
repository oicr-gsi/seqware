package net.sourceforge.seqware.common.business.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.err.DataIntegrityException;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * IUSServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class IUSServiceImpl implements IUSService {

    private IUSDAO dao = null;
    private FileDAO fileDAO = null;
    private final Logger log;

    /**
     * <p>
     * Constructor for IUSServiceImpl.
     * </p>
     */
    public IUSServiceImpl() {
        super();
        log = LoggerFactory.getLogger(IUSServiceImpl.class);
    }

    /** {@inheritDoc} */
    @Override
    public void setIUSDAO(IUSDAO dao) {
        this.dao = dao;
    }

    /**
     * <p>
     * Setter for the field <code>fileDAO</code>.
     * </p>
     * 
     * @param fileDAO
     *            a {@link net.sourceforge.seqware.common.dao.FileDAO} object.
     */
    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    /**
     * {@inheritDoc}
     * 
     * @param obj
     * @return
     */
    @Override
    public Integer insert(IUS obj) {
        obj.setCreateTimestamp(new Date());
        return (dao.insert(obj));
    }

    /**
     * {@inheritDoc}
     * 
     * @param ius
     * @param deleteRealFiles
     */
    @Override
    public void delete(IUS ius, boolean deleteRealFiles) {
        List<File> deleteFiles = null;
        if (deleteRealFiles) {
            deleteFiles = dao.getFiles(ius.getIusId());
        }

        ius.getSample().getIUS().remove(ius);
        ius.getLane().getIUS().remove(ius);

        Set<Processing> processings = ius.getProcessings();
        for (Processing processing : processings) {
            processing.getIUS().remove(ius);
        }

        dao.delete(ius);

        if (deleteRealFiles) {
            fileDAO.deleteAllWithFolderStore(deleteFiles);
        }
    }

    @Override
    public void delete(IUS ius) throws DataIntegrityException {
        if (ius.getSample() != null
                || ius.getLane() != null
                || ius.getLimsKey() != null
                || !ius.getProcessings().isEmpty()
                || !ius.getWorkflowRuns().isEmpty()) {
            throw new DataIntegrityException("The IUS = [" + ius.getSwAccession() + "]" + "has references to other entities");
        }
        if (ius.getOwner() != null) {
            ius.setOwner(null);
        }

        dao.delete(ius);
    }

    /**
     * {@inheritDoc}
     * 
     * @param obj
     */
    @Override
    public void update(IUS obj) {
        dao.update(obj);
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer iusId) {
        return dao.getFiles(iusId);
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer iusId, String metaType) {
        return dao.getFiles(iusId, metaType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer iusId, String metaType) {
        return dao.isHasFile(iusId, metaType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer iusId) {
        return dao.isHasFile(iusId);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<IUS> setWithHasFile(SortedSet<IUS> list) {
        for (IUS ius : list) {
            ius.setIsHasFile(isHasFile(ius.getIusId()));
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<IUS> listWithHasFile(SortedSet<IUS> list, String metaType) {
        SortedSet<IUS> result = new TreeSet<>();
        for (IUS ius : list) {
            boolean isHasFile = isHasFile(ius.getIusId(), metaType);
            if (isHasFile) {
                // logger.debug("ADD IUS");
                ius.setIsHasFile(true);
                result.add(ius);
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public IUS findByID(Integer id) {
        IUS ius = null;
        if (id != null) {
            try {
                ius = dao.findByID(id);
            } catch (Exception exception) {
                log.error("Cannot find IUS by id " + id);
                log.error(exception.getMessage());
            }
        }
        return ius;
    }

    /** {@inheritDoc} */
    @Override
    public IUS findBySWAccession(Integer swAccession) {
        IUS ius = null;
        if (swAccession != null) {
            try {
                ius = dao.findBySWAccession(swAccession);
            } catch (Exception exception) {
                log.error("Cannot find IUS by swAccession " + swAccession);
                log.error(exception.getMessage());
            }
        }
        return ius;
    }

    /** {@inheritDoc} */
    @Override
    public List<IUS> findByOwnerID(Integer registrationId) {
        List<IUS> ius = null;
        if (registrationId != null) {
            try {
                ius = dao.findByOwnerID(registrationId);
            } catch (Exception exception) {
                log.error("Cannot find IUS by registrationId " + registrationId);
                log.error(exception.getMessage());
            }
        }
        return ius;
    }

    /** {@inheritDoc} */
    @Override
    public List<IUS> findByCriteria(String criteria, boolean isCaseSens) {
        return dao.findByCriteria(criteria, isCaseSens);
    }

    /** {@inheritDoc} */
    @Override
    public IUS updateDetached(IUS ius) {
        return dao.updateDetached(ius);
    }

    /** {@inheritDoc} */
    @Override
    public List<IUS> findBelongsToStudy(Study study) {
        return dao.findBelongsToStudy(study);
    }

    /** {@inheritDoc} */
    @Override
    public List<IUS> list() {
        return dao.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, IUS ius) {
        dao.update(registration, ius);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public Integer insert(Registration registration, IUS ius) {
        ius.setCreateTimestamp(new Date());
        return (dao.insert(registration, ius));
    }

    /** {@inheritDoc} */
    @Override
    public IUS updateDetached(Registration registration, IUS ius) {
        return dao.updateDetached(registration, ius);
    }

    /** {@inheritDoc} */
    @Override
    public List<IUS> find(String sequencerRunName, Integer lane, String sampleName) {
        checkNotNull(sequencerRunName);
        checkNotNull(lane);
        checkState(lane > 0, "lane must greater than 0");
        return dao.find(sequencerRunName, lane, sampleName);
    }
}
