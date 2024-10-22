package com.springboottayjv.demolearntayjv.service;

import com.springboottayjv.demolearntayjv.dto.request.UserDTO;
import com.springboottayjv.demolearntayjv.dto.response.PageResponse;
import com.springboottayjv.demolearntayjv.dto.response.UserDetailResponse;
import com.springboottayjv.demolearntayjv.util.UserStatus;
import org.springframework.stereotype.Service;

@Service
public interface UserService{
    long saveUser(UserDTO request);

    void updateUser(long userId, UserDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize,String sortBy);

    PageResponse<?> getAllUserWithSortByMultupleColumns(int pageNo, int pageSize,String... sorts);

    PageResponse<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize,String search,String sortBy);
//
//    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);
//
//    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);
//
//    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);
}
