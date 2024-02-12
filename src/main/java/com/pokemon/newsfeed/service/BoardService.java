package com.pokemon.newsfeed.service;

import com.pokemon.newsfeed.dto.requestDto.BoardDeleteDto;
import com.pokemon.newsfeed.dto.requestDto.BoardRequestDto;
import com.pokemon.newsfeed.dto.requestDto.BoardUpdateDto;
import com.pokemon.newsfeed.dto.responseDto.BoardResponseDto;
import com.pokemon.newsfeed.entity.Board;
import com.pokemon.newsfeed.entity.User;
import com.pokemon.newsfeed.repository.BoardRepository;
import com.pokemon.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private Board board;

    //    userRepository.findById(userId)에서 발생한 IllegalArgumentException이 원인으로 보입니다.
    //    해당 부분을 확인하고 유저 ID가 유효한지, 즉 null이 아닌지 확인

    public BoardResponseDto createBoard(Long userId, BoardRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Board savedBoard = boardRepository.save(board);

        return null;
//        return new BoardResponseDto(savedBoard.getBoardSeq(), savedBoard.getTitle(), savedBoard.getContent(),
//                savedBoard.getCreatedAt(), savedBoard.getModifiedAt());
    }

    public List<Board> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return null;
//        return boards.stream()
//                .map(board -> new BoardResponseDto(board.getBoardSeq(), board.getTitle(), board.getContent(),
//                        board.getCreatedAt(), board.getModifiedAt()))
//                .collect(Collectors.toList());
    }

    public Board getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        return null;
//        return new BoardResponseDto(board.getBoardSeq(), board.getTitle(), board.getContent(),
//                board.getCreatedAt(), board.getModifiedAt());
    }

    public List<BoardResponseDto> getAllUserBoards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<Board> userBoards = boardRepository.findByUser(user);
        return null;
//        return userBoards.stream()
//                .map(board -> new BoardResponseDto(board.getBoardSeq(), board.getTitle(), board.getContent(),
//                        board.getCreatedAt(), board.getModifiedAt()))
//                .collect(Collectors.toList());
    }

    public List<BoardResponseDto> getSelectedUserBoards(Long userId) {
        return null;
        // 이 부분은 특정 사용자가 선택한 게시물을 조회하는 로직을 작성
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

    public void deleteBoard(Long boardnum, BoardDeleteDto requestDto, User user) {
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
