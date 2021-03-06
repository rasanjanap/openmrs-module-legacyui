/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.directwebremoting.servlet.EfficientShutdownServletContextAttributeListener;
import org.openmrs.module.web.filter.ForcePasswordChangeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class WebComponentRegistrar implements ServletContextAware {
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		
		ServletRegistration openmrsServletReg = servletContext.getServletRegistration("openmrs");
		addMappings(openmrsServletReg, "*.htm", "*.form", "*.list", "*.json", "*.field", "*.portlet");
		
		addMappings(servletContext.getServletRegistration("jsp"), "*.withjstl");
		
		Dynamic filter = servletContext.addFilter("forcePasswordChangeFilter", new ForcePasswordChangeFilter());
		filter.setInitParameter("changePasswordForm", "/admin/users/changePassword.form");
		filter.setInitParameter("excludeURL", "changePasswordForm,logout,.js,.css,.gif,.jpg,.jpeg,.png");
		filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		servletContext.addListener(new SessionListener());
		/*
		 * EfficientShutdownServletContextAttributeListener is used instead of
		 * EfficientShutdownServletContextListener since the latter implements ServletContextListener,
		 * which is not supported by ServletContext.addListener.
		*/
		servletContext.addListener(new EfficientShutdownServletContextAttributeListener());
	}
	
	private void addMappings(ServletRegistration reg, String... mappings) {
		for (String mapping : mappings) {
			reg.addMapping(mapping);
		}
	}
	
}
