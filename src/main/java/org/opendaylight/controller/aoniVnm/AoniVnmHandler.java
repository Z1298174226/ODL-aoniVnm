/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;


import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.errorinform.rev161109.PacketErrorInform;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.errorinform.rev161109.PacketErrorInformListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.pathmodreply.rev161109.PacketPathModReply;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.pathmodreply.rev161109.PacketPathModReplyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

//import org.opendaylight.controller.aoniBod.SetupManager;

class AoniVnmHandler implements PacketPathModReplyListener, PacketErrorInformListener {

    private static final Logger LOG = LoggerFactory.getLogger(AoniVnmHandler.class);

    // public static Object _lock = new Object();

    public static int addCounter;
    public static int deleteCounter;
    public static int modifyCounter;

    public static boolean addFlag = true;
    public static boolean deleteFlag = true;
    public static boolean modifyFlag = true;

    public static Set<Long> addReply = new HashSet<Long>();
    public static Set<Long> deleteReply = new HashSet<Long>();
    public static Set<Long> modifyReply = new HashSet<Long>();

    public static void setAddCounter(int counter){
        addFlag = true;
        addCounter = counter;
    }

    public static void setDeleteCounter(int counter){
        deleteFlag = true;
        deleteCounter = counter;
    }

    public static void setModifyCounter(int counter){
        modifyFlag = true;
        modifyCounter = counter;
    }


    @Override
    public void onPacketPathModReply(PacketPathModReply notification){

        // synchronized(_lock){
        if(notification.getOperateStatus()==0xffffffffL){
            if(notification.getCommandType()==0xffffL){
                if(addFlag){
                    LOG.info("====AddFlow reply from node :{}",notification.getNodeId());
                    addReply.add(notification.getNodeId());
                    if(addReply.size() == addCounter){
                        LOG.info("====AddFlow Reply nodeSet :{}",addReply);
                        addFlag = false;
                        addReply = new HashSet<Long>();
                        // _lock.notify();
                    }
                }
            }
            if(notification.getCommandType()==0xffff0000L){
                if(deleteFlag){
                    LOG.info("====DeleteFlow reply from node :{}",notification.getNodeId());
                    deleteReply.add(notification.getNodeId());
                    if(deleteReply.size() == deleteCounter){
                        LOG.info("====DeleteFlow Reply nodeSet :{}",deleteReply);
                        deleteFlag = false;
                        deleteReply = new HashSet<Long>();
                        // _lock.notify();
                    }
                }
            }
            if(notification.getCommandType()==0xffffffffL){
                if(modifyFlag){
                    LOG.info("====ModifyFlow reply from node :{}",notification.getNodeId());
                    modifyReply.add(notification.getNodeId());
                    if(modifyReply.size() == modifyCounter){
                        modifyFlag = false;
                        LOG.info("====ModifyFlow Reply nodeSet :{}",modifyReply);
                        modifyReply = new HashSet<Long>();
                        // _lock.notify();
                    }
                }
            }

        }
        // }
    }

    @Override
    public void onPacketErrorInform(PacketErrorInform notification){

        LOG.info("====Run onPacketErrorInform in AoniBodHandler,notification={}",notification);
        NodeConnectorRef nodeRef = notification.getIngress();
        Long nodeIP = notification.getNodeIP();
        Long remoteNodeIP = notification.getRemoteNodeIP();
        LOG.info("====Error Link from node {} to {} is found",nodeIP,remoteNodeIP);
      //  SendPacket.sendErrorAck(nodeIP,nodeRef);
       // TopoResource.deletePhyErrorLink(nodeIP, remoteNodeIP);
        //SetupManager.storeErrorService(nodeIP, remoteNodeIP);//hlk modified 6.24
    }


}