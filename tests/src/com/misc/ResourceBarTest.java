package com.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourceBarTest {

    private ResourceBar resource;

    @Before
    public void setUp() {
        resource = new ResourceBar(10f, 2f);
    }

    @Test
    public void resourceBarDecreases() {
        resource.subtractResourceAmount(10);
        assertEquals((int)resource.getCurrentAmount(), 90);
    }

    @Test
    public void resourceBarIncreases() {
        resource.resetResourceAmount();
        resource.subtractResourceAmount(20);
        resource.addResourceAmount(15);
        assertEquals((int)resource.getCurrentAmount(), 95);
    }
}
