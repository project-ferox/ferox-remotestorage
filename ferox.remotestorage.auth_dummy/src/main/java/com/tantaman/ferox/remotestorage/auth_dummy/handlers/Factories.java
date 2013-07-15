package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.remotestorage.auth_dummy.auth.UserRepo;

public class Factories {
	public static IRouteHandlerFactory loginPage(final String templateRoot) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new LoginPageHandler(templateRoot);
			}
		};
	}
	
	public static IRouteHandlerFactory authenticate(final UserRepo userRepo) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new LoginAuthenticationHandler(userRepo);
			}
		};
	}

	public static IRouteHandlerFactory clearUsers(final UserRepo userRepo) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new ClearUsersHandler(userRepo);
			}
		};
	}

	public static IRouteHandlerFactory registrationPage(final String templateRoot) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new RegistrationPageHandler(templateRoot);
			}
		};
	}

	public static IRouteHandlerFactory createUser(final UserRepo userRepo) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new CreateUserHandler(userRepo);
			}
		};
	}
}
