package com.blueocn.platform.gateway.kong.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Title: EnabledPlugins
 * Create Date: 2016-06-01 18:54
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 1.0.0
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnabledPlugins implements Serializable {
    private static final long serialVersionUID = -2383652413092210923L;

    private List<String> enabled_plugins;
}
