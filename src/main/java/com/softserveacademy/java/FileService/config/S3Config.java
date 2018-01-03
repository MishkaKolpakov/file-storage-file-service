package com.softserveacademy.java.FileService.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.softserveacademy.java.FileService.exceptions.S3Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static com.amazonaws.services.s3.internal.Constants.MB;

@Configuration
@Profile("s3")
public class S3Config {
    @Value("${jsa.aws.access_key_id}")
    private String awsId;

    @Value("${jsa.aws.secret_access_key}")
    private String awsKey;

    @Value("${jsa.s3.region}")
    private String region;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    private static int oneDay = 1000 * 60 * 60 * 24;

    @Bean
    public AmazonS3 s3client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsId, awsKey);

        return  AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    public TransferManager transferManager(){
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3client())
                .withDisableParallelDownloads(false)
                .withMinimumUploadPartSize(5L * MB)
                .withMultipartUploadThreshold(16L * MB)
                .withMultipartCopyPartSize(5L * MB)
                .withMultipartCopyThreshold(100L * MB)
                .withExecutorFactory(() -> createExecutorService(20))
                .build();

        Date oneDayAgo = new Date(System.currentTimeMillis() - this.oneDay);
        try{
            tm.abortMultipartUploads(bucketName, oneDayAgo);
        } catch (S3Exception e){
            e.printStackTrace();
        }

        return tm;
    }

    private ThreadPoolExecutor createExecutorService(int threadNumber){
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("jsa-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
    }
}
