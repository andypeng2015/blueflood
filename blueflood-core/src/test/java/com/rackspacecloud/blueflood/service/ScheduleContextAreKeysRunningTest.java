package com.rackspacecloud.blueflood.service;

import com.rackspacecloud.blueflood.rollup.Granularity;
import com.rackspacecloud.blueflood.rollup.SlotKey;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ScheduleContextAreKeysRunningTest {

    @Test
    public void noneScheduledOrRunningReturnsFalse() {

        // given
        long currentTime = 1234000L;

        final int shard = 0;
        List<Integer> managedShards = new ArrayList<Integer>() {{ add(shard); }};

        ScheduleContext ctx = new ScheduleContext(currentTime, managedShards);

        int slot = Granularity.MIN_5.slot(currentTime);
        SlotKey slotkey = SlotKey.of(Granularity.MIN_5, slot, shard);

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertFalse(areKeysRunning);
    }

    @Test
    public void slotScheduledReturnsTrue() {

        // given
        long currentTime = 1234000L;

        final int shard = 0;
        List<Integer> managedShards = new ArrayList<Integer>() {{ add(shard); }};

        ScheduleContext ctx = new ScheduleContext(currentTime, managedShards);
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        int slot = Granularity.MIN_5.slot(currentTime);
        SlotKey slotkey = SlotKey.of(Granularity.MIN_5, slot, shard);

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void childSlotScheduledReturnsTrue() {

        // given
        long currentTime = 1234000L;

        final int shard = 0;
        List<Integer> managedShards = new ArrayList<Integer>() {{ add(shard); }};

        ScheduleContext ctx = new ScheduleContext(currentTime, managedShards);
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        int slot = Granularity.MIN_20.slot(currentTime);
        SlotKey slotkey = SlotKey.of(Granularity.MIN_20, slot, shard);

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void unrelatedSlotScheduledReturnsFalse() {

        // given
        long currentTime = 1234000L;

        final int shard = 0;
        List<Integer> managedShards = new ArrayList<Integer>() {{ add(shard); }};

        ScheduleContext ctx = new ScheduleContext(currentTime, managedShards);
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        int slot = Granularity.MIN_5.slot(currentTime - 5*60*1000); // check the previous slot from 5 minutes ago
        SlotKey slotkey = SlotKey.of(Granularity.MIN_5, slot, shard);

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertFalse(areKeysRunning);
    }
}
