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
        assertThat(ResourceLoader.home, is(notNullValue()));
        assertThat(ResourceLoader.scheduler, is(notNullValue()));
        assertThat(ResourceLoader.request, is(notNullValue()));
        assertThat(ResourceLoader.createNode, is(notNullValue()));
        assertThat(ResourceLoader.maintenanceRequest, is(notNullValue()));

        assertThat(ResourceLoader.floristRequest, is(notNullValue()));
        assertThat(ResourceLoader.patientInfoRequest, is(notNullValue()));
        assertThat(ResourceLoader.interpreterRequest, is(notNullValue()));
        assertThat(ResourceLoader.sanitationRequest, is(notNullValue()));
        assertThat(ResourceLoader.ToyRequest, is(notNullValue()));
        assertThat(ResourceLoader.securityRequest, is(notNullValue()));
        assertThat(ResourceLoader.giftStoreRequest, is(notNullValue()));
        assertThat(ResourceLoader.medicineRequest, is(notNullValue()));
        assertThat(ResourceLoader.itRequest, is(notNullValue()));

        assertThat(ResourceLoader.edges, is(notNullValue()));
        assertThat(ResourceLoader.nodes, is(notNullValue()));
        assertThat(ResourceLoader.fulfillrequest, is(notNullValue()));
        assertThat(ResourceLoader.internalTransportRequest, is(notNullValue()));
        assertThat(ResourceLoader.reservablespaces, is(notNullValue()));
        assertThat(ResourceLoader.employees, is(notNullValue()));
        assertThat(ResourceLoader.continue_icon, is(notNullValue()));
    }

}