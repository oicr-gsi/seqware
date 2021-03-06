package net.sourceforge.seqware.queryengine.webservice.controller;

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sourceforge.seqware.queryengine.webservice.security.SeqWareVerifier;
import net.sourceforge.seqware.webservice.resources.SeqwareAccessionIDResource;
import net.sourceforge.seqware.webservice.resources.SeqwareAccessionResource;
import net.sourceforge.seqware.webservice.resources.filters.ExperimentIDFilter;
import net.sourceforge.seqware.webservice.resources.filters.IUSIDFilter;
import net.sourceforge.seqware.webservice.resources.filters.LaneIDFilter;
import net.sourceforge.seqware.webservice.resources.filters.SampleIDFilter;
import net.sourceforge.seqware.webservice.resources.queries.AnalysisProvenanceResource;
import net.sourceforge.seqware.webservice.resources.queries.EnvironmentResource;
import net.sourceforge.seqware.webservice.resources.queries.FileProvenanceResource;
import net.sourceforge.seqware.webservice.resources.queries.LaneProvenanceResource;
import net.sourceforge.seqware.webservice.resources.queries.ProcessIdProcessResource;
import net.sourceforge.seqware.webservice.resources.queries.RunWorkflowResource;
import net.sourceforge.seqware.webservice.resources.queries.SampleHierarchyResource;
import net.sourceforge.seqware.webservice.resources.queries.SampleProvenanceResource;
import net.sourceforge.seqware.webservice.resources.queries.TriggerFileProvenanceResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowReportResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowRunIDProcessingsResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowRunIDWorkflowResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowRunIdFilesResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowRunReportResource;
import net.sourceforge.seqware.webservice.resources.queries.WorkflowRuntimeResource;
import net.sourceforge.seqware.webservice.resources.tables.ExperimentIDResource;
import net.sourceforge.seqware.webservice.resources.tables.ExperimentLibraryDesignResource;
import net.sourceforge.seqware.webservice.resources.tables.ExperimentResource;
import net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignReadSpecResource;
import net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignResource;
import net.sourceforge.seqware.webservice.resources.tables.FileChildLimitedWorkflowRunsResource;
import net.sourceforge.seqware.webservice.resources.tables.FileChildWorkflowRunsResource;
import net.sourceforge.seqware.webservice.resources.tables.FileIDResource;
import net.sourceforge.seqware.webservice.resources.tables.FileLinkReportResource;
import net.sourceforge.seqware.webservice.resources.tables.FileResource;
import net.sourceforge.seqware.webservice.resources.tables.FileReverseHierarchyDisplayResource;
import net.sourceforge.seqware.webservice.resources.tables.IusIDResource;
import net.sourceforge.seqware.webservice.resources.tables.IusResource;
import net.sourceforge.seqware.webservice.resources.tables.LaneIDResource;
import net.sourceforge.seqware.webservice.resources.tables.LaneResource;
import net.sourceforge.seqware.webservice.resources.tables.LibrarySelectionResource;
import net.sourceforge.seqware.webservice.resources.tables.LibrarySourceResource;
import net.sourceforge.seqware.webservice.resources.tables.LibraryStrategyResource;
import net.sourceforge.seqware.webservice.resources.tables.LimsKeyResource;
import net.sourceforge.seqware.webservice.resources.tables.OrganismResource;
import net.sourceforge.seqware.webservice.resources.tables.PlatformResource;
import net.sourceforge.seqware.webservice.resources.tables.ProcessIDResource;
import net.sourceforge.seqware.webservice.resources.tables.ProcessResource;
import net.sourceforge.seqware.webservice.resources.tables.ProcessingStructureResource;
import net.sourceforge.seqware.webservice.resources.tables.RootSampleResource;
import net.sourceforge.seqware.webservice.resources.tables.SampleIDResource;
import net.sourceforge.seqware.webservice.resources.tables.SampleResource;
import net.sourceforge.seqware.webservice.resources.tables.SequencerRunIDResource;
import net.sourceforge.seqware.webservice.resources.tables.SequencerRunResource;
import net.sourceforge.seqware.webservice.resources.tables.StudyIDResource;
import net.sourceforge.seqware.webservice.resources.tables.StudyResource;
import net.sourceforge.seqware.webservice.resources.tables.StudyTypeResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowIDResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowParamIDResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowParamResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowParamValueIDResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowParamValueResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowRunIDResource;
import net.sourceforge.seqware.webservice.resources.tables.WorkflowRunResource;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

