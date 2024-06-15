package com.shiqu.xxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloJob {

    @Value("${xxl.job.executor.port}")
    private String port;

    @XxlJob("demoJobHandler")
    public void helloJob(){
        System.out.println("简单任务执行了。。。。" + port);
    }

    @XxlJob("shardingJobHandler")
    public void shardingJob(){
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        XxlJobHelper.log("当前分片序号 = {}，总分片数 = {}", shardIndex, shardTotal);
        System.out.println(String.format("当前分片序号 = %s，总分片数 = %s", shardIndex, shardTotal));
        List<Integer> list = getList();
        for (Integer integer : list) {
            if (integer % shardTotal == shardIndex){
                System.out.println("当前第"+shardIndex+"分片执行了，任务项为："+integer);
            }
        }
    }

    public List<Integer> getList(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        return list;
    }
}