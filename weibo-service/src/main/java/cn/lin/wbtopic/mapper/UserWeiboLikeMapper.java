package cn.lin.wbtopic.mapper;

import cn.lin.wbtopic.model.UserWeiboLike;

public interface UserWeiboLikeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWeiboLike record);

    int insertSelective(UserWeiboLike record);

    UserWeiboLike selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWeiboLike record);

    int updateByPrimaryKey(UserWeiboLike record);
}