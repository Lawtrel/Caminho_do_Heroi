package br.lawtrel.hero;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.CharacterBuilder;
import br.lawtrel.hero.entities.PhysicalAttackStrategy;
import br.lawtrel.hero.screens.BattleTestScreen;
import br.lawtrel.hero.ui.menu.PauseMenuScreen;
import br.lawtrel.hero.utils.MapManager;
import br.lawtrel.hero.screens.MainMenuScreen;
import br.lawtrel.hero.utils.SoundManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import br.lawtrel.hero.entities.items.Item;
import br.lawtrel.hero.entities.items.ItemFactory;
import br.lawtrel.hero.utils.PlayerState;
import br.lawtrel.hero.utils.SaveManager;
import com.badlogic.gdx.utils.Array;

public class Hero extends Game {
    public MapManager mapManager;
    private Player player;
    private PauseMenuScreen pauseMenuScreen;
    private boolean isPaused = false;
    private InputProcessor screenInputProcessor;
    public SoundManager soundManager;

    // Armazena a posição do jogador antes da batalha
    private Vector2 playerLastWorldMapPosition = null;
    private String lastWorldMapId = null; // caso tenha múltiplos mapas mundiais ou áreas

    @Override
    public void create() {
        soundManager = new SoundManager();
        //setScreen(new BattleTestScreen(this));
        mapManager = new MapManager(this);
        setScreen(new MainMenuScreen(this));
    }
    private void initializePlayer() {
        Character playerCharacter = new CharacterBuilder()
            .setName("Vent") // Nome do jogador global
            .setMaxHp(120)
            .setMaxMP(40)
            .setAttack(15)
            .setDefense(8)
            .setMagicAttack(10)
            .setMagicDefense(6)
            .setSpeed(12)
            .setLuck(7)
            .setExpYield(0)
            .setGoldYield(0)
            .setStrategy(new PhysicalAttackStrategy())
            .build();

        this.player = new PlayerBuilder()
            .setPosition(0, 0)
            .setSpeed(100) // Velocidade de movimento no mapa
            .setCharacter(playerCharacter)
            .loadAnimation("sprites/hero.png")
            .build();

        // Adiciona alguns itens iniciais para teste no PauseMenuScreen
        player.addItem(br.lawtrel.hero.entities.items.ItemFactory.createItem("ITM001")); //Poção Pequena
        //player.addItem(br.lawtrel.hero.entities.items.ItemFactory.createItem("ITM005")); // Eter
        player.addItem(br.lawtrel.hero.entities.items.ItemFactory.createItem("ITM003")); // Espada Curta
        player.getCharacter().gainExp(0); // Adiciona um pouco de XP inicial
        player.addMoney(50); // Adiciona um pouco de ouro inicial

    }
    public void setPlayer(Player player) {
        this.player = player;
        if (this.player != null) {
            Gdx.app.log("Hero", "Instância do Jogador definida: " + player.getCharacter().getName());
        } else {
            Gdx.app.log("Hero", "Instância do Jogador definida como null.");
        }
    }

    public Player getPlayer() {
        if (this.player == null) {
            Gdx.app.log("Hero_getPlayer", "AVISO: getPlayer() chamado, mas a instância do jogador é nula.");
        }
        return player;
    }

    public void pauseGame() {
        if (isPaused) return;
        isPaused = true;
        if (pauseMenuScreen == null) {
            pauseMenuScreen = new PauseMenuScreen(this);
        }
        screenInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(pauseMenuScreen.getStage());
    }

    public void resumeGame() {
        if (!isPaused) return;
        isPaused = false;
        Gdx.input.setInputProcessor(screenInputProcessor);
    }

    @Override
    public void render() {
        super.render(); // Renderiza a tela atual

        if (isPaused) {
            pauseMenuScreen.render(Gdx.graphics.getDeltaTime());
        }
    }
    public boolean consumeJustPausedFlag() {
        return false;
    }

    public void saveGame() {
        if (player == null || player.getCharacter() == null) {
            Gdx.app.error("Hero_saveGame", "Não é possível salvar, jogador é nulo.");
            return;
        }

        PlayerState state = new PlayerState();
        Character character = player.getCharacter();

        // Salva a posição exata do jogador
        state.playerX = player.getX();
        state.playerY = player.getY();
        if (mapManager != null) {
            state.lastMapId = mapManager.getCurrentMapId();
        }

        // Popula o estado com os dados do personagem
        state.name = character.getName();
        state.level = character.getLevel();
        state.exp = character.getExp();
        state.maxHp = character.getMaxHp();
        state.hp = character.getHp();
        state.maxMp = character.getMaxMP();
        state.mp = character.getMp();
        state.attack = character.getAttack();
        state.defense = character.getDefense();
        state.magicAttack = character.getMagicAttack();
        state.magicDefense = character.getMagicDefense();
        state.speed = character.getSpeed();
        state.luck = character.getLuck();
        state.money = player.getMoney();
        // Salva os IDs dos itens do inventário
        state.inventoryItemIds = new Array<>();
        for (Item item : player.getInventory()) {
            state.inventoryItemIds.add(item.getId());
        }

        // Salva os IDs dos itens equipados
        if (player.getEquippedWeapon() != null) state.equippedWeaponId = player.getEquippedWeapon().getId();
        if (player.getEquippedArmor() != null) state.equippedArmorId = player.getEquippedArmor().getId();
        if (player.getEquippedAccessory() != null) state.equippedAccessoryId = player.getEquippedAccessory().getId();

        SaveManager.saveGame(state);
    }

