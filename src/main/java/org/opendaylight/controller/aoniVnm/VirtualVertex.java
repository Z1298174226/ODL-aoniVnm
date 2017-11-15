/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

public class VirtualVertex{

    public Long domainReq;
    public Long name;
    public Long cpuReq;
    public Long id;

    public VirtualVertex(Long d,Long n,Long c){
        this.domainReq=d;
        this.name=n;
        this.cpuReq=c;
        this.id=d*10+n;//function?
    }
}
