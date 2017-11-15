/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex> {

    /**
     * 节点名称(A,B,C,D)
     */
    private Long vertex_id;

    /**
     * 最短路径长度
     */
    private int distance;

    /**
     * 节点是否已经出列(是否已经处理完毕)
     */
    public boolean marked;

    /**
     * 最短路径途径节点
     */
    private List<Long> path= new ArrayList<Long>();

    public Vertex(Long vertex_id) {
        this.vertex_id = vertex_id;
        this.distance = Integer.MAX_VALUE; //初始设置为无穷大
        this.setMarked(false);
    }

    public Vertex(Long vertex_id, int distance) {
        this.vertex_id = vertex_id;
        this.distance = distance;
        this.setMarked(false);
    }

    @Override
    public int compareTo(Vertex o) {
        return o.distance > distance ? -1 : 1;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public Long getVertext_id() {
        return vertex_id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Long>getPath(){
        return this.path;
    }

    public List<Long> setPath(Vertex vertex){
        for(Long Path:vertex.getPath())
        {
            this.path.add(Path);
        }
        this.path.add(vertex.getVertext_id());
        return this.path;
    }
}