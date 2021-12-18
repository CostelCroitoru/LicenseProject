package com.costel.license.LicenseProject.security;

import com.costel.license.LicenseProject.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import tables.User;

import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                    .antMatchers("/", "/register", "/loginn", "/isClickBait/**", "/setCategory/**").permitAll()
                    .antMatchers("/css/loginStyle.css", "/img/**", "js/**").permitAll()
                    .antMatchers("/h2-console").hasAnyAuthority("ADMIN")
                    .antMatchers("/news", "/insertNews").hasAnyAuthority("ADMIN", "USER")
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();

        httpSecurity.csrf().disable();                      //permisuni pentru a avea acces la continutul
        httpSecurity.headers().frameOptions().disable();    //bazei de date din browser
    }


    @Autowired
    UserMapper userMapper;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("In functia configureGlobal!\n");
        auth
                .inMemoryAuthentication()
                .withUser("costel").password("costel1234").roles("ADMIN");



        ArrayList<User> userList;
        try{
            userList = (ArrayList<User>) userMapper.getAllUsers();


            if(userList != null)
            {
                for(int i = 0; i < userList.size(); ++i)
                {
                    auth.inMemoryAuthentication()
                            .withUser(userList.get(i).getUsername())
                            .password(userList.get(i).getPassword())
                            .roles(userList.get(i).getRole());
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
