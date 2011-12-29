/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.junit.*;

/**
 * Tests for <code>{@link ProjectVariable#replaceProjectVariableWithProjectName(IPath, IProject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProjectVariable_replaceProjectVariableWithProjectName_Test {

  private IProject project;

  @Before public void setUp() {
    project = mock(IProject.class);
  }

  @Test public void should_use_project_name_if_path_contains_variable() {
    when(project.getName()).thenReturn("test");
    IPath path = new Path("/${project}/src/test");
    IPath newPath = ProjectVariable.replaceProjectVariableWithProjectName(path, project);
    assertThat(newPath.toOSString(), equalTo("/test/src/test"));
  }

  @Test public void should_not_use_project_name_if_path_does_not_contain_variable() {
    when(project.getName()).thenReturn("test");
    IPath path = new Path("/main/src/test");
    IPath newPath = ProjectVariable.replaceProjectVariableWithProjectName(path, project);
    assertThat(newPath.toOSString(), equalTo("/main/src/test"));
  }

  @Test public void should_not_use_project_name_if_path_already_contains_it() {
    when(project.getName()).thenReturn("test");
    IPath path = new Path("/test/src/test");
    IPath newPath = ProjectVariable.replaceProjectVariableWithProjectName(path, project);
    assertThat(newPath.toOSString(), equalTo("/test/src/test"));
  }
}