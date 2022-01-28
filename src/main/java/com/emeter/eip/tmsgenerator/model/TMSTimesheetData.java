package com.emeter.eip.tmsgenerator.model;

public class TMSTimesheetData {

	private String userId;
	private String tmsProjectName;
	private String tmsActivityName;
	private String billableType;
	private String locationIndicator;
	private String dateofEntry;
	private String effort;
	private String comments;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the tmsProjectName
	 */
	public String getTmsProjectName() {
		return tmsProjectName;
	}

	/**
	 * @param tmsProjectName the tmsProjectName to set
	 */
	public void setTmsProjectName(String tmsProjectName) {
		this.tmsProjectName = tmsProjectName;
	}

	/**
	 * @return the tmsActivityName
	 */
	public String getTmsActivityName() {
		return tmsActivityName;
	}

	/**
	 * @param tmsActivityName the tmsActivityName to set
	 */
	public void setTmsActivityName(String tmsActivityName) {
		this.tmsActivityName = tmsActivityName;
	}

	/**
	 * @return the billableType
	 */
	public String getBillableType() {
		return billableType;
	}

	/**
	 * @param billableType the billableType to set
	 */
	public void setBillableType(String billableType) {
		this.billableType = billableType;
	}

	/**
	 * @return the locationIndicator
	 */
	public String getLocationIndicator() {
		return locationIndicator;
	}

	/**
	 * @param locationIndicator the locationIndicator to set
	 */
	public void setLocationIndicator(String locationIndicator) {
		this.locationIndicator = locationIndicator;
	}

	/**
	 * @return the dateofEntry
	 */
	public String getDateofEntry() {
		return dateofEntry;
	}

	/**
	 * @param dateofEntry the dateofEntry to set
	 */
	public void setDateofEntry(String dateofEntry) {
		this.dateofEntry = dateofEntry;
	}

	/**
	 * @return the effort
	 */
	public String getEffort() {
		return effort;
	}

	/**
	 * @param effort the effort to set
	 */
	public void setEffort(String effort) {
		this.effort = effort;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "TMSTimesheetData [userId=" + userId + ", tmsProjectName=" + tmsProjectName + ", tmsActivityName="
				+ tmsActivityName + ", billableType=" + billableType + ", locationIndicator=" + locationIndicator
				+ ", dateofEntry=" + dateofEntry + ", effort=" + effort + ", comments=" + comments + "]";
	}

}
