package com.blueocn.platform.gateway.kong.client;

import retrofit2.Retrofit;

import javax.inject.Inject;

/**
 * Title: BaseConnector
 * Create Date: 2016-06-01 12:42
 * Description:
 *
 * @author Yufan
 */
public abstract class BaseClient {

    @Inject
    protected Retrofit retrofit;
}
