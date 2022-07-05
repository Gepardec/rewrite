/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.format;

import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.style.*;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;

public class Spaces extends Recipe {

    @Override
    public String getDisplayName() {
        return "Spaces";
    }

    @Override
    public String getDescription() {
        return "Format whitespace in Java code.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new SpacesFromCompilationUnitStyle();
    }

    private static class SpacesFromCompilationUnitStyle extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public JavaSourceFile visitJavaSourceFile(JavaSourceFile cu, ExecutionContext executionContext) {
            SpacesStyle style = ((SourceFile) cu).getStyle(SpacesStyle.class);
            if (style == null) {
                style = IntelliJ.spaces();
            }
            doAfterVisit(new SpacesVisitor<>(style, ((SourceFile) cu).getStyle(EmptyForInitializerPadStyle.class),
                    ((SourceFile) cu).getStyle(EmptyForIteratorPadStyle.class)));
            return super.visitJavaSourceFile(cu, executionContext);
        }
    }

    public static <J2 extends J> J2 formatSpaces(J j, Cursor cursor) {
        SourceFile cu = cursor.firstEnclosingOrThrow(SourceFile.class);
        SpacesStyle style = cu.getStyle(SpacesStyle.class);
        //noinspection unchecked
        return (J2) new SpacesVisitor<>(style == null ? IntelliJ.spaces() : style,
                cu.getStyle(EmptyForInitializerPadStyle.class),
                cu.getStyle(EmptyForIteratorPadStyle.class)).visitNonNull(j, 0, cursor);
    }
}
