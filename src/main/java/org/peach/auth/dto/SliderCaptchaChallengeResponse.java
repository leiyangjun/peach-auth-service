package org.peach.auth.dto;

import java.time.Instant;

/**
 * 滑块拼图挑战出参。
 */
public class SliderCaptchaChallengeResponse {

	private String captchaId;
	private int sliderY;
	private int canvasWidth;
	private int blockWidth;
	private Instant expireAt;

	public String getCaptchaId() {
		return captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}

	public int getSliderY() {
		return sliderY;
	}

	public void setSliderY(int sliderY) {
		this.sliderY = sliderY;
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public int getBlockWidth() {
		return blockWidth;
	}

	public void setBlockWidth(int blockWidth) {
		this.blockWidth = blockWidth;
	}

	public Instant getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Instant expireAt) {
		this.expireAt = expireAt;
	}
}
