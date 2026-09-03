package com.digviajay.taskflow;

import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

@SpringBootApplication
public class TaskflowApplication {

	public static void main(String[] args) {
		ApplicationContext context =  SpringApplication.run(TaskflowApplication.class, args);


	}

}
