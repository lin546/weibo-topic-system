package cn.lin.wbtopic.service;

import cn.lin.wbtopic.wbservice.facade.TopicService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SyncDiscussCountJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TopicService topicService = (TopicService) jobExecutionContext.getJobDetail().getJobDataMap().get("springBean");
        topicService.syncTopicDiscussCount();
    }
}
