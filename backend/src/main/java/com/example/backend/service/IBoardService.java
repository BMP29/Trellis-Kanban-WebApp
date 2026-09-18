package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.Board;

import java.util.UUID;

public interface IBoardService {
    public Board createBoard(String tittle, UUID userId);
    public BoardDto getBoard(UUID id);
    public BoardUpdateDto updateBoard(BoardUpdateDto data);
    public BoardDto deleteBoard(UUID boardId);
}