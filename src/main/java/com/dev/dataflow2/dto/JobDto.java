/**
 * 
 */
package com.dev.dataflow2.dto;

import java.util.List;

/**
 * @author tonyr
 *
 */
public class JobDto {

	private int connectionId;
	private String sourceSchema;
	private String destinationSchema;
	private List<String> sourceTables;
	private List<String> destinationTables;
	private String jobType;

	public int getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
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

	public JobDto(int connectionId, String sourceDatabase, String destinationDatabase, List<String> sourceTables,
			List<String> destinationTables, String jobType) {
		super();
		this.connectionId = connectionId;
		this.sourceSchema = sourceDatabase;
		this.destinationSchema = destinationDatabase;
		this.sourceTables = sourceTables;
		this.destinationTables = destinationTables;
		this.jobType = jobType;
	}

	public JobDto() {
		super();
	}

}
