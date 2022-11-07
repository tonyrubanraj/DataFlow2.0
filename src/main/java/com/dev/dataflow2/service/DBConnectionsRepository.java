/**
 * 
 */
package com.dev.dataflow2.service;

import org.springframework.data.repository.CrudRepository;

import com.dev.dataflow2.model.DBConnections;

/**
 * @author tonyr
 *
 */
public interface DBConnectionsRepository extends CrudRepository<DBConnections, Integer> {

}