/**
 * <p>
 * SeqWareWebServiceApplication class.
 * </p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class SeqWareWebServiceApplication extends WadlApplication {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     *
     * @return a {@link org.restlet.Restlet} object.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        final Component component = new Component();
        component.getClients().add(Protocol.CLAP);

        ChallengeAuthenticator guard = getGuard();

        // String rootURL = "";
        // if (EnvUtil.getProperty("urlhack") != null) { rootURL = EnvUtil.getProperty("urlhack"); }

        // We don't want to use CGLIB since it is a huge memory hog
        // see for more information: http://beanlib.svn.sourceforge.net/viewvc/beanlib/trunk/beanlib-doc/faq.html
        UnEnhancer.setDefaultCheckCGLib(false);

        String version = "queryengine";
        // if (EnvUtil.getProperty("version") != null) { rootURL = EnvUtil.getProperty("version"); }

        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());
        router.setDefaultMatchingQuery(false);

        router.setRoutingMode(Router.MODE_LAST_MATCH);

        // I don't know if this is needed anymore
        getConnectorService().getClientProtocols().add(Protocol.FILE);

        Restlet slashRedirect = new OptionalSlashRedirect(getContext());

        router.attachDefault(EnvironmentResource.class);
        /*
         * New version of webservices
         */
        router.attach("/SWA/", SeqwareAccessionResource.class);
        router.attach("/SWA/{SWA}", SeqwareAccessionIDResource.class);

        router.attach("/experiments", ExperimentResource.class);
        router.attach("/experiments/", slashRedirect);
        router.attach("/experiments/{experimentId}", ExperimentIDResource.class);
        router.attach("/experiments/{experimentId}/samples", SampleIDFilter.class);
        // router.attach("/experiments/{ID}/processes", Resource.class);
        // router.attach("/experiments/{ID}/processes/{ID}", Resource.class);
        //

        // TODO: make sure sequencer run, lane, and IUS have post methods!

        router.attach("/platforms", PlatformResource.class);
        router.attach("/platforms/", slashRedirect);
        router.attach("/studytypes", StudyTypeResource.class);
        router.attach("/studytypes/", slashRedirect);
        router.attach("/libraryselections", LibrarySelectionResource.class);
        router.attach("/libraryselections/", slashRedirect);
        router.attach("/librarysources", LibrarySourceResource.class);
        router.attach("/librarysources/", slashRedirect);
        router.attach("/librarystrategies", LibraryStrategyResource.class);
        router.attach("/librarystrategies/", slashRedirect);
        router.attach("/organisms", OrganismResource.class);
        router.attach("/organisms/", slashRedirect);
        router.attach("/experimentspotdesigns", ExperimentSpotDesignResource.class);
        router.attach("/experimentspotdesigns/", slashRedirect);
        router.attach("/experimentlibrarydesigns", ExperimentLibraryDesignResource.class);
        router.attach("/experimentlibrarydesigns/", slashRedirect);
        router.attach("/experimentspotdesignreadspecs", ExperimentSpotDesignReadSpecResource.class);
        router.attach("/experimentspotdesignreadspecs/", slashRedirect);

        router.attach("/files", FileResource.class);
        router.attach("/files/", slashRedirect);
        router.attach("/files/{fileId}", FileIDResource.class);

        router.attach("/limskey", LimsKeyResource.class);
        router.attach("/limskey/", slashRedirect);
        router.attach("/limskey/{limsKeyId}", LimsKeyResource.class);
        router.attach("/ius", IusResource.class);
        router.attach("/ius/", slashRedirect);
        router.attach("/ius/{iusId}", IusIDResource.class);
        router.attach("/ius/{iusId}/lane", LaneIDFilter.class);

        router.attach("/ius/{iusId}/{object}", IusIDResource.class);
        
        router.attach("/lanes", LaneResource.class);
        router.attach("/lanes/", slashRedirect);
        router.attach("/lanes/{laneId}", LaneIDResource.class);
        router.attach("/lanes/{laneId}/ius", IUSIDFilter.class);

        router.attach("/processes", ProcessResource.class);
        router.attach("/processes/", slashRedirect);
        router.attach("/processes/{processId}", ProcessIDResource.class);
        router.attach("/processes/{processId}/parents", new ProcessIdProcessResource(getContext()));
        // router.attach("/processes/{ID}/parents", Resource.class);
        // router.attach("/processes/{ID}/parents/{ID}", Resource.class);
        // router.attach("/processes/{ID}/children", Resource.class);
        // router.attach("/processes/{ID}/children/{ID}", Resource.class);
        //
        router.attach("/samples", SampleResource.class);
        router.attach("/samples/", slashRedirect);
        router.attach("/samples/{sampleId}", SampleIDResource.class);
        router.attach("/samples/{parentId}/children", SampleIDFilter.class);
        router.attach("/samples/{childId}/parents", SampleIDFilter.class);
        router.attach("/samples/{sampleId}/ius", IUSIDFilter.class);
        router.attach("/samples/root", RootSampleResource.class);
        router.attach("/samples/root/", slashRedirect);
        // router.attach("/samples/{ID}/processes", Resource.class);
        // router.attach("/samples/{ID}/processes/{ID}", Resource.class);
        //
        router.attach("/sequencerruns", SequencerRunResource.class);
        router.attach("/sequencerruns/", slashRedirect);
        router.attach("/sequencerruns/{sequencerRunId}", SequencerRunIDResource.class);
        router.attach("/sequencerruns/{sequencerRunId}/lanes", LaneIDFilter.class);
        // router.attach("/sequencer_runs/{ID}/lanes", Resource.class);
        // router.attach("/sequencer_runs/{ID}/lanes/{ID}", Resource.class);
        // router.attach("/sequencer_runs/{ID}/processes/", Resource.class);
        // router.attach("/sequencer_runs/{ID}/processes/{ID}", Resource.class);
        //
        router.attach("/studies", StudyResource.class);
        router.attach("/studies/", slashRedirect);
        router.attach("/studies/{studyId}", StudyIDResource.class);
        router.attach("/studies/{studyId}/experiments", ExperimentIDFilter.class);

        //
        router.attach("/workflowruns", WorkflowRunResource.class);
        router.attach("/workflowruns/", slashRedirect);
        router.attach("/workflowruns/{workflowRunId}", WorkflowRunIDResource.class);
        router.attach("/workflowruns/{workflowRunId}/files", new WorkflowRunIdFilesResource(getContext()));
        router.attach("/workflowruns/{workflowRunId}/processings", new WorkflowRunIDProcessingsResource(getContext()));
        router.attach("/workflowruns/{workflowRunId}/workflow", new WorkflowRunIDWorkflowResource(getContext()));

        router.attach("/workflows", WorkflowResource.class);
        router.attach("/workflows/", slashRedirect);
        router.attach("/workflows/{workflowId}", WorkflowIDResource.class);
        router.attach("/workflows/{workflowId}/runs", new RunWorkflowResource(getContext()));
        router.attach("/workflows/{workflowId}/runs/", slashRedirect);

        router.attach("/workflowparams", WorkflowParamResource.class);
        router.attach("/workflowparams/", slashRedirect);
        router.attach("/workflowparams/{workflowParamId}", WorkflowParamIDResource.class);
        router.attach("/workflowparamvalues", WorkflowParamValueResource.class);
        router.attach("/workflowparamvalues/", slashRedirect);
        router.attach("/workflowparamvalues/{workflowParamValueId}", WorkflowParamValueIDResource.class);

        /*
         * Reports
         */
        router.attach("/reports/file-provenance", new FileProvenanceResource(getContext()));
        router.attach("/reports/file-provenance/generate", new TriggerFileProvenanceResource(getContext()));

        router.attach("/reports/analysis-provenance", AnalysisProvenanceResource.class);
        router.attach("/reports/sample-provenance", SampleProvenanceResource.class);
        router.attach("/reports/sample-provenance/{operation}", SampleProvenanceResource.class);
        router.attach("/reports/lane-provenance", LaneProvenanceResource.class);
        router.attach("/reports/lane-provenance/{operation}", LaneProvenanceResource.class);

        // the following collides with the non-variable paths.
        // router.attach("/reports/studies/{studyId}", new CycleCheckResource(getContext()));
        router.attach("/reports/workflows/{workflowId}", new WorkflowReportResource(getContext()));

        WorkflowRunReportResource wrrr = new WorkflowRunReportResource(getContext());
        router.attach("/reports/workflowruns/{workflowRunId}", wrrr);
        router.attach("/reports/workflowruns", wrrr);
        router.attach("/reports/workflows/{workflowId}/runs", wrrr);
        router.attach("/reports/workflowruns/", slashRedirect);
        router.attach("/reports/workflows/{workflowId}/runs/", slashRedirect);
        router.attach("/reports/workflowruns/{workflowRunId}/stderr", wrrr);
        router.attach("/reports/workflowruns/{workflowRunId}/stdout", wrrr);
        router.attach("/reports/workflowruns/{workflowRunId}/stderr/", slashRedirect);
        router.attach("/reports/workflowruns/{workflowRunId}/stdout/", slashRedirect);

        // A report giving runtime info for workflows
        router.attach("/reports/workflowruntimes", new WorkflowRuntimeResource(getContext()));

        // A report giving workflow runs that are relevant for a group of files
        router.attach("/reports/fileworkflowruns", FileChildWorkflowRunsResource.class);
        router.attach("/reports/fileworkflowruns/", slashRedirect);
        router.attach("/reports/fileworkflowruns/limit", FileChildLimitedWorkflowRunsResource.class);
        router.attach("/reports/fileworkflowruns/limit/", slashRedirect);

        // STATIC COMPONENTS

        router.attach("/x/report/filelinkreport", FileLinkReportResource.class);
        router.attach("/x/report/filelinkreport/{swas}", FileLinkReportResource.class);
        router.attach("/x/report/reversehierarchy/{swa}", FileReverseHierarchyDisplayResource.class);

        // Directory directory = new Directory(getContext(), "war:///WEB-INF/html");
        // router.attachDefault(directory);
        // router.attach("/" + version + "/static", directory);

        router.attach("/processingstructure", new ProcessingStructureResource(getContext()));
        router.attach("/sample/parents", new SampleHierarchyResource(getContext()));
        guard.setNext(router);
        return guard;

    }

    private ChallengeAuthenticator getGuard() {
        // FIXME: double slash is an artifact of the groove proxy server
        // get the ROOT URL for various uses
        // String rootURL = "";
        // if (EnvUtil.getProperty("urlhack") != null) { rootURL = EnvUtil.getProperty("urlhack"); }
        // Guard the restlet with BASIC authentication.
        ChallengeAuthenticator guard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "SeqWare metadata Web service");
        // Instantiates a Verifier of identifier/secret couples based on a
        // simple Map.
        SeqWareVerifier verifier = new SeqWareVerifier();
        guard.setVerifier(verifier);
        return guard;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ApplicationInfo getApplicationInfo(Request request, Response response) {
        ApplicationInfo result = super.getApplicationInfo(request, response);

        DocumentationInfo docInfo = new DocumentationInfo("SeqWare Web Service Application");
        docInfo.setTitle("First resource sample application.");
        result.setDocumentation(docInfo);

        return result;
    }

    /**
     * http://restlet-discuss.1400322.n2.nabble.com/Proper-handling-of-at-the-end-of-the-requested-URI-td5819896.html
     */
    protected static class OptionalSlashRedirect extends Restlet {

        public OptionalSlashRedirect(Context context) {
            super(context);
        }

        public OptionalSlashRedirect() {
        }

        @Override
        public void handle(Request request, Response response) {
            super.handle(request, response);

            Method m = request.getMethod();
            if (m.equals(Method.GET) || m.equals(Method.HEAD)) {

                Reference ref = request.getOriginalRef().getTargetRef();
                String path = ref.getPath();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                } else {
                    path = path + "/";
                }
                ref.setPath(path);

                response.redirectPermanent(ref);

            } else {
                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            }
        }
    }
}
