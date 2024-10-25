package com.springboottayjv.demolearntayjv.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    // firstName:T , lastName:T , id:T.....

    private String key; //
    private String operation; // : , < , >
    private Object value;
}