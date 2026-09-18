package com.example.backend.service;

import com.example.backend.dto.ColumnUpdateDto;
import com.example.backend.entity.BoardColumn;
import com.example.backend.exception.ColumnNotFoundException;
import com.example.backend.repository.BoardRepository;
import com.example.backend.repository.ColumnRepository;
import com.example.backend.service.Impl.BoardServiceImpl;
import com.example.backend.service.Impl.ColumnServiceImpl;
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
public class ColumnServiceImplTest {

    private ColumnServiceImpl columnService;
    private ColumnRepository columnRepository;

    @Autowired
    public ColumnServiceImplTest(
            ColumnServiceImpl columnService,
            ColumnRepository columnRepository
    ) {
        this.columnService = columnService;
        this.columnRepository = columnRepository;
    }

    @Test
    @Transactional
    public void updateColumnTest_UpdatesColumnFields_whenEditingColumn() {
        ColumnUpdateDto data = new ColumnUpdateDto(
                "ColumnAtt",
                1
        );

        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");

        ColumnUpdateDto updatedColumn = columnService.updateColumn(boardId, columnId, data);

        assertEquals(data.title(), updatedColumn.title());
        assertEquals(data.position(), updatedColumn.position());
    }

    @Test
    @Transactional
    public void updateColumnTest_keepsColumnPositionsContiguousZeroBased_whenMovingColumnFromRightToLeft() {
        ColumnUpdateDto data = new ColumnUpdateDto(
                "ColumnAtt",
                1
        );

        UUID columnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");

        ColumnUpdateDto updatedColumn = columnService.updateColumn(boardId, columnId, data);

        List<BoardColumn> columns = columnRepository.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId))
                .getBoard().getColumns();

        columns.sort(Comparator.comparingInt(BoardColumn::getPosition));

        boolean arePositionsContiguousZeroBased  = IntStream.range(0, columns.size())
                .allMatch(i -> columns.get(i).getPosition() == i);

        assertEquals(true, arePositionsContiguousZeroBased);
    }

    @Test
    @Transactional
    public void updateColumnTest_keepsColumnsPositionsStrictlyIncreasing_whenMovingColumnFromLeftToRight() {
        ColumnUpdateDto data = new ColumnUpdateDto(
                "ColumnAtt",
                3
        );

        UUID columnId = UUID.fromString("8c532cb7-b709-43ee-bf80-e86fa8461a39");
        UUID boardId = UUID.fromString("7696bb61-4c34-491c-93ab-3fb212dedf6d");

        ColumnUpdateDto updatedColumn = columnService.updateColumn(boardId, columnId, data);

        List<BoardColumn> columns = columnRepository.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId))
                .getBoard().getColumns();

        columns.sort(Comparator.comparingInt(BoardColumn::getPosition));

        boolean arePositionsStrictlyIncreasing = IntStream.range(0, columns.size()-1)
                .allMatch(i -> columns.get(i).getPosition() < columns.get(i+1).getPosition());

        assertEquals(true, arePositionsStrictlyIncreasing);
    }

}
