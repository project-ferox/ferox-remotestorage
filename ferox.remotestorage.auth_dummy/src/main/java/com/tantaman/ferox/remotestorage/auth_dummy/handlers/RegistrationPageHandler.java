package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

public class RegistrationPageHandler extends AbstractAccountPageHandler {
	private final STGroup templateGroup;

	public RegistrationPageHandler(String templateRoot) {
		templateGroup = new STGroupDir(templateRoot, "UTF-8", '{', '}');
	}

	@Override
	protected ST getTemplate() {
		return templateGroup.getInstanceOf("RegistrationPage");
	}
}
