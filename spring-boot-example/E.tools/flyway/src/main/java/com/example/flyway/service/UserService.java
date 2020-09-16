package com.example.flyway.service;

import com.example.flyway.dao.UserRepository;
import com.example.flyway.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> queryAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User query(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

}
