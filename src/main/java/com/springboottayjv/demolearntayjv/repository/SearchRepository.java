package com.springboottayjv.demolearntayjv.repository;

import com.springboottayjv.demolearntayjv.dto.response.PageResponse;
import com.springboottayjv.demolearntayjv.model.UserEntity;
import com.springboottayjv.demolearntayjv.repository.criteria.SearchCriteria;
import com.springboottayjv.demolearntayjv.repository.criteria.UserSearchCriteriaQueryConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SearchRepository {

    private static final Logger log = LoggerFactory.getLogger(SearchRepository.class);
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

        log.info("num of users: {}", users);
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
    public PageResponse advanceSearchUser(int pageNo, int pageSize, String sortBy, String... search) {

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
        List<UserEntity> users = getUsers(pageNo,  pageSize,criteriaList,sortBy);


        Long totalElements = 1l;
        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPage(totalElements)
                .items(users)
                .build();

    }

    private List<UserEntity> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = criteriaQuery.from(UserEntity.class);

        // handle search conditions
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,predicate,root);

        criteriaList.forEach(queryConsumer);
        predicate = queryConsumer.getPredicate();

        criteriaQuery.where(predicate);
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

}
