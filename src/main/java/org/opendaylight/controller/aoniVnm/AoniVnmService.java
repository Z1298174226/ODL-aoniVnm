/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

/**
 *author: YuBingxin
 *VnmdService is for other bundle(eg. northbound) call this
 */
import java.util.List;

public interface AoniVnmService{

    public boolean setupVnm(Long vnmId,List<List<Long>> request,List<List<Long>> result);
   // public boolean setupVnm_Dyna(Long vnmId,Long ReqDomain,Long ReqType,Long ReqScale,List<List<Long>> result);
    public boolean setupVnm_Dyna_new(Long vnmId,Long ReqDomain,Long ReqType,Long ReqScale,List<List<Long>> result);

    //public boolean protectVon(Long vonId, Long headNodeID, Long tailNodeID, List<Long> workPath, List<Long> protectionPath);

    //public boolean deleteVnm(Long vnmId);

    //public boolean TestDij
    public void iniTopoResource() ;


    }
