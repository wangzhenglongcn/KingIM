package kingim.dao;

import kingim.model.Group;
import kingim.model.User;
import tk.mybatis.mapper.common.Mapper;

public interface GroupMapper extends Mapper<Group> {
    User getGroupMaster(int groupId);
}