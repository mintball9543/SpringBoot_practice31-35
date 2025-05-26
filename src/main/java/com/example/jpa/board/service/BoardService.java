package com.example.jpa.board.service;

import com.example.jpa.board.model.BoardTypeInput;
import com.example.jpa.board.model.ServiceResult;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

public interface BoardService {
    ServiceResult addBoard(BoardTypeInput boardTypeInput);

    ServiceResult updateBoard(long id, BoardTypeInput boardTypeInput);

    ServiceResult deleteBoard(Long id);
}
