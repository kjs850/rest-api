package me.jake.restapi.configs;

import me.jake.restapi.accounts.Account;
import me.jake.restapi.accounts.AccountRole;
import me.jake.restapi.accounts.AccountService;
import me.jake.restapi.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthServerConfigTest{

    @Autowired
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        //Given
        Set roleSet = new HashSet();
        roleSet.add(AccountRole.ADMIN);
        roleSet.add(AccountRole.USER);

        String username = "jake@aaa.com";
        String password = "jake";

        Account jake = Account.builder()
                .email(username)
                .password(password)
                .roles(roleSet)
                .build();

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                ;

        //{"access_token":"03105921-8a10-489b-8f70-11b6cc125e07","token_type":"bearer","refresh_token":"49c1107e-43c2-4249-a9d7-766d901ecd1d","expires_in":599,"scope":"read write"}
    }
}