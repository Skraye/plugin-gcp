package io.kestra.plugin.gcp.bigquery;

import com.devskiller.friendly_id.FriendlyId;
import com.google.common.collect.ImmutableMap;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.core.utils.TestsUtils;

import java.util.Arrays;
import java.util.Collections;
import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MicronautTest
class LoadFromGcsTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Value("${kestra.tasks.bigquery.project}")
    private String project;

    @Value("${kestra.tasks.bigquery.dataset}")
    private String dataset;

    @Test
    void fromJson() throws Exception {
        LoadFromGcs task = LoadFromGcs.builder()
            .id(LoadFromGcsTest.class.getSimpleName())
            .type(LoadFromGcs.class.getName())
            .from(Collections.singletonList(
                "gs://cloud-samples-data/bigquery/us-states/us-states.json"
            ))
            .destinationTable(project + "." + dataset + "." + FriendlyId.createFriendlyId())
            .format(AbstractLoad.Format.JSON)
            .schema(ImmutableMap.of(
                "fields", Arrays.asList(
                    ImmutableMap.of("name", "name", "type", "STRING"),
                    ImmutableMap.of("name", "post_abbr", "type", "STRING")
                )
            ))
            .build();
        RunContext runContext = TestsUtils.mockRunContext(runContextFactory, task, ImmutableMap.of());

        AbstractLoad.Output run = task.run(runContext);
        assertThat(run.getRows(), is(50L));
    }
}