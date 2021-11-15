package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.Weibo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeiboMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Weibo record);

    int insertSelective(Weibo record);

    Weibo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Weibo record);

    int updateByPrimaryKey(Weibo record);

    List<Weibo> listWeibo();

    List<Weibo> listWeiboByTopicId(Integer id);

    List<Weibo> listWeiboByIds(@Param(value = "ids") List<Integer> ids);

    int increaseLikeCount(Integer id);
}