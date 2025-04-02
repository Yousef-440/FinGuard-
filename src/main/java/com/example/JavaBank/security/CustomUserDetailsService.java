package com.example.JavaBank.security;

import com.example.JavaBank.model.Role;
import com.example.JavaBank.model.User;
import com.example.JavaBank.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Sorry, Not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail() , user.getPassword() , mapRolesToAuthorities(user.getRoles()));

    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream()
                .map(role ->new SimpleGrantedAuthority(role.getNameRole()))
                .collect(Collectors.toList());
    }
}
