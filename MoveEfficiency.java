package com.javarush.task.task35.task3513;

// класс описывает эффективность хода
public class MoveEfficiency implements Comparable<MoveEfficiency>{
    private int numberOfEmptyTiles;  // количество пустых клеток
    private int score;               // количество очков
    private  Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }
    // сравнивает последовательно кол-во пустых клеток(numberOfEmptyTiles)
    // и score для оценки качества хода
    @Override
    public int compareTo(MoveEfficiency o) {
        if (numberOfEmptyTiles > o.numberOfEmptyTiles) {
            return 1;
        } else if (numberOfEmptyTiles < o.numberOfEmptyTiles) {
            return -1;
        } else {
            if (score > o.score) {
                return 1;
            } else if (score < o.score) {
                return -1;
            }
        }
        return 0;
    }
}
