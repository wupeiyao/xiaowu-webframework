package org.xiaowu.wpywebframework.object.service;

import io.minio.ObjectWriteResponse;
import org.springframework.web.multipart.MultipartFile;
import org.xiaowu.wpywebframework.object.bucket.Bucket;
import org.xiaowu.wpywebframework.object.model.ObjectMetadataResponse;
import org.xiaowu.wpywebframework.object.model.ObjectReadResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ObjectStorageService {
    String getType();

    ObjectWriteResponse putObject(MultipartFile filePart);

    ObjectWriteResponse putObject(Bucket bucket, MultipartFile filePart);

    ObjectWriteResponse putObject(String name, InputStream stream);

    ObjectWriteResponse putObject(Bucket bucket, String name, InputStream stream);

    ObjectWriteResponse putObject(Bucket bucket, String name, Map<String, String> headers, Map<String, String> metadata, byte[] bytes);

    ObjectWriteResponse putObject(Bucket bucket, String name, Map<String, String> headers, Map<String, String> metadata, InputStream stream);

    ObjectReadResponse getObject(String path);

    ObjectReadResponse getObject(String path, Long offset, Long length);

    ObjectMetadataResponse getObjectMetadata(String path);

    void delete(String path);


    String uploadFilePart(String fileId, String fileName, MultipartFile filePart, Integer chunkIndex, Integer totalChunks, String bucketName) throws IOException;
}