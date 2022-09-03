/**
 * 
 */
package com.dev.dataflow2.service;

import org.springframework.data.repository.CrudRepository;

import com.dev.dataflow2.model.DBConnection;

/**
 * @author tonyr
 *
 */
public interface DBConnectionRepository extends CrudRepository<DBConnection, Integer> {

}
