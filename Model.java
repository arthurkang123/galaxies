package galaxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Formatter;
import java.util.Collections;
import java.util.Collection;


import static galaxy.Place.pl;
import static java.util.Arrays.asList;


/**
 * The state of a Galaxies Puzzle.  Each cell, cell edge, and intersection of
 * edges has coordinates (x, y). For cells, x and y are positive and odd.
 * For intersections, x and y are even.  For horizontal edges, x is odd and
 * y is even.  For vertical edges, x is even and y is odd.  On a board
 * with w columns and h rows of cells, (0, 0) indicates the bottom left
 * corner of the board, and (2w, 2h) indicates the upper right corner.
 * If (x, y) are the coordinates of a cell, then (x-1, y) is its left edge,
 * (x+1, y) its right edge, (x, y-1) its bottom edge, and (x, y+1) its
 * top edge.  The four cells (x, y), (x+2, y), (x, y+2), and (x+2, y+2)
 * meet at intersection (x+1, y+1).  Cells contain nonnegative integer
 * values, or "marks". A cell containing 0 is said to be unmarked.
 *
 * @author Ho Jong Kang
 */
class Model {

    /**
     * The default number of squares on a side of the board.
     */
    static final int DEFAULT_SIZE = 7;
    /**
     * w for width.
     */
    private int w;
    /**
     * h for height.
     */
    private int h;
    /**
     * Toggles the presence of a boundary at the edge (X, Y). That is, negates
     * the value of isBoundary(X, Y) (from true to false or vice-versa).
     * Requires that (X, Y) is an edge.
     */
    private Map<String, Boolean> iftoggle = new HashMap();
    /**
     * Places center at P.
     */
    private Map<String, Boolean> ifcenter = new HashMap();
    /**
     * Marks the cell at (X, Y) with value V. Requires that V must be greater
     * than or equal to 0, and that (X, Y) is a valid cell address.
     */

    private Map<String, Integer> info = new HashMap();

