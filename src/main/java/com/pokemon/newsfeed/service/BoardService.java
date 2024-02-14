package com.pokemon.newsfeed.service;

import com.pokemon.newsfeed.dto.requestDto.BoardRequestDto;
import com.pokemon.newsfeed.dto.requestDto.BoardUpdateDto;
import com.pokemon.newsfeed.dto.responseDto.BoardResponseDto;
import com.pokemon.newsfeed.entity.Board;
import com.pokemon.newsfeed.entity.User;
import com.pokemon.newsfeed.repository.BoardRepository;
import com.pokemon.newsfeed.repository.UserRepository;
import com.pokemon.newsfeed.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public Board createBoard(BoardRequestDto requestDto, User user) {
        String title = requestDto.getTitle();
        String contents = requestDto.getContents();
        Board board = new Board(title, contents, user);
        boardRepository.save(board);

        return board;
    }

    public List<Board> getAllBoards() {
        // 저장소에서 모든 게시물을 찾습니다.
        return boardRepository.findAll();
    }

    public Board getBoardById(Long boardNum) {
        Board board = boardRepository.findById(boardNum)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 게시물을 찾을 수 없습니다: " + boardNum));

        return boardRepository.findById(boardNum).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));

    }

    // 자신 게시물 전체 조회(마이페이지)
    public List<BoardResponseDto> getUserAllBoards(UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하는 회원이 없습니다."));
        return boardRepository.findAllByUser(user)
                .stream().map(BoardResponseDto::new).toList();
    }

    @Transactional
    public Board updateBoard(Long boardNum, BoardUpdateDto requestDto, User user) {
        Board board = findOne(boardNum);
        if (!board.getUser().equals(user)) {
            throw new IllegalArgumentException("작성자만 수정 할 수 있습니다.");
        }
        board.updateBoard(requestDto);
        return board;
    }

    public void deleteBoard(Long boardNum, User user) {
        Board board = findOne(boardNum);

        if (!board.getUser().equals(user)) {
            throw new IllegalArgumentException("작성자만 삭제 할 수 있습니다.");
        }
        boardRepository.delete(board);
    }


    private Board findOne(Long boardNum) {
        return boardRepository.findById(boardNum).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));
    }


}
