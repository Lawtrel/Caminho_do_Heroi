package br.lawtrel.hero.utils;

import com.badlogic.gdx.utils.Array;
//Esta classe armazena todos os dados que precisam ser salvos.
public class PlayerState {
    // --- Atributos do Personagem ---
    public String name;
    public int level;
    public int exp;
    public int maxHp;
    public int hp;
    public int maxMp;
    public int mp;
    public int attack;
    public int defense;
    public int magicAttack;
    public int magicDefense;
    public int speed;
    public int luck;

    // --- Dados do Jogador ---
    public int money;
    public Array<String> inventoryItemIds;
    public String equippedWeaponId;
    public String equippedArmorId;
    public String equippedAccessoryId;

    // --- Posição no Mundo ---
    public String lastMapId;
    public float playerX;
    public float playerY;

    // Construtor padrão (obrigatório para a deserialização JSON do LibGDX)
    public PlayerState() {
        inventoryItemIds = new Array<>();
    }
}
