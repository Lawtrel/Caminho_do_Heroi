package br.lawtrel.hero.entities.items;
import br.lawtrel.hero.entities.items.Item;
import java.util.HashMap;
import java.util.Map;
public class ItemFactory {
    private static final Map<String, Item> itemDefinitions = new HashMap<>();
    static {
        // ID, Nome, Descrição, Tipo, ATK, DEF, M.ATK, M.DEF, HP, MP, HP Rec, MP Rec
        defineItem(new Item("ITM001", "Pocao Pequena", "Restaura 25 HP.", Item.Type.CONSUMABLE,
            0, 0, 0, 0, 0, 0, 25, 0));
        defineItem(new Item("ITM002", "Pele de Goblin", "Material simples.", Item.Type.MATERIAL,
            0, 0, 0, 0, 0, 0, 0, 0));
        defineItem(new Item("ITM003", "Espada Curta", "Uma espada curta e básica.", Item.Type.WEAPON,
            3, 0, 0, 0, 0, 0, 0, 0));
        defineItem(new Item("ITM004", "Escudo de Madeira", "Um escudo simples de madeira.", Item.Type.ARMOR, // ARMOR geralmente é para o corpo, SHIELD seria um subtipo ou slot diferente
            0, 2, 0, 0, 0, 0, 0, 0)); // ARMOR pode ser slot de armadura, ou SHIELD um slot separado
        defineItem(new Item("ITM005", "Eter Pequeno", "Restaura 10 MP.", Item.Type.CONSUMABLE,
            0, 0, 0, 0, 0, 0, 0, 10));
        // Adicione mais itens conforme necessário
    }

    private static void defineItem(Item item) {
        itemDefinitions.put(item.getId(), item);
    }

    public static Item createItem(String itemId) {
        Item definition = itemDefinitions.get(itemId);
        if (definition != null) {
            // Para itens consumíveis ou com estado, você pode querer retornar uma nova instância:
            // if (definition.getType() == Item.Type.CONSUMABLE) {
            //     return new Item(definition.getId(), definition.getName(), definition.getDescription(), ... );
            // }
            // Por enquanto, retornamos a definição compartilhada, assumindo que os bônus são fixos.
            return definition;
        }
        System.err.println("ItemFactory: Item com ID '" + itemId + "' não encontrado.");
        return null;
    }

    public static java.util.Set<String> getAllItemIds() {
        return itemDefinitions.keySet();
    }
}
