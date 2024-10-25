package com.springboottayjv.demolearntayjv.controller;

import com.springboottayjv.demolearntayjv.configuration.Translator;
import com.springboottayjv.demolearntayjv.dto.request.UserDTO;
import com.springboottayjv.demolearntayjv.dto.response.ResponseData;
import com.springboottayjv.demolearntayjv.dto.response.ResponseError;
import com.springboottayjv.demolearntayjv.dto.response.UserDetailResponse;
import com.springboottayjv.demolearntayjv.exception.ResourceNotFoundException;
import com.springboottayjv.demolearntayjv.service.UserService;
import com.springboottayjv.demolearntayjv.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping("/")
    public ResponseData<Long> addUser(@Validated @RequestBody UserDTO user) {
        log.info("Request add user, {} {}", user.getFirstName(), user.getLastName());

        try {
            long userId = userService.saveUser(user);
            log.info("Request add user, {} {}", userId,user.getFirstName());
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }


    }



    @GetMapping("/id")
    public ResponseData<UserDTO> getUserById(@Min(1) Long id) {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),Translator.toLocale("user.add.success"));
    }


    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) long userId, @Valid @RequestBody UserDTO user) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, user);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable int userId, @RequestParam UserStatus status) {
        log.info("Request change status, userId={}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable @Min(1) long userId) {
        log.info("Request get user detail, userId={}", userId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "user", userService.getUser(userId));
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                       @RequestParam(defaultValue = "20", required = false) int pageSize,
                                       @RequestParam(required = false) String sortBy) {
        log.info("Request get all of users");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsers(pageNo,pageSize , sortBy));
    }

    @Operation(summary = "Get list of users with sort by multiple column", description = "Send a request via this API to get user list by pageNo and pageSize and with sort by multiple column")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUserWithSortByMultupleColumns(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                               @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                               @RequestParam(required = false) String... sorts) {
        log.info("Request get all of users with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUserWithSortByMultupleColumns(pageNo,pageSize , sorts));
    }


    @Operation(summary = "Get list of users with sort column and search", description = "Send a request via this API to get user list by pageNo and pageSize and with sort by column and search")
    @GetMapping("/list-with-sort-by-columns-and-search")
    public ResponseData<?> getAllUserWithSortByColumnAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                               @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                               @RequestParam(required = false) String search,
                                                               @RequestParam(required = false) String sortBy) {
        log.info("Request get all of users with sort by column and search");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUserWithSortByColumnAndSearch(pageNo, pageSize, search, sortBy));
    }

    @Operation(summary = "Get list of users with sort column and search", description = "Send a request via this API to get user list by pageNo and pageSize and with sort by column and search")
    @GetMapping("/advance-search-by-criteria")
    public ResponseData<?> advanceSearchByCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                               @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                               @RequestParam(defaultValue = "",required = false) String sortBy,
                                                                @RequestParam(defaultValue = "",required = false) String address,
                                                               @RequestParam(defaultValue = "",required = false) String... search) {
        log.info("Request advance search with criteria paging and sorting");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchByCriteria(pageNo, pageSize, sortBy,address ,search));
    }

}
