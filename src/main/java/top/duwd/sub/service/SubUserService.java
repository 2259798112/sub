package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.duwd.common.domain.sub.entity.SubUser;

@Service
@Slf4j
public class SubUserService {

    public SubUser getUserByToken(String token) {
        SubUser subUser = new SubUser();
        subUser.setUserId("dwd");
        return subUser;
    }
}
