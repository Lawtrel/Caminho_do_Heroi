package br.lawtrel.hero.screens.castle;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.entities.EnemyFactory;
import br.lawtrel.hero.entities.NPC;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.screens.BattleScreen;
import br.lawtrel.hero.ui.DialogueBox;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BossFightScreen extends ScreenAdapter implements InputProcessor {
    private final Hero game;
    private final MapManager  mapManager ;
    private final  OrthographicCamera camera;
    private final SpriteBatch batch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private Viewport viewport;
    private final Array<NPC> npcs = new Array<>();
    private boolean battleStarted = false;
    private final Stage stage;
    private final Skin skin;
    private final DialogueBox dialogueBox;
    private boolean inDialogue = false;
    private final String MAP_ID = "maps/bossFight.tmx";
    // Adicione estas constantes com as dimensões REAIS do seu mapa vila.tmx
    private final int MAP_WIDTH_PIXELS = 40 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile
    private final int MAP_HEIGHT_PIXELS = 30 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile

    private static final float WORLD_WIDTH = 500;
    private static final float WORLD_HEIGHT = 400;

    public BossFightScreen(Hero game, MapManager mapManager){
        this.game = game;
        this.mapManager = mapManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        this.stage = new Stage(new ScreenViewport());
        this.dialogueBox = new DialogueBox(skin);
        this.stage.addActor(dialogueBox);

    }

    @Override
    public void show(){
        Gdx.input.setInputProcessor(this);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Ponto de Spawn
        this.player = game.getPlayer();
        if (player == null) {
            Gdx.app.error("BossFightScreen", "Jogador nulo ao mostrar a tela!");
            return;
        }

        player.setScale(0.5f);
        player.setInBattleView(false);

        // <<< CORREÇÃO: Procura o spawn point com o nome correto do seu arquivo TMX >>>
        Vector2 spawnPoint = findSpawnPoint(map);
        player.setPosition(spawnPoint.x, spawnPoint.y);
        loadBossNpc();
    }

    private void loadBossNpc() {
        npcs.clear();
        MapLayer npcLayer = map.getLayers().get("NPCs");
        if (npcLayer == null) return;

        for (MapObject object : npcLayer.getObjects()) {
            if ("final_boss".equals(object.getProperties().get("type", String.class))) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String spritePath = object.getProperties().get("sprite", String.class);
                NPC bossNpc = new NPC(new Texture(Gdx.files.internal(spritePath)), rect.x, rect.y, "final_boss");

                String dialogueStr = object.getProperties().get("dialogue", "", String.class);
                if (!dialogueStr.isEmpty()) {
                    String[] lines = dialogueStr.split("\\|");
                    bossNpc.setDialogue(lines);
                }

                npcs.add(bossNpc);
                return;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!inDialogue &&!battleStarted) {
            // Verifica se o jogo deve ser pausado
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (game != null) { // Boa prática verificar se 'game' não é nulo
                    game.pauseGame(); // Chama o método de Hero.java para abrir o PauseMenuScreen
                }
                return; // Retorna imediatamente para não processar o resto do frame da WorldMapScreen
            }
            //atualizar o hero
            boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            player.update(delta, up, down, left, right);
            //Salva a posição anterior do jogador antes de atualizar
            float oldPLayerX = player.getX();
            float oldPlayerY = player.getY();
            checkMapObjectCollisions(oldPLayerX, oldPlayerY); //função nova para lidar com as colisões
            checkBossTrigger();

        }

        //Camera
        camera.position.x = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH_PIXELS - viewport.getWorldWidth() / 2f);
        camera.position.y = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT_PIXELS - viewport.getWorldHeight() / 2f);
        camera.update();
        viewport.apply();
        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();
        //Renderizar o Hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY());
        for (NPC npc : npcs) {
            npc.render(batch);
        }
        batch.end();
        stage.act(delta);
        stage.draw();

    }
    private void checkBossTrigger() {
        if (battleStarted) return;
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        if (triggerLayer == null) return;

        for (MapObject object : triggerLayer.getObjects()) {
            if ("boss_trigger".equals(object.getName()) && object instanceof RectangleMapObject) {
                Rectangle triggerRect = ((RectangleMapObject) object).getRectangle();
                if (player.getBounds().overlaps(triggerRect)) {
                    startBossDialogue();
                    break;
                }
            }
        }
    }
    private void startBossDialogue() {
        if (battleStarted || npcs.size == 0) return;
        battleStarted = true;
        inDialogue = true;

        Array<String> bossDialogue = npcs.get(0).getDialogueLines();
        if (bossDialogue.size > 0) {
            dialogueBox.startDialogue(bossDialogue);
        } else {
            inDialogue = false;
            startBossBattle();
        }
    }

    private void startBossBattle() {
        game.soundManager.stopMusic();
        Gdx.app.log("BossFightScreen", "Batalha contra o chefe iniciada!");
        Enemy bossEnemy = EnemyFactory.createEnemy(EnemyFactory.EnemyType.CHAOS, 0, 0);
        Array<Enemy> enemies = new Array<>();
        enemies.add(bossEnemy);

        game.setPlayerLastWorldMapPosition(player.getX(), player.getY(), MAP_ID);
        player.setCurrentArea("castelo");
        game.setScreen(new BattleScreen(game, player, enemies));
    }


    private void checkMapObjectCollisions(float oldPlayerX, float oldPlayerY){
        handleSolidObjectCollisions(map.getLayers().get("Colisoes"), oldPlayerX, oldPlayerY);
        handleTransitionCollisions(map.getLayers().get("Portas"));
    }

    //Metodo generico para lidar com colisões que resultam em transições
    private void handleTransitionCollisions(MapLayer layer){
        if (layer == null) return;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (player.getBounds().overlaps(((RectangleMapObject) object).getRectangle())) {
                    String targetMap = object.getProperties().get("target", String.class);
                    if (targetMap != null) {

                        // <<< CORREÇÃO: Salva a posição atual do jogador antes de mudar >>>
                        game.setPlayerLastWorldMapPosition(player.getX(), player.getY(), MAP_ID);

                        switch (targetMap.toLowerCase()) {
                            case "world":
                                mapManager.changeMap(MapManager.MapType.WORLD_MAP);
                                return;
                        }
                    }
                }
            }
        }
    }

    private void handleSolidObjectCollisions(MapLayer layer, float oldPlayerX, float oldPlayerY){
        if (layer == null) return;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (player.getBounds().overlaps(((RectangleMapObject) object).getRectangle())) {
                    player.setPosition(oldPlayerX, oldPlayerY);
                    return;
                }
            }
        }
    }

    private Vector2 findSpawnPoint(TiledMap map){
        MapLayer objectLayer = map.getLayers().get("Spawn");
        if(objectLayer != null){
            for (MapObject object : objectLayer.getObjects()){
                if("spawnPoint".equals(object.getName()) && object instanceof RectangleMapObject){
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    return new Vector2(rect.x, rect.y);
                }
            }
        }
        Gdx.app.log("BossFightScreen", "Objeto 'spawnPoint' não encontrado na camada 'Spawn'. Usando valor padrão.");
        return new Vector2(100, 100); // valor padrão caso não encontre
    }

    @Override
    public boolean keyDown(int keycode) {
        if (inDialogue) {
            if (keycode == Input.Keys.Z) {
                dialogueBox.advanceDialogue();
                if (!dialogueBox.isVisible()) {
                    inDialogue = false;
                    startBossBattle();
                }
            }
            return true;
        }

        if (keycode == Input.Keys.ESCAPE && !inDialogue) {
            if (game != null) {
                game.pauseGame();
            }
            return true;
        }
        return false;
    }


    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        stage.dispose();
        skin.dispose();
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
