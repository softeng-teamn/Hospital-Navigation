package service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class ResourceLoaderTest {

    @Test
    @Category(FastTest.class)
    public void resourcesNotNull() {
        assertThat(ResourceLoader.edges, is(notNullValue()));
        assertThat(ResourceLoader.home, is(notNullValue()));
        assertThat(ResourceLoader.mapEdit, is(notNullValue()));
        assertThat(ResourceLoader.nodes, is(notNullValue()));
        assertThat(ResourceLoader.edges, is(notNullValue()));
        assertThat(ResourceLoader.request, is(notNullValue()));
        assertThat(ResourceLoader.scheduler, is(notNullValue()));
    }

}