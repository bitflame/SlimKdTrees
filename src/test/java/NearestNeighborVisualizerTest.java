import edu.princeton.cs.algs4.In;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class NearestNeighborVisualizerTest {
    final static File folder = new File("src\\main\\resources");
/* There are 17 files in above folder */
    @BeforeEach
    void setUp() {
        NearestNeighborVisualizer nnv = new NearestNeighborVisualizer();

        for (final File fileEntry : folder.listFiles()) {
            In in = new In(fileEntry.getAbsoluteFile());
        }
    }

    @AfterEach
    void tearDown() {
    }
}