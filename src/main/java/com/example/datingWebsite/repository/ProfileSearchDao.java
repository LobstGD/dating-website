package com.example.datingWebsite.repository;

import com.example.datingWebsite.dto.ProfileResponse;
import com.example.datingWebsite.mapper.ProfileMapper;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.ProfileGender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class ProfileSearchDao {

    private final EntityManager em;
    private final ProfileMapper profileMapper;

    public PagedModel<ProfileResponse> search(
            ProfileGender gender,
            Integer minAge,
            Integer maxAge,
            String city,
            String firstname,
            Pageable pageable
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> root = cq.from(Profile.class);

        List<Predicate> predicates = new ArrayList<>();
        if (gender != null) predicates.add(cb.equal(root.get("gender"), gender));
        if (minAge != null) predicates.add(cb.greaterThanOrEqualTo(root.get("age"), minAge));
        if (maxAge != null) predicates.add(cb.lessThanOrEqualTo(root.get("age"), maxAge));
        if (city != null) predicates.add(cb.equal(root.get("city"), city));
        if (firstname != null) predicates.add(cb.like(root.get("firstname"), "%" + firstname + "%"));

        Predicate[] predicateArray = predicates.toArray(new Predicate[0]);

        cq.where(cb.and(predicateArray));

        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            });
            cq.orderBy(orders);
        }

        TypedQuery<Profile> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<ProfileResponse> content = query.getResultList().stream()
                .map(profileMapper::toResponse)
                .toList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Profile> countRoot = countQuery.from(Profile.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(predicateArray));
        Long total = em.createQuery(countQuery).getSingleResult();

        Page<ProfileResponse> page = new PageImpl<>(content, pageable, total);
        return new PagedModel<>(page);
    }
}
