package com.example.realestate.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {

    public int getLength(int count,int size){
        if(count < size){
            return 1;
        }else if(count % size == 0){
            return count/size;
        }else {
            return count/size + 1;
        }
    }

    public Pageable getCheckedPageable(int pageNumber,int size,int length){
        if(pageNumber >= length){
            return PageRequest.of(0,size);
        }else {
            return PageRequest.of(pageNumber,size);
        }
    }
}