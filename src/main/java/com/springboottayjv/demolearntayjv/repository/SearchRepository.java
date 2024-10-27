package com.springboottayjv.demolearntayjv.repository;

import com.springboottayjv.demolearntayjv.dto.response.PageResponse;
import com.springboottayjv.demolearntayjv.model.AddressEntity;
import com.springboottayjv.demolearntayjv.model.UserEntity;
import com.springboottayjv.demolearntayjv.repository.criteria.SearchCriteria;
import com.springboottayjv.demolearntayjv.repository.criteria.UserSearchCriteriaQueryConsumer;
import com.springboottayjv.demolearntayjv.repository.specification.SpecSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SearchRepository {


    @PersistenceContext
    EntityManager entityManager;



    public PageResponse<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize,String search, String sortBy) {

       // query list user
        StringBuilder sqlQuery = new StringBuilder("select new com.springboottayjv.demolearntayjv.dto.response.UserDetailResponse(u.id,u.firstName,u.lastName,u.phone,u.email) from UserEntity u where 1=1 ");
        if(StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:fisrtName)");
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower(u.email) like lower(:email)");
        }
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                sqlQuery.append(String.format("order by u.%s %s", matcher.group(1), matcher.group(3)));
            }

        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if(StringUtils.hasLength(search)) {
            selectQuery.setParameter("fisrtName",String.format("%%%s%%",search));
            selectQuery.setParameter("lastName",String.format("%%%s%%",search));
            selectQuery.setParameter("email",String.format("%%%s%%",search));

        }
        List users = selectQuery.getResultList();

        // query record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from UserEntity u where 1=1 ");
        if(StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower(u.email) like lower(?3)");
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1,String.format("%%%s%%",search));
            selectCountQuery.setParameter(2,String.format("%%%s%%",search));
            selectCountQuery.setParameter(3,String.format("%%%s%%",search));

        }

        Long totalElement = (Long) selectCountQuery.getSingleResult();

        Page<?> page = new PageImpl<Object>(users,PageRequest.of(pageNo,pageSize),totalElement);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPage(page.getTotalPages())
                .items(page.stream().toList())
                .build();

    }
    public PageResponse advanceSearchUser(int pageNo, int pageSize, String sortBy, String address ,String... search) {

        List<SearchCriteria> criteriaList = new ArrayList<>();

        // 1. get list user
        if(search != null) {
                for(String searchi : search) {
                    // firstName:value
                    Pattern pattern = Pattern.compile("(\\w+?)([:><])(.*)");
                    Matcher matcher = pattern.matcher(searchi);
                    if(matcher.find()) {
                        criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3) ));
                    }
                }
        }



        // 2. get total record
        List<UserEntity> users = getUsers(pageNo,pageSize,criteriaList,sortBy,address);


        Long totalElements = getTotalElement(criteriaList,address);
        return PageResponse.builder()
                .page(pageNo) // offset = index of record
                .size(pageSize) // sizes of record
                .totalPage(totalElements.intValue()) //total element
                .items(users)
                .build();

    }



    private List<UserEntity> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy , String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);

        // handle search conditions
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);

        if(StringUtils.hasLength(address)) {
            Join<AddressEntity,UserEntity> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // search for all field of address
            criteriaQuery.where(predicate,addressPredicate);
        }
        else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            criteriaQuery.where(predicate);
        }

        //handle sortBy
        if(StringUtils.hasLength(sortBy)) {
                // firstName:value
                Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
                Matcher matcher = pattern.matcher(sortBy);
                if(matcher.find()) {
                    String columnName = matcher.group(1);
                    if(matcher.group(3).equalsIgnoreCase("desc")) {
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(columnName)));
                    }
                    else {
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
                    }
                }
        }

        return entityManager.createQuery(criteriaQuery).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }


    private Long getTotalElement(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);

        // handle search conditions
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);

        if(StringUtils.hasLength(address)) {
            Join<AddressEntity,UserEntity> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // search for all field of address
            criteriaQuery.select(criteriaBuilder.count(root));
            criteriaQuery.where(predicate,addressPredicate);
        }
        else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            criteriaQuery.select(criteriaBuilder.count(root));
            criteriaQuery.where(predicate);
        }
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    public PageResponse getUserJoinedAddress(Pageable pageable, String[] user , String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);
        Join<AddressEntity,UserEntity> addressRoot = root.join("addresses");

        // build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toPredicate(root,criteriaBuilder,criteria);
                userPre.add(predicate);
            }
        }
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toPredicate(addressRoot,criteriaBuilder,criteria);
                addressPre.add(predicate);
            }
        }

        Predicate userPredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre = criteriaBuilder.and(userPredicateArr,addressPredicateArr);

        criteriaQuery.where(finalPre);


        List<UserEntity> users = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = count(user,address);

        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(count)
                .items(users)
                .build();
    }

    private long count(String[] user , String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);
        Join<AddressEntity,UserEntity> addressRoot = root.join("addresses");

        // build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toPredicate(root,criteriaBuilder,criteria);
                userPre.add(predicate);
            }
        }
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toPredicate(addressRoot,criteriaBuilder,criteria);
                addressPre.add(predicate);
            }
        }

        Predicate userPredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre = criteriaBuilder.and(userPredicateArr,addressPredicateArr);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(finalPre);



        return entityManager.createQuery(criteriaQuery).getSingleResult();




    }



    public Predicate toPredicate(@NonNull final Root<UserEntity> root, @NonNull final CriteriaBuilder builder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public Predicate toPredicate(@NonNull final Join<AddressEntity,UserEntity> root, @NonNull final CriteriaBuilder builder, SpecSearchCriteria criteria) {

        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }



}
