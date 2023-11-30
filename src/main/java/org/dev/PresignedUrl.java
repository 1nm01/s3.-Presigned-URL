package org.dev;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;


import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

public class PresignedUrl {
    public static String getPresignedUrl(){
        BasicAWSCredentials awsCred = new BasicAWSCredentials(AccessKeys.ACCESS_KEYS, AccessKeys.SECRET_ACCESS_KEY);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).withCredentials(new AWSStaticCredentialsProvider(awsCred)).build();
        Date expiration = new Date();
        expiration.setTime(expiration.getTime()+ 120000);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(AccessKeys.BUCKET_NAME, AccessKeys.OBJECT_KEY).withExpiration(expiration);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    public static String getPresignedUrlMethod2(){
        S3Presigner presigner = S3Presigner.create();

        // Create a GetObjectRequest to be pre-signed
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(AccessKeys.BUCKET_NAME)
                        .key(AccessKeys.OBJECT_KEY)
                        .build();

        // Create a GetObjectPresignRequest to specify the signature duration
        GetObjectPresignRequest getObjectPresignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest);

        // Log the presigned URL, for example.
        String presignedurl = "Presigned URL: " + presignedGetObjectRequest.url();

        // It is recommended to close the S3Presigner when it is done being used, because some credential
        // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
        // that need to be freed. If you are using one S3Presigner per application (as recommended), this
        // usually is not needed.
        presigner.close();
        return presignedurl;
    }

    public static String putPresignedUrl(){
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        // Set the presigned URL to expire after one hour.
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AccessKeys.BUCKET_NAME, AccessKeys.OBJECT_KEY)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return "Pre-Signed URL: " + url.toString();

    }

    public static String putPresignedUrlMethod2(){
        S3Presigner presigner = S3Presigner.create();

        // Create a GetObjectRequest to be pre-signed
        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(AccessKeys.BUCKET_NAME)
                        .key(AccessKeys.OBJECT_KEY)
                        .contentType("image/jpeg")
                        .build();

        // Create a GetObjectPresignRequest to specify the signature duration
        PutObjectPresignRequest putObjectPresignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                        .build();

        // Generate the presigned request
        PresignedPutObjectRequest presignedPutObjectRequest =
                presigner.presignPutObject(putObjectPresignRequest);

        // Log the presigned URL, for example.
        String presignedurl = "Presigned URL: " + presignedPutObjectRequest.url();

        // It is recommended to close the S3Presigner when it is done being used, because some credential
        // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
        // that need to be freed. If you are using one S3Presigner per application (as recommended), this
        // usually is not needed.
        presigner.close();
        return presignedurl;
    }

}
