package hollowrealm.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SocketingJeiCacheBenchmarkTest {
    private record ExampleRow(String base, String gem, String output) {
    }

    @Test
    void skipsNullLevelAndReusesImmutableSlotListsAcrossCacheHits() {
        var cache = new SocketingJeiExtension.SnapshotCache<String, Object, String, String, String>();
        AtomicInteger computeCount = new AtomicInteger();
        SocketingJeiExtension.SnapshotCache.ExampleComputer<String, Object, String, String, String> computer = (recipe, level, gems, rarities) -> {
            computeCount.incrementAndGet();
            List<String> bases = new ArrayList<>();
            List<String> gemStacks = new ArrayList<>();
            List<String> outputs = new ArrayList<>();
            for (String g : gems) {
                for (String r : rarities) {
                    bases.add("base:" + r);
                    gemStacks.add(g + ":" + r);
                    outputs.add(recipe + ":" + g + ":" + r);
                }
            }
            return new SocketingJeiExtension.SnapshotCache.Snapshot<>(
                    List.copyOf(bases), List.copyOf(gemStacks), List.copyOf(outputs));
        };

        List<String> gems = List.of("ruby", "sapphire", "emerald");
        List<String> rarities = List.of("common", "rare", "mythic");

        // 1. Null level fast-path must not compute
        var nullSnap = cache.getOrCompute("recipe1", null, gems, rarities, computer);
        assertTrue(nullSnap.bases().isEmpty());
        assertEquals(0, computeCount.get());

        // 2. First real level computes once, then three slot calls hit exact cached snapshot lists
        Object level1 = new Object();
        var s1 = cache.getOrCompute("recipe1", level1, gems, rarities, computer);
        var s2 = cache.getOrCompute("recipe1", level1, gems, rarities, computer);
        var s3 = cache.getOrCompute("recipe1", level1, gems, rarities, computer);
        assertEquals(1, computeCount.get());
        assertSame(s1.bases(), s2.bases());
        assertSame(s1.gems(), s3.gems());
        assertEquals(9, s1.outputs().size());

        // 3. Registry change or level change invalidates cache cleanly
        Object level2 = new Object();
        var s4 = cache.getOrCompute("recipe1", level2, gems, rarities, computer);
        assertEquals(2, computeCount.get());
        assertEquals(9, s4.bases().size());

        List<String> gemsUpdated = List.of("ruby", "sapphire", "emerald", "diamond");
        var s5 = cache.getOrCompute("recipe1", level2, gemsUpdated, rarities, computer);
        assertEquals(3, computeCount.get());
        assertEquals(12, s5.bases().size());

        // 4. Null level clears cached state
        var cleared = cache.getOrCompute("recipe1", null, gemsUpdated, rarities, computer);
        assertTrue(cleared.outputs().isEmpty());
        cache.getOrCompute("recipe1", level2, gemsUpdated, rarities, computer);
        assertEquals(4, computeCount.get());
    }

    @Test
    void concurrentLookupsMaintainConsistentSnapshotSizes() throws Exception {
        var cache = new SocketingJeiExtension.SnapshotCache<String, Object, String, String, String>();
        List<String> gems = List.of("g1", "g2", "g3", "g4");
        List<String> rarities = List.of("r1", "r2", "r3");
        Object[] levels = {new Object(), new Object()};
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int t = 0; t < 8; t++) {
                final int threadId = t;
                tasks.add(() -> {
                    for (int i = 0; i < 1000; i++) {
                        Object lvl = levels[(i + threadId) % 2];
                        var snap = cache.getOrCompute("recipe", lvl, gems, rarities, (rec, l, g, r) -> {
                            List<String> out = new ArrayList<>(g.size() * r.size());
                            for (String gg : g) {
                                for (String rr : r) {
                                    out.add(rec + ":" + gg + ":" + rr);
                                }
                            }
                            List<String> imm = List.copyOf(out);
                            return new SocketingJeiExtension.SnapshotCache.Snapshot<>(imm, imm, imm);
                        });
                        assertEquals(snap.bases().size(), snap.gems().size());
                        assertEquals(snap.bases().size(), snap.outputs().size());
                    }
                    return null;
                });
            }
            for (Future<Void> f : pool.invokeAll(tasks)) {
                f.get();
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void benchmarkCacheHitPerformanceVersusLegacy200PerSlotCopies() {
        List<String> gems = new ArrayList<>();
        for (int i = 0; i < 48; i++) {
            gems.add("gem_" + i);
        }
        List<String> rarities = List.of("common", "uncommon", "rare", "epic", "mythic");
        Collection<String> rawGems = List.copyOf(gems);
        Collection<String> rawRarities = List.copyOf(rarities);
        List<ExampleRow> legacyRows = new ArrayList<>();
        for (String g : gems) {
            for (String r : rarities) {
                legacyRows.add(new ExampleRow("base_" + r, g + "_" + r, "out_" + g + "_" + r));
            }
        }
        List<ExampleRow> immutableRows = List.copyOf(legacyRows);
        Object level = new Object();
        String recipe = "apotheosis:socketing";

        var cache = new SocketingJeiExtension.SnapshotCache<String, Object, String, String, String>();
        SocketingJeiExtension.SnapshotCache.ExampleComputer<String, Object, String, String, String> computer = (rec, lvl, g, r) -> {
            List<String> b = new ArrayList<>(immutableRows.size());
            List<String> gm = new ArrayList<>(immutableRows.size());
            List<String> o = new ArrayList<>(immutableRows.size());
            for (ExampleRow row : immutableRows) {
                b.add(row.base());
                gm.add(row.gem());
                o.add(row.output());
            }
            return new SocketingJeiExtension.SnapshotCache.Snapshot<>(List.copyOf(b), List.copyOf(gm), List.copyOf(o));
        };
        cache.getOrCompute(recipe, level, rawGems, rawRarities, computer);

        // Simulate 2.0.0 hot path: List.copyOf(gems) + List.copyOf(rarities) + examples.stream().map(...).toList() on each of 3 slot methods
        final int warmup = 2000;
        final int iterations = 20000;
        long sink = 0;

        for (int i = 0; i < warmup; i++) {
            sink += legacySlotPass(rawGems, rawRarities, gems, rarities, immutableRows);
            sink += optimizedSlotPass(cache, recipe, level, rawGems, rawRarities, computer);
        }

        long t0 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sink += legacySlotPass(rawGems, rawRarities, gems, rarities, immutableRows);
        }
        long legacyNs = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sink += optimizedSlotPass(cache, recipe, level, rawGems, rawRarities, computer);
        }
        long optimizedNs = System.nanoTime() - t1;

        double speedup = (double) legacyNs / Math.max(1L, optimizedNs);
        System.out.printf("[BENCHMARK-FIXES] iterations=%d entries=%d legacy_ms=%.3f optimized_ms=%.3f speedup=%.2fx sink=%d%n",
                iterations, immutableRows.size(), legacyNs / 1_000_000.0D, optimizedNs / 1_000_000.0D, speedup, sink);
        assertTrue(sink > 0);
        assertTrue(optimizedNs < legacyNs, "Expected 2.0.1 cached slot pass (" + optimizedNs + " ns) < 2.0.0 (" + legacyNs + " ns)");
    }

    private static int legacySlotPass(
            Collection<String> rawGems,
            Collection<String> rawRarities,
            List<String> prevGems,
            List<String> prevRarities,
            List<ExampleRow> examples) {
        int count = 0;
        for (int slot = 0; slot < 3; slot++) {
            List<String> gCopy = List.copyOf(rawGems);
            List<String> rCopy = List.copyOf(rawRarities);
            if (!prevGems.equals(gCopy) || !prevRarities.equals(rCopy)) {
                return -1;
            }
            List<String> mapped = switch (slot) {
                case 0 -> examples.stream().map(ExampleRow::base).toList();
                case 1 -> examples.stream().map(ExampleRow::gem).toList();
                default -> examples.stream().map(ExampleRow::output).toList();
            };
            count += mapped.size();
        }
        return count;
    }

    private static int optimizedSlotPass(
            SocketingJeiExtension.SnapshotCache<String, Object, String, String, String> cache,
            String recipe,
            Object level,
            Collection<String> rawGems,
            Collection<String> rawRarities,
            SocketingJeiExtension.SnapshotCache.ExampleComputer<String, Object, String, String, String> computer) {
        int count = 0;
        count += cache.getOrCompute(recipe, level, rawGems, rawRarities, computer).bases().size();
        count += cache.getOrCompute(recipe, level, rawGems, rawRarities, computer).gems().size();
        count += cache.getOrCompute(recipe, level, rawGems, rawRarities, computer).outputs().size();
        return count;
    }
}
