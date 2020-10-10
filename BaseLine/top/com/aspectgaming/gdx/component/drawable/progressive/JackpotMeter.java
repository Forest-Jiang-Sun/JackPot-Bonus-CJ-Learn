//package com.aspectgaming.gdx.component.drawable.progressive;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.aspectgaming.common.actor.Image;
//import com.badlogic.gdx.math.Vector2;
//
//public class JackpotMeter {
//
//	public long value;
//
//	private Image comma;
//	private Image point;
//	private Image dollar;
//
//	private List<Vector2> commaPosition = new ArrayList<>();
//	private Vector2 pointPosition;
//	private Vector2 dollarPosition;
//
//	private List<RollSingleReel> numbers = new ArrayList<>();
//	private long targetValue;
//
//	private int level;
//
//	public JackpotMeter(int idx) {
//	}
//
//	/**
//	 * roll once to cur val.
//	 *
//	 * @param value : cur progressive value
//	 */
//	public void setValue(long value) {
//		this.value = value;
//		this.targetValue = value;
//		String msg;
//		if (value < 100) {
//			msg = String.format("%03d", value);
//		} else
//			msg = Long.toString(value);
//
//		for (int i = msg.length() - 1; i >= 0; i--) {
//			char c = msg.charAt(i);
//			numbers.get(msg.length() - 1 - i).rollTo(Integer.valueOf(String.valueOf(c)));
//		}
//	}
//
//	/**
//	 * roll many a time to cur val.
//	 *
//	 * @param l : cur progressive value
//	 */
//	public void rollValue(long l) {
//		this.targetValue = l;
//	}
//}
