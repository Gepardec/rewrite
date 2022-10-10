/*
 * Copyright 2022 the original author or authors.
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
package org.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.test.SourceSpecs.text;

public class RenameFileTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RenameFile("**/hello.txt", "goodbye.txt"));
    }

    @Test
    void hasFileMatch() {
        rewriteRun(
          text(
            "hello world",
            "hello world",
            spec -> {
                AtomicReference<SourceFile> before = new AtomicReference<>();
                spec
                  .path("a/b/hello.txt")
                  .beforeRecipe(before::set)
                  .afterRecipe(pt -> {
                      assertThat(pt.getSourcePath()).isEqualTo(Paths.get("a/b/goodbye.txt"));
                      assertThat(new Result(before.get(), pt, Collections.emptyList()).diff())
                        .isEqualTo(
                          """
                                diff --git a/a/b/hello.txt b/a/b/goodbye.txt
                                similarity index 0%
                                rename from a/b/hello.txt
                                rename to a/b/goodbye.txt
                            """ + "\n"
                        );
                  });
            }
          )
        );
    }

    @Test
    void hasNoFileMatch() {
        rewriteRun(
          text("hello world", spec -> spec.path("a/b/goodbye.txt"))
        );
    }
}