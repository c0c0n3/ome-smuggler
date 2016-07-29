package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static util.sequence.Arrayz.array;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.types.BaseStringId;
import ome.smuggler.core.types.PositiveN;


@RunWith(Theories.class)
public class TSafeKeyValueStoreTest {

    interface TestStore extends KeyValueStore<BaseStringId, Integer> {}

    @DataPoints
    public static Integer[] stripeSupply = array(1, 2, 3);

    @DataPoints
    public static BaseStringId[] keySupply =
            Stream.generate(BaseStringId::new)
                  .limit(200)
                  .toArray(BaseStringId[]::new);

    @Theory
    public void sameKeysShareSameLock(BaseStringId key, Integer stripes) {
        TSafeKeyValueStore<BaseStringId, Integer> target =
                new TSafeKeyValueStore<>(mock(TestStore.class),
                                         PositiveN.of(stripes));
        BaseStringId sameKey = new BaseStringId(key.id());
        assertThat(target.lookupLock(key), is(target.lookupLock(sameKey)));
    }

    @Test
    public void differentKeysMayHaveDifferentLocks() {
        int stripes = 2;
        TSafeKeyValueStore<BaseStringId, Integer> target =
                new TSafeKeyValueStore<>(mock(TestStore.class),
                                         PositiveN.of(stripes));
        Set<Lock> lookedUpLocks = new HashSet<>();

        for (BaseStringId key : keySupply) {
            lookedUpLocks.add(target.lookupLock(key));
        }
        assertThat(lookedUpLocks.size(), is(stripes));
    }

    @Test
    public void forwardPutCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<BaseStringId, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        wrapper.put(keySupply[0], 1);
        verify(target).put(keySupply[0], 1);
    }

    @Test
    public void forwardModifyCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<BaseStringId, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        Function<Integer, Integer> id = x -> x;
        wrapper.modify(keySupply[0], id);
        verify(target).modify(keySupply[0], id);
    }

    @Test
    public void forwardRemoveCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<BaseStringId, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        wrapper.remove(keySupply[0]);
        verify(target).remove(keySupply[0]);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTarget() {
        new TSafeKeyValueStore<>(null, PositiveN.of(1));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullStripes() {
        new TSafeKeyValueStore<>(mock(TestStore.class), null);
    }

}
