package kingim.dao;

import kingim.model.FriendMessage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FriendMessageMapper extends Mapper<FriendMessage> {

    List<FriendMessage> getHistoryMessage(@Param("fromUserId")Integer fromUserId, @Param("toUserId")Integer toUserId);

    int updateAllToRead(@Param("fromUserId")Integer fromUserId,@Param("toUserId")Integer toUserId);

}
