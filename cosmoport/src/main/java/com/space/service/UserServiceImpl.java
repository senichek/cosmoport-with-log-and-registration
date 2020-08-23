package com.space.service;

import com.space.repository.ShipRepository;
import com.space.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.logging.Logger;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private UserRepository userRepository;

    public UserServiceImpl() {
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        //super();
        this.userRepository = userRepository;
    }

}
