
import edu.princeton.cs.algs4.*;

import java.util.ArrayList;


public class KdTree {
    private Node root;
    private Queue<Node> q = new Queue<>();
    private Queue<Point2D> pq = new Queue<>();
    ArrayList<Point2D> points = new ArrayList<>();

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right, parent; // subtrees
        int n = 0; // # nodes in this subtree
        boolean coordinate; // 0 means horizontal
        private RectHV rect; // the axis-aligned rectangle corresponding to this node


        public Node(Point2D p, int n, boolean coordinate, Node parent) {
            this.p = p;
            this.coordinate = coordinate;
            this.parent = parent;
            this.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
            this.n = n;
        }

        /* Check to make sure this method checks the coordinate of what is already on the org.example.KDTree with
        the new node not the other way around */
        @Override
        public int compareTo(Node h) {
            double thisX = this.p.x();
            double thisY = this.p.y();
            double hX = h.p.x();
            double hY = h.p.y();
            if (h.coordinate == false) {
                if (thisX < hX) {
                    // this.x to 1.0, 0.0 to h.p.y()
                    // h.rect = new RectHV(thisX, 0.0, 1.0, 1.0);
                    this.coordinate = true;
                    return -1;
                } else {
                    // 0.0 to this.p.x(), 0.0 to 1.0
                    // h.rect = new RectHV(0.0, 0.0, thisX, 1.0);
                    this.coordinate = true;
                    return 1;
                }
            }
            if (h.coordinate) {
                if (thisY < hY) {
                    // 0.0, this.p.y(), h.p.x(), 1.0
                    // h.rect = new RectHV(0.0, thisY, 1.0, 1.0);
                    this.coordinate = false;
                    return -1;
                } else {
                    // 0.0, 0.0 to h.p.x(), this.p.y()
                    //h.rect = new RectHV(0.0, 0.0, 1.0, thisY);
                    this.coordinate = false;
                    return 1;
                }
            }
            return 1;
        }
    }

    public void draw() {
        RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);
