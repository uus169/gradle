/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks.diagnostics.internal

import org.gradle.api.Action
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.tasks.diagnostics.internal.dependencies.RenderableDependency
import org.gradle.logging.StyledTextOutput

import static org.gradle.logging.StyledTextOutput.Style.Info

/**
 * by Szczepan Faber, created at: 9/20/12
 */
class DependencyGraphRenderer {

    GraphRenderer renderer
    NodeRenderer nodeRenderer
    boolean hasCyclicDependencies = false

    DependencyGraphRenderer(GraphRenderer renderer, NodeRenderer nodeRenderer) {
        this.renderer = renderer
        this.nodeRenderer = nodeRenderer
    }

    void render(RenderableDependency root) {
        def visited = new HashSet<ModuleVersionIdentifier>()
        visited.add(root.getId())
        renderChildren(root.getChildren(), visited);
    }

    private void renderChildren(Set<? extends RenderableDependency> children, Set<ModuleVersionIdentifier> visited) {
        renderer.startChildren();
        int i = 0;
        for (RenderableDependency child : children) {
            boolean last = i++ == children.size() - 1;
            render(child, last, visited);
        }
        renderer.completeChildren();
    }

    private void render(final RenderableDependency parent, boolean last, Set<ModuleVersionIdentifier> visited) {
        def children = parent.getChildren();
        boolean alreadyRendered = !visited.add(parent.getId())
        if (alreadyRendered) {
            hasCyclicDependencies = true
        }

        renderer.visit(new Action<StyledTextOutput>() {
            public void execute(StyledTextOutput output) {
                nodeRenderer.renderNode(output, parent, children, alreadyRendered);
            }
        }, last);

        if (!alreadyRendered) {
            renderChildren(children, visited);
        }
    }

    void printLegend() {
        if (hasCyclicDependencies) {
            renderer.output.withStyle(Info).println("(*) - dependencies omitted (listed previously)");
        }
    }
}
