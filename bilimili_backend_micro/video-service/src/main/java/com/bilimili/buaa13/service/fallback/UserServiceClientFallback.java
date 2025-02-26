package com.bilimili.buaa13.service.fallback;

import com.bilimili.buaa13.entity.User;
import com.bilimili.buaa13.entity.dto.UserDTO;
import com.bilimili.buaa13.service.client.UserServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public UserDTO getUserById(Integer uid) {
        return null;
    }

    @Override
    public Integer getCurrentUserId() {
        return 0;
    }

    @Override
    public Boolean currentIsAdmin() {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateFavoriteVideo(List<Map<String, Object>> result, Integer fid) {
        return null;
    }

    @Override
    public void handleComment(Integer uid, Integer toUid, Integer id) {

    }

    @Override
    public List<Integer> getVidsByFid(Integer fid) {
        return List.of();
    }

    @Override
    public List<Date> getTimeByFid(Integer fid) {
        return List.of();
    }

    @Override
    public User getUserByName(String account) {
        return null;
    }
}
