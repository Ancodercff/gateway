package com.blueocn.platform.gateway.kong.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blueocn.platform.gateway.kong.connector.ApiConnector;
import com.blueocn.platform.gateway.kong.model.Api;
import com.blueocn.platform.gateway.web.rest.util.Asserts;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Title: ApiClient
 * Description: 调用 Kong API 请求接口
 *
 * @author Yufan
 * @version 1.0.0
 * @since 2016-02-03 18:35
 */
@Component
public class ApiClient extends BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);

    private ApiConnector apiConnector;

    @PostConstruct
    public void init() { // NOSONAR
        apiConnector = retrofit.create(ApiConnector.class);
    }

    /**
     * 增一个
     *
     * @throws IOException
     */
    public Api add(Api api) throws IOException {
        Call<Api> call = apiConnector.add(api);
        Response<Api> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Api result = new Api();
        result.setErrorMessage(response.errorBody().string());
        LOGGER.debug("API 保存失败 - {}", result.getErrorMessage());
        return result;
    }

    /**
     * 查一堆
     *
     * @throws IOException
     */
    public List<Api> query(Api api) throws IOException {
        Call<ResponseBody> call = apiConnector.query(api == null ? Maps.newHashMap() : api.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            JSONArray array = object.getJSONArray("data");
            return JSON.parseArray(array.toJSONString(), Api.class);
        }
        return Lists.newArrayList();
    }

    /**
     * 当前查询条件下的 API 个数
     *
     * @param api 查询参数
     * @return API 总数, 对于总数大于100的情况有效
     */
    public Integer totalSize(Api api) throws IOException {
        Call<ResponseBody> call = apiConnector.query(api == null ? Maps.newHashMap() : api.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            return object.getInteger("total");
        }
        return 0;
    }

    /**
     * 查一个
     *
     * @param apiId 其实 API Name 和 API ID 均可以使用, 为了防止混淆, 这里约定只用 API ID.
     * @throws IOException
     */
    public Api queryOne(String apiId) throws IOException {
        Call<Api> call = apiConnector.queryOne(apiId);
        Response<Api> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Api result = new Api();
        result.setErrorMessage(response.errorBody().string());
        return result;
    }

    /**
     * 更新一个
     *
     * @throws IOException
     */
    public Api update(Api api) throws IOException {
        Preconditions.checkNotNull(api, "API 信息不能为空");
        Asserts.checkNotBlank(api.getId(), "API ID 不能为空");
        Call<Api> call = apiConnector.update(api.getId(), api);
        Response<Api> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Api result = new Api();
        result.setErrorMessage(response.errorBody().string());
        return result;
    }

    /**
     * 删一个
     *
     * @param apiId 其实 API Name 和 API ID 均可以使用, 为了防止混淆, 这里约定只用 API ID.
     * @throws IOException
     */
    public void delete(String apiId) throws IOException {
        Asserts.checkNotBlank(apiId, "待删除的 API ID 不能为空");
        Call<String> call = apiConnector.delete(apiId);
        call.execute();
    }
}
