package com.project.reviewSite_backend.user.service;

import com.project.reviewSite_backend.user.UserRole;
import com.project.reviewSite_backend.user.dao.UserRepository;
import com.project.reviewSite_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    private final EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        Optional<User> _user = userRepository.findByUserid(userid);
        System.out.println("from repository userSecurityService : " + _user);

        em.persist(_user.get());

        List<GrantedAuthority> authorities = new ArrayList<>();
        if("admin".equals(userid)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }

        return (UserDetails) _user.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
    }

}
