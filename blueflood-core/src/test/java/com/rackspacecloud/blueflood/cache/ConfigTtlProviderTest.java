/*
 * Copyright 2013 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rackspacecloud.blueflood.cache;

import com.rackspacecloud.blueflood.rollup.Granularity;
import com.rackspacecloud.blueflood.service.Configuration;
import com.rackspacecloud.blueflood.service.TtlConfig;
import com.rackspacecloud.blueflood.types.RollupType;
import com.rackspacecloud.blueflood.utils.TimeValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ConfigTtlProviderTest {
    private ConfigTtlProvider ttlProvider;
    private static final String RAW_METRICS_TTL = TtlConfig.RAW_METRICS_TTL.toString();
    private static final String TTL_CONFIG_CONST = TtlConfig.TTL_CONFIG_CONST.toString();

    @Before
    public void setUp() {
        System.setProperty(RAW_METRICS_TTL, "5");
        System.setProperty(TTL_CONFIG_CONST, "5");

        // We can't use ConfigTtlProvider.getInstance() because
        // that instance can be initialized by other previously
        // run tests (thus we have not gotten to make the
        // System.setProperty() calls above, and ttlMapper
        // wont be initialized properly). Since this is a
        // test class testing the ConfigTtlProvider, it is
        // ok to call the constructor directly, to ensure
        // we get a fresh instance for this test.
        this.ttlProvider = new ConfigTtlProvider();
    }

    @Test
    public void testConfigTtl_valid() throws Exception {
        Assert.assertEquals(Configuration.getInstance().getIntegerProperty(RAW_METRICS_TTL), 5);
        Assert.assertTrue(new TimeValue(5, TimeUnit.DAYS).equals(
                ttlProvider.getTTL("acFoo", Granularity.FULL, RollupType.BF_BASIC).get()));
    }

    @Test
    public void testConfigTtl_invalid() {
        Assert.assertFalse(ttlProvider.getTTL("acBar", Granularity.FULL, RollupType.SET).isPresent());
    }

    @Test
    public void testConfigTtlForIngestion() throws Exception {
        Assert.assertEquals(Configuration.getInstance().getIntegerProperty(TTL_CONFIG_CONST), 5);
        Assert.assertTrue(new TimeValue(5, TimeUnit.DAYS).equals(ttlProvider.getConfigTTLForIngestion()));
    }

    @After
    public void tearDown() {
        System.clearProperty(RAW_METRICS_TTL);
        System.clearProperty(TTL_CONFIG_CONST);
    }
}