package com.pokemon.newsfeed.controller;

import com.pokemon.newsfeed.dto.requestDto.LoginRequestDto;
import com.pokemon.newsfeed.dto.requestDto.SignupRequestDto;
import com.pokemon.newsfeed.dto.requestDto.UserUpdateDto;
import com.pokemon.newsfeed.dto.responseDto.BoardResponseDto;
import com.pokemon.newsfeed.dto.responseDto.LoginResponseDto;
import com.pokemon.newsfeed.security.UserDetailsImpl;
import com.pokemon.newsfeed.dto.responseDto.ProfileResponseDto;
import com.pokemon.newsfeed.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup (@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        // todo: RequestBody 어노테이션 없으면 null이 들어오는 이유
        System.out.println(requestDto);
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "회원가입도중 에러가 발생했습니다.";
        }

        System.out.println(requestDto);
        userService.signup(requestDto);

        return "회원가입 성공";
    }

    @PostMapping("/login")
    public LoginResponseDto login (@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return responseDto;
    }

    // 회원 관련 정보 받기 + 강의에서 봤던 이름님 환영합니다 부분 연결 api
    @GetMapping("/info")
    @ResponseBody
    public String getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userId = userDetails.getUser().getUserId();
        String password = userDetails.getPassword();
        System.out.println(userId + ", " + password);

        return "로그인 성공";
    }

    // 프로필 단건조회
    @GetMapping
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProfileResponseDto response = userService.getProfile(userDetails.getUser().getUserNum());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 프로필 수정
    @PutMapping
    public ResponseEntity<ProfileResponseDto> updateProfile(@RequestBody UserUpdateDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProfileResponseDto response = userService.updateProfile(userDetails.getUser().getUserNum(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //  자신이 작성한 모든 게시물 조회 요청 처리
    @GetMapping("/userboard")
    public List<BoardResponseDto> getUserAllBoards(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return userService.getUserAllBoards(userDetails);
    }

    // 자신이 선택한 게시물 조회 요청 처리
    @GetMapping("/boards/{boardnum}")
    public BoardResponseDto getUserSelectedBoards(
            @PathVariable Long boardnum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return userService.getUserSelectedBoards(boardnum, userDetails);

    }

}
