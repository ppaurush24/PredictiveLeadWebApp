package com.insideview.dp.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;

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

@Controller
public class WebAppController {
	private static final Logger LOG = LoggerFactory
			.getLogger(WebAppController.class);

	@RequestMapping(value = "/uploadLead")
	@ResponseBody
	public String testMethod(HttpServletRequest request,
			@RequestParam MultipartFile file) {
		Gson gson = new GsonBuilder().create();
		LOG.info("inside controller method ");
		return "Hello Dude";
		/*if (file != null) {
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
						DataRecordService service = new DataRecordService();
						DataRecord result = service
								.getDataRecordForEmail(email);
						if (LogisticRegression.predict(result) >= 0.5) {
							result.setLabel(true);
						} else
							result.setLabel(false);

						return gson.toJson(result);
					} catch (Exception e) {
						LOG.error("Exception in record with email" + email, e);
					}

				}
			} catch (IOException e) {
				LOG.error("Exception in reading file", e);
			} catch (Exception e) {
				LOG.error("Exception: ", e);
			}
		}*/
//		return null;
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
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						fs.create(path, true)));
				while ((line = reader.readLine()) != null) {
					if (!fs.exists(path)) {
						bw.write(line);
						bw.newLine();
					} else {
						fs.append(path);
					}

					boolean flag = Boolean.getBoolean(fs.getConf().get(
							"dfs.support.append"));
					System.out.println("dfs.support.append is set to be "
							+ flag);

					if (flag) {
						// FSDataOutputStream fsout = fs.append(new Path(uri));

						// wrap the outputstream with a writer
						// PrintWriter writer = new PrintWriter(fsout);
						// writer.append(content);
						// writer.close();

					}
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "Hello Bitch";
	}
}
