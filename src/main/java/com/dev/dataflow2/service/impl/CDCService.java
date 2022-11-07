package com.dev.dataflow2.service.impl;

import com.dev.dataflow2.dao.DBConnectionsDao;
import com.dev.dataflow2.dto.TransferJobsDto;
import com.dev.dataflow2.model.DBConnections;
import com.dev.dataflow2.service.DatabaseCDCService;
import com.dev.dataflow2.utils.DatabaseUtils;

public class CDCService extends Thread {

	DatabaseCDCService dbCDCService;
	DBConnectionsDao dbConnectionsDao;
	TransferJobsDto transferJobsDto;

	public CDCService(DBConnectionsDao dbConnectionsDao, TransferJobsDto transferJobsDto) {
		DBConnections source = dbConnectionsDao.getById(transferJobsDto.getSourceId());
		this.dbCDCService = DatabaseUtils.getDBCDCService(source.getDbType());
		this.dbConnectionsDao = dbConnectionsDao;
		this.transferJobsDto = transferJobsDto;
	}

	public void run() {
		System.out.println("New thread started running... " + currentThread().getName());
		dbCDCService.produceStreamingData(dbConnectionsDao, transferJobsDto);
	}

	@Override
	public void interrupt() {
		System.out.println("Stopping thread... " + currentThread().getName());
		dbCDCService.stopStreamingData();
		super.interrupt();
	}

}
