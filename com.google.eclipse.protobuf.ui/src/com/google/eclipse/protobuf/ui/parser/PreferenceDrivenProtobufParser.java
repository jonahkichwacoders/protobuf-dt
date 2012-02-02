/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.parser;

import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.parser.antlr.ProtobufParser;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.ui.preferences.parser.core.ParserChecksPreferences;
import com.google.inject.Inject;

import org.antlr.runtime.CharStream;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceDrivenProtobufParser extends ProtobufParser {
  private static final String[] ERRORS_TO_LOOK_FOR = { "missing EOF at 'c'", "missing EOF at 'java'",
      "missing EOF at 'parsed'", "missing EOF at 'python'", "missing EOF at 'sawzall'",
      "no viable alternative at input '<'" };

  @Inject private IPreferenceStoreAccess storeAccess;

  // @Inject private ParserChecksSettingsProvider settingsProvider;
  @Override protected IParseResult doParse(String ruleName, CharStream in, NodeModelBuilder builder,
      int initialLookAhead) {
    IParseResult result = super.doParse(ruleName, in, builder, initialLookAhead);
    ParserChecksPreferences preferences = new ParserChecksPreferences(storeAccess);
    if (preferences.enableProto2OnlyChecks().getValue() && isNotProto2(result)) {
      return new ParseResult(new NonProto2Protobuf(), result.getRootNode(), false);
    }
    return result;
  }

  private boolean isNotProto2(IParseResult result) {
    for (INode node : result.getSyntaxErrors()) {
      if (isNonProto2(node.getSyntaxErrorMessage())) {
        return true;
      }
    }
    Protobuf root = (Protobuf) result.getRootASTElement();
    if (root != null && root.getSyntax() == null) {
      return true;
    }
    return false;
  }

  private boolean isNonProto2(SyntaxErrorMessage syntaxErrorMessage) {
    if (syntaxErrorMessage == null) {
      return false;
    }
    String message = syntaxErrorMessage.getMessage();
    for (String nonProto2Keyword : ERRORS_TO_LOOK_FOR) {
      if (message.contains(nonProto2Keyword)) {
        return true;
      }
    }
    return false;
  }
}
