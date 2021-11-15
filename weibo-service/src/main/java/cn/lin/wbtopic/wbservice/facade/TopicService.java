package cn.lin.wbtopic.wbservice.facade;

import cn.lin.wbtopic.model.Topic;
import cn.lin.wbtopic.service.result.ServiceResult;

import java.util.List;

public interface TopicService {
    ServiceResult<Topic> create(Topic topic);
    ServiceResult<Topic> get(Integer id);
    ServiceResult<Topic> getByTitle(String title);
    ServiceResult increaseDiscussCount(Integer id);
    ServiceResult<List<Topic>> list();
    ServiceResult follow(Integer userId,Integer topicId);
    ServiceResult read(Integer userId,Integer topicId);

    void syncTopicDiscussCount();

}
