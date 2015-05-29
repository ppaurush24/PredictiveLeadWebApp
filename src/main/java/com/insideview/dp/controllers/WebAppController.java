package com.insideview.dp.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insideview.DataRecord;
import com.insideview.DataRecordService;
import com.insideview.LogisticRegression;

@Controller
public class WebAppController {
	private static final Logger LOG = LoggerFactory
	    .getLogger(WebAppController.class);

	@RequestMapping(value = "/uploadLead")
	public void testMethod(HttpServletRequest request,
	    @RequestParam MultipartFile file, HttpServletResponse response) {
		Gson gson = new GsonBuilder().create();
		List<DataRecord> list = new ArrayList<DataRecord>();
		LOG.info("inside controller method ");
		if (file != null) {
			try {
				InputStream is = file.getInputStream();
				BufferedReader reader = new BufferedReader(
				    new InputStreamReader(is));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String email = null;
					try {
						DataRecord dto = gson.fromJson(line, DataRecord.class);
						email = dto.getEmail();
						System.out.println("Processed email: " + email);
						DataRecord result = DataRecordService
						    .getDataRecordForEmail(email);
						if (LogisticRegression.predict(result) == 1) {
							result.setLabel(true);
							result.setProbability(1.0);
						} else
							result.setLabel(false);
						result.setProbability(0.0);
						list.add(result);
					} catch (Exception e) {
						LOG.error("Exception in record with email" + email, e);
					}

				}
				Collections.sort(list, new Comparator<DataRecord>() {
					@Override
					public int compare(DataRecord o1, DataRecord o2) {
						if (o1.isLabel() && o2.isLabel()) {
							return 0;
						} else if (o1.isLabel()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				// START

				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println("<html>");
				out.println("<head>");
				out.println("<font color=\"red\"><h1 align=\"center\">The Scored Leads are :</h1></font>");
				out.println("</head>");
				out.println("<body bgcolor=#CC9966>");
				out.println("<form action=\"http://localhost:8080/public/feedback.html\" enctype=\"multipart/form-data\" method=\"post\">");
				out.println("<table align=\"center\" border=\"1\" cellspacing=\"0\" cellpadding=\"5\">");

				out.println("<tr>");
				out.println("<th align=\"left\">S.No</th>");
				out.println("<th align=\"left\">Lead Id</th>");
				out.println("<th align=\"left\">Email</th>");
				out.println("<th align=\"left\">Job Level</th>");
				out.println("<th align=\"left\">Job Function</th>");
				out.println("<th align=\"left\">Revenue</th>");
				out.println("<th align=\"left\">Employee Count</th>");
				out.println("<th align=\"left\">Will Convert?</th>");
				out.println("<th align=\"left\">Bad Lead(Yes/No)</th>");
				out.println("</tr>");

				for (int i = 0; i < list.size(); i++) {
					out.println("<tr>");
					out.println("<td align=\"left\">" + (i + 1) + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getExecId() + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getEmail() + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getJobLevel() + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getJobFunction() + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getRevenue() + "</td>");
					out.println("<td align=\"left\">" + list.get(i).getEmpCount() + "</td>");
					out.println("<td align=\"left\">" + (list.get(i).isLabel() ? "Yes" : "No") + "</td>");
					out.println("<td align=\"left\"><input type=\"checkbox\" name=\"radio" + i + "\" value=\"radio_" + i + "\"></td>");
					out.println("</tr>");
				}
				out.println("<tr>");
				out.println("<td align=\"center\" colspan=\"7\"><input type=\"submit\" value=\"Send Feed back to the System\" size=\"100\"></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("</form>");
				out.println("</body>");
				out.println("</html>");
				// END

			} catch (IOException e) {
				LOG.error("Exception in reading file", e);
			} catch (Exception e) {
				LOG.error("Exception: ", e);
			}
		}
	}

	@RequestMapping(value = "/uploadTraining", method = RequestMethod.POST)
	@ResponseBody
	public String trainEngine(HttpServletRequest request,
	    @RequestParam("file") MultipartFile file) {
		Gson gson = new GsonBuilder().create();
		LOG.info("inside controller method for feedback ");
		if (file != null) {
			try {
				InputStream is = file.getInputStream();
				BufferedReader reader = new BufferedReader(
				    new InputStreamReader(is));
				String line = null;
				Configuration conf = new Configuration();
				conf.set("fs.default.name", "hdfs://172.24.2.77:9000");
				Path path = new Path("/var/tmp/predictiveFeedback/record.txt");
				FileSystem fs = FileSystem.get(conf);

				if (!fs.exists(path))
				{
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					    fs.create(path, true)));
					while ((line = reader.readLine()) != null)
					{
						bw.write(line);
						bw.newLine();
					}
					bw.close();
				}
				else
				{
					BufferedWriter bwa = new BufferedWriter(new OutputStreamWriter(
					    fs.append(path)));
					while ((line = reader.readLine()) != null)
					{
						bwa.append(line);
						bwa.newLine();
					}
					bwa.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "Hello Bitch";
	}
}
