package com.pokemon.newsfeed.service;

import com.pokemon.newsfeed.dto.requestDto.LoginRequestDto;
import com.pokemon.newsfeed.dto.requestDto.SignupRequestDto;
import com.pokemon.newsfeed.dto.requestDto.UserUpdateDto;
import com.pokemon.newsfeed.dto.responseDto.BoardResponseDto;
import com.pokemon.newsfeed.dto.responseDto.LoginResponseDto;
import com.pokemon.newsfeed.dto.responseDto.ProfileResponseDto;
import com.pokemon.newsfeed.entity.Board;
import com.pokemon.newsfeed.entity.User;
import com.pokemon.newsfeed.entity.UserRoleEnum;
import com.pokemon.newsfeed.repository.BoardRepository;
import com.pokemon.newsfeed.repository.UserRepository;
import com.pokemon.newsfeed.security.UserDetailsImpl;
import com.pokemon.newsfeed.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto requestDto) {
        Optional<User> user = userRepository.findByUserId(requestDto.getUserId());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        String token = jwtUtil.createToken(user.get().getUserId(), UserRoleEnum.USER);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .userId(requestDto.getUserId())
                .password(requestDto.getPassword())
                .token(token)
                .build();

        return responseDto;
    }

    public void signup(SignupRequestDto requestDto) {
        System.out.println(requestDto);
        // 회원 중복 체크
        String userId = requestDto.getUserId();
        Optional<User> checkUserId = userRepository.findByUserId(userId);
        if (checkUserId.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
        }

        String password = passwordEncoder.encode(requestDto.getPassword());
        // 받아온 비밀번호 암호화

        // email 중복 체크
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }

        String name = requestDto.getName();
        UserRoleEnum role = UserRoleEnum.USER;

        // 사용자 등록
        User user = new User(userId, password, email, name, role);
        userRepository.save(user);
    }

    // 프로필 조회
    public ProfileResponseDto getProfile(Long userNum) {
        User user = userRepository.findById(userNum).orElseThrow(() -> new IllegalArgumentException("해당 아이디는 존재하지 않습니다."));
        return new ProfileResponseDto(user.getName(), user.getUserId(), user.getEmail());
    }


    // 프로필 수정
    @Transactional
    public ProfileResponseDto updateProfile(Long userNum, UserUpdateDto request) {
        User user = userRepository.findById(userNum).orElseThrow(() -> new IllegalArgumentException(" 계정 정보가 일치하지 않습니다."));
        // 비밀번호 확인
        // todo: RuntimeException인지 다른 Exception인지.. 같이 고민해보기
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("패스워드가 일치하지 않습니다.");
        }
        user.updateProfile(request.getName(), request.getUserId(), request.getEmail());
        return new ProfileResponseDto(user.getName(), user.getUserId(), user.getEmail());
    }

    // 자신 게시물 전체 조회
    public List<BoardResponseDto> getUserAllBoards(UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하는 회원이 없습니다."));
        return boardRepository.findAllByUser(user)
                .stream().map(BoardResponseDto::new).toList();
    }

    // 자신 게시물 선택 조회
    public BoardResponseDto getUserSelectedBoards(Long boardnum, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(boardnum)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 게시물이 없습니다."));
        if (!Objects.equals(userDetails.getUser().getUserNum(), board.getUser().getUserNum())) {
            throw new IllegalArgumentException("로그인 정보와 다른 게시물을 선택하였습니다.");
        }
        return new BoardResponseDto(board);
    }
}
