package br.lawtrel.hero;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.CharacterBuilder;
import br.lawtrel.hero.entities.PhysicalAttackStrategy;
import br.lawtrel.hero.screens.BattleTestScreen;
import br.lawtrel.hero.ui.menu.PauseMenuScreen;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

public class Hero extends Game {
    public MapManager mapManager;
    private Player player;
    private Screen screenBeforePause;
    private boolean justPaused = false;

    // Armazena a posição do jogador antes da batalha
    private Vector2 playerLastWorldMapPosition = null;
    private String lastWorldMapId = null; // caso tenha múltiplos mapas mundiais ou áreas

    @Override
    public void create() {
        initializePlayer();
        //setScreen(new BattleTestScreen(this));
        mapManager = new MapManager(this);
        mapManager.changeMap(MapManager.MapType.WORLD_MAP);

    }
    private void initializePlayer() {
        Character playerCharacter = new CharacterBuilder()
            .setName("Heroi") // Nome do jogador global
            .setMaxHp(100)
            .setMaxMP(50)
            .setAttack(10)
            .setDefense(8)
            .setMagicAttack(5)
            .setMagicDefense(5)
            .setSpeed(12)
            .setLuck(5)
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
        player.addItem(br.lawtrel.hero.entities.items.ItemFactory.createItem("ITM001")); // Poção Pequena
        player.addItem(br.lawtrel.hero.entities.items.ItemFactory.createItem("ITM003")); // Espada Curta
        player.getCharacter().gainExp(50); // Adiciona um pouco de XP inicial
        player.addMoney(25); // Adiciona um pouco de ouro inicial

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
        Screen currentActiveScreen = getScreen();
        if (currentActiveScreen != null && !(currentActiveScreen instanceof PauseMenuScreen)) {
            this.screenBeforePause = currentActiveScreen;
            super.setScreen(new PauseMenuScreen(this)); // Use super.setScreen aqui
            this.justPaused = true; // <<----- SETA O FLAG
            Gdx.app.log("Hero", "Jogo pausado. Tela anterior: " + (screenBeforePause != null ? screenBeforePause.getClass().getSimpleName() : "null"));
        } else if (currentActiveScreen != null) {
            Gdx.app.log("Hero", "O jogo já está pausado.");
        } else {
            Gdx.app.log("Hero", "Nenhuma tela ativa para pausar.");
        }
    }

    public void resumeGame() {
        if (this.screenBeforePause != null) {
            Gdx.app.log("Hero", "Resumindo para: " + screenBeforePause.getClass().getSimpleName());
            super.setScreen(this.screenBeforePause); // Use super.setScreen aqui
            this.screenBeforePause = null;
        } else {
            Gdx.app.log("Hero", "Nenhuma tela para resumir. Voltando para o Mapa Mundial como fallback.");
            if (mapManager != null) {
                mapManager.changeMap(MapManager.MapType.WORLD_MAP);
            } else {
                Gdx.app.error("Hero", "MapManager é nulo, não é possível voltar ao mapa mundial como fallback.");
            }
        }
    }
    public boolean consumeJustPausedFlag() {
        if (justPaused) {
            justPaused = false; // Consome o flag
            return true;
        }
        return false;
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


}
