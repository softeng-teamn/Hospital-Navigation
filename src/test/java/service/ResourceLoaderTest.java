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
<<<<<<< Temporary merge branch 1
=======

        assertThat(ResourceLoader.continue_icon, is(notNullValue()));
        assertThat(ResourceLoader.elevator_icon, is(notNullValue()));
        assertThat(ResourceLoader.walking_icon, is(notNullValue()));
        assertThat(ResourceLoader.stairs_down_icon, is(notNullValue()));
        assertThat(ResourceLoader.stairs_up_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_left_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_right_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_sharp_left_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_sharp_right_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_slight_left_icon, is(notNullValue()));
        assertThat(ResourceLoader.turn_slight_right_icon, is(notNullValue()));
        assertThat(ResourceLoader.uturn_icon, is(notNullValue()));

        assertThat(ResourceLoader.default_style, is(notNullValue()));
        assertThat(ResourceLoader.high_contrast_style, is(notNullValue()));

    }

}