package icm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;


public class IcmCalcModel {

    private final Random random = new Random();

    public double[] getEquities(double[] payouts, double[] stacks,
                                int maxIterations) {
        if (payouts.length > stacks.length)
            payouts = Arrays.copyOf(payouts, stacks.length);

        double[] equities = new double[stacks.length];
        double[] exp = getExponents(stacks);
        final double[] r = new double[stacks.length];

        Integer[] ids = new Integer[stacks.length];
        for (int i = 0; i < ids.length; i++)
            ids[i] = i;

        Comparator<Integer> comp = (i1, i2) -> {
            if (r[i1] < r[i2])
                return 1;
            if (r[i1] > r[i2])
                return -1;
            return 0;
        };

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            for (int i = 0; i < r.length; i++)
                r[i] = Math.pow(random.nextDouble(), exp[i]);
            Arrays.sort(ids, comp);
            for (int i = 0; i < payouts.length; i++)
                equities[ids[i]] += payouts[i];
        }

        for (int i = 0; i < equities.length; i++)
            equities[i] /= maxIterations;

        return equities;
    }


    private static double[] getExponents(double[] stacks) {
        double t = 0;
        for (double s : stacks)
            t += s;
        t /= stacks.length;
        double[] exp = new double[stacks.length];
        for (int i = 0; i < stacks.length; i++)
            exp[i] = t / stacks[i];
        return exp;
    }
}
