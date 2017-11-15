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
import java.util.Comparator;
import java.util.Collections;

public class PacketTopo {
    public Long vnmID;
    public int[][] topo;
    public Long NodeIDBase;
    private static final Logger LOG = LoggerFactory.getLogger(PacketTopo.class);

    public PacketTopo(Long vnmid, List<VirtualVertex> vnmNode, List<VirtualEdge> virLink) {
//将虚拟拓扑转换为矩阵的形式 start

        vnmID = vnmid;
        //根据虚拟节点ID升序排列
        Collections.sort(vnmNode, new Comparator<VirtualVertex>() {
            @Override
            public int compare(VirtualVertex o1, VirtualVertex o2) {
                long cpu_1 = o1.name;
                long cpu_2 = o2.name;
                return (cpu_1 < cpu_2) ? -1 : 1;
            }
        });
        //记录最小虚拟节点ID
        NodeIDBase = vnmNode.get(0).name;
        //记录虚拟拓扑矩阵大小
        int ReqScale = vnmNode.size();
        //初始化除自环 均为99999999
        topo = new int[ReqScale][ReqScale];
        for (int i = 0; i < ReqScale; i++) {
            for (int j = 0; j < ReqScale; j++) {
                if (i == j)
                    topo[i][j] = 0;
                else
                    topo[i][j] = 99999999;
            }
        }
        //show
        for (int i = 0; i < ReqScale; i++) {
            LOG.info("topo[i][]:{}", topo[i]);
            for (int j = 0; j < ReqScale; j++) {

            }
        }
        //若为直连 则为1
        for (VirtualEdge virlink : virLink) {
            topo[virlink.src.name.intValue() - 1][virlink.des.name.intValue() - 1] = 1;
        }
        //若为不可达 则为0
        for (int i = 0; i < ReqScale; i++) {
            List<Integer> AllDes = new ArrayList<Integer>();
            for (int j = 0; j < ReqScale; j++) {
                if (topo[i][j] == 1) {
                    AllDes.add(new Integer(j));
                    LOG.info("AllDes are:{}.", AllDes);
                }

                while (!AllDes.isEmpty()) {
                    List<Integer> AllDes_add = new ArrayList<Integer>();

                    for (Integer pass : AllDes) {

                        for (int k = 0; k < ReqScale; k++) {
                            if (topo[pass][k] == 1 && topo[i][k] == 99999999) {
                                AllDes_add.add(new Integer(k));
                                topo[i][k] = 0;
                            }
                        }
                    }
                    AllDes.clear();
                    AllDes.addAll(AllDes_add);
                    LOG.info("AllDes are:{}.", AllDes);

                }


            }
        }
        //show
        for (int i = 0; i < ReqScale; i++) {
            LOG.info("topo[i][]:{}", topo[i]);
            for (int j = 0; j < ReqScale; j++) {

            }
        }
        //将虚拟拓扑转换为矩阵的形式 end


    }

}