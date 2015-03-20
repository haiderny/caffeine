/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache;

import com.squareup.javapoet.CodeBlock;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class NodeSelectorCode {
  private final CodeBlock.Builder name;

  private NodeSelectorCode() {
    name = CodeBlock.builder()
        .addStatement("$T sb = new $T()", StringBuilder.class, StringBuilder.class);
  }

  private NodeSelectorCode keys() {
    name.beginControlFlow("if (strongKeys)")
            .addStatement("sb.append('S')")
        .nextControlFlow("else")
            .addStatement("sb.append('W')")
        .endControlFlow();
    return this;
  }

  private NodeSelectorCode values() {
    name.beginControlFlow("if (strongValues)")
            .addStatement("sb.append(\"St\")")
        .nextControlFlow("else if (weakValues)")
            .addStatement("sb.append('W')")
        .nextControlFlow("else")
            .addStatement("sb.append(\"So\")")
        .endControlFlow();
    return this;
  }

  private NodeSelectorCode expires() {
    name.beginControlFlow("if (expiresAfterAccess)")
            .addStatement("sb.append('A')")
        .endControlFlow()
        .beginControlFlow("if (expiresAfterWrite)")
            .addStatement("sb.append('W')")
        .endControlFlow()
        .beginControlFlow("if (refreshAfterWrite)")
            .addStatement("sb.append('R')")
        .endControlFlow();
    return this;
  }

  private NodeSelectorCode maximum() {
    name.beginControlFlow("if (maximumSize)")
            .addStatement("sb.append('M')")
            .beginControlFlow("if (weighed)")
                .addStatement("sb.append('W')")
            .nextControlFlow("else")
                .addStatement("sb.append('S')")
            .endControlFlow()
        .endControlFlow();
    return this;
  }

  private CodeBlock build() {
    return name
        .addStatement("return valueOf(sb.toString())")
        .build();
  }

  public static CodeBlock get() {
    return new NodeSelectorCode()
        .keys()
        .values()
        .expires()
        .maximum()
        .build();
  }
}
