package com.sky.service;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;




public interface CategoryService {

    void update(CategoryDTO categoryDTO);

    PageResult selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void update_status(Integer status,Long id);

    void insert(CategoryDTO categoryDTO);

    void deleteById(Long id);

    Category[] selectByType(String type);

}
