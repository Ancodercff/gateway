package com.blueocn.platform.gateway.kong.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blueocn.platform.gateway.kong.connector.ConsumerConnector;
import com.blueocn.platform.gateway.kong.model.Consumer;
import com.blueocn.platform.gateway.web.rest.util.Asserts;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
 * Title: CustomerClient
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 2016-02-22 11:19
 */
@Component
public class ConsumerClient extends BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerClient.class);

    private ConsumerConnector consumerConnector;

    @PostConstruct
    private void init() { // NOSONAR
        consumerConnector = retrofit.create(ConsumerConnector.class);
    }

    /**
     * 增
     *
     * @throws IOException
     */
    public Consumer add(Consumer consumer) throws IOException {
        Call<Consumer> call = consumerConnector.add(consumer);
        Response<Consumer> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Consumer result = new Consumer();
        result.setErrorMessage(response.errorBody().string());
        LOGGER.warn("保存失败 {}", result.getErrorMessage());
        return result;
    }

    /**
     * 查一堆
     *
     * @throws IOException
     */
    public List<Consumer> query(Consumer consumer) throws IOException {
        Call<ResponseBody> call = consumerConnector.query(consumer == null ? null : consumer.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            JSONArray array = object.getJSONArray("data");
            return JSON.parseArray(array.toJSONString(), Consumer.class);
        }
        return Lists.newArrayList();
    }

    /**
     * 当前查询条件下的 API 个数
     *
     * @param consumer 查询参数
     * @return API 总数, 对于总数大于100的情况有效
     */
    public Integer totalSize(Consumer consumer) throws IOException {
        Call<ResponseBody> call = consumerConnector.query(consumer == null ? null : consumer.toMap());
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
     * @param consumerId 用户ID, 其实还可以使用用户名称, 这里废弃用户名称方式
     * @throws IOException
     */
    public Consumer queryOne(String consumerId) throws IOException {
        Call<Consumer> call = consumerConnector.queryOne(consumerId);
        Response<Consumer> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Consumer result = new Consumer();
        result.setErrorMessage(response.errorBody().string());
        return result;
    }

    /**
     * 更新一个
     *
     * @throws IOException
     */
    public Consumer update(Consumer consumer) throws IOException {
        Preconditions.checkNotNull(consumer, "用户信息");
        Call<Consumer> call = consumerConnector.update(consumer.getId(), consumer);
        Response<Consumer> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        Consumer result = new Consumer();
        result.setErrorMessage(response.errorBody().string());
        LOGGER.warn("保存失败 {}", result.getErrorMessage());
        return result;
    }

    /**
     * 删除一个
     *
     * @param consumerId 用户ID, 不建议使用用户名
     * @throws IOException
     */
    public void delete(String consumerId) throws IOException {
        Asserts.checkNotBlank(consumerId, "需要删除的用户 ID");
        Call<String> call = consumerConnector.delete(consumerId);
        call.execute();
    }
}