//        StdDraw.setPenRadius(0.012);
//        for (Node n : this.keys()) {
//            double nX = n.p.x();
//            double nY = n.p.y();
//            if (isHorizontal(n)) {
//                StdDraw.setPenColor(StdDraw.BLACK);
//                StdDraw.setPenRadius(0.012);
//                StdDraw.point(n.p.x(), n.p.y());
//                StdDraw.setPenRadius(0.003);
//                StdDraw.setPenColor(StdDraw.RED);
//                StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
//            } else if (isVertical(n)) {
//                StdDraw.setPenColor(StdDraw.BLACK);
//                StdDraw.setPenRadius(0.012);
//                StdDraw.point(n.p.x(), n.p.y());
//                StdDraw.setPenRadius(0.003);
//                StdDraw.setPenColor(StdDraw.BLUE);
//                StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
//            }
//        }
        if (root==null) return;
        draw(root, rec);
    }

    private void draw(Node h, RectHV rectHV) {
        if (h.coordinate == false) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.RED);
            // StdDraw.line(h.p.x(), rectHV.ymin(), h.p.x(), rectHV.ymax());
            StdDraw.line(h.p.x(), h.rect.ymin(), h.p.x(), h.rect.ymax());
            /* if h is less than its parent y goes from 0 to h.parent.y , if h is greater than its parent, y goes
            * from h.parent.y to 1.0 */
            if (h.left != null) {
                h.left.rect = new RectHV(rectHV.xmin(), rectHV.ymin(), h.p.x(), rectHV.ymax());
                draw(h.left, h.left.rect);
            }
            if (h.right != null) {
                h.right.rect = new RectHV(h.p.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, h.right.rect);
            }
        } else if (h.coordinate) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), h.p.y(), rectHV.xmax(), h.p.y());
            if (h.left!=null) {
                // the sub rectangles are different depending on parent axis orientation
            }
            if (h.right!=null) {

            }
        }

    }

    private Point2D get(Point2D p) {
        return get(root, p);
    }

    public boolean isEmpty() {
        return keys() == null;
    }

    private Point2D get(Node h, Point2D p) {
        if (h == null) return null;
        int cmp = p.compareTo(h.p);
        if (cmp < 0) return get(h.left, h.p);
        else if (cmp > 0) return get(h.right, h.p);
        else return h.p;
    }

    private Iterable<Node> keys() {
        q = new Queue<>();
        return keys(root);
    }

    private Queue<Node> keys(Node h) {
        if (h == null) return null;
        if (h != null) q.enqueue(h);
        if (h.left != null) {
            keys(h.left);
        }
        if (h.right != null) {
            keys(h.right);
        }
        return q;
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("rectangle has to be a valid " +
                "object. ");
        return range(root, rect);
    }

    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    private Iterable<Point2D> range(Node h, RectHV rect) {
        // If h is horizontal and h.right() or h.left()
        if (h.coordinate == false) {
            RectHV rHl = new RectHV(0.0, 0.0, h.p.x(), 1.0);
            if (rHl.intersects(rect) && h.left != null) {
                range(h.left, rect);
            } else if (h.left == null) {
                for (Node n : keys(h)) {
                    if (rect.contains(n.p) && (!points.contains(n.p))) points.add(n.p);
                }
            }
            RectHV rHr = new RectHV(h.p.x(), 0.0, 1.0, 1.0);//<------
            if (rHr.intersects(rect) && h.right != null) {
                range(h.right, rect);
            } else if (h.right == null) {
                for (Node n : keys(h)) {
                    if (rect.contains(n.p) && (!points.contains(n.p))) points.add(n.p);
                }
            }
        } else if (h.coordinate) {
            RectHV rHl = new RectHV(0.0, 0.0, 1.0, h.p.y());
            if (rHl.intersects(rect) && h.left != null) {
                range(h.left, rect);
            } else if (h.left == null) {
                for (Node n : keys(h)) {
                    if (rect.contains(n.p) && (!points.contains(n.p))) points.add(n.p);
                }
            }
            RectHV rHr = new RectHV(0.0, h.p.y(), 1.0, 1.0);
            if (rHr.intersects(rect) && h.right != null) {
                range(h.right, rect);
            } else if (h.right == null) {
                for (Node n : keys(h)) {
                    if (rect.contains(n.p) && (!points.contains(n.p))) points.add(n.p);
                }
            }
        }
        return points;
    }

    private static boolean isHorizontal(Node x) {
        if (x == null) return false;
        return x.coordinate == false;
    }

    private static boolean isVertical(Node x) {
        if (x == null) return false;
        return x.coordinate == true;
    }

    private void makeVertical(Node x) {
        if (x == null) return;
        x.coordinate = true;
    }

    private void makeHorizontal(Node x) {
        if (x == null) return;
        x.coordinate = false;
    }


    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("You can not insert null object" +
                "into the tree");
        Node newNode = new Node(p, 1, false, null);
        root = insert(root, newNode);
    }

    private Node insert(Node h, Node newNode) {
        if (h == null) {
            return newNode;
        } else {
            int cmp = newNode.compareTo(h);
            if (cmp < 0) {
                h.left = insert(h.left, newNode);
            } else if (cmp > 0) {
                h.right = insert(h.right, newNode);
            }
        }
        /* Create a new node here with p, and use Node compareTo() to add it to
         * the right branch something like:
         * if h.coordinate == false and newNode
        if (isHorizontal(h) && p.x() < h.p.x()) {
            h.left = insert(h.left, p);
            h.left.rect = new RectHV(h.rect.xmin(), h.rect.ymin(), h.p.x(), h.rect.ymax());
            h.left.parent = h;
            makeVertical(h.left);
        } else if (isHorizontal(h) && p.x() >= h.p.x()) {
            h.right = insert(h.right, p);
            h.right.rect = new RectHV(h.p.x(), h.rect.ymin(), h.rect.xmax(), h.rect.ymax());
            h.right.parent = h;
            makeVertical(h.right);
        } else if (isVertical(h) && p.y() < h.p.y()) {
            h.left = insert(h.left, p);
            h.left.rect = new RectHV(h.rect.xmin(), h.rect.ymin(), h.rect.xmax(), h.p.y());
            h.left.parent = h;
            makeHorizontal(h.left);
        } else if (isVertical(h) && p.y() >= h.p.y()) {
            h.right = insert(h.right, p);
            h.right.rect = new RectHV(h.rect.xmin(), h.p.y(), h.rect.xmax(), h.rect.ymax());
            h.right.parent = h;
            makeHorizontal(h.right);
        }*/
        int leftN = 0;
        if (h.left != null) {
            leftN = h.left.n;
        }
        int rightN = 0;
        if (h.right != null) {
            rightN = h.right.n;
        }
        h.n = leftN + rightN + 1;
        return h;
    }

    public int size() {
        if (root == null) return 0;
        else return root.n;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) throw new IllegalArgumentException("The tree is empty.");
        /* if the closest point discovered so far is closer than the distance between the query point and the rectangle
        corresponding to a node, there is no need to explore that node (or its subtrees). */
        Point2D nearestNeig = root.p;
        return nearest(root, p, nearestNeig);
    }

    private Point2D nearest(Node n, Point2D p, Point2D nearstP) {
        if (n.left != null) {
            if (n.left.rect.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                if (n.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                    nearstP = n.left.p;
                }
            }
            nearstP = nearest(n.left, p, nearstP);
        }
        if (n.right != null) {
            if (n.right.rect.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                if (n.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                    nearstP = n.right.p;
                }
            }
            nearstP = nearest(n.right, p, nearstP);
        }
        return nearstP;
    }

    public static void main(String[] args) {
//        String filename = args[0];
//        In in = new In(filename);
//        PointSET brute = new PointSET();
//        KdTree kdtree = new KdTree();
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree.insert(p);
//            brute.insert(p);
//        }
//        StdOut.println("Should be 10 " + kdtree.size());
//        StdOut.println("Should be 10 " + brute.size());
//        StdOut.println("Should be false " + kdtree.isEmpty());
//        StdOut.println("Should be false " + brute.isEmpty());
//        kdtree.draw();

//        KdTree kt = new KdTree();
//        Point2D p1 = new Point2D(0.5, 0.25);
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
        //Point2D queryPoint = new Point2D(0.75, 0.75);
        //kt.draw();
        // StdOut.println("Distance Squared to Query Point: " + kt.nearest(queryPoint).distanceSquaredTo(queryPoint));
        // StdOut.println(kt.nearest(queryPoint));
//        StdOut.println("Changed something for testing.");
        KdTree k = new KdTree();
        Queue<Point2D> s = new Queue<>();
        Point2D p1 = new Point2D(0.7, 0.2);
        s.enqueue(p1);
        Point2D p2 = new Point2D(0.5, 0.4);
        s.enqueue(p2);
        Point2D p3 = new Point2D(0.2, 0.3);
        s.enqueue(p3);
        Point2D p4 = new Point2D(0.4, 0.7);
        s.enqueue(p4);
        Point2D p5 = new Point2D(0.9, 0.6);
        s.enqueue(p5);
        for (Point2D p : s) {
            k.insert(p);
        }
//        RectHV r = new RectHV(0.4, 0.3, 0.6, 0.7);
//        StdOut.println("Here is the point in your rectangle : " + k.range(r));
        k.draw();
//        for (int i = 0; i < 20; i++) {
//            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
//            k.insert(p);
//        }
//        StdOut.println("Finished w/o errors.");
//        int index = 1;
//        for (Node n : k.keys()) {
//            if (n.coordinate == true) {
//                StdOut.println(index + "-" + n.p);
//                index++;
//            }
//        }
    }
}

