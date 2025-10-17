package org.xiaowu.wpywebframework.object.configuration;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaowu.wpywebframework.object.properties.MinIOProperties;
import org.xiaowu.wpywebframework.object.service.MinIOServiceImpl;
import org.xiaowu.wpywebframework.object.service.ObjectStorageService;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(MinIOProperties.class)
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MinIOAutoConfiguration {

    private final MinIOProperties minIOProperties;

    public MinIOAutoConfiguration(MinIOProperties minIOProperties) {
        this.minIOProperties = minIOProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() {
        // 创建OkHttpClient，设置超时时间
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(minIOProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(minIOProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(minIOProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();

        return MinioClient.builder()
                .endpoint(minIOProperties.getEndpoint())
                .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                .httpClient(httpClient)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageService minIOService(MinioClient minioClient) {
        return new MinIOServiceImpl(minioClient, minIOProperties);
    }
}