/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

//import akka.actor.ActorRef;
import org.opendaylight.controller.aoniTed.LinkProperty;
import org.opendaylight.controller.aoniTed.LinkPropertyService;
//import org.osgi.framework.*;
import org.osgi.framework.Bundle; //added for communications between bundles;
import org.osgi.framework.BundleContext; //added for communications between bundles;
import org.osgi.framework.BundleReference; //added for communications between bundles;
import org.osgi.framework.FrameworkUtil;//added for communications between bundles;
import org.osgi.framework.ServiceReference; //added for communications between bundles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.*;
import java.util.Iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;

/*OSGI contact*/


/*
import java.lang.Integer;

import java.lang.Long;
import java.lang.Math;
import java.util.Collection;
import java.util.Map;
import org.opendaylight.controller.aoniBod.SetupManager;
import org.opendaylight.controller.aoniBod.Graph;

*/

public class VnmManager {

    private static final Logger LOG = LoggerFactory.getLogger(VnmManager.class);
    public static int row = 200;
    public static int column = 200;
    public static int[][] phyTopo = new int[row][column];
    public static int[][] phyEdges = new int[row][column];
    public static int[][] phypath_f = new int[row][column];
    public static final double slotband = 12.5;

    public static List<Long> phyNodes = new ArrayList<Long>();
    public static List<Vertex> vertexs = new ArrayList<Vertex>();

    public static boolean firstInit = true;
    public static List<DataCenter> domain1nodes = new ArrayList<>();//datacenter in domain 1
    public static List<DataCenter> domain2nodes = new ArrayList<>();//datacenter in domain 2
    public static List<DataCenter> domain3nodes = new ArrayList<>();//datacenter in domain 3
    public static List<DataCenter> domain4nodes = new ArrayList<>();//datacenter in domain 4
    public static List<DataCenter> domain5nodes = new ArrayList<>();//datacenter in domain 5
    // public static List<Edge> EdgeList = new ArrayList<>();//old
     public static List<Edge> EdgeList = new ArrayList<>();//old
    public static List<Edge> domain1EdgeList = new ArrayList<>();//new
    public static List<Edge> domain2EdgeList = new ArrayList<>();//new
    public static List<Edge> domain3EdgeList = new ArrayList<>();//new
    public static List<Edge> domain4EdgeList = new ArrayList<>();//new
    public static List<Edge> domain5EdgeList = new ArrayList<>();//new

    //public static List<Edge> EdgeList_f = new ArrayList<>();//old
    public static List<Edge> EdgeList_f = new ArrayList<>();//old
    public static List<Edge> domain1EdgeList_f = new ArrayList<>();//new
    public static List<Edge> domain2EdgeList_f = new ArrayList<>();//new
    public static List<Edge> domain3EdgeList_f = new ArrayList<>();//new
    public static List<Edge> domain4EdgeList_f = new ArrayList<>();//new
    public static List<Edge> domain5EdgeList_f = new ArrayList<>();//new


    public static List<Long> vnmIDVm = new ArrayList<>();
    public static HashMap<Long, List<VirtualEdge>> vnmLinkList = new HashMap<>();//vnmID->linkID，
    public static HashMap<Long, List<VirtualVertex>> vnmNodeList = new HashMap<>();
    public static HashMap<Long, List<Long>> vnmRouteList = new HashMap<>();//RouteID->route,根据link查对应路由
    public static HashMap<Long, List<List<Long>>> RouteList = new HashMap<Long, List<List<Long>>>();//RouteID->route,根据link查对应路由
    public static HashMap<Long, HashMap<Long, DataCenter>> vnmDataCenterList = new HashMap<Long, HashMap<Long, DataCenter>>();
  //  public static HashMap<Long, List<List<Long>>> VirRoutesList = new HashMap<Long, List<List<Long>>>();//src -- des for front
  //  public static HashMap<Long, List<List<Long>>> VirRequestsList = new HashMap<Long, List<List<Long>>>();//vnmID+virLink_List
    public static HashMap<Long, PacketTopo> VirTopoList = new HashMap<Long, PacketTopo>();
    public static HashMap<Long, PacketResource> VirResourceList = new HashMap<Long, PacketResource>();

    /*
      LocalActor
       */
   // public static ActorSystem system;
   // public static ActorRef localActor = system.actorOf(Props.create(LocalDemo.LocalActor.class), "LocalActor");
    /*
    * Bundle Contatct
    * */
    private static BundleContext getBundleContext() {
        ClassLoader tlcl = Thread.currentThread().getContextClassLoader();
        Bundle bundle = null;

        if (tlcl instanceof BundleReference) {
            bundle = ((BundleReference) tlcl).getBundle();
        } else {
            LOG.info("Unable to determine the bundle context based on " +
                    "thread context classloader.");
            bundle = FrameworkUtil.getBundle(Graph.class);
        }
        return (bundle == null ? null : bundle.getBundleContext());
    }

