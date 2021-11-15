package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User selectByLoginName(String name);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}