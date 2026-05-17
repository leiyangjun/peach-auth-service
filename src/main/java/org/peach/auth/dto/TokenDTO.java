package org.peach.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录成功返回的访问令牌（与 JWT {@code jti} 一致的业务侧令牌标识）。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "访问令牌")
public class TokenDTO {

	@Schema(description = "令牌唯一标识，与 JWT jti 一致")
	private String tokenId;

	@Schema(description = "令牌类型", example = "Bearer")
	private String tokenType;

	@Schema(description = "JWT access token")
	private String accessToken;

	@Schema(description = "有效期（秒）")
	private long expiresIn;
}
