package com.example.attendance.dto;

public class PaidLeaveDTO {
    private int id;
    private String username;
    private String startDate;  // yyyy-MM-dd
    private String endDate;
    private String reason;
    private String status;

    public PaidLeaveDTO() {}

    public PaidLeaveDTO(String username, String startDate, String endDate, String reason, String status) {
        this.username  = username;
        this.startDate = startDate;
        this.endDate   = endDate;
        this.reason    = reason;
        this.status    = status;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}