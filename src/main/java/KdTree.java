
import edu.princeton.cs.algs4.*;

import java.util.ArrayList;


public class KdTree {
    private Node root;
    private Queue<Node> q = new Queue<>();
    private Queue<Point2D> pq = new Queue<>();
    ArrayList<Point2D> points = new ArrayList<>();
    Stack<Node> intersectingRectangles = new Stack<>();
    RectHV rHl = null;
    RectHV rHr = null;

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right, parent; // subtrees
        int n = 0; // # nodes in this subtree
        boolean coordinate; // 0 means horizontal
        RectHV nodeRect = null;

        public Node(Point2D p, int n, boolean coordinate, Node parent) {
            this.p = p;
            this.coordinate = coordinate;
            this.parent = parent;
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
                    this.coordinate = true;
                    return -1;
                } else {
                    this.coordinate = true;
                    return 1;
                }
            }
            if (h.coordinate) {
                if (thisY < hY) {
                    this.coordinate = false;
                    return -1;
                } else {
                    this.coordinate = false;
                    return 1;
                }
            }
            return 1;
        }
    }

    public void draw() {
        RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);
        if (root == null) return;
        draw(root, rec);
    }

    private void draw(Node h, RectHV rectHV) {
        RectHV tempRect = null;
        if (h.coordinate == false) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.p.x(), rectHV.ymin(), h.p.x(), rectHV.ymax());
            if (h.left != null) {
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), h.p.x(), rectHV.ymax());
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(h.p.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
            }
        } else if (h.coordinate) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), h.p.y(), rectHV.xmax(), h.p.y());
            if (h.left != null) {
                // the sub rectangles are different depending on parent axis orientation
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), h.p.y());
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(rectHV.xmin(), h.p.y(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
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
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return range(root, rect);
    }

    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    private Iterable<Point2D> range(Node h, RectHV rect) {

        /*1- Does h intersect with rect? if no, do not check its subtrees it is over; you are done with it
         * 2- Does h cover all of the rect area?
         * 3- Do h's subtrees cover all the rect area also? if so, then move on to those */
        if (h.left == null) {
            if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
        }
        if (h.right == null) {
            if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
        }
        if (h.coordinate == false) {
            if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
            if (h.left != null) rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.p.x(),
                    h.nodeRect.ymax());
            if (h.right != null) rHr = new RectHV(h.p.x(), h.nodeRect.ymin(), h.nodeRect.xmax(),
                    h.nodeRect.ymax());
            if (rHl.intersects(rect) && h.left != null) {
                intersectingRectangles.push(h.left);
                h.left.parent = h;
                h.left.nodeRect = rHl;
                range(h.left, rect);
                    /* put the intersecting rectangles in a stack; later pull them out 1 by 1 until the entire
                    rectangle is covered */
            }
            if (rHr.intersects(rect) && h.right != null) {
                h.right.parent = h;
                h.right.nodeRect = rHr;
                intersectingRectangles.push(h.right);
                range(h.right, rect);
            }
        }
        if (h.coordinate) {  /// If h does not have a rectangle, recreate it. If it does use it.
            if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
            RectHV rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.p.x(), h.nodeRect.ymax());
            RectHV rHr = new RectHV(h.p.x(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.nodeRect.ymax());
            if (rHl.intersects(rect) && h.left == null) {
                if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
            }
            if (rHl.intersects(rect) && h.left != null) {
                intersectingRectangles.push(h.left);
                h.left.parent = h;
                h.left.nodeRect = rHl;
                range(h.left, rect);
                    /* put the intersecting rectangles in a stack; later pull them out 1 by 1 until the entire
                    rectangle is covered */
            }
            if (rHr.intersects(rect) && h.right == null) {
                if (rect.contains(h.p) && !points.contains(h.p)) points.add(h.p);
            }
            if (rHr.intersects(rect) && h.right != null) {
                h.right.parent = h;
                h.right.nodeRect = rHr;
                intersectingRectangles.push(h.right);
                range(h.right, rect);
            }
        }
//        double xminCounter = 1.0;
//        double yminCounter = 1.0;
//        double xmaxCounter = 0.0;
//        double ymaxCounter = 0.0;
        for (Node node : intersectingRectangles) {
            for (Node n : keys(node)) {
                if (rect.contains(n.p) && (!points.contains(n.p))) points.add(n.p);
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
                newNode.parent = h;
                h.left = insert(h.left, newNode);
            } else if (cmp > 0) {
                newNode.parent = h;
                h.right = insert(h.right, newNode);
            }
        }
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
        RectHV initialRec = new RectHV(0.0, 0.0, 1.0, 1.0);
        Point2D nearestNeig = root.p;
        return nearest(root, p, nearestNeig, initialRec);
    }

    private Point2D nearest(Node h, Point2D p, Point2D nearstP, RectHV rect) {
        RectHV rHl = null;
        RectHV rHr = null;
        if (h.coordinate == false) {
            if (h.parent == null) {
                rHl = new RectHV(0.0, 0.0, h.p.x(), 1.0);
                rHr = new RectHV(h.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
            } else if (h.parent != null) {
                // I have to rebuild the h rectangle here or save it in the node from previous round.
                rHl = new RectHV(rect.xmin(), rect.ymin(), h.p.x(), rect.ymax());
                rHr = new RectHV(h.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
            }
            if (h.left != null) {
                if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                    }
                }
                nearstP = nearest(h.left, p, nearstP, rHl);
            }
            if (h.right != null) {
                if (rect.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.right.p;
                    }
                }
            }
            nearstP = nearest(h.right, p, nearstP, rHr);
        }
        if (h.coordinate) {
            rHl = new RectHV(rect.xmin(), rect.ymin(), h.p.x(), rect.ymax());
            rHr = new RectHV(h.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
            if (h.left != null) {
                if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                    }
                }
                nearstP = nearest(h.left, p, nearstP, rHl);
            }
            if (h.right != null) {
                if (rect.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.right.p;
                    }
                }
            }
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
        // RectHV r = new RectHV(0.8, 0.5, 1.0, 0.7);
        RectHV r = new RectHV(0.1, 0.1, 0.8, 0.6);   // Just want to see the point 0.7, 0.2
        /* But this is what I get : [(0.2, 0.3), (0.5, 0.4)] */
        // RectHV r = new RectHV(0.0, 0.0, 1.0, 1.0);
        //RectHV r = new RectHV(0.7, 0.2, 1.0, 1.0);
        //StdOut.println("Does r contain the first node? " + r.contains(p1));
//        for (Point2D p : k.range(r)) {
//            StdOut.println("Here is the points in above rectangle: " + p);
//        }
        StdOut.println("Here is the point in your rectangle : " + k.range(r));
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

