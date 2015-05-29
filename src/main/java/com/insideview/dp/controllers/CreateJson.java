package com.insideview.dp.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insideview.DataRecord;


public class CreateJson {
	List<DataRecord> list = new ArrayList<DataRecord>();
	Gson gson = new GsonBuilder().create();
	
	public static void main(String[] args) {
		CreateJson cj = new CreateJson();
		try {
			cj.readFile();
			cj.writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void writeFile() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("jsonFile.txt")));
		for (DataRecord cur : list)
		{
			String json = gson.toJson(cur, DataRecord.class);
			bw.write(json);
			bw.newLine();
		}
		bw.close();
	}
	
	public void readFile() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("TestData.csv")));
		String line = null;
		br.readLine();
		while ((line=br.readLine()) != null)
		{
			try
			{
				DataRecord obj = new DataRecord();
				String[] split = line.split(",");
				short jobLevel = Short.valueOf(split[3]);
				int jobFunction = Integer.parseInt(split[4]);
				int empCount = Integer.valueOf(split[7]);
				int popularity = Integer.valueOf(split[8]);
				double revenue = Double.valueOf(split[6]);
				boolean fortuneListed;
				if (Integer.valueOf(split[9]) == -1)
					 fortuneListed = false;
				else 
					fortuneListed = true;
				String email = split[5];
				boolean label;
				int compId = Integer.parseInt(split[2]);
				int execId = Integer.parseInt(split[0]);
				int empId = Integer.parseInt(split[1]);
				double probability;
				
				obj.setCompId(compId);
				obj.setEmail(email);
				obj.setEmpCount(empCount);
				obj.setEmpId(empId);
				obj.setExecId(execId);
				obj.setFortuneListed(fortuneListed);
				obj.setJobFunction(jobFunction);
				obj.setJobLevel(jobLevel);
				obj.setPopularity(popularity);
				obj.setRevenue(revenue);
				list.add(obj);	
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
