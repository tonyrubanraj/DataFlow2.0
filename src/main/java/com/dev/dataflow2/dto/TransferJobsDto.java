/**
 * 
 */
package com.dev.dataflow2.dto;

import java.util.List;

/**
 * @author tonyr
 *
 */
public class TransferJobsDto {

	private int sourceId;
	private int destinationId;
	private String sourceSchema;
	private String destinationSchema;
	private List<String> sourceTables;
	private List<String> destinationTables;
	private String jobType;

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}

	public String getSourceSchema() {
		return sourceSchema;
	}

	public void setSourceSchema(String sourceDatabase) {
		this.sourceSchema = sourceDatabase;
	}

	public String getDestinationSchema() {
		return destinationSchema;
	}

	public void setDestinationSchema(String destinationDatabase) {
		this.destinationSchema = destinationDatabase;
	}

	public List<String> getSourceTables() {
		return sourceTables;
	}

	public void setSourceTables(List<String> sourceTables) {
		this.sourceTables = sourceTables;
	}

	public List<String> getDestinationTables() {
		return destinationTables;
	}

	public void setDestinationTables(List<String> destinationTables) {
		this.destinationTables = destinationTables;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public TransferJobsDto(int sourceId, int destinationId, String sourceSchema, String destinationSchema,
			List<String> sourceTables, List<String> destinationTables, String jobType) {
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.sourceSchema = sourceSchema;
		this.destinationSchema = destinationSchema;
		this.sourceTables = sourceTables;
		this.destinationTables = destinationTables;
		this.jobType = jobType;
	}

	@Override
	public String toString() {
		return "TransferJobsDto [sourceId=" + sourceId + ", destinationId=" + destinationId + ", sourceSchema="
				+ sourceSchema + ", destinationSchema=" + destinationSchema + ", sourceTables=" + sourceTables
				+ ", destinationTables=" + destinationTables + ", jobType=" + jobType + "]";
	}

}
