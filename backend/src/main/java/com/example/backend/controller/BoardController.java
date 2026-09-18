package com.example.backend.controller;

import com.example.backend.dto.BoardDto;
import com.example.backend.dto.BoardUpdateDto;
import com.example.backend.dto.CreateBoardDto;
import com.example.backend.entity.Board;
import com.example.backend.service.IBoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/board")
@AllArgsConstructor
public class BoardController {
    IBoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable String id) {
        UUID boardId = UUID.fromString(id);

        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createBoard(@ModelAttribute CreateBoardDto dataDto) {
        Board board = boardService.createBoard(dataDto.tittle(), UUID.fromString(dataDto.userId()));

        return ResponseEntity
                .created(URI.create("localhost:8080/" + board.getId().toString()))
                .build();
    }

    @PutMapping("/update")
    public ResponseEntity<BoardUpdateDto> updateBoard(@RequestBody BoardUpdateDto data) {
        BoardUpdateDto newBoard = boardService.updateBoard(data);

        return ResponseEntity.ok(newBoard);
    }
}
