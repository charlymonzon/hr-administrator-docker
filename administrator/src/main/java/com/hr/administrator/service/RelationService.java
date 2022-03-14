package com.hr.administrator.service;

import com.hr.administrator.exception.InvalidHierarchyException;
import com.hr.administrator.model.CompanyHierarchy;

import java.util.Map;

public interface RelationService {

    Map<String, Object> getSupervisorsByCompanyIdAndEmployee(Long companyId, String employeeName);

    CompanyHierarchy save(CompanyHierarchy companyHierarchy);

    Integer getLastVersion(Long companyId);

    void deleteRelationsByCompanyAndVersion(Long companyId, Integer version);

    void deleteLastRelationsByCompany(Long companyId);

    CompanyHierarchy buildNewCompanyHierarchy(Long companyId, Map<String, String> relationsMap) throws InvalidHierarchyException;
}
