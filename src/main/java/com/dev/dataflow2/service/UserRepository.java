/**
 * 
 */
package com.dev.dataflow2.service;

import org.springframework.data.repository.CrudRepository;

import com.dev.dataflow2.model.User;

/**
 * @author tonyr
 *
 */
public interface UserRepository extends CrudRepository<User, Integer> {

}
