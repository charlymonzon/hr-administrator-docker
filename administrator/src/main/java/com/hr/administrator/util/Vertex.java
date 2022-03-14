package com.hr.administrator.util;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Vertex {

    private String label;
    private boolean beingVisited;
    private boolean visited;
    private List<Vertex> adjacencyList;

    public Vertex(String label) {
        this.label = label;
        this.adjacencyList = new ArrayList<>();
    }

    public void addNeighbor(Vertex adjacent) {
        this.adjacencyList.add(adjacent);
    }
    //getters and setters
}
