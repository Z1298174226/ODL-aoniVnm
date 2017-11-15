/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;


public class VirtualEdge {
    public VirtualVertex src, des;
    public long linkID;
    public long bandReq;

    public VirtualEdge(Long linkIdReq,Long servBand, Long srcDo, Long srcNo, Long srcCpu, Long destDo, Long destNo,
                       Long destCpu){
        linkID = linkIdReq;
        bandReq = servBand;
        src = new VirtualVertex(srcDo,srcNo,srcCpu);
        des = new VirtualVertex(destDo,destNo,destCpu);
    }

    public long getBandReq() {
        return bandReq;
    }

}
