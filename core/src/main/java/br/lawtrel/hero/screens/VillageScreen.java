package br.lawtrel.hero.screens;
import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.NPC;
import br.lawtrel.hero.entities.Player;
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

public class VillageScreen extends ScreenAdapter implements InputProcessor {

    private final Hero game;
    private final MapManager  mapManager ;
    private final  OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch batch;
    private Player player;
    private final Array<NPC> npcs;
    private final Stage stage;
    private final Skin skin;
    private final DialogueBox dialogueBox;
    private boolean inDialogue = false;
    private final String MAP_ID = "maps/vila.tmx";

    // Adicione estas constantes com as dimensões REAIS do seu mapa vila.tmx
    private final int MAP_WIDTH_PIXELS = 38 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile
    private final int MAP_HEIGHT_PIXELS = 26 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile

    private static final float WORLD_WIDTH = 380;
    private static final float WORLD_HEIGHT = 260;

    public VillageScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.npcs = new Array<>();
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        this.stage = new Stage(new ScreenViewport());
        this.dialogueBox = new DialogueBox(skin);
        this.stage.addActor(dialogueBox);

    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        game.soundManager.playMusic("audio/music/village_map.mp3", true);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Ponto de Spawn
        this.player = game.getPlayer();
        if (player == null) {
            Gdx.app.error("VillageScreen", "Jogador nulo ao mostrar a tela!");
            return;
        }
        float scale = map.getProperties().get("playerScale", 0.5f, Float.class);
        player.setScale(scale);
        player.setInBattleView(false);

        Vector2 spawnPoint = findSpawnPoint(map);
        player.setPosition(spawnPoint.x, spawnPoint.y);
        loadNpcsFromMap();
    }
    private void loadNpcsFromMap() {
        // Limpa a lista de NPCs antigos, caso exista
        for (NPC npc : npcs) {
            npc.dispose();
        }
        npcs.clear();

        MapLayer npcLayer = map.getLayers().get("NPCs");
        if (npcLayer == null) {
            Gdx.app.log("VillageScreen", "Camada 'NPCs' nao encontrada no mapa.");
            return;
        }

        for (MapObject object : npcLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                float x = rectObject.getRectangle().x;
                float y = rectObject.getRectangle().y;

                // Lê as propriedades personalizadas que definimos no Tiled
                String type = object.getProperties().get("type", String.class);
                String spritePath = object.getProperties().get("sprite", String.class);

                if (type != null && spritePath != null) {
                    try {
                        Texture npcTexture = new Texture(Gdx.files.internal(spritePath));
                        NPC npc = new NPC(npcTexture, x, y, type);

                        // Futuramente, aqui definiremos o diálogo
                        // npc.setDialogue(new String[]{"Olá, aventureiro!", "O tempo está bom hoje."});

                        npcs.add(npc);
                    } catch (Exception e) {
                        Gdx.app.error("VillageScreen", "Nao foi possivel carregar a textura do NPC: " + spritePath, e);
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Salva a posição anterior do jogador antes de atualizar
        float oldPLayerX = player.getX();
        float oldPlayerY = player.getY();

        if (!inDialogue) {
            //atualizar o hero
            boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            player.update(delta, up, down, left, right);
            checkMapObjectCollisions(oldPLayerX, oldPlayerY); //função nova para lidar com as colisões
        }

        //Camera
        camera.position.x = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH_PIXELS - viewport.getWorldWidth() / 2f);
        camera.position.y = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT_PIXELS - viewport.getWorldHeight() / 2f);
        camera.update();
        viewport.apply();
        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();
        //checkDoorCollision();

        //Renderizar o Hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY() );
        for (NPC npc : npcs) {
            npc.render(batch);
        }
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    //Analisa as colisões existentes no jogo
    private void checkMapObjectCollisions(float oldPlayerX, float oldPlayerY){
        handleSolidObjectCollisions(map.getLayers().get("Colisoes"), oldPlayerX, oldPlayerY);
        handleSolidObjectCollisions(map.getLayers().get("Arvores"), oldPlayerX, oldPlayerY);
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
                            case "shop":
                                mapManager.changeMap(MapManager.MapType.SHOP);
                                return;
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
        Gdx.app.log("VillageScreen", "Objeto 'spawnPoint' não encontrado na camada 'Spawn'. Usando valor padrão.");
        return new Vector2(100, 100); // valor padrão caso não encontre
    }



    @Override
    public boolean keyDown(int keycode) {
        // Primeiro, dá ao stage a oportunidade de usar o input.
        // Se um actor do stage (como um campo de texto) usar o input, ele retorna true.
        if (stage.keyDown(keycode)) {
            return true;
        }

        // Se o stage não usou o input, processamos a nossa lógica
        if (keycode == Input.Keys.Z) {
            if (inDialogue) {
                dialogueBox.advanceDialogue();
                if (!dialogueBox.isVisible()) {
                    inDialogue = false;
                }
            } else {
                checkForNpcInteraction();
            }
            return true;
        }

        if (keycode == Input.Keys.ESCAPE && !inDialogue) {
            game.pauseGame();
            return true;
        }

        return false;
    }

    private void checkForNpcInteraction() {
        for (NPC npc : npcs) {
            Rectangle interactionBounds = new Rectangle(npc.getBounds());
            interactionBounds.x -= 8;
            interactionBounds.y -= 8;
            interactionBounds.width += 16;
            interactionBounds.height += 16;

            if (player.getBounds().overlaps(interactionBounds)) {
                inDialogue = true;
                dialogueBox.startDialogue(npc.getDialogueLines());
                break;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        for (NPC npc : npcs) {
            npc.dispose();
        }
        stage.dispose();
        skin.dispose();
    }

    @Override
    public boolean keyUp(int keycode) { return stage.keyUp(keycode); }
    @Override
    public boolean keyTyped(char character) { return stage.keyTyped(character); }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return stage.touchDown(screenX, screenY, pointer, button); }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return stage.touchUp(screenX, screenY, pointer, button); }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return stage.touchDragged(screenX, screenY, pointer); }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return stage.mouseMoved(screenX, screenY); }
    @Override
    public boolean scrolled(float amountX, float amountY) { return stage.scrolled(amountX, amountY); }

}
