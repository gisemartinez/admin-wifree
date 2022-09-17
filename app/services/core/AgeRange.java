package services.core;

import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

import static services.core.AnyRange.range;

@SuppressWarnings("WeakerAccess")
public class AgeRange {

    public static final Tuple2<Integer, Integer> RANGE_0_TO_20 = range(0, 20);
    public static final Tuple2<Integer, Integer> RANGE_21_TO_30 = range(21, 30);
    public static final Tuple2<Integer, Integer> RANGE_31_TO_40 = range(31, 40);
    public static final Tuple2<Integer, Integer> RANGE_41_TO_50 = range(41, 50);
    public static final Tuple2<Integer, Integer> RANGE_51_TO_150 = range(51, 150);

    public static final List<Tuple2<Integer, Integer>> values = Arrays.asList(
            RANGE_0_TO_20, RANGE_21_TO_30, RANGE_31_TO_40, RANGE_41_TO_50, RANGE_51_TO_150
    );

}
