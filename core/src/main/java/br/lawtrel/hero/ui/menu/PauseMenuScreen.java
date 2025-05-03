package br.lawtrel.hero.ui.menu;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.ui.components.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenuScreen extends ScreenAdapter {
    private Hero game;
    private Stage stage;
    private Skin skin;
    private Table table;

    public PauseMenuScreen(Hero game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        table = new Table();
        table.setFillParent(true);
        stage.addAction(table);

        //Adicionar as seções dos items
        table.add(new ItemsSection(game).pad(10));
        table.row();
        table.add(new EquipmentSection(game).pad(10));
        table.row();
        table.add(new StatusSection(game).pad(10));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
}
