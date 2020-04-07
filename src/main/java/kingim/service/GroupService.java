package kingim.service;

import kingim.model.Group;
import kingim.model.User;

/**
 * 群组Service
 */
public interface GroupService extends BaseService<Group> {

	User getGroupMaster(int groupId);
}
