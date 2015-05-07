/**
 * Copyright (c) 2011 The Regents of The University of Michigan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer;
 * redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution;
 * neither the name of the copyright holders nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author David Meisner (meisner@umich.edu)
 *
 */

package core;

import java.io.Serializable;
import java.util.Vector;

import sawt.RandomNGenerator;
import sawt.ServiceTimeFilter;
import sawt.SurvivorGenerator;
import core.Constants.FilterType;
import core.Constants.WorkType;
import datacenter.DataCenter;

/**
 * ExperimentInput contains datacenter used in the experiment (and all the
 * components in the datacenter e.g., servers).
 *
 * @author David Meisner (meisner@umich.edu)
 */
public final class ExperimentInput implements Serializable {

    /**
     * The serialization id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The datacenter modeled in the simulation.
     */
    private DataCenter datacenter;
    
    /**
     * 
     */
    private WorkType workType;
    
    /**
     * 
     */
    private ServiceTimeFilter serviceTimeFilter;
    
    /**
     * 
     */
    private Vector<FilterType> filterTypes;
    
    private SurvivorGenerator survivorGenerator;
    
    private RandomNGenerator randomNGenerator;

    /**
     * Creates a new ExperimentInput.
     */
    public ExperimentInput() {
    	this.filterTypes = new Vector<FilterType>();
    }

    /**
     * Sets the datacenter for the input.
     * @param dataCenter the datacenter
     */
    public void setDataCenter(final DataCenter dataCenter) {
        this.datacenter = dataCenter;
    }

    /**
     * Gets the input datacenter. Can be null if not set.
     * @return the datacenter
     */
    public DataCenter getDataCenter() {
        return this.datacenter;
    }

    /**
     * 
     */
    public void setWorkType(final WorkType workType) {
    	this.workType = workType;
    }
    
    public WorkType getWorkType() {
    	return this.workType;
    }
    
    public void setServiceTimeFilter(ServiceTimeFilter serviceTimeFilter) {
    	this.serviceTimeFilter = serviceTimeFilter;
    }
    
    public ServiceTimeFilter getServiceTimeFilter() {
    	return this.serviceTimeFilter;
    }
    
    public void setFilterType(final FilterType filterType) {
    	this.filterTypes.add(filterType);
    }
    
    public Vector<FilterType> getFilterTypes() {
    	return this.filterTypes;
    }
    
    public boolean containsFilterType(final FilterType filterType) {
    	return this.filterTypes.contains(filterType);
    }
    
    public void setSurvivorGenerator(SurvivorGenerator survivorGenerator) {
    	this.survivorGenerator = survivorGenerator;
    }
    
    public SurvivorGenerator getSurvivorGenerator() {
    	return this.survivorGenerator;
    }
    
    public void setRandomNGenerator(RandomNGenerator randomNGenerator) {
    	this.randomNGenerator = randomNGenerator;
    }
    
    public RandomNGenerator getRandomNGenerator() {
    	return this.randomNGenerator;
    }
}