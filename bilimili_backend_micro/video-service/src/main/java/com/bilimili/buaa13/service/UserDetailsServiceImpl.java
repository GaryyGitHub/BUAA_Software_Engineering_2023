package com.bilimili.buaa13.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilimili.buaa13.entity.User;
import com.bilimili.buaa13.service.client.UserServiceClient;
import com.bilimili.buaa13.service.impl.user.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserServiceClient userServiceClient;
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        User user = userServiceClient.getUserByName(account);
        if (user == null) {
            return null;
        }

        return new UserDetailsImpl(user);
    }
}
