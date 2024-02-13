package com.pokemon.newsfeed.controller;

import com.pokemon.newsfeed.dto.requestDto.BoardDeleteDto;
import com.pokemon.newsfeed.dto.requestDto.BoardRequestDto;
import com.pokemon.newsfeed.dto.requestDto.BoardUpdateDto;
import com.pokemon.newsfeed.dto.responseDto.BoardResponseDto;
import com.pokemon.newsfeed.entity.Board;
import com.pokemon.newsfeed.security.UserDetailsImpl;
import com.pokemon.newsfeed.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    // 게시물 생성 요청 처리
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @RequestBody BoardRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 게시물 서비스를 통해 새로운 게시물을 생성하고 생성된 게시물을 반환
        Board board = boardService.createBoard(requestDto, userDetails.getUser());
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        // 생성된 게시물을 HttpStatus.CREATED 상태로 반환
        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);
    }


    // 모든 게시물 조회 요청 처리
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        // 게시물 서비스를 통해 모든 게시물을 조회하고 조회된 게시물 리스트를 반환
        List<Board> boards = boardService.getAllBoards();
        // 조회된 게시물 리스트를 HttpStatus.OK 상태로 반환
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    // 특정 게시물 조회 요청 처리
    @GetMapping("/{boardnum}")
    public ResponseEntity<BoardResponseDto> getBoardById(
            @PathVariable Long boardnum
    ) {
        // 게시물 서비스를 통해 특정 ID의 게시물을 조회하고 조회된 게시물을 반환
        Board board = boardService.getBoardById(boardnum);
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);
        // 만약 조회된 게시물이 존재한다면 HttpStatus.OK 상태로 반환
        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);

    }

    @PutMapping("/{boardnum}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long boardnum,
            @RequestBody BoardUpdateDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Board board = boardService.updateBoard(boardnum, requestDto, userDetails.getUser());
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{boardnum}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long boardnum,
            @RequestBody BoardDeleteDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardService.deleteBoard(boardnum, requestDto, userDetails.getUser());
        return ResponseEntity.ok("삭제 완료");
    }
}