    public void startNewGame() {
        Gdx.app.log("Hero", "Inicializando um novo jogo....");
        initializePlayer();
        mapManager.changeMap(MapManager.MapType.WORLD_MAP);
    }

    public boolean loadGame() {
        PlayerState state = SaveManager.loadGame();
        if (state == null) {
            return false; // Nenhum save encontrado
        }

        try {
            // Recria o Character com os dados salvos
            Character loadedCharacter = new CharacterBuilder()
                .setName(state.name)
                .setLevel(state.level)
                .setStartingExp(state.exp)
                .setMaxHp(state.maxHp)
                .setMaxMP(state.maxMp)
                .setAttack(state.attack)
                .setDefense(state.defense)
                .setMagicAttack(state.magicAttack)
                .setMagicDefense(state.magicDefense)
                .setSpeed(state.speed)
                .setLuck(state.luck)
                .setStrategy(new PhysicalAttackStrategy()) // Estratégia padrão
                .build();
            // Ajusta HP e MP atuais
            loadedCharacter.heal(state.hp); // Usa heal para definir o HP atual sem exceder o máximo
            loadedCharacter.restoreMp(state.mp);

            // Recria o Player com o personagem carregado
            this.player = new PlayerBuilder()
                .setPosition(state.playerX, state.playerY)
                .setCharacter(loadedCharacter)
                .loadAnimation("sprites/hero.png")
                .build();

            // Adiciona dinheiro
            this.player.addMoney(state.money);

            // Recria e adiciona os itens do inventário a partir dos IDs
            if (state.inventoryItemIds != null) {
                for (String itemId : state.inventoryItemIds) {
                    this.player.addItem(ItemFactory.createItem(itemId));
                }
            }

            // Recria e equipa os itens
            if (state.equippedWeaponId != null) this.player.equip(ItemFactory.createItem(state.equippedWeaponId));
            if (state.equippedArmorId != null) this.player.equip(ItemFactory.createItem(state.equippedArmorId));
            if (state.equippedAccessoryId != null) this.player.equip(ItemFactory.createItem(state.equippedAccessoryId));

            // Salva a posição carregada para ser usada pela WorldMapScreen
            setPlayerLastWorldMapPosition(state.playerX, state.playerY, state.lastMapId);

            Gdx.app.log("Hero_loadGame", "Progresso carregado para o jogador " + state.name);
            return true;
        } catch (Exception e) {
            Gdx.app.error("Hero_loadGame", "Falha ao aplicar o estado do jogo carregado.", e);
            // Se houver um erro ao aplicar o save (ex: save corrompido), retorne false
            // para que um novo jogo seja iniciado.
            this.player = null; // Garante que o jogador parcialmente carregado seja descartado
            return false;
        }
    }
    public void continueGame() {
        if (player == null) {
            Gdx.app.error("Hero", "Tentou continuar para o jogo, mas o jogador é nulo.");
            setScreen(new MainMenuScreen(this));
            return;
        }
        if (lastWorldMapId != null) {
            mapManager.changeMap(mapManager.getMapTypeFromId(lastWorldMapId));
        } else {
            Gdx.app.log("Hero", "Transicionando para o mapa mundial...");
            mapManager.changeMap(MapManager.MapType.WORLD_MAP);
        }
    }



    @Override
    public void setScreen(Screen screen) {
        Gdx.app.log("Hero_setScreen", "Trocando para tela: " + (screen != null ? screen.getClass().getSimpleName() : "null"));
        super.setScreen(screen);
    }

    public void setPlayerLastWorldMapPosition(float x, float y, String mapId) {
        if (this.playerLastWorldMapPosition == null) {
            this.playerLastWorldMapPosition = new Vector2();
        }
        this.playerLastWorldMapPosition.set(x, y);
        this.lastWorldMapId = mapId; // Armazena o ID/nome do mapa
        Gdx.app.log("Hero", "Posição do jogador salva: " + x + "," + y + " no mapa: " + mapId);
    }

    public Vector2 getPlayerLastWorldMapPosition(String mapId) {
        // Retorna a posição apenas se o mapId corresponder, para evitar usar
        // a posição de um mapa diferente se o sistema for expandido.
        if (mapId != null && mapId.equals(this.lastWorldMapId) && this.playerLastWorldMapPosition != null) {
            return new Vector2(this.playerLastWorldMapPosition); // Retorna uma nova instância para evitar modificação externa
        }
        return null;
    }

    public void clearPlayerLastWorldMapPosition() {
        // Chame isso depois que a posição for usada, para que na próxima vez
        // que entrar no mapa sem uma batalha anterior, ele use o spawn padrão.
        this.playerLastWorldMapPosition = null;
        this.lastWorldMapId = null;
        Gdx.app.log("Hero", "Posição salva do jogador limpa.");
    }
    @Override
    public void dispose() {
        super.dispose();
        if (soundManager != null) {
            soundManager.dispose();
        }
    }


}
