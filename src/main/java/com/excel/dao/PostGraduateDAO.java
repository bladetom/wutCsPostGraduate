package com.excel.dao;

import com.excel.entity.DemoData;
import com.excel.entity.PostGraduate;

import java.util.List;

public class PostGraduateDAO {
    public void save(List<PostGraduate> list) {
        // 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入
    }
}
