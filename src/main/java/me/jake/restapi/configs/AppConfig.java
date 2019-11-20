package me.jake.restapi.configs;

import me.jake.restapi.accounts.Account;
import me.jake.restapi.accounts.AccountRole;
import me.jake.restapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Set roleSet = new HashSet();
                roleSet.add(AccountRole.ADMIN);
                roleSet.add(AccountRole.USER);

                Account jake = Account.builder()
                        .email("jake@aaa.com")
                        .password("jake")
                        .roles(roleSet)
                        .build();
                accountService.saveAccount(jake);
            }
        };
    }



}
