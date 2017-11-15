/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

//import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Graph {
    private static final Logger LOG = LoggerFactory.getLogger(Graph.class);
    /*
     * 顶点
     */
    private List<Vertex> vertexs;

    /*
     * 边
     */
    private int[][] edges;

    /*
     * 没有访问的顶点
     */
    private List<Vertex> unVisited;
    /*
     * Constructed function
     */
    public Graph(List<Vertex> vertexs, int[][] edges) {
        this.vertexs = vertexs;
        this.edges = edges;
        initUnVisited();
    }

    /*
     * 搜索各顶点最短路径
     */
    public void search() {
        //Vertex vertex_temp = new Vertex(0x0L);

        LOG.info("-------------------------------------------------");

        while (!unVisited.isEmpty()) {
            Collections.sort(unVisited, new Comparator<Vertex>() {
                @Override
                public int compare(Vertex o1, Vertex o2) {
                    long dis_1 = o1.getDistance();
                    long dis_2 = o2.getDistance();
                    return (dis_1 < dis_2) ? -1 : 1;
                }
            });
            //  Vertex vertex = unVisited.element();
            Vertex vertex = unVisited.get(0);
            Long vertex_id = vertex.getVertext_id();
            LOG.info("The vertex is :{}", vertex_id);
            //顶点已经计算出最短路径，设置为"已访问"
            vertex.setMarked(true);
            //获取所有"未访问"的邻居
            List<Vertex> neighbors = getNeighbors(vertex);
            //更新邻居的最短路径
            updatesDistance(vertex, neighbors);
            /*
            if(vertex_id != 0x4L) {
                vertex.setPath(vertex_temp);
            }
            vertex_temp = vertex;
            */
            pop(vertex);
            LOG.info("-------------------------------------------------");
        }
        LOG.info("search over");
    }

    /*
         * 获取顶点所有(未访问的)邻居
         */
    private List<Vertex> getNeighbors(Vertex v) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        int position = vertexs.indexOf(v);
        Vertex neighbor = null;
        int distance;
        for (int i = 0; i < vertexs.size(); i++) {
            if (i == position) {
                //顶点本身，跳过
                continue;
            }
            distance = edges[position][i];    //到所有顶点的距离
            if (distance < Integer.MAX_VALUE) {
                //是邻居(有路径可达)
                neighbor = getVertex(i);
                if (!neighbor.marked) {
                    //如果邻居没有访问过，则加入list;
                    neighbors.add(neighbor);
                    LOG.info("The neighbours are:{}", neighbor.getVertext_id());

                }
            }
        }

        return neighbors;
    }

    /*
     * 更新所有邻居的最短路径
     */
    private void updatesDistance(Vertex vertex, List<Vertex> neighbors) {
        for (Vertex neighbor : neighbors) {
            updateDistance(vertex, neighbor);
        }
    }

    /*
     * 更新邻居的最短路径
     */
    private void updateDistance(Vertex vertex, Vertex neighbor) {
        int distance = getDistance(vertex, neighbor) + vertex.getDistance();
        LOG.info("The distance between SourceVertex : 4 and {}  is {}", neighbor.getVertext_id(), distance);
        if (distance < neighbor.getDistance()) {
            neighbor.setDistance(distance);
            neighbor.getPath().clear();
            if(neighbor.getVertext_id() != 0x4L) {
                neighbor.setPath(vertex);
                neighbor.getPath();
                for (Long Path : vertex.getPath()) {
                    LOG.info("{}", Path);
                }
            }

            LOG.info("Updated the distance of :{} with {}", +neighbor.getVertext_id(), distance);
        } else
            LOG.info("Keep the distance of :{} with {}", neighbor.getVertext_id(),neighbor.getDistance());

    }

    /*
     * 初始化未访问顶点集合
     */
    private void initUnVisited() {
        //   unVisited = new PriorityQueue<Vertex>();
        unVisited = new ArrayList<Vertex>();
        for (Vertex v : vertexs) {
            unVisited.add(v);
        }
    }


    /*
     * 从未访问顶点集合中删除已找到最短路径的节点
     */
    private void pop(Vertex vertex) {
        unVisited.remove(vertex);
    }

    /*
     * 获取顶点到目标顶点的距离
     */
    private int getDistance(Vertex source, Vertex destination) {
        int sourceIndex = vertexs.indexOf(source);
        int destIndex = vertexs.indexOf(destination);
        return edges[sourceIndex][destIndex];
    }

    /*
     * 根据顶点位置获取顶点
     */
    private Vertex getVertex(int index) {
        return vertexs.get(index);
    }

    /*
     * 打印图
     */
    public void printGraph() {
        int verNums = vertexs.size();
        LOG.info("The verNums is : {}",verNums);
        LOG.info("The edges are :");
        for (int row = 0; row < verNums; row++) {
            for (int col = 0; col < verNums; col++) {
                if (Integer.MAX_VALUE == edges[row][col]) {
                    LOG.info("X ");
                    continue;
                }
                LOG.info("{} ",edges[row][col]);
            }
            LOG.info("\n");
        }
    }
}