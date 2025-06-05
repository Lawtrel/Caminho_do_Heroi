package br.lawtrel.hero.ui.menu;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.ui.components.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenuScreen extends ScreenAdapter {
    private Hero game;
    private Stage stage;
    private Skin nesSkin;
    private BitmapFont font;
    private Texture whitePixel;;
    private Table mainTable;

    //Seçcoes do menu
    private StatusSection statusSection;
    private ItemsSection itemsSection;
    private EquipmentSection equipmentSection;

    //botoes
    private TextButton resumeButton;
    private TextButton saveButton;
    private TextButton settingsButton;
    private TextButton exitButton;
    private TextButton statusTab, itemsTab, equipmentTab;

    //Posicao do menu
    private int currentTab = 0; // Status , 1 = items, 2 = equipamentos
    // Cores NES
    private static final Color NES_WHITE = new Color(1f, 1f, 1f, 1f);
    private static final Color NES_WINDOW_BACKGROUND = new Color(0.0f, 0.1f, 0.3f, 0.85f); // Azul escuro para fundos de janela/tabela (opacidade ajustável)
    private static final Color NES_WINDOW_BORDER_OR_TEXT = new Color(1f, 1f, 1f, 1f);    // Branco para bordas e texto
    private static final Color NES_TEXT_DEFAULT = new Color(1f, 1f, 1f, 1f);             // Branco para texto padrão
    private static final Color NES_TEXT_SELECTED = new Color(1f, 1f, 0.3f, 1f);         // Amarelo para texto selecionado (como no BattleHUD)
    private static final Color NES_BUTTON_BG_NORMAL = new Color(0.0f, 0.1f, 0.3f, 1f);    // Azul escuro opaco para botões normais
    private static final Color NES_BUTTON_BG_OVER = new Color(0.1f, 0.2f, 0.4f, 1f);   // Um azul um pouco mais claro para mouse over

    public PauseMenuScreen(Hero game) {
        this.game = game;
    }
    private void loadResources() {
        // 1. Criar whitePixel (lógica do BattleHUD)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf")); //
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 16; // Ajuste o tamanho conforme necessário
            parameter.color = NES_WHITE;
            parameter.borderColor = Color.BLACK; // Opcional: borda para melhor legibilidade
            parameter.borderWidth = 1;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            Gdx.app.error("PauseMenuScreen", "Erro ao carregar fonte arial.ttf", e);
            font = new BitmapFont();
        }
    }
    private void createNesSkin() {
        nesSkin = new Skin();
        nesSkin.add("default-font", font, BitmapFont.class); // Adiciona a fonte ao skin

        // Drawable para fundo de janelas/tabelas
        TextureRegionDrawable windowBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        windowBackgroundDrawable.tint(NES_WINDOW_BACKGROUND);
        nesSkin.add("nesWindowBackground", windowBackgroundDrawable, Drawable.class);

        // Estilo para Label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = NES_TEXT_DEFAULT;
        nesSkin.add("default", labelStyle);

        // Estilo para TextButton
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = NES_TEXT_DEFAULT;
        textButtonStyle.overFontColor = NES_TEXT_SELECTED;
        textButtonStyle.downFontColor = NES_TEXT_SELECTED;
        textButtonStyle.checkedFontColor = NES_TEXT_SELECTED;

        TextureRegionDrawable buttonUpDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        buttonUpDrawable.tint(NES_BUTTON_BG_NORMAL);
        textButtonStyle.up = buttonUpDrawable;

        TextureRegionDrawable buttonOverDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        buttonOverDrawable.tint(NES_BUTTON_BG_OVER);
        textButtonStyle.over = buttonOverDrawable;

        TextureRegionDrawable buttonDownDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        buttonDownDrawable.tint(NES_BUTTON_BG_OVER);
        textButtonStyle.down = buttonDownDrawable;

        TextButton.TextButtonStyle tabStyle = new TextButton.TextButtonStyle(textButtonStyle);
        TextureRegionDrawable tabCheckedDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        tabCheckedDrawable.tint(NES_TEXT_SELECTED); // Fundo amarelo para aba ativa
        // Se você quiser que apenas a cor da fonte mude para a aba ativa, e o fundo permaneça o mesmo do "up":
        tabStyle.checked = buttonUpDrawable; // Usa o mesmo fundo do botão normal
        tabStyle.checkedFontColor = NES_BUTTON_BG_OVER; // Mas a fonte é amarela

        nesSkin.add("default", textButtonStyle); // Estilo padrão para TextButton
        nesSkin.add("tab", tabStyle);           // Estilo "tab" para os botões de aba

        // Estilo para List (usado em ItemsSection)
        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
        listStyle.font = font;
        listStyle.fontColorSelected = NES_TEXT_SELECTED;
        listStyle.fontColorUnselected = NES_TEXT_DEFAULT;
        TextureRegionDrawable selectionDrawable = new TextureRegionDrawable(new TextureRegion(whitePixel));
        selectionDrawable.tint(new Color(NES_TEXT_SELECTED.r, NES_TEXT_SELECTED.g, NES_TEXT_SELECTED.b, 0.3f)); // Branco semi-transparente para seleção
        listStyle.selection = selectionDrawable;
        nesSkin.add("default", listStyle); // ADICIONA O ESTILO AO SKIN

        // Estilo para ScrollPane (usado em ItemsSection)
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();

        // Fundo do ScrollPane
         scrollPaneStyle.background = nesSkin.getDrawable("nesWindowBackground"); // Se precisar

        // Drawable para o fundo da barra de rolagem vertical
        TextureRegionDrawable vScrollBg = new TextureRegionDrawable(new TextureRegion(whitePixel));
        vScrollBg.tint(new Color(NES_BUTTON_BG_NORMAL.r * 0.5f, NES_BUTTON_BG_NORMAL.g * 0.5f, NES_BUTTON_BG_NORMAL.b * 0.5f, 0.8f)); // Cor escura para o trilho da barra
        scrollPaneStyle.vScroll = vScrollBg;

        // Drawable  da barra de rolagem vertical
        TextureRegionDrawable vScrollKnob = new TextureRegionDrawable(new TextureRegion(whitePixel));
        vScrollKnob.tint(NES_WINDOW_BORDER_OR_TEXT); // Cor para o knob
        scrollPaneStyle.vScrollKnob = vScrollKnob;
        nesSkin.add("default", scrollPaneStyle);
    }


    @Override
    public void show() {
        loadResources(); // Carrega fonte e whitePixel
        createNesSkin(); // Cria o skin

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        if (game.getPlayer() == null) {
            Gdx.app.error("PauseMenuScreen", "Player é null. Resumindo jogo.");
            game.resumeGame();
            return;
        }

        //Criar as Secoes
        statusSection = new StatusSection(game, nesSkin);
        itemsSection = new ItemsSection(game, nesSkin);
        equipmentSection = new EquipmentSection(game, nesSkin);

        //Criar os botoes
        statusTab = new TextButton("Status", nesSkin, "tab"); // Usando estilo "tab"
        itemsTab = new TextButton("Itens", nesSkin, "tab");
        equipmentTab = new TextButton("Equipamentos", nesSkin, "tab");

        //Criar os botos de acao
        resumeButton = new TextButton("Continuar (ESC)", nesSkin);
        saveButton = new TextButton("Salvar Jogo", nesSkin);
        settingsButton = new TextButton("Configurações", nesSkin);
        exitButton = new TextButton("Sair para Menu Principal", nesSkin);

        // Adiciona listeners
        statusTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(0);
            }
        });

        itemsTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(1);
            }
        });

        equipmentTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(2);
            }
        });

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.resumeGame();
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                /* ... */
            }
        });
        settingsButton.addListener(new ClickListener() { /* ... */ });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                /* ... */
            }
        });

        // Layout do menu
        Table tabTable = new Table();
        tabTable.add(statusTab).pad(5);
        tabTable.add(itemsTab).pad(5);
        tabTable.add(equipmentTab).pad(5);

        final Table contentTable = new Table();
        contentTable.setBackground(nesSkin.getDrawable("nesWindowBackground")); // Fundo para a área de conteúdo


        Table buttonTable = new Table();
        buttonTable.add(resumeButton).pad(5).width(250).fillX().height(30).row(); // Ajuste width/height
        buttonTable.add(saveButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(settingsButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(exitButton).pad(5).width(250).fillX().height(30).row();

        mainTable.add(tabTable).colspan(2).top().padBottom(10).row();
        mainTable.add(contentTable).expand().fill().pad(10);
        mainTable.add(buttonTable).top().right().pad(10).width(260); // Dar uma largura para a coluna de botões

        switchTab(currentTab);

    }
    private void switchTab(int tabIndex) {
        currentTab = tabIndex;
        Table contentTable = (Table) mainTable.getCells().get(1).getActor(); // Pega a contentTable
        contentTable.clearChildren();
        contentTable.setBackground(nesSkin.getDrawable("nesWindowBackground")); // Aplica fundo à contentTable

        switch(tabIndex) {
            case 0:
                contentTable.add(statusSection).expand().fill().pad(10);
                break;
            case 1:
                if (itemsSection != null) itemsSection.updateItemsDisplay();
                contentTable.add(itemsSection).expand().fill().pad(10);
                break;
            case 2:
                contentTable.add(equipmentSection).expand().fill().pad(10);
                break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Verifica se ESC foi pressionado para continuar o jogo
        if (game.consumeJustPausedFlag()) {

        } else if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.resumeGame();
            return;
        }

        try {
            if (stage != null) {
                stage.act(delta);
                stage.draw();
            }
        } catch (Exception e) {
            Gdx.app.error("PauseMenuScreen", "Erro no render do Stage", e);
            game.resumeGame();
        }
    }
    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("PauseMenuScreen", "Disposing PauseMenuScreen");
        if (stage != null) {
            stage.dispose();
        }
        if (nesSkin != null) {
            nesSkin.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (whitePixel != null) {
            whitePixel.dispose();
        }
    }
    private void saveGame() { Gdx.app.log("PauseMenuScreen", "Jogo salvo! (A Implementar)");}
    private void exitToMenu() { Gdx.app.log("PauseMenuScreen", "Sair para Menu Principal. (A Implementar)");}
}
