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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Board createBoard(BoardRequestDto requestDto, User user) {
        String title = requestDto.getTitle();
        String contents = requestDto.getContents();
        Board board = new Board(title, contents, user);
        boardRepository.save(board);

        return board;
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
