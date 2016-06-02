package com.blueocn.platform.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Title: RetrofitConfiguration
 * Create Date: 2016-05-31 15:44
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RetrofitConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitConfiguration.class);

    @Inject
    private Jackson2ObjectMapperBuilder builder;

    private String getKongAdminAddress(GatewayProperties.Kong.Admin kongAdmin) {
        String host = kongAdmin.getHost();
        if (isNotBlank(host)) {
            // Kong 管理页面默认无 HTTPS, 实际使用时位于内网, 对外不公开, 所以使用 HTTP.
            return host.startsWith("http") ? host : "http://" + host;
        }
        LOGGER.error("Kong 管理地址未配置, 使用缺省调用地址。");
        return "http://127.0.0.1"; // 缺省配置 NOSONAR
    }

    private Integer getKongAdminPort(GatewayProperties.Kong.Admin kongAdmin) {
        Integer port = kongAdmin.getPort();
        if (port == null) {
            LOGGER.error("Kong 的管理端口未配置, 使用缺省端口 [8001]");
            return 8001;
        }
        return port;
    }

    /**
     * 获取 Kong 的 API 管理地址
     * 不带 "/" 结尾, Retrofit2 调用问题, 相对路径.
     *
     * @param kong
     */
    private String getKongAdminUrl(GatewayProperties.Kong kong) {
        return getKongAdminAddress(kong.getAdmin()) + ":" + getKongAdminPort(kong.getAdmin());
    }

    @Bean
    public Retrofit retrofit(GatewayProperties gatewayProperties) {
        return new Retrofit.Builder()
            .baseUrl(getKongAdminUrl(gatewayProperties.getKong()))
            .addConverterFactory(JacksonConverterFactory.create(builder.build()))
            .build();
    }
}
