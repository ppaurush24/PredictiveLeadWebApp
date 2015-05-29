package com.insideview.dp.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RecordController {
	private static final Logger LOG = LoggerFactory
	    .getLogger(RecordController.class);

	@RequestMapping(value = "/report")
	@ResponseBody
	public String actions(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		try {
			if (session.getAttribute("email") == null) {
				response.sendRedirect("/ui");
			} else {
				response.sendRedirect("/ui/report.html");
				Integer score = Integer.valueOf(request.getParameter("score"));
				String action = request.getParameter("action");
				String email = session.getAttribute("email").toString();
				LOG.info("Email:" + email + " Action:" + action + " score:" + score);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirecting to login";
	}

	@RequestMapping(value = "/login")
	@ResponseBody
	public String login(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		String email = request.getParameter("email");
		session.setAttribute("email", email);
		try {
			response.sendRedirect("/ui/report.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirecting";
	}

}
