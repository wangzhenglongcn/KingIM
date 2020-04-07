package kingim.service;

import kingim.model.User;

public interface UserService extends BaseService<User>{
	 
	  User getUserById(int userId);

	  User matchUser(String userName);

}
