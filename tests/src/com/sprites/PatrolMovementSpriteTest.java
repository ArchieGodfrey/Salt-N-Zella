package com.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.pathFinding.Junction;
import com.pathFinding.MapGraph;
import com.pathFinding.Road;
import com.testrunner.GdxTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(GdxTestRunner.class)
public class PatrolMovementSpriteTest {

    private MapGraph mapgraph;

    private PatrolMovementSprite patrolMovementSpriteUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        Texture texture = Mockito.mock(Texture.class);
        mapgraph = new MapGraph();
        patrolMovementSpriteUnderTest = new PatrolMovementSprite(texture, mapgraph);
    }

    @Test
    public void testSetGoal() {
        final Junction goal = new Junction(0.0f, 0.0f, "name");
        when(mapgraph.findPath(any(Junction.class), any(Junction.class))).thenReturn(null);

        patrolMovementSpriteUnderTest.setGoal(goal);

        assertEquals(patrolMovementSpriteUnderTest.getGoal(), goal);
    }

    @Test
    public void testStep() {
        final Array<Junction> junctions = new Array<>(false, new Junction[]{new Junction(0.0f, 0.0f, "name")}, 0, 0);
        when(mapgraph.getJunctions()).thenReturn(junctions);

        when(mapgraph.findPath(any(Junction.class), any(Junction.class))).thenReturn(null);
        when(mapgraph.isRoadLocked(any(Junction.class), any(Junction.class))).thenReturn(false);

        final Road road = new Road(new Junction(0.0f, 0.0f, "name"), new Junction(0.0f, 0.0f, "name"));
        when(mapgraph.getRoad(any(Junction.class), any(Junction.class))).thenReturn(road);

        patrolMovementSpriteUnderTest.step();

        verify(mapgraph).unlockRoad(any(Road.class), any(PatrolMovementSprite.class));
        verify(mapgraph).lockRoad(any(Road.class), any(PatrolMovementSprite.class));
    }
}
