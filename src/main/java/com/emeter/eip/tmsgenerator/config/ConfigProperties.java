package com.emeter.eip.tmsgenerator.config;

import com.emeter.eip.tmsgenerator.exception.TimesheetGeneratorRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.*;

@Configuration
@Validated
@ConfigurationProperties(prefix = "tmsgen")
public class ConfigProperties {

    @NotEmpty
    private String orbitInputTimesheetPath;
    @NotEmpty
    private String orbitProcessedTimesheetPath;
    @NotEmpty
    private String orbitErrorTimesheetPath;
    @NotEmpty
    private String timeSheetPeriod;
    @NotEmpty
    private String tmsTimesheetPath;
    @NotEmpty(message = "Property cannot be empty. Please provider the property in applications file")
    private Map<String, String> tmsProjectName;
    @NotEmpty
    private Map<String, String> nameToGID;
    @NotEmpty
    private Map<String, Map<String, String>> orbitTaskToTmsAct;
    @NotEmpty
    private Map<String, Map<String, String>> orbitProjToLocationIndicator;

    @Autowired
    private Environment env;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public void setEnv(Environment env) {
        this.env = env;
    }

    public String getOrbitInputTimesheetPath() {
        createValidateFileDirectory(orbitInputTimesheetPath);
        return orbitInputTimesheetPath;
    }

    public void setOrbitInputTimesheetPath(String orbitInputTimesheetPath) {
        this.orbitInputTimesheetPath = orbitInputTimesheetPath;
    }

    public String getOrbitProcessedTimesheetPath() {
        createValidateFileDirectory(orbitProcessedTimesheetPath);
        return orbitProcessedTimesheetPath;
    }

    public void setOrbitProcessedTimesheetPath(String orbitProcessedTimesheetPath) {
        this.orbitProcessedTimesheetPath = orbitProcessedTimesheetPath;
    }

    public String getOrbitErrorTimesheetPath() {
        createValidateFileDirectory(orbitErrorTimesheetPath);
        return orbitErrorTimesheetPath;
    }

    public void setOrbitErrorTimesheetPath(String orbitErrorTimesheetPath) {
        this.orbitErrorTimesheetPath = orbitErrorTimesheetPath;
    }

    public String getTimeSheetPeriod() {
        return timeSheetPeriod;
    }

    public void setTimeSheetPeriod(String timeSheetPeriod) {
        this.timeSheetPeriod = timeSheetPeriod;
    }

    public String getTmsTimesheetPath() {
        return tmsTimesheetPath;
    }

    public void setTmsTimesheetPath(String tmsTimesheetPath) {
        this.tmsTimesheetPath = tmsTimesheetPath;
    }

    public Map<String, String> getTmsProjectName() {
        return tmsProjectName;
    }

    public void setTmsProjectName(Map<String, String> tmsProjectName) {
        this.tmsProjectName = tmsProjectName;
    }

    public Map<String, String> getNameToGID() {
        return nameToGID;
    }

    public void setNameToGID(Map<String, String> nameToGID) {
        this.nameToGID = nameToGID;
    }


    public Map<String, Map<String, String>> getOrbitTaskToTmsAct() {
        return orbitTaskToTmsAct;
    }

    public void setOrbitTaskToTmsAct(Map<String, Map<String, String>> orbitTaskToTmsAct) {
        this.orbitTaskToTmsAct = orbitTaskToTmsAct;
    }

    public Map<String, Map<String, String>> getOrbitProjToLocationIndicator() {
        return orbitProjToLocationIndicator;
    }

    public void setOrbitProjToLocationIndicator(Map<String, Map<String, String>> orbitProjToLocationIndicator) {
        this.orbitProjToLocationIndicator = orbitProjToLocationIndicator;
    }

