/**
 * 
 */
package com.dev.dataflow2.service;

import com.dev.dataflow2.dao.DBConnectionsDao;
import com.dev.dataflow2.dto.TransferJobsDto;

/**
 * @author tonyr
 *
 */
public abstract class DatabaseCDCService {

	public abstract void produceStreamingData(DBConnectionsDao dbConnectionsDao, TransferJobsDto transferJobsDto);

	public abstract void consumeStreamingData();

	public abstract void stopStreamingData();
}
