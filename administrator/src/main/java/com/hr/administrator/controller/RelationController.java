package com.hr.administrator.controller;

import com.hr.administrator.exception.InvalidInputException;
import com.hr.administrator.model.CompanyHierarchy;
import com.hr.administrator.service.RelationService;
import com.hr.administrator.util.HierarchyGraph;
import com.hr.administrator.util.SqlSafeUtil;
import com.hr.administrator.util.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/relation")
public class RelationController {

    private RelationService relationService;

    public RelationController(RelationService relationService){
        this.relationService = relationService;
    }

    private static final Logger logger = LogManager.getLogger(RelationController.class);

    @PostMapping("/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> save(@PathVariable Long companyId, @RequestBody Map<String, String> relations){
        //check for invalid inputs
        checkSaveInputs(companyId, relations);

        CompanyHierarchy companyHierarchy = relationService.buildNewCompanyHierarchy(companyId, relations);
        companyHierarchy = relationService.save(companyHierarchy);
        return mapHierarchyGraphToMap(companyHierarchy.getHierarchy());
    }

    @GetMapping("/{companyId}/{employee}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> get(@PathVariable Long companyId, @PathVariable String employee) {
        //check for invalid inputs
        checkGetInputs(companyId, employee);

        Map<String, Object> relationList = relationService.getSupervisorsByCompanyIdAndEmployee(companyId, employee);
        return relationList;
    }

    Map<String, Object> mapHierarchyGraphToMap(HierarchyGraph hierarchyGraph) {
        if(hierarchyGraph.getVertices().size()==0) {
            return new HashMap<>();
        }

        Map<String, Object> hierarchy = new HashMap<>();
        Vertex root = hierarchyGraph.getRoot();
        hierarchy.put(root.getLabel(), getSupEmployeesMap(root));

        return hierarchy;
    }

    private Map<String, Object> getSupEmployeesMap(Vertex supervisor) {
        if(supervisor.getAdjacencyList().size()==0)
            return new HashMap<>();

        Map<String, Object> employees = new HashMap<>();
        for (Vertex employee : supervisor.getAdjacencyList()) {
            employees.put(employee.getLabel(), getSupEmployeesMap(employee));
        }
        return employees;
    }

    private void checkCompanyId(Long companyId) {
        if(Objects.isNull(companyId))
            throw new InvalidInputException("Company id cannot be null");
    }

    private void checkGetInputs(Long companyId, String employee) {
        checkCompanyId(companyId);

        //Check null/empty/blank for employee
        if(Objects.isNull(employee) || employee.isEmpty() || employee.isBlank())
            throw new InvalidInputException("Null and empty values are not allowed for employee");

        //Check Sql Injection patterns
        if(!SqlSafeUtil.isSqlInjectionSafe(employee))
            throw new InvalidInputException("Pattern not allowed");
    }

    private void checkSaveInputs(Long companyId, Map<String, String> inputs) {
        checkCompanyId(companyId);

        if(Objects.isNull(inputs))
            throw new InvalidInputException("Body cannot be null");

        Set<String> tested = new HashSet<>();
        for (String employee : inputs.keySet()) {

            //Check null/empty/blank values for body
            if(Objects.isNull(employee) || Objects.isNull(inputs.get(employee))
                || employee.isEmpty() || inputs.get(employee).isEmpty()
                || employee.isBlank() || inputs.get(employee).isBlank())
                throw new InvalidInputException("Null and empty values are not allowed");

            String supervisor = inputs.get(employee);

            //Check Sql Injection patterns
            if(!tested.contains(employee)) {
                if(SqlSafeUtil.isSqlInjectionSafe(employee))
                    tested.add(employee);
                else
                    throw new InvalidInputException("Pattern not allowed");
            }
            if(!tested.contains(supervisor)) {
                if(SqlSafeUtil.isSqlInjectionSafe(supervisor))
                    tested.add(supervisor);
                else
                    throw new InvalidInputException("Pattern not allowed");
            }
        }
    }

}
