package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Topic record);

    int insertSelective(Topic record);

    Topic selectByPrimaryKey(Integer id);

    Topic selectByTitle(String title);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);

    int increaseDiscussCount(Integer id);

    int increaseFollowCount(Integer id);

    int increaseReadCount(Integer id);

    List<Topic> list();

    int updateDiscussCount(@Param(value = "topicId") Integer topicId,
                           @Param(value = "count") Integer count);
}