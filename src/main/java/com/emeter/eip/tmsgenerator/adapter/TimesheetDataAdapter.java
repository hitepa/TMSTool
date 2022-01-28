package com.emeter.eip.tmsgenerator.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.*;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils.*;

import com.emeter.eip.tmsgenerator.config.ConfigProperties;
import com.emeter.eip.tmsgenerator.exception.TimesheetGeneratorRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.emeter.eip.tmsgenerator.model.OrbitTimesheetData;
import com.emeter.eip.tmsgenerator.model.TMSTimesheetData;

@Component
public class TimesheetDataAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimesheetDataAdapter.class);

	@Autowired
	private Environment env;

	@Autowired
	ConfigProperties configProperties;
	public void setEnv(Environment env) {
		this.env = env;
	}

	public Map<String, List<TMSTimesheetData>> toTMSTimesheetData(Map<String, List<OrbitTimesheetData>> orbitDataMap) {
		// Incoming Map contains Time sheet records from 21 to 20 of the month for each
		// employee...
		info(LOGGER,()-> "Map Orbit data to TMS Data");
		Map<String, List<TMSTimesheetData>> tmsDataMap = new HashMap<>();
		for (Entry<String, List<OrbitTimesheetData>> entry : orbitDataMap.entrySet()) {
			Map<String, TMSTimesheetData> tmsTimeSheeDataMap = new TreeMap<>();
			List<TMSTimesheetData> userTMSDataList = new ArrayList<>();
			String resourceName = entry.getKey();
			String userID = configProperties.getNameToGID().get(resourceName);
			info(LOGGER, ()->"Preparing TMS data for user " + resourceName + " having GUID : " + userID);
			List<OrbitTimesheetData> userOrbitDataList = entry.getValue();
			if (StringUtils.isNotEmpty(userID)) {
				info(LOGGER, ()-> "Converting orbit to TMS data for resource " + resourceName + " having Guid" + userID);
				for (OrbitTimesheetData userOrbitData : userOrbitDataList) {
					String dateOfEntry = getTMSCompatibleDate(userOrbitData.getTimesheetDate());
					String orbitTaskName = userOrbitData.getTaskName();
					String tmsActName = deriveTMSActName(dateOfEntry, userOrbitData.getProjectName(), orbitTaskName);
					String tmsProjectName = configProperties.getTmsProjectNameByOrbitName(userOrbitData.getProjectName());
					String tmsTimeSheeDataMapKey = dateOfEntry.concat("#").concat(tmsActName).concat(tmsProjectName);
					if (tmsTimeSheeDataMap.containsKey(tmsTimeSheeDataMapKey)) {
						TMSTimesheetData userTMSData = tmsTimeSheeDataMap.get(tmsTimeSheeDataMapKey);
						Double aggregatedEfforts = Double.valueOf(userTMSData.getEffort()) + userOrbitData.getPayableTime();
						userTMSData.setEffort(aggregatedEfforts.toString());
						tmsTimeSheeDataMap.put(tmsTimeSheeDataMapKey, userTMSData);
						debug(LOGGER,()-> "TMS Data for the date Key::"+tmsTimeSheeDataMapKey+" for user: "+resourceName+":: "+userTMSData);
					} else {
						TMSTimesheetData userTMSData = new TMSTimesheetData();
						userTMSData.setUserId(userID);
						userTMSData.setTmsProjectName(tmsProjectName);
						userTMSData.setTmsActivityName(tmsActName);
						userTMSData.setBillableType(tmsActName.split("-")[2].trim());
						userTMSData.setLocationIndicator(configProperties.getLocationByOrbitProjectTaskName(userOrbitData.getProjectName(), orbitTaskName));
						userTMSData.setDateofEntry(dateOfEntry);
						userTMSData.setEffort(userOrbitData.getPayableTime().toString());
						tmsTimeSheeDataMap.put(tmsTimeSheeDataMapKey, userTMSData);
						debug(LOGGER,()-> "New TMS Data for the date Key for user:"+resourceName+":: "+userTMSData);
					}
				}
				for (Entry<String, TMSTimesheetData> entryTMS : tmsTimeSheeDataMap.entrySet()) {
					userTMSDataList.add(entryTMS.getValue());
				}
				tmsDataMap.put(userID, userTMSDataList);
			}else {
				LOGGER.warn("Could not process Orbit data for resource \""+ resourceName + "\" as Guid is not configured in yaml/properties file. Please check tmsgen.nameToGID property");
			}

			
		}
		//debug(LOGGER, ()->"Aggregated TMS Data Map::"+tmsDataMap);
		info(LOGGER,()-> "Map Orbit data to TMS Data finished !!!");
		return tmsDataMap;
	}

	private String deriveTMSActName(String tmsDateOfEntry, String orbitProjectName, String orbitTaskName) {
		//String orbitTaskPropName = orbitTaskToTmsActPropName + PROP_DELIMETER + orbitTaskName;
		//String holidayTaskPropName = holidayPropName + PROP_DELIMETER + tmsDateOfEntry;
		
		//String holidayTMSActName = configProperties.getHoliday().get(tmsDateOfEntry);

		/*if (holidayTMSActName != null) {
			//Check if someone worked on holiday
			if(orbitTaskName.contains(CUSTOM_WORK_SUFFIX)) {
				tmsActName = configProperties.getOrbitTaskToTmsAct().get(CUSTOM_WORK_SUFFIX);
			}else {
				tmsActName = holidayTMSActName;
			}
		} else if (orbitTaskName.contains(CUSTOM_WORK_SUFFIX)) {
			tmsActName = configProperties.getOrbitTaskToTmsAct().get(CUSTOM_WORK_SUFFIX);
		} else if (orbitTaskName.contains(VACATION_SUFFIX)) {
			tmsActName = configProperties.getOrbitTaskToTmsAct().get(VACATION_SUFFIX);
		} else {
			tmsActName = configProperties.getOrbitTaskToTmsAct().get(DEFAULT);
		}*/
		String tmsActName = configProperties.getTmsActNameByOrbitProjectTaskName(orbitProjectName, orbitTaskName);
		return tmsActName;
	}

	private String getTMSCompatibleDate(Date orbitDate) {
		return new SimpleDateFormat("MM/dd/yyyy").format(orbitDate);
	}
}
