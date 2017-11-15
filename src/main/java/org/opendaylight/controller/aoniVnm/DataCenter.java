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


public class DataCenter  {
    public Long cpuRes;
    public Long domainId;
    public Long LSP;
    public Long ID = 0x0L;
    private double _weight = 0;
    private static final Logger LOG = LoggerFactory.getLogger(DataCenter.class);

    public DataCenter(){};
    public DataCenter(Long ID,Long cpu,Long domain,Long lsp){
        this.ID  = ID;
        this.cpuRes=cpu;
        this.domainId=domain;
        this.LSP = lsp ;
       // LOG.info("_id={},cpures={},domainId={},LSP={}.",ID,cpuRes,domainId,LSP);
    }
    public void getDataCenter()
    {
        LOG.info("getDataCenter : cpures={},domainId={},LSP={}.",cpuRes,domainId,LSP);
    }
    /*not sure*/
    public Long getID()
    {
        return ID ;
    }

    public Long getLSP() {
        return LSP;
    }

    public String toString()
    {
        return ""+ID ;
    }

    public double get_weight()
    {
        return _weight;
    }
    public void set_weight(double status)
    {
        _weight = status;
    }
    /*public int compareTo(Vertex r_vertex)
    {
        double diff = this._weight - r_vertex.distance;
        if(diff > 0)
            return 1;
        else if(diff < 0)
            return -1;
        else
            return 0;
    }
    */
}
