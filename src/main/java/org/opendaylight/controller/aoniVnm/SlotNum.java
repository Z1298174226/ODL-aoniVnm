/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

public class SlotNum {

    public Boolean[] slot;
    public Long[] connectionID;
    public int MaxAvailNum;

    public SlotNum(int l){
        this.slot = new Boolean[l];
       this. connectionID = new Long[l];
        for(int i=0; i<l; i++){
            this.slot[i] = false;
            this.connectionID[i] = 0x00L;
        }
        this.MaxAvailNum = l;
    }
}