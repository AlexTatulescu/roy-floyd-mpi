import mpi.*;

public class RoyFloydMpi {

    private static final int NR = 5;
    private static final int INF = 99999;

    public static void main(String[] args) {

        MPI.Init(args);


        int[][] finalGraph = new int[NR][NR];
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int graph[][] = {
                {0, 5, INF, 3, INF},
                {INF, 0, 6, INF, 4},
                {INF, INF, 0, 9, INF},
                {INF, INF, INF, 0, INF},
                {INF, INF, INF, 2, 0}
        };

        if (rank == 0) {
            MPI.COMM_WORLD.Bcast(graph, 0, NR * NR, MPI.INT, 0);

            for (int k = 0; k < NR; k++) {
                for (int j = 0; j < NR; j++) {
                    if (graph[0][k] + graph[k][j] < graph[0][j]) {
                        graph[0][j] = graph[0][k] + graph[k][j];
                    }
                }
            }

            for (int i = 0; i < NR; i++) {
                finalGraph[0][i] = finalGraph[0][i];
            }

        }
        if (rank != 0) {

            MPI.COMM_WORLD.Recv(graph, 0, NR, MPI.OBJECT, 0, 0);

            for (int k = 0; k < NR; k++) {
                for (int j = 0; j < NR; j++) {
                    if (graph[rank][k] + graph[k][j] < graph[rank][j]) {
                        graph[rank][j] = graph[rank][k] + graph[k][j];
                    }
                }
            }
            MPI.COMM_WORLD.Alltoall(graph, 0, NR, MPI.OBJECT, graph, 0, NR, MPI.OBJECT);
        }
        if (rank == 0) {

            for (int p = 1; p < size; p++) {
                MPI.COMM_WORLD.Recv(graph, 0, NR, MPI.OBJECT, p, 0);
                for (int j = p; j < graph.length; j++) {
                    for (int k = 0; k < graph.length; k++) {
                        finalGraph[j][k] = graph[j][k];
                    }
                }
            }

            for (int i = 0; i < NR; i++) {
                for (int j = 0; j < NR; j++) {
                    System.out.print(finalGraph[i][j] + "   ");
                }
            }
        }

        MPI.Finalize();
    }
}
