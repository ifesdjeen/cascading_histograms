package reactor.contrib.stats;

import edu.stanford.ppl.concurrent.SnapTreeMap;

import java.util.Iterator;
import java.util.Map;

/**
 * http://jmlr.org/papers/volume11/ben-haim10a/ben-haim10a.pdf
 */
public class Histogram {

  public final int maxBinSize;
  public final SnapTreeMap<Double, Long> bins;

  public Histogram(int maxBinSize) {
    this.maxBinSize = maxBinSize;
    this.bins = new SnapTreeMap<>();
  }

  public void add(Double t) {
    // if p = pi for some i then
    // mi = mi +1
    if(bins.containsKey(t)) {
      bins.put(t, bins.get(t) + 1);
    } else {
      bins.put(t, 1L);

      while(bins.size() > maxBinSize) {
        Iterator<Double> iterator = bins.keySet().iterator();

        double q1 = iterator.next();
        double q2 = iterator.next();
        double minDiff = q2 - q1;

        double i1 = q1;
        double i2 = q2;
        while(iterator.hasNext()) {
          i1 = i2;
          i2 = iterator.next();

          double currentDiff = i2 - i1;
          if(currentDiff < minDiff) {
            minDiff = currentDiff;
            q1 = i1;
            q2 = i2;
          }
        }

        long k1 = bins.remove(q1);
        long k2 = bins.remove(q2);

        bins.put(  (q1 * k1 + q2 * k2) / (k1 + k2), k1 + k2 );
      }
    }
  }

  public Map<Double, Long> getDistributions() {
    return this.bins.clone();
  }

  public void clear() {
    this.bins.clear();
  }
}
