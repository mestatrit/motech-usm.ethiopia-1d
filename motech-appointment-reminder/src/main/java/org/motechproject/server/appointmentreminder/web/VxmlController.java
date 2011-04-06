/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.appointmentreminder.web;

import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * Appointment Reminder related VXML documents
 *
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Autowired
    private PatientDAO patientDAO;


    /**
     * Handles Appointment Reminder HTTP requests and generates a VXML document based on a Velocity template.
     * The HTTP request should contain the mandatory 'a' parameter with value of ID of the Appointment for which
     * a VXML document will be generated.
     *
     * If Invalid appointment ID has been sent or appointment data can not be obtained a VVML with an error message
     * will be generated.
     *
	 * URL to request appointment reminder VoiceXML:
	 * http://<host>:<port>/<motech-platform-server>/module/ar/vxml/ar?a=<appointmentId>
	 */
	public ModelAndView ar(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate appointment reminder VXML");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();


        String appointmentId = request.getParameter("a");

        logger.debug("Appointment Reminder ID: " + appointmentId );

        if (appointmentId  == null) {
            logger.warn("Invalid request. Value of the Appointment Reminder ID parameter - 'a'  sent as a part of that VXML URL is null." +
                    " Generating a VXML with the error message...");

            mav.setViewName("ar_error");
		    return mav;
        }

        Appointment appointment = null;

        try {
            appointment = patientDAO.getAppointment(appointmentId );
        } catch (Exception e) {
            logger.error("Can not obtain Appointment by ID: " + appointmentId , e);
            logger.warn("Generating a VXML with the error message...");
            mav.setViewName("ar_error");
            return mav;
        }


        if (appointment == null) {
            logger.error("Can not find Appointment by ID: " + appointmentId );
            logger.warn("Generating a VXML with the error message...");
            mav.setViewName("ar_error");
             return mav;
        }


        mav.setViewName("ar");
		mav.addObject("appointmentDueDate", appointment.getReminderWindowEnd());
		return mav;
	}
}