package cn.lin.wbtopic.wbservice.facade;

import cn.lin.wbtopic.model.Weibo;
import cn.lin.wbtopic.service.result.ServiceResult;

import java.util.List;

public interface WeiboService {

    ServiceResult<Weibo> get(Integer id);

    ServiceResult<Weibo> create(Weibo weiboForCreate);

    ServiceResult<List<Weibo>> list();

    ServiceResult like(Integer userId,Integer weiboId);

    ServiceResult<List<Weibo>> listByTopicId(Integer topicId);

    ServiceResult<List<Weibo>> listByFeed(Integer userId);
}
