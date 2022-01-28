package com.emeter.eip.tmsgenerator.model;

import java.util.Comparator;
import java.util.Date;

public final class OrbitTimesheetData implements Comparator<OrbitTimesheetData> {

	private String resourceName;
	private String resourceGroup;
	private String projectName;
	private String taskName;
	private String taskType;
	private Date timesheetDate;
	private Double payableTime;
	private Double billableTime;
	private Date lastModified;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Date getTimesheetDate() {
		return timesheetDate;
	}

	public void setTimesheetDate(Date timesheetDate) {
		this.timesheetDate = timesheetDate;
	}

	/*@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(resourceName);
		sb.append(":").append(timesheetDate).append(":").append(taskName).append(":").append(billableTime);
		return sb.toString();
	}*/

	@Override
	public String toString() {
		return "OrbitTimesheetData{" +
				"resourceName='" + resourceName + '\'' +
				", resourceGroup='" + resourceGroup + '\'' +
				", projectName='" + projectName + '\'' +
				", taskName='" + taskName + '\'' +
				", timesheetDate=" + timesheetDate +
				", payableTime=" + payableTime +
				", billableTime=" + billableTime +
				'}';
	}

	@Override
	public int compare(OrbitTimesheetData o1, OrbitTimesheetData o2) {
		return o1.getTimesheetDate().compareTo(o2.getTimesheetDate());
	}

	public Double getPayableTime() {
		return payableTime;
	}

	public void setPayableTime(Double payableTime) {
		this.payableTime = payableTime;
	}

	public Double getBillableTime() {
		return billableTime;
	}

	public void setBillableTime(Double billableTime) {
		this.billableTime = billableTime;
	}
}
