package org.motechproject.commcarestdemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public class VxmlController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @RequestMapping(value = "/vxml", method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate VXML");

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();

        mav.addObject("audioPathBase", "http://74.65.137.20:8080/motech-platform-server/module");

        mav.setViewName("vxml");

        return mav;
    }

}
