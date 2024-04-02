import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class MedievalBattleGame {
    private static final int FIELD_SIZE = 15;
    private static final char NORMAL_TILE = '*';
    private static final char SWAMP_TILE = '@';
    private static final char HILL_TILE = '#';
    private static final char TREE_TILE = '♤';

    private static char[][] battlefield = new char[FIELD_SIZE][FIELD_SIZE];
    private static Random random = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Player> playerUnits = new ArrayList<>();
        List<Player> botUnits = new ArrayList<>();
        String[] unitNames = {"Мечники", "Копьеносцы", "Топорщики", "Лучники с длинным луком", "Лучники с коротким луком", "Арбалетчики", "Рыцари", "Кирасиры", "Конные лучники"};
        int[] unitCosts = {10, 15, 20, 15, 19, 23, 20, 23, 25};

        Start start=new Start();
        start.starttextgame();
        start.histori();

        int balance = 100;
        while (balance >= 10) {
            System.out.println("Ваша казна: " + balance + " золота. Выберите войска для закупки:");
            for (int i = 0; i < unitNames.length; i++) {
                System.out.println((i + 1) + ". " + unitNames[i] + " (стоимость: " + unitCosts[i] + " золота)");
            }
            System.out.println("0. Завершить закупку войск");

            int choice = scanner.nextInt();
            if (choice == 0) {
                break;
            } else if (choice >= 1 && choice <= unitNames.length) {
                int unitIndex = choice - 1;
                int unitCost = unitCosts[unitIndex];
                if (balance >= unitCost) {
                    balance -= unitCost;
                    boolean placed = false;
                    while (!placed) {
                        int randomX = random.nextInt(2);
                        int randomY = random.nextInt(FIELD_SIZE);
                        if (isPositionFree(randomX, randomY, playerUnits, botUnits)) {
                            switch (choice) {
                                case 1:
                                    playerUnits.add(new Swordsman(randomX, randomY));
                                    break;
                                case 2:
                                    playerUnits.add(new Spearman(randomX, randomY));
                                    break;
                                case 3:
                                    playerUnits.add(new Axeman(randomX, randomY));
                                    break;
                                case 4:
                                    playerUnits.add(new Longbowman(randomX, randomY));
                                    break;
                                case 5:
                                    playerUnits.add(new Shortbowman(randomX, randomY));
                                    break;
                                case 6:
                                    playerUnits.add(new Crossbowman(randomX, randomY));
                                    break;
                                case 7:
                                    playerUnits.add(new Knight(randomX, randomY));
                                    break;
                                case 8:
                                    playerUnits.add(new Hussar(randomX, randomY));
                                    break;
                                case 9:
                                    playerUnits.add(new HorseArcher(randomX, randomY));
                                    break;
                            }
                            placed = true;
                        }
                    }
                } else {
                    System.out.println("Нужно больше золота.");
                }
            } else {
                System.out.println("Комманда не распознана, повторите ввод.");
            }
        }



        // Генерация юнитов бота
        createBotUnits(botUnits, playerUnits);

        initializeBattlefield();
        updateBattlefield(null, playerUnits, botUnits);
        printBattlefield(null, playerUnits, botUnits);

        while (!playerUnits.isEmpty() && !botUnits.isEmpty()) {
            for (Player unit : playerUnits) {
                if (!unit.isAlive()) continue;

                System.out.println("Здоровье выбранного юнита:              " + unit.health);
                System.out.println("Атака выбранного юнита:                 " + unit.attackDamage);
                System.out.println("Возможная дистанция выбранного юнита:   " + unit.attackRange);
                System.out.println("Защита выбранного юнита:                " + unit.defense);
                System.out.println("Возможное перемещение выбранного юнита: " + unit.movement);

                System.out.println("Ход игрока. Выберите действие для юнита " + unit.getClass().getSimpleName() + " (x= " + unit.x + ", y = " + unit.y + "):");
                System.out.println("1. Атаковать");
                System.out.println("2. Переместиться");

                int action = scanner.nextInt();
                if (action == 1) {
                    attackRandomPlayer(unit, botUnits);
                    if (botUnits.isEmpty()) {
                        System.out.println("Победа! Победа! Время обеда.");
                        return;
                    }
                } else if (action == 2) {
                    boolean MovM = false;
                    while (MovM != true) {
                        System.out.println("Введите координаты хода (x, y):");
                        int newX = scanner.nextInt();
                        int newY = scanner.nextInt();
                        if(unit.checkMove(newX, newY) && isPositionFree(newX, newY, playerUnits, botUnits)) {
                            MovM = true;
                            unit.move(newX, newY);
                        }else {
                            System.out.println("Для особо умных: юнит не может переместиться на данную локацию");
                        }
                    }
                } else {
                    System.out.println("Ну все, облажался, пропускай ход :D");
                }

                updateBattlefield(unit, playerUnits, botUnits);
                printBattlefield(unit, playerUnits, botUnits);
            }

            for (Player unit : botUnits) {
                if (!unit.isAlive()) continue;
                if (canAttack(unit, playerUnits)) {
                    attackRandomPlayer(unit, playerUnits);
                    if (playerUnits.isEmpty()) {
                        System.out.println("Королевство потеряно. Земли захвачены врагом.");
                        return;
                    }
                } else {
                    unit.move(unit.x - 1, unit.y);
                }
                updateBattlefield(null, playerUnits, botUnits);
            }
            printBattlefield(null, playerUnits, botUnits);
        }
    }

    private static Player randomBotUnit(int x, int y) {
        Random random = new Random();
        int choice = random.nextInt(9) + 1; // Случайный выбор типа юнита бота

        // Создаем и возвращаем экземпляр соответствующего типа юнита бота
        switch (choice) {
            case 1:
                return new Swordsman(x, y);
            case 2:
                return new Spearman(x, y);
            case 3:
                return new Axeman(x, y);
            case 4:
                return new Longbowman(x, y);
            case 5:
                return new Shortbowman(x, y);
            case 6:
                return new Crossbowman(x, y);
            case 7:
                return new Knight(x, y);
            case 8:
                return new Hussar(x, y);
            case 9:
                return new HorseArcher(x, y);
            default:
                // В случае ошибки ставим мечника (стандарт)
                return new Swordsman(x, y);
        }
    }

    private static void createBotUnits(List<Player> botUnits, List<Player> playerUnits) {
        Random random = new Random();

        while (botUnits.size() < playerUnits.size()) { // Желаемое количество юнитов бота
            int randomX = FIELD_SIZE-1-random.nextInt(2); // Случайные координаты X (0 или 1) ( начала поля боя)
            int randomY = random.nextInt(FIELD_SIZE); // Случайные координаты Y

            // Проверяем, свободна ли позиция для размещения юнита бота
            if (isPositionFree(randomX, randomY, playerUnits, botUnits)) {
                // Случайным образом выбираем тип юнита бота и добавляем его в список
                botUnits.add(randomBotUnit(randomX, randomY));
            }
        }
    }

    private static void initializeBattlefield() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                int rand = random.nextInt(15);
                if (rand < 2) {
                    battlefield[i][j] = SWAMP_TILE;
                } else if (rand < 4) {
                    battlefield[i][j] = HILL_TILE;
                } else if (rand < 6) {
                    battlefield[i][j] = TREE_TILE;
                } else {
                    battlefield[i][j] = NORMAL_TILE;
                }
            }
        }
    }

    private static void updateBattlefield(Player selectedPlayer, List<Player> playerUnits, List<Player> botUnits) {
        char botUnitSymbol = 'a'; // Буква для обозначения юнита противника

        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (battlefield[i][j] != NORMAL_TILE && battlefield[i][j] != SWAMP_TILE && battlefield[i][j] != HILL_TILE && battlefield[i][j] != TREE_TILE) {
                    battlefield[i][j] = NORMAL_TILE; // Сброс препятствий к обычным клеткам
                }
            }
        }

        for (Player unit : playerUnits) {// 1 2 3 4
            char symbol = getSymbolForPlayerUnit(unit);
            if (unit.x >= 0 && unit.x < FIELD_SIZE && unit.y >= 0 && unit.y < FIELD_SIZE) {
                battlefield[unit.x][unit.y] = symbol;
            }
        }

        for (Player unit : botUnits) {
            char symbol = getSymbolForBotUnit(unit, botUnitSymbol++);
            if (unit.x >= 0 && unit.x < FIELD_SIZE && unit.y >= 0 && unit.y < FIELD_SIZE) {
                battlefield[unit.x][unit.y] = symbol;
                if (botUnitSymbol > 'j') {
                    botUnitSymbol = 'a';
                }
            }
        }
    }

    private static boolean isPositionFree(int x, int y, List<Player> playerUnits, List<Player> botUnits) {
        // Проверяем, свободна ли позиция от союзников
        for (Player player : playerUnits) {
            if (player.getX() == x && player.getY() == y) {
                return false;
            }
        }
        // Проверяем, свободна ли позиция от врагов
        for (Player bot : botUnits) {
            if (bot.getX() == x && bot.getY() == y) {
                return false;
            }
        }
        // Проверяем, является ли клетка препятствием
        if ( battlefield[x][y] == TREE_TILE) {
            return false;
        }
        // Клетка свободна
        return true;
    }

    private static void printBattlefield(Player selectedPlayer, List<Player> playerUnits, List<Player> botUnits) {
        System.out.println("======= Поле битвы =======");
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                System.out.print(battlefield[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("================================");
        System.out.println();
    }

    private static char getSymbolForPlayerUnit(Player unit) {
        if (unit instanceof Swordsman) {
            return '1';
        } else if (unit instanceof Spearman) {
            return '2';
        } else if (unit instanceof Axeman) {
            return '3';
        } else if (unit instanceof Longbowman) {
            return '4';
        } else if (unit instanceof Shortbowman) {
            return '5';
        } else if (unit instanceof Crossbowman) {
            return '6';
        } else if (unit instanceof Knight) {
            return '7';
        } else if (unit instanceof Hussar) {
            return '8';
        } else if (unit instanceof HorseArcher) {
            return '9';
        } else {
            return '?';
        }
    }

    private static char getSymbolForBotUnit(Player unit, char symbol) {
        if (unit instanceof Swordsman) {
            return 'a';
        } else if (unit instanceof Spearman) {
            return 'b';
        } else if (unit instanceof Axeman) {
            return 'c';
        } else if (unit instanceof Longbowman) {
            return 'd';
        } else if (unit instanceof Shortbowman) {
            return 'e';
        } else if (unit instanceof Crossbowman) {
            return 'f';
        } else if (unit instanceof Knight) {
            return 'g';
        } else if (unit instanceof Hussar) {
            return 'h';
        } else if (unit instanceof HorseArcher) {
            return 'j';
        } else {
            return '?';
        }
    }

    private static char getRandomTerrain() {
        int rand = random.nextInt(15);
        if (rand < 2) {
            return SWAMP_TILE;
        } else if (rand < 4) {
            return HILL_TILE;
        } else if (rand < 6) {
            return TREE_TILE;
        } else {
            return NORMAL_TILE;
        }
    }

    private static boolean canAttack(Player attacker, List<Player> targets) {
        for (Player target : targets) {
            int distance = Math.abs(attacker.x - target.x) + Math.abs(attacker.y - target.y);
            if (distance <= attacker.attackRange) {
                return true; // Есть возможность атаки
            }
        }
        return false;
    }

    public static int calculateDamage(Player attacker, Player target) {
        // Вычисляем базовый урон атакующего юнита
        int baseDamage = attacker.getAttack();

        // Вычисляем защиту целевого юнита
        int defense = target.getDefense();

        // Вычисляем урон, учитывая защиту
        int damage = Math.max(baseDamage - defense, 0);

        return damage;
    }



    private static void attackRandomPlayer(Player attacker, List<Player> enemies) {
        // Проверка, есть ли противники
        if (enemies.isEmpty()) {
            System.out.println("Нет противников для атаки.");
            return;
        }

        // Поиск противников в радиусе атаки
        List<Player> targetsInRange = new ArrayList<>();
        for (Player enemy : enemies) {
            int distance = Math.abs(attacker.getX() - enemy.getX()) + Math.abs(attacker.getY() - enemy.getY());
            if (distance <= attacker.getAttackRange()) {
                targetsInRange.add(enemy);
            }
        }

        // Если нет противников в радиусе атаки, выходим из метода
        if (targetsInRange.isEmpty()) {
            System.out.println("Нет противников в радиусе атаки.");
            return;
        }

        // Выбор случайного противника из списка противников в радиусе атаки
        Player target = targetsInRange.get(random.nextInt(targetsInRange.size()));

        // Вычисление урона

        int damage = calculateDamage(attacker, target);

        // Применение урона к противнику
        target.takeDamage(damage);
        if (!target.isAlive()) {
            // Если юнит мертв, удаляем его из списка врагов
            enemies.remove(target);
            System.out.println("Вражеский юнит уничтожен!");
        }
        System.out.println(attacker.getClass().getSimpleName() + " атаковал " + target.getClass().getSimpleName() +
                " и нанес " + damage + " урона.");
    }


    private static boolean IsOutOfField(int x, int y){
        if (x >= 0 && x <= FIELD_SIZE && y >= 0 && y <= FIELD_SIZE) {
            return true;
        }
        System.out.println("ХОД ВНЕ ПОЛЯ!");
        return false;
    }

    private static int Fine(int x, int y,Player unit){
        if(IsOutOfField(x, y)==false) return 999;
        int fine = 0;
        System.out.println("DEBUG: " + unit.x + " "+ unit.y + " " + Math.abs(unit.x - x));
        if(unit.x < x) {
            for (int i = 1; i < Math.abs(unit.x - x)+2; i++) {
                if (battlefield[unit.x + i][unit.y] == SWAMP_TILE || battlefield[x][y] == TREE_TILE) {
                    fine++;
                }
            }
        }else{
            for (int i = 1; i < Math.abs(unit.x - x)+2; i++) {
                if (battlefield[unit.x - i][unit.y] == SWAMP_TILE || battlefield[x][y] == TREE_TILE) {
                    fine++;
                }
            }
        }
        if(unit.y < y){
            for(int j=1; j < Math.abs(unit.y - y)+2; j++){
                if (battlefield[x][unit.y+j] == SWAMP_TILE || battlefield[x][y] == TREE_TILE) {
                    fine++;
                }
            }
        }else {
            for(int j=1; j < Math.abs(unit.y - y)+2; j++){
                if (battlefield[x][unit.y-j] == SWAMP_TILE || battlefield[x][y] == TREE_TILE) {
                    fine++;
                }
            }
        }
        System.out.println("ШТРАФ: " + fine);
        return fine;
    }


}


