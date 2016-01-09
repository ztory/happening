package com.ztory.lib.happening;

import com.ztory.lib.happening.typed.Slab;

import junit.framework.TestCase;

/**
 * Tests the functionality in the Slab class.
 */
public class SlabTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSlab() {
        //TODO Test functionality from CLASS: ConcurrentHashMap<String, Object>
        //TODO Test functionality from INTERFACE: TypedMap<String, Object>
        //TODO Test functionality from INTERFACE: TypedPayload<P>
        //TODO Test functionality from INTERFACE: Run
        //TODO Test functionality from INTERFACE: DeedSetter<Slab, P>
    }

    public void testMemberRun() throws Exception {
        Slab<Void> slab = new Slab<>(4);
        slab.put(Slab.RUN, new Run<String, Object>() {
            @Override
            public String r(Object o) {
                return "HELLO RUN WORLD!";
            }
        });
        Object slabRunObject = slab.r(null);
        assertEquals("HELLO RUN WORLD!", slabRunObject);
        assertTrue(slab.typed(Slab.RUN) instanceof Run);
    }

}
