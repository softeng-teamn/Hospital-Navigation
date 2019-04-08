package service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ResourceLoaderTest {

    @Test
    @Category(FastTest.class)
    public void resourcesNotNull() {
        assertThat(ResourceLoader.edges, is(notNullValue()));
        assertThat(ResourceLoader.home, is(notNullValue()));
        assertThat(ResourceLoader.nodes, is(notNullValue()));
        assertThat(ResourceLoader.edges, is(notNullValue()));
        assertThat(ResourceLoader.request, is(notNullValue()));
        assertThat(ResourceLoader.scheduler, is(notNullValue()));
        assertThat(ResourceLoader.reservablespaces, is(notNullValue()));
        assertThat(ResourceLoader.maintenanceRequest, is(notNullValue()));
        assertThat(ResourceLoader.externalTransportRequest, is(notNullValue()));
        assertThat(ResourceLoader.giftStoreRequest, is(notNullValue()));
        assertThat(ResourceLoader.ToyRequest, is(notNullValue()));
    }

}