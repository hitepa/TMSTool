package com.emeter.eip.tmsgenerator.file.orbit;

import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emeter.eip.tmsgenerator.exception.TimesheetGeneratorRuntimeException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.emeter.eip.tmsgenerator.model.OrbitTimesheetData;


@Component
public class OrbitTimesheetReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrbitTimesheetReader.class);

	public  Map<String, List<OrbitTimesheetData>> processOrbitTimesheetFiles(File orbitFile, String runPeriod) {
		info(LOGGER,()-> "Start Processing Orbit files");
		Map<String, List<OrbitTimesheetData>> orbitDataMap =  new HashMap<String, List<OrbitTimesheetData>>();
		try (FileInputStream fis = new FileInputStream(orbitFile);) {
			try (XSSFWorkbook wb = new XSSFWorkbook(fis);) {
				XSSFSheet sheet = wb.getSheetAt(0);
				System.out.println(sheet.getSheetName());
				orbitDataMap = createOrbitDataMap(sheet, wb,runPeriod);
			} catch (Exception e) {
				e.printStackTrace();
				//LOGGER.error("Exception ,", e);
				throw new TimesheetGeneratorRuntimeException(
						"Exception while reading Orbit timesheet Data::" + e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception ,", e);
			throw new TimesheetGeneratorRuntimeException(
					"Exception while reading Orbit timesheet Data::" + e.getMessage());
		}finally {
			info(LOGGER,()-> "End Processing Orbit files !!!");
		}
		return orbitDataMap;
	}

	private   Map<String, List<OrbitTimesheetData>> createOrbitDataMap(XSSFSheet sheet, XSSFWorkbook wb,String runPeriod) {
		Date timesheetEdDate = getTimesheetEndDate(runPeriod);
		Date timesheetStDate = getTimesheetStartDate(timesheetEdDate);
		info(LOGGER,()-> "Timesheet start date::"+timesheetStDate);
		info(LOGGER,()-> "Timesheet end date::"+timesheetEdDate);
		Map<String, List<OrbitTimesheetData>> orbitDataMap = new HashMap<String, List<OrbitTimesheetData>>();
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			if (row.getCell(0) == null) {
				   continue;
			}
			OrbitTimesheetData orbitData = new OrbitTimesheetData();
			//System.out.println("Row:"+row);
			String resourceName = row.getCell(0).getStringCellValue().replace(" ", "-").replace(",", "");
			
			/* if(!"Pawar-Hitesh".equalsIgnoreCase(resourceName)) { continue; } */
			 
			Date timeSheetDate = row.getCell(5).getDateCellValue();
			if (!isValidTimesheetDate(timeSheetDate, timesheetStDate, timesheetEdDate)) {
				continue;
			}
			String resourceGroup = row.getCell(1).getStringCellValue();
			String projectName = row.getCell(2).getStringCellValue();
			String taskName = row.getCell(3).getStringCellValue().replace(" ", "-");;
			String taskType = row.getCell(4).getStringCellValue().replace(" ", "-");
			Double payableTime = row.getCell(6).getNumericCellValue();
			Double billableTime = row.getCell(7).getNumericCellValue();
			Date lastModified = row.getCell(8).getDateCellValue();
			orbitData.setBillableTime(billableTime);
			orbitData.setLastModified(lastModified);
			orbitData.setPayableTime(payableTime);
			orbitData.setProjectName(projectName);
			orbitData.setResourceGroup(resourceGroup);
			orbitData.setResourceName(resourceName);
			orbitData.setTaskName(taskName);
			orbitData.setTaskType(taskType);
			orbitData.setTimesheetDate(timeSheetDate);
			debug(LOGGER, ()->"Orbit Data for Row " +row.getRowNum()+ " :: "+ orbitData.toString());
			if (orbitDataMap.containsKey(resourceName)) {
				orbitDataMap.get(resourceName).add(orbitData);
			} else {
				List<OrbitTimesheetData> orbitDataList = new ArrayList<>();
				orbitDataList.add(orbitData);
				orbitDataMap.put(resourceName, orbitDataList);
			}
		}
		return orbitDataMap;

	}
}
