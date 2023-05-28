package pt.up.fe.comp2023.optimization;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

class Graph {
    private int V;
    private LinkedList<Integer>[] adj;

    Graph(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList<>();
    }

    void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
    }

    void GraphPaint(int k) {
        int[] colors = new int[V];
        boolean[] available = new boolean[k];

        Arrays.fill(colors, -1);
        Stack<Integer> stack = new Stack<>();

        for (int v = 0; v < V; v++) {

            if (adj[v].size() < k) {

                stack.push(v);
                adj[v].clear();


                while (!stack.isEmpty()) {
                    int node = stack.pop();

                    for (int neighbor : adj[node]) {
                        if (adj[neighbor].size() > 0) {
                            adj[neighbor].removeFirstOccurrence(node);
                            if (adj[neighbor].size() < k)
                                stack.push(neighbor);
                        }
                    }
                }
            } else {
                System.out.println("The algorithm cannot be applied.");
                return;
            }
        }


        while (!stack.isEmpty()) {
            int node = stack.pop();
            Arrays.fill(available, true);

            for (int neighbor : adj[node]) {
                if (colors[neighbor] != -1)
                    available[colors[neighbor]] = false;
            }


            for (int i = 0; i < k; i++) {
                if (available[i]) {
                    colors[node] = i;
                    break;
                }
            }
        }

        for (int v = 0; v < V; v++) {
            System.out.println("Node " + v + " --> Color " + colors[v]);
        }
    }
}
