package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.auth_dummy.auth.UserRepo;

public class ClearUsersHandler extends RouteHandlerAdapter {
	private final UserRepo userRepo;
	
	public ClearUsersHandler(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		userRepo.clearUsers();
		response.send("Users cleared");
	}
}
