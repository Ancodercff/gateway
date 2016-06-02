package com.blueocn.platform.gateway.kong.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.blueocn.platform.gateway.kong.connector.PluginConnector;
import com.blueocn.platform.gateway.kong.model.EnabledPlugins;
import com.blueocn.platform.gateway.kong.model.Plugin;
import com.blueocn.platform.gateway.web.rest.util.Asserts;
import com.google.common.collect.Lists;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: PluginClient
 * Description: 通用插件调用接口类
 *
 * @author Yufan
 * @version 1.0.0
 * @since 2016-02-29 17:31
 */
public class PluginClient extends BaseClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClient.class);

    private PluginConnector pluginConnector;

    @PostConstruct
    public void init() { // NOSONAR
        pluginConnector = retrofit.create(PluginConnector.class);
    }

    /**
     * 从插件 ID 获取它对应的 API ID
     *
     * @param pluginId 插件 ID
     * @return API ID
     * @throws IOException
     */
    private String getApiIdByPluginId(String pluginId) throws IOException {
        Plugin existPlugin = queryOne(pluginId);
        return existPlugin.getApi_id();
    }

    /**
     * 查询某个名称的插件是否在 Kong 上启用
     *
     * @param pluginName 插件名称
     */
    private Boolean isPluginEnabled(String pluginName) {
        try {
            List<String> pluginList = enabledPlugins();
            return pluginList.contains(pluginName);
        } catch (IOException e) {
            LOGGER.info("", e);
        }
        return false;
    }

    /**
     * 新增一个插件配置
     *
     * @param apiId  API ID
     * @param plugin 插件信息
     * @return 成功创建的插件对象
     * @throws IOException
     */
    public Plugin add(String apiId, Plugin plugin) throws IOException {
        if (!isPluginEnabled(plugin.getName())) {
            throw new IllegalArgumentException(String.format("插件 %s 未启用, 请联系管理员或稍后重试.", plugin.getName()));
        }
        Call<ResponseBody> call = pluginConnector.add(apiId, plugin);
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            return JSON.parseObject(response.body().string(), Plugin.class);
        }
        Plugin plugins = new Plugin();
        plugins.setErrorMessage(response.errorBody().string());
        LOGGER.warn(plugins.getErrorMessage());
        return plugins;
    }

    /**
     * 查询某个配置信息
     *
     * @param pluginId 配置信息 ID
     * @throws IOException
     */
    public Plugin queryOne(String pluginId) throws IOException {
        Call<ResponseBody> call = pluginConnector.queryOne(pluginId);
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            return JSON.parseObject(response.body().string(), Plugin.class);
        }
        Plugin plugins = new Plugin();
        plugins.setErrorMessage(response.errorBody().string());
        LOGGER.warn(plugins.getErrorMessage());
        return plugins;
    }

    /**
     * 查询配置信息列表
     *
     * @param plugin 查询参数
     * @throws IOException
     */
    public List<Plugin> query(Plugin plugin) throws IOException {
        Call<ResponseBody> call = pluginConnector.query(plugin.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            JSONArray array = object.getJSONArray("data");
            return JSON.parseArray(array.toJSONString(), Plugin.class);
        }
        LOGGER.warn(response.errorBody().string());
        return Lists.newArrayList();
    }

    /**
     * 查询某个具体API对应的插件配置
     *
     * @param apiId  API ID
     * @param plugin 插件查询参数
     * @return 配置列表
     */
    public List<Plugin> querySpecificApi(String apiId, Plugin plugin) throws IOException {
        Call<ResponseBody> call = pluginConnector.querySpecificApi(apiId, plugin == null ? null : plugin.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            return JSON.parseObject(response.body().string(), new TypeReference<List<Plugin>>() {
            });
        }
        LOGGER.warn(response.errorBody().string());
        return Lists.newArrayList();
    }

    /**
     * 查询某个具体的 API 对应的某个插件信息, 由最终的接口实现类定义插件是什么
     *
     * @param apiId API ID
     */
    public Plugin querySpecificApiAndPlugin(String apiId, String pluginName) throws IOException {
        Plugin plugin = new Plugin();
        plugin.setName(pluginName);
        Call<ResponseBody> call = pluginConnector.querySpecificApi(apiId, plugin.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            List<Plugin> plugins = JSON.parseArray(object.getString("data"), Plugin.class); // 直接硬编码 ...
            if (!CollectionUtils.isEmpty(plugins)) {
                return plugins.get(0); // 算坑么 ?
            }
        } else {
            LOGGER.warn(response.errorBody().string());
        }
        return null;
    }

    /**
     * 更新插件信息
     *
     * @param pluginId 插件ID
     * @param plugin   待更新的插件信息
     * @return 更新后的插件
     */
    public Plugin update(String pluginId, Plugin plugin) throws IOException {
        String apiId = plugin.getApi_id();
        if (isBlank(apiId)) {
            apiId = getApiIdByPluginId(pluginId);
        }
        Call<ResponseBody> call = pluginConnector.update(apiId, pluginId, plugin);
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            return JSON.parseObject(response.body().string(), Plugin.class);
        }
        Plugin plugins = new Plugin();
        plugins.setErrorMessage(response.errorBody().string());
        LOGGER.warn(plugins.getErrorMessage());
        return plugins;
    }

    /**
     * 删除插件信息
     *
     * @param pluginId 插件ID
     */
    public void delete(String pluginId) throws IOException {
        delete(null, pluginId);
    }

    /**
     * 删除插件信息
     *
     * @param apiId    插件对应的API ID
     * @param pluginId 插件ID
     */
    public void delete(final String apiId, String pluginId) throws IOException {
        Asserts.checkNotBlank(pluginId, "插件 ID 不能为空");
        pluginConnector.delete(isBlank(apiId) ? getApiIdByPluginId(pluginId) : apiId, pluginId).execute();
    }

    /**
     * 查询所有启用的 API
     *
     * @return API的名称集合, @NotNull
     */
    public List<String> enabledPlugins() throws IOException {
        Call<EnabledPlugins> call = pluginConnector.queryEnabled();
        Response<EnabledPlugins> response = call.execute();
        if (response.isSuccessful()) {
            return response.body().getEnabled_plugins();
        }
        LOGGER.warn(response.errorBody().string());
        return Lists.newArrayList();
    }

    /**
     * 查询按照当前查询参数的插件个数
     *
     * @param plugin 查询参数
     * @return 个数
     */
    public Integer totalSize(Plugin plugin) throws IOException {
        Call<ResponseBody> call = pluginConnector.query(plugin == null ? null : plugin.toMap());
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            JSONObject object = JSON.parseObject(response.body().string());
            return object.getInteger("total");
        }
        return 0;
    }
}
