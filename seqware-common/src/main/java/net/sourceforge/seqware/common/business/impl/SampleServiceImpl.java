package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>
 * SampleServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleServiceImpl implements SampleService {

    private SampleDAO sampleDAO = null;
    private FileDAO fileDAO = null;

    /**
     * <p>
     * Constructor for SampleServiceImpl.
     * </p>
     */
    public SampleServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Sets a private member variable with an instance of an implementation of SampleDAO. This method is called by the Spring framework at
     * run time.
     * 
     * @see SampleDAO
     */
    @Override
    public void setSampleDAO(SampleDAO sampleDAO) {
        this.sampleDAO = sampleDAO;
    }

    /**
     * Sets a private member variable with an instance of an implementation of FileDAO. This method is called by the Spring framework at run
     * time.
     * 
     * @param fileDAO
     *            implementation of FileDAO
     * @see FileDAO
     */
    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of Sample into the database.
     */
    @Override
    public Integer insert(Sample sample) {
        sample.setCreateTimestamp(new Date());
        return (sampleDAO.insert(sample));
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of Sample in the database.
     */
    @Override
    public void update(Sample sample) {
        sampleDAO.update(sample);
    }

    /**
     * {@inheritDoc}
     * 
     * Deletes an instance of Sample in the database.
     * 
     * @param sample
     * @param deleteRealFiles
     */
    @Override
    public void delete(Sample sample, boolean deleteRealFiles) {
        List<File> deleteFiles = null;
        if (deleteRealFiles) {
            deleteFiles = sampleDAO.getFiles(sample.getSampleId());
        }

        Set<Sample> parents = sample.getParents();
        for (Sample parent : parents) {
            parent.getChildren().remove(sample);
        }

        Set<Sample> children = sample.getChildren();
        for (Sample child : children) {
            child.getParents().remove(sample);
        }

        sampleDAO.delete(sample);

        if (deleteRealFiles) {
            fileDAO.deleteAllWithFolderStore(deleteFiles);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> getFiles(Integer sampleId) {
        return sampleDAO.getFiles(sampleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHasFile(Integer sampleId) {
        return sampleDAO.isHasFile(sampleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> getFiles(Integer studyId, String metaType) {
        return sampleDAO.getFiles(studyId, metaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHasFile(Integer studyId, String metaType) {
        return sampleDAO.isHasFile(studyId, metaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Sample> setWithHasFile(Integer expId, SortedSet<Sample> list) {
        /*
         * Map<Integer, Integer> countFiles = sampleDAO.getCountFiles(expId); for (Sample sample : list) { boolean isHasFile = false;
         * Integer count = countFiles.get(sample.getSampleId()); if(count > 0){ isHasFile = true; } sample.setIsHasFile(isHasFile); } return
         * list;
         */
        SortedSet<Sample> result = new TreeSet<>();
        for (Sample sample : list) {
            if (isHasFile(sample.getSampleId())) {
                sample.setIsHasFile(true);
                result.add(sample);
            }
            // experiment.setIsHasFile(isHasFile(experiment.getExperimentId(),
            // metaType));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Sample> listWithHasFile(Integer expId, SortedSet<Sample> list, String metaType) {
        /*
         * SortedSet<Sample> result = new TreeSet<Sample>(); Map<Integer, Integer> countFiles = sampleDAO.getCountFiles(expId, metaType);
         * for (Sample sample : list) { boolean isHasFile = false; Integer count = countFiles.get(sample.getSampleId()); if(count > 0){
         * isHasFile = true; sample.setIsHasFile(isHasFile); result.add(sample); } //sample.setIsHasFile(isHasFile); } return result;
         */
        SortedSet<Sample> result = new TreeSet<>();
        for (Sample sample : list) {
            if (isHasFile(sample.getSampleId(), metaType)) {
                sample.setIsHasFile(true);
                result.add(sample);
            }
            // experiment.setIsHasFile(isHasFile(experiment.getExperimentId(),
            // metaType));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample findByTitle(String title) {
        Sample sample = null;
        if (title != null) {
            try {
                sample = sampleDAO.findByTitle(title.trim());
            } catch (Exception exception) {
                Log.debug("Cannot find Sample by title " + title, exception);
            }
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of Sample in the database by the Sample emailAddress, and copies the Sample properties to an instance of Sample.
     */
    @Override
    public Sample findByName(String name) {
        Sample sample = null;
        if (name != null) {
            try {
                sample = sampleDAO.findByName(name.trim());
            } catch (Exception exception) {
                Log.debug("Cannot find Sample by name " + name, exception);
            }
        }
        return sample;
    }

    @Override
    public List<Sample> matchName(String name) {
        List<Sample> sample = null;
        if (name != null) {
            try {
                sample = sampleDAO.matchName(name.trim());
            } catch (Exception exception) {
                Log.debug("Cannot find matches for name " + name, exception);
            }
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     * 
     * @param sampleId
     */
    @Override
    public Sample findByID(Integer sampleId) {
        Sample sample = null;
        if (sampleId != null) {
            try {
                sample = sampleDAO.findByID(sampleId);
            } catch (Exception exception) {
                Log.error("Cannot find Sample by expID " + sampleId, exception);
            }
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample findBySWAccession(Integer swAccession) {
        Sample sample = null;
        if (swAccession != null) {
            try {
                sample = sampleDAO.findBySWAccession(swAccession);
            } catch (Exception exception) {
                Log.error("Cannot find Sample by swAccession " + swAccession, exception);
            }
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> findByOwnerID(Integer registrationId) {
        List<Sample> samples = null;
        if (registrationId != null) {
            try {
                samples = sampleDAO.findByOwnerID(registrationId);
            } catch (Exception exception) {
                Log.error("Cannot find Sample by registrationId " + registrationId, exception);
            }
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> findByCriteria(String criteria, boolean isCaseSens) {
        return sampleDAO.findByCriteria(criteria, isCaseSens);
    }

    /**
     * {@inheritDoc}
     * 
     * Determines if an email address has already been used.
     */
    @Override
    public boolean hasNameBeenUsed(String oldName, String newName) {
        boolean nameUsed = false;
        boolean checkName = true;

        if (newName != null) {

            if (oldName != null) {
                /*
                 * We do not want to check if an name address has been used if the user is updating an existing sample and has not changed
                 * the nameAddress.
                 */
                checkName = !newName.trim().equalsIgnoreCase(oldName.trim());
            }

            if (checkName) {
                Sample sample = this.findByName(newName.trim());
                if (sample != null) {
                    nameUsed = true;
                }
            }
        }
        return nameUsed;
    }

    /**
     * <p>
     * listComplete.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Sample> listComplete() {
        return sampleDAO.listComplete();
    }

    /**
     * <p>
     * listIncomplete.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Sample> listIncomplete() {
        return sampleDAO.listIncomplete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> listSample(Registration registration) {
        return sampleDAO.listSample(registration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample updateDetached(Sample sample) {
        return sampleDAO.updateDetached(sample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getRootSamples(Study study) {
        return sampleDAO.getRootSamples(study);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample getRootSample(Sample childSample) {
        return sampleDAO.getRootSample(childSample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> list() {
        return sampleDAO.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Registration registration, Sample sample) {
        sampleDAO.update(registration, sample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer insert(Registration registration, Sample sample) {
        sample.setCreateTimestamp(new Date());
        return (sampleDAO.insert(registration, sample));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample updateDetached(Registration registration, Sample sample) {
        return sampleDAO.updateDetached(registration, sample);
    }
}

// ex:sw=4:ts=4:
