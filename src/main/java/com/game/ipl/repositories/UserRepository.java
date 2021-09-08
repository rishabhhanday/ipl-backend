package com.game.ipl.repositories;

import com.game.ipl.entity.UserInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface UserRepository extends CrudRepository<UserInfo, String> {
    @Override
    UserInfo save(UserInfo userInfo);
}
