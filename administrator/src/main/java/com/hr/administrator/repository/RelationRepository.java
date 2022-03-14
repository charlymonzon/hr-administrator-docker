package com.hr.administrator.repository;

import com.hr.administrator.model.Relation;
import com.hr.administrator.view.RelationView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelationRepository extends CrudRepository<Relation, Long> {

    /**
     * Get sorted employee/supervisor relations from the employee/supervisor[0] to its supervisor[n-1]/supervisor[n]
     * @param companyId
     * @param employeeName
     * @return RelationView list sorted as: first element has the employee parameter (lowest in hierarchy), last element has the higher supervisor
     */
    List<RelationView> getUpperRelationsByCompanyIdAndEmployee(Long companyId, String employeeName);

    @Query(value = "SELECT COALESCE((SELECT version FROM user_relation WHERE company_id = :companyId ORDER BY version DESC LIMIT 1), 0);",
            nativeQuery = true)
    Integer getLastVersionByCompanyId(@Param("companyId") Long companyId);

    @Query(value = "SELECT count(id) FROM user_relation WHERE supervisor = :supervisorName and company_id=:companyId and is_deleted=false ;",
            nativeQuery = true)
    Integer employeesCount(@Param("companyId") Long companyId, @Param("supervisorName") String supervisorName);

    @Modifying //Solves the problem for updating none
    @Query(value = "UPDATE user_relation SET is_deleted = true WHERE company_id = :companyId AND version = :version and is_deleted = false ;",
            nativeQuery = true)
    void deleteRelationsByCompanyIdAndVersion(@Param("companyId") Long companyId, @Param("version") Integer version);
}
