package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.Feed;

import java.util.List;

public interface FeedMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feed record);

    int insertSelective(Feed record);

    Feed selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Feed record);

    int updateByPrimaryKey(Feed record);

    List<Feed> list(Integer userId);
}