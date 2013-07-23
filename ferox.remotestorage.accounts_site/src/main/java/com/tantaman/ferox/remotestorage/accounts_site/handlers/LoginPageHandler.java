package com.tantaman.ferox.remotestorage.accounts_site.handlers;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

public class LoginPageHandler extends AbstractAccountPageHandler {
	private final STGroup templateGroup;
	
	public LoginPageHandler(String templateRoot) {
		templateGroup = new STGroupDir(templateRoot, "UTF-8", '{', '}');
	}
	
	@Override
	protected ST getTemplate() {
		return templateGroup.getInstanceOf("AuthDialog");
	}
}