    public String getTmsProjectNameByOrbitName(String orbitProjectName){
        String tmsProName = tmsProjectName.get(orbitProjectName);
        if(StringUtils.isEmpty(tmsProName)) {
            tmsProName = tmsProjectName.get(DEFAULT);
        }
        return tmsProName;
    }
    public String getTmsActNameByOrbitProjectTaskName(String orbitProjectName, String orbitTaskName){
        Map<String, String> orbitTaskTmsAcNameMap = getOrbitTaskToTmsAct().get(orbitProjectName);
        String tmsActName;
        if (orbitTaskTmsAcNameMap == null) {
            logger.debug("Could not found orbit project name " + orbitProjectName + " in  orbitTaskToTmsAct. Hence taking the default value");
            tmsActName = getDefaultTmsActivityName();
        }else {
            tmsActName = orbitTaskTmsAcNameMap.get(orbitTaskName);
        }
        if (StringUtils.isEmpty(tmsActName)) {
            logger.debug(orbitProjectName +" project found but TaskName ("+orbitTaskName+") is not present in orbitTaskToTmsAct. Hence taking default value");
            tmsActName = getDefaultTmsActivityName();
        }
        String[] tmsActNameArr = tmsActName.split("-");
        if (tmsActNameArr.length!=3) {
            throw new TimesheetGeneratorRuntimeException("Configuration issue. Please check the value of tmsgen.orbitTaskToTmsAct."+ orbitProjectName + "." + tmsActName);
        }
        return tmsActName;
    }

    private String getDefaultTmsActivityName() {
        String tmsActName;
        Map<String, String> commonProjectTmsActivityName = getOrbitTaskToTmsAct().get(COMMON_TASK_NAME);
        if (commonProjectTmsActivityName == null) {
            throw new TimesheetGeneratorRuntimeException("Could not found entry for tmsgen.orbitTaskToTmsAct." + COMMON_TASK_NAME + "." + DEFAULT);
        }
        tmsActName = commonProjectTmsActivityName.get(DEFAULT);
        if (tmsActName == null) {
            throw new TimesheetGeneratorRuntimeException("Could not found entry for tmsgen.orbitTaskToTmsAct." + COMMON_TASK_NAME + "." + DEFAULT);
        }
        return tmsActName;
    }


    public String getLocationByOrbitProjectTaskName(String orbitProjectName, String orbitTaskName) {
        Map<String, String> orbitTaskLocationMap = getOrbitProjToLocationIndicator().get(orbitProjectName);
        String tmsLocation;
        if (orbitTaskLocationMap == null) {
            logger.debug("Could not found orbit project name ("+orbitProjectName+") in orbitProjToLocationIndicator. Hence taking the default value");
            tmsLocation = getDefaultLocation();
        }else{
            tmsLocation = orbitTaskLocationMap.get(orbitTaskName);
        }
        if (StringUtils.isEmpty(tmsLocation)) {
            logger.debug(orbitProjectName +" project found but TaskName ("+orbitTaskName+") is not present in orbitProjToLocationIndicator. Hence taking default value");
            tmsLocation = getDefaultLocation();
        }
        if (tmsLocation == null) {
            throw new TimesheetGeneratorRuntimeException("Could not found entry for tmsgen.orbitProjToLocationIndicator." + orbitProjectName+"." + DEFAULT);
        }
        return tmsLocation;
    }
    private String getDefaultLocation() {
        String locationIndicator;
        Map<String, String> commonProjectLocationIndicator = getOrbitProjToLocationIndicator().get(COMMON_LOCATION_INDICATOR);
        if (COMMON_LOCATION_INDICATOR == null) {
            throw new TimesheetGeneratorRuntimeException("Could not found entry for tmsgen.orbitTaskToTmsAct." + COMMON_LOCATION_INDICATOR + "." + DEFAULT);
        }
        locationIndicator = commonProjectLocationIndicator.get(DEFAULT);
        if (locationIndicator == null) {
            throw new TimesheetGeneratorRuntimeException("Could not found entry for tmsgen.orbitTaskToTmsAct." + COMMON_LOCATION_INDICATOR + "." + DEFAULT);
        }
        return locationIndicator;
    }

    void createValidateFileDirectory(String directoryPath){
        try {
            Files.createDirectories(Paths.get(directoryPath));
        } catch (IOException e) {
            throw new TimesheetGeneratorRuntimeException("Unable to create directory for path " + directoryPath);
        }
    }
}






