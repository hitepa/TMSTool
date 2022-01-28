package com.emeter.eip.tmsgenerator.file.tms;

import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.BILLABLE_TYPE;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.CALIBRI;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.COMMENTS;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.DATE_OF_ENTRY;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.EFFORT;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.LOCATION_INDICATOR;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.TMS_ACTIVITY_NAME;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.TMS_PROJECT_NAME;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.USER_ID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils.*;
import com.emeter.eip.tmsgenerator.model.TMSTimesheetData;

@Component
public class TMSTimesheetWriter {
   
	private static final Logger LOGGER = LoggerFactory.getLogger(TMSTimesheetWriter.class);
	
	
	public void writeTMSTimesheetFile(Map<String, List<TMSTimesheetData>> tmsDataMap, String filePath, String fileName) throws IOException {
		info(LOGGER,()-> "Generate TMS File");
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = configureWorkBook(workbook);
		writeHeader(sheet);
		writeRows(sheet, tmsDataMap);
		writeFile(workbook, filePath, fileName);
		info(LOGGER,()-> "Generate TMS File done !!!");
	}

	private Sheet configureWorkBook(Workbook workbook) {
		Sheet sheet = workbook.createSheet("TMS_Sheet");
		sheet.setColumnWidth(1, 10000);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName(CALIBRI);
		font.setFontHeightInPoints((short) 11);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		return sheet;
	}

	private void writeHeader(Sheet sheet) {
		Row header = sheet.createRow(0);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue(USER_ID);

		headerCell = header.createCell(1);
		headerCell.setCellValue(TMS_PROJECT_NAME);

		headerCell = header.createCell(2);
		headerCell.setCellValue(TMS_ACTIVITY_NAME);

		headerCell = header.createCell(3);
		headerCell.setCellValue(BILLABLE_TYPE);

		headerCell = header.createCell(4);
		headerCell.setCellValue(LOCATION_INDICATOR);

		headerCell = header.createCell(5);
		headerCell.setCellValue(DATE_OF_ENTRY);

		headerCell = header.createCell(6);
		headerCell.setCellValue(EFFORT);

		headerCell = header.createCell(7);
		headerCell.setCellValue(COMMENTS);

	}

	private void writeRows(Sheet sheet, Map<String, List<TMSTimesheetData>> tmsDataMap) {
		int i = 1;
		for (Map.Entry<String, List<TMSTimesheetData>> entry : tmsDataMap.entrySet()) {
			List<TMSTimesheetData> timeSheetDatas = entry.getValue();
			for(TMSTimesheetData timeSheetData : timeSheetDatas) {
				Row header = sheet.createRow(i);
				Cell headerCell = header.createCell(0);
				headerCell.setCellValue(timeSheetData.getUserId());

				headerCell = header.createCell(1);
				headerCell.setCellValue(timeSheetData.getTmsProjectName());

				headerCell = header.createCell(2);
				headerCell.setCellValue(timeSheetData.getTmsActivityName());

				headerCell = header.createCell(3);
				headerCell.setCellValue(timeSheetData.getBillableType());

				headerCell = header.createCell(4);
				headerCell.setCellValue(timeSheetData.getLocationIndicator());

				headerCell = header.createCell(5);
				headerCell.setCellValue(timeSheetData.getDateofEntry());

				headerCell = header.createCell(6);
				headerCell.setCellValue(timeSheetData.getEffort());

				headerCell = header.createCell(7);
				headerCell.setCellValue(timeSheetData.getComments());
				i++;
			}
		}

	}

	private void writeFile(Workbook workbook, String tmsSheetPath, String tmsFileName) throws IOException {
		createDir(tmsSheetPath);
		String filePath = tmsSheetPath + File.separator + tmsFileName;
		File tmsFile = new File(filePath);
		tmsFile.createNewFile();
		FileOutputStream outputStream = new FileOutputStream(tmsFile);
		//FileOutputStream outputStream = new FileOutputStream("D:\\Hitesh\\Learning\\Emeter\\POCs\\Internal\\TimesheetGenerator\\Outputs\\TMSUpload.xslx");
		workbook.write(outputStream);
		workbook.close();

	}
	
	private void createDir(String tmsSheetPath) {
		File dir = new File(tmsSheetPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}

}
