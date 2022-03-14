package com.hr.administrator.util;


import java.util.*;

public class RelationValidator {

    public static String getRoot(Map<String, String> relations) throws Exception {
        List<String> roots = new ArrayList<>();
        for (String supervisor : relations.values()) {
            if(relations.get(supervisor)==null) {
                roots.add(supervisor);
            }
        }

        if(roots.size()>1){
            //TODO: Pick a custom exception
            throw new Exception("More than one root");
        }
        return roots.get(0);
    }

//    public static boolean checkCycle(Map<String, String> relations) throws Exception {
//        HierarchyGraph graph = new HierarchyGraph();
//
//        Map<String, Vertex> vertices = new HashMap<>();
//        for (String employee : relations.keySet()) {
//            if(!vertices.containsKey(employee)) {
//                Vertex empVertex = new Vertex(employee);
//                vertices.put(employee, empVertex);
//                graph.addVertex(empVertex);
//            }
//            String supervisor = relations.get(employee);
//            if(!vertices.containsKey(supervisor)) {
//                Vertex supVertex = new Vertex(employee);
//                vertices.put(supervisor, supVertex);
//                graph.addVertex(supVertex);
//            }
//
//            graph.addEdge(vertices.get(employee), vertices.get(supervisor));
//        }
//
//        Vertex root = vertices.get(getRoot(relations));
//
//        graph.setRoot(root);
//
//        return graph.hasCycle();
//    }
}
