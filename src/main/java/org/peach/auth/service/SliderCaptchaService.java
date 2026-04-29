package org.peach.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.peach.auth.dto.SliderCaptchaChallengeResponse;
import org.springframework.stereotype.Service;

/**
 * 滑块挑战服务：负责生成一次性挑战与校验。
 */
@Service
public class SliderCaptchaService {

	private static final int CANVAS_WIDTH = 320;
	private static final int BLOCK_WIDTH = 48;
	private static final int MIN_TARGET_X = 40;
	private static final int MAX_TARGET_X = 240;
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
		response.setExpireAt(expireAt);
		return response;
	}

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

	private record SliderCaptchaSession(int targetX, Instant expireAt) {
	}
}
