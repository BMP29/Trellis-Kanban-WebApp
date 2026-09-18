package com.example.backend.service.Impl;

import com.example.backend.dto.*;
import com.example.backend.entity.Board;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.User;
import com.example.backend.exception.BoardNotFoundException;
import com.example.backend.mapper.BoardMapper;
import com.example.backend.mapper.TaskMapper;
import com.example.backend.repository.BoardRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.IBoardService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BoardServiceImpl implements IBoardService {
    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private TaskMapper taskMapper;

    @Override
    public Board createBoard(String title, UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Board newBoard = new Board(
                null,
                title,
                owner,
                new ArrayList<>()
        );

        List<BoardColumn> defaultColumns = generateDefaultColumns(newBoard);
        newBoard.getColumns().addAll(defaultColumns);

        return boardRepository.save(newBoard);
    }

    private List<BoardColumn> generateDefaultColumns(Board board) {
        BoardColumn todo = new BoardColumn(null, board, "Todo", 100, new ArrayList<>());
        BoardColumn inProgress = new BoardColumn(null, board, "In Progress", 200, new ArrayList<>());
        BoardColumn done = new BoardColumn(null, board, "Done", 300, new ArrayList<>());

        return List.of(todo, inProgress, done);
    }

    @Override
    public BoardDto getBoard(UUID id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));

        return BoardMapper.toDto(board);
    }

    @Transactional
    @Override
    public BoardUpdateDto updateBoard(BoardUpdateDto data) {
        Board board = boardRepository.findById(data.boardId())
                .orElseThrow(() -> new BoardNotFoundException(data.boardId()));

        board.setTitle(data.title());

        return new BoardUpdateDto(
                board.getId(),
                board.getTitle()
        );
    }

    @Override
    public BoardDto deleteBoard(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        BoardDto boardDto = BoardMapper.toDto(board);

        boardRepository.delete(board);

        return boardDto;
    }
}
