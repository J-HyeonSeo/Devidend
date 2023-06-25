package com.jhsfully.web;

import com.jhsfully.model.Auth;
import com.jhsfully.persist.entity.MemberEntity;
import com.jhsfully.security.TokenProvider;
import com.jhsfully.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request){
        log.info("user register -> " + request.getUsername());
        return ResponseEntity.ok(memberService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request){
        MemberEntity member = memberService.authenticate(request);
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(tokenProvider.generateToken(
                member.getUsername(), member.getRoles()));
    }
}
