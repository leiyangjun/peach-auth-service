package org.peach.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 滑块人机验证：生成一次性挑战，校验成功后会话即销毁。
 * <p>
 * 会话存于进程内 {@link java.util.concurrent.ConcurrentHashMap}；另启定时任务扫除过期会话。
 * </p>
 */
@Service
public class SliderCaptchaService {

	private static final int CANVAS_WIDTH = 320;
	private static final int BLOCK_WIDTH = 42;
	private static final int MIN_TARGET_X = 75;
	private static final int MAX_TARGET_X = 245;
	private static final int TOLERANCE = 6;
	private static final Duration EXPIRE_TTL = Duration.ofMinutes(2);

	private final Map<String, SliderCaptchaSession> sessions = new ConcurrentHashMap<>();

	public SliderCaptchaChallengeResponse createChallenge() {
		clearExpired();
		String captchaId = UUID.randomUUID().toString().replace("-", "");
		int targetX = ThreadLocalRandom.current().nextInt(MIN_TARGET_X, MAX_TARGET_X + 1);
		int sliderY = ThreadLocalRandom.current().nextInt(30, 130);
		Instant expireAt = Instant.now().plus(EXPIRE_TTL);
		sessions.put(captchaId, new SliderCaptchaSession(targetX, expireAt));

		SliderCaptchaChallengeResponse response = new SliderCaptchaChallengeResponse();
		response.setCaptchaId(captchaId);
		response.setSliderY(sliderY);
		response.setCanvasWidth(CANVAS_WIDTH);
		response.setBlockWidth(BLOCK_WIDTH);
		response.setTargetX(targetX);
		response.setExpireAt(expireAt);
		return response;
	}

	/**
	 * 校验用户提交的偏移是否与后台目标位置在容差内；无论成功与否均移除挑战（失败需重新拉取）。
	 */
	public boolean verifyAndConsume(String captchaId, int sliderOffset) {
		clearExpired();
		SliderCaptchaSession session = sessions.remove(captchaId);
		if (session == null || Instant.now().isAfter(session.expireAt())) {
			return false;
		}
		return Math.abs(session.targetX() - sliderOffset) <= TOLERANCE;
	}

	private void clearExpired() {
		Instant now = Instant.now();
		sessions.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expireAt()));
	}

	@Scheduled(fixedDelayString = "${peach.auth.slider.cleanup-interval-ms:60000}")
	void scheduledPurgeExpired() {
		clearExpired();
	}

	private record SliderCaptchaSession(int targetX, Instant expireAt) {
	}

	/**
	 * 滑块挑战返回给前端的结构（原独立 DTO，现内聚在本服务内）。
	 */
	public static class SliderCaptchaChallengeResponse {

		private String captchaId;
		private int sliderY;
		private int canvasWidth;
		private int blockWidth;
		private int targetX;
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

		public int getTargetX() {
			return targetX;
		}

		public void setTargetX(int targetX) {
			this.targetX = targetX;
		}

		public Instant getExpireAt() {
			return expireAt;
		}

		public void setExpireAt(Instant expireAt) {
			this.expireAt = expireAt;
		}
	}
}
