package br.lawtrel.hero.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;

public class DialogueBox extends Window {

    private final Label textLabel;
    private final Queue<String> linesToDisplay;

    public DialogueBox(Skin skin) {
        // Usa o novo estilo "dialog" que definimos no uiskin.json
        super("", skin, "dialog");

        this.textLabel = new Label("", skin);
        this.textLabel.setWrap(true); // Permite quebra de linha automática
        this.textLabel.setAlignment(Align.topLeft);

        // Adiciona o label à janela, fazendo-o expandir para preencher o espaço
        this.add(textLabel).expand().fill().pad(15);
        this.bottom(); // Alinha a janela na parte de baixo

        // Define o tamanho e a posição da caixa de diálogo
        this.setSize(Gdx.graphics.getWidth() - 40, Gdx.graphics.getHeight() * 0.25f);
        this.setPosition(20, 20);
        this.setVisible(false);

        this.linesToDisplay = new Queue<>();
    }

    public void startDialogue(Array<String> lines) {
        linesToDisplay.clear();
        for (String line : lines) {
            linesToDisplay.addLast(line);
        }
        advanceDialogue();
        this.setVisible(true);
    }

    public void advanceDialogue() {
        if (linesToDisplay.size > 0) {
            String line = linesToDisplay.removeFirst();
            this.textLabel.setText(line);
        } else {
            this.setVisible(false);
        }
    }
}
