package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.UserTopicFollow;

import java.util.List;

public interface UserTopicFollowMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTopicFollow record);

    int insertSelective(UserTopicFollow record);

    UserTopicFollow selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTopicFollow record);

    int updateByPrimaryKey(UserTopicFollow record);

    List<UserTopicFollow> listFollowByTopicId(Integer topicId);
}