package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // query the chat list of the current user
    // each conversation only needs to return the latest private  message
    List<Message> selectConversations(int userId, int offset, int limit);

    // query the number of conversations of the current user
    int selectConversationCount(int userId);

    // query the private message list of one conversation
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // query the number of private message of one conversation
    int selectLetterCount(String conversationId);

    // query the number of unread letters
    int selectLetterUnreadCount(int userId, String conversationId);

    // add a new message
    int insertMessage(Message message);

    // change the status of a message
    int updateStatus(List<Integer> ids, int status);
}
