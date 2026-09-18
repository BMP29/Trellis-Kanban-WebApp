package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.Board;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.Task;
import com.example.backend.exception.BoardNotFoundException;
import com.example.backend.exception.ColumnNotFoundException;
import com.example.backend.repository.BoardRepository;
import com.example.backend.service.Impl.BoardServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BoardServiceImplTest {

    private BoardServiceImpl boardService;
    private BoardRepository boardRepository;

    @Autowired
    public BoardServiceImplTest(
            BoardServiceImpl boardService,
            BoardRepository boardRepository
    ) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

//    @Test
//    @Transactional
//    public void updateColumnTest_UpdatesColumnFields_whenEditingColumn() {
//        ColumnUpdateDto data = new ColumnUpdateDto(
//                "ColumnAtt",
//                1
//        );
//
//        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//
//        ColumnUpdateDto updatedColumn = boardService.updateColumn(boardId, columnId, data);
//
//        assertEquals(data.title(), updatedColumn.title());
//        assertEquals(data.position(), updatedColumn.position());
//    }
//
//    @Test
//    @Transactional
//    public void updateColumnTest_keepsColumnPositionsContiguousZeroBased_whenMovingColumnFromRightToLeft() {
//        ColumnUpdateDto data = new ColumnUpdateDto(
//                "ColumnAtt",
//                1
//        );
//
//        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//
//        ColumnUpdateDto updatedColumn = boardService.updateColumn(boardId, columnId, data);
//
//        List<BoardColumn> columns = boardRepository.findById(boardId)
//                .orElseThrow(() -> new RuntimeException("board not found"))
//                .getColumns();
//
//        columns.sort(Comparator.comparingInt(BoardColumn::getPosition));
//
//        boolean arePositionsContiguousZeroBased  = IntStream.range(0, columns.size())
//                        .allMatch(i -> columns.get(i).getPosition() == i);
//
//        assertEquals(true, arePositionsContiguousZeroBased);
//    }
//
//    @Test
//    @Transactional
//    public void updateColumnTest_keepsColumnsPositionsStrictlyIncreasing_whenMovingColumnFromLeftToRight() {
//        ColumnUpdateDto data = new ColumnUpdateDto(
//                "ColumnAtt",
//                3
//        );
//
//        UUID columnId = UUID.fromString("8c532cb7-b709-43ee-bf80-e86fa8461a39");
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//
//        ColumnUpdateDto updatedColumn = boardService.updateColumn(boardId, columnId, data);
//
//        List<BoardColumn> columns = boardRepository.findById(boardId)
//                .orElseThrow(() -> new RuntimeException("board not found"))
//                .getColumns();
//
//        columns.sort(Comparator.comparingInt(BoardColumn::getPosition));
//
//        boolean arePositionsStrictlyIncreasing = IntStream.range(0, columns.size()-1)
//                .allMatch(i -> columns.get(i).getPosition() < columns.get(i+1).getPosition());
//
//        assertEquals(true, arePositionsStrictlyIncreasing);
//    }
//
//    @Test
//    @Transactional
//    public void deleteColumnTest_keepsColumnsPositionsStrictlyIncreasing_whenDeletingColumn() {
//        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//
//        boardService.deleteColumn(boardId, columnId);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new BoardNotFoundException(boardId));
//
//        List<BoardColumn> columns = board.getColumns();
//
//        columns.sort(Comparator.comparingInt(BoardColumn::getPosition));
//
//        boolean arePositionsStrictlyIncreasing = IntStream.range(0, columns.size()-1)
//                .allMatch(i -> columns.get(i).getPosition() < columns.get(i+1).getPosition());
//
//        assertEquals(true, arePositionsStrictlyIncreasing);
//    }

//    @Test
//    @Transactional
//    public void deleteTaskTest_keepsTasksPositionsStrictlyIncreasing_whenDeletingTask() {
//        UUID taskId = UUID.fromString("03f9417c-b739-4507-8aed-830a2415d337");
//        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//
//        boardService.deleteTask(boardId, columnId, taskId);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new BoardNotFoundException(boardId));
//
//        BoardColumn column = board.getColumns().stream()
//                .filter(col -> col.getId().equals(columnId))
//                .findAny()
//                .orElseThrow(() -> new ColumnNotFoundException(boardId, columnId));
//
//        List<Task> tasks = column.getTasks();
//
//        tasks.sort(Comparator.comparingInt(Task::getPosition));
//
//        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
//                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());
//
//        assertEquals(true, arePositionsStrictlyIncreasing);
//    }
//
//    @Test
//    @Transactional
//    public void updateTaskTest_keepsTasksPositionsStrictlyIncreasing_whenUpdatingTaskPosition() {
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//        UUID columnId = UUID.fromString("8c532cb7-b709-43ee-bf80-e86fa8461a39");
//        UUID newColumnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID taskId = UUID.fromString("18bbe496-194a-4ec3-899f-5f2a31e4f101");
//
//        TaskUpdateDto data = new TaskUpdateDto(
//                null,
//                2,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        TaskUpdateResponseDto response = boardService.updateTask(boardId, columnId, taskId, data);
//
//        List<BoardColumn> columns = boardRepository.findById(boardId)
//                .orElseThrow(() -> new BoardNotFoundException(boardId))
//                .getColumns();
//
//        BoardColumn column = columns.stream()
//                .filter(col -> col.getId().equals(response.columnId()))
//                .findAny()
//                .orElseThrow(() -> new ColumnNotFoundException(boardId, response.columnId()));
//
//        List<Task> tasks = column.getTasks();
//
//        tasks.sort(Comparator.comparingInt(Task::getPosition));
//
//        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
//                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());
//
//        assertEquals(true, arePositionsStrictlyIncreasing);
//    }
//
//    @Test
//    @Transactional
//    public void updateTaskTest_keepsTasksPositionsStrictlyIncreasing_whenUpdatingTaskPositionAndColumn() {
//        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");
//        UUID columnId = UUID.fromString("8c532cb7-b709-43ee-bf80-e86fa8461a39");
//        UUID newColumnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
//        UUID taskId = UUID.fromString("18bbe496-194a-4ec3-899f-5f2a31e4f101");
//
//        TaskUpdateDto data = new TaskUpdateDto(
//                newColumnId,
//                2,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        TaskUpdateResponseDto response = boardService.updateTask(boardId, columnId, taskId, data);
//
//        List<BoardColumn> columns = boardRepository.findById(boardId)
//                .orElseThrow(() -> new BoardNotFoundException(boardId))
//                .getColumns();
//
//        BoardColumn column = columns.stream()
//                .filter(col -> col.getId().equals(response.columnId()))
//                .findAny()
//                .orElseThrow(() -> new ColumnNotFoundException(boardId, response.columnId()));
//
//        List<Task> tasks = column.getTasks();
//
//        tasks.sort(Comparator.comparingInt(Task::getPosition));
//
//        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
//                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());
//
//        assertEquals(true, arePositionsStrictlyIncreasing);
//    }

}
