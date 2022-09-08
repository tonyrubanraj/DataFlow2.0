/**
 * 
 */
package com.dev.dataflow2.service;

import org.springframework.data.repository.CrudRepository;

import com.dev.dataflow2.model.Job;

/**
 * @author tonyr
 *
 */
public interface JobRepository extends CrudRepository<Job, Integer> {

}
