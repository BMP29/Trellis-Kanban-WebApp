package com.example.backend.service.Impl;

import com.example.backend.dto.BoardColumnDto;
import com.example.backend.dto.ColumnCreateRequest;
import com.example.backend.dto.ColumnUpdateDto;
import com.example.backend.entity.Board;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.Task;
import com.example.backend.exception.BoardNotFoundException;
import com.example.backend.exception.ColumnNotFoundException;
import com.example.backend.mapper.BoardMapper;
import com.example.backend.mapper.ColumnMapper;
import com.example.backend.repository.BoardRepository;
import com.example.backend.repository.ColumnRepository;
import com.example.backend.service.IColumnService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ColumnServiceImpl implements IColumnService {
    ColumnRepository columnRepository;
    BoardRepository boardRepository;
    ColumnMapper columnMapper;

    @Transactional
    @Override
    public BoardColumnDto createColumn(ColumnCreateRequest data) {
        Board board = boardRepository.findById(data.boardId())
                .orElseThrow(() -> new BoardNotFoundException(data.boardId()));

        int maxPosition = board.getColumns().stream()
                .mapToInt(BoardColumn::getPosition)
                .max()
                .orElse(0);

        BoardColumn newCol = new BoardColumn(
                UUID.randomUUID(),
                board,
                data.columnName(),
                maxPosition+100,
                new ArrayList<Task>()
        );

        board.getColumns().add(newCol);

        return BoardMapper.toColumnDto(newCol);
    }

    @Transactional
    @Override
    public ColumnUpdateDto updateColumn(UUID boardId, UUID columnId, ColumnUpdateDto data) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId));

        if(data.position() != null && data.position() < 0)
            throw new IllegalArgumentException("Position must be greater than or equal to 0");

        columnMapper.updateColumnFromDto(data, column);

//        if(!Objects.equals(data.position(), column.getPosition())) {
//            //ensure newPosition is within the range 0 to size - 1
//            int newPosition = Math.max(Math.min(data.position(), columns.size() - 1), 0);
//            int oldPosition = column.getPosition();
//            column.setPosition(newPosition);
//
//            if(oldPosition < newPosition) {
//                OrderFromLeftToRight(columns, column, oldPosition);
//            } else if (oldPosition  > newPosition) {
//                OrderFromRightToLeft(columns, column, oldPosition);
//            }
//        }

        List<BoardColumn> columns = column.getBoard().getColumns();

        orderColumnListAfterColumn(columns, column);

        return new ColumnUpdateDto(
                column.getTitle(),
                column.getPosition()
        );
    }

    private void orderColumnListAfterColumn(List<BoardColumn> list, BoardColumn column) {
        list.sort(
                Comparator.comparingInt(BoardColumn::getPosition)
                        .thenComparing(col -> col.getId().equals(column.getId()) ? 0 : 1)
        );

        int index = list.indexOf(column);

        BoardColumn currentColumn = column;

        for(int i = index+1; i < list.size(); i++) {
            if(list.get(i).getPosition() > currentColumn.getPosition())
                return;

            list.get(i).setPosition(currentColumn.getPosition()+10);
            currentColumn = list.get(i);
        }
    }

//    public void OrderFromLeftToRight(List<BoardColumn> columns, BoardColumn column, int oldPosition) {
//        columns
//                .forEach(col -> {
//                    if(col.getId().equals(column.getId())) return;
//
//                    if(col.getPosition() < oldPosition) return;
//
//                    if(col.getPosition() <= column.getPosition()) {
//                        col.setPosition(col.getPosition()-1) ;
//                    }
//                });
//    }
//
//    public void OrderFromRightToLeft(List<BoardColumn> columns, BoardColumn column, int oldPosition) {
//        columns
//                .forEach(col -> {
//                    if(col.getId().equals(column.getId())) return;
//
//                    if(col.getPosition() > oldPosition) return;
//
//                    if(col.getPosition() >= column.getPosition()) {
//                        col.setPosition(col.getPosition()+1) ;
//                    }
//                });
//    }

    @Transactional
    @Override
    public void deleteColumn(UUID columnId) {
        BoardColumn toBeDeleted = columnRepository.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId));

        List<BoardColumn> columns = toBeDeleted.getBoard().getColumns();

        columns.forEach(col -> {
            if(col.getPosition() > toBeDeleted.getPosition()) {
                col.setPosition(col.getPosition()-1);
            }
        });

        toBeDeleted.getTasks().clear();
        columns.remove(toBeDeleted);
    }
}
