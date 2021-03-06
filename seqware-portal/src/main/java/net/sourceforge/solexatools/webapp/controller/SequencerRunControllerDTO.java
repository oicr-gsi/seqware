package net.sourceforge.solexatools.webapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.solexatools.Debug;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * SequencerRunController
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunControllerDTO extends MultiActionController {
    private SequencerRunService sequencerRunService;
    private LaneService laneService;
    private Validator validator;

    /**
     * <p>
     * Constructor for SequencerRunControllerDTO.
     * </p>
     */
    public SequencerRunControllerDTO() {
        super();
    }

    /**
     * <p>
     * Constructor for SequencerRunControllerDTO.
     * </p>
     * 
     * @param delegate
     *            a {@link java.lang.Object} object.
     */
    public SequencerRunControllerDTO(Object delegate) {
        super(delegate);
    }

    /**
     * <p>
     * Getter for the field <code>sequencerRunService</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
     */
    public SequencerRunService getSequencerRunService() {
        return sequencerRunService;
    }

    /**
     * <p>
     * Setter for the field <code>sequencerRunService</code>.
     * </p>
     * 
     * @param sequencerRunService
     *            a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
     */
    public void setSequencerRunService(SequencerRunService sequencerRunService) {
        this.sequencerRunService = sequencerRunService;
    }

    /**
     * <p>
     * Getter for the field <code>validator</code>.
     * </p>
     * 
     * @return a {@link org.springframework.validation.Validator} object.
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * <p>
     * Setter for the field <code>validator</code>.
     * </p>
     * 
     * @param validator
     *            a {@link org.springframework.validation.Validator} object.
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Handles the user's request to submit a new experiment.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param command
     *            SequencerRun command object
     * @return ModelAndView
     * @throws java.lang.Exception
     *             if any.
     */
    public ModelAndView handleSubmit(HttpServletRequest request, HttpServletResponse response, SequencerRun command) throws Exception {

        Registration registration = Security.getRegistration(request);
        if (registration == null) return new ModelAndView("redirect:/login.htm");

        ModelAndView modelAndView = null;
        BindingResult errors = this.validateSequencerRun(request, command);
        if (errors.hasErrors()) {
            Map model = errors.getModel();
            modelAndView = new ModelAndView("SequencerRun1", model);
        } else {
            Debug.put("HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
            getSequencerRunService().insert(command);
            // command.setOwnerId(registration.getRegistrationId());
            modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
        }

        request.getSession(false).removeAttribute("sequencerRun");

        return modelAndView;
    }

    /**
     * Handles the user's request to reset the experiment page during a new or update experiment.
     * 
     * @param command
     *            SequencerRun command object
     * @param request
     *            a {@link javax.servlet.http.HttpServletRequest} object.
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     * @throws java.lang.Exception
     *             if any.
     */
    public ModelAndView handleReset(HttpServletRequest request, HttpServletResponse response, SequencerRun command) throws Exception {

        ModelAndView modelAndView = null;
        SequencerRun sequencerRun = getCurrentSequencerRun(request);
        if (sequencerRun.getUpdateTimestamp() == null) {
            modelAndView = new ModelAndView("SequencerRun");
        } else {
            modelAndView = new ModelAndView("SequencerRunUpdate");
        }
        request.setAttribute(getCommandName(command), sequencerRun);
        return modelAndView;
    }

    /**
     * Handles the user's request to cancel the experiment or the experiment update page.
     * 
     * @param command
     *            SequencerRun command object
     * @param request
     *            a {@link javax.servlet.http.HttpServletRequest} object.
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     * @throws java.lang.Exception
     *             if any.
     */
    public ModelAndView handleCancel(HttpServletRequest request, HttpServletResponse response, SequencerRun command) throws Exception {

        return new ModelAndView("redirect:/Welcome.htm");
    }

    /**
     * Handles the user's request to update their experiment.
     * 
     * @param command
     *            SequencerRun command object
     * @param request
     *            a {@link javax.servlet.http.HttpServletRequest} object.
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     * @throws java.lang.Exception
     *             if any.
     */
    public ModelAndView handleUpdate(HttpServletRequest request, HttpServletResponse response, SequencerRun command) throws Exception {

        Registration registration = Security.getRegistration(request);
        if (registration == null) return new ModelAndView("redirect:/login.htm");

        ModelAndView modelAndView = null;
        BindingResult errors = this.validateSequencerRun(request, command);
        if (errors.hasErrors()) {
            Map model = errors.getModel();
            modelAndView = new ModelAndView("SequencerRunUpdate", model);
        } else {
            SequencerRun newSequencerRun = command;
            SequencerRun oldSequencerRun = getCurrentSequencerRun(request);
            if (newSequencerRun != null && oldSequencerRun != null) {
                new ServletRequestDataBinder(oldSequencerRun).bind(request);
                getSequencerRunService().update(oldSequencerRun);
                modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
            } else {
                modelAndView = new ModelAndView("redirect:/Error.htm");
            }
        }

        request.getSession(false).removeAttribute("sequencerRun");

        return modelAndView;
    }

    /**
     * Validates a experiment.
     * 
     * @param command
     *            the Command instance as an Object
     * 
     * @return BindingResult validation errors
     */
    private BindingResult validateSequencerRun(HttpServletRequest request, Object command) {
        BindingResult errors = new BindException(command, getCommandName(command));
        // ValidationUtils.invokeValidator(getValidator(), command, errors);
        return errors;
    }

    /**
     * Gets the emailAddress from the experiment in the session.
     * 
     * @param request
     *            HttpServletRequest
     * 
     * @return the emailAddress from the experiment in the session, or null if there is no experiment in the session
     */
    private String getNameFromSession(HttpServletRequest request) {
        return getCurrentSequencerRun(request).getName();
    }

    /**
     * Gets the experiment from the session.
     * 
     * @param request
     *            HttpServletRequest
     * 
     * @return instance of SequencerRun from the session, or a new instance if the experiment is not in the session (e.g. the user is not
     *         logged in)
     */
    private SequencerRun getCurrentSequencerRun(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sequencerRun = session.getAttribute("sequencerRun");
            if (sequencerRun != null) {
                return (SequencerRun) sequencerRun;
            }
        }
        return new SequencerRun();
    }

    /**
     * <p>
     * Getter for the field <code>laneService</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
     */
    public LaneService getLaneService() {
        return laneService;
    }

    /**
     * <p>
     * Setter for the field <code>laneService</code>.
     * </p>
     * 
     * @param laneService
     *            a {@link net.sourceforge.seqware.common.business.LaneService} object.
     */
    public void setLaneService(LaneService laneService) {
        this.laneService = laneService;
    }
}
