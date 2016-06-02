package com.blueocn.platform.gateway.cucumber.stepdefs;

import com.blueocn.platform.gateway.kong.client.ApiClient;
import com.blueocn.platform.gateway.kong.model.Api;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

/**
 * Title: KongStepDefs
 * Create Date: 2016-06-02 15:00
 * Description:
 *
 * @author Yufan
 */
public class KongStepDefs extends StepDefs {

    @Inject
    private ApiClient apiClient;

    private Api api;

    @Before
    public void setup() throws IOException {
        // purge all existing apis on kong
        List<Api> apis = apiClient.query(null);
        for (Api api : apis) {
            apiClient.delete(api.getId());
        }
    }

    @Given("^a api information like this$")
    public void have_a_api_like_these(DataTable dataTable) throws IOException {
        List<Api> apis = dataTable.asList(Api.class);
        Assert.assertThat(apis.size(), is(1));
        api = apis.get(0);
    }

    @When("^I add the given api to kong$")
    public void add_the_api_to_kong() throws IOException {
        Assert.assertEquals(api.getId(), null);
        api = apiClient.add(api);
        Assert.assertNotNull(api.getId());
    }

    @Then("I can query the newly added api from kong$")
    public void query_the_added_api() throws IOException {
        Api queryApi = apiClient.queryOne(api.getId());
        Assert.assertEquals(queryApi.getName(), api.getName());
    }

    @Given("^a bunch of apis and register them on kong$")
    public void register_apis_on_kong(DataTable dataTable) throws Throwable {
        List<Api> apis = dataTable.asList(Api.class);
        Assert.assertNotNull(apis);
        Assert.assertThat(apis.size(), not(0));
        for (Api api : apis) {
            apiClient.add(api);
        }
    }

    @When("^I query the api like (.*?) and update api name to (.*?)$")
    public void update_api_name(String name, String newName) throws Throwable {
        List<Api> queryApis = apiClient.query(Api.builder().name(name).build());
        Assert.assertNotNull(queryApis);
        Assert.assertThat(queryApis.size(), is(1));

        Api newApi = queryApis.get(0);
        newApi.setName(newName);
        apiClient.update(newApi);
    }

    @Then("^I can get the api by using the name (.*?)$")
    public void get_the_newly_updated_api(String newName) throws Throwable {
        List<Api> apis = apiClient.query(Api.builder().name(newName).build());
        Assert.assertNotNull(apis);
        Assert.assertThat(apis.size(), is(1));
    }
}
