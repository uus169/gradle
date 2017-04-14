/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.tasks;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.changedetection.state.FileCollectionSnapshotter;
import org.gradle.api.internal.changedetection.state.GenericFileCollectionSnapshotter;
import org.gradle.api.internal.changedetection.state.SnapshotNormalizationStrategy;
import org.gradle.api.internal.changedetection.state.TaskFilePropertyCompareStrategy;
import org.gradle.api.internal.changedetection.state.TaskFilePropertySnapshotNormalizationStrategy;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.PathSensitivity;

import static org.gradle.api.internal.changedetection.state.TaskFilePropertySnapshotNormalizationStrategy.ABSOLUTE;

public class DefaultTaskInputPropertySpec extends AbstractTaskPropertyBuilder implements TaskInputPropertySpecAndBuilder {

    private final TaskPropertyFileCollection files;
    private boolean skipWhenEmpty;
    private boolean optional;
    private SnapshotNormalizationStrategy snapshotNormalizationStrategy = ABSOLUTE;
    private Class<? extends FileCollectionSnapshotter> snapshotter = GenericFileCollectionSnapshotter.class;

    public DefaultTaskInputPropertySpec(String taskName, boolean skipWhenEmpty, FileResolver resolver, Object paths) {
        this.files = new TaskPropertyFileCollection(taskName, "input", this, resolver, paths);
        this.skipWhenEmpty = skipWhenEmpty;
    }

    @Override
    public FileCollection getPropertyFiles() {
        return files;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal withPropertyName(String propertyName) {
        setPropertyName(propertyName);
        return this;
    }

    public boolean isSkipWhenEmpty() {
        return skipWhenEmpty;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal skipWhenEmpty(boolean skipWhenEmpty) {
        this.skipWhenEmpty = skipWhenEmpty;
        return this;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal skipWhenEmpty() {
        return skipWhenEmpty(true);
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal optional() {
        return optional(true);
    }

    @Override
    public TaskFilePropertyCompareStrategy getCompareStrategy() {
        return TaskFilePropertyCompareStrategy.UNORDERED;
    }

    @Override
    public SnapshotNormalizationStrategy getSnapshotNormalizationStrategy() {
        return snapshotNormalizationStrategy;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal withPathSensitivity(PathSensitivity sensitivity) {
        return withSnapshotNormalizationStrategy(TaskFilePropertySnapshotNormalizationStrategy.valueOf(sensitivity));
    }

    @Override
    public TaskInputFilePropertyBuilderInternal withSnapshotNormalizationStrategy(SnapshotNormalizationStrategy snapshotNormalizationStrategy) {
        this.snapshotNormalizationStrategy = snapshotNormalizationStrategy;
        return this;
    }

    @Override
    public TaskInputFilePropertyBuilderInternal withSnapshotter(Class<? extends FileCollectionSnapshotter> snapshotter) {
        this.snapshotter = snapshotter;
        return this;
    }

    @Override
    public Class<? extends FileCollectionSnapshotter> getSnapshotter() {
        return snapshotter;
    }

    @Override
    public String toString() {
        return getPropertyName() + " (" + snapshotNormalizationStrategy + ")";
    }

    @Override
    public int compareTo(TaskPropertySpec o) {
        return getPropertyName().compareTo(o.getPropertyName());
    }
}