    /**
     * Initializes an empty puzzle board of size DEFAULT_SIZE x DEFAULT_SIZE,
     * with a boundary around the periphery.
     */
    Model() {
        init(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Initializes an empty puzzle board of size COLS x ROWS, with a boundary
     * around the periphery.
     */
    Model(int cols, int rows) {
        init(cols, rows);
    }

    /**
     * Initializes a copy of MODEL.
     */
    Model(Model model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Model model) {
        if (model == this) {
            return;
        } else {
            this.w = model.w;
            this.h = model.h;
            this.ifcenter.putAll(model.ifcenter);
            this.iftoggle.putAll(model.iftoggle);
            this.info.putAll(model.info);
        }
    }

    /**
     * Sets the puzzle board size to COLS x ROWS, and clears it.
     */
    void init(int cols, int rows) {
        this.w = cols;
        this.h = rows;
        this.info.clear();
        this.ifcenter.clear();
        this.iftoggle.clear();
    }

    /**
     * Clears the board (removes centers, boundaries that are not on the
     * periphery, and marked cells) without resizing.
     */
    void clear() {
        init(cols(), rows());
    }

    /**
     * Returns the number of columns of cells in the board.
     */
    int cols() {
        return xlim() / 2;
    }

    /**
     * Returns the number of rows of cells in the board.
     */
    int rows() {
        return ylim() / 2;
    }

    /**
     * Returns the number of vertical edges and cells in a row.
     */
    int xlim() {
        return this.w * 2 + 1;
    }

    /**
     * Returns the number of horizontal edges and cells in a column.
     */
    int ylim() {
        return this.h * 2 + 1;
    }

    /**
     * Returns true iff (X, Y) is a valid cell.
     */
    boolean isCell(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim()
                && x % 2 == 1 && y % 2 == 1;
    }

    /**
     * Returns true iff P is a valid cell.
     */
    boolean isCell(Place p) {
        return isCell(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a valid edge.
     */
    boolean isEdge(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim() && x % 2 != y % 2;
    }

    /**
     * Returns true iff P is a valid edge.
     */
    boolean isEdge(Place p) {
        return isEdge(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a vertical edge.
     */
    boolean isVert(int x, int y) {
        return isEdge(x, y) && x % 2 == 0;
    }

    /**
     * Returns true iff P is a vertical edge.
     */
    boolean isVert(Place p) {
        return isVert(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a horizontal edge.
     */
    boolean isHoriz(int x, int y) {
        return isEdge(x, y) && y % 2 == 0;
    }

    /**
     * Returns true iff P is a horizontal edge.
     */
    boolean isHoriz(Place p) {
        return isHoriz(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a valid intersection.
     */
    boolean isIntersection(int x, int y) {
        return x % 2 == 0 && y % 2 == 0
                && x >= 0 && y >= 0 && x < xlim() && y < ylim();
    }

    /**
     * Returns true iff P is a valid intersection.
     */
    boolean isIntersection(Place p) {
        return isIntersection(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a center.
     */
    boolean isCenter(int x, int y) {

        try {
            boolean temp = ifcenter.get(Integer.toString(x) + ","
                    + Integer.toString(y));
            return temp;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns true iff P is a center.
     */
    boolean isCenter(Place p) {
        return isCenter(p.x, p.y);
    }

    /**
     * Returns true iff (X, Y) is a boundary.
     */
    boolean isBoundary(int x, int y) {
        try {
            boolean temp = iftoggle.get(Integer.toString(x) + ","
                    + Integer.toString(y));
            return temp;
        } catch (NullPointerException e) {
            return x == 0 || x == xlim() - 1 || y == 0 || y == ylim() - 1;
        }
    }

    /**
     * Returns true iff P is a boundary.
     */
    boolean isBoundary(Place p) {
        return isBoundary(p.x, p.y);
    }

    /**
     * Returns true iff the puzzle board is solved, given the centers and
     * boundaries that are currently on the board.
     */
    boolean solved() {
        int total;
        total = 0;
        for (Place c : centers()) {
            HashSet<Place> r = findGalaxy(c);
            if (r == null) {
                return false;
            } else {
                total += r.size();
            }
        }
        return total == rows() * cols();
    }

    /**
     * Finds cells reachable from CELL and adds them to REGION.  Specifically,
     * it finds cells that are reachable using only vertical and horizontal
     * moves starting from CELL that do not cross any boundaries and
     * do not touch any cells that were initially in REGION. Requires
     * that CELL is a valid cell.
     */
    private void accreteRegion(Place cell, HashSet<Place> region) {
        assert isCell(cell);
        if (region.contains(cell)) {
            return;
        }
        region.add(cell);
        for (int i = 0; i < 4; i += 1) {
            int dx = (i % 2) * (2 * (i / 2) - 1),
                    dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
            if (!isBoundary(cell.move(dx, dy))) {
                region.add(cell);
                accreteRegion(cell.move(2 * dx, 2 * dy), region);
            }
        }
    }

    /**
     * Returns true iff REGION is a correctly formed galaxy. A correctly formed
     * galaxy has the following characteristics:
     * - is symmetric about CENTER,
     * - contains no interior boundaries, and
     * - contains no other centers.
     * Assumes that REGION is connected.
     */
    private boolean isGalaxy(Place center, HashSet<Place> region) {
        for (Place cell : region) {
            if (isCenter(cell) && (cell.x != center.x || cell.y != center.y)) {
                return false;
            }
            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1),
                        dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place boundary = cell.move(dx, dy),
                        nextCell = cell.move(2 * dx, 2 * dy);

                if (region.contains(nextCell) && isBoundary(boundary)) {
                    return false;
                }
            }

            if (!region.contains(opposing(center, cell))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the galaxy containing CENTER that has the following
     * characteristics:
     * - encloses CENTER completely,
     * - is symmetric about CENTER,
     * - is connected,
     * - contains no stray boundary edges, and
     * - contains no other centers aside from CENTER.
     * Otherwise, returns null. Requires that CENTER is not on the
     * periphery.
     */
    HashSet<Place> findGalaxy(Place center) {

        HashSet<Place> galaxy = new HashSet<>();

        ifcenter.put(Integer.toString(center.x) + ","
                + Integer.toString(center.y), true);

        if (isCell(center)) {
            accreteRegion(center, galaxy);
        } else if (isVert(center)) {
            for (Place temp : asList(center.move(-1, 0), center.move(1, 0))) {
                accreteRegion(temp, galaxy);
            }
        } else if (isHoriz(center)) {
            for (Place temp : asList(center.move(0, -1), center.move(0, 1))) {
                accreteRegion(temp, galaxy);
            }
        } else {
            List<Place> temp = new ArrayList<>();
            for (int i = -1; i < 3; i += 2) {
                for (int j = -1; j < 3; j += 2) {
                    temp.add(pl(center.x + i, center.y + j));
                }
            }
            for (Place x : temp) {
                accreteRegion(x, galaxy);
            }
        }
        if (isGalaxy(center, galaxy)) {
            return galaxy;
        } else {
            return null;
        }
    }

    /**
     * Returns the largest, unmarked region around CENTER with the
     * following characteristics:
     * - contains all cells touching CENTER,
     * - consists only of unmarked cells,
     * - is symmetric about CENTER, and
     * - is contiguous.
     * The method ignores boundaries and other centers on the current board.
     * If there is no such region, returns the empty set.
     */
    Set<Place> maxUnmarkedRegionhelper(Place center) {
        HashSet<Place> region = new HashSet<>();
        for (int i = -2; i < 3; i += 2) {
            Place hor = center.move(i, 0);
            Place ver = center.move(0, i);
            if (isCell(hor) && mark(hor) == 0) {
                region.add(hor);
                mark(hor, 1);
                if (isCell(hor.move(-2, 0)) && isCell(hor.move(2, 0))) {
                    region.addAll(maxUnmarkedRegionhelper(hor));
                }
            }
            if (isCell(ver) && mark(ver) == 0) {
                region.add(ver);
                mark(ver, 1);
                try {
                    if (isCell(ver.move(0, -2)) && isCell(ver.move(0, 2))) {
                        region.addAll(maxUnmarkedRegionhelper(ver));
                    }
                } catch (AssertionError e) {
                    continue;
                }
            }
        }
        return region;
    }

    /**
     * Returns the largest, unmarked region around CENTER with the
     * following characteristics:
     * - contains all cells touching CENTER,
     * - consists only of unmarked cells,
     * - is symmetric about CENTER, and
     * - is contiguous.
     * The method ignores boundaries and other centers on the current board.
     * If there is no such region, returns the empty set.
     */
    Set<Place> maxUnmarkedRegion(Place center) {
        HashSet<Place> region = new HashSet<>();
        region.addAll(maxUnmarkedRegionhelper(center));
        markAll(region, 0);
        HashSet<Place> result = new HashSet<>();

        for (Place elem : region) {
            try {
                if (mark(opposing(center, elem)) == 0) {
                    result.add(elem);
                }
            } catch (NullPointerException | AssertionError e) {
                continue;
            }
        }
        return result;
    }

    /**
     * Marks all properly formed galaxies with value V. Unmarks all cells that
     * are not contained in any of these galaxies. Requires that V is greater
     * than or equal to 0.
     */
    void markGalaxies(int v) {
        assert v >= 0;
        markAll(0);
        for (Place c : centers()) {
            HashSet<Place> region = findGalaxy(c);
            if (region != null) {
                markAll(region, v);
            }
        }
    }

    /**
     * Toggles boundary boolean value.
     * @param x is x coordinate.
     * @param y is y coordinate.
     */
    void toggleBoundary(int x, int y) {
        if (isEdge(x, y)) {
            if (isBoundary(x, y)) {
                iftoggle.put(Integer.toString(x) + ","
                        + Integer.toString(y), false);
            } else {
                iftoggle.put(Integer.toString(x) + ","
                        + Integer.toString(y), true);
            }
        }
    }

    /**
     * Places a center at (X, Y). Requires that X and Y are within bounds of
     * the board.
     */
    void placeCenter(int x, int y) {
        placeCenter(pl(x, y));
    }

    /**
     * Places a center at (X, Y). Requires that X and Y are within bounds of
     * the board.
     * @param p is for place.
     */
    void placeCenter(Place p) {
        ifcenter.put(Integer.toString(p.x) + ","
                + Integer.toString(p.y), true);
    }

    /**
     * Returns the current mark on cell (X, Y), or -1 if (X, Y) is not a valid
     * cell address.
     */
    int mark(int x, int y) {
        if (!isCell(x, y)) {
            return -1;
        }
        try {
            return info.get(Integer.toString(x) + ","
                    + Integer.toString(y));
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the current mark on cell P, or -1 if P is not a valid cell
     * address.
     */
    int mark(Place p) {
        return mark(p.x, p.y);
    }

    /**
     * Marks the points.
     * @param y is y coordinate.
     * @param v is the mark number.
     * @param x is x coordinate.
     */
    void mark(int x, int y, int v) {
        if (!isCell(x, y)) {
            throw new IllegalArgumentException("bad cell coordinates");
        }
        if (v < 0) {
            throw new IllegalArgumentException("bad mark value");
        }

        info.put(Integer.toString(x) + "," + Integer.toString(y), v);
    }

    /**
     * Marks the cell at P with value V. Requires that V must be greater
     * than or equal to 0, and that P is a valid cell address.
     */
    void mark(Place p, int v) {
        mark(p.x, p.y, v);
    }

    /**
     * Sets the marks of all cells in CELLS to V. Requires that V must be
     * greater than or equal to 0.
     */
    void markAll(Collection<Place> cells, int v) {
        assert v >= 0;
        for (Place elem : cells) {
            mark(elem, v);
        }
    }

    /**
     * Sets the marks of all cells to V. Requires that V must be greater than
     * or equal to 0.
     */
    void markAll(int v) {
        assert v >= 0;
        for (int x = 1; x < xlim(); x += 2) {
            for (int y = 1; y < ylim(); y += 2) {
                mark(x, y, v);
            }
        }
    }

    /**
     * Returns the position of the cell that is opposite P using P0 as the
     * center, or null if that is not a valid cell address.
     */
    Place opposing(Place p0, Place p) {
        int x = (p0.x - p.x) + p0.x;
        int y = (p0.y - p.y) + p0.y;
        try {
            if (isCell(pl(x, y))) {
                return pl(x, y);
            } else {
                return null;
            }
        } catch (NullPointerException | AssertionError e) {
            return null;
        }
    }

    /**
     * Returns a list of all cells "containing" PLACE if all of the cells are
     * unmarked. A cell, c, "contains" PLACE if
     * - c is PLACE itself,
     * - PLACE is a corner of c, or
     * - PLACE is an edge of c.
     * Otherwise, returns an empty list.
     */
    List<Place> unmarkedContaining(Place place) {
        if (isCell(place)) {
            if (mark(place) == 0) {
                return asList(place);
            }
        } else if (isVert(place)) {
            if (mark(place.move(-1, 0)) == 0 && mark(place.move(1, 0)) == 0) {
                return asList(place.move(-1, 0), place.move(1, 0));
            }
        } else if (isHoriz(place)) {
            if (mark(place.move(0, -1)) == 0 && mark(place.move(0, 1)) == 0) {
                return asList(place.move(0, -1), place.move(0, 1));
            }
        } else {
            List<Place> temp = new ArrayList<>();
            for (int i = -1; i < 3; i += 2) {
                for (int j = -1; j < 3; j += 2) {
                    if (mark(place.move(i, j)) == 0) {
                        temp.add(pl(place.x + i, place.y + j));
                    } else {
                        return Collections.emptyList();
                    }
                }
            }
            return temp;
        }
        return Collections.emptyList();
    }

    /**
     * Returns a list of all cells, c, such that:
     * - c is unmarked,
     * - The opposite cell from c relative to CENTER exists and
     * is unmarked, and
     * - c is vertically or horizontally adjacent to a cell in REGION.
     * CENTER and all cells in REGION must be valid cell positions.
     * Each cell appears at most once in the resulting list.
     */
    List<Place> unmarkedSymAdjacent(Place center, List<Place> region) {
        ArrayList<Place> result = new ArrayList<>();
        for (Place r : region) {
            assert isCell(r);
            for (int i = -2; i < 3; i += 2) {
                try {
                    Place temp = r.move(i, 0);
                    Place opp = opposing(center, temp);
                    if (mark(temp) == 0 && isCell(opp) && mark(opp) == 0) {
                        result.add(temp);
                    }
                } catch (NullPointerException | AssertionError e) {
                    continue;
                }
            }
            for (int i = -2; i < 3; i += 2) {
                try {
                    Place temp1 = r.move(0, i);
                    Place opp1 = opposing(center, temp1);
                    if (mark(temp1) == 0 && isCell(opp1) && mark(opp1) == 0) {
                        result.add(temp1);
                    }
                } catch (NullPointerException | AssertionError e) {
                    continue;
                }
            }

        }
        return result;
    }

    /**
     * Returns an unmodifiable view of the list of all centers.
     */
    List<Place> centers() {
        List<Place> placewithcenter = new ArrayList<>();
        for (String key : ifcenter.keySet()) {
            String[] xandy = key.split(",", 2);
            placewithcenter.add(pl(Integer.parseInt(xandy[0]),
                    Integer.parseInt(xandy[1])));
        }
        return placewithcenter;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        int w1 = xlim();
        int h1 = ylim();
        for (int y = h1 - 1; y >= 0; y -= 1) {
            for (int x = 0; x < w1; x += 1) {
                boolean cent = isCenter(x, y);
                boolean bound = isBoundary(x, y);
                if (isIntersection(x, y)) {
                    out.format(cent ? "o" : " ");
                } else if (isCell(x, y)) {
                    if (cent) {
                        out.format(mark(x, y) > 0 ? "O" : "o");
                    } else {
                        out.format(mark(x, y) > 0 ? "*" : " ");
                    }
                } else if (y % 2 == 0) {
                    if (cent) {
                        out.format(bound ? "O" : "o");
                    } else {
                        out.format(bound ? "=" : "-");
                    }
                } else if (cent) {
                    out.format(bound ? "O" : "o");
                } else {
                    out.format(bound ? "I" : "|");
                }
            }
            out.format("%n");
        }
        return out.toString();
    }

}
