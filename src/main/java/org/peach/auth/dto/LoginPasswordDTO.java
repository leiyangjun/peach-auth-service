package org.peach.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 密码登录：口令须为 RSA 公钥加密后的 Base64 密文，禁止明文。
 */
@Data
@Schema(description = "用户名密码登录（滑块 + RSA 密文口令）")
public class LoginPasswordDTO {

	@NotBlank(message = "用户名不能为空")
	@Schema(description = "登录名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
	private String username;

	/**
	 * RSA PKCS#1 v1.5 加密后的口令，Base64 编码（与 GET /auth/login/password/public-key
	 * 中公钥对应）。
	 */
	@NotBlank(message = "口令密文不能为空")
	@Schema(description = "RSA 加密后的口令（Base64）", requiredMode = Schema.RequiredMode.REQUIRED)
	private String password;

	@NotBlank(message = "滑块挑战 ID 不能为空")
	@Schema(description = "滑块挑战 ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private String captchaId;

	@NotNull(message = "滑块水平偏移不能为空")
	@Schema(description = "滑块水平偏移像素", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer sliderOffset;
}
