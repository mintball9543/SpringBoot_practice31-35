package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLogCount;
import com.example.jpa.user.model.UserNoticeCount;
import com.example.jpa.user.model.UserSummary;

import java.util.List;

public interface UserService {

    UserSummary getUserStatusCount();

    List<User> getTodayUsers();

    List<UserNoticeCount> getUserNoticeCount();

    List<UserLogCount> getUserLogCount();

    List<UserLogCount> getUserLikeBest();

    ServiceResult addInterestUser(String email, Long id);

    ServiceResult removeInterestUser(String email, Long id);
}
