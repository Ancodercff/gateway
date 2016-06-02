package com.blueocn.platform.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Title: GatewayProperties
 * Create Date: 2016-06-01 16:23
 * Description: Properties specific to Gateway
 *
 * @author Yufan
 */
@ConfigurationProperties(prefix = "gateway", ignoreUnknownFields = false)
public class GatewayProperties {

    @Getter
    private final Kong kong = new Kong();

    public static class Kong {

        @Getter
        private final Admin admin = new Admin();

        @Getter
        private final Invoke invoke = new Invoke();

        public static class Admin {

            @Getter
            @Setter
            private String host;

            @Getter
            @Setter
            private Integer port;
        }

        public static class Invoke {

            @Setter
            @Getter
            private String timeout;
        }
    }
}
