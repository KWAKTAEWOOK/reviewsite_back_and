package com.project.reviewSite_backend.user.service;

import com.project.reviewSite_backend.exception.PasswordNotMatchException;
import com.project.reviewSite_backend.exception.UserNotFoundException;
import com.project.reviewSite_backend.user.CreateForm;
import com.project.reviewSite_backend.user.UserRole;
import com.project.reviewSite_backend.user.dao.UserRepository;
import com.project.reviewSite_backend.user.domain.User;
import com.project.reviewSite_backend.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void joinUser(CreateForm createForm) {
        User user = User.builder()
                .username(createForm.getUsername())
                .nickname(createForm.getNickname())
                .userid(createForm.getUserid())
                .password(passwordEncoder.encode(createForm.getPassword1()))
                .email(createForm.getEmail())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public User login(User user) {
        Optional<User> opUser = userRepository.findByUserid(user.getUserid());

        if (opUser.isPresent()) {
            User loginedUser = opUser.get();
            if (passwordEncoder.matches(user.getPassword(), loginedUser.getPassword())) {
                return loginedUser;
            }
            throw new PasswordNotMatchException(String.format("password do not match"));
        }
        throw new UserNotFoundException(String.format("%s not found", user.getUserid()));
    }

    public User deleteById(Long id){
        userRepository.deleteById(id);

        return deleteById(id);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    UserDto userDto = new UserDto();
                    userDto.setEmail(user.getEmail());
                    userDto.setNickname(user.getNickname());
                    userDto.setUserid(user.getUserid());
                    userDto.setId(user.getId());
                    return userDto;
                })
                .toList();

        return userDtos;
    }

    public boolean checkUseridDuplicate(String userid) {
        return userRepository.existsByUserid(userid);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public User modifynickname(User user) {

        User b;

        if (userRepository.save(user) == null) {
            b = null;
        } else {
            b = user;
        }

        return b;
    }
}