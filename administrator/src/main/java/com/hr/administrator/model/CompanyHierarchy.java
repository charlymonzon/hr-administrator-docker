package com.hr.administrator.model;

import com.hr.administrator.util.HierarchyGraph;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class CompanyHierarchy {
    private final Long companyId;
    private final Date createdAt;
    private final Integer version;
    private List<Relation> relations;
    private final HierarchyGraph hierarchy;

    public CompanyHierarchy(Long companyId, Integer version, List<Relation> relations) {
        this.companyId = companyId;
        this.version = version;
        this.relations = relations;
        this.hierarchy = null;
        createdAt = new Date();
    }

    public CompanyHierarchy(Long companyId, Integer version, HierarchyGraph hierarchy) {
        this.companyId = companyId;
        this.version = version;
        this.hierarchy = hierarchy;
        createdAt = new Date();
    }

    public Integer getPreviousVersion() {
        return version-1;
    }
}
