package br.lawtrel.hero.screens;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
import br.lawtrel.hero.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

//Mapa Principal do Game
public class WorldMapScreen extends ScreenAdapter {
    private Hero game;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;

    private final int TILE_SIZE = 16; // Tamanho das texturas
    private final int MAP_WIDTH = 100 * TILE_SIZE;
    private final int MAP_HEIGHT = 100 * TILE_SIZE;

    public  WorldMapScreen(Hero game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        //Carrega o mapa
        map = new TmxMapLoader().load("maps/word.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Criar o Player com o builder
        player = new PlayerBuilder()
            .setPosition(100, 100)
            .loadAnimation("sprites/hero.png")
            .build();

        //Cria a camera do jogo
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        player.update(delta, up, down, left, right);
        //player.render(batch);

        //atualiza a camera para seguir hero
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2, MAP_WIDTH - camera.viewportWidth / 2);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2, MAP_HEIGHT - camera.viewportHeight / 2);
        camera.position.set(camX, camY, 0);
        camera.update();

        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        //Renderizar o Hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        player.dispose();
    }
}
