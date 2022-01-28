package com.emeter.eip.tmsgenerator.model;

public class TimesheetGenInput {

	private String runPeriod;
	
	private String tmsSheetPath;
	
	private String tmsFileName;

	private String orbitProcessedPath;

	private String orbitErrorPath;

	public String getTmsFileName() {
		return tmsFileName;
	}

	public void setTmsFileName(String tmsFileName) {
		this.tmsFileName = tmsFileName;
	}

	public String getTmsSheetPath() {
		return tmsSheetPath;
	}

	public void setTmsSheetPath(String tmsSheetPath) {
		this.tmsSheetPath = tmsSheetPath;
	}

	private String orbitTimeSheetPath;
	
	private boolean analyze;

	

	public String getOrbitTimeSheetPath() {
		return orbitTimeSheetPath;
	}

	public void setOrbitTimeSheetPath(String orbitTimeSheetPath) {
		this.orbitTimeSheetPath = orbitTimeSheetPath;
	}

	public boolean isAnalyze() {
		return analyze;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}
	
	public String getRunPeriod() {
		return runPeriod;
	}

	public void setRunPeriod(String runPeriod) {
		this.runPeriod = runPeriod;
	}

	public String getOrbitProcessedPath() {
		return orbitProcessedPath;
	}

	public void setOrbitProcessedPath(String orbitProcessedPath) {
		this.orbitProcessedPath = orbitProcessedPath;
	}

	public String getOrbitErrorPath() {
		return orbitErrorPath;
	}

	public void setOrbitErrorPath(String orbitErrorPath) {
		this.orbitErrorPath = orbitErrorPath;
	}
}
