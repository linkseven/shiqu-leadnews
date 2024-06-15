package com.shiqu.schedule.service.impl;

import com.shiqu.model.schedule.dtos.Task;
import com.shiqu.schedule.ScheduleApplication;
import com.shiqu.schedule.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    @Test
    void addTask() {
        Task task = new Task();
        task.setExecuteTime(new Date().getTime());
        task.setParameters("task test".getBytes());
        task.setTaskType(100);
        task.setPriority(50);

        long taskId = taskService.addTask(task);
        System.out.println(taskId);
    }

    @Test
    public void pollTest(){
        Task poll = taskService.poll(50, 100);
        System.out.println(poll);
    }
}