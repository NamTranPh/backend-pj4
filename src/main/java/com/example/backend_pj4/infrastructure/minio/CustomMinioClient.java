package com.example.backend_pj4.infrastructure.minio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Multimap;

import io.minio.AbortMultipartUploadResponse;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

public class CustomMinioClient extends MinioAsyncClient {

    public CustomMinioClient(MinioAsyncClient client) {
        super(client);
    }

    public CompletableFuture<CreateMultipartUploadResponse> initMultipartUpload(
            String bucket, String region, String object, Multimap<String, String> headers,
            Multimap<String, String> extraQueryParams
    ) throws InsufficientDataException, InternalException, InvalidKeyException,
            IOException, NoSuchAlgorithmException, XmlParserException {
        return super.createMultipartUploadAsync(bucket, region, object, headers, extraQueryParams);
    }

    public CompletableFuture<ObjectWriteResponse> mergeMultipartUpload(
            String bucket, String region, String object, String uploadId,
            Part[] parts, Multimap<String, String> extraHeaders,
            Multimap<String, String> extraQueryParams
    ) throws InsufficientDataException, InternalException, InvalidKeyException,
            IOException, NoSuchAlgorithmException, XmlParserException {
        return super.completeMultipartUploadAsync(bucket, region, object, uploadId, parts, extraHeaders, extraQueryParams);
    }

    public CompletableFuture<AbortMultipartUploadResponse> cancelMultipartUpload(
            String bucket, String region, String object, String uploadId,
            Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams
    ) throws InsufficientDataException, InternalException, InvalidKeyException,
            IOException, NoSuchAlgorithmException, XmlParserException {
        return super.abortMultipartUploadAsync(bucket, region, object, uploadId, extraHeaders, extraQueryParams);
    }

    public CompletableFuture<ListPartsResponse> listMultipartParts(
            String bucket, String region, String object, Integer maxParts,
            Integer partNumberMarker, String uploadId,
            Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams
    ) throws InsufficientDataException, InternalException, InvalidKeyException,
            IOException, NoSuchAlgorithmException, XmlParserException {
        return super.listPartsAsync(bucket, region, object, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }
}
