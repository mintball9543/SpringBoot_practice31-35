package com.example.jpa.board.repository;

import com.example.jpa.board.model.BoardTypeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardTypeCustomRepository {

    private final EntityManager entityManager;

    public List<BoardTypeCount> getBoardTypeCount() {

        String sql = " select bt.id, bt.board_name, bt.reg_date, bt.using_yn "
                + ", (select count(*) from board b where b.board_type_id = bt.id) as board_count "
                + " from board_type bt ";

        List<BoardTypeCount> list = entityManager.createNativeQuery(sql).getResultList();
        return list;

    }
}