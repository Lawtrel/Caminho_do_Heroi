package br.lawtrel.hero.entities;

public class Equipment {
    private Item weapon;
    private Item armor;
    private Item accessory;

    // Métodos para equipar
    public void equipWeapon(Item weapon) {
        if(weapon == null || weapon.getType() != Item.Type.WEAPON) {
            this.weapon = weapon;
        }
    }

    public void equipArmor(Item armor) {
        if(armor == null || armor.getType() != Item.Type.ARMOR) {
            this.armor = armor;
        }
    }

    public void equipAccessory(Item acessory) {
        if(acessory == null || acessory.getType() != Item.Type.ACCESSORY) {
            this.accessory = acessory;
        }
    }


    // Métodos para cálculo de bônus
    public int getTotalAttackBonus() {
        int bonus  = 0;
        if(weapon != null) bonus  += weapon.getAttackBonus();
        if(accessory != null) bonus  += accessory.getAttackBonus();
        return bonus ;
    }

    public int getTotalDefenseBonus() {
        int bonus  = 0;
        if(armor != null) bonus  += armor.getDefenseBonus();
        if(accessory != null) bonus  += accessory.getDefenseBonus();
        return bonus ;
    }

    // Getters para os itens equipados
    public Item getWeapon() { return weapon; }
    public Item getArmor() { return armor; }
    public Item getAccessory() {return accessory;}
}