    /*
* Initializa the topo from Ted
* and invoke Floyd
* */
    public static void iniTopoResource() {

        LOG.info("===YBX aoniVNM -- TopoResource Start initTopoResource_Floyd()");
        BundleContext ctx = getBundleContext();
        ServiceReference linkPropertyServiceReference = ctx.getServiceReference(LinkPropertyService.class);
        LinkProperty linkProperty = (LinkProperty) ctx.getService(linkPropertyServiceReference);

        if (linkProperty != null) {
            phyTopo = linkProperty.getPhysicalTopoMatrix(); // 0  1
            phyEdges = linkProperty.getPhysicalEdgesMatrix();//Edges weight
            phyNodes = linkProperty.getPhysicalNodes();
            if (phyNodes != null) {
                LOG.info("===YBX aoniVnm -- phyNodes are {},size is:{}", phyNodes, phyNodes.size());

        /*initialization of dataCenter in phyNodes*/
                for (int i = 0; i < phyNodes.size(); i++) {

                    if (phyNodes.get(i).intValue() > 0 && phyNodes.get(i).intValue() < 201) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x1L, 0x1f400L);//1800G YBX edited on 05/17/17
                        domain1nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 200 && phyNodes.get(i).intValue() < 401) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x2L, 0x1f400L);//1800G YBX edited on 05/17/17
                        domain2nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 400 && phyNodes.get(i).intValue() < 601) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x3L, 0x2710L);//400G YBX edited on 05/17/17
                        domain3nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 600 && phyNodes.get(i).intValue() < 801) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x4L, 0x2710L);//400G YBX edited on 05/17/17
                        domain4nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 800 && phyNodes.get(i).intValue() < 1001) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x5L, 0x2710L);//400G YBX edited on 05/17/17
                        domain5nodes.add(dataCenter);
                    } else {
                        LOG.info("vnmNode{} is with name of {} is not DataCenter@#@#", i, phyNodes.get(i));
                    }
                }
                LOG.info("===YBX aoniVnm -- DataCenter1 nodes : {}", domain1nodes);
                LOG.info("===YBX aoniVnm -- DataCenter2 nodes : {}", domain2nodes);
                LOG.info("===YBX aoniVnm -- DataCenter3 nodes : {}", domain3nodes);
                LOG.info("===YBX aoniVnm -- DataCenter4 nodes : {}", domain4nodes);
                LOG.info("===YBX aoniVnm -- DataCenter5 nodes : {}", domain5nodes);
                LOG.info("Vnm Datacenters are Initialized.");
            }
        }
        if (phyTopo != null) {//Map<Long,List<Long>>  Map<node,List<remoteList>>
            //Initialize the topo edge resource from original phyedge[][]
            LOG.info("----The phyEdges[][] are : ----");
            for (int i = 0; i < phyNodes.size(); i++) {
                for (int j = 0; j < phyNodes.size(); j++) {
                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        Edge edge = new Edge(new Long((long) (i + 1)), new Long((long) (j + 1)), new Long((long) (phyEdges[i][j])), new SlotNum(1280));
                        // EdgeList.add(edge);//old
                        if (i > -1 && i < 200 && j > -1 && j < 200) {
                            domain1EdgeList.add(edge);//new
                        } else if (i > 199 && i < 400 && j > 199 && j < 400) {
                            domain2EdgeList.add(edge);//new
                        } else if (i > 399 && i < 600 && j > 399 && j < 600) {
                            domain3EdgeList.add(edge);//new
                        } else if (i > 599 && i < 800 && j > 599 && j < 800) {
                            domain4EdgeList.add(edge);//new
                        } else if (i > 799 && i < 1000 && j > 799 && j < 1000) {
                            domain5EdgeList.add(edge);//new
                        } else {
                            LOG.info("vnmEdge{}{} is with name of {} is not DataCenter@#@#", i, j, phyEdges[i][j]);
                        }
                        //    LOG.info("edge.weight:{}", edge.weight);
                    }
                }
            }
            LOG.info("--------------------------");

            //Initialize the phypath_f[][] in Floyd //all
            LOG.info("----The original phypath_f[][] is : ----");
            for (int i = 0; i < phyEdges.length; i++) {
                for (int j = 0; j < phyEdges.length; j++) {

                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        phypath_f[i][j] = j + 1;
                    } else {
                        phypath_f[i][j] = j + 1;
                    }

                    //    LOG.info(" {} ", phypath_f[i][j]);
                }
                //  LOG.info("");
            }
            LOG.info("--------------------------");

            //Run the Floyd in all
            //Get the new phyedges[][] and phypath_f[][]
            /*
            LOG.info("----The new phypath_f[][] is : ----");
            for (int k = 0; k < phyEdges.length; k++) {
                for (int i = 0; i < phyEdges.length; i++) {
                    for (int j = 0; j < phyEdges.length; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");

                for (int i = 0; i < phyEdges.length; i++) {
                    for (int j = 0; j < phyEdges.length; j++) {
                        //        LOG.info(" {} ", phypath_f[i][j]);
                    }
                    //    LOG.info("");
                }
                //     LOG.info("--------------------------");

            }

            LOG.info("--------------------------");
            */
            //Run the Floyd in 5 domains
            //Get the new phyedges[][] and phypath_f[][]
            LOG.info("----The new phypath_f[][] is : ----");
            //domain1
            for (int k = 0; k < 200; k++) {
                for (int i = 0; i < 200; i++) {
                    for (int j = 0; j < 200; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
//domain2
            /*
            for (int k = 200; k < 400; k++) {
                for (int i = 200; i < 400; i++) {
                    for (int j = 200; j < 400 ;j++){
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain3
            /*
            for (int k = 400; k < 600; k++) {
                for (int i = 400; i < 600; i++) {
                    for (int j = 400; j < 600; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain4
            /*
            for (int k = 600; k < 800; k++) {
                for (int i = 600; i < 800; i++) {
                    for (int j = 600; j < 800; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain5
            /*
            for (int k = 800; k < 1000; k++) {
                for (int i = 800; i < 1000; i++) {
                    for (int j = 800; j < 1000; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            LOG.info("--------------------------");
*/
            LOG.info("----The phypathes[][] after Floryd are : ----");
            for (int i = 0; i < phyNodes.size(); i++) {
                for (int j = 0; j < phyNodes.size(); j++) {
                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        Edge edge = new Edge(new Long((long) (i + 1)), new Long((long) (j + 1)), new Long((long) (phyEdges[i][j])));
                        //EdgeList_f.add(edge);//old
                        if (i > -1 && i < 200 && j > -1 && j < 200) {
                            domain1EdgeList_f.add(edge);//new
                        } else if (i > 199 && i < 400 && j > 199 && j < 400) {
                            domain2EdgeList_f.add(edge);//new
                        } else if (i > 399 && i < 600 && j > 399 && j < 600) {
                            domain3EdgeList_f.add(edge);//new
                        } else if (i > 599 && i < 800 && j > 599 && j < 800) {
                            domain4EdgeList_f.add(edge);//new
                        } else if (i > 799 && i < 1000 && j > 799 && j < 1000) {
                            domain5EdgeList_f.add(edge);//new
                        } else {
                            LOG.info("vnmEdge_f{}{} is with value of {} is not in DataCenter domain.", i, j, phyEdges[i][j]);
                        }
                        //   LOG.info("edge.weight:{}",edge.weight);
                    }
                }
            }
            LOG.info("--------------------------");

                /*
                * Show the result in log
                * */

/*          Iterator iter = vertexs.iterator();
            LOG.info("--------------------------");
            while (iter.hasNext()) {
                Vertex ver = (Vertex) iter.next();
                LOG.info("The vertex id is :{}" , ver.getVertext_id());
                LOG.info("The shortest edge is {}:", ver.getDistance());
                LOG.info("The passed vertexs sre :");
                for (Long path : ver.getPath()) {
                    LOG.info("{} ", path);
                }
                LOG.info("--------------------------");
            }
            */
            LOG.info("Vnm Edges are initialized.");

        }
    }

    /*
* Initializa the topo from Ted
* and invoke Floyd
* */
    public static void iniTopoResource_C1(Long vnmID) {

        LOG.info("===YBX aoniVNM -- TopoResource Start initTopoResource_Floyd()");
        BundleContext ctx = getBundleContext();
        ServiceReference linkPropertyServiceReference = ctx.getServiceReference(LinkPropertyService.class);
        LinkProperty linkProperty = (LinkProperty) ctx.getService(linkPropertyServiceReference);

        if (linkProperty != null) {
            phyTopo = linkProperty.getPhysicalTopoMatrix(vnmID); // 0  1
            phyEdges = linkProperty.getPhysicalEdgesMatrix(vnmID);//Edges weight
            /*
            phyNodes = linkProperty.getPhysicalNodes();
            if (phyNodes != null) {
                LOG.info("===YBX aoniVnm -- phyNodes are {},size is:{}", phyNodes, phyNodes.size());

       //initialization of dataCenter in phyNodes
                for (int i = 0; i < phyNodes.size(); i++) {

                    if (phyNodes.get(i).intValue() > 0 && phyNodes.get(i).intValue() < 201) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x1L, 0x1f400L);//1800G YBX edited on 05/17/17
                        domain1nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 200 && phyNodes.get(i).intValue() < 401) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x2L, 0x1f400L);//1800G YBX edited on 05/17/17
                        domain2nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 400 && phyNodes.get(i).intValue() < 601) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x3L, 0x2710L);//400G YBX edited on 05/17/17
                        domain3nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 600 && phyNodes.get(i).intValue() < 801) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x4L, 0x2710L);//400G YBX edited on 05/17/17
                        domain4nodes.add(dataCenter);
                    } else if (phyNodes.get(i).intValue() > 800 && phyNodes.get(i).intValue() < 1001) {
                        DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x5L, 0x2710L);//400G YBX edited on 05/17/17
                        domain5nodes.add(dataCenter);
                    } else {
                        LOG.info("vnmNode{} is with name of {} is not DataCenter@#@#", i, phyNodes.get(i));
                    }
                }
                LOG.info("===YBX aoniVnm -- DataCenter1 nodes : {}", domain1nodes);
                LOG.info("===YBX aoniVnm -- DataCenter2 nodes : {}", domain2nodes);
                LOG.info("===YBX aoniVnm -- DataCenter3 nodes : {}", domain3nodes);
                LOG.info("===YBX aoniVnm -- DataCenter4 nodes : {}", domain4nodes);
                LOG.info("===YBX aoniVnm -- DataCenter5 nodes : {}", domain5nodes);
                LOG.info("Vnm Datacenters are Initialized.");
            }
            */
        }
        if (phyTopo != null) {//Map<Long,List<Long>>  Map<node,List<remoteList>>
            //Initialize the topo edge resource from original phyedge[][]
            LOG.info("----The phyEdges[][] are : ----");
            /*
            for (int i = 0; i < phyNodes.size(); i++) {
                for (int j = 0; j < phyNodes.size(); j++) {
                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        Edge edge = new Edge(new Long((long) (i + 1)), new Long((long) (j + 1)), new Long((long) (phyEdges[i][j])), new SlotNum(1280));
                        // EdgeList.add(edge);//old
                        if (i > -1 && i < 200 && j > -1 && j < 200) {
                            domain1EdgeList.add(edge);//new
                        } else if (i > 199 && i < 400 && j > 199 && j < 400) {
                            domain2EdgeList.add(edge);//new
                        } else if (i > 399 && i < 600 && j > 399 && j < 600) {
                            domain3EdgeList.add(edge);//new
                        } else if (i > 599 && i < 800 && j > 599 && j < 800) {
                            domain4EdgeList.add(edge);//new
                        } else if (i > 799 && i < 1000 && j > 799 && j < 1000) {
                            domain5EdgeList.add(edge);//new
                        } else {
                            LOG.info("vnmEdge{}{} is with name of {} is not DataCenter@#@#", i, j, phyEdges[i][j]);
                        }
                        //    LOG.info("edge.weight:{}", edge.weight);
                    }
                }
            }*/

            LOG.info("--------------------------");

            //Initialize the phypath_f[][] in Floyd //all
            LOG.info("----The original phypath_f[][] is : ----");
            for (int i = 0; i < phyEdges.length; i++) {
                for (int j = 0; j < phyEdges.length; j++) {

                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        phypath_f[i][j] = j + 1;
                    } else {
                        phypath_f[i][j] = j + 1;
                    }

                    //    LOG.info(" {} ", phypath_f[i][j]);
                }
                //  LOG.info("");
            }
            LOG.info("--------------------------");

            //Run the Floyd in all
            //Get the new phyedges[][] and phypath_f[][]
            /*
            LOG.info("----The new phypath_f[][] is : ----");
            for (int k = 0; k < phyEdges.length; k++) {
                for (int i = 0; i < phyEdges.length; i++) {
                    for (int j = 0; j < phyEdges.length; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");

                for (int i = 0; i < phyEdges.length; i++) {
                    for (int j = 0; j < phyEdges.length; j++) {
                        //        LOG.info(" {} ", phypath_f[i][j]);
                    }
                    //    LOG.info("");
                }
                //     LOG.info("--------------------------");

            }

            LOG.info("--------------------------");
            */
            //Run the Floyd in 5 domains
            //Get the new phyedges[][] and phypath_f[][]
            LOG.info("----The new phypath_f[][] is : ----");
            //domain1
            for (int k = 0; k < 200; k++) {
                for (int i = 0; i < 200; i++) {
                    for (int j = 0; j < 200; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
//domain2
            /*
            for (int k = 200; k < 400; k++) {
                for (int i = 200; i < 400; i++) {
                    for (int j = 200; j < 400 ;j++){
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain3
            /*
            for (int k = 400; k < 600; k++) {
                for (int i = 400; i < 600; i++) {
                    for (int j = 400; j < 600; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain4
            /*
            for (int k = 600; k < 800; k++) {
                for (int i = 600; i < 800; i++) {
                    for (int j = 600; j < 800; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            */
            //domain5
            /*
            for (int k = 800; k < 1000; k++) {
                for (int i = 800; i < 1000; i++) {
                    for (int j = 800; j < 1000; j++) {
                        //    LOG.info(" {} ", phyEdges[i][j]);

                        if (phyEdges[i][j] > phyEdges[i][k] + phyEdges[k][j]) {
                            phyEdges[i][j] = phyEdges[i][k] + phyEdges[k][j];
                            phypath_f[i][j] = k + 1;
                        } else {
                            phyEdges[i][j] = phyEdges[i][j];
                        }
                    }
                }
                //      LOG.info("--------------------------");
            }
            LOG.info("--------------------------");
*/
            LOG.info("----The phypathes[][] after Floryd are : ----");
            for (int i = 0; i < phyNodes.size(); i++) {
                for (int j = 0; j < phyNodes.size(); j++) {
                    if (phyEdges[i][j] > 0 && phyEdges[i][j] < 99999999) {
                        Edge edge = new Edge(new Long((long) (i + 1)), new Long((long) (j + 1)), new Long((long) (phyEdges[i][j])));
                        //EdgeList_f.add(edge);//old
                        if (i > -1 && i < 200 && j > -1 && j < 200) {
                            domain1EdgeList_f.add(edge);//new
                        } else if (i > 199 && i < 400 && j > 199 && j < 400) {
                            domain2EdgeList_f.add(edge);//new
                        } else if (i > 399 && i < 600 && j > 399 && j < 600) {
                            domain3EdgeList_f.add(edge);//new
                        } else if (i > 599 && i < 800 && j > 599 && j < 800) {
                            domain4EdgeList_f.add(edge);//new
                        } else if (i > 799 && i < 1000 && j > 799 && j < 1000) {
                            domain5EdgeList_f.add(edge);//new
                        } else {
                            LOG.info("vnmEdge_f{}{} is with value of {} is not in DataCenter domain.", i, j, phyEdges[i][j]);
                        }
                        //   LOG.info("edge.weight:{}",edge.weight);
                    }
                }
            }
            LOG.info("--------------------------");

                /*
                * Show the result in log
                * */

/*          Iterator iter = vertexs.iterator();
            LOG.info("--------------------------");
            while (iter.hasNext()) {
                Vertex ver = (Vertex) iter.next();
                LOG.info("The vertex id is :{}" , ver.getVertext_id());
                LOG.info("The shortest edge is {}:", ver.getDistance());
                LOG.info("The passed vertexs sre :");
                for (Long path : ver.getPath()) {
                    LOG.info("{} ", path);
                }
                LOG.info("--------------------------");
            }
            */
            LOG.info("Vnm Edges are initialized.");

        }
    }
    ///*
    public static boolean vnmSetup(Long vnmID, List<List<Long>> virlink_list, List<List<Long>> vnmRoutes) {
        if (firstInit) {
            // Graph.storeGraph();//topo initialization from ted should without Dij
            iniTopoResource();
            // dataCenterInit();in iniToporesource
            firstInit = false;
            LOG.info("===YBX aoniVnm -- Topo and datacenters are initialized!!!!");
        }
        //von记录-#:1
        vnmIDVm.add(vnmID);

        //初始化虚拟请求的链路请求
        List<VirtualEdge> VnmLink = new ArrayList<>();
        for (List<Long> list : virlink_list) {
            LOG.info("===YBX aoniVnm -- von linkID: {}", list.get(0));
            LOG.info("===YBX aoniVnm -- von bandReq: {}", list.get(1));
            LOG.info("===YBX aoniVnm -- von sourceNode domain: {}", list.get(2));
            LOG.info("===YBX aoniVnm -- von sourceNode num: {}", list.get(3));
            LOG.info("===YBX aoniVnm -- von sourceNode cpu: {}", list.get(4));
            LOG.info("===YBX aoniVnm -- von destNode domain: {}", list.get(5));
            LOG.info("===YBX aoniVnm -- von destNode num: {}", list.get(6));
            LOG.info("===YBX aoniVnm -- von destNode cpu: {}", list.get(7));
            LOG.info("----------------------------");

            VirtualEdge vl = new VirtualEdge(list.get(0), list.get(1), list.get(2), list.get(3),
                    list.get(4), list.get(5), list.get(6), list.get(7));
            VnmLink.add(vl);
            LOG.info("===YBX aoniVnm -- von in request are {},{},{},{},{},{},{},{}@@@",
                    vl.linkID, vl.bandReq, vl.src.domainReq, vl.src.name, vl.src.cpuReq,
                    vl.des.domainReq, vl.des.name, vl.des.cpuReq);
        }
        LOG.info("The number of VnmLink in reqest is {}", VnmLink.size());

        LOG.info("The vonlink num is {}", VnmLink.size());

        //对虚拟请求的链路根据带宽排序
        Collections.sort(VnmLink, new Comparator<VirtualEdge>() {
            @Override
            public int compare(VirtualEdge o1, VirtualEdge o2) {
                long bandReq_1 = o1.bandReq;
                long bandReq_2 = o2.bandReq;
                return (bandReq_1 > bandReq_2) ? -1 : 1;
            }
        });

        //存VON的link
        vnmLinkList.put(vnmID, VnmLink);

        //初始化虚拟请求的RouteId
        List<Long> vnmRoute = new ArrayList<>();
        for (int i = 0; i < VnmLink.size(); i++) {
            vnmRoute.add(vnmID * 100000 + i);
        }
        LOG.info("The number of routes in reqest is {}", vnmRoute.size());
        LOG.info("虚拟请求的routeId是{}@@", vnmRoute);

        //von记录-#:2
        vnmRouteList.put(vnmID, vnmRoute);

        //初始化虚拟请求的节点请求，将虚拟请求的点放入一个List中
        List<VirtualVertex> vnmNode = new ArrayList<>();
        for (int i = 0; i < VnmLink.size(); i++) {
            if (!isIn(VnmLink.get(i).src, vnmNode)) {
                vnmNode.add(VnmLink.get(i).src);
            }
            if (!isIn(VnmLink.get(i).des, vnmNode)) {
                vnmNode.add(VnmLink.get(i).des);
            }
        }
        /*necessary?*/
        //对vnmlist中的节点需求根据cpu需求大小进行排序
        /*
        Collections.sort(vnmNode, new Comparator<VirtualVertex>() {
            @Override
            public int compare(VirtualVertex o1, VirtualVertex o2) {
                long cpu_1 = o1.cpuReq;
                long cpu_2 = o2.cpuReq;
                return (cpu_1 > cpu_2) ? -1 : 1;
            }
        }); */
        //vnm记录-#：3
        ///*
        vnmNodeList.put(vnmID, vnmNode);

        LOG.info("The number of vertexes in reqest is {}", vnmNode.size());
        for (int i = 0; i < vnmNode.size(); i++) {
            LOG.info("===YBX aoniVnm -- Vertex in Request are domain:{}-No:{},", vnmNode.get(i).domainReq, vnmNode.get(i).name);
            //LOG.info("czd~~vertex in Request is id : {}", vnmNode.get(i).domainReq * 10 + vnmNode.get(i).name);
        }

        HashMap<Long, DataCenter> MapResult = new HashMap<Long, DataCenter>();
        List<List<Long>> Routes = new ArrayList<List<Long>>();

        for (VirtualEdge vl : VnmLink) {
            int vl_slotnum = (int) Math.ceil((vl.getBandReq() * 1.0) / slotband);
            LOG.info("The vl_slotnum of the {} is :{}", vl.linkID, vl_slotnum);
            boolean flag_vl = true;
            int slot_success = 0;

            HashMap<Long, DataCenter> datacenters = new HashMap<Long, DataCenter>(); //mapping
            HashMap<DataCenter, Long> DataCenters = new HashMap<DataCenter, Long>();//allocate
            boolean flag_src = false;
            boolean flag_des = false;
            Edge edge = new Edge();
            DataCenter datacenter_src = new DataCenter();
            DataCenter datacenter_des = new DataCenter();
            List<Edge> EdgeList_f_src = new ArrayList<Edge>();
            List<Edge> EdgeList_f_des = new ArrayList<Edge>();
            List<Edge> EdgeList = new ArrayList<Edge>();
            List<Edge> EdgeList_f = new ArrayList<Edge>();
            //check the resource of src and des vertexs
            // /*start 1
            if (vl.src.domainReq == 1 && vl.des.domainReq == 1)//2 3 4 similar
            {
                EdgeList = domain1EdgeList;//new
                EdgeList_f = domain1EdgeList_f;//new

                Iterator iter_MapResult = MapResult.keySet().iterator();
                while (iter_MapResult.hasNext()) {
                    Long vertex_ID = (Long) iter_MapResult.next();
                    if (vertex_ID.equals(vl.src.name)) {
                        flag_src = true;
                        datacenter_src = MapResult.get(vertex_ID);
                    }
                    if (vertex_ID.equals(vl.des.name)) {
                        flag_des = true;
                        datacenter_des = MapResult.get(vertex_ID);
                    }
                }

                if (!flag_src && !flag_des) {//src and des have not been mapped
                    LOG.info("Node mode 1");

                    //对物理链路根据权重排序
                    Collections.sort(EdgeList_f, new Comparator<Edge>() {
                        @Override
                        public int compare(Edge o1, Edge o2) {
                            long weight_1 = o1.weight;
                            long weight_2 = o2.weight;
                            return (weight_1 == weight_2) ? 0 : ((weight_1 < weight_2) ? -1 : 1);
                            //  return (weight_1 < weight_2) ? -1 : 1;
                            ///  return weight_1.compareTo(weight_2);
                        }
                    });

                    edge = EdgeList_f.get(0); //weight min
                    LOG.info("The edge of shortest edge is with src:{},des:{},weight:{}", edge.src, edge.des, edge.weight);


                    Iterator iter_domain1nodes = domain1nodes.iterator();

                    while (iter_domain1nodes.hasNext() && ((!flag_src) || (!flag_des))) {

                        DataCenter dc = (DataCenter) iter_domain1nodes.next();
                        LOG.info("The  DataCenter is   ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);

                        if (dc.ID.equals(edge.src)) {

                            if (dc.cpuRes < vl.src.cpuReq) {
                                break;
                            } else if (dc.LSP < 1) {
                                break;
                            } else {
                                // datacenters.put(edge.src, dc);
                                datacenters.put(vl.src.name, dc);
                                DataCenters.put(dc, vl.src.cpuReq);
                                LOG.info("The src DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                                flag_src = true;
                            }
                        }
                        if (dc.ID.equals(edge.des)) {
                            if (dc.cpuRes < vl.des.cpuReq)
                                break;
                            else if (dc.LSP < 1)
                                break;
                            else {
                                //datacenters.put(edge.des, dc);
                                datacenters.put(vl.des.name, dc);
                                DataCenters.put(dc, vl.des.cpuReq);
                                LOG.info("The des DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                                flag_des = true;

                            }


                        }
                    }
                    flag_vl = flag_src && flag_des;

                    if (flag_vl) {
                        LOG.info("The available DataCenters are enough.");
                    } else {
                        LOG.info("The available DataCenter is not enough.The mapping is failed.");
                        return false;
                    }
                } else if (flag_src && flag_des) {
                    LOG.info("Node mode 2");

                    boolean flag_edge = false;
                    Iterator iter_EdgeList_f = EdgeList_f.iterator();
                    while (iter_EdgeList_f.hasNext()) {
                        Edge edge1 = (Edge) iter_EdgeList_f.next();
                        if (edge1.src.equals(datacenter_src.ID) && edge1.des.equals(datacenter_des.ID)) {
                            edge = edge1;
                            flag_edge = true;
                            break;
                        }
                    }
                    if (flag_edge)
                        LOG.info("The edge with src :{} des:{} has been found.", edge.src, edge.des);
                    else {
                        LOG.info("The edge with src :{} des:{} has not been found.The mapping is failed.", edge.src, edge.des);
                        return false;
                    }
                } else if (flag_src && !flag_des) {
                    LOG.info("Node mode 3  src yes des no");

                    Iterator iterator_src = EdgeList_f.iterator();
                    while (iterator_src.hasNext()) {
                        Edge edge2 = (Edge) iterator_src.next();
                        if (edge2.src.equals(datacenter_src.ID)) {
                            EdgeList_f_src.add(edge2);
                        }
                    }
                    Collections.sort(EdgeList_f_src, new Comparator<Edge>() {
                        @Override
                        public int compare(Edge o1, Edge o2) {
                            long weight_1 = o1.weight;
                            long weight_2 = o2.weight;
                            return (weight_1 < weight_2) ? -1 : 1;
                        }
                    });

                    edge = EdgeList_f_src.get(0); //weight min
                    LOG.info("The edge of shortest edge is with src:{},des:{},weight:{}", edge.src, edge.des, edge.weight);

                    Iterator iter_domain1nodes = domain1nodes.iterator();

                    while (iter_domain1nodes.hasNext() && !flag_des) {

                        DataCenter dc = (DataCenter) iter_domain1nodes.next();
                        LOG.info("The  DataCenter is   ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);

                        if (dc.ID.equals(edge.des)) {
                            if (dc.cpuRes < vl.des.cpuReq)
                                break;
                            else if (dc.LSP < 1)
                                break;
                            else {
                                datacenters.put(vl.des.name, dc);
                                DataCenters.put(dc, vl.des.cpuReq);
                                LOG.info("The des DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                                flag_des = true;
                            }
                        }
                    }
                    flag_vl = flag_src && flag_des;

                    if (flag_vl) {
                        LOG.info("The available DataCenters are enough.");
                    } else {
                        LOG.info("The available DataCenter is not enough.The mapping is failed.");
                        return false;
                    }
                } else {
                    LOG.info("Node mode 4 src no des yes");
                    //  LOG.info("vl.src:{}   vl.des:{}",vl.src.name,vl.des.name);

                    Iterator iterator_des = EdgeList_f.iterator();
                    while (iterator_des.hasNext()) {
                        Edge edge2 = (Edge) iterator_des.next();
                        LOG.info("edge2.des:{}  datacenter_des.ID:{}", edge2.des, datacenter_des.ID);
                        if (edge2.des.equals(datacenter_des.ID)) {
                            EdgeList_f_des.add(edge2);
                            LOG.info("The edge is:{}", edge2);
                        }
                    }
                    Collections.sort(EdgeList_f_des, new Comparator<Edge>() {
                        @Override
                        public int compare(Edge o1, Edge o2) {
                            long weight_1 = o1.weight;
                            long weight_2 = o2.weight;
                            return (weight_1 < weight_2) ? -1 : 1;
                        }
                    });

                    edge = EdgeList_f_des.get(0); //weight min
                    LOG.info("The edge of shortest edge is with src:{},des:{},weight:{}", edge.src, edge.des, edge.weight);

                    Iterator iter_domain1nodes = domain1nodes.iterator();

                    while (iter_domain1nodes.hasNext() && !flag_src) {

                        DataCenter dc = (DataCenter) iter_domain1nodes.next();
                        LOG.info("The  DataCenter is   ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);

                        if (dc.ID.equals(edge.src)) {
                            if (dc.cpuRes < vl.src.cpuReq)
                                break;
                            else if (dc.LSP < 1)
                                break;
                            else {
                                datacenters.put(vl.src.name, dc);
                                DataCenters.put(dc, vl.src.cpuReq);
                                LOG.info("The des DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                                flag_src = true;
                            }
                        }
                    }
                    flag_vl = flag_src && flag_des;

                    if (flag_vl) {
                        LOG.info("The available DataCenters are enough.");
                    } else {
                        LOG.info("The available DataCenter is not enough.The mapping is failed.");
                        return false;
                    }
                }
            }
            //  end 1*/
///*
            //get the passed vertexs
            ///*start 2
            int temp = edge.des.intValue();
            //    Map<Long, Integer> startslotnum = new HashMap<>();
            List<Long> slotnum = new ArrayList<Long>();
            //LOG.info("temp is :{}",temp);
            //  LOG.info("phypath_f[ed.src.intValue()-1][temp-1] is :{}", phypath_f[edge.src.intValue() - 1][temp - 1]);
            // LOG.info("ed.src.intValue()-1 is :{}",(ed.src.intValue()-1));
            while (temp != phypath_f[edge.src.intValue() - 1][temp - 1]) {
                //startslotnum.put(new Long((long) (temp)), 1280);
                slotnum.add(new Long((long) (temp)));
                temp = phypath_f[edge.src.intValue() - 1][temp - 1];
            }
            //         startslotnum.put(new Long((long) temp), 1280);
            slotnum.add(new Long((long) (temp)));
            //         startslotnum.put(ed.src, 1280);
            slotnum.add(edge.src);
            //        LOG.info("The startslotnums are :{}", startslotnum);
            Collections.reverse(slotnum);//reverse src---des
            //end 2*/
            //  /*
            Long[] slotnums = slotnum.toArray(new Long[slotnum.size()]);
            Map<Edge, List<Long>> startslotnum = new HashMap<Edge, List<Long>>();
            Iterator iter_EdgeList = EdgeList.iterator();

            while (iter_EdgeList.hasNext()) {

                Edge ed = (Edge) iter_EdgeList.next();

                //--------------------------------src:slotnum[i+1],des:slotnum[i]-------------------------------
                for (int i = 0; i < (slotnum.size() - 1); i++) {

                    if (ed.src.equals(slotnums[i]) && ed.des.equals(slotnums[i + 1])) {
                        LOG.info("ed.src:{}    ed.des:{}", ed.src, ed.des);
                        LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[i], slotnums[i + 1]);

                        int start = 1280;
                        boolean flag_count = false;
                        int counter = 0;
                        boolean slot_avai = false;
                        //--------------------------------------slot[]-------------------------------------
                        for (int j = 0; j < ed.slotNum.slot.length; j++) {

                            if (!ed.slotNum.slot[j]) //available
                            {
                                if (!flag_count)//not counting
                                {
                                    start = j;
                                    flag_count = true;

                                }
                                counter++;
                                if (counter == vl_slotnum) {
                                    List<Long> num_band = new ArrayList<Long>();
                                    num_band.add((long) (vnmID * 100000 + vl.linkID));
                                    num_band.add((long) start);
                                    num_band.add((long) vl_slotnum);
                                    startslotnum.put(ed, num_band);
                                    slot_avai = true;
                                    slot_success++;
                                    LOG.info("counter:{}", counter);
                                    LOG.info("slot_success :{}", slot_success);
                                    LOG.info("The startslotnum  :{}", startslotnum);
                                    break;
                                }
                            }
                            if (ed.slotNum.slot[j] && flag_count) {//unavailable and counting
                                start = 1280;
                                flag_count = false;
                                counter = 0;
                            }

                        }
                        //--------------------------------------slot[]-------------------------------------

                        if (slot_avai)
                            break;
                        else {
                            flag_vl = false;//?????
                            LOG.info("The available slots are not enough.The mapping is failed.");
                            return false;
                        }
                    }
                }
                //--------------------------------src:slotnum[i+1],des:slotnum[i]-------------------------------

                if (slot_success == (slotnum.size() - 1)) {
                    LOG.info("The available slots are enough.The mapping succeed.");
                    break;
                }
            }
            //mapping the vertexs
            Iterator iter_datacenters = datacenters.keySet().iterator();
            while (iter_datacenters.hasNext()) {
                Long vertex_ID = (Long) iter_datacenters.next();
                MapResult.put(vertex_ID, datacenters.get(vertex_ID));
            }
            LOG.info("The MapResult is :{}", MapResult);

            //allocate the resource of DataCenters
            Iterator iter_DataCenters = DataCenters.keySet().iterator();
            while (iter_DataCenters.hasNext()) {
                DataCenter datacenter = (DataCenter) iter_DataCenters.next();
                Long cpuReq = DataCenters.get(datacenter);
                datacenter.cpuRes = datacenter.cpuRes - cpuReq;
                datacenter.LSP = datacenter.LSP - 1;
                LOG.info("The DataCenter is with the ID of :{},cpuRes :{},LSP :{}.", datacenter.ID, datacenter.cpuRes, datacenter.LSP);
            }
            LOG.info("The resource of DataCenters has been allocated.");
            //record the path for front
            List<Long> ResultPath = new ArrayList<Long>();//linkID+vl_slotnum+src+des
            ResultPath.add(vl.linkID);
            ResultPath.add((long) vl_slotnum);
            ResultPath.add(slotnum.get(0));//src
            Collections.reverse(slotnum);
            ResultPath.add(slotnum.get(0));//des

            vnmRoutes.add(ResultPath);
            //record the path for record
            List<Long> path = slotnum;//src+ passed vertex +des

            Routes.add(path);

            //allocate the resource of edge
            Iterator iter_startslotnum = startslotnum.keySet().iterator();
            while (iter_startslotnum.hasNext()) {
                Edge edge1 = (Edge) iter_startslotnum.next();
                List<Long> num_band = startslotnum.get(edge1);

                for (int i = 0; i < edge1.slotNum.slot.length; i++) {
                    if (i == num_band.get(1).intValue()) {
                        for (int j = 0; j < num_band.get(2).intValue(); j++) {
                            edge1.slotNum.slot[j] = true;
                            edge1.slotNum.connectionID[j] = num_band.get(0);
                        }
                        LOG.info("The edge is with slot:{},connectionID:{}", edge1.slotNum.slot, edge1.slotNum.connectionID);
                    }
                }

            }
            LOG.info("The resource of Edges has been allocated.");
            //edit the edge
            edge.weight = Long.MAX_VALUE;
            Iterator iter_EdgeList2 = EdgeList_f.iterator();
            while (iter_EdgeList2.hasNext()) {
                Edge edge2 = (Edge) iter_EdgeList2.next();
                if (edge2.src.equals(edge.des) && edge2.des.equals(edge.src)) {
                    edge2.weight = Long.MAX_VALUE;
                    break;
                }
            }

        }
        /* cascade start1
        //record the topo to C1
        List<List<Long>> temp_vnmRoutes = new ArrayList<List<Long>>(vnmRoutes);
        VirRoutesList.put(vnmID,temp_vnmRoutes);
        LOG.info("The VirRoutes are:{}",VirRoutesList);
        //record the  resource to C1
        List<List<Long>> temp_virRequest = new ArrayList<List<Long>>(virlink_list);
        VirRequestsList.put(vnmID, temp_virRequest);
        LOG.info("The VirRequests are:{}",VirRequestsList);
        cascade end1*/

         /* cascade start2
        //record the topo to C1
        List<List<Long>> temp_vnmRoutes = new ArrayList<List<Long>>(vnmRoutes);

        VirRoutesList.put(vnmID,temp_vnmRoutes);
        LOG.info("The VirRoutes are:{}",VirRoutesList);
        //record the  resource to C1
        List<List<Long>> temp_virRequest = new ArrayList<List<Long>>(virlink_list);
        VirRequestsList.put(vnmID, temp_virRequest);
        LOG.info("The VirRequests are:{}",VirRequestsList);
        cascade end2*/

         ///* cascade start3
        PacketTopo packetTopo = new PacketTopo(vnmID,vnmNode,VnmLink);
        PacketResource packetResource = new PacketResource(vnmID,vnmNode,VnmLink);
        // cascade end3*/
/*
        //add localActor sender
        localActor.tell(packetTopo, ActorRef.noSender());
        localActor.tell(packetResource, ActorRef.noSender());
*/
        //record the info
        vnmDataCenterList.put(vnmID, MapResult);
        RouteList.put(vnmID, Routes);
        VirTopoList.put(vnmID,packetTopo);
        VirResourceList.put(vnmID,packetResource);

        //add localActor sender
        //localActor.tell(VirTopoList, ActorRef.noSender());
        //localActor.tell(VirResourceList, ActorRef.noSender());

       // CreateActorRef.localActorRef.tell(VirTopoList, ActorRef.noSender());
       // CreateActorRef.localActorRef.tell(VirResourceList, ActorRef.noSender());
        BundleContext ctx = getBundleContext();
        ServiceReference linkPropertyServiceReference = ctx.getServiceReference(LinkPropertyService.class);
        LinkProperty linkProperty = (LinkProperty) ctx.getService(linkPropertyServiceReference);

      //  linkProperty.setVirTopoList(VirTopoList);
      //  linkProperty.setVirResourceList(VirResourceList);

//        linkProperty.setVirTopoList(Long vnmID,int[][]topo,Long NodeIDBase);
//        linkProperty.setVirResourceList(Long vnmID,int[][]topo,Long NodeIDBase, List<DataCenter> DataCenterList,List<Edge> EdgeList);

        return true;
    }

    //*/
    /*Floyd opti*/
    /* 记录F算法的中间跳转节点*/
    //坐标ID = 节点ID-1
    private static void F_passed_vertexes(List<Long> list, int[][] path, Long start, Long end) {
        //  LOG.info("path[{}][{}]-1 :  {}", start.intValue(), end.intValue(), path[start.intValue()][end.intValue()] - 1);
        Long temp = new Long((long) (path[start.intValue()][end.intValue()] - 1));
        if (temp.equals(end)) return;
            //   if(temp.equals(new Long((long) end)) )return;
            //list.add(temp);
        else {
            F_passed_vertexes(list, path, start, temp);
            list.add(temp + 0x1L);
            F_passed_vertexes(list, path, temp, end);
        }

    }

    /**
     * 判断该物理链路的链路资源是否满足
     *
     * @param edge         待判定是否满足链路资源的物理链路
     * @param ReqSlotNum   请求的链路slot数
     * @param num_band     记录满足链路资源的链路以及资源分配信息
     * @param startslotnum 所有/虚拟链路（用于显示的链路）用于计数
     * @return 该链路的链路资源情况
     */
    private static boolean Resource_Slot(Long vnmID, Edge edge, int ReqSlotNum, List<Long> num_band, Map<Edge, List<Long>> startslotnum) {
        LOG.info("Slot Resource?");
        int start = 1280;
        boolean flag_count = false;
        int counter = 0;
        boolean slot_avai = false;
        int slot_success = 0;
        //List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
        // Map<DataCenter, Long> DC_to_allo = new HashMap<DataCenter, Long>();//(slot+node ok) to allocate 0:in 1: out

        //--------------------------------------slot[]-------------------------------------
        for (int j = 0; j < edge.slotNum.slot.length; j++) {

            if (!edge.slotNum.slot[j]) //available
            {
                if (!flag_count)//not counting
                {
                    start = j;
                    flag_count = true;

                }
                counter++;
                if (counter == ReqSlotNum) {
                    num_band.add((long) (vnmID * 100000 + startslotnum.size() + 1));
                    num_band.add((long) start);
                    num_band.add((long) ReqSlotNum);
                    //startslotnum.put(ed, num_band);
                    slot_avai = true;
                    slot_success++;
                    LOG.info("counter:{}", counter);
                    LOG.info("slot_success :{}", slot_success);
                    LOG.info("The startslotnum  :{}", startslotnum);
                    //LOG.info("The startslotnum  :{}", startslotnum);
                    break;
                }
            }
            if (edge.slotNum.slot[j] && flag_count) {//unavailable and counting
                start = 1280;
                flag_count = false;
                counter = 0;
            }
        }
        LOG.info("Slot Resource : {}", slot_avai);
        return slot_avai;
    }


    /**
     * 判断DC资源是否满足
     *
     * @param edge           已判定满足链路资源的链路
     * @param DataCenterList 该域中数据中心列表
     * @param ReqCpu         请求的节点资源
     * @param DC_to_allo     记录满足节点资源的数据中心和源节点或宿节点（入/出）
     * @return 该链路的源宿节点的资源情况
     */
    private static boolean Resource_DC(Edge edge, List<DataCenter> DataCenterList, Long ReqCpu, Map<DataCenter, Long> DC_to_allo) {
        LOG.info("DC Resource?");

        boolean flag_vl = false;
        boolean flag_src = false;
        boolean flag_des = false;
        Iterator iter_DataCenterList = DataCenterList.iterator();
        while (iter_DataCenterList.hasNext()) {
            DataCenter dc = (DataCenter) iter_DataCenterList.next();
            //        LOG.info("The  DataCenter is   ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
            if (dc.ID.equals(edge.src)) {

                if (dc.cpuRes < ReqCpu) {
                    break;
                } else if (dc.LSP < 1) {
                    break;
                } else {
                    DC_to_allo.put(dc, 0x1L);
                    LOG.info("The src DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                    flag_src = true;
                }
            }
            if (dc.ID.equals(edge.des)) {
                if (dc.cpuRes < ReqCpu)
                    break;
                else if (dc.LSP < 1)
                    break;
                else {
                    DC_to_allo.put(dc, 0x0L);
                    LOG.info("The des DataCenter has been found. ID:{},cpuRes:{},LSP:{}.", dc.ID, dc.cpuRes, dc.LSP);
                    flag_des = true;
                }
            }
        }
        flag_vl = flag_src && flag_des;
        LOG.info("DC Resource : {}", flag_vl);

        return flag_vl;
    }

    private static void Add_Slot(Edge edge, List<Long> num_band, Map<Edge, List<Long>> startslotnum) {
        LOG.info("Add_Slot start.");
        startslotnum.put(edge, num_band);
        LOG.info("startslotnum :{}", startslotnum);
    }

    //  private static void Add_DC1(Map<DataCenter, Long> DC_to_allo, HashMap<DataCenter, List<Long>> DataCenters_search,
    //                              HashMap<DataCenter, List<Long>> DataCenters_add, Long ReqCpu, Long Nodes_Dyna_add, Long cpu, Long in, Long out, Long sum) {
    private static void Add_DC1(Map<DataCenter, Long> DC_to_allo, HashMap<DataCenter, List<Long>> DataCenters_search,
                                HashMap<DataCenter, List<Long>> DataCenters_add, Long ReqCpu, List<Long> Nodes_Dyna_add) {

        LOG.info("Add_DC1 start.");
        Long temp_Nodes_Dyna_add = Nodes_Dyna_add.get(0);
        // LOG.info("The Nodes_Dyna_add is :{}.", Nodes_Dyna_add);
        LOG.info("The temp_Nodes_Dyna_add is :{}.", temp_Nodes_Dyna_add);
        Long cpu = 0x0L;
        Long in = 0x0L;
        Long out = 0x0L;
        Long sum = 0x0L;
        //    LOG.info("The Nodes_Dyna is :{}.",Nodes_Dyna);

        //判断该节点是否已判定可用
        //若是 将待分配占用增加
        Iterator iter_DataCenters = DataCenters_search.entrySet().iterator();
        //   Iterator iter_DataCenters = DataCenters.entrySet().iterator();
        while (iter_DataCenters.hasNext()) {
            Map.Entry<DataCenter, List<Long>> entry = (Map.Entry<DataCenter, List<Long>>) iter_DataCenters.next();//Entry
            DataCenter dcs = (DataCenter) entry.getKey();
            cpu = (Long) entry.getValue().get(0);
            in = (Long) entry.getValue().get(1);
            out = (Long) entry.getValue().get(2);
            sum = (Long) entry.getValue().get(3);
                                                /*
                                                LOG.info("cpu: {}.",cpu);
                                                LOG.info("in : {}.",in );
                                                LOG.info("out: {}.",out);
                                                LOG.info("sum: {}.",sum);
                                                */
            if (DC_to_allo.containsKey(dcs)) {
                temp_Nodes_Dyna_add = temp_Nodes_Dyna_add - 0x1L;
                cpu = cpu + ReqCpu;
                if (DC_to_allo.get(dcs).equals(0x0L)) {
                    in = in + 0x1L;
                    sum = sum + 0x1L;
                    DC_to_allo.remove(dcs);
                } else if (DC_to_allo.get(dcs).equals(0x1L)) {
                    out = out + 0x1L;
                    sum = sum + 0x1L;
                    DC_to_allo.remove(dcs);
                }

                List<Long> cpu_in_out_sum = new ArrayList<Long>();
                cpu_in_out_sum.add(cpu);
                cpu_in_out_sum.add(in);
                cpu_in_out_sum.add(out);
                cpu_in_out_sum.add(sum);
                DataCenters_add.put(dcs, cpu_in_out_sum);
                                                    /*
                                                    LOG.info("优化的节点已在DataCenters中。");
                                                    LOG.info("cpu: {}.",cpu);
                                                    LOG.info("in : {}.",in );
                                                    LOG.info("out: {}.",out);
                                                    LOG.info("sum: {}.",sum);
                                                    */
                //     Nodes_Dyna = Nodes_Dyna + Nodes_Dyna_add;

                LOG.info("DataCenters_add : {}", DataCenters_add);
                LOG.info("The temp_Nodes_Dyna_add is :{}.", temp_Nodes_Dyna_add);
                Nodes_Dyna_add.clear();
                Nodes_Dyna_add.add(temp_Nodes_Dyna_add);
                //  LOG.info("The Nodes_Dyna_add is :{}.", Nodes_Dyna_add);
                //      LOG.info("The Nodes_Dyna is :{}.",Nodes_Dyna);

                //    LOG.info("DataCenters : {}", DataCenters);
            }
        }
        /*
        //若不是 待分配占用即为ReqCpu
        LOG.info("DC_to_allo:{}", DC_to_allo);
        Iterator iter_DC_to_allo = DC_to_allo.keySet().iterator();
        while (iter_DC_to_allo.hasNext()) {
            DataCenter dc = (DataCenter) iter_DC_to_allo.next();
            if (DC_to_allo.get(dc).equals(0x0L)) {
                cpu = cpu + ReqCpu;
                in = in + 0x1L;
                sum = sum + 0x1L;
                //DC_to_allo.remove(dc);
            } else if (DC_to_allo.get(dc).equals(0x1L)) {
                cpu = cpu + ReqCpu;
                out = out + 0x1L;
                sum = sum + 0x1L;
                // DC_to_allo.remove(dc);
            }
            List<Long> cpu_in_out_sum = new ArrayList<Long>();
            cpu_in_out_sum.add(cpu);
            cpu_in_out_sum.add(in);
            cpu_in_out_sum.add(out);
            cpu_in_out_sum.add(sum);
            DataCenters.put(dc, cpu_in_out_sum);
            LOG.info("DataCenters : {}", DataCenters);
            // LOG.info("DataCenters : {}", DataCenters);
        }
        DC_to_allo.clear();
        Nodes_Dyna = Nodes_Dyna + Nodes_Dyna_add;
        */

    }


    private static void Add_DC2(Map<DataCenter, Long> DC_to_allo, HashMap<DataCenter, List<Long>> DataCenters, Long ReqCpu) {
        //  private static void Add_DC2(Map<DataCenter, Long> DC_to_allo, HashMap<DataCenter, List<Long>> DataCenters, Long ReqCpu, Long cpu, Long in, Long out, Long sum) {


        //判断该节点是否已判定可用
        //若是 将待分配占用增加
        /*
        Iterator iter_DataCenters = DataCenters.entrySet().iterator();
        //   Iterator iter_DataCenters = DataCenters.entrySet().iterator();
        while (iter_DataCenters.hasNext()) {
            Map.Entry<DataCenter, List<Long>> entry = (Map.Entry<DataCenter, List<Long>>) iter_DataCenters.next();//Entry
            DataCenter dcs = (DataCenter) entry.getKey();
            cpu = (Long) entry.getValue().get(0);
            in = (Long) entry.getValue().get(1);
            out = (Long) entry.getValue().get(2);
            sum = (Long) entry.getValue().get(3);
                                                /*
                                                LOG.info("cpu: {}.",cpu);
                                                LOG.info("in : {}.",in );
                                                LOG.info("out: {}.",out);
                                                LOG.info("sum: {}.",sum);

            if (DC_to_allo.containsKey(dcs)) {
                Nodes_Dyna_add = Nodes_Dyna_add - 0x1L;
                cpu = cpu + ReqCpu;
                if (DC_to_allo.get(dcs).equals(0x0L)) {
                    in = in + 0x1L;
                    sum = sum + 0x1L;
                    DC_to_allo.remove(dcs);
                } else if (DC_to_allo.get(dcs).equals(0x1L)) {
                    out = out + 0x1L;
                    sum = sum + 0x1L;
                    DC_to_allo.remove(dcs);
                }

                List<Long> cpu_in_out_sum = new ArrayList<Long>();
                cpu_in_out_sum.add(cpu);
                cpu_in_out_sum.add(in);
                cpu_in_out_sum.add(out);
                cpu_in_out_sum.add(sum);
                DataCenters.put(dcs, cpu_in_out_sum);
                                                    /*
                                                    LOG.info("优化的节点已在DataCenters中。");
                                                    LOG.info("cpu: {}.",cpu);
                                                    LOG.info("in : {}.",in );
                                                    LOG.info("out: {}.",out);
                                                    LOG.info("sum: {}.",sum);

                LOG.info("DataCenters : {}", DataCenters);
                //    LOG.info("DataCenters : {}", DataCenters);
            }
        }
        */
        LOG.info("Add_DC2 start.");

        //  LOG.info("The Nodes_Dyna is :{}.",Nodes_Dyna);
        //若不是 待分配占用即为ReqCpu
        LOG.info("DC_to_allo:{}", DC_to_allo);
        Iterator iter_DC_to_allo = DC_to_allo.keySet().iterator();
        while (iter_DC_to_allo.hasNext()) {
            DataCenter dc = (DataCenter) iter_DC_to_allo.next();
            Long cpu = 0x0L;
            Long in = 0x0L;
            Long out = 0x0L;
            Long sum = 0x0L;
            if (DC_to_allo.get(dc).equals(0x0L)) {
                cpu = cpu + ReqCpu;
                in = in + 0x1L;
                sum = sum + 0x1L;
                //DC_to_allo.remove(dc);
            } else if (DC_to_allo.get(dc).equals(0x1L)) {
                cpu = cpu + ReqCpu;
                out = out + 0x1L;
                sum = sum + 0x1L;
                // DC_to_allo.remove(dc);
            }
            List<Long> cpu_in_out_sum = new ArrayList<Long>();
            cpu_in_out_sum.add(cpu);
            cpu_in_out_sum.add(in);
            cpu_in_out_sum.add(out);
            cpu_in_out_sum.add(sum);
            DataCenters.put(dc, cpu_in_out_sum);
            LOG.info("DataCenters : {}", DataCenters);
            // LOG.info("DataCenters : {}", DataCenters);

        }
        DC_to_allo.clear();
        //    Nodes_Dyna = Nodes_Dyna + Nodes_Dyna_add;
        //    LOG.info("The Nodes_Dyna is :{}.",Nodes_Dyna);


    }

    /**
     * 动态虚拟化映射 new
     */

    public static boolean vnmSetup_Dyna_new(Long vnmID, Long ReqDomain, Long ReqType, Long ReqScale, List<List<Long>> vnmRoutes) {//ReqType:0x0l HighWidth
        LOG.info("===YBX aoniVnm -- DynamicVirtualization new start.");
        if (firstInit) {
            iniTopoResource();
            firstInit = false;
            LOG.info("===YBX aoniVnm -- Topo and datacenters are initialized!!!!");
        }
        //清空上一次的记录结果
        vnmRoutes.clear();
        //vnm记录-#:1
        vnmIDVm.add(vnmID);
        //Create Service start
        LOG.info("Create Services.");
        //==1==Req Nodes Number  ReqScale
        LOG.info("===YBX aoniVnm -- ReqScale : {}.", ReqScale);
        //==2==Req Nodes Cpu
        Long ReqCpu = 0x14L;//20
        LOG.info("===YBX aoniVnm -- ReqCpu : {}.", ReqCpu);
        //==3==Req Degree Bandwidth
        int ReqDegree = 4;
        //==4==Req Degree
        if (ReqType.equals(0x0L)) {
            Long ReqBandwidth = 0xc8L;//200
            int ReqSlotNum = (int) Math.ceil((ReqBandwidth * 1.0) / slotband);
            LOG.info("===YBX aoniVnm -- ReqSlotNum : {}.", ReqSlotNum);
            //Create Service end

            //EdgeList EdgeList_f DataCenterList start
            List<Edge> EdgeList = new ArrayList<Edge>();
            List<Edge> EdgeList_f = new ArrayList<Edge>();
            List<DataCenter> DataCenterList = new ArrayList<DataCenter>();
            if (ReqDomain.equals(0x1L)) {
                EdgeList.addAll(domain1EdgeList);
                EdgeList_f.addAll(domain1EdgeList_f);
                DataCenterList.addAll(domain1nodes);
            } else if (ReqDomain.equals(0x2L)) {
                EdgeList = domain2EdgeList;
                EdgeList_f.addAll(domain2EdgeList_f);
                DataCenterList = domain2nodes;
            } else if (ReqDomain.equals(0x3L)) {
                EdgeList = domain3EdgeList;
                EdgeList_f.addAll(domain3EdgeList_f);
                DataCenterList = domain3nodes;
            } else if (ReqDomain.equals(0x4L)) {
                EdgeList = domain4EdgeList;
                EdgeList_f.addAll(domain4EdgeList_f);
                DataCenterList = domain4nodes;
            } else if (ReqDomain.equals(0x5L)) {
                EdgeList = domain5EdgeList;
                EdgeList_f.addAll(domain5EdgeList_f);
                DataCenterList = domain5nodes;
            }
            //EdgeList EdgeList_f DataCenterList end

            boolean flag_NodesDyna = false;//记录是否满足规模
            boolean flag_Connected = false;//记录是否连通

            Long Nodes_Dyna = 0x0L;
            Long Nodes_Dyna_in = 0x0L;
            Map<Edge, List<Long>> startslotnum = new HashMap<Edge, List<Long>>();//分配edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)
            Map<Edge, List<Long>> startslotnum_in = new HashMap<Edge, List<Long>>();//跳转 edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)
            Map<Edge, List<Long>> startslotnum_out = new HashMap<Edge, List<Long>>();//显示 edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)
            HashMap<DataCenter, List<Long>> DataCenters = new HashMap<DataCenter, List<Long>>();// 分配(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
            HashMap<DataCenter, List<Long>> DataCenters_in = new HashMap<DataCenter, List<Long>>();//跳转(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
            HashMap<DataCenter, List<Long>> DataCenters_out = new HashMap<DataCenter, List<Long>>();//显示(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
            HashMap<Long, DataCenter> MapResult = new HashMap<Long, DataCenter>();
            List<List<Long>> Routes = new ArrayList<List<Long>>();

            //对物理链路根据带宽排序
            LOG.info("Sort EdgeList.");
            Collections.sort(EdgeList, new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    int MaxAvailNum_1 = o1.slotNum.MaxAvailNum;
                    int MaxAvailNum_2 = o2.slotNum.MaxAvailNum;
                    return (MaxAvailNum_1 > MaxAvailNum_2) ? -1 : 1;
                }
            });
            LOG.info("The first edge is src:{}  des:{}", EdgeList.get(1).src, EdgeList.get(1).des);
            Long Next_src = EdgeList.get(1).src;
            //Long Final_des = EdgeList.get(1).src;
            Long Final_des = Long.MAX_VALUE;
            while (!flag_NodesDyna || !flag_Connected) {
                //while (Nodes_Dyna < ReqScale && !flag_Connected) {
                LOG.info("The Nodes_Dyna is :{}.", Nodes_Dyna);
                LOG.info("The flag_Connected is :{}.", flag_Connected);

                List<Edge> EdgeList_src = new ArrayList<Edge>();//源节点为出度小于确定值的链路

                if (!flag_NodesDyna) {
                    ///*找到以Next_src为源节点的物理链路  start
                    Iterator iter_EdgeList_src = EdgeList.iterator();
                    while (iter_EdgeList_src.hasNext()) {
                        Edge edge_src = (Edge) iter_EdgeList_src.next();
                        if (edge_src.src.equals(Next_src)) {//该链路是以该节点为源节点
                            if (!startslotnum.containsKey(edge_src)) { //该链路不在已选链路中 not sure
                                LOG.info("edge_src.slotNum.MaxAvailNum : {} .", edge_src.slotNum.MaxAvailNum);
                                if (edge_src.slotNum.MaxAvailNum > ReqSlotNum) { //该链路满足请求带宽

                                    boolean flag_des = false;//宿节点不在可用节点中

                                    for (DataCenter dc : DataCenters_out.keySet()) {
                                        //  for (DataCenter dc : DataCenters.keySet()) {
                                        // LOG.info("The node in DataCenter_out is : {}.", dc.getID());
                                        if (dc.getID().equals(edge_src.des))
                                            flag_des = true;
                                    }
                                    if (!flag_des)
                                        EdgeList_src.add(edge_src);
                                    else {
                                        LOG.info("The des node of the edge has been in the DataCenters_out.");
                                    }

                                }
                            } else {
                                //LOG.info("The edge has been in the startslotnum.");
                            }
                        }
                        //    LOG.info("Found the proper src links.");
                    }
                    LOG.info("EdgeList_src are : {}.", EdgeList_src);
                    ///*找到以Next_src为源节点的物理链路  end

                    if (!EdgeList_src.isEmpty()) {

                        //对物理链路根据带宽降序排序
                        Collections.sort(EdgeList_src, new Comparator<Edge>() {
                            @Override
                            public int compare(Edge o1, Edge o2) {
                                int MaxAvailNum_1 = o1.slotNum.MaxAvailNum;
                                int MaxAvailNum_2 = o2.slotNum.MaxAvailNum;
                                return (MaxAvailNum_1 > MaxAvailNum_2) ? -1 : 1;
                            }
                        });

                        //取物理链路带宽 次 大的中满足链路资源请求的链路,若不满足 则取其次
                        // for (Edge edge_to_add : EdgeList_src) {

                        Iterator iter_EdgeList_src1 = EdgeList_src.iterator();
                        Edge edge_to_add = (Edge) iter_EdgeList_src1.next();
                        LOG.info("EdgeList_src:  src:{}  des:{}.", edge_to_add.src, edge_to_add.des);
                        while (iter_EdgeList_src1.hasNext()) {
                            edge_to_add = (Edge) iter_EdgeList_src1.next();
                            LOG.info("EdgeList_src:  src:{}  des:{}.", edge_to_add.src, edge_to_add.des);


                            List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
                            Map<DataCenter, Long> DC_to_allo = new HashMap<DataCenter, Long>();//(slot+node ok) to allocate 0:in 1: out

                            if (Resource_Slot(vnmID, edge_to_add, ReqSlotNum, num_band, startslotnum)) {//判断链路资源是否满足
                                if (Resource_DC(edge_to_add, DataCenterList, ReqCpu, DC_to_allo)) {
                                    // slot_success++;//该链路资源和节点资源都满足
                                    //加入可用链路
                                                        /*
                                                        startslotnum_in_in.put(ed, num_band);
                                                        LOG.info("startslotnum_in_in :{}", startslotnum_in_in);
                                                        */
                                    Add_Slot(edge_to_add, num_band, startslotnum_out);
                                    LOG.info("startslotnum_out new add:  edge: src:{}  des:{}  num_band:{}.", edge_to_add.src, edge_to_add.des, num_band);
                                    LOG.info("startslotnum_out :{}", startslotnum_out);

                                    List<Long> Nodes_Dyna_add = new ArrayList<Long>();
                                    Nodes_Dyna_add.add(0x2L);

                                    // Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, Nodes_Dyna_in_in, cpu, in out, sum);
                                    // Add_DC1(DC_to_allo, DataCenters, DataCenters_out, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                    Add_DC1(DC_to_allo, DataCenters, DataCenters_out, ReqCpu, Nodes_Dyna_add);
                                    LOG.info("The Nodes_Dyna_add is :{}.", Nodes_Dyna_add);
                                    Nodes_Dyna = Nodes_Dyna + Nodes_Dyna_add.get(0);
                                    LOG.info(" Nodes_Dyna is :{}.", Nodes_Dyna);

                                    if (!DC_to_allo.isEmpty())
                                        Add_DC2(DC_to_allo, DataCenters_out, ReqCpu);

                                    Next_src = edge_to_add.des;
                                    LOG.info("The Next_src is :{}.", Next_src);
                                    if (!(Nodes_Dyna < ReqScale))
                                        flag_NodesDyna = true;
                                    LOG.info(" Nodes_Dyna is :{}.", Nodes_Dyna);
                                    LOG.info(" flag_NodesDyna is :{}.", flag_NodesDyna);
                                    DataCenters.putAll(DataCenters_out);
                                    startslotnum.putAll(startslotnum_out);
                                    if (Final_des.equals(Long.MAX_VALUE))
                                        Final_des = edge_to_add.src;
                                    break;//break：  for(Edge edge_to_add :  EdgeList_des){
                                } else {

                                    LOG.info("DCs resource fail,to check the next sorted slot.");

                                }
                                //判断DC资源是否充足 end
                            } else {

                                LOG.info("Slot resource fail,to check the next sorted slot.");

                            }
                            ///*判断链路 节点 资源 end
                        }

                    } else {
                        LOG.info("The EdgeList_src id empty.");
                    }

                } else {
                    LOG.info("To connect the net");
                    //在物理链路中找满足条件的链路
                    LOG.info("在物理链路中找满足条件的链路.");

                    Iterator iter_EdgeList_src_des = EdgeList.iterator();
                    while (iter_EdgeList_src_des.hasNext()) {
                        Edge edge_src_des = (Edge) iter_EdgeList_src_des.next();
                        if (edge_src_des.src.equals(Next_src) && edge_src_des.des.equals(Final_des)) {//该链路是以上一条链路的宿节点为源节点 第一条链路的源节点为宿节点
                            if (!startslotnum.containsKey(edge_src_des)) { //该链路不在已选链路中
                                LOG.info("edge_src_des:  src:{}  des:{}.", edge_src_des.src, edge_src_des.des);

                                List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
                                Map<DataCenter, Long> DC_to_allo = new HashMap<DataCenter, Long>();//(slot+node ok) to allocate 0:in 1: out

                                if (Resource_Slot(vnmID, edge_src_des, ReqSlotNum, num_band, startslotnum)) {//判断链路资源是否满足
                                    if (Resource_DC(edge_src_des, DataCenterList, ReqCpu, DC_to_allo)) {
                                        //  slot_success++;//该链路资源和节点资源都满足
                                        //加入可用链路
                                                        /*
                                                        startslotnum_in_in.put(ed, num_band);
                                                        LOG.info("startslotnum_in_in :{}", startslotnum_in_in);
                                                        */
                                        Add_Slot(edge_src_des, num_band, startslotnum_out);
                                        LOG.info("startslotnum_out new add:  edge: src:{}  des:{}  num_band:{}.", edge_src_des.src, edge_src_des.des, num_band);

                                        LOG.info("startslotnum_out :{}", startslotnum_out);
                                        List<Long> Nodes_Dyna_add = new ArrayList<Long>();
                                        Nodes_Dyna_add.add(0x2L);

                                        // Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, Nodes_Dyna_in_in, cpu, in out, sum);
                                        // Add_DC1(DC_to_allo, DataCenters, DataCenters_out, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                        Add_DC1(DC_to_allo, DataCenters, DataCenters_out, ReqCpu, Nodes_Dyna_add);
                                        Nodes_Dyna = Nodes_Dyna + Nodes_Dyna_add.get(0);
                                        LOG.info(" Nodes_Dyna is :{}.", Nodes_Dyna);

                                        if (!DC_to_allo.isEmpty())
                                            //      Add_DC2(DC_to_allo, DataCenters_out, ReqCpu, cpu, in, out, sum);
                                            Add_DC2(DC_to_allo, DataCenters_out, ReqCpu);

                                        DataCenters.putAll(DataCenters_out);
                                        startslotnum.putAll(startslotnum_out);
                                        flag_Connected = true;
                                        break;//break：  iter
                                    } else {

                                        LOG.info("DCs resource fail,to check the next sorted slot.");

                                    }
                                    //判断DC资源是否充足 end
                                } else {

                                    LOG.info("Slot resource fail,to check the next sorted slot.");

                                }
                                ///*判断链路 节点 资源 end

                            }
                        }
                    }

                    //在最短径中找满足条件的链路
                    if (!flag_Connected) {
                        LOG.info("在最短径中找满足条件的链路.");
                        HashMap<DataCenter, List<Long>> DataCenters_in_in = new HashMap<DataCenter, List<Long>>();//跳转(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
                        Map<Edge, List<Long>> startslotnum_in_in = new HashMap<Edge, List<Long>>();//跳转 edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)

                        Iterator iter_EdgeList_f_src_des = EdgeList_f.iterator();
                        while (iter_EdgeList_f_src_des.hasNext()) {
                            Edge edge_f_src_des = (Edge) iter_EdgeList_f_src_des.next();
                            if (edge_f_src_des.src.equals(Next_src) && edge_f_src_des.des.equals(Final_des)) {//该链路是以上一条链路的宿节点为源节点 第一条链路的源节点为宿节点
                                if (!startslotnum.containsKey(edge_f_src_des)) { //该链路不在已选链路中
                                    LOG.info("edge_f_src_des:  src:{}  des:{}.", edge_f_src_des.src, edge_f_src_des.des);
                                    //找到该链路对应的跳转节点 strat
                                    List<Long> slotnum = new ArrayList<Long>();
                                    slotnum.add(new Long((long) (edge_f_src_des.src)));
                                    F_passed_vertexes(slotnum, phypath_f, (edge_f_src_des.src - 0x1L), (edge_f_src_des.des - 0x1L));
                                    slotnum.add(new Long((long) (edge_f_src_des.des)));
                                    LOG.info("The passed vertexs have been found.");
                                    LOG.info("Passed vertexs:{}", slotnum);
                                    Long[] slotnums = slotnum.toArray(new Long[slotnum.size()]);
                                    //找到该链路对应的跳转节点 end
                                    ///*判断每一跳跳转链路的 链路 节点 资源 start
                                    int slot_success = 0;
                                    Iterator iter_EdgeList = EdgeList.iterator();

                                    while (iter_EdgeList.hasNext()) {//在域中的物理链路中找对应的跳转链路,判断资源是否满足

                                        Edge ed = (Edge) iter_EdgeList.next();

                                        //--------------------------------src:slotnum[i],des:slotnum[i+1]-------------------------------
                                        for (int m = 0; m < (slotnum.size() - 1); m++) {//判断每一段跳转链路资源

                                            if (ed.src.equals(slotnums[m]) && ed.des.equals(slotnums[m + 1])) {//找到对应的物理链路
                                                //       LOG.info("ed.src:{}    ed.des:{}", ed.src, ed.des);
                                                LOG.info("\n - - - - - - ");
                                                LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);

                                                List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
                                                Map<DataCenter, Long> DC_to_allo = new HashMap<DataCenter, Long>();//(slot+node ok) to allocate 0:in 1: out

                                                if (Resource_Slot(vnmID, ed, ReqSlotNum, num_band, startslotnum)) {//判断链路资源是否满足
                                                    if (Resource_DC(ed, DataCenterList, ReqCpu, DC_to_allo)) {
                                                        slot_success++;//该链路资源和节点资源都满足
                                                        //加入可用链路
                                                        /*
                                                        startslotnum_in_in.put(ed, num_band);
                                                        LOG.info("startslotnum_in_in :{}", startslotnum_in_in);
                                                        */
                                                        Add_Slot(ed, num_band, startslotnum_in_in);

                                                        LOG.info("startslotnum_in_in :{}", startslotnum_in_in);
                                                        List<Long> Nodes_Dyna_add = new ArrayList<Long>();
                                                        Nodes_Dyna_add.add(0x2L);
                                                        //加入可用节点
                                                        //    Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                                        //    Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                                        Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);
                                                        Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);
                                                        Nodes_Dyna_in = Nodes_Dyna_in + Nodes_Dyna_add.get(0);

                                                        if (!DC_to_allo.isEmpty())
                                                            //  Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu, cpu, in, out, sum);
                                                            Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu);


                                                    } //该链路资源和节点资源都满足
                                                    else {
                                                        LOG.info("DCs resource fail,to check the next sorted slot.");
                                                        LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                                        break;
                                                    }
                                                    //判断DC资源是否充足 end

                                                } else {
                                                    //             flag_vl = false;//?????
                                                    LOG.info("The available slots are not enough.The mapping is failed.");
                                                    LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);

                                                    break;//new 1011
                                                    // return false;
                                                }
                                            }//找到对应的物理链路
                                        }// 判断每一段跳转链路资源
                                        //--------------------------------src:slotnum[i+1],des:slotnum[i]-------------------------------
                                    }// while (iter_EdgeList.hasNext()) {//在域中的物理链路中找对应的跳转链路,判断资源是否满足

                                    if (slot_success == (slotnum.size() - 1)) {
                                        LOG.info("The available slots are enough.The mapping succeed.");
                                        flag_Connected = true;//1008 22
                                        DataCenters_in.putAll(DataCenters_in_in);

                                        DataCenters.putAll(DataCenters_in);//wro
                                        startslotnum_in.putAll(startslotnum_in_in);
                                        startslotnum.putAll(startslotnum_in);//
                                        List<Long> temp_num_band = new ArrayList<Long>();
                                        temp_num_band.add(vnmID * 100000 + startslotnum_out.size() + 0x1L);
                                        temp_num_band.add(0x0L);
                                        temp_num_band.add((long) ReqSlotNum);
                                        startslotnum_out.put(edge_f_src_des, temp_num_band);

                                        LOG.info("startslotnum_out new add:  edge: src:{}  des:{}  num_band:{}.",
                                                edge_f_src_des.src, edge_f_src_des.des, temp_num_band);
                                        LOG.info("startslotnum_out :{}", startslotnum_out);

                                        // startslotnum_out.put(edge_f_src_des,new ArrayList<Long>((long) (vnmID * 100000 + startslotnum_out.size() + 1), (0x0L), (long) ReqSlotNum));
                                        break;
                                    } else {
                                        LOG.info("The available slots are not enough.The mapping failed.");
                                        DataCenters_in_in.clear();
                                        startslotnum_in_in.clear();
                                        Nodes_Dyna_in = 0x0L;
                                        break;
                                    }
///*判断每一跳跳转链路的 链路 节点 资源end

                                }
                            }
                        }
                    }
                    if (!flag_Connected) {
                        LOG.info(" Nodes_Dyna is :{}.", Nodes_Dyna);

                        if (Nodes_Dyna > 2 * ReqScale) {
                            LOG.info("网络无法闭环 建网失败.");
                        } else {
                            flag_NodesDyna = false;
                        }
                    }
                }
            }
            if (flag_Connected) {
                //网络已连接
                //对节点度数做优化 start
                LOG.info("ReqScale success.");
                LOG.info("DynamicVirtualization optimization start.");
                Map<DataCenter, Long> DC_to_allo = new HashMap<DataCenter, Long>();//(slot+node ok) to allocate 0:in 1: out

                //对待分配节点根据总度sum升序
                List<Map.Entry<DataCenter, List<Long>>> list_DataCenters_out = new ArrayList<Map.Entry<DataCenter, List<Long>>>(DataCenters_out.entrySet());

                Collections.sort(list_DataCenters_out, new Comparator<Map.Entry<DataCenter, List<Long>>>() {
                    @Override
                    public int compare(Map.Entry<DataCenter, List<Long>> o1, Map.Entry<DataCenter, List<Long>> o2) {
                        long sum_1 = o1.getValue().get(3);
                        long sum_2 = o2.getValue().get(3);
                        return (sum_1 == sum_2) ? 0 : ((sum_1 < sum_2) ? -1 : 1);
                    }
                });
                LOG.info("DataCenters_out :{}", DataCenters_out.keySet());

                int check_start = 0;//升序排序后开始判断的位置,默认为0；若度数最低的那条链路已不能再优化，则下次从后一个开始检查

                while (list_DataCenters_out.get(check_start).getValue().get(3) < (ReqDegree + 1) && check_start < (Nodes_Dyna - 1)) {

                    LOG.info("该节点:{} 的度是: {}", list_DataCenters_out.get(check_start).getKey().getID(), list_DataCenters_out.get(check_start).getValue().get(3));

                    List<Edge> EdgeList_des = new ArrayList<Edge>();//宿节点为入度小于确定值的链路
                    List<Edge> EdgeList_src = new ArrayList<Edge>();//源节点为出度小于确定值的链路
                    boolean optimize_des = false;
                    boolean optimize_src = false;

                    if (list_DataCenters_out.get(check_start).getValue().get(1) < (ReqDegree / 2 + 1)) {
                        LOG.info("该节点的入度是: {}", list_DataCenters_out.get(check_start).getValue().get(1));

                        Iterator iter_EdgeList_f = EdgeList_f.iterator();
                        while (iter_EdgeList_f.hasNext()) {
                            Edge edge_f = (Edge) iter_EdgeList_f.next();
                            if (edge_f.des.equals(list_DataCenters_out.get(check_start).getKey().ID)) {//该链路是以该节点为宿节点

                                boolean flag_Contain = false;
                                for (Edge edge : startslotnum.keySet()) {
                                    if (edge.src.equals(edge_f.src) && edge.des.equals(edge_f.des))
                                        flag_Contain = true;
                                }
                                for (Edge edge : startslotnum_out.keySet()) {
                                    if (edge.src.equals(edge_f.src) && edge.des.equals(edge_f.des))
                                        flag_Contain = true;
                                }
                                if (!flag_Contain) { //该链路不在已选隐藏链路中
                                    // if (!startslotnum.containsKey(edge_f)) { //该链路不在已选隐藏链路中
                                    for (DataCenter dc : DataCenters_out.keySet()) {//该链路的源节点在虚拟节点中
                                        if (dc.getID().equals(edge_f.src))
                                            EdgeList_des.add(edge_f);
                                    }
                                }
                            }
                            //    LOG.info("Found the proper des links.");
                        }
                        LOG.info("EdgeList_des are : {}.", EdgeList_des);
                        if (!EdgeList_des.isEmpty()) {
                            //对最短径根据权重升序排序
                            Collections.sort(EdgeList_des, new Comparator<Edge>() {
                                @Override
                                public int compare(Edge o1, Edge o2) {
                                    long weight_1 = o1.weight;
                                    long weight_2 = o2.weight;
                                    return (weight_1 < weight_2) ? -1 : 1;
                                }
                            });

                            //取最短径中权重最小的中满足链路资源请求的链路,若不满足 则取其次
                            for (Edge edge_to_add : EdgeList_des) {
                                HashMap<DataCenter, List<Long>> DataCenters_in_in = new HashMap<DataCenter, List<Long>>();//跳转(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
                                Map<Edge, List<Long>> startslotnum_in_in = new HashMap<Edge, List<Long>>();//跳转 edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)
                                Long Nodes_Dyna_in_in = 0x0L;
                                //找到该链路对应的跳转节点 strat
                                List<Long> slotnum = new ArrayList<Long>();
                                slotnum.add(new Long((long) (edge_to_add.src)));
                                F_passed_vertexes(slotnum, phypath_f, (edge_to_add.src - 0x1L), (edge_to_add.des - 0x1L));
                                slotnum.add(new Long((long) (edge_to_add.des)));
                                LOG.info("The passed vertexs have been found.");
                                LOG.info("Passed vertexs:{}", slotnum);
                                Long[] slotnums = slotnum.toArray(new Long[slotnum.size()]);
                                //找到该链路对应的跳转节点 end

                                ///*判断每一跳跳转链路的 链路 节点 资源 start
                                int slot_success = 0;
                                Iterator iter_EdgeList = EdgeList.iterator();
                                while (iter_EdgeList.hasNext()) {//在域中的物理链路中找对应的跳转链路,判断资源是否满足
                                    Edge ed = (Edge) iter_EdgeList.next();
                                    //--------------------------------src:slotnum[i],des:slotnum[i+1]-------------------------------
                                    for (int m = 0; m < (slotnum.size() - 1); m++) {//判断每一段跳转链路资源

                                        if (ed.src.equals(slotnums[m]) && ed.des.equals(slotnums[m + 1])) {//找到对应的物理链路
                                            //       LOG.info("ed.src:{}    ed.des:{}", ed.src, ed.des);
                                            LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                            List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
                                            if (Resource_Slot(vnmID, ed, ReqSlotNum, num_band, startslotnum)) {//判断链路资源是否满足
                                                if (Resource_DC(ed, DataCenterList, ReqCpu, DC_to_allo)) {
                                                    slot_success++;//该链路资源和节点资源都满足
                                                    Add_Slot(ed, num_band, startslotnum_in_in);

                                                    List<Long> Nodes_Dyna_add = new ArrayList<Long>();
                                                    Nodes_Dyna_add.add(0x2L);


                                                    //     Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                                    //     Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);

                                                    Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);
                                                    Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);

                                                    Nodes_Dyna_in_in = Nodes_Dyna_in_in + Nodes_Dyna_add.get(0);
                                                    if (!DC_to_allo.isEmpty())
                                                        //       Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu, cpu, in, out, sum);
                                                        Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu);

                                                    optimize_des = true;
                                                    LOG.info("{} 作为宿节点优化成功", list_DataCenters_out.get(check_start).getKey().getID());
                                                } else {
                                                    LOG.info("DCs resource fail,to check the next sorted slot.");
                                                    LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                                    break;
                                                }
                                            } else {
                                                //flag_vl = false;//?????
                                                LOG.info("The available slots are not enough.The mapping is failed.");
                                                LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);

                                                break;//new 1011
                                                // return false;
                                            }

                                        }
                                    }
                                }
                                if (slot_success == (slotnum.size() - 1)) {
                                    LOG.info("The available slots are enough.The mapping succeed.");
                                    DataCenters_in.putAll(DataCenters_in_in);
                                    DataCenters.putAll(DataCenters_in);
                                    startslotnum_in.putAll(startslotnum_in_in);
                                    startslotnum.putAll(startslotnum_in);//
                                    List<Long> temp_num_band = new ArrayList<Long>();
                                    temp_num_band.add(vnmID * 100000 + startslotnum_out.size() + 0x1L);
                                    temp_num_band.add(0x0L);
                                    temp_num_band.add((long) ReqSlotNum);
                                    startslotnum_out.put(edge_to_add, temp_num_band);
                                    //更新DataCenters_out中节点的信息 节点的度
                                    for (DataCenter dataCenter : DataCenters_in_in.keySet()) {
                                        if (DataCenters_out.containsKey(dataCenter)) {
                                            LOG.info("The origin : {} {}", dataCenter.getID(), DataCenters_out.get(dataCenter));
                                            DataCenters_out.put(dataCenter, DataCenters_in_in.get(dataCenter));
                                        }
                                        LOG.info("The updated : {} {}", dataCenter.getID(), DataCenters_out.get(dataCenter));

                                    }
                                    LOG.info("startslotnum_out new add:  edge: src:{}  des:{}  num_band:{}.",
                                            edge_to_add.src, edge_to_add.des, temp_num_band);
                                    LOG.info("startslotnum_out :{}", startslotnum_out);

                                    //  startslotnum_out.put(edge_to_add,new ArrayList<Long>((long) (vnmID * 100000 + startslotnum_out.size() + 1), (0x0L), (long) ReqSlotNum));
                                    Nodes_Dyna_in = Nodes_Dyna_in + Nodes_Dyna_in_in;
                                    break;
                                } else {
                                    LOG.info("The available slots are not enough.The mapping failed.");
                                    DataCenters_in_in.clear();
                                    startslotnum_in_in.clear();
                                    Nodes_Dyna_in_in = 0x0L;
                                    break;
                                }
                            }

                        } else {
                            LOG.info("The edge with des:{} is empty in EdgeList_f.", list_DataCenters_out.get(check_start).getKey().ID);
                        }
                    } else if (list_DataCenters_out.get(check_start).getValue().get(2) < (ReqDegree / 2 + 1)) {
                        LOG.info("该节点的出度是: {}", list_DataCenters_out.get(check_start).getValue().get(2));
                        Iterator iter_EdgeList_f = EdgeList_f.iterator();
                        while (iter_EdgeList_f.hasNext()) {
                            Edge edge_f = (Edge) iter_EdgeList_f.next();
                            if (edge_f.src.equals(list_DataCenters_out.get(check_start).getKey().ID)) {//该链路是以该节点为宿节点

                                boolean flag_Contain = false;
                                for (Edge edge : startslotnum.keySet()) {
                                    if (edge.src.equals(edge_f.src) && edge.des.equals(edge_f.des))
                                        flag_Contain = true;
                                }
                                for (Edge edge : startslotnum_out.keySet()) {
                                    if (edge.src.equals(edge_f.src) && edge.des.equals(edge_f.des))
                                        flag_Contain = true;
                                }
                                if (!flag_Contain) { //该链路不在已选隐藏链路中
                                    //  if (!startslotnum.containsKey(edge_f)) { //该链路不在已选隐藏链路中
                                    for (DataCenter dc : DataCenters_out.keySet()) {//该链路的宿节点在虚拟节点中
                                        if (dc.getID().equals(edge_f.des))
                                            EdgeList_src.add(edge_f);
                                    }
                                }
                            }
                            //    LOG.info("Found the proper des links.");
                        }
                        LOG.info("EdgeList_src are : {}.", EdgeList_src);
                        if (!EdgeList_src.isEmpty()) {
                            //对最短径根据权重升序排序
                            Collections.sort(EdgeList_src, new Comparator<Edge>() {
                                @Override
                                public int compare(Edge o1, Edge o2) {
                                    long weight_1 = o1.weight;
                                    long weight_2 = o2.weight;
                                    return (weight_1 < weight_2) ? -1 : 1;
                                }
                            });

                            //取最短径中权重最小的中满足链路资源请求的链路,若不满足 则取其次
                            for (Edge edge_to_add : EdgeList_src) {
                                HashMap<DataCenter, List<Long>> DataCenters_in_in = new HashMap<DataCenter, List<Long>>();//跳转(slot+nodes ok)to allocate DataCenter+cpu+in+out+sum
                                Map<Edge, List<Long>> startslotnum_in_in = new HashMap<Edge, List<Long>>();//跳转 edge+num_band((vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum)
                                Long Nodes_Dyna_in_in = 0x0L;
                                //找到该链路对应的跳转节点 strat
                                List<Long> slotnum = new ArrayList<Long>();
                                slotnum.add(new Long((long) (edge_to_add.src)));
                                F_passed_vertexes(slotnum, phypath_f, (edge_to_add.src - 0x1L), (edge_to_add.des - 0x1L));
                                slotnum.add(new Long((long) (edge_to_add.des)));
                                LOG.info("The passed vertexs have been found.");
                                LOG.info("Passed vertexs:{}", slotnum);
                                Long[] slotnums = slotnum.toArray(new Long[slotnum.size()]);
                                //找到该链路对应的跳转节点 end

                                ///*判断每一跳跳转链路的 链路 节点 资源 start
                                int slot_success = 0;
                                Iterator iter_EdgeList = EdgeList.iterator();
                                while (iter_EdgeList.hasNext()) {//在域中的物理链路中找对应的跳转链路,判断资源是否满足
                                    Edge ed = (Edge) iter_EdgeList.next();
                                    //--------------------------------src:slotnum[i],des:slotnum[i+1]-------------------------------
                                    for (int m = 0; m < (slotnum.size() - 1); m++) {//判断每一段跳转链路资源

                                        if (ed.src.equals(slotnums[m]) && ed.des.equals(slotnums[m + 1])) {//找到对应的物理链路
                                            //       LOG.info("ed.src:{}    ed.des:{}", ed.src, ed.des);
                                            LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                            List<Long> num_band = new ArrayList<Long>();//(vnmID * 100000+linkID)++StartSlotNum+ReqSlotNum
                                            if (Resource_Slot(vnmID, ed, ReqSlotNum, num_band, startslotnum)) {//判断链路资源是否满足
                                                if (Resource_DC(ed, DataCenterList, ReqCpu, DC_to_allo)) {
                                                    slot_success++;//该链路资源和节点资源都满足
                                                    Add_Slot(ed, num_band, startslotnum_in_in);

                                                    List<Long> Nodes_Dyna_add = new ArrayList<Long>();
                                                    Nodes_Dyna_add.add(0x2L);
                                                    // Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);
                                                    // Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add, cpu, in, out, sum);


                                                    Add_DC1(DC_to_allo, DataCenters_in_in, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);
                                                    Add_DC1(DC_to_allo, DataCenters, DataCenters_in_in, ReqCpu, Nodes_Dyna_add);
                                                    Nodes_Dyna_in_in = Nodes_Dyna_in_in + Nodes_Dyna_add.get(0);
                                                    if (!DC_to_allo.isEmpty())
                                                        //       Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu, cpu, in, out, sum);
                                                        Add_DC2(DC_to_allo, DataCenters_in_in, ReqCpu);
                                                    // Add_DC(DC_to_allo, DataCenters, Nodes_Dyna_in_in);
                                                    optimize_des = true;
                                                    LOG.info("{} 作为源节点优化成功", list_DataCenters_out.get(check_start).getKey().getID());
                                                    LOG.info("该节点的出度是: {}", list_DataCenters_out.get(check_start).getValue().get(2));
                                                } else {
                                                    LOG.info("DCs resource fail,to check the next sorted slot.");
                                                    LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                                    break;
                                                }
                                            } else {
                                                //flag_vl = false;//?????
                                                LOG.info("The available slots are not enough.The mapping is failed.");
                                                LOG.info("slotnums[i]:{}    slotnums[i+1]:{}", slotnums[m], slotnums[m + 1]);
                                                break;//new 1011
                                                // return false;
                                            }
                                        }
                                    }
                                }
                                if (slot_success == (slotnum.size() - 1)) {
                                    LOG.info("The available slots are enough.The mapping succeed.");
                                    DataCenters_in.putAll(DataCenters_in_in);
                                    DataCenters.putAll(DataCenters_in);
                                    startslotnum_in.putAll(startslotnum_in_in);
                                    startslotnum.putAll(startslotnum_in);//
                                    List<Long> temp_num_band = new ArrayList<Long>();
                                    temp_num_band.add(vnmID * 100000 + startslotnum_out.size() + 0x1L);
                                    temp_num_band.add(0x0L);
                                    temp_num_band.add((long) ReqSlotNum);
                                    //更新DataCenters_out中节点的信息 节点的度
                                    for (DataCenter dataCenter : DataCenters_in_in.keySet()) {
                                        if (DataCenters_out.containsKey(dataCenter)) {
                                            LOG.info("The origin : {} {}", dataCenter.getID(), DataCenters_out.get(dataCenter));
                                            DataCenters_out.put(dataCenter, DataCenters_in_in.get(dataCenter));
                                        }
                                        LOG.info("The updated : {} {}", dataCenter.getID(), DataCenters_out.get(dataCenter));

                                    }
                                    startslotnum_out.put(edge_to_add, temp_num_band);
                                    LOG.info("startslotnum_out new add:  edge: src:{}  des:{}  num_band:{}.",
                                            edge_to_add.src, edge_to_add.des, temp_num_band);
                                    LOG.info("startslotnum_out :{}", startslotnum_out);

                                    //     startslotnum_out.put(edge_to_add,new ArrayList<Long>((long) (vnmID * 100000 + startslotnum_out.size() + 1), (0x0L), (long) ReqSlotNum));
                                    Nodes_Dyna_in = Nodes_Dyna_in + Nodes_Dyna_in_in;
                                    break;
                                } else {
                                    LOG.info("The available slots are not enough.The mapping failed.");
                                    DataCenters_in_in.clear();
                                    startslotnum_in_in.clear();
                                    Nodes_Dyna_in_in = 0x0L;
                                    break;
                                }
                            }

                        } else {
                            LOG.info("The edge with des:{} is empty in EdgeList_f.", list_DataCenters_out.get(check_start).getKey().ID);
                        }

                    } else
                        LOG.info("Errors about ReqDegree.");

                    LOG.info("排序前 下一次 该节点的度是: {}", list_DataCenters_out.get(check_start).getValue().get(3));

                    //对待分配节点根据总度sum升序
                    Collections.sort(list_DataCenters_out, new Comparator<Map.Entry<DataCenter, List<Long>>>() {
                        @Override
                        public int compare(Map.Entry<DataCenter, List<Long>> o1, Map.Entry<DataCenter, List<Long>> o2) {
                            long sum_1 = o1.getValue().get(3);
                            long sum_2 = o2.getValue().get(3);
                            return (sum_1 == sum_2) ? 0 : ((sum_1 < sum_2) ? -1 : 1);
                        }
                    });

                    if (!optimize_des && !optimize_src) {
                        LOG.info("The check_start is: {}", check_start);
                        check_start++;
                        LOG.info("该节点不能再优化。");
                        LOG.info("Nodes_Dyna : {}.", Nodes_Dyna);
                        LOG.info("Nodes_Dyna_in : {}.", Nodes_Dyna_in);

                        //      LOG.info("排序后 下一次 该位置节点的度是: {}",list_DataCenters_out.get(check_start).getValue().get(3));
                    } else {

                        LOG.info("该节点的出度是: {}", list_DataCenters_out.get(check_start).getValue().get(2));

                    }
                }
                //对节点度数做优化 end
            }

            LOG.info("DCs map and allocate.");
            //mapping the vertexs and allocate the resource of DataCenters
            Iterator iter_DataCenters = DataCenters.keySet().iterator();
            while (iter_DataCenters.hasNext()) {
                DataCenter datacenter = (DataCenter) iter_DataCenters.next();
                Long cpuReq = DataCenters.get(datacenter).get(0);
                //mapping the vertexs
                MapResult.put((long) (MapResult.size() + 1), datacenter);
                LOG.info("The MapResult is :{}", MapResult);

                //allocate the resource of DataCenters
                datacenter.cpuRes = datacenter.cpuRes - cpuReq;
                datacenter.LSP = datacenter.LSP + 1;
                LOG.info("The DataCenter is with the ID of :{},cpuRes :{},LSP :{}.", datacenter.ID, datacenter.cpuRes, datacenter.LSP);
            }
            LOG.info("DCs mapping success.");
            LOG.info("DCs resource allocation success.");

            LOG.info("Slots record and allocate.");
            //record the path for record and allocate the resource of edge
            Iterator iter_startslotnum = startslotnum.keySet().iterator();
            while (iter_startslotnum.hasNext()) {
                Edge edge1 = (Edge) iter_startslotnum.next();
                List<Long> num_band = startslotnum.get(edge1);
                //Record the path for record
                List<Long> path = new ArrayList<Long>();//src+des
                path.add(edge1.src);
                path.add(edge1.des);
                Routes.add(path);


                //allocate the resource of edge
                for (int i = 0; i < edge1.slotNum.slot.length; i++) {
                    if (i == num_band.get(1).intValue()) {
                        for (int j = 0; j < num_band.get(2).intValue(); j++) {
                            edge1.slotNum.slot[j] = true;
                            edge1.slotNum.connectionID[j] = num_band.get(0);
                        }
                        //    LOG.info("The edge is with slot:{},connectionID:{}", edge1.slotNum.slot, edge1.slotNum.connectionID);
                    }
                }
                //update slot MaxAvailNum
                int max = 0;
                boolean flag_count = false;
                int count = 0;
                for (int i = 0; i < edge1.slotNum.slot.length; i++) {
                    if (!edge1.slotNum.slot[i] && !flag_count) {
                        flag_count = true;
                        count++;
                    } else if (!edge1.slotNum.slot[i] && flag_count) {
                        count++;
                    } else if (edge1.slotNum.slot[i] && flag_count) {
                        flag_count = false;
                        if (count > max)
                            max = count;
                    }
                }
                edge1.slotNum.MaxAvailNum = max;

                //ii++;
            }
            // LOG.info("The startslotnum_out:{}", startslotnum_out);
            /*
            for (Edge edge111 : startslotnum_out.keySet()) {
                LOG.info("Star   src :{}  des:{}", edge111.src, edge111.des);
                LOG.info("The  num_band is :{}",startslotnum_out.get(edge111));
            }
*/

            Iterator iter_startslotnum_out = startslotnum_out.keySet().iterator();
            while (iter_startslotnum_out.hasNext()) {
                Edge edge1 = (Edge) iter_startslotnum_out.next();
                LOG.info("edge1 src :{}  des:{}", edge1.src, edge1.des);

                List<Long> num_band = startslotnum_out.get(edge1);
                //record the path for front
                LOG.info("The  num_band is :{}", num_band);
                List<Long> ResultPath = new ArrayList<Long>();//linkID+ReqSlotNum+src+des
                ResultPath.add(num_band.get(0) - vnmID * 100000);
                ResultPath.add(num_band.get(2));
                ResultPath.add(edge1.src);
                ResultPath.add(edge1.des);
                LOG.info("The  ResultPath is :{}", ResultPath);
                vnmRoutes.add(ResultPath);
            }

            //将startslotnum_out按照linkID升序排列

            Collections.sort(vnmRoutes, new Comparator<List<Long>>() {
                @Override
                public int compare(List<Long> o1, List<Long> o2) {
                    long linkID_1 = o1.get(0);
                    long linkID_2 = o2.get(0);
                    return (linkID_1 == linkID_2) ? 0 : ((linkID_1 < linkID_2) ? -1 : 1);
                }
            });


            LOG.info("The vnmRoutes is:{}.", vnmRoutes);

            LOG.info("Slots record success.");
            LOG.info("Slots resource allocation success.");

            //record the info
            vnmDataCenterList.put(vnmID, MapResult);
            RouteList.put(vnmID, Routes);
            return true;
        }
        // return true;// not sure the position
        else {
            LOG.info("The ReqType must be 0x0L.");
            return false;
        }
    }


    /**
     * Initialize from Ted
     */

    public void dataCenterInit() {
        /*
        Iterator iterator = phyNodes.keySet().iterator();
        while (iterator.hasNext()) {
            phyTopoNodes.add((Long) iterator.next());
        }*/
        LOG.info("===YBX aoniVnm -- phyNodes are {}", phyNodes);

        /*initialization of dataCenter in phyNodes*/
        for (int i = 0; i < phyNodes.size(); i++) {
                /*//check here especially!!!
            Long domainId = domainIDofNode.get(phyNodes.get(i));
            LOG.info("CZD:datacenter domainID is {}@@@@#####", domainId);
            int nameId = phyNodes.get(i).intValue() & 0xff;//??
            LOG.info("CZD:vonNode name is {}", phyNodes.get(i) & 0xff);
            */

            if (phyNodes.get(i).intValue() > 0 && phyNodes.get(i).intValue() < 200) {
                DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x1L, 0x1f400L);//1800G YBX edited on 05/17/17
                domain1nodes.add(dataCenter);
            } else if (phyNodes.get(i).intValue() > 199 && phyNodes.get(i).intValue() < 400) {
                DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x1a3185c5000L, 0x2L, 0x1f400L);//1800G YBX edited on 05/17/17
                domain2nodes.add(dataCenter);
            } else if (phyNodes.get(i).intValue() > 399 && phyNodes.get(i).intValue() < 600) {
                DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x3L, 0x2710L);//400G YBX edited on 05/17/17
                domain3nodes.add(dataCenter);
            } else if (phyNodes.get(i).intValue() > 599 && phyNodes.get(i).intValue() < 800) {
                DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x4L, 0x2710L);//400G YBX edited on 05/17/17
                domain4nodes.add(dataCenter);
            } else if (phyNodes.get(i).intValue() > 799 && phyNodes.get(i).intValue() < 1001) {
                DataCenter dataCenter = new DataCenter(phyNodes.get(i), 0x5d21dba000L, 0x5L, 0x2710L);//400G YBX edited on 05/17/17
                domain5nodes.add(dataCenter);
            } else {
                LOG.info("vnmNode{} is with name of {} is not DataCenter@#@#", i, phyNodes.get(i));
            }
        }
        LOG.info("===YBX aoniVnm -- DataCenter1 nodes : {}", domain1nodes);
        LOG.info("===YBX aoniVnm -- DataCenter2 nodes : {}", domain2nodes);
        LOG.info("===YBX aoniVnm -- DataCenter3 nodes : {}", domain3nodes);
        LOG.info("===YBX aoniVnm -- DataCenter4 nodes : {}", domain4nodes);
        LOG.info("===YBX aoniVnm -- DataCenter5 nodes : {}", domain5nodes);
        LOG.info("Vnm Datacenters are Initialized!");
    }

    //判断虚拟点是否在List中
    private static boolean isIn(VirtualVertex vs, List<VirtualVertex> vsA) {
        boolean flag = false;
        if (vsA == null) {
            return false;
        } else {
            for (VirtualVertex v : vsA) {
                if (v.domainReq.equals(vs.domainReq) && v.name.equals(vs.name)) {  //virtualvertex的name是北向发的
                    flag = true;
                    break;
                }
            }
            return flag;
        }
    }
}


    /*
    * Initializa the topo from Ted
    * and invoke Dij
    * */
