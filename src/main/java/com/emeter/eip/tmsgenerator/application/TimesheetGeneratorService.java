package com.emeter.eip.tmsgenerator.application;

import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emeter.eip.tmsgenerator.adapter.TimesheetDataAdapter;
import com.emeter.eip.tmsgenerator.exception.TimesheetGeneratorRuntimeException;
import com.emeter.eip.tmsgenerator.file.orbit.OrbitTimesheetReader;
import com.emeter.eip.tmsgenerator.file.tms.TMSTimesheetWriter;
import com.emeter.eip.tmsgenerator.model.OrbitTimesheetData;
import com.emeter.eip.tmsgenerator.model.TMSTimesheetData;
import com.emeter.eip.tmsgenerator.model.TimesheetGenInput;
import com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils;

@Service
public class TimesheetGeneratorService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimesheetGeneratorService.class);

	@Autowired
	private OrbitTimesheetReader orbitReader;

	@Autowired
	private TMSTimesheetWriter tmsWriter;

	@Autowired
	private TimesheetDataAdapter timesheetAdapter;

	public void execute(TimesheetGenInput svcInput) throws Exception {
		Map<String, List<OrbitTimesheetData>> orbitDataMap = null;
		Map<String, List<TMSTimesheetData>> tmsDataMap = null;
		
		List<File> orbitFiles = searchOrbitFiles(svcInput.getOrbitTimeSheetPath());
		if(orbitFiles!=null && !orbitFiles.isEmpty()) {
			boolean isErrorOccured;
			for(File orbitFile: orbitFiles) {
				isErrorOccured =false;
				try {
					System.out.println(orbitFile.getName());
					String tmsFileName = deriveTmsFileName(orbitFile.getName());
					info(LOGGER,()-> "Derived TMS File name::"+tmsFileName);
					try {
						orbitDataMap = orbitReader.processOrbitTimesheetFiles(orbitFile,
								svcInput.getRunPeriod());
					} catch (Exception e) {
						isErrorOccured = true;
						e.printStackTrace();
						LOGGER.error("Unexpected Error come", e);
						throw new TimesheetGeneratorRuntimeException(
								"Exception while reading Orbit timesheet Data::" + e.getMessage());
					}
					if (!orbitDataMap.isEmpty()) {
						try {
							tmsDataMap = timesheetAdapter.toTMSTimesheetData(orbitDataMap);
						} catch (Exception e) {
							isErrorOccured = true;
							//e.printStackTrace();
							LOGGER.error("Unexpected Error come", e);
							throw new TimesheetGeneratorRuntimeException(
									"Exception while converting orbit data to TMS data::"+ e.getMessage());
						}

					}else {
						info(LOGGER,()-> "No Orbit Data can be mapped to generate TMS File");
					}

					if (MapUtils.isNotEmpty(tmsDataMap)) {
						try {
							tmsWriter.writeTMSTimesheetFile(tmsDataMap,svcInput.getTmsSheetPath(),tmsFileName);
						} catch (Exception e) {
							isErrorOccured =true;
							//e.printStackTrace();
							LOGGER.error("Unexpected Error come", e);
							throw new TimesheetGeneratorRuntimeException(
									"Exception while reading Orbit timesheet Data::" + e.getMessage());
						}
					}else {
						info(LOGGER,()-> "No TMS Data can be mapped from Orbit Data");
					}
				}
				catch (Exception e) {
					isErrorOccured = true;
					//e.printStackTrace();
					LOGGER.error("Unexpected Error come", e);
//					throw new TimesheetGeneratorRuntimeException(
//							"Exception while converting orbit data to TMS data::"+ e.getMessage());
				}finally{
					LOGGER.info("Removing file from input directory : "+ svcInput.getOrbitTimeSheetPath());
					deleteFileFromDirectory(svcInput, orbitFile, isErrorOccured);
				}
		}
	
		}else {
			info(LOGGER,()-> "No MS Orbit files found in directory::"+svcInput.getOrbitTimeSheetPath());
		}
	}

	void deleteFileFromDirectory(TimesheetGenInput svcInput, File file, boolean isErrorOccured){

		String inputFileLocation = svcInput.getOrbitTimeSheetPath() + "\\"+ file.getName();
		String outputFileLocation = null;
		if (isErrorOccured) {
			outputFileLocation = svcInput.getOrbitErrorPath() + "\\"+ deriveTmsFileName(file.getName());
		}else{
			outputFileLocation = svcInput.getOrbitProcessedPath() + "\\"+ deriveTmsFileName(file.getName());
		}

		try {
			Path temp = Files.move
					(Paths.get(inputFileLocation),
							Paths.get(outputFileLocation));

			if(temp != null)
			{
				info(LOGGER, ()->"File has been successfully moved to "+ temp.toString() );
			}
			else
			{
				info(LOGGER, ()->"Failed to move the file to " + temp.toString());
			}
		}catch (Exception e){
			LOGGER.error("Exception occurred while deleting file from directory" ,e);
			/*throw new TimesheetGeneratorRuntimeException(
					"Exception while moving the file "+ e.getMessage());*/
		}


	}
	/**
	 * @param orbitReader the orbitReader to set
	 */
	public void setOrbitReader(OrbitTimesheetReader orbitReader) {
		this.orbitReader = orbitReader;
	}

	/**
	 * @param tmsWriter the tmsWriter to set
	 */
	public void setTmsWriter(TMSTimesheetWriter tmsWriter) {
		this.tmsWriter = tmsWriter;
	}

	/**
	 * @param timesheetAdapter the timesheetAdapter to set
	 */
	public void setTimesheetAdapter(TimesheetDataAdapter timesheetAdapter) {
		this.timesheetAdapter = timesheetAdapter;
	}

}
