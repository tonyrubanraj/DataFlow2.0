/**
 * 
 */
package com.dev.dataflow2.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author tonyr
 *
 */
@Entity
@Table(name = "transfer_jobs")
public class TransferJobs {

	@Id
	@Column(name = "jobid")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int jobId;

	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	@ManyToOne
	@JoinColumn(name = "sourceid")
	private DBConnections sourceDBConnection;

	@ManyToOne
	@JoinColumn(name = "destinationid")
	private DBConnections destinationDBConnection;

	@Column(name = "sourcedatabase")
	private String sourceDatabase;

	@Column(name = "destinationdatabase")
	private String destinationDatabase;

	@Column(name = "sourcetables")
	private String sourceTables;

	@Column(name = "destinationtables")
	private String destinationTables;

	@Column(name = "jobtype")
	private String jobType;

	@Column
	private String status;

	@Column(name = "createdtimestamp")
	private Date createdTimestamp;

	@Column(name = "completedtimestamp")
	private Date completedTimestamp;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public DBConnections getSourceDBConnection() {
		return sourceDBConnection;
	}

	public void setSourceDBConnection(DBConnections sourceDBConnection) {
		this.sourceDBConnection = sourceDBConnection;
	}

	public DBConnections getDestinationDBConnection() {
		return destinationDBConnection;
	}

	public void setDestinationDBConnection(DBConnections destinationDBConnection) {
		this.destinationDBConnection = destinationDBConnection;
	}

	public String getSourceDatabase() {
		return sourceDatabase;
	}

	public void setSourceDatabase(String sourceDatabase) {
		this.sourceDatabase = sourceDatabase;
	}

	public String getDestinationDatabase() {
		return destinationDatabase;
	}

	public void setDestinationDatabase(String destinationDatabase) {
		this.destinationDatabase = destinationDatabase;
	}

	public String getSourceTables() {
		return sourceTables;
	}

	public void setSourceTables(String sourceTables) {
		this.sourceTables = sourceTables;
	}

	public String getDestinationTables() {
		return destinationTables;
	}

	public void setDestinationTables(String destinationTables) {
		this.destinationTables = destinationTables;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Date getCompletedTimestamp() {
		return completedTimestamp;
	}

	public void setCompletedTimestamp(Date completedTimestamp) {
		this.completedTimestamp = completedTimestamp;
	}

	public int getJobId() {
		return jobId;
	}

	public TransferJobs(User user, DBConnections sourceDBConnection, DBConnections destinationDBConnection,
			String sourceDatabase, String destinationDatabase, String sourceTables, String destinationTables,
			String jobType, String status, Date createdTimestamp, Date completedTimestamp) {
		this.user = user;
		this.sourceDBConnection = sourceDBConnection;
		this.destinationDBConnection = destinationDBConnection;
		this.sourceDatabase = sourceDatabase;
		this.destinationDatabase = destinationDatabase;
		this.sourceTables = sourceTables;
		this.destinationTables = destinationTables;
		this.jobType = jobType;
		this.status = status;
		this.createdTimestamp = createdTimestamp;
		this.completedTimestamp = completedTimestamp;
	}

	public TransferJobs() {
	}

	@Override
	public String toString() {
		return "TransferJobs [jobId=" + jobId + ", user=" + user + ", sourceDBConnection=" + sourceDBConnection
				+ ", destinationDBConnection=" + destinationDBConnection + ", sourceDatabase=" + sourceDatabase
				+ ", destinationDatabase=" + destinationDatabase + ", sourceTables=" + sourceTables
				+ ", destinationTables=" + destinationTables + ", jobType=" + jobType + ", status=" + status
				+ ", createdTimestamp=" + createdTimestamp + ", completedTimestamp=" + completedTimestamp + "]";
	}

}
