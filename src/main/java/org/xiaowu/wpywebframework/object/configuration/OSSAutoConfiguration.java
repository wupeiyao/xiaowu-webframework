package org.xiaowu.wpywebframework.object.configuration;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaowu.wpywebframework.object.properties.OSSProperties;
import org.xiaowu.wpywebframework.object.service.OSSService;
import org.xiaowu.wpywebframework.object.service.OSSServiceImpl;

/**
 * OSS自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(OSSProperties.class)
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OSSAutoConfiguration {

    private OSS ossClient;

    @Bean
    @ConditionalOnMissingBean
    public OSS ossClient(OSSProperties ossProperties) {
        log.info("开始初始化阿里云OSS客户端...");
        log.info("OSS Endpoint: {}", ossProperties.getEndpoint());
        log.info("OSS Bucket: {}", ossProperties.getBucketName());

        // 创建ClientConfiguration实例
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setConnectionTimeout(ossProperties.getConnectionTimeout());
        config.setSocketTimeout(ossProperties.getSocketTimeout());
        config.setMaxConnections(ossProperties.getMaxConnections());
        if (ossProperties.isUseHttps()) {
            config.setProtocol(Protocol.HTTPS);
        } else {
            config.setProtocol(Protocol.HTTP);
        }
        String endpoint = ossProperties.getEndpoint();
        if (ossProperties.isUseHttps() && !endpoint.startsWith("https://")) {
            endpoint = "https://" + endpoint.replace("http://", "");
        } else if (!ossProperties.isUseHttps() && !endpoint.startsWith("http://")) {
            endpoint = "http://" + endpoint.replace("https://", "");
        }
        this.ossClient = new OSSClientBuilder().build(
                endpoint,
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                config
        );
        log.info("阿里云OSS客户端初始化成功");
        return this.ossClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public OSSService ossService(OSS ossClient, OSSProperties ossProperties) {
        log.info("创建OSSService实例...");
        return new OSSServiceImpl(ossClient, ossProperties);
    }
    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            log.info("开始关闭阿里云OSS客户端...");
            ossClient.shutdown();
            log.info("阿里云OSS客户端已关闭");
        }
    }
}
