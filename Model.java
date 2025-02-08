package com.javarush.task.task35.task3513;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//Класс будет содержать игровую логику и хранить игровое поле
public class Model {
    private static final int FIELD_WIDTH = 4;
    private  Tile[][] gameTiles = new Tile[ FIELD_WIDTH][ FIELD_WIDTH];
    int score;    //должны хранить текущий счет
    int maxTile;  //максимальный вес плитки на игровом поле

    public Model() {
        resetGameTiles();
    }

    /*
      Метод addTile должен изменять значение случайной
     пустой плитки в массиве gameTiles на 2 или 4 с
     вероятностью 0.9 и 0.1 соответственно.
    */
    private void addTile() {
        if (!getEmptyTiles().isEmpty()) {
            int randomNumerIsEmtyTile = (int)(getEmptyTiles().size() * Math.random());
            getEmptyTiles().get(randomNumerIsEmtyTile).value = (Math.random() < 0.9 ? 2 : 4);
        }

    }

    // собирает из gameTiles Tile c value = 0;
    private List<Tile> getEmptyTiles() {
        List<Tile> listIsEmtyOfGameTile = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if(gameTiles[i][j].isEmpty()) {
                    listIsEmtyOfGameTile.add(gameTiles[i][j]);
                }
            }
        }
        return listIsEmtyOfGameTile;
    }
    /*
     resetGameTiles должен заполнять массив gameTiles новыми
     плитками и менять значение двух из них с помощью двух вызовов метода addTile.
    */
    public void resetGameTiles() {
        for (int i = 0; i< FIELD_WIDTH; i++) {
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
        int answer = 0;
        int insertPosition = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    answer++;
                }
                insertPosition++;
            }
        }
        return answer != 0;
    }

    /*
    Слияние плиток одного номинала, т.е.ряд {4, 4, 2, 0} становится рядом
    {8, 2, 0, 0}. Обрати внимание, что ряд {4, 4, 4, 4}
    превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.
    */

    private boolean mergeTiles(Tile[] tiles) {
        int mark =0;
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
                mark++;
            } else {
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0;
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }
        return mark !=0;
    }
  /*
   left, который будет для каждой строки массива gameTiles вызывать методы compressTiles
   и mergeTiles и добавлять одну плитку с помощью метода addTile в том случае, если это необходимо.
    4. Метод left не должен быть приватным, т.к. вызваться он будет, помимо прочего, из класса Controller.
   */
    public void left() {
        boolean isChange = false;
        for (int i =0 ; i < FIELD_WIDTH;i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChange = true;
            }
        }
        if (isChange) {
            addTile();
        }
    }
}


