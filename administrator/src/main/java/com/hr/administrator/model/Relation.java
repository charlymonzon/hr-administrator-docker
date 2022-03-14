package com.hr.administrator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_relation")
public class Relation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "employee")
    private String employee;

    @Column(name = "supervisor")
    private String supervisor;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "version")
    private Integer version;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "created_at")
    private Date createdAt;

    public Relation(String employee, String supervisor, Integer version, Long companyId, Date createdAt) {
        this.employee = employee;
        this.supervisor = supervisor;
        this.version = version;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.isDeleted = false;
    }

    //TODO: Maybe a builder pattern is no needed because we dont have optional parameters forr constructor

    //Builder Class
    public static class RelationListBuilder{

        private Map<String, String> relationsMap;
        private int version;
        private long companyId;
        private Date createdAt;

        public RelationListBuilder(Map<String, String> relations, Integer version, Long companyId) {
            this.relationsMap = relations;
            this.version = version;
            this.companyId  = companyId;
            this.createdAt = new Date();
        }

        public List<Relation> build(){
            List<Relation> relations = new ArrayList<>();
            for (String key : this.relationsMap.keySet()) {
                relations.add(new Relation(key, this.relationsMap.get(key), version, companyId, createdAt));
            }
            return relations;
        }

    }
}
