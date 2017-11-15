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

import java.util.List;

public class AoniVnmServiceImpl implements AoniVnmService{
    private static final Logger LOG = LoggerFactory.getLogger(AoniVnmServiceImpl.class);
///*
    @Override
    public boolean setupVnm(Long vnmId, List<List<Long>> request,List<List<Long>> result) {
       LOG.info("--Vnm req is undertaking...--");
       return VnmManager.vnmSetup(vnmId,request,result);
      //  return true;
    }
//*/
    /*
    @Override

    public boolean setupVnm_Dyna(Long vnmId, Long ReqDomain, Long ReqType,Long ReqScale, List<List<Long>> result) {
        LOG.info("--Vnm_Dyna req is undertaking...--");
        return VnmManager.vnmSetup_Dyna(vnmId, ReqDomain, ReqType, ReqScale, result);
        //  return true;
    }
    */
@Override
public boolean setupVnm_Dyna_new(Long vnmId, Long ReqDomain, Long ReqType,Long ReqScale, List<List<Long>> result) {
    LOG.info("--Vnm_Dyna req is undertaking...--");
    return VnmManager.vnmSetup_Dyna_new(vnmId, ReqDomain,ReqType, ReqScale,result);
    //  return true;
}
/*
    @Override
    public boolean protectVnm(Long vnmId,Long headNodeID,Long tailNodeID,List<Long> workPath,List<Long> protectionPath){
        LOG.info("--Vnm protection is undertaking...--");
        return VnmManager.vnmProtect(vnmId,headNodeID,tailNodeID,workPath,protectionPath);
    }

    @Override
    public boolean deleteVnm(Long vnmId){

        //return VnmManager.vnmDelete(vnmId);
        return true;
    }*/
public  void iniTopoResource() {
    LOG.info("--Vnm iniTopoResource is undertaking...--");
    VnmManager.iniTopoResource();
}

}