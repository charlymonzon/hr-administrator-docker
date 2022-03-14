package com.hr.administrator.util;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class HierarchyGraph {
    private Set<Vertex> vertices;
    private Vertex root;

    public HierarchyGraph() {
        this.vertices = new HashSet<>();
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
    }

    public void addEdge(Vertex from, Vertex to) {
        from.addNeighbor(to);
    }

    public boolean hasCycle() {
        return !Objects.isNull(this.root) && this.hasCycle(this.root);
    }

    private boolean hasCycle(Vertex sourceVertex) {
        sourceVertex.setBeingVisited(true);

        for (Vertex neighbor : sourceVertex.getAdjacencyList()) {
            if (neighbor.isBeingVisited()) {
                // backward edge exists
                return true;
            } else if (!neighbor.isVisited() && hasCycle(neighbor)) {
                return true;
            }
        }

        sourceVertex.setBeingVisited(false);
        sourceVertex.setVisited(true);
        return false;
    }
}
