package com.emeter.eip.tmsgenerator.application;

import com.emeter.eip.tmsgenerator.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorUtils.*;
import static com.emeter.eip.tmsgenerator.util.TimesheetGeneratorConstants.*;
import com.emeter.eip.tmsgenerator.model.TimesheetGenInput;

import java.io.File;

/**
 * <p>
 * Spring Boot main class to load spring application context and start the
 * application
 * </p>
 * 
 * @author z0034ydd
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.emeter.eip.tmsgenerator"})
public class TimesheetGeneratorApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimesheetGeneratorApplication.class);

	@Autowired
	private TimesheetGeneratorService timesheetGenSvc;


	@Autowired
	private ConfigProperties configProperties;

	public static void main(String[] args) {
		info(LOGGER, ()->"TMS Application started");
		SpringApplication app = new SpringApplication(TimesheetGeneratorApplication.class);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		TimesheetGenInput input = new TimesheetGenInput();
		setInput(input);
		try {
			timesheetGenSvc.execute(input);
			LOGGER.info("TMS Application executed successfully");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception", e);
			LOGGER.error("TMS Application executed with errors");
		}

	}
	
	private void setInput(TimesheetGenInput input) {
		input.setOrbitTimeSheetPath(configProperties.getOrbitInputTimesheetPath());
		input.setOrbitProcessedPath(configProperties.getOrbitProcessedTimesheetPath());
		input.setOrbitErrorPath(configProperties.getOrbitErrorTimesheetPath());
		input.setTmsSheetPath(configProperties.getTmsTimesheetPath());
		input.setRunPeriod(configProperties.getTimeSheetPeriod());
		info(LOGGER,()-> "TMS Generation Period::"+input.getRunPeriod());
	}


	public void setTimesheetGenSvc(TimesheetGeneratorService timesheetGenSvc) {
		this.timesheetGenSvc = timesheetGenSvc;
	}

}
