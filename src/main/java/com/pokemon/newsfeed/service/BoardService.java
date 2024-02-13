package com.pokemon.newsfeed.service;

import com.pokemon.newsfeed.dto.requestDto.BoardDeleteDto;
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

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private Board board;

    //    userRepository.findById(userId)에서 발생한 IllegalArgumentException이 원인(?)
    //    해당 부분을 확인하고 유저 ID가 유효한지, 즉 null이 아닌지 확인

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
        // ID로 게시물을 찾습니다.
        Board board = boardRepository.findById(boardNum)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 게시물을 찾을 수 없습니다: " + boardNum));

        return boardRepository.findById(boardNum).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));

    }

    @Transactional
    public Board updateBoard(Long boardNum, BoardUpdateDto requestDto, User user) {
        Board board = findOne(boardNum);
        if (!board.getUser().equals(user)) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
        board.updateBoard(requestDto);
        return board;
    }

    public void deleteBoard(Long boardnum, User user) {
        Board board = findOne(boardnum);

        if (!board.getUser().equals(user)) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
        boardRepository.delete(board);
    }


    private Board findOne(Long boardNum) {
        return boardRepository.findById(boardNum).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));
    }
}
