# spring-batch-practice

<img width="2260" alt="_2020-10-31__10 52 43" src="https://user-images.githubusercontent.com/19872667/98110020-d3833b80-1ee1-11eb-8999-cfce3e45553c.png">

@EnableBatchProcessing 의 마법
<pre>
  <b>기본적인 bean 들이 자동으로 등록되며, 또한 custom 이 가능하다.</b>

  a {@link JobRepository} (bean name "jobRepository")
  a {@link JobLauncher} (bean name "jobLauncher")
  a {@link JobRegistry} (bean name "jobRegistry")
  a {@link org.springframework.batch.core.explore.JobExplorer} (bean name "jobExplorer")
  a {@link PlatformTransactionManager} (bean name "transactionManager")
  a {@link JobBuilderFactory} (bean name "jobBuilders") as a convenience to prevent you from having to inject the job repository into every job, as in the examples above
  a {@link StepBuilderFactory} (bean name "stepBuilders") as a convenience to prevent you from having to inject the job repository and transaction manager into every step
  
  <b>The transaction manager provided by this annotation will be of type:</b>
  
 {@link org.springframework.batch.support.transaction.ResourcelessTransactionManager} if no {@link javax.sql.DataSource} is provided within the context
 {@link org.springframework.jdbc.datasource.DataSourceTransactionManager} if a {@link javax.sql.DataSource} is provided within the context
 </pre>

Job Parameter 사용 방법<br>

<pre>
java -jar ./build/libs/{jar 이름}.jar "{key1}={value1}" "{key2}={value2}" "{key3}={value3}";
</pre>

Tasklet 에서 아래와 같이 사용
<pre>
String value1 = chunkContext.getStepContext().getJobParameters().getOrDefault("key1", "").toString();
String value2 = chunkContext.getStepContext().getJobParameters().getOrDefault("key2", "").toString();
String value3 = chunkContext.getStepContext().getJobParameters().getOrDefault("key3", "").toString();
</pre>
