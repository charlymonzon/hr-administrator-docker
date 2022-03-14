package com.hr.administrator.service.impl;

import com.hr.administrator.exception.InvalidHierarchyException;
import com.hr.administrator.exception.NoContentException;
import com.hr.administrator.model.CompanyHierarchy;
import com.hr.administrator.model.Relation;
import com.hr.administrator.util.HierarchyGraph;
import com.hr.administrator.util.Vertex;
import com.hr.administrator.view.RelationView;
import com.hr.administrator.repository.RelationRepository;
import com.hr.administrator.service.RelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DefaultRelationService implements RelationService {

    RelationRepository relationRepository;

    public DefaultRelationService(RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    /**
     * Builds a CompanyHierarchy object that keeps the information about the new hierarchy for that company and
     * allows to check these relations as a Directed Graph to look for non sense patterns
     * @param companyId
     * @param relationsMap
     * @return a new hierarchy object for that companyId and relationsMap
     */
    @Override
    public CompanyHierarchy buildNewCompanyHierarchy(Long companyId, Map<String, String> relationsMap) {
        Integer lastVersion = this.getLastVersion(companyId);
        HierarchyGraph hierarchyGraph = buildGraph(relationsMap);

        return new CompanyHierarchy(companyId, lastVersion+1, hierarchyGraph);
    }

    /**
     * Saves the new company hierarchy and soft deletes the previous one
     * @param companyHierarchy
     * @return companyHierarchy
     */
    @Transactional
    @Override
    public CompanyHierarchy save(CompanyHierarchy companyHierarchy) {
        List<Relation> relations = this.buildRelationsFromCompanyHierarchy(companyHierarchy);
        relationRepository.saveAll(relations);
        this.deleteRelationsByCompanyAndVersion(companyHierarchy.getCompanyId(), companyHierarchy.getPreviousVersion());
        return companyHierarchy;
    }

    @Override
    public void deleteRelationsByCompanyAndVersion(Long companyId, Integer version) {
            relationRepository.deleteRelationsByCompanyIdAndVersion(companyId, version);
    }

    @Override
    public void deleteLastRelationsByCompany(Long companyId) {
        int lastVersion = this.getLastVersion(companyId);
        relationRepository.deleteRelationsByCompanyIdAndVersion(companyId, lastVersion);
    }

    /**
     * Gets the hierarchy from the employee to the higher supervisor
     * @param companyId
     * @param employeeName
     * @return Map representing the employee's supervisors
     * @throws NoContentException if has no supervisors to show
     */
    @Override
    public Map<String, Object> getSupervisorsByCompanyIdAndEmployee(Long companyId, String employeeName) {
        // Sorted one/one relations: first element has the employee parameter (lowest in hierarchy), last element has the higher supervisor
        List<RelationView> employeeRelationsToHigherSupervisor =  this.relationRepository.getUpperRelationsByCompanyIdAndEmployee(companyId, employeeName);

        if(employeeRelationsToHigherSupervisor.size()==0 && relationRepository.employeesCount(companyId, employeeName)>0)
            return Collections.emptyMap();
            //throw new NoContentException("This employee is the higher supervisor, then there are no supervisors to be shown");

        // Attention!: Now we reverse it to start working from highest to lower in hierarchy
        Collections.reverse(employeeRelationsToHigherSupervisor);

        return getSupervisors(employeeRelationsToHigherSupervisor);
    }

    private List<Relation> buildRelationsFromCompanyHierarchy(CompanyHierarchy companyHierarchy) {
        if (companyHierarchy.getHierarchy().getVertices().size()==0)
            return Collections.emptyList();

        List<Relation> relations = new ArrayList<>();
        relations = this.addRelations(relations, companyHierarchy.getHierarchy().getRoot(), companyHierarchy);
        return relations;
    }

    private List<Relation> addRelations(List<Relation> relations, Vertex supervisor, CompanyHierarchy companyHierarchy) {
        if(supervisor.getAdjacencyList().size()==0){
            return relations;
        }
        for (Vertex employee: supervisor.getAdjacencyList()){
            relations.add(new Relation(employee.getLabel(), supervisor.getLabel(), companyHierarchy.getVersion(),
                    companyHierarchy.getCompanyId(), companyHierarchy.getCreatedAt()));
            relations = addRelations(relations, employee, companyHierarchy);
        }

        return relations;
    }


    @Override
    public Integer getLastVersion(Long companyId) {
        return relationRepository.getLastVersionByCompanyId(companyId);
    }

    //Improve code replication
    private Map<String, Object> getSupervisors(List<RelationView> relations) {
        String supervisor = relations.get(0).getSupervisor();

        //TODO: If exists but has no supervisors, respond that

        //Base case with 1 element
        // just return supervisor : employee: emptyMap
        if(relations.size()==1) {
            Map<String, Object> employees = new HashMap<>();
            String employee = relations.get(0).getEmployee();
            employees.put(employee, new HashMap<>());

            Map<String, Object> result = new HashMap<>();
            result.put(supervisor, employees);

            return result;
        }

        Map<String, Object> supervisors = new HashMap<>();
        // Adds employee to supervisor recursively
        supervisors.put(supervisor, addEmployee(relations, 1));

        return supervisors;
    }

    // recursive function to add employees to supervisors
    private Map<String, Object> addEmployee(List<RelationView> relations, int index) {
        String supervisor = relations.get(index).getSupervisor();
        Map<String, Object> supervisors = new HashMap<>();

        //base case for last item
        // just return supervisor : employee: emptyMap
        if(index==relations.size()-1) {
            Map<String, Object> employees = new HashMap<>();
            String employee = relations.get(index).getEmployee();
            employees.put(employee, new HashMap<>());

            supervisors.put(supervisor, employees);
            return supervisors;
        }

        // employee its a supervisor and gets its employees
        supervisors.put(supervisor, addEmployee(relations, index+1));
        return supervisors;
    }

    /**
     * Builds a graph for all the relations and checks it's structure
     * @param relations
     * @return Hierarchy as a graph
     * @throws InvalidHierarchyException if root is not unique or it has cyclic connections
     */
    public HierarchyGraph buildGraph(Map<String, String> relations) throws InvalidHierarchyException {
        HierarchyGraph graph = new HierarchyGraph();
        Map<String, Vertex> vertices = new HashMap<>();
        Set<String> employees = new HashSet<>();
        for (String employee : relations.keySet()) {
            if(!employees.contains(employee)) //Check for cyclic relations
                employees.add(employee);
            else
                throw new InvalidHierarchyException("You have a cycle in the employee: " + employee);

            if(!vertices.containsKey(employee)) {
                Vertex empVertex = new Vertex(employee);
                vertices.put(employee, empVertex);
                graph.addVertex(empVertex);
            }

            String supervisor = relations.get(employee);
            if(supervisor.equals(employee)) //Check for employees who is managing it self
                throw new InvalidHierarchyException("An employee can not manage to it self, please check relations of employee: " + employee);

            if(!vertices.containsKey(supervisor)) {
                Vertex supVertex = new Vertex(supervisor);
                vertices.put(supervisor, supVertex);
                graph.addVertex(supVertex);
            }

            graph.addEdge(vertices.get(supervisor), vertices.get(employee));
        }

        Vertex root = vertices.get(calculateRootFromRelationsMap(relations));
        graph.setRoot(root);

        if(graph.hasCycle())
            throw new InvalidHierarchyException("There are cycles in the relations");

        return graph;
    }

    private String calculateRootFromRelationsMap(Map<String, String> relations) throws InvalidHierarchyException {
        List<String> roots = new ArrayList<>();
        for (String supervisor : relations.values()) {
            if(relations.get(supervisor)==null) {
                roots.add(supervisor);
            }
        }
        if(roots.size()>1)
            throw new InvalidHierarchyException("There is more than one root");
        if(roots.size()==0)
            throw new InvalidHierarchyException("There is no defined root");

        return roots.get(0);
    }

}
