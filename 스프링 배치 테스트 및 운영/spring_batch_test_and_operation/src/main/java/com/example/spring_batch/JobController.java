package com.example.spring_batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@RestController
public class JobController {

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private JobExplorer jobExplorer;

	@PostMapping(value = "/batch/start")
	public String start(@RequestBody JobInfo jobInfo) throws Exception {

		for(Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();){

			SimpleJob job = (SimpleJob)jobRegistry.getJob(iterator.next());
			System.out.println("job name: " + job.getName());

			jobOperator.start(job.getName(), "id=" + jobInfo.getId());
		}

		return "batch is started";
	}

	@PostMapping(value = "/batch/restart")
	public String restart() throws Exception {

		for(Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();){

			SimpleJob job = (SimpleJob)jobRegistry.getJob(iterator.next());
			System.out.println("job name: " + job.getName());

			JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
			JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
			jobOperator.restart(lastJobExecution.getId());

		}

		return "batch is restarted";
	}

	@PostMapping(value = "/batch/stop")
	public String stop() throws Exception {

		for(Iterator<String> iterator = jobRegistry.getJobNames().iterator(); iterator.hasNext();){

			SimpleJob job = (SimpleJob)jobRegistry.getJob(iterator.next());
			System.out.println("job name: " + job.getName());

			Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
			JobExecution jobExecution = runningJobExecutions.iterator().next();

			jobOperator.stop(jobExecution.getId());
		}

		return "batch is stopped";
	}
}