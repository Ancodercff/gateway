Feature: Kong admin api management

    Scenario: Register a api on kong
        Given a api information like this
            # We only need one api test case
            | name    | request_host | request_path | strip_request_path | preserve_host | upstream_url        |
            | Mockbin | mockbin.com  | /someservice | false              | false         | https://mockbin.com |
        When I add the given api to kong
        Then I can query the newly added api from kong

    Scenario Outline: Update some existing apis on kong
        Given a bunch of apis and register them on kong
            # At least one test case
            | name     | request_host | request_path | strip_request_path | preserve_host | upstream_url        |
            | Mockbin1 | mockbin1.com | /someservice | false              | false         | https://mockbin.com |
        When I query the api like <name> and update api name to <new_name>
        Then I can get the api by using the name <new_name>
        Examples:
            | name     | new_name    |
            | Mockbin1 | NewMockbin1 |
