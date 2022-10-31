package com.hcmute.hotel.repository.custom.impl;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.custom.UserRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> search(String keyword, UserStatus userStatus, AppUserRole userRole, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        //Create filter query and count query
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);

        Root<UserEntity> root = query.from(UserEntity.class);
        //Although filter query and count query have the same where clause, but we have to create 2 list predicate separately
        List<Predicate> predicates = new ArrayList<>();
        //Filter by keyword, we have 3 options: filter by carType field, carNumber field or customer name
        Predicate predicatesPhone =  cb.like(root.get("phone"), "%" + keyword + "%");
        Predicate predicatesFullName =cb.like(root.get("fullName"), "%" + keyword + "%");
        Predicate predicatesEmail = cb.like(root.get("email"), "%" + keyword + "%");
        predicates.add(cb.or(predicatesPhone,predicatesFullName,predicatesEmail));

        //Filter by status, we have 2 options: filter by active, inactice
        if (userStatus != null) {
            if (userStatus.equals(UserStatus.INACTIVE)) {
                predicates.add(cb.equal(root.get("status"), false));
            }
            if (userStatus.equals(UserStatus.ACTIVE)) {
                predicates.add(cb.equal(root.get("status"), true));
            }
        }

        //Filter by role, we have 3 options here: ROLE_ADMIN, ROLE_USER, ROLE_CUSTOMER
        if (userRole != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserEntity> subqueryUser = subquery.from(UserEntity.class);
            Join<UserEntity, RoleEntity> subqueryRoles = subqueryUser.join("roles");
            if (userRole.equals(AppUserRole.ROLE_USER)) {
                subquery.select(subqueryUser.get("id")).where(cb.equal(subqueryRoles.get("name"), AppUserRole.ROLE_USER));
                predicates.add(cb.in(root.get("id")).value(subquery));
            } else if (userRole.equals(AppUserRole.ROLE_ADMIN)) {
                subquery.select(subqueryUser.get("id")).where(cb.equal(subqueryRoles.get("name"), AppUserRole.ROLE_ADMIN));
                predicates.add(cb.in(root.get("id")).value(subquery));
            } else if (userRole.equals(AppUserRole.ROLE_OWNER)) {
                subquery.select(subqueryUser.get("id")).where(cb.equal(subqueryRoles.get("name"), AppUserRole.ROLE_OWNER));
                predicates.add(cb.in(root.get("id")).value(subquery));
            }
        }
        //Paging

        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        List<UserEntity> listRepairedCar =
                entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
        return listRepairedCar;
    }
}



