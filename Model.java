package com.javarush.task.task35.task3513;

import java.util.*;

//Класс будет содержать игровую логику и хранить игровое поле
public class Model {
    private static final int FIELD_WIDTH = 4;
    private  Tile[][] gameTiles;
    int score;    //должны хранить текущий счет
    int maxTile;  //максимальный вес плитки на игровом поле
    private final Stack<Tile[][]>  previousStates = new Stack<>();    // предыдущее состояние матрицы
    private Stack<Integer> previousScores = new Stack<>();     // предыдущее состояние очков
    private boolean isSaveNeeded = true;   // флаг сигнализирует о том был ли вызван метод saveState(флаг меняется на false в этом методе)


    public Model() {
        resetGameTiles();
    }

    Tile[][] getGameTiles() {
        return gameTiles;
    }

    /*
      Метод addTile должен изменять значение случайной
     пустой плитки в массиве gameTiles на 2 или 4 с
     вероятностью 0.9 и 0.1 соответственно.
    */
    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size();
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;

        }
    }

    // собирает из gameTiles Tile c value = 0;
    private List<Tile> getEmptyTiles() {
        final List<Tile> list = new ArrayList<Tile>();
        for (Tile[] tileArray : gameTiles) {
            for (Tile t : tileArray)
                if (t.isEmpty()) {
                    list.add(t);
                }
        }
        return list;
    }
    /*
     resetGameTiles должен заполнять массив gameTiles новыми
     плитками и менять значение двух из них с помощью двух вызовов метода addTile.
    */
    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    /*
    Сжатие плиток, таким образом, чтобы все пустые плитки были справа,
    т.е.ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
    */

    private boolean compressTiles(Tile[] tiles) {
        int insertPosition = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    result = true;
                }
                insertPosition++;
            }
        }
        return result;
    }

    /*
    Слияние плиток одного номинала, т.е.ряд {4, 4, 2, 0} становится рядом
    {8, 2, 0, 0}. Обрати внимание, что ряд {4, 4, 4, 4}
    превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.
    */

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) {
                continue;
            }

            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) {
                int updatedValue = tiles[i].value * 2;
                if (updatedValue > maxTile) {
                    maxTile = updatedValue;
                }
                score += updatedValue;
                tilesList.addLast(new Tile(updatedValue));
                tiles[i + 1].value = 0;
                result = true;
            } else {
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0;
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }

        return result;
    }
  /*
   left, который будет для каждой строки массива gameTiles вызывать методы compressTiles
   и mergeTiles и добавлять одну плитку с помощью метода addTile в том случае, если это необходимо.
    4. Метод left не должен быть приватным, т.к. вызваться он будет, помимо прочего, из класса Controller.
   */
    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;  // меняем флаг в false для будующих сохранений
    }

    /* поверенем против часовой, выполним compress & merge через метод left()
    вернем назад повернув по часовой*/

    public void up () {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
    }

     /* поверенем против часовой два раза, выполним compress & merge через метод left()
    вернем назад повернув по часовой*/

    public  void right() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    /* поверенем по часовой, выполним compress & merge через метод left()
вернем назад повернув против часовой*/
    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    /** Поворачиваем матрицу
     * @param clock  направление поворота: по часовой
     *               стрелке ↻ или против часовой стрелки ↺
     * @param matrix исходная матрица
     * @return повёрнутая матрица
     */
    public Tile[][] rotateMatrix( boolean clock, Tile[][] matrix) {
        // новая матрица, количества строк и колонок меняются местами
        Tile[][] rotated = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        // обходим строки исходной матрицы
        for (int i = 0; i < FIELD_WIDTH; i++)
            // обходим колонки исходной матрицы
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (clock) // поворот по часовой стрелке ↻
                    rotated[j][i] = matrix[FIELD_WIDTH-i-1][j];
                else // поворот против часовой стрелки ↺
                    rotated[j][i] = matrix[i][FIELD_WIDTH-j-1];
        return rotated;
    }

    private Tile[][] rotateClockwise(Tile[][] tiles) {
        final int N = tiles.length;
        Tile[][] result = new Tile[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                result[c][N - 1 - r] = tiles[r][c];
            }
        }
        return result;
    }

    /*
    * метод canMove возвращающий true в случае, если в текущей позиции
    * возможно сделать ход так, чтобы состояние игрового поля изменилось. Иначе - false.
    */
    public boolean canMove () {
        boolean result = false;
        if(!getEmptyTiles().isEmpty()) {return true;} // проверка на содержание 0 в матрице

        // проверка на возможность компрессии и слияния матрицы и перевернутой матрицы
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                Tile t = gameTiles[i][j];
                if ((i < FIELD_WIDTH - 1 && t.value == gameTiles[i + 1][j].value)
                        || ((j < FIELD_WIDTH - 1) && t.value == gameTiles[i][j + 1].value)) {
                    return true;
                }
            }
        }
        return result;
    }
     // сохранение сотояния матрицы и score в Stack для испльзования в rollback
    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    //метод rollback будет устанавливать текущее игровое состояние равным
    // последнему находящемуся в стеках с помощью метода pop()

    public void rollback() {
        if (!previousStates.isEmpty()&&!previousScores.isEmpty()) {
            gameTiles =previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {

        int n = ((int) (Math.random() * 100)) % 4;

        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }
    // проверяем количество пустых клеток в существующем состоянии матрицы(gameTiles)
    // и сравниваем с тем же параметром предыдущего хода
    public boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousStates.peek()[i][j].value) {
                    return true;
                }
            }
        }
        return false;
    }

    private  MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTilesCount(), score, move);
        }
        rollback();
        return moveEfficiency;// в случае, если ход не меняет состояние игрового поля,
                                                                         // количество пустых плиток(numberOfEmptyTiles)
                                                                        // и счет(score) у объекта MoveEfficiency
                                                                       // сделай равными -1 и 0 соответственно.
    }
    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }
    // передаем абстрактный класс реализующий метод move() через funcInterf
    // можно так getMoveEfficiency(() -> left())
    // либо так getMoveEfficiency(new Move() {  @Override public void move() {left();}})
    void autoMove() {

        PriorityQueue<MoveEfficiency> moveEfficiencies = new PriorityQueue<>(4, Collections.reverseOrder());
        moveEfficiencies.offer(getMoveEfficiency(this::left));
        moveEfficiencies.offer(getMoveEfficiency(this::up));
        moveEfficiencies.offer(getMoveEfficiency(this::right));
        moveEfficiencies.offer(getMoveEfficiency(this::down));

        moveEfficiencies.peek().getMove().move();

    }
}


