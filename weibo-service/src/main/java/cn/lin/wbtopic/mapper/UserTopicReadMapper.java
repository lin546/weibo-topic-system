package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.UserTopicRead;

public interface UserTopicReadMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTopicRead record);

    int insertSelective(UserTopicRead record);

    UserTopicRead selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTopicRead record);

    int updateByPrimaryKey(UserTopicRead record);
}