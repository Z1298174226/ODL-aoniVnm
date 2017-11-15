/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.lang.Long;

public class Edge {
    private static final Logger LOG = LoggerFactory.getLogger(Edge.class);

    public  Long src;
    public  Long des;
    public  Long weight;
    public  SlotNum slotNum;

    public Edge(){};
    public Edge(Long src,Long des,Long weight,SlotNum slotNum) {
        this.src = src;
        this.des = des;
        this.weight = weight;
        this.slotNum = slotNum;
     //   LOG.info("The new edge : src:{},des:{},weight:{}",this.src,this.des,this.weight);
    }
    public Edge(Long src,Long des,Long weight) {
        this.src = src;
        this.des = des;
        this.weight = weight;
     //   LOG.info("The new edge : src:{},des:{},weight:{}",this.src,this.des,this.weight);
    }

/*
    public Long getWeight() {
        return weight;
    }*/
}
