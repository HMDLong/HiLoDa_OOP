package graphEngine.algos;

import graphEngine.graph.DirectedGraph;
import graphEngine.utils.EdgeRecord;
import javafx.scene.paint.Color;
import sample.EdgeGraph;

import java.util.*;

public class Kruskal extends AbstractAlgo{
    private int mstWeight = 0;

    static class Subsets{
        int parent,rank;
    }

    int find(Subsets[] subsets, int i) {
        // find root and make root as parent of i
        if (subsets[i].parent != i) {
            subsets[i].parent = find(subsets,subsets[i].parent);
        }
        return subsets[i].parent;
    }

    // A function that does union of two sets
    void Union(Subsets[] subsets, int x, int y) {
        int xroot = find(subsets, x);
        int yroot = find(subsets, y);

        // Attach smaller rank tree under root of high rank tree (Union by Rank)
        if (subsets[xroot].rank < subsets[yroot].rank)
            subsets[xroot].parent = yroot;
        else if (subsets[xroot].rank > subsets[yroot].rank)
            subsets[yroot].parent = xroot;
        else {
            subsets[yroot].parent = xroot;
            (subsets[xroot].rank)++;
            //same rank --> take 1 as root --> increase rank.
        }
    }

    @Override
    public List<EdgeGraph> init(){
        List<EdgeGraph> resultEdges = new ArrayList<>();
        // graph check
        if(graph instanceof DirectedGraph) return null;
        // initialize the parent tables: using Disjoint-set / Set-Union algo to detect cycle
        Subsets[] subsets = new Subsets[graph.getVerticesSize()];
        // initialize the edges priority queue
        PriorityQueue<EdgeRecord> queue = new PriorityQueue<>();
        for (Map.Entry<Integer, TreeMap<Integer, Integer>> src : graph.getAdjacentMap().entrySet())
            for (Map.Entry<Integer, Integer> dest : src.getValue().entrySet()) {
                EdgeRecord edge = new EdgeRecord(src.getKey(), dest.getKey(), dest.getValue());
                if (queue.contains(edge)) edge = null;
                else queue.add(edge);
            }
        // Create V subsets with single elements
        for (int v = 0; v < graph.getVerticesSize(); ++v) {
            subsets[v] = new Subsets(); //need to be init
            subsets[v].parent = v;
            subsets[v].rank = 0;
        }
        // start algorithm
        for (int i = 0; i < graph.getVerticesSize(); i++) {
            EdgeRecord next_edge = queue.poll();
            int x = find(subsets, next_edge.v1);
            int y = find(subsets, next_edge.v2);

            //No cycle --> add
            if (x != y) {
                Union(subsets, x, y);
                this.mstWeight += next_edge.weight;

                String begin = String.valueOf(next_edge.v1);
                String end = String.valueOf(next_edge.v2);
                for (EdgeGraph eg : this.edgefx) {
                    if ((eg.s1.name.equals(begin) && eg.s2.name.equals(end)) ||
                            (eg.s1.name.equals(end) && eg.s2.name.equals(begin))) {
                        resultEdges.add(eg);
                    }
                }
            }
        }
        return resultEdges;
    }

    @Override
    public String resultToString(){
        return "Kruskal MST weight = " + this.mstWeight;
    }
}

