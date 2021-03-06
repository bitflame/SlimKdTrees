

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;


import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PointInfoAggregator implements ArgumentsAggregator {
    /* I was trying to test private methods using Mocking following this
    https://roytuts.com/how-to-test-private-methods-using-junit-5/ tutorial. The next two lines
    belong to this attempt. But I need to learn more about it; it is not working b/c Node is
    also private in my case and the tutorial does not cover how to address that. */
    @InjectMocks
    private KdTree KdTreePrivateMethods = new KdTree();

    @Override
    public Point2D aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) {
        return new Point2D(arguments.getDouble(0),
                arguments.getDouble(1));
    }
}

class KdTreeParameterizedTest {

    private KdTree kt = new KdTree();
    private int increment = 5;
    RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);

    @Disabled
    @BeforeEach
    void setup() {
        Point2D p1 = new Point2D(0.5, 0.25);
//        kt.insert(p1);
//        Point2D p2 = new Point2D(0.0, 0.5);
//        kt.insert(p2);
//        Point2D p3 = new Point2D(0.5, 0.0);
//        kt.insert(p3);
//        Point2D p4 = new Point2D(0.25, 0.0);
//        kt.insert(p4);
//        Point2D p5 = new Point2D(0.0, 1.0);
//        kt.insert(p5);
//        Point2D p6 = new Point2D(1.0, 0.5);
//        kt.insert(p6);
//        Point2D p7 = new Point2D(0.25, 0.0);
//        kt.insert(p7);
//        Point2D p8 = new Point2D(0.0, 0.25);
//        kt.insert(p8);
//        Point2D p9 = new Point2D(0.25, 0.0);
//        kt.insert(p9);
//        Point2D p10 = new Point2D(0.25, 0.5);
//        kt.insert(p10);
        for (int i = 0; i < 10; i++) {
            p1 = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            kt.insert(p1);
        }

    }

    /* This is not exactly what I wanted to do, but it is a good example that I can follow as a
     * reference Here are some extra points if you need later: "0.5,0.25", "0.0,0.0", "0.5,0.0", "0.5,0.0", "0.25,0.0", "0.0,1.0", "1.0,0.5",
            "1.0,0.5", "0.25,0.0", "0.0,0.25", "0.25,0.0", "0.25,0.5",*/
    @Disabled
    @ParameterizedTest
    @CsvSource({"0.0,0.1", "0.1, 0.2", "0.2, 0.3", "0.3, 0.4", "0.4, 0.5", "0.5, 0.6", "0.6, 0.7", "0.7, 0.8"
            , "0.8, 0.9", "0.9,1.0"})
    void nearest(@AggregateWith(PointInfoAggregator.class) Point2D point1, @AggregateWith(PointInfoAggregator.class)
            Point2D point2) {
        for (int i = 0; i < 100; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            kt.insert(p);
        }
        RectHV r = new RectHV(point1.x(), point1.y(), point2.x(), point1.y());
        Point2D queryPoint = new Point2D(0.75, 0.75);
//        assertFalse(kt.contains(queryPoint));
//        assertFalse(queryPoint.equals(kt.nearest(queryPoint)));
//        kt.draw();
    }

    @ParameterizedTest
    @CsvSource({"0.0,0.1", "0.1, 0.2", "0.2, 0.3", "0.3, 0.4", "0.4, 0.5", "0.5, 0.6", "0.6, 0.7", "0.7, 0.8"
            , "0.8, 0.9", "0.9,1.0"})
    void rangeParameterizedTestWithControlledRectangleSize(@AggregateWith(PointInfoAggregator.class)
                                                                   Point2D point1,
                                                           @AggregateWith(PointInfoAggregator.class)
                                                                   Point2D point2) {
        for (int i = 0; i < 100; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            kt.insert(p);
        }
        /* I had to get creative with the values. The names below do not map to what should go into rectangle
         * but the values are correct and make the test work the way I want. */
        RectHV r = new RectHV(point1.x(), point2.x(), point1.y(), point2.y());
        //StdOut.println("Here are the points in rectangle " + r);
        StdOut.printf("%2s%6s%6s%6s%n", "xmin", "ymin", "xmax", "ymax");
        StdOut.printf("%.2f%5.2f%5.2f%5.2f%n",r.xmin(),r.ymin(),r.xmax(),r.ymax());
        for (Point2D p : kt.range(r)) {
            StdOut.println(p);
        }
    }

    @Disabled
    @Test
    void size() {
        //Point2D queryPoint = new Point2D(0.75, 0.75);
        assertEquals(kt.size(), 10);
        //assertFalse(kt.contains(queryPoint));
        //assertFalse(queryPoint.equals(kt.nearest(queryPoint)));
        //assertEquals(kt.nearest(queryPoint).distanceSquaredTo(queryPoint),0.125);
    }

    @Disabled
    @Test
    void KdTree_Should_Be_in_Order() throws NoSuchMethodException, SecurityException
            , IllegalAccessError, IllegalArgumentException, InvocationTargetException {
        /* This needs more know how since Node is private also. I am not sure how to address
         * that right now. I was following https://roytuts.com/how-to-test-private-methods-using-junit-5/
         */
        // Method mthod = KdTree.class.getDeclaredMethod("printCurrentLevel",
        // KdTree.Node.class, int.class);
    }

    @Disabled
    @RepeatedTest(5)
    void range_should_work() {
        rec = new RectHV(StdRandom.uniform(0.0, 0.5), StdRandom.uniform(0.0, 0.5),
                StdRandom.uniform(0.5, 1.0), StdRandom.uniform(0.5, 1.0));

        StdOut.printf("xmin:%.2f ymin:%5.2f xmax:%5.2f ymax:%5.2f%n ", rec.xmin(), rec.ymin(), rec.xmax(),
                rec.ymax());
        kt.range(rec);
        for (Point2D p : kt.range(rec)) {
            StdOut.println("Here are the points in the rectangle above: " + p);
        }
    }

    @Disabled
    @Test
    void range_should_catch_something() {
        rec = new RectHV(0.0, 0.0, 1.0, 1.0);
        StdOut.println("Here are the intersecting points ");
        for (Point2D p : kt.range(rec)) {
            StdOut.println("Here are the points in the rectangle above: " + p);
        }
    }

    @Disabled
    @Test
    void range_with_rec_of_zero_size() {
        rec = new RectHV(0.0, 0.0, 0.0, 0.0);
        kt.range(rec);
    }

    @Disabled
    @Test
    void range_with_rec_of_zero_width() {
        rec = new RectHV(0.0, 0.1, 0.0, 0.1);
        kt.range(rec);
    }

    @Disabled
    @Test
    void isEmpty() {
        assertFalse(kt.isEmpty());
    }

}