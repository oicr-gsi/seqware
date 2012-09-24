package net.sourceforge.seqware.common.metadata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;

import org.apache.log4j.Logger;

/**
 * 
 * @author boconnor@oicr.on.ca
 * 
 *         This Metadata object essentially does nothing. It returns null, 0, or
 *         a successful ReturnValue for all methods. This lets us do absolutely
 *         no metadata writeback with objects that expect a validate Metadata
 *         object. Keep in mind this may break code that assumes it's talking to
 *         a Database- or WebService-backed Metadata object!
 * 
 */
public class MetadataNoConnection extends Metadata {

  private Logger logger = Logger.getLogger(MetadataNoConnection.class);

  @Override
  public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    List<ReturnValue> list = new ArrayList<ReturnValue>();
    list.add(finished);
    return list;

  }

  @Override
  public List<ReturnValue> findFilesAssociatedWithASample(String sampleName) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    List<ReturnValue> list = new ArrayList<ReturnValue>();
    list.add(finished);
    return list;

  }

  // FIXME: Need to tune these statements in case of null values. Need to figure
  // what we exactly need
  // FIXME: to require in a ReturnValue and gracefully exit on missing required
  // value.
  /**
   * Find out the primary key for the last inserted record FIXME: This is
   * hardcoded for Postgres, need to make DB agnostic
   * 
   * @param SequenceID
   * @return
   * @throws SQLException
   */
  public int InsertAndReturnNewPrimaryKey(String sqlQuery, String SequenceID) throws SQLException {
    logger.info("No metadata connection");
    return (0);
  }

  @Override
  public ReturnValue addStudy(String title, String description, String accession, StudyType studyType,
      String centerName, String centerProjectName, Integer studyTypeId) {
    logger.info("No metadata connection");
    return (new ReturnValue(ReturnValue.SUCCESS));
  }

  public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title) {
    logger.info("No metadata connection");
    return (new ReturnValue(ReturnValue.SUCCESS));
  }

  public ReturnValue addSample(Integer experimentAccession, Integer organismId, String description, String title) {
    logger.info("No metadata connection");
    return (new ReturnValue(ReturnValue.SUCCESS));
  }

  // FIXME: This should all be a transaction. For now, we end up with cruft in
  // the DB if something failed.
  /*
   * FIXME: instead of taking in parentID's here, need to take in tubles to
   * discuss the relationship. Different types of relationships: match1 ->
   * variant1 is process match -> variant is algorithm match -> match1, match2,
   * etc is subprocess
   */
  @Override
  public ReturnValue add_empty_processing_event(int[] parentIDs) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  /**
   * This maps processing_id to sw_accession for that event.
   * 
   * @param processingId
   * @return sw_accession for that processingId
   */
  public int mapProcessingIdToAccession(int processingId) {
    logger.info("No metadata connection");
    return (0);
  }

  /**
   * TODO: needs to support more relationship types, but will need to add to the
   * SQL schema to support this
   * 
   * @param workflowRunId
   * @param parentAccession
   * @return
   * @throws SQLException
   */
  public boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException {
    logger.info("No metadata connection");
    return (true);
  }

  public boolean linkAccessionAndParent(int accession, int processingID) throws SQLException {
    logger.info("No metadata connection");
    return (true);
  }

  @Override
  public ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs,
      String algorithm, String description) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  public ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  /*
   * FIXME: this should check if association is already made, to make duplicates
   * impossible
   */
  public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  public ReturnValue update_processing_status(int processingID, String status) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;

  }

  public int add_workflow_run(int workflowAccession) {
    logger.info("No metadata connection");
    return (0);
  }

  public int get_workflow_run_accession(int workflowRunId) {
    logger.info("No metadata connection");
    return (0);
  }

  public int get_workflow_run_id(int workflowRunAccession) {
    logger.info("No metadata connection");
    return (0);
  }

  @Override
  public WorkflowRun getWorkflowRun(int workflowRunAccession) {
    return null;
  }

  public void add_workflow_run_ancestor(int workflowRunAccession, int processingId) {
    logger.info("No metadata connection");
  }

  @Override
  public ReturnValue update_processing_workflow_run(int processingID, int workflowRunAccession) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;

  }

  @Override
  public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, String status,
      String statusCmd, String workingDirectory, String dax, String ini, String host, int currStep, int totalSteps,
      String stdErr, String stdOut) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;

  }

  @Override
  public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  /**
   * Connect to a database for future use
   */
  @Override
  public ReturnValue init(String database, String username, String password) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  public ReturnValue clean_up() {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;
  }

  @Override
  public ArrayList<String> fix_file_paths(String prefix, ArrayList<String> files) {
    logger.info("No metadata connection");
    return new ArrayList<String>();
  }

  public ReturnValue addWorkflow(String name, String version, String description, String baseCommand,
      String configFile, String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip,
      boolean storeArchiveZip) {
    logger.info("No metadata connection");
    ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    finished.setExitStatus(ReturnValue.SUCCESS);
    return finished;

  }

  public Map<String, String> get_workflow_info(int workflowAccession) {
    logger.info("No metadata connection");
    HashMap<String, String> map = new HashMap<String, String>();
    return (map);

  }

  @Override
  public ReturnValue saveFileForIus(int workflowRunId, int iusAccession, FileMetadata file) {
    logger.info("No metadata connection");
    return new ReturnValue();
  }

  @Override
  public Boolean isDuplicateFile(String filepath) {
    logger.info("No metadata connection");
    return false;
  }

  @Override
  public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
    logger.info("No metadata connection");
    return new ReturnValue();
  }

  @Override
  public String listInstalledWorkflows() {
    logger.info("No metadata connection");
    return "";
  }

  @Override
  public String listInstalledWorkflowParams(String workflowAccession) {
    logger.info("No metadata connection");
    return "";
  }

  @Override
  public int getWorkflowAccession(String name, String version) {
    logger.info("No metadata connection");
    return 1;
  }

  @Override
  public List<ReturnValue> findFilesAssociatedWithASequencerRun(String sequencerRunName) {
    logger.info("No metadata connection");
    return new ArrayList<ReturnValue>();
  }

  public List<WorkflowRun> getWorkflowRunsByStatus(String status) {
    logger.info("No metadata connection");
    return new ArrayList<WorkflowRun>();
  }

  public List<WorkflowRun> getWorkflowRunsByHost(String host) {
    logger.info("No metadata connection");
    return new ArrayList<WorkflowRun>();
  }

  /**
   * 
   * @param workflowRunAccession
   * @return
   */
  @Override
  public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
    return (null);
  }

  public List<Study> getAllStudies() {
    logger.info("No metadata connection");
    return new ArrayList<Study>();
  }

  public String getSequencerRunReport() {
    logger.info("No metadata connection");
    return (null);
  }

  @Override
  public void annotateIUS(int iusSWID, IUSAttribute iusAtt, Boolean skip) {
    logger.info("No metadata connection");
    return;
  }

  @Override
  public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
    logger.info("No metadata connection");
    return;
  }

  @Override
  public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
    logger.info("No metadata connection");
    return;
  }

  @Override
  public void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip) {
    logger.info("No metadata connection");
  }

  @Override
  public void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip) {
    logger.info("No metadata connection");
  }

  @Override
  public void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip) {
    logger.info("No metadata connection");
  }

  @Override
  public void annotateStudy(int studySWID, StudyAttribute att, Boolean skip) {
    logger.info("No metadata connection");
  }

  @Override
  public String getWorkflowRunReport(int workflowRunSWID) {
    logger.info("No metadata connection");
    return "";
  }

  @Override
  public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
    logger.info("No metadata connection");
    return "";
  }

  @Override
  public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
    logger.info("No metadata connection");
    return "";
  }

  public net.sourceforge.seqware.common.model.File getFile(int swAccession) {
    return new net.sourceforge.seqware.common.model.File();
  }

  @Override
  public SortedSet<WorkflowParam> getWorkflowParams(String swAccession) {
    logger.info("No metadata connection");
    return new TreeSet<WorkflowParam>();
  }

  @Override
  public void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts) {
    // TODO Auto-generated method stub

  }
}
