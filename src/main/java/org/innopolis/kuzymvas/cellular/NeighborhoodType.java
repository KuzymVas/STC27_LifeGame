package org.innopolis.kuzymvas.cellular;

/**
 * Тип локального окружения клетки: по Муру - это 8 клеток вокруг,
 * по Вон Нейману - 4 ортогональных клетки вокруг,
 * Расширенный Вон Нейман - 8 ортогональных клеток, по две в каждую сторону
 */
public enum NeighborhoodType {
    MOORE,
    VON_NEUMANN,
    EXTENDED_VON_NEUMANN
}
