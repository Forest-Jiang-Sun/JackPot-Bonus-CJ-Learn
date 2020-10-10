package com.aspectgaming.gdx.component.drawable.progressive;

import java.util.ArrayList;
import java.util.List;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.loader.ImageLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * The same treatment as reel to progressive top meter.
 * There functions like reel.
 * 
 * @author kumo.wang
 *
 */
public class RollSingleReel extends Actor {
	private List<Image> characters = new ArrayList<>();
	private List<Vector2> positions = new ArrayList<>();
	private List<Rectangle> rectangles = new ArrayList<>();
	private Rectangle range = new Rectangle();

	private float speed;
	private boolean isCarry = false;

	public boolean Visable = true;
	private float currentOffset;
	private float totalOffset;

	private int currentNumber = -1;
	private boolean isRooling;

	public RollSingleReel(String font, float x, float y) {
		range.x = x;
		range.y = y;
		for (int i = 0; i < 10; i++) {
			Image sprite = ImageLoader.getInstance().load(font + "/" + i);
			range.width = sprite.getWidth();
			range.height = sprite.getHeight();
			positions.add(new Vector2(x, y + (i + 1) * sprite.getHeight()));
			sprite.setPosition(positions.get(i).x, positions.get(i).y);
			characters.add(sprite);
			rectangles.add(new Rectangle(0, 0, range.width, range.height));
		}

	}

	@Override
	public void act(float arg0) {
		super.act(arg0);

		currentOffset += speed;
		if (currentOffset > totalOffset) {
			speed = speed - (currentOffset - totalOffset);
			currentOffset = totalOffset;
		}
		for (int i = 0; i < characters.size(); i++) {

			Image sprite = characters.get(i);
			Vector2 position = positions.get(i);

			position.y -= speed;

			if (position.y < range.y - sprite.getHeight()) {
				float temp = range.y - sprite.getHeight() - position.y;
				position.y = range.y + 9 * range.height - temp;

			}
			sprite.setPosition(position.x, position.y);

			Rectangle rectangle = rectangles.get(i);
			if (position.y > range.y) {
				if ((position.y - range.y) < range.height) {
					float temp = range.height - (position.y - range.y);
					rectangle.set(0, 0, range.width, temp);
				} else {
					rectangle.set(0, 0, range.width, 0);
				}
			} else if (position.y < range.y) {
				float temp = range.y - position.y;
				rectangle.set(0, temp, range.width, range.height - temp);
			} else {
				rectangle.set(0, 0, range.width, range.height);
			}
		}
		if (currentOffset >= totalOffset) {
			speed = 0;
			isRooling = false;
		}
	}

	public boolean carry() {
		return isCarry;
	}

	public boolean isRooling() {
		return isRooling;
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
//		for (int i = 0; i < characters.size(); i++) {
//			Image character = characters.get(i);
//			Vector2 position = positions.get(i);
//			Rectangle rectangle = rectangles.get(i);
//			if (rectangle.y == 0) {
//				Vector2 vector2 = new Vector2(position);
//				vector2.y -= (range.height - rectangle.height);
//				character.Draw(batch, vector2, rectangle, new Color(1, 1, 1, 1));
//			} else
//				character.Draw(batch, position, rectangle, new Color(1, 1, 1, 1));
//
//		}
	}

	public void rollOne(float speed) {
		this.currentOffset = 0;
		this.speed = speed;
		this.isRooling = true;
		this.totalOffset = range.height;
		if (currentNumber == 9) {
			isCarry = true;
			currentNumber = 0;
		} else if (currentNumber == -1) {
			isCarry = false;
			currentNumber = 1;
			this.totalOffset += range.height;
			this.speed += speed;
		} else {
			isCarry = false;
			currentNumber++;
		}
	}

	public void rollTo(int number) {
		if (currentNumber <= number) {
			totalOffset = range.height * (number - currentNumber);
		} else {
			totalOffset = range.height * (number + 10 - currentNumber);
		}
		speed = 30;
		isRooling = false;
		currentOffset = 0;
		currentNumber = number;
	}

	public Vector2 getSize() {
		return new Vector2(range.width, range.height);
	}

	public void setOffset(Vector2 offset) {
		range.x += offset.x;
		range.y += offset.y;
		for (Vector2 position : positions) {
			position.x += offset.x;
			position.y += offset.y;
		}
	}
}
