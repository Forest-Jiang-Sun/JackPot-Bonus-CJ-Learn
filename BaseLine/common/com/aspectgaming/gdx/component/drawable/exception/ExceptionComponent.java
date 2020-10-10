package com.aspectgaming.gdx.component.drawable.exception;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class ExceptionComponent extends DrawableComponent {
    private Exception exception;

    // private Image errorImage;
    private Label label;

    private StringBuilder sb;
    private int totalCount;
    private int currentCount;

    public ExceptionComponent(Exception exception) {
        this.exception = exception;
        // this.errorImage = ImageLoader.getInstance().load("Error/error");
        // Vector2[] positions =
        // CoordinateLoader.getInstance().getPath(errorImage, "Error");
        // this.errorImage.setPosition(positions[0].x, positions[0].y);
        // this.errorImage.setColor(1, 1, 1, 0);

        // AlphaAction alphaAction = new AlphaAction();
        // alphaAction.setAlpha(1);
        // alphaAction.setDuration(1);
        // MoveToAction moveToAction = new MoveToAction();
        // moveToAction.setDuration(1);
        // moveToAction.setPosition(positions[1].x, positions[1].y);

        // this.errorImage.addAction(alphaAction);
        // this.errorImage.addAction(moveToAction);
        // this.addActor(this.errorImage);

        sb = new StringBuilder(AspectGamingUtil.WORKING_DIR + "\n" + this.exception.toString());
        StackTraceElement[] sTraceElements = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : sTraceElements) {
            sb.append("\n\r        at ");
            sb.append(stackTraceElement.toString());
        }
        this.label = new Label("", new LabelStyle(FontLoader.getInstance().load("ErrorMessageFont"), Color.WHITE));
        this.addActor(this.label);
        Rectangle bounds = CoordinateLoader.getInstance().getBound("ErrorMessage");
        this.label.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        this.label.setAlignment(Align.center, Align.left);

        totalCount = sb.length();

        RunnableAction runnableAction = run(new Runnable() {
            @Override
            public void run() {
                currentCount++;
                label.setText(sb.subSequence(0, currentCount));
            }
        });

        addAction(repeat(totalCount, delay(0.01f, runnableAction)));
    }
}
