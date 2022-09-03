/**
 * 
 */
package com.dev.dataflow2.service;

import org.springframework.data.repository.CrudRepository;

import com.dev.dataflow2.model.UserConnections;

/**
 * @author tonyr
 *
 */
public interface UserConnectionsRepository extends CrudRepository<UserConnections, Integer> {

}
