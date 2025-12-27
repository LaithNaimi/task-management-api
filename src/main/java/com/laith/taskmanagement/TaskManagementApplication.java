package com.laith.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(TaskManagementApplication.class, args);
    }

}
