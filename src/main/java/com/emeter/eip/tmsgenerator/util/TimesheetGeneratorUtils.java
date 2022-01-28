package com.emeter.eip.tmsgenerator.util;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class TimesheetGeneratorUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimesheetGeneratorUtils.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private TimesheetGeneratorUtils() {

	}

	// Conditional-Deferred execution pattern for Logging
	public static void info(Logger log, Supplier<String> message) {
		log.info(message.get());
	}

	public static void debug(Logger log, Supplier<String> message) {
        if(log.isDebugEnabled()) {
        	log.debug(message.get());
        }
		
	}

	public static void error(Logger log, Supplier<String> message) {
		log.info(message.get());
	}

	public static Date getTimesheetEndDate(String timeSheetPeriod) {
		String[] tokens = timeSheetPeriod.split(":");
		int month = Integer.valueOf(tokens[0]) - 1;
		int year = Integer.valueOf(tokens[1]);
		Calendar stDate = Calendar.getInstance();
		stDate.set(year, month, 20, 0, 0, 0);
		stDate.set(Calendar.MILLISECOND, 0);
		return stDate.getTime();
	}

	public static Date getTimesheetStartDate(Date timesheetStartDate) {
		Calendar edDate = Calendar.getInstance();
		edDate.setTime(timesheetStartDate);
		edDate.add(Calendar.MONTH, -1);
		return edDate.getTime();
	}

	public static boolean isValidTimesheetDate(Date timesheetDate, Date timesheetStDate, Date timesheetEdDate) {
		Calendar effTimesheetDate = Calendar.getInstance();
		effTimesheetDate.setTime(timesheetDate);
		effTimesheetDate.set(Calendar.HOUR_OF_DAY, 0);
		effTimesheetDate.set(Calendar.MINUTE, 0);
		effTimesheetDate.set(Calendar.SECOND, 0);
		effTimesheetDate.set(Calendar.MILLISECOND, 0);
		// System.out.println("Eff Timesheet Date::"+effTimesheetDate.getTime());
		if (effTimesheetDate.getTime().after(timesheetStDate) && (effTimesheetDate.getTime().before(timesheetEdDate)
				|| effTimesheetDate.getTime().equals(timesheetEdDate))) {
			//System.out.println("Valid  Time sheet Date::" + effTimesheetDate.getTime());
			return true;
		}
		return false;
	}
	
	public static List<File> searchOrbitFiles(String inputDir){
		info(LOGGER,()-> "Search MS Orbit Timesheet Files");
		File folder = new File(inputDir);
		File[] orbitFiles  = folder.listFiles();
		List<File> fileList = new ArrayList<>();
		if(orbitFiles!=null) {
			for(File file : orbitFiles) {
				if(file.isDirectory() || file.getName().contains("$")|| file.getName().contains("~")){
					continue;
				}
				fileList.add(file);
			}
		}

		return fileList;
	}
	
	public static String deriveTmsFileName(String fileNameWithFormat){
		String fileName = fileNameWithFormat.split("\\.")[0];
		StringBuilder sb = new StringBuilder(fileName);
		sb.append("_").append(sdf.format(new Date())).append(".xlsx");
		return sb.toString();
	}

}
