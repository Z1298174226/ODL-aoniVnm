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

import java.util.ArrayList;
import java.util.List;


public class PacketResource {
    public Long vnmID;
    public List<DataCenter> DataCenterList = new ArrayList<DataCenter>();//virtual datacenters
    public List<Edge> EdgeList = new ArrayList<Edge>();//new

    private static final Logger LOG = LoggerFactory.getLogger(PacketResource.class);

    public PacketResource(Long vnmid, List<VirtualVertex> vnmNode, List<VirtualEdge> virLink) {

//将虚拟化请求转换为虚拟拓扑 start
        vnmID = vnmid;
        for (VirtualVertex virtualVertex : vnmNode) {
            if (!isIn(virtualVertex, DataCenterList)) {
                DataCenter dataCenter = new DataCenter(virtualVertex.name, virtualVertex.cpuReq, virtualVertex.domainReq, new Long((long) (1)));
                DataCenterList.add(dataCenter);
            } else {
                for (DataCenter dataCenter : DataCenterList) {
                    if (dataCenter.ID.equals(virtualVertex.name) && dataCenter.domainId.equals(virtualVertex.domainReq)) {
                        dataCenter.LSP = dataCenter.LSP + 0x1L;
                    }
                }
            }
            LOG.info("The size of DataCenterList:{}  The size of vnmNode is :{} ",DataCenterList.size(),vnmNode.size());
        }
        for (VirtualEdge virlink : virLink) {
            //Edge edge = new Edge(virlink.src.name,virlink.des.name,);
            Edge edge = new Edge(virlink.src.name, virlink.des.name, new Long((long) (1)), new SlotNum((int)Math.floor(Math.ceil((virlink.bandReq * 1.0) / 12.5))));
            EdgeList.add(edge);
        }
        LOG.info("The size of EdgeList is:{}",EdgeList.size());

//将虚拟化请求转换为虚拟拓扑 end

    }
    //判断虚拟点是否在List中
    private static boolean isIn(VirtualVertex vs, List<DataCenter> vsA) {
        boolean flag = false;
        if (vsA == null) {
            return false;
        } else {
            for (DataCenter v : vsA) {
                if (v.ID.equals(vs.name) && v.domainId.equals(vs.domainReq)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }
    }

}