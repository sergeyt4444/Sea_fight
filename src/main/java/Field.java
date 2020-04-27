public class Field {
    public int[][] matrix;

    public Field() {
        matrix = new int[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                matrix[i][j] = 0;
    }

    public void Default_Init() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++)
                matrix[i][j] = 0;
        }
        matrix[0][0] = 1;
        matrix[0][1] = 1;
        matrix[0][2] = 1;
        matrix[0][3] = 1;
        matrix[0][6] = 1;
        matrix[0][9] = 1;
        matrix[1][6] = 1;
        matrix[2][9] = 1;
        matrix[3][0] = 1;
        matrix[3][9] = 1;
        matrix[4][0] = 1;
        matrix[4][3] = 1;
        matrix[4][9] = 1;
        matrix[5][0] = 1;
        matrix[5][6] = 1;
        matrix[7][4] = 1;
        matrix[9][2] = 1;
        matrix[9][3] = 1;
        matrix[9][7] = 1;
        matrix[9][8] = 1;

    }
}
