package kingim.dao;

import kingim.model.GroupMessage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GroupMessageMapper extends Mapper<GroupMessage> {

    List<GroupMessage> getHistoryMessage(@Param("groupId")Integer groupId);
}
