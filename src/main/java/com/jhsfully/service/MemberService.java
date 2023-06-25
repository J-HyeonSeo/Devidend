package com.jhsfully.service;

import com.jhsfully.exception.impl.AlreadyExistsUserException;
import com.jhsfully.model.Auth;
import com.jhsfully.persist.entity.MemberEntity;
import com.jhsfully.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("cannot find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member){
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if(exists){
            throw new AlreadyExistsUserException();
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member){
        MemberEntity user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("doesn't exists username"));

        if(!passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new RuntimeException("password is not correct!");
        }

        return user;
    }

}