/*    public    void  iniTopoResource() {

        LOG.info("===YBX aoniVNM -- TopoResource Start initTopoResource()");
        BundleContext ctx = getBundleContext();
        ServiceReference linkPropertyServiceReference = ctx.getServiceReference(LinkPropertyService.class);
        LinkProperty linkProperty = (LinkProperty)ctx.getService(linkPropertyServiceReference);
        if(linkProperty != null)
        {
            phyTopo  = linkProperty.getPhysicalTopoMatrix(); // 0  1
            phyEdges = linkProperty.getPhysicalEdgesMatrix();//Edges weight
            phyNodes = linkProperty.getPhysicalNodes();
            if(phyTopo!=null) {//Map<Long,List<Long>>  Map<node,List<remoteList>>
                for (int i = 0; i < row; i++) {
            //Get the vertex and set the source vertex
                    if(i==3)
                    {
                        Vertex ver =  new Vertex( new Long((long) (i + 1)),0);
                        vertexs.add(ver);
                    }
                    else
                    {
                        Vertex ver =  new Vertex( new Long((long) (i + 1)));
                        vertexs.add(ver);
                    }
                    for (int j = 0; j < column; j++) {
                        LOG.info("{} ", phyTopo[i][j]);
                    }
                }
               LOG.info("--------------------------");

                Graph graph = new Graph(vertexs, phyEdges);
                graph.printGraph();
                graph.search();//Dij

                //Show the result in log
                Iterator iter = vertexs.iterator();
                LOG.info("--------------------------");
                while (iter.hasNext()) {
                    Vertex ver = (Vertex) iter.next();
                    LOG.info("The vertex id is :{}" , ver.getVertext_id());
                    LOG.info("The shortest edge is {}:", ver.getDistance());
                    LOG.info("The passed vertexs sre :");
                    for (Long path : ver.getPath()) {
                        LOG.info("{} ", path);
                    }
                    LOG.info("--------------------------");
                }
            }
        }

        }
    */